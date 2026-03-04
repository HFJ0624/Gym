package com.sau.gym.admin.mapper;


import com.sau.gym.model.entity.user.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysUserMapper{

    //根据用户名查询用户
    User selectByUserName(String userName);
}
