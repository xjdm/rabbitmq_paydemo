package com.idstaa.rabbitmq.demo.pojo;

import lombok.Data;

import java.util.ArrayList;

/**
 * @author chenjie
 * @date 2021/3/9 19:32
 */
@Data
public class Order {
    private String orderId;

    private String userId;

    private String status;

    private ArrayList<OrderDetail> detail;
}
