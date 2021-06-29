package com.kunbu.pay.payment.order.constant;

public enum OrderStatusEnum {

    WAIT_PAY(1, "已下单待付款"),
    PAID(2, "已支付待发货"),
    DELIVERED(3, "已发货待收货"),
    FINISHED(4, "已完成"),
    /**
     * 下单未支付-取消
     * 已付款-取消
     */
    CANCEL(5, "已取消"),

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
