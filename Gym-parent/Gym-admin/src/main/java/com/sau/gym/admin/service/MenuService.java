package com.sau.gym.admin.service;

import com.sau.gym.model.entity.menu.Menu;
import com.sau.gym.model.vo.menu.MenuVo;

import java.util.List;

public interface MenuService {

    //找到所有节点
    List<Menu> findNodes();

    //保存菜单
    void save(Menu menu);

    //修改菜单
    void updateById(Menu menu);

    //根据菜单id删除菜单
    void removeById(Long id);

    //动态显示该角色才有的菜单
    List<MenuVo> findUserMenuList();
}
