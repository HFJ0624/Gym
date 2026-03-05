package com.sau.gym.admin.service;

import com.github.pagehelper.PageInfo;
import com.sau.gym.model.dto.role.RoleDto;
import com.sau.gym.model.entity.role.Role;

public interface RoleService {

    //分页查询所有角色
    PageInfo<Role> findByPage(RoleDto roleDto, Integer pageNum, Integer pageSize);
}
