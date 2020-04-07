package com.thinkingin.cloud.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

/**
 * @author wbw
 * @version 1.0
 * @date 2020/4/2 6:33 上午
 */

@Data
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseResult<T> {

    private boolean success;
    private String message;
    private T result;
    private String errorMsg;
}
