package com.sau.gym.model.entity.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:响应结果实体类
 * 日期: 2026/3/3 20:33
 */
@Data
@Schema(description = "响应结果实体类")
public class Result<T> {

    @Schema(description = "业务状态码") //返回码
    private Integer code;

    @Schema(description = "响应消息") //返回消息
    private String message;

    @Schema(description = "业务数据") //返回数据
    private T data;

    //私有化构造
    private Result() {

    }

    //返回数据
    public static <T> Result<T> build(T body,Integer code,String message){
        Result<T> result = new Result<>();
        result.setData(body);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    //通过枚举构造Result对象
    public static <T> Result build(T body,ResultCodeEnum resultCodeEnum){
        return build(body,resultCodeEnum.getCode(),resultCodeEnum.getMessage());
    }
}
