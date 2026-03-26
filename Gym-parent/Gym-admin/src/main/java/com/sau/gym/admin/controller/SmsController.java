package com.sau.gym.admin.controller;

import com.sau.gym.admin.service.SmsService;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/25 14:15
 */
@RestController
@RequestMapping(value = "/admin/system/index")
public class SmsController {

    @Autowired
    private SmsService smsService;

    //1.先发送短信验证码
    @GetMapping(value = "/sendCode/{phone}")
    public Result sendValidateCode(@PathVariable String phone) {
        smsService.sendValidateCode(phone);
        return Result.build(null , ResultCodeEnum.SUCCESS);
    }

}
