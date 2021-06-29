package com.kunbu.pay.payment.order.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderItemDto implements Serializable {

    private Long productId;

    private Integer count;

}

