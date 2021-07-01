package com.kunbu.pay.payment.service;

import com.kunbu.pay.payment.biz.ali.AlipayHandler;
import com.kunbu.pay.payment.biz.ali.AlipaySignContent;
import com.kunbu.pay.payment.order.constant.OrderStatusEnum;
import com.kunbu.pay.payment.constant.PayConstant;
import com.kunbu.pay.payment.constant.PayTypeEnum;
import com.kunbu.pay.payment.order.dao.BizOrderRepository;
import com.kunbu.pay.payment.order.entity.BizOrder;
import com.kunbu.pay.payment.util.MoneyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
public class PaymentService {

    @Autowired
    private BizOrderRepository bizOrderRepository;

    @Autowired
    private AlipayHandler alipayHandler;

    /**
     * 发起支付
     * @param bizOrderNo
     * @return
     */
    public String paySign(String bizOrderNo, Integer payType) {
        try {
            // 查询订单 TODO 远程
            BizOrder bizOrder = bizOrderRepository.findByOrderNo(bizOrderNo);
            log.info(">>> paySign, bizOrder:{}", bizOrder);
            if (bizOrder != null) {
                if (OrderStatusEnum.WAIT_PAY.getStatus().equals(bizOrder.getOrderStatus())) {
                    if (PayTypeEnum.ALIPAY.getType().equals(payType)) {
                        AlipaySignContent content = new AlipaySignContent();
                        content.setOutTradeNo(bizOrder.getBizOrderNo());
                        content.setTitle(PayConstant.DEFAULT_TITLE_PREFIX + bizOrder.getUserId());
                        content.setTotalAmount(MoneyUtil.convertFen2Yuan(bizOrder.getOrderAmount()));
                        return alipayHandler.pagePay(content);
                    } else if (PayTypeEnum.WXPAY.getType().equals(bizOrder.getPayType())) {
                        return null;
                    } else {
                        return null;
                    }
                } else {
                    log.error(">>> bizOrder productStatus error:{}", bizOrder);
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
    @Transactional(rollbackFor = Exception.class)
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
