package com.sau.gym.admin.controller;

import com.sau.gym.admin.service.MenuService;
import com.sau.gym.admin.service.UserService;
import com.sau.gym.admin.service.ValidateCodeService;
import com.sau.gym.model.dto.system.LoginDto;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.user.User;
import com.sau.gym.model.vo.menu.MenuVo;
import com.sau.gym.model.vo.system.LoginVo;
import com.sau.gym.model.vo.system.ValidateCodeVo;
import com.sau.gym.utils.AuthContextUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 作者:hfj
 * 功能:体育场馆的首页登录及注册功能
 * 日期: 2026/3/4 15:24
 */
@Tag(name = "用户接口")
@RestController
@RequestMapping(value = "/admin/system/index")
public class IndexController {

    @Autowired
    private UserService userService;

    @Autowired
    private ValidateCodeService validateCodeService;

    @Autowired
    private MenuService menuService;

    @Operation(summary = "登录接口")
    @PostMapping(value = "/login")
    public Result<LoginVo> login(@RequestBody LoginDto loginDto){
        LoginVo loginVo = userService.login(loginDto);
        return Result.build(loginVo,ResultCodeEnum.SUCCESS);
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

    //退出功能
    @GetMapping("/logout")
    public Result logout(@RequestHeader(value = "token") String token){
        userService.logout(token);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }

    //动态显示该角色才有的菜单
    @GetMapping("/menus")
    public Result menus() {
        List<MenuVo> menuVoList =  menuService.findUserMenuList();
        return Result.build(menuVoList, ResultCodeEnum.SUCCESS);
    }
}
