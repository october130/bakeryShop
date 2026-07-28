package com.cake.platform.order.mq;

import com.cake.platform.common.config.RabbitMQConfig;
import com.cake.platform.order.entity.Order;
import com.cake.platform.order.mapper.OrderMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderDelayConsumer {
    @Resource
    private OrderMapper orderMapper;

@RabbitListener( queues = RabbitMQConfig.DEAD_QUEUE)
    public  void handle(Long orderId){
    log.info("订单延迟队列监听到消息: {}", orderId);
    Order order = orderMapper.selectById(orderId);
    if (order == null){
        return;
    }
    if (order.getStatus() == 0){
        order.setStatus(5);
        orderMapper.updateById(order);
        log.info("订单超时,取消订单: {}", orderId);
    }
}
}
