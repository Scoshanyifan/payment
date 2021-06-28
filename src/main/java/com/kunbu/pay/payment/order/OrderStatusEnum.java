package com.kunbu.pay.payment.order;

public enum OrderStatusEnum {

    WAIT_PAY(0, "待支付"),
    PAID(1, "已支付"),
    CANCEL(2, "已取消"),

    ;

    private Integer status;

    private String value;

    OrderStatusEnum(int status, String value) {
        this.status = status;
        this.value = value;
    }

    public Integer getStatus() {
        return status;
    }

    public String getValue() {
        return value;
    }

    public static OrderStatusEnum of(Integer status) {
        for (OrderStatusEnum e : values()) {
            if (e.status.equals(status)) {
                return e;
            }
        }
        return null;
    }
}
