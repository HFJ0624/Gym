package com.sau.gym.admin.controller;

import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.service.VenueVisitService;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.vo.venue.VenueVisitVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 作者:hfj
 * 功能:场馆访问量查询
 * 日期: 2026/4/1 8:28
 */
@RestController
@RequestMapping(value = "/admin/visit/visitVenue")
public class VenueVisitController {

    @Autowired
    private VenueVisitService venueVisitService;

    //场馆访问量分页查询
    //current:当前页 limit:每页显示的数量
    @PostMapping(value = "/findByPage/{current}/{limit}")
    public Result<PageInfo<VenueVisitVO>> findByPage(@PathVariable(value = "current") Integer current, @PathVariable(value = "limit") Integer limit){
        PageInfo<VenueVisitVO> pageInfo = venueVisitService.findByPage(current,limit);
        return Result.build(pageInfo, ResultCodeEnum.SUCCESS);
    }
}
