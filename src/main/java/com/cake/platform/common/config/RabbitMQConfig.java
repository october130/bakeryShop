package com.cake.platform.common.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {//秒杀队列配置
    public static final String QUEUE_NAME = "seckill.queue";//队列名称
    public static final String EXCHANGE_NAME = "seckill.exchange";//交换机名称

    public static final String DELAY_QUEUE = "seckill.delay.queue";//队列名称
    public static final String DELAY_EXCHANGE = "seckill.delay.exchange";//交换机名称
    public static final String DELAY_ROUTING_KEY = "seckill.delay.routing.key";//队列路由键

    public  static  final String  DEAD_QUEUE = "dead.queue";//死信队列名称
    public static final String DEAD_EXCHANGE = "dead.exchange";//死信交换机名称
    public static final String DEAD_ROUTING_KEY = "dead.routing.key";//死信队列路由键

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


    @Bean
    public Queue seckillDelayQueue() {
        Map<String,Object> args = new HashMap<>();
        args.put("x-message-ttl", 900000);//延迟时间,15分钟
        args.put("x-dead-letter-exchange",DEAD_EXCHANGE);//过期后转发的交换机
        args.put("x-dead-letter-routing-key",DEAD_ROUTING_KEY);//过期后转发的路由键
        return new Queue(DELAY_QUEUE,true,false,false,args);//创建延迟队列
    }
    @Bean
    public DirectExchange seckillDelayExchange() {
        return new DirectExchange(DELAY_EXCHANGE, true, false);
    }
    @Bean
    public Binding seckillDelayBinding() {//绑定队列和交换机
        return BindingBuilder.bind(seckillDelayQueue()).to(seckillDelayExchange()).with(DELAY_ROUTING_KEY);//绑定队列和交换机
    }
    @Bean
    public Queue deadQueue() {
        return new Queue(DEAD_QUEUE, true);
    }
    @Bean
    public DirectExchange deadExchange() {
        return new DirectExchange(DEAD_EXCHANGE, true, false);
    }
    @Bean
    public Binding deadBinding() {//绑定队列和交换机
        return BindingBuilder.bind(deadQueue()).to(deadExchange()).with(DEAD_ROUTING_KEY);//绑定队列和交换机
    }
}
