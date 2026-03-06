package com.sau.gym.admin.utils;

import com.sau.gym.model.entity.menu.Menu;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者:hfj
 * 功能:构建树形菜单的工具类
 * 日期: 2026/3/6 19:20
 */
public class MenuHelper {

    /**
     * 使用递归方法建菜单
     * @param MenuList 菜单集合
     * @return 返回树形结构
     */
    public static List<Menu> buildTree(List<Menu> MenuList) {
        List<Menu> trees = new ArrayList<>();
        for (Menu menu : MenuList) {
            if (menu.getParentId().longValue() == 0) {
                trees.add(findChildren(menu,MenuList));
            }
        }
        return trees;
    }

    /**
     * 递归查找子节点
     * @param treeNodes 子节点
     * @return 返回最后一层菜单
     */
    public static Menu findChildren(Menu menu, List<Menu> treeNodes) {
        menu.setChildren(new ArrayList<Menu>());
        for (Menu it : treeNodes) {
            if(menu.getId().longValue() == it.getParentId().longValue()) {
                menu.getChildren().add(findChildren(it,treeNodes));
            }
        }
        return menu;
    }
}
