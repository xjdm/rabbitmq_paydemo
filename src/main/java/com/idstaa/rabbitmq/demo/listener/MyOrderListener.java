package com.idstaa.rabbitmq.demo.listener;

import com.idstaa.rabbitmq.demo.pojo.Order;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * @author chenjie
 * @date 2021/3/9 19:30
 */
@Component
public class MyOrderListener {
    @RabbitListener(queues = "q.order",ackMode = "MANUAL")
    public void onMessage(Order order, Channel channel, Message message) throws IOException {
        System.out.println("写数据库，订单为待支付");
        System.out.println(order.toString());
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }
}
