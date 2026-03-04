package com.sau.gym.common.exception;


import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/4 15:50
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    //全局异常处理
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e){
        e.printStackTrace();
        return Result.build(null , ResultCodeEnum.SYSTEM_ERROR);
    }

    //自定义异常处理类
    @ExceptionHandler(value = SauException.class)
    @ResponseBody
    public Result error(SauException exception) {
        return Result.build(null , exception.getResultCodeEnum());
    }
}
