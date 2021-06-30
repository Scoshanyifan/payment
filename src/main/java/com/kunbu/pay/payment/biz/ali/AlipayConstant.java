package com.kunbu.pay.payment.biz.ali;

public interface AlipayConstant {

    String CONFIG_SERVER_URL                = "alipay.serverUrl";
    String CONFIG_APP_ID                    = "alipay.appId";
    String CONFIG_PRIVATE_KEY               = "alipay.privateKey";
    String CONFIG_ALIPAY_PUBLIC_KEY         = "alipay.alipayPublicKey";

    String CONFIG_FORMAT                    = "alipay.format";
    String CONFIG_CHARSET                   = "alipay.charset";
    String CONFIG_SIGN_TYPE                 = "alipay.signType";

    String CONFIG_NOTIFY_URL                = "alipay.notifyUrl";
    String CONFIG_RETURN_URL                = "alipay.returnUrl";

    /** ================================= 支付接口参数 ================================= */

    String PARAM_OUT_TRADE_NO               = "out_trade_no";
    String PARAM_PRODUCT_CODE               = "product_code";
    String PARAM_TOTAL_AMOUNT               = "total_amount";
    String PARAM_SUBJECT                    = "subject";
    String PARAM_APP_ID                      = "app_id";
    String PARAM_SELLER_ID                  = "seller_id";

    String PRODUCT_CODE_PAGE                = "FAST_INSTANT_TRADE_PAY";

    String CALLBACK_TRADE_STATUS             = "trade_status";
    String TRADE_STATUS_SUCCESS             = "TRADE_SUCCESS";
    String TRADE_STATUS_FINISHED            = "TRADE_FINISHED";
    String TRADE_STATUS_CLOSED              = "TRADE_CLOSED";

}
