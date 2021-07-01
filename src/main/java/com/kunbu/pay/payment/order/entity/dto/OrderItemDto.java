package com.kunbu.pay.payment.order.entity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderItemDto implements Serializable {

    private Long productId;

    private Integer productNumber;

    //

    private Long productPrice;

    private String productName;



}

