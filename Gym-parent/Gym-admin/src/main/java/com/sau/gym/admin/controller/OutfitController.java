package com.sau.gym.admin.controller;

import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.service.OutfitService;
import com.sau.gym.common.log.annotation.Log;
import com.sau.gym.common.log.enums.OperatorType;
import com.sau.gym.model.dto.shopping.OutfitDto;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.shopping.Outfit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 作者:hfj
 * 功能:器材管理基本功能
 * 日期: 2026/4/10 18:12
 */
@RestController
@RequestMapping(value = "/admin/shopping/outfit")
public class OutfitController {

    @Autowired
    private OutfitService outfitService;

    //器材查询方法
    @PostMapping(value = "/findByPage/{current}/{limit}")
    public Result<PageInfo<Outfit>> findByPage(@PathVariable(value = "current") Integer current, @PathVariable(value = "limit") Integer limit, @RequestBody OutfitDto outfitDto){
        PageInfo<Outfit> pageInfo = outfitService.findByPage(current,limit,outfitDto);
        return Result.build(pageInfo, ResultCodeEnum.SUCCESS);
    }

    //添加器材
    @Log(title = "添加器材",businessType = 1,operatorType = OperatorType.MANAGE)
    @PostMapping(value = "/saveOutfit")
    public Result saveOutfit(@RequestBody Outfit outfit){
        outfitService.saveOutfit(outfit);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }

    //修改器材
    @Log(title = "修改器材",businessType = 2,operatorType = OperatorType.MANAGE)
    @PutMapping(value = "/updateOutfit")
    public Result updateOutfit(@RequestBody Outfit outfit){
        outfitService.updateOutfit(outfit);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }

    //删除器材
    @Log(title = "删除器材",businessType = 3,operatorType = OperatorType.MANAGE)
    @DeleteMapping(value = "/deleteById/{id}")
    public Result deleteById(@PathVariable(value = "id") Long id){
        outfitService.deleteById(id);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }
}
