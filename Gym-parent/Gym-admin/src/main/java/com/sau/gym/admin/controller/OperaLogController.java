package com.sau.gym.admin.controller;

import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.service.OperaLogService;
import com.sau.gym.model.dto.system.OperaLogDto;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.system.OperaLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        PageInfo<OperaLog> pageInfo = operaLogService.findByPage(current,limit,operaLogDto);
        return Result.build(pageInfo, ResultCodeEnum.SUCCESS);
    }

    //删除操作日志记录
    @DeleteMapping(value = "/deleteById/{id}")
    public Result deleteById(@PathVariable(value = "id") Long id){
        operaLogService.deleteById(id);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }

}
