package com.sau.gym.admin.mapper;

import com.sau.gym.model.dto.menu.AssignMenuDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleMenuMapper {

    //查询当前角色的菜单数据
    List<Long> findRoleMenuByRoleId(Long roleId);

    //根据角色的id删除其所对应的菜单数据
    void deleteByRoleId(Long roleId);

    //获取菜单的id
    void doAssign(AssignMenuDto assignMenuDto);

    //将该id的菜单设置为半开
    void updateSysRoleMenuIsHalf(Long id);
}
