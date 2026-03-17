package com.sau.gym.admin.controller;

import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.service.VenueBookingService;
import com.sau.gym.common.log.annotation.Log;
import com.sau.gym.common.log.enums.OperatorType;
import com.sau.gym.model.dto.venue.VenueBookDto;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.vo.venue.VenueBookVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 作者:hfj
 * 功能:场馆预约的基本功能
 * 日期: 2026/3/17 9:24
 */
@RestController
@RequestMapping(value = "/admin/booking/venueBook")
public class VenueBookingController {

    @Autowired
    private VenueBookingService venueBookingService;

    //场馆预约的查询功能
    @PostMapping(value = "/findByPage/{current}/{limit}")
    public Result<PageInfo<VenueBookVO>> findByPage(@PathVariable(value = "current") Integer current, @PathVariable(value = "limit") Integer limit,@RequestBody VenueBookDto venueBookDto){
        PageInfo<VenueBookVO> pageInfo = venueBookingService.findByPage(current,limit,venueBookDto);
        return Result.build(pageInfo, ResultCodeEnum.SUCCESS);
    }

    //删除预约场馆
    @Log(title = "删除预约场馆",businessType = 3,operatorType = OperatorType.MANAGE)
    @DeleteMapping(value = "/deleteById/{id}")
    public Result deleteById(@PathVariable(value = "id") Long id){
        venueBookingService.deleteById(id);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }
}
