package com.thinkingin.cloud.util;

import com.thinkingin.cloud.dto.ResponseResult;

/**
 * @author wbw
 * @version 1.0
 * @date 2020/4/2 6:33 上午
 */


public class RestResultGenerator {
    private static <T> ResponseResult<T> generateResult(T data, String message, String errorMsg, boolean isSuccess) {
        ResponseResult<T> result = new ResponseResult<>();
        result.setResult(data);
        result.setSuccess(isSuccess);
        result.setMessage(message);
        result.setErrorMsg(errorMsg);
        return result;
    }

    public static <T> ResponseResult<T> generateFailReuslt(String errorMsg) {
        return generateResult(null, null, errorMsg, false);
    }

    public static <T> ResponseResult<T> generateSuccessResult(T data) {
        return generateResult(data, null, null, true);
    }
}