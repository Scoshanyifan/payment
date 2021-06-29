package com.kunbu.pay.payment.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class ApiResult implements Serializable {

    private static final Boolean SUCCESS = new Boolean(true);
    private static final Boolean FAILURE = new Boolean(false);

    private static final Integer FAILURE_CODE = -1;

    private Integer code;

    private String msg;

    private Boolean success;

    private Object data;

    private ApiResult() {}

    public static ApiResult success() {
        return success(null);
    }

    public static ApiResult success(Object data) {
        ApiResult result = new ApiResult();
        result.setSuccess(SUCCESS);
        result.setData(data);
        return result;
    }

    public static ApiResult failure(String msg) {
        return failure(FAILURE_CODE, msg);
    }

    public static ApiResult failure(Integer code, String msg) {
        ApiResult result = new ApiResult();
        result.setSuccess(FAILURE);
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    public boolean isSuccess() {
        return success.booleanValue() == true;
    }
}
