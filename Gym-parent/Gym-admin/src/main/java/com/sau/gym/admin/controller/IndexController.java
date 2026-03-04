package com.sau.gym.admin.controller;

import com.sau.gym.admin.service.SysUserService;
import com.sau.gym.admin.service.ValidateCodeService;
import com.sau.gym.model.dto.system.LoginDto;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.user.User;
import com.sau.gym.model.vo.system.LoginVo;
import com.sau.gym.model.vo.system.ValidateCodeVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/4 15:24
 */
@Tag(name = "用户接口")
@RestController
@RequestMapping(value = "/admin/system/index")
public class IndexController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private ValidateCodeService validateCodeService;

    @Operation(summary = "登录接口")
    @PostMapping(value = "/login")
    public Result<LoginVo> login(@RequestBody LoginDto loginDto){
        LoginVo loginVo = sysUserService.login(loginDto);
        return Result.build(loginVo,ResultCodeEnum.SUCCESS);
    }

    //获取验证码图片
    @GetMapping(value = "/generateValidateCode")
    public Result<ValidateCodeVo> generateValidateCode() {
        ValidateCodeVo validateCodeVo = validateCodeService.generateValidateCode();
        return Result.build(validateCodeVo,ResultCodeEnum.SUCCESS);
    }

    //获取用户信息
    @GetMapping("/getUserInfo")
    public Result<User> getUserInfo(@RequestHeader(name = "token") String token){
        User user = sysUserService.getUserInfo(token);
        return Result.build(user,ResultCodeEnum.SUCCESS);
    }

    //退出功能
    @GetMapping("/logout")
    public Result logout(@RequestHeader(value = "token") String token){
        sysUserService.logout(token);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }
}
