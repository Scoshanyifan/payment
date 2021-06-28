package com.kunbu.pay.payment.order;

/**
 * 业务类型
 */
public enum BizTypeEnum {

    ORDER(1, "订单业务"),
    SERVICE(2, "服务业务"),

    ;

    private Integer type;

    private String value;

    BizTypeEnum(Integer type, String value) {
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
