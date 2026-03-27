package com.sau.gym.admin.service;

import com.github.pagehelper.PageInfo;
import com.sau.gym.model.dto.role.AssignRoleDto;
import com.sau.gym.model.dto.system.LoginDto;
import com.sau.gym.model.dto.user.FrontUserDto;
import com.sau.gym.model.dto.user.UserDto;
import com.sau.gym.model.entity.user.User;
import com.sau.gym.model.vo.system.LoginVo;
import com.sau.gym.model.vo.user.UserVo;

import java.util.List;

public interface UserService {

    //登录接口
    LoginVo login(LoginDto loginDto);

    //获取用户信息
    User getUserInfo(String token);

    //退出功能
    void logout(String token);

    //角色的分页查询
    PageInfo<User> findByPage(UserDto userDto, Integer pageNum, Integer pageSize);

    //角色添加
    void saveUser(User user);

    //修改用户状态
    boolean updateUserStatus(Long id, String status);

    //修改用户信息
    void updateUser(User user);

    //根据用户Id删除用户信息
    void deleteById(Long userId);

    //分配角色
    void doAssign(AssignRoleDto assignRoleDto);

    //注册用户
    void register(User user);

    //统计用户的男女比例数量
    List<UserVo> findGender();

    //用户修改个人信息
    void updateProfile(FrontUserDto frontUserDto);
}
