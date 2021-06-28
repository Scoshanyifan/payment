package com.kunbu.pay.payment.web;

import com.google.common.collect.Maps;
import com.kunbu.pay.payment.constant.PayTypeEnum;
import com.kunbu.pay.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.Map;

@RestController
@RequestMapping("/notify")
public class PayCallbackController {

    @Autowired
    private PaymentService paymentService;

    /**
     * https://www.merchant.com/receive_notify.htm?
     * notify_type=trade_status_sync&
     * notify_id=91722adff935e8cfa58b3aabf4dead6ibe&
     * notify_time=2017-02-16 21:46:15&
     * sign_type=RSA2&
     * sign=WcO+t3D8Kg71dTlKwN7r9PzUOXeaBJwp8/FOuSxcuSkXsoVYxBpsAidprySCjHCjmaglNcjoKJQLJ28/Asl93joTW39FX6i07lXhnbPknezAlwmvPdnQuI01HZsZF9V1i6ggZjBiAd5lG8bZtTxZOJ87ub2i9GuJ3Nr/NUc9VeY=&
     * fund_bill_list=null&
     * receipt_amount=null&
     * invoice_amount=null&
     * buyer_pay_amount=null&
     * point_amount=null&
     * voucher_detail_list=null&
     * passback_params=null&
     * out_channel_type=null&
     * trade_no=null&
     * app_id=null&
     * out_trade_no=null&
     * out_biz_no=null&
     * buyer_id=null&
     * seller_id=null&
     * trade_status=null&
     * total_amount=null&
     * refund_fee=null&
     * subject=null&
     * body=null&
     * gmt_create=null&
     * gmt_payment=null&
     * gmt_refund=null&
     * gmt_close=null&
     * charge_amount=8.88&
     * charge_flags=bluesea_1&
     * settlement_id=2018101610032004620239146945&
     * receipt_currency_type=DC
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/alipay")
    public String callback(HttpServletRequest request, HttpServletResponse response) {

        return paymentService.payCallback(getRequestParams(request), PayTypeEnum.ALIPAY.getType());

    }

    private Map<String, String> getRequestParams(HttpServletRequest request) {
        Map<String,String> paramMap = Maps.newHashMap();
        Enumeration<String> parameterNames = request.getParameterNames();
        String name;
        while(parameterNames.hasMoreElements()){
            name = parameterNames.nextElement();
            paramMap.put(name, request.getParameter(name));
        }
        return paramMap;
    }

}
