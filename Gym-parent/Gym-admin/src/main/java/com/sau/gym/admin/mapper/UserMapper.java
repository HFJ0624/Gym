package com.sau.gym.admin.mapper;


import com.sau.gym.model.dto.user.UserDto;
import com.sau.gym.model.entity.user.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

    //根据用户名查询用户
    User selectByUserName(String userName);

    //角色的分页查询
    List<User> findByPage(UserDto userDto);
}
