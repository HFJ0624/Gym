package com.sau.gym.admin.controller;

import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.service.CourtBookingService;
import com.sau.gym.admin.service.CourtService;
import com.sau.gym.admin.service.VenueCommentService;
import com.sau.gym.common.log.annotation.Log;
import com.sau.gym.common.log.enums.OperatorType;
import com.sau.gym.model.dto.venue.BookingDto;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.venue.Venue;
import com.sau.gym.model.entity.venue.VenueComment;
import com.sau.gym.model.vo.venue.VenueCommentVO;
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

    @Autowired
    private VenueCommentService venueCommentService;

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

    //场馆评论查询方法(前台)
    //current:当前页 limit:每页显示的数量
    @PostMapping(value = "/findByPageComment/{venueId}/{current}/{limit}")
    public Result<PageInfo<VenueCommentVO>> findByPageComment(@PathVariable(value = "venueId") Integer venueId, @PathVariable(value = "current") Integer current, @PathVariable(value = "limit") Integer limit){
        PageInfo<VenueCommentVO> pageInfo = venueCommentService.findByPageComment(current,limit,venueId);
        return Result.build(pageInfo, ResultCodeEnum.SUCCESS);
    }

    //添加体育场馆评论(前台)
    @Log(title = "添加体育场馆评论(前台)",businessType = 1,operatorType = OperatorType.MANAGE)
    @PostMapping(value = "/saveVenueComment")
    public Result saveVenueComment(@RequestBody VenueComment venueComment){
        venueCommentService.saveVenueComment(venueComment);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }
}
