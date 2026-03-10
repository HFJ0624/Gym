package com.sau.gym.admin.controller;

import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.service.UserService;
import com.sau.gym.common.log.annotation.Log;
import com.sau.gym.common.log.enums.OperatorType;
import com.sau.gym.model.dto.role.AssignRoleDto;
import com.sau.gym.model.dto.user.UserDto;
import com.sau.gym.model.dto.user.UserStatusDTO;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 作者:hfj
 * 功能:用户的增删改查功能
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

    @Log(title = "新增用户",businessType = 1,operatorType = OperatorType.MANAGE)
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

    @Log(title = "修改用户",businessType = 2,operatorType = OperatorType.MANAGE)
    //修改用户信息
    @PutMapping(value = "/updateUser")
    public Result updateUser(@RequestBody User user){
        userService.updateUser(user);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }

    @Log(title = "删除用户",businessType = 3,operatorType = OperatorType.MANAGE)
    //根据用户Id删除用户信息
    @DeleteMapping(value = "/deleteById/{userId}")
    public Result deleteById(@PathVariable("userId") Long userId){
        userService.deleteById(userId);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }

    //分配角色
    @PostMapping(value = "/doAssign")
    public Result doAssign(@RequestBody AssignRoleDto assignRoleDto){
        userService.doAssign(assignRoleDto);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }

    @Log(title = "注册用户",businessType = 0,operatorType = OperatorType.MANAGE)
    //注册用户
    @PostMapping(value = "/register")
    public Result register(@RequestBody User user){
        userService.register(user);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }
}
