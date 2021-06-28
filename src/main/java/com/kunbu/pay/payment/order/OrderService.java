package com.kunbu.pay.payment.order;

import com.kunbu.pay.payment.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentService paymentService;

    public String createOrder(Product product, String userId, Integer count) {

        // 1-生成订单


        // 2-调用支付



        return null;
    }

}
