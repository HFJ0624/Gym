package com.sau.gym.admin.service;

import com.github.pagehelper.PageInfo;
import com.sau.gym.model.dto.role.RoleDto;
import com.sau.gym.model.entity.role.Role;

import java.util.Map;

public interface RoleService {

    //分页查询所有角色
    PageInfo<Role> findByPage(RoleDto roleDto, Integer pageNum, Integer pageSize);

    //角色添加
    void saveRole(Role role);

    //角色修改
    void updateRole(Role role);

    //角色删除
    void deleteById(Long roleId);

    //查询所有角色
    Map<String, Object> findAllRoles(Long userId);
}
