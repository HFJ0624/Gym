package com.sau.gym.admin.service.impl;

import com.sau.gym.admin.mapper.MenuMapper;
import com.sau.gym.admin.mapper.RoleMenuMapper;
import com.sau.gym.admin.service.MenuService;
import com.sau.gym.admin.utils.MenuHelper;
import com.sau.gym.common.exception.SauException;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.menu.Menu;
import com.sau.gym.model.entity.user.User;
import com.sau.gym.model.vo.menu.MenuVo;
import com.sau.gym.utils.AuthContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
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

    @Autowired
    private RoleMenuMapper roleMenuMapper;

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
    @Transactional
    @Override
    public void save(Menu menu) {
        //添加新节点菜单方法
        menuMapper.save(menu);

        //新添加一个菜单，那么此时就需要将该菜单所对应的父级菜单设置为半开 is_half = 1
        updateSysRoleMenuIsHalf(menu);
    }

    private void updateSysRoleMenuIsHalf(Menu sysMenu) {
        //1.查询是否存在父节点
        Menu parentMenu = menuMapper.selectById(sysMenu.getParentId());

        if(parentMenu != null) {
            //2.将该id的菜单设置为半开
            roleMenuMapper.updateSysRoleMenuIsHalf(parentMenu.getId()) ;
            //3.递归调用
            updateSysRoleMenuIsHalf(parentMenu) ;
        }
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

    //动态显示该角色才有的菜单
    @Override
    public List<MenuVo> findUserMenuList() {

        //1.获取当前登录用户的id
        User sysUser = AuthContextUtil.get();
        Long userId = sysUser.getId();

        //2.根据用户id查询该用户有的菜单
        List<Menu> menuList = menuMapper.selectListByUserId(userId);

        //3.构建树形数据
        List<Menu> menuTreeList = MenuHelper.buildTree(menuList);
        return this.buildMenus(menuTreeList);
    }

    // 将List<Menu>对象转换成List<MenuVo>对象
    private List<MenuVo> buildMenus(List<Menu> menus) {

        List<MenuVo> menuVoList = new LinkedList<MenuVo>();
        for (Menu menu : menus) {
            MenuVo menuVo = new MenuVo();
            menuVo.setTitle(menu.getTitle());
            menuVo.setName(menu.getComponent());
            List<Menu> children = menu.getChildren();
            if (!CollectionUtils.isEmpty(children)) {
                menuVo.setChildren(buildMenus(children));
            }
            menuVoList.add(menuVo);
        }
        return menuVoList;
    }
}
