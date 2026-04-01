package com.sau.gym.admin.controller;

import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.cache.OperaLogCache;
import com.sau.gym.admin.service.OperaLogService;
import com.sau.gym.model.dto.system.OperaLogDto;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.system.OperaLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 作者:hfj
 * 功能:展示日志记录功能
 * 日期: 2026/3/10 19:39
 */
@RestController
@RequestMapping(value = "/admin/system/operaLog")
public class OperaLogController {

    @Autowired
    private OperaLogService operaLogService;

    //操作日志列表查询
    @PostMapping(value = "/findByPage/{current}/{limit}")
    public Result<PageInfo<OperaLog>> findByPage(@PathVariable(value = "current") Integer current, @PathVariable(value = "limit") Integer limit, @RequestBody OperaLogDto operaLogDto){
        // 1. 从缓存拿 全部数据（每天只查一次库）
        List<OperaLog> allList = OperaLogCache.ALL_OPERA_LOG_LIST;

        //核心：缓存为空时，只查一次全量，初始化缓存
        if (allList == null || allList.isEmpty()) {
            allList = operaLogService.findAll();
            OperaLogCache.ALL_OPERA_LOG_LIST = allList;
        }

        // 2. 手动分页（工具类自动计算）
        PageInfo<OperaLog> pageInfo = manualPage(allList, current, limit);

        return Result.build(pageInfo, ResultCodeEnum.SUCCESS);
    }

    //删除操作日志记录
    @DeleteMapping(value = "/deleteById/{id}")
    public Result deleteById(@PathVariable(value = "id") Long id){
        operaLogService.deleteById(id);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }

    // ===================== 手动分页工具 =====================
    private PageInfo<OperaLog> manualPage(List<OperaLog> allList, int current, int limit) {
        if (allList == null || allList.isEmpty()) {
            return new PageInfo<>();
        }

        int total = allList.size();
        int start = (current - 1) * limit;
        int end = Math.min(start + limit, total);

        List<OperaLog> pageList = allList.subList(start, end);

        PageInfo<OperaLog> pageInfo = new PageInfo<>();
        pageInfo.setList(pageList);
        pageInfo.setPageNum(current);
        pageInfo.setPageSize(limit);
        pageInfo.setTotal(total);
        pageInfo.setPages((total + limit - 1) / limit);

        return pageInfo;
    }

}
