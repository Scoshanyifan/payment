package com.kunbu.pay.payment.service;

import com.kunbu.pay.payment.biz.ali.AlipayHandler;
import com.kunbu.pay.payment.biz.ali.AlipaySignContent;
import com.kunbu.pay.payment.order.OrderStatusEnum;
import com.kunbu.pay.payment.constant.PayConstant;
import com.kunbu.pay.payment.constant.PayTypeEnum;
import com.kunbu.pay.payment.order.OrderRepository;
import com.kunbu.pay.payment.order.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class PaymentService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AlipayHandler alipayHandler;

    public String orderSign(String orderId) {
        try {
            Order order = orderRepository.findFirstByOrderId(orderId);
            if (order != null) {
                if (OrderStatusEnum.WAIT_PAY.getStatus().equals(order.getOrderStatus())) {

                    if (PayTypeEnum.ALIPAY.getType().equals(order.getPayType())) {
                        AlipaySignContent content = new AlipaySignContent();
                        content.setOutTradeNo(order.getOrderId());
                        content.setTitle(PayConstant.DEFAULT_TITLE_PREFIX + order.getOriginProductName());
                        content.setTotalAmount(order.getOrderAmount().toString());
                        return alipayHandler.pagePay(content);
                    } else if (PayTypeEnum.WXPAY.getType().equals(order.getPayType())) {

                    } else {

                    }
                } else {
                    log.error(">>> order status error:{}", order);
                }
            }
            return null;
        } catch (Exception e) {
            log.error(">>> orderSign error", e);
            return null;
        }
    }

    public String payCallback(Map<String, String> callbackParams, Integer payType) {



        return null;
    }

}
