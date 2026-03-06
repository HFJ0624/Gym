package com.sau.gym.admin.controller;

import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.service.RoleService;
import com.sau.gym.model.dto.role.RoleDto;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.role.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/5 10:23
 */
@RestController
@RequestMapping(value = "/admin/system/sysRole")
public class RoleController {

    @Autowired
    private RoleService roleService;

    //分页查询所有角色
    @PostMapping("/findByPage/{pageNum}/{pageSize}")
    public Result<PageInfo<Role>> findByPage(@RequestBody RoleDto roleDto , @PathVariable(value = "pageNum") Integer pageNum , @PathVariable(value = "pageSize") Integer pageSize){
        PageInfo<Role> pageInfo = roleService.findByPage(roleDto,pageNum,pageSize);
        return Result.build(pageInfo, ResultCodeEnum.SUCCESS);
    }

    //角色添加
    @PostMapping(value = "/saveSysRole")
    public Result saveRole(@RequestBody Role role){
        roleService.saveRole(role);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }

    //角色修改
    @PutMapping(value = "/updateSysRole")
    public Result updateRole(@RequestBody Role role){
        roleService.updateRole(role);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }

    //角色删除
    @DeleteMapping(value = "/deleteById/{roleId}")
    public Result deleteById(@PathVariable(value = "roleId") Long roleId){
        roleService.deleteById(roleId);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }

    //查询所有角色
    @GetMapping(value = "/findAllRoles")
    public Result<Map<String,Object>> findAllRoles(){
        Map<String, Object> resultMap = roleService.findAllRoles();
        return Result.build(resultMap,ResultCodeEnum.SUCCESS);
    }
}
