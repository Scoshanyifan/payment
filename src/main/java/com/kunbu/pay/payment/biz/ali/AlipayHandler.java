package com.kunbu.pay.payment.biz.ali;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.kunbu.pay.payment.constant.BillTypeEnum;
import com.kunbu.pay.payment.order.OrderStatusEnum;
import com.kunbu.pay.payment.constant.PayConstant;
import com.kunbu.pay.payment.dao.OrderJournalRepository;
import com.kunbu.pay.payment.order.OrderRepository;
import com.kunbu.pay.payment.order.Order;
import com.kunbu.pay.payment.entity.OrderJournal;
import com.kunbu.pay.payment.util.PropertyPayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class AlipayHandler {

    @Autowired
    private OrderJournalRepository orderJournalRepository;

    @Autowired
    private OrderRepository orderRepository;

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

    /**
     * tradeStatus.TRADE_CLOSED	    交易关闭
     * tradeStatus.TRADE_FINISHED	交易完结
     * tradeStatus.TRADE_SUCCESS	支付成功
     * tradeStatus.WAIT_BUYER_PAY   交易创建
     *
     * @param result
     * @return
     */
    public String payCallback(Map<String, String> result) throws Exception {
        boolean signVerified = AlipaySignature.rsaCheckV1(
                result,
                PropertyPayUtil.getValue(AlipayConstant.CONFIG_ALIPAY_PUBLIC_KEY),
                PropertyPayUtil.getValue(AlipayConstant.CONFIG_CHARSET),
                PropertyPayUtil.getValue(AlipayConstant.CONFIG_SIGN_TYPE)
        );
        if (signVerified) {
            // 检查基础配置
            String callbackAppId = result.get(AlipayConstant.CONFIG_APP_ID);
            String appId = PropertyPayUtil.getValue(AlipayConstant.CONFIG_APP_ID);
            if (!appId.equals(callbackAppId)) {
                log.error(">>> callback order null, result:{}", result);
                return PayConstant.FAILURE;
            }
            // 检查订单
            String outTradeNo = result.get(AlipayConstant.PARAM_OUT_TRADE_NO);
            Order order = orderRepository.findFirstByOrderId(outTradeNo);
            if (order == null) {
                log.error(">>> callback order null, result:{}", result);
                return PayConstant.FAILURE;
            }
            // 检查金额
            String totalAmount = order.getOrderAmount().toString();
            String callbackAmount = result.get(AlipayConstant.PARAM_TOTAL_AMOUNT);
            if (!totalAmount.equals(callbackAmount)) {
                log.error(">>> callback amount error, result:{}, order:{}", result, order);
                return PayConstant.FAILURE;
            }
            // 检查是否已经回调
            int checkJournal = orderJournalRepository.countByBizIdAndPayTypeAndBillType(order.getOrderId(), order.getPayType(), BillTypeEnum.ORDER.getType());
            if (checkJournal > 0) {
                log.error(">>> callback journal repeat, order:{}", order);
                return PayConstant.FAILURE;
            }
            // 检查支付状态
            String tradeStatus = result.get(AlipayConstant.CALLBACK_TRADE_STATUS);
            if (AlipayConstant.TRADE_STATUS_SUCCESS.equals(tradeStatus) || AlipayConstant.TRADE_STATUS_FINISHED.equals(tradeStatus)) {
                // 记录流水
                OrderJournal orderJournal = OrderJournal.build(
                        order.getOrderId(),
                        order.getUserId(),
                        BillTypeEnum.ORDER.getType(),
                        order.getPayType(),
                        order.getOrderAmount(),
                        BillTypeEnum.ORDER.getValue()
                );
                orderJournalRepository.save(orderJournal);
                // 更新订单状态 TODO MQ
                int res = orderRepository.updateOrderStatus(OrderStatusEnum.PAID.getStatus(), order.getOrderId(), OrderStatusEnum.WAIT_PAY.getStatus());
                if (res <= 0) {
                    log.error(">>> callback update order status failure, order:{}", order);
                }
                return PayConstant.SUCCESS;
            } else if (AlipayConstant.TRADE_STATUS_SUCCESS.equals(tradeStatus)) {
                return PayConstant.SUCCESS;
            } else {
                return PayConstant.FAILURE;
            }
        } else {
            log.error(">>> callback sign verify failure, result:{}", result);
            return PayConstant.FAILURE;
        }
    }

    public String refundCallback() {

        return PayConstant.SUCCESS;
    }

}
