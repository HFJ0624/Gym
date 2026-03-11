package com.sau.gym.admin.service;

import com.github.pagehelper.PageInfo;
import com.sau.gym.model.dto.equipment.EquipmentDto;
import com.sau.gym.model.entity.equipment.Equipment;

public interface EquipmentService {

    //器械查询列表
    PageInfo<Equipment> findByPage(Integer pageNum, Integer pageSize, EquipmentDto equipmentDto);

    //添加器械
    void saveEquipment(Equipment equipment);

    //修改器械
    void updateEquipment(Equipment equipment);

    //删除器械
    void deleteById(Long equipmentId);
}
