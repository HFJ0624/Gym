package com.sau.gym.admin.service.impl;

import com.sau.gym.admin.mapper.MenuMapper;
import com.sau.gym.admin.service.MenuService;
import com.sau.gym.admin.utils.MenuHelper;
import com.sau.gym.common.exception.SauException;
import com.sau.gym.model.entity.base.ResultCodeEnum;
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

    //保存菜单
    @Override
    public void save(Menu menu) {
        menuMapper.save(menu);
    }

    //修改菜单
    @Override
    public void updateById(Menu menu) {
        menuMapper.updateById(menu);
    }

    //根据菜单id删除菜单
    @Override
    public void removeById(Long id) {
        //1.先查询是否存在子菜单，如果存在不允许进行删除
        int count = menuMapper.countByParentId(id);
        if (count > 0){
            throw new SauException(ResultCodeEnum.NODE_ERROR);
        }

        //2.不存在子菜单直接删除
        menuMapper.deleteById(id);
    }
}
