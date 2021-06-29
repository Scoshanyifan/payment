package com.kunbu.pay.payment.service;

import com.kunbu.pay.payment.biz.ali.AlipayHandler;
import com.kunbu.pay.payment.biz.ali.AlipaySignContent;
import com.kunbu.pay.payment.order.constant.OrderStatusEnum;
import com.kunbu.pay.payment.constant.PayConstant;
import com.kunbu.pay.payment.constant.PayTypeEnum;
import com.kunbu.pay.payment.order.dao.OrderRepository;
import com.kunbu.pay.payment.order.entity.Order;
import com.kunbu.pay.payment.util.MoneyUtil;
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

    /**
     * 发起支付
     * @param orderId
     * @return
     */
    public String paySign(String orderId, Integer payType) {
        try {
            // 查询订单 TODO 远程
            Order order = orderRepository.findFirstByOrderId(orderId);
            log.info(">>> paySign, order:{}", order);
            if (order != null) {
                if (OrderStatusEnum.WAIT_PAY.getStatus().equals(order.getOrderStatus())) {
                    if (PayTypeEnum.ALIPAY.getType().equals(payType)) {
                        AlipaySignContent content = new AlipaySignContent();
                        content.setOutTradeNo(order.getOrderId());
                        content.setTitle(PayConstant.DEFAULT_TITLE_PREFIX + order.getUserId());
                        content.setTotalAmount(MoneyUtil.convertFen2Yuan(order.getOrderAmount()));
                        return alipayHandler.pagePay(content);
                    } else if (PayTypeEnum.WXPAY.getType().equals(order.getPayType())) {
                        return null;
                    } else {
                        return null;
                    }
                } else {
                    log.error(">>> order status error:{}", order);
                }
            }
        } catch (Exception e) {
            log.error(">>> orderSign error", e);
        }
        return null;
    }

    /**
     * 支付回调
     * @param callbackParams
     * @param payType
     * @return
     */
    public String payCallback(Map<String, String> callbackParams, Integer payType) {
        log.info(">>> payCallback params:{}, payType:{}", callbackParams, payType);
        try {
            if (PayTypeEnum.ALIPAY.getType().equals(payType)) {
                return alipayHandler.payCallback(callbackParams);
            } else if (PayTypeEnum.WXPAY.getType().equals(payType)) {
                return PayConstant.SUCCESS;
            } else {
                return PayConstant.SUCCESS;
            }
        } catch (Exception e) {
            log.error(">>> payCallback error", e);
        }
        return null;
    }

}
