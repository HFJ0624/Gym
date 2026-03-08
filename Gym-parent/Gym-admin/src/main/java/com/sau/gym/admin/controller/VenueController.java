package com.sau.gym.admin.controller;

import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.service.VenueService;
import com.sau.gym.model.dto.venue.VenueDto;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.venue.Venue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 作者:hfj
 * 功能:场馆
 * 日期: 2026/3/7 10:51
 */
@RestController
@RequestMapping(value = "/admin/stadium/venue")
public class VenueController {

    @Autowired
    private VenueService venueService;

    //场馆列表查询方法
    //current:当前页 limit:每页显示的数量 venueDto:条件角色名称对象
    @PostMapping(value = "/findByPage/{current}/{limit}")
    public Result<PageInfo<Venue>> findByPage(@PathVariable(value = "current") Integer current, @PathVariable(value = "limit") Integer limit,@RequestBody VenueDto venueDto){
        PageInfo<Venue> pageInfo = venueService.findByPage(current,limit,venueDto);
        return Result.build(pageInfo, ResultCodeEnum.SUCCESS);
    }
}
