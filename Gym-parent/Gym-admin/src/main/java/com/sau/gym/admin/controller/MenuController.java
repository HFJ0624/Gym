package com.sau.gym.admin.controller;

import com.sau.gym.admin.service.MenuService;
import com.sau.gym.common.log.annotation.Log;
import com.sau.gym.common.log.enums.OperatorType;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.menu.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/6 19:14
 */
@RestController
@RequestMapping(value="/admin/system/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    //找到所有节点
    @GetMapping(value = "/findNodes")
    public Result<List<Menu>> findNodes(){
        List<Menu> list = menuService.findNodes();
        return Result.build(list, ResultCodeEnum.SUCCESS);
    }

    //保存菜单
    @Log(title = "保存菜单",businessType = 1,operatorType = OperatorType.MANAGE)
    @PostMapping(value = "/save")
    public Result save(@RequestBody Menu menu){
        menuService.save(menu);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }

    @Log(title = "修改菜单",businessType = 2,operatorType = OperatorType.MANAGE)
    //修改菜单
    @PutMapping(value = "/updateById")
    public Result updateById(@RequestBody Menu menu){
        menuService.updateById(menu);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }

    @Log(title = "删除菜单",businessType = 3,operatorType = OperatorType.MANAGE)
    //根据菜单id删除菜单
    @DeleteMapping(value = "/removeById/{id}")
    public Result removeById(@PathVariable(value = "id") Long id){
        menuService.removeById(id);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }
}
