package com.kunbu.pay.payment.biz.ali;

import cn.hutool.json.JSONUtil;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.kunbu.pay.payment.util.PropertyPayUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AlipayClientSingleton {

    private AlipayClientSingleton(){}

    private static class SingletonFactory {
        private static AlipayClient instance = new DefaultAlipayClient(
                PropertyPayUtil.getValue(AlipayConstant.CONFIG_SERVER_URL),
                PropertyPayUtil.getValue(AlipayConstant.CONFIG_APP_ID),
                PropertyPayUtil.getValue(AlipayConstant.CONFIG_PRIVATE_KEY),
                PropertyPayUtil.getValue(AlipayConstant.CONFIG_FORMAT),
                PropertyPayUtil.getValue(AlipayConstant.CONFIG_CHARSET),
                PropertyPayUtil.getValue(AlipayConstant.CONFIG_ALIPAY_PUBLIC_KEY),
                PropertyPayUtil.getValue(AlipayConstant.CONFIG_SIGN_TYPE)
        );
    }

    public static AlipayClient getAlipayClient() {
        return SingletonFactory.instance;
    }

}
