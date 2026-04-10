package com.sau.gym.admin.controller;

import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.service.BeverageService;
import com.sau.gym.common.log.annotation.Log;
import com.sau.gym.common.log.enums.OperatorType;
import com.sau.gym.model.dto.shopping.BeverageDto;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.shopping.Beverage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 作者:hfj
 * 功能:零食饮品基本功能
 * 日期: 2026/4/10 10:56
 */
@RestController
@RequestMapping(value = "/admin/shopping/beverage")
public class BeverageController {

    @Autowired
    private BeverageService beverageService;

    //零食饮品查询方法
    @PostMapping(value = "/findByPage/{current}/{limit}")
    public Result<PageInfo<Beverage>> findByPage(@PathVariable(value = "current") Integer current, @PathVariable(value = "limit") Integer limit, @RequestBody BeverageDto beverageDto){
        PageInfo<Beverage> pageInfo = beverageService.findByPage(current,limit,beverageDto);
        return Result.build(pageInfo, ResultCodeEnum.SUCCESS);
    }

    //添加商品
    @Log(title = "添加零食饮料",businessType = 1,operatorType = OperatorType.MANAGE)
    @PostMapping(value = "/saveBeverage")
    public Result saveBeverage(@RequestBody Beverage beverage){
        beverageService.saveBeverage(beverage);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }

    //修改商品
    @Log(title = "修改零食饮料",businessType = 2,operatorType = OperatorType.MANAGE)
    @PutMapping(value = "/updateBeverage")
    public Result updateBeverage(@RequestBody Beverage beverage){
        beverageService.updateBeverage(beverage);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }

    //删除商品
    @Log(title = "删除零食饮料",businessType = 3,operatorType = OperatorType.MANAGE)
    @DeleteMapping(value = "/deleteById/{beverageId}")
    public Result deleteById(@PathVariable(value = "beverageId") Long beverageId){
        beverageService.deleteById(beverageId);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }
}
