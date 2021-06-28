package com.kunbu.pay.payment.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderItemDto implements Serializable {

    private Long productId;

    private String productName;

    private Long price;

    private Integer count;

}

