package com.sau.gym.admin.service;

import com.sau.gym.model.dto.system.LoginDto;
import com.sau.gym.model.vo.system.LoginVo;

public interface SysUserService {

    //登录接口
    LoginVo login(LoginDto loginDto);
}
