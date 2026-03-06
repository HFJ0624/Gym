package com.sau.gym.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.mapper.RoleMapper;
import com.sau.gym.admin.mapper.RoleUserMapper;
import com.sau.gym.admin.service.RoleService;
import com.sau.gym.model.dto.role.RoleDto;
import com.sau.gym.model.entity.role.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/5 10:25
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleUserMapper roleUserMapper;

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

    //角色删除
    @Override
    public void deleteById(Long roleId) {
        roleMapper.deleteById(roleId);
    }


    //查询所有角色
    @Override
    public Map<String, Object> findAllRoles(Long userId) {

        //1.查询所有的角色数据
        List<Role> roleList = roleMapper.findAllRoles();

        //2.查询当前登录用户的角色数据
        List<Long> roles = roleUserMapper.findUserRoleByUserId(userId);

        //3.构建响应结果数据
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("allRolesList",roleList);
        resultMap.put("sysUserRoles", roles);
        return resultMap;
    }
}
