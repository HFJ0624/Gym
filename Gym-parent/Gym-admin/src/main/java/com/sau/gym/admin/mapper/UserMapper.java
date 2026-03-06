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

    //保存用户
    void saveUser(User user);

    //修改用户状态
    int updateUserStatus(Long id, String status);

    //修改用户信息
    void updateUser(User user);

    //根据用户Id删除用户信息
    void deleteById(Long id);
}
