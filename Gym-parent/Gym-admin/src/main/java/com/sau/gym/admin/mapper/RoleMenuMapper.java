package com.sau.gym.admin.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleMenuMapper {

    //查询当前角色的菜单数据
    List<Long> findRoleMenuByRoleId(Long roleId);
}
