package com.sau.gym.admin.service.impl;

import com.sau.gym.admin.mapper.MenuMapper;
import com.sau.gym.admin.service.MenuService;
import com.sau.gym.admin.utils.MenuHelper;
import com.sau.gym.model.entity.menu.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/6 19:15
 */
@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuMapper menuMapper;

    //找到所有节点
    @Override
    public List<Menu> findNodes() {
        List<Menu> menuList = menuMapper.selectAll();
        if (CollectionUtils.isEmpty(menuList)){
            return null;
        }
        //构建树形结构
        List<Menu> treeList = MenuHelper.buildTree(menuList);
        return treeList;
    }
}
