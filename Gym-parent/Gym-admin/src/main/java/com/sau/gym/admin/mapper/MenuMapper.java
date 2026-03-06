package com.sau.gym.admin.mapper;

import com.sau.gym.model.entity.menu.Menu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MenuMapper {

    //找到所有节点
    List<Menu> selectAll();
}
