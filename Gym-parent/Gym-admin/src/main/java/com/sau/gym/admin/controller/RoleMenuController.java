package com.sau.gym.admin.controller;

import com.sau.gym.admin.service.RoleMenuService;
import com.sau.gym.model.dto.menu.AssignMenuDto;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/6 20:35
 */
@RestController
@RequestMapping(value = "/admin/system/roleMenu")
public class RoleMenuController {

    @Autowired
    private RoleMenuService roleMenuService;

    //根据角色的id查询出其对应的菜单id，并且需要将系统中所有的菜单数据查询出来
    @GetMapping(value = "/findRoleMenuByRoleId/{roleId}")
    public Result<Map<String , Object>> findRoleMenuByRoleId(@PathVariable(value = "roleId") Long roleId) {
        Map<String , Object> roleMenuList = roleMenuService.findRoleMenuByRoleId(roleId);
        return Result.build(roleMenuList, ResultCodeEnum.SUCCESS);
    }

    //保存分配菜单
    @PostMapping("/doAssign")
    public Result doAssign(@RequestBody AssignMenuDto assignMenuDto) {
        roleMenuService.doAssign(assignMenuDto);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }
}
