package com.sau.gym.admin.controller;

import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.service.VenueCollectService;
import com.sau.gym.model.dto.venue.VenueCollectDto;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.vo.venue.VenueCollectVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 作者:hfj
 * 功能:主要功能统计场馆的点击量和收藏
 * 日期: 2026/3/31 9:30
 */
@RestController
@RequestMapping(value = "/admin/visit/collectVenue")
public class VenueCollectController {

    @Autowired
    private VenueCollectService venueCollectService;

    //场馆收藏分页查询
    //current:当前页 limit:每页显示的数量
    @PostMapping(value = "/findByPage/{current}/{limit}")
    public Result<PageInfo<VenueCollectVO>> findByPage(@PathVariable(value = "current") Integer current, @PathVariable(value = "limit") Integer limit, @RequestBody VenueCollectDto venueCollectDto){
        PageInfo<VenueCollectVO> pageInfo = venueCollectService.findByPage(current,limit,venueCollectDto);
        return Result.build(pageInfo, ResultCodeEnum.SUCCESS);
    }
}
