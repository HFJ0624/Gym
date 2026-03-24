package com.sau.gym.admin.controller;

import com.sau.gym.admin.service.CourtBookingService;
import com.sau.gym.admin.service.CourtService;
import com.sau.gym.common.log.annotation.Log;
import com.sau.gym.common.log.enums.OperatorType;
import com.sau.gym.model.dto.venue.BookingDto;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.venue.Venue;
import com.sau.gym.utils.AuthContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private CourtBookingService courtBookingService;

    //查询场馆对应的场地
    @GetMapping(value = "/court/{venueId}")
    public Result<Map<String,Object>> getCourtById(@PathVariable(value = "venueId") Long venueId){
        Map<String, Object> resultMap = courtService.getAllCourt(venueId);
        return Result.build(resultMap, ResultCodeEnum.SUCCESS);
    }

    //查询一个场地的所有信息
    @GetMapping(value = "/details/{courtId}")
    public Result<Map<String,Object>> getCourt(@PathVariable(value = "courtId") Long courtId,@RequestParam(required = false) Long venueId){
        Map<String, Object> resultMap = courtService.getCourt(courtId,venueId);
        return Result.build(resultMap, ResultCodeEnum.SUCCESS);
    }

    //添加预约场地
    @Log(title = "预约场地",businessType = 1,operatorType = OperatorType.MANAGE)
    @PostMapping(value = "/book")
    public Result saveVenue(@RequestBody BookingDto bookingDto){
        courtBookingService.saveCourtBook(bookingDto);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }
}
