package com.idstaa.rabbitmq.demo.controller;

import com.idstaa.rabbitmq.demo.pojo.Order;
import com.idstaa.rabbitmq.demo.pojo.OrderDetail;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * @author chenjie
 * @date 2021/3/6 13:58
 */
@Controller
public class OrderController {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RequestMapping("/createOrder")
    public String createOrder(Model model) throws ExecutionException, InterruptedException {
        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString().substring(0,10));
        order.setStatus("待支付");
        order.setUserId("jianyi");
        OrderDetail detail = new OrderDetail();
        detail.setItemId(UUID.randomUUID().toString().substring(0,5));
        detail.setItemName("");
        detail.setItemPrice(100d);
        detail.setNum(2);
        ArrayList detailList = new ArrayList();
        detailList.add(detail);
        order.setDetail(detailList);
        CorrelationData correlationData = new CorrelationData();
        rabbitTemplate.convertAndSend("ex.order",
                "key.order",
                order,
                correlationData);
        CorrelationData.Confirm confirm = correlationData.getFuture().get();
        boolean ack = confirm.isAck();
        if(!ack){
            return "failOrder";
        }
        System.out.println("发送延迟取消信息，10s不支付就取消"+",当前时间"+ new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
        rabbitTemplate.convertAndSend("ex.ttl","key.ttl",order.getOrderId());
        model.addAttribute("orderId",order.getOrderId());
        return "order";
    }

    @RequestMapping("/failOrder/{orderId}")
    public String failOrder(@PathVariable  String orderId,Model model) throws ExecutionException, InterruptedException {
        // 修改订单状态
        System.out.println(orderId);
        model.addAttribute("orderId",orderId);
        return "fail";
    }

    @RequestMapping("/pay")
    public String pay(String orderId,Model model) throws ExecutionException, InterruptedException {
        // 修改订单状态
        System.out.println(orderId+"订单状态为已支付");
        model.addAttribute("orderId",orderId);
        return "success";
    }

    @RequestMapping("/cancelOrderView")
    public String cancelOrderView(String orderId,Model model) throws ExecutionException, InterruptedException {
        // 修改订单状态
        System.out.println(orderId+"订单状态为已取消");
        model.addAttribute("orderId",orderId);
        return "cancelOrderView";
    }
}
