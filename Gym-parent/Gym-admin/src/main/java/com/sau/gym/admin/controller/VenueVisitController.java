package com.sau.gym.admin.controller;

import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.cache.VenueVisitCache;
import com.sau.gym.admin.service.VenueVisitService;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.vo.venue.VenueVisitVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

        // 1. 从缓存拿 全部数据（每天只查一次库）
        List<VenueVisitVO> allList = VenueVisitCache.ALL_VENUE_VISIT_LIST;

        //核心：缓存为空时，只查一次全量，初始化缓存
        if (allList == null || allList.isEmpty()) {
            allList = venueVisitService.findAll();
            VenueVisitCache.ALL_VENUE_VISIT_LIST = allList;
        }

        // 2. 手动分页（工具类自动计算）
        PageInfo<VenueVisitVO> pageInfo = manualPage(allList, current, limit);

        return Result.build(pageInfo, ResultCodeEnum.SUCCESS);
    }

    // ===================== 手动分页工具 =====================
    private PageInfo<VenueVisitVO> manualPage(List<VenueVisitVO> allList, int current, int limit) {
        if (allList == null || allList.isEmpty()) {
            return new PageInfo<>();
        }

        int total = allList.size();
        int start = (current - 1) * limit;
        int end = Math.min(start + limit, total);

        List<VenueVisitVO> pageList = allList.subList(start, end);

        PageInfo<VenueVisitVO> pageInfo = new PageInfo<>();
        pageInfo.setList(pageList);
        pageInfo.setPageNum(current);
        pageInfo.setPageSize(limit);
        pageInfo.setTotal(total);
        pageInfo.setPages((total + limit - 1) / limit);

        return pageInfo;
    }
}
