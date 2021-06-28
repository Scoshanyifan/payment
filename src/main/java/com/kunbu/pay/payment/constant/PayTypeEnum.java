package com.kunbu.pay.payment.constant;

public enum PayTypeEnum {

    ALIPAY(1, "支付宝"),
    WXPAY(2, "微信支付"),
    BANK_(3, "支付宝"),


    ;

    private Integer type;

    private String value;

    PayTypeEnum(Integer type, String value) {
        this.type = type;
        this.value = value;
    }

    public Integer getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
