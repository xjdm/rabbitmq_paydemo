package com.idstaa.rabbitmq.demo.listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author chenjie
 * @date 2021/3/9 19:38
 */
@Component
public class MyCancelListener {
    @RabbitListener(queues = "q.dlx", ackMode = "MANUAL")
    public void onMessage(Message message, Channel channel) throws IOException {
        String orderId = new String(message.getBody());
        System.out.println("取消订单" + orderId + "时间" + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
