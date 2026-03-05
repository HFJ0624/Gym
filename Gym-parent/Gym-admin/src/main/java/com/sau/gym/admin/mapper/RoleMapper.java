package com.sau.gym.admin.mapper;

import com.sau.gym.model.dto.role.RoleDto;
import com.sau.gym.model.entity.role.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleMapper {

    //分页查询所有角色
    List<Role> findByPage(RoleDto roleDto);

    //角色添加
    void saveRole(Role role);
}
