package com.sau.gym.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.mapper.EquipmentMapper;
import com.sau.gym.admin.service.EquipmentService;
import com.sau.gym.model.dto.equipment.EquipmentDto;
import com.sau.gym.model.entity.equipment.Equipment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/11 19:27
 */
@Service
public class EquipmentServiceImpl implements EquipmentService {

    @Autowired
    private EquipmentMapper equipmentMapper;

    //器械查询列表
    @Override
    public PageInfo<Equipment> findByPage(Integer pageNum, Integer pageSize, EquipmentDto equipmentDto) {

        PageHelper.startPage(pageNum,pageSize);

        List<Equipment> list = equipmentMapper.findByPage(equipmentDto);

        PageInfo<Equipment> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    //添加器械
    @Override
    public void saveEquipment(Equipment equipment) {
        equipmentMapper.saveEquipment(equipment);
    }

    //修改器械
    @Override
    public void updateEquipment(Equipment equipment) {
        equipmentMapper.updateEquipment(equipment);
    }

    //删除器械
    @Override
    public void deleteById(Long equipmentId) {
        equipmentMapper.deleteById(equipmentId);
    }
}
