package com.cake.platform.Sale.mq;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.cake.platform.Sale.entity.FlashSale;
import com.cake.platform.Sale.entity.FlashSaleOrder;
import com.cake.platform.Sale.mapper.FlashSaleMapper;
import com.cake.platform.Sale.mapper.FlashSaleOrderMapper;
import com.cake.platform.common.config.RabbitMQConfig;
import com.cake.platform.order.entity.Order;
import com.cake.platform.order.mapper.OrderMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class OrderDelayConsumer {
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private FlashSaleOrderMapper flashSaleOrderMapper;
    @Resource
    private FlashSaleMapper flashSaleMapper;
    @Resource
    private RedisTemplate redisTemplate;

    @RabbitListener(queues = RabbitMQConfig.DEAD_QUEUE)
    public void handle(Map<String, Object> msg) {
        // 普通订单超时消息：下单后15分钟未支付
        if (msg.containsKey("orderId")) {
            handleOrderTimeout(Long.valueOf(msg.get("orderId").toString()));
        }
        // 秒杀订单超时消息：抢购后15分钟未支付
        else if (msg.containsKey("flashSaleOrderId")) {
            handleFlashSaleTimeout(msg);
        } else {
            log.warn("未知的死信消息，跳过: {}", msg);
        }
    }

    /** 普通订单超时未支付 → 取消（0-待支付 → 5-已取消） */
    private void handleOrderTimeout(Long orderId) {
        int rows = orderMapper.update(null,
                new UpdateWrapper<Order>()
                        .eq("id", orderId)
                        .eq("status", 0)
                        .set("status", 5));
        if (rows > 0) {
            log.info("订单超时未支付，已取消: orderId={}", orderId);
        } else {
            log.info("订单 {} 不存在或已支付/已处理，跳过", orderId);
        }
    }

    /** 秒杀订单超时未支付 → 取消（0-待支付 → 2-已取消）+ 回补库存 */
    private void handleFlashSaleTimeout(Map<String, Object> msg) {
        Long flashSaleOrderId = Long.valueOf(msg.get("flashSaleOrderId").toString());
        int rows = flashSaleOrderMapper.update(null,
                new UpdateWrapper<FlashSaleOrder>()
                        .eq("id", flashSaleOrderId)
                        .eq("status", 0)
                        .set("status", 2));
        if (rows == 0) {
            log.info("秒杀订单 {} 不存在或已处理，跳过", flashSaleOrderId);
            return;
        }
        // 回补库存（Redis + DB）
        Long flashSaleId = Long.valueOf(msg.get("flashSaleId").toString());
        redisTemplate.opsForValue().increment("flash:stock:" + flashSaleId, 1);
        flashSaleMapper.update(null,
                new UpdateWrapper<FlashSale>()
                        .eq("id", flashSaleId)
                        .setSql("stock = stock + 1"));
        log.info("秒杀订单超时取消，库存已回补: flashSaleOrderId={}, flashSaleId={}",
                flashSaleOrderId, flashSaleId);
    }
}
