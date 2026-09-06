package com.cake.platform.Sale.mq;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.cake.platform.Sale.entity.FlashSale;
import com.cake.platform.Sale.entity.FlashSaleOrder;
import com.cake.platform.Sale.mapper.FlashSaleMapper;
import com.cake.platform.Sale.mapper.FlashSaleOrderMapper;
import com.cake.platform.common.config.RabbitMQConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class SeckillConsumer {//秒杀消费者
    @Resource
    private FlashSaleOrderMapper flashSaleOrderMapper;
    @Resource
    private FlashSaleMapper flashSaleMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)//监听队列
    @Transactional
    public void listener(Map<String, Object>msg){
        log.info("====== 秒杀消费者收到消息 ====== msg={}", msg);
        if (msg == null || msg.isEmpty()) {
            log.error("消息为空，跳过处理");
            return;
        }

        Long flashSaleId =Long.valueOf(msg.get("flashSaleId").toString()) ;
      Long userId =  Long.valueOf(msg.get("userId").toString());//从用户端获取用户ID，
        //通过生产者发送的参数，生成订单
        String generateOrderNo = "FS" + System.currentTimeMillis() + userId;//订单编号
        log.info("订单编号: {}", generateOrderNo);


//        检查用户是否已经购买过该秒杀商品
        //在数据库中检查用户是否已经购买过该秒杀商品
        //在redis层面是用lua脚本实现的
        Long count = flashSaleOrderMapper.selectCount(
                new QueryWrapper<FlashSaleOrder>()
                        .eq("flash_sale_id", flashSaleId)
                        .eq("user_id", userId)
        );
        if (count > 0) {
            log.info("用户 {} 已经购买过该秒杀商品 {}", userId, flashSaleId);
            return;
        }
        int rows = flashSaleMapper.update(null,
                new UpdateWrapper<FlashSale>()
                        .eq("id", flashSaleId)
                        .gt("stock", 0)
                        .setSql("stock = stock - 1"));
        if (rows == 0) {
            log.info("秒杀商品 {} 已经售罄", flashSaleId);
            return;
        }

        FlashSaleOrder flashSaleOrder = new FlashSaleOrder();
        flashSaleOrder.setFlashSaleId(flashSaleId);
        flashSaleOrder.setUserId(userId);
        flashSaleOrder.setOrderNo(generateOrderNo);
        flashSaleOrder.setStatus(0);//订单状态：0-待支付
        flashSaleOrderMapper.insert(flashSaleOrder);//最终将订单写入数据库
        log.info("====== 订单创建成功 ====== orderId={}, orderNo={}", flashSaleOrder.getId(), flashSaleOrder.getOrderNo());

        // 发送延迟消息：15分钟未支付则超时取消（走死信队列）
        Map<String, Object> delayMsg = new HashMap<>();
        delayMsg.put("flashSaleOrderId", flashSaleOrder.getId());
        delayMsg.put("flashSaleId", flashSaleId);
        delayMsg.put("userId", userId);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.DELAY_EXCHANGE,
                RabbitMQConfig.DELAY_ROUTING_KEY,
                delayMsg);
    }

}
