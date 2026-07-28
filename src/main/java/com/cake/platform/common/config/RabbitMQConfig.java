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


    public  static  final String DELAY_QUEUE = "delay.queue";//延迟队列名称
    public static final String DELAY_EXCHANGE = "delay.exchange";//延迟交换机名称
    public static final String DELAY_ROUTING_KEY = "delay.routing.key";//延迟队列路由键

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
    public  Queue delayQueue(){//创建延迟队列
        Map<String,Object> args = new HashMap<>();
        args.put("x-message-ttl", 900000);//延迟时间,15分钟
        args.put("x-dead-letter-exchange",DEAD_EXCHANGE);//过期后转发的交换机
        args.put("x-dead-letter-routing-key",DEAD_ROUTING_KEY);//过期后转发的路由键
        return new Queue(DELAY_QUEUE,true,false,false,args);//创建延迟队列
    }
    @Bean
    public DirectExchange delayExchange(){//创建延迟交换机
         return new DirectExchange(DELAY_EXCHANGE,true,false);
         }
    @Bean
    public Binding delayBinding(){//绑定延迟队列和延迟交换机
         return BindingBuilder.bind(delayQueue()).to(delayExchange()).with(DELAY_ROUTING_KEY);
     }



     @Bean
    public Queue deadQueue(){//创建死信队列
         return new Queue(DEAD_QUEUE,true);
     }
     @Bean
    public DirectExchange deadExchange(){//创建死信交换机
         return new DirectExchange(DEAD_EXCHANGE,true,false);
     }
     @Bean
    public Binding deadBinding(){//绑定死信队列和死信交换机
         return BindingBuilder.bind(deadQueue()).to(deadExchange()).with(DEAD_ROUTING_KEY);
     }

}
