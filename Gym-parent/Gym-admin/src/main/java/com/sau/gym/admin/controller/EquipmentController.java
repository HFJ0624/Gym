package com.sau.gym.admin.controller;

import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.service.EquipmentService;
import com.sau.gym.common.log.annotation.Log;
import com.sau.gym.common.log.enums.OperatorType;
import com.sau.gym.model.dto.equipment.EquipmentDto;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.equipment.Equipment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 作者:hfj
 * 功能:器械的基本功能
 * 日期: 2026/3/11 16:33
 */
@RestController
@RequestMapping(value = "/admin/stadium/equipment")
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    //器械查询列表
    @GetMapping(value = "/findByPage/{pageNum}/{pageSize}")
    public Result<PageInfo<Equipment>> findByPage(@PathVariable(value = "pageNum") Integer pageNum , @PathVariable(value = "pageSize") Integer pageSize,@RequestBody EquipmentDto equipmentDto){
        PageInfo<Equipment> pageInfo = equipmentService.findByPage(pageNum,pageSize,equipmentDto);
        return Result.build(pageInfo, ResultCodeEnum.SUCCESS);
    }

    //添加器械
    @Log(title = "添加器械",businessType = 1,operatorType = OperatorType.MANAGE)
    @PostMapping(value = "/saveEquipment")
    public Result saveEquipment(@RequestBody Equipment equipment){
        equipmentService.saveEquipment(equipment);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }

    //修改器械
    @Log(title = "修改器械",businessType = 2,operatorType = OperatorType.MANAGE)
    @PutMapping(value = "/updateEquipment")
    public Result updateEquipment(@RequestBody Equipment equipment){
        equipmentService.updateEquipment(equipment);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }

    //删除器械
    @Log(title = "删除器械",businessType = 3,operatorType = OperatorType.MANAGE)
    @DeleteMapping(value = "/deleteById/{equipmentId}")
    public Result deleteById(@PathVariable(value = "equipmentId") Long equipmentId){
        equipmentService.deleteById(equipmentId);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }
}
