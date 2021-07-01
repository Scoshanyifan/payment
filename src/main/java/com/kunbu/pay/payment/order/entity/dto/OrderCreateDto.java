package com.kunbu.pay.payment.order.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OrderCreateDto implements Serializable {

    private List<OrderItemDto> items;

    private Long addressId;

    private String userId;
}
