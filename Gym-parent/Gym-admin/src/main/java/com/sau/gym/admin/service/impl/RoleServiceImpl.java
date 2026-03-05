package com.sau.gym.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.mapper.RoleMapper;
import com.sau.gym.admin.service.RoleService;
import com.sau.gym.model.dto.role.RoleDto;
import com.sau.gym.model.entity.role.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/5 10:25
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    //分页查询所有角色
    @Override
    public PageInfo<Role> findByPage(RoleDto roleDto, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Role> roleList = roleMapper.findByPage(roleDto);
        PageInfo<Role> pageInfo = new PageInfo<>(roleList);
        return pageInfo;
    }

    //角色添加
    @Override
    public void saveRole(Role role) {
        roleMapper.saveRole(role);
    }

    //角色修改
    @Override
    public void updateRole(Role role) {
        roleMapper.updateRole(role);
    }
}
