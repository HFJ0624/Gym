package com.sau.gym.admin.controller;

import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.service.UserService;
import com.sau.gym.model.dto.user.UserDto;
import com.sau.gym.model.dto.user.UserStatusDTO;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/5 16:49
 */
@RestController
@RequestMapping(value = "/admin/system/user")
public class UserController {

    @Autowired
    private UserService userService;

    //角色的分页查询
    @PostMapping(value = "/findByPage/{pageNum}/{pageSize}")
    public Result<PageInfo<User>> findByPage(@RequestBody UserDto userDto,@PathVariable(value = "pageNum") Integer pageNum , @PathVariable(value = "pageSize") Integer pageSize){
        PageInfo<User> pageInfo = userService.findByPage(userDto,pageNum,pageSize);
        return Result.build(pageInfo, ResultCodeEnum.SUCCESS);
    }

    //角色添加
    @PostMapping(value = "/saveUser")
    public Result saveUser(@RequestBody User user){
        userService.saveUser(user);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }

    //修改用户状态
    @PutMapping("/updateUserStatus")
    public Result updateUserStatus(@RequestBody UserStatusDTO userStatusDTO){
        boolean is_success = userService.updateUserStatus(userStatusDTO.getId(),userStatusDTO.getStatus());
        if (is_success){
            return Result.build(null,ResultCodeEnum.SUCCESS);
        }else {
            return Result.build(null,ResultCodeEnum.DATA_ERROR);
        }
    }

    //修改用户信息
    @PutMapping(value = "/updateUser")
    public Result updateUser(@RequestBody User user){
        userService.updateUser(user);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }

    //根据用户Id删除用户信息
    @DeleteMapping(value = "/deleteById/{userId}")
    public Result deleteById(@PathVariable("userId") Long userId){
        userService.deleteById(userId);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }
}
