package com.sau.gym.admin.service;

import com.github.pagehelper.PageInfo;
import com.sau.gym.model.dto.system.LoginDto;
import com.sau.gym.model.dto.user.UserDto;
import com.sau.gym.model.entity.user.User;
import com.sau.gym.model.vo.system.LoginVo;

public interface UserService {

    //登录接口
    LoginVo login(LoginDto loginDto);

    //获取用户信息
    User getUserInfo(String token);

    //退出功能
    void logout(String token);

    //角色的分页查询
    PageInfo<User> findByPage(UserDto userDto, Integer pageNum, Integer pageSize);
}
