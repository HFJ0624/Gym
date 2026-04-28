package com.sau.gym.admin.controller;

import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.service.SignInService;
import com.sau.gym.model.dto.system.SignInDto;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.system.SignIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 作者:hfj
 * 功能:后台查看签到功能
 * 日期: 2026/4/28 8:35
 */
@RestController
@RequestMapping("/admin/booking/signIn")
public class SignInController {

    @Autowired
    private SignInService signInService;

    //分页查看所有签到记录
    @PostMapping(value = "/findByPage/{current}/{limit}")
    public Result<PageInfo<SignIn>> findByPage(@PathVariable Integer current,@PathVariable Integer limit, @RequestBody SignInDto signInDto) {
        PageInfo<SignIn> pageInfo = signInService.findByPage(current,limit,signInDto);
        return Result.build(pageInfo, ResultCodeEnum.SUCCESS);
    }
}
