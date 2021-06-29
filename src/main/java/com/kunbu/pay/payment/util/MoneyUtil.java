package com.kunbu.pay.payment.util;

import java.math.BigDecimal;

public class MoneyUtil {

    public static String convertFen2Yuan(Long amount) {
        return new BigDecimal(amount).movePointLeft(2).toString();
    }

}
