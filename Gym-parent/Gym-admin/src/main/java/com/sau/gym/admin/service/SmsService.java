package com.sau.gym.admin.service;

public interface SmsService {

    //1.先发送短信验证码
    void sendValidateCode(String phone);
}
