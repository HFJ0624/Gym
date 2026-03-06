package com.sau.gym.admin.service;

import com.sau.gym.model.entity.menu.Menu;

import java.util.List;

public interface MenuService {

    //找到所有节点
    List<Menu> findNodes();
}
