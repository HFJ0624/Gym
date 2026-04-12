package com.sau.gym.admin.controller;

import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.service.BeverageService;
import com.sau.gym.model.dto.shopping.BeverageDto;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.shopping.Beverage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 作者:hfj
 * 功能:前台商城的基本功能
 * 日期: 2026/4/12 9:01
 */
@RestController
@RequestMapping(value = "/front/goods")
public class FrontGoodsController {

    @Autowired
    private BeverageService beverageService;

    //前台获取所有商品
    @GetMapping("/list")
    public Result<PageInfo<Beverage>> getGoodsList(@RequestParam Integer page, @RequestParam Integer limit, BeverageDto beverageDto) {
        PageInfo<Beverage> pageInfo = beverageService.findByPage(page, limit, beverageDto);
        return Result.build(pageInfo, ResultCodeEnum.SUCCESS);
    }

    //前台查询商品的详情信息
    @GetMapping(value = "/detail/{id}")
    public Result GetGoodsDetail(@PathVariable Long id){
        Beverage beverage = beverageService.selectById(id);
        return Result.build(beverage,ResultCodeEnum.SUCCESS);
    }
}
