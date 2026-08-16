package com.cake.platform.Sale.mq;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.cake.platform.Sale.entity.FlashSale;
import com.cake.platform.Sale.entity.FlashSaleOrder;
import com.cake.platform.Sale.mapper.FlashSaleMapper;
import com.cake.platform.Sale.mapper.FlashSaleOrderMapper;
import com.cake.platform.common.config.RabbitMQConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class SeckillConsumer {//秒杀消费者
    @Resource
    private FlashSaleOrderMapper flashSaleOrderMapper;
    @Resource
    private FlashSaleMapper flashSaleMapper;
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)//监听队列
    public void listener(Map<String, Object>msg){
        log.info("接收到消息: {}", msg);
        Long flashSaleId =Long.valueOf(msg.get("flashSaleId").toString()) ;
      Long userId =  Long.valueOf(msg.get("userId").toString());//从用户端获取用户ID，
        //通过生产者发送的参数，生成订单
        String generateOrderNo = "FS" + System.currentTimeMillis();//订单编号
        log.info("订单编号: {}", generateOrderNo);
        FlashSaleOrder flashSaleOrder = new FlashSaleOrder();
        flashSaleOrder.setFlashSaleId(flashSaleId);
        flashSaleOrder.setUserId(userId);
        flashSaleOrder.setOrderNo(generateOrderNo);
        
        flashSaleOrder.setStatus(0);
        flashSaleOrderMapper.insert(flashSaleOrder);//最终将订单写入数据库
        log.info("订单创建成功: {}", flashSaleOrder);
        flashSaleMapper.update( null, new UpdateWrapper<FlashSale>()
                .eq("id", flashSaleId)
                .setSql("stock = stock -1"));
    }

}
