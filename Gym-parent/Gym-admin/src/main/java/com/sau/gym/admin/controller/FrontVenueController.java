package com.sau.gym.admin.controller;

import com.sau.gym.admin.service.CourtService;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 作者:hfj
 * 功能:前台系统返回对应的场地信息
 * 日期: 2026/3/23 19:41
 */
@RestController
@RequestMapping(value = "/front/venues")
public class FrontVenueController {

    @Autowired
    private CourtService courtService;

    //查询场馆对应的场地
    @GetMapping(value = "/court/{venueId}")
    public Result<Map<String,Object>> getCourtById(@PathVariable(value = "venueId") Long venueId){
        Map<String, Object> resultMap = courtService.getAllCourt(venueId);
        return Result.build(resultMap, ResultCodeEnum.SUCCESS);
    }
}
