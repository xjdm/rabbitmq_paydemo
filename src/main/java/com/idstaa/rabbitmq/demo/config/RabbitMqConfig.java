package com.idstaa.rabbitmq.demo.config;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chenjie
 * @date 2021/3/9 19:14
 */
@Configuration
@EnableRabbit
@ComponentScan("com.idstaa")
public class RabbitMqConfig {
    @Bean
    /**
     * 订单消息队列
     */
    public Queue orderQueue() {
        return QueueBuilder.durable("q.order").build();
    }

    @Bean
    /**
     * 订单消息队列
     */
    public Queue ttlQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 10000);
        args.put("x-dead-letter-exchange", "ex.dlx");
        args.put("x-dead-letter-routing-key", "key.dlx");
        return new Queue("ttl.order", true, false, false, args);
    }

    /**
     * 死信队列，用于取消用户订单
     */
    @Bean
    public Queue dlxQueue() {
        Map<String, Object> args = new HashMap<>();
        return new Queue("q.dlx", true, false, false, args);
    }

    /**
     * 订单交换器
     */
    @Bean
    public Exchange orderExchange() {
        Map<String, Object> args = new HashMap<>();
        DirectExchange exchange = new DirectExchange("ex.order",
                true,
                false,
                args);
        return exchange;
    }

    /**
     * ttl交换器
     */
    @Bean
    public Exchange ttlExchange() {
        Map<String, Object> args = new HashMap<>();
        DirectExchange exchange = new DirectExchange("ex.ttl",
                true,
                false,
                args);
        return exchange;
    }

    /**
     * 订单交换器
     */
    @Bean
    public Exchange dlxExchange() {
        Map<String, Object> args = new HashMap<>();
        DirectExchange exchange = new DirectExchange("ex.dlx",
                true,
                false,
                args);
        return exchange;
    }

    /**
     * 用于发送下单，做分布式事务的MQ
     */
    @Bean
    public Binding orderBinding() {
        return BindingBuilder.bind(orderQueue()).to(orderExchange()).with("key.order").noargs();
    }

    /**
     * 用于等待用户支付的延迟队列绑定
     */
    @Bean
    public Binding ttlBinding() {
        return BindingBuilder.bind(ttlQueue()).to(ttlExchange()).with("key.ttl").noargs();
    }


    /**
     * 用于支付超时取消用户订单的死信队列绑定
     */
    @Bean
    public Binding dlxBinding() {
        return BindingBuilder.bind(dlxQueue()).to(dlxExchange()).with("key.dlx").noargs();
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory){
        return new RabbitAdmin(connectionFactory);
    }

   @Bean(name="rabbitMessageListenerContainer")
    public DirectMessageListenerContainer listenerContainer(ConnectionFactory connectionFactory){
        DirectMessageListenerContainer container = new DirectMessageListenerContainer(connectionFactory);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setPrefetchCount(5);
        container.setConsumersPerQueue(5);
        container.setMessagesPerAck(1);

       ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
       taskExecutor.setCorePoolSize(10);
       taskExecutor.setMaxPoolSize(20);
       // 设置改属性，灵活设置并发
       container.setTaskExecutor(taskExecutor);
       return container;
   }

   @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
   }


}
