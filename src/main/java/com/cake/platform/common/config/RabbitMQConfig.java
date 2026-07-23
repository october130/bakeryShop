package com.cake.platform.common.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {//秒杀队列配置
    public static final String QUEUE_NAME = "seckill.queue";//队列名称
    public static final String EXCHANGE_NAME = "seckill.exchange";//交换机名称

    @Bean
    public Queue seckillQueue() {
        return new Queue(QUEUE_NAME, true);
    }//创建队列, true表示持久化
//创建交换机, true表示持久化
    @Bean
    public DirectExchange seckillExchange() {
        return new DirectExchange(EXCHANGE_NAME, true, false);
    }
//创建交换机, true表示持久化
    @Bean
    public Binding seckillBinding() {//绑定队列和交换机
        return BindingBuilder.bind(seckillQueue()).to(seckillExchange()).with("seckill");//绑定队列和交换机
    }
}
