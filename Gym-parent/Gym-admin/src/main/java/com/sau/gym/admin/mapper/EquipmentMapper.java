package com.sau.gym.admin.mapper;

import com.sau.gym.model.dto.equipment.EquipmentDto;
import com.sau.gym.model.entity.equipment.Equipment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EquipmentMapper {

    //器械查询列表
    List<Equipment> findByPage(EquipmentDto equipmentDto);

    //添加器械
    void saveEquipment(Equipment equipment);

    //修改器械
    void updateEquipment(Equipment equipment);

    //删除器械
    void deleteById(Long equipmentId);
}
