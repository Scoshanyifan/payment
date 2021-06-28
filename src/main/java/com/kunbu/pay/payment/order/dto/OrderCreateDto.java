package com.kunbu.pay.payment.order.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OrderCreateDto implements Serializable {

    private List<OrderItemDto> items;

    private Integer payType;

    private Long addressId;

    private String userId;
}
