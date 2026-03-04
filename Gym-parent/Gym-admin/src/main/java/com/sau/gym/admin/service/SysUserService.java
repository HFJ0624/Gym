package com.sau.gym.admin.service;

import com.sau.gym.model.dto.system.LoginDto;
import com.sau.gym.model.entity.user.User;
import com.sau.gym.model.vo.system.LoginVo;

public interface SysUserService {

    //登录接口
    LoginVo login(LoginDto loginDto);

    //获取用户信息
    User getUserInfo(String token);

    //退出功能
    void logout(String token);
}
