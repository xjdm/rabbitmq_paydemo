package com.idstaa.rabbitmq.demo.pojo;

import lombok.Data;

/**
 * @author chenjie
 * @date 2021/3/9 19:33
 */
@Data
public class OrderDetail {
    private String itemId;

    private String itemName;

    private double itemPrice;

    private int num;
}
