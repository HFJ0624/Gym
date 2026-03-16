package com.sau.gym.admin.controller;

import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.service.CourtBookingService;
import com.sau.gym.model.dto.venue.CourtBookDto;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.vo.court.CourtBookVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 作者:hfj
 * 功能:场地预约的基本功能
 * 日期: 2026/3/16 18:14
 */
@RestController
@RequestMapping(value = "/admin/booking/courtBook")
public class CourtBookingController {

    @Autowired
    private CourtBookingService courtBookingService;

    //场地预约的查询功能
    @PostMapping(value = "/findByPage/{current}/{limit}")
    public Result<PageInfo<CourtBookVO>> findByPage(@PathVariable(value = "current") Integer current, @PathVariable(value = "limit") Integer limit, @RequestBody CourtBookDto courtBookDto){
        PageInfo<CourtBookVO> pageInfo = courtBookingService.findByPage(current,limit,courtBookDto);
        return Result.build(pageInfo, ResultCodeEnum.SUCCESS);
    }
}
