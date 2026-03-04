package com.sau.gym.common.exception;

import com.sau.gym.model.entity.base.ResultCodeEnum;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:自定义异常类
 * 日期: 2026/3/4 15:52
 */
@Data
public class SauException extends RuntimeException{

    //错误状态码
    private Integer code;

    //错误消息
    private String message;

    //封装错误状态码和错误消息
    private ResultCodeEnum resultCodeEnum;

    public SauException(ResultCodeEnum resultCodeEnum) {
        this.resultCodeEnum = resultCodeEnum;
        this.code = resultCodeEnum.getCode();
        this.message = resultCodeEnum.getMessage();
    }

    public SauException(Integer code , String message) {
        this.code = code;
        this.message = message;
    }
}
