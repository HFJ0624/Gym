package com.sau.gym.admin.controller;

import com.sau.gym.admin.service.UserService;
import com.sau.gym.admin.service.ValidateCodeService;
import com.sau.gym.admin.service.VenueService;
import com.sau.gym.common.log.annotation.Log;
import com.sau.gym.common.log.enums.OperatorType;
import com.sau.gym.model.dto.system.LoginDto;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.user.User;
import com.sau.gym.model.vo.system.LoginVo;
import com.sau.gym.model.vo.system.ValidateCodeVo;
import com.sau.gym.utils.AuthContextUtil;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 作者:hfj
 * 功能:前台用户的登录功能
 * 日期: 2026/3/17 17:16
 */
@RestController
@RequestMapping(value = "/front")
public class FrontIndexController {

    @Autowired
    private UserService userService;

    @Autowired
    private VenueService venueService;

    @Autowired
    private ValidateCodeService validateCodeService;

    //前台登录后台
    @Log(title = "前台登录",businessType = 0,operatorType = OperatorType.MANAGE)
    @Operation(summary = "登录接口")
    @PostMapping(value = "/login")
    public Result<LoginVo> login(@RequestBody LoginDto loginDto){
        LoginVo loginVo = userService.login(loginDto);
        return Result.build(loginVo, ResultCodeEnum.SUCCESS);
    }

    //获取验证码图片
    @GetMapping(value = "/generateValidateCode")
    public Result<ValidateCodeVo> generateValidateCode() {
        ValidateCodeVo validateCodeVo = validateCodeService.generateValidateCode();
        return Result.build(validateCodeVo,ResultCodeEnum.SUCCESS);
    }

    //后端获取用户信息的接口就无需获取token，然后根据token从Redis中进行查询。可以直接从ThreadLocal中获取用户信息，然后进行返回。
    @GetMapping("/getUserInfo")
    public Result<User> getUserInfo(){
        return Result.build(AuthContextUtil.get(),ResultCodeEnum.SUCCESS);
    }

    //注册用户
    @Log(title = "前台注册用户",businessType = 0,operatorType = OperatorType.MANAGE)
    @PostMapping(value = "/register")
    public Result register(@RequestBody User user){
        userService.register(user);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }

    //查找所有场馆
    @GetMapping("/venues")
    public Result<Map<String,Object>> getAllVenue(){
        Map<String, Object> resultMap = venueService.getAllVenue();
        return Result.build(resultMap,ResultCodeEnum.SUCCESS);
    }
}
