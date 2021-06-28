package com.kunbu.pay.payment.constant;

public enum BillTypeEnum {

    ORDER(1, "订单"),
    REFUND(2, "退款"),
    WITHDRAW(3, "提现"),
    DEPOSIT(4, "保证金"),
    ;

    private Integer type;

    private String value;

    BillTypeEnum(Integer type, String value) {
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
