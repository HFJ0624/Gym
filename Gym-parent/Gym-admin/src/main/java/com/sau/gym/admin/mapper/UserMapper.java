package com.sau.gym.admin.mapper;


import com.sau.gym.model.dto.user.UserDto;
import com.sau.gym.model.entity.user.User;
import com.sau.gym.model.vo.user.UserVo;
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

    //注册用户
    void register(User user);

    //统计用户的男女比例数量
    List<UserVo> findGender();

    //根据用户电话查询用户
    User selectByUserPhone(String phone);

    //先查数据库的用户
    User selectById(Long id);

    //根据用户邮箱查询用户(假设邮箱唯一)
    User selectByUserEmail(String email);

    //批量插入数据
    void batchInsert(List<User> userList);
}
