package com.sau.gym.model.entity.base;

import lombok.Getter;

@Getter
public enum ResultCodeEnum {

    SUCCESS(200 , "操作成功") ,
    LOGIN_PROHIBIT(301,"用户为禁用状态"),
    LOGIN_ERROR(201 , "用户名或者密码错误"),
    VALIDATECODE_ERROR(202 , "验证码错误") ,
    LOGIN_AUTH(208 , "用户未登录"),
    USER_NAME_IS_EXISTS(209 , "用户名已经存在"),
    SYSTEM_ERROR(9999 , "您的网络有问题请稍后重试"),
    NODE_ERROR( 217, "该节点下有子节点，不可以删除"),
    DATA_ERROR(204, "数据异常"),
    ACCOUNT_STOP( 216, "账号已停用"),
    STOCK_LESS( 219, "库存不足"),
    VENUE_NAME_EXIST(220,"场馆名称已经存在"),
    PHONE_ERROR(221,"电话错误或不存在"),
    PASSWORD_NOT_EQ(222,"新密码和旧密码不相等"),
    EMAIL_NOT_EXIST(223,"邮箱不存在"),
    BALANCE_NOT_ENOUGH(224,"用户余额不足,请你充值"),
    ;

    //业务状态码
    private Integer code;

    //响应消息
    private String message;

    private ResultCodeEnum(Integer code,String message){
        this.code = code;
        this.message = message;
    }
}
