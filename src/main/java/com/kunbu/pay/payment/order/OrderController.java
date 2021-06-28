package com.kunbu.pay.payment.order;

import cn.hutool.http.server.HttpServerRequest;
import com.kunbu.pay.payment.entity.ApiResult;
import com.kunbu.pay.payment.order.dto.OrderCreateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public ApiResult createOrder(@RequestBody OrderCreateDto orderCreateDto, HttpServerRequest request) {

        orderCreateDto.setUserId("2233");
        return orderService.createOrder(orderCreateDto);
    }

}
