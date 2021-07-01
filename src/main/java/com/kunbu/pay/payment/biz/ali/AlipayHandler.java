package com.kunbu.pay.payment.biz.ali;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.kunbu.pay.payment.constant.BillTypeEnum;
import com.kunbu.pay.payment.order.constant.OrderStatusEnum;
import com.kunbu.pay.payment.constant.PayConstant;
import com.kunbu.pay.payment.dao.PayJournalRepository;
import com.kunbu.pay.payment.order.dao.BizOrderRepository;
import com.kunbu.pay.payment.order.dao.SubOrderRepository;
import com.kunbu.pay.payment.order.entity.BizOrder;
import com.kunbu.pay.payment.entity.PayJournal;
import com.kunbu.pay.payment.util.MoneyUtil;
import com.kunbu.pay.payment.util.PropertyPayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class AlipayHandler {

    @Autowired
    private PayJournalRepository payJournalRepository;

    @Autowired
    private BizOrderRepository bizOrderRepository;

    @Autowired
    private SubOrderRepository subOrderRepository;

    public String pagePay(AlipaySignContent alipaySignContent) throws Exception {
        AlipayClient alipayClient = AlipayClientSingleton.getAlipayClient();

        JSONObject json = buildSignContent(alipaySignContent);
        json.set(AlipayConstant.PARAM_PRODUCT_CODE, AlipayConstant.PRODUCT_CODE_PAGE);
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setBizContent(json.toString());
        request.setNotifyUrl(PropertyPayUtil.getValue(AlipayConstant.CONFIG_NOTIFY_URL));
        request.setReturnUrl(PropertyPayUtil.getValue(AlipayConstant.CONFIG_RETURN_URL));

        AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
        log.info(">>> pagePay response:{}", JSONUtil.toJsonStr(response));
        if (response.isSuccess()) {
            return response.getBody();
        } else {
            return null;
        }
    }

    private JSONObject buildSignContent(AlipaySignContent alipaySignContent) {
        JSONObject json = new JSONObject();
        json.set(AlipayConstant.PARAM_OUT_TRADE_NO, alipaySignContent.getOutTradeNo());
        json.set(AlipayConstant.PARAM_SUBJECT, alipaySignContent.getTitle());
        json.set(AlipayConstant.PARAM_TOTAL_AMOUNT, alipaySignContent.getTotalAmount());
        return json;
    }

    public String payCallback(Map<String, String> params) throws Exception {
        // 验签
        boolean signVerified = AlipaySignature.rsaCheckV1(
                params,
                PropertyPayUtil.getValue(AlipayConstant.CONFIG_ALIPAY_PUBLIC_KEY),
                PropertyPayUtil.getValue(AlipayConstant.CONFIG_CHARSET),
                PropertyPayUtil.getValue(AlipayConstant.CONFIG_SIGN_TYPE)
        );
        if (signVerified) {
            // 检查基础配置
            String callbackAppId = params.get(AlipayConstant.PARAM_APP_ID);
            String appId = PropertyPayUtil.getValue(AlipayConstant.CONFIG_APP_ID);
            if (!appId.equals(callbackAppId)) {
                log.error(">>> callback appId error, params:{}", params);
                return PayConstant.FAILURE;
            }
            // 检查订单 TODO 远程
            String outTradeNo = params.get(AlipayConstant.PARAM_OUT_TRADE_NO);
            BizOrder bizOrder = bizOrderRepository.findByOrderNo(outTradeNo);
            if (bizOrder == null) {
                log.error(">>> callback bizOrder null, params:{}", params);
                return PayConstant.FAILURE;
            }
            // 检查金额
            String totalAmount = MoneyUtil.convertFen2Yuan(bizOrder.getOrderAmount());
            String callbackAmount = params.get(AlipayConstant.PARAM_TOTAL_AMOUNT);
            if (!totalAmount.equals(callbackAmount)) {
                log.error(">>> callback amount error, params:{}, bizOrder:{}", params, bizOrder);
                return PayConstant.FAILURE;
            }
            // 检查是否已经回调
            int checkJournal = payJournalRepository.countByBizIdAndPayTypeAndBillType(bizOrder.getBizOrderNo(), bizOrder.getPayType(), BillTypeEnum.ORDER.getType());
            if (checkJournal > 0) {
                log.error(">>> callback journal repeat, bizOrder:{}", bizOrder);
                return PayConstant.FAILURE;
            }
            // 检查支付状态
            String tradeStatus = params.get(AlipayConstant.CALLBACK_TRADE_STATUS);
            if (AlipayConstant.TRADE_STATUS_SUCCESS.equals(tradeStatus) || AlipayConstant.TRADE_STATUS_FINISHED.equals(tradeStatus)) {
                // 记录流水
                PayJournal payJournal = PayJournal.build(
                        bizOrder.getBizOrderNo(),
                        bizOrder.getUserId(),
                        BillTypeEnum.ORDER.getType(),
                        bizOrder.getPayType(),
                        bizOrder.getOrderAmount(),
                        BillTypeEnum.ORDER.getValue()
                );
                payJournalRepository.save(payJournal);
                // 更新订单状态 TODO MQ
                int bizRes = bizOrderRepository.updateOrderStatus(OrderStatusEnum.PAID.getStatus(), bizOrder.getBizOrderNo(), OrderStatusEnum.WAIT_PAY.getStatus());
                int subRes = subOrderRepository.updateSubOrderStatusByBizOrder(OrderStatusEnum.PAID.getStatus(), bizOrder.getBizOrderNo(), OrderStatusEnum.WAIT_PAY.getStatus());
                log.info(">>> bizRes:{}, subRes:{}", bizRes,subRes);
                return PayConstant.SUCCESS;
            } else if (AlipayConstant.TRADE_STATUS_SUCCESS.equals(tradeStatus)) {
                return PayConstant.SUCCESS;
            } else {
                return PayConstant.FAILURE;
            }
        } else {
            log.error(">>> callback sign verify failure, params:{}", params);
        }
        return PayConstant.FAILURE;
    }

    public String refundCallback() {

        return PayConstant.SUCCESS;
    }

}
