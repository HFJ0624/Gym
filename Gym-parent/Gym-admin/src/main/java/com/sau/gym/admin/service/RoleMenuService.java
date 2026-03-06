package com.sau.gym.admin.service;

import java.util.Map;

public interface RoleMenuService {

    //根据角色的id查询出其对应的菜单id，并且需要将系统中所有的菜单数据查询出来
    Map<String, Object> findRoleMenuByRoleId(Long roleId);
}
