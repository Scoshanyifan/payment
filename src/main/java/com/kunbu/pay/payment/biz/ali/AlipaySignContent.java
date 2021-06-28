package com.kunbu.pay.payment.biz.ali;

import lombok.Data;

import java.io.Serializable;

@Data
public class AlipaySignContent implements Serializable {

    private String outTradeNo;

    private String title;

    private String totalAmount;

}
