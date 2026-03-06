package com.sau.gym.admin.mapper;

import com.sau.gym.model.entity.menu.Menu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MenuMapper {

    //找到所有节点
    List<Menu> selectAll();

    //保存菜单
    void save(Menu menu);

    //修改菜单
    void updateById(Menu menu);

    //先查询是否存在子菜单，如果存在不允许进行删除
    int countByParentId(Long id);

    //不存在子菜单直接删除
    void deleteById(Long id);

    //根据用户id查询该用户有的菜单
    List<Menu> selectListByUserId(Long userId);

    //查询是否存在父节点
    Menu selectById(Long parentId);
}
