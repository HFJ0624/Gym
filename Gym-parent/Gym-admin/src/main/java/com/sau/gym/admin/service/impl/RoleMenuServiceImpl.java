package com.sau.gym.admin.service.impl;

import com.sau.gym.admin.mapper.RoleMenuMapper;
import com.sau.gym.admin.service.MenuService;
import com.sau.gym.admin.service.RoleMenuService;
import com.sau.gym.model.entity.menu.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/6 20:36
 */
@Service
public class RoleMenuServiceImpl implements RoleMenuService {

    @Autowired
    private MenuService menuService;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    //根据角色的id查询出其对应的菜单id，并且需要将系统中所有的菜单数据查询出来
    @Override
    public Map<String, Object> findRoleMenuByRoleId(Long roleId) {
        //1.查询所有的菜单数据
        List<Menu> menuList = menuService.findNodes();

        //2.查询当前角色的菜单数据
        List<Long> roleMenuIds = roleMenuMapper.findRoleMenuByRoleId(roleId);

        //3.将数据存储到Map中进行返回
        Map<String , Object> result = new HashMap<>() ;
        result.put("sysMenuList" , menuList);
        result.put("roleMenuIds" , roleMenuIds);

        //4.返回
        return result;
    }
}
