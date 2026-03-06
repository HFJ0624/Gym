package com.sau.gym.admin.controller;

import com.sau.gym.admin.service.MenuService;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.menu.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
