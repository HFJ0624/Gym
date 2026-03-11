package com.sau.gym.admin.controller;

import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.service.CourtService;
import com.sau.gym.common.log.annotation.Log;
import com.sau.gym.common.log.enums.OperatorType;
import com.sau.gym.model.dto.venue.CourtDto;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.venue.Court;
import com.sau.gym.model.vo.court.CourtVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 作者:hfj
 * 功能:场地的增删改查
 * 日期: 2026/3/10 9:23
 */
@RestController
@RequestMapping(value = "/admin/stadium/court")
public class CourtController {

    @Autowired
    private CourtService courtService;

    //场地查询方法
    @PostMapping(value = "/findByPage/{current}/{limit}")
    public Result<PageInfo<CourtVO>> findByPage(@PathVariable(value = "current") Integer current, @PathVariable(value = "limit") Integer limit, @RequestBody CourtDto courtDto){
        PageInfo<CourtVO> pageInfo = courtService.findByPage(current,limit,courtDto);
        return Result.build(pageInfo, ResultCodeEnum.SUCCESS);
    }

    //添加场地
    @Log(title = "添加场地",businessType = 1,operatorType = OperatorType.MANAGE)
    @PostMapping(value = "/saveCourt")
    public Result saveCourt(@RequestBody Court court){
        courtService.saveCourt(court);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }

    //修改场地
    @Log(title = "修改场地",businessType = 2,operatorType = OperatorType.MANAGE)
    @PutMapping(value = "/updateCourt")
    public Result updateCourt(@RequestBody Court court){
        courtService.updateCourt(court);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }

    //删除场地
    @Log(title = "删除场地",businessType = 3,operatorType = OperatorType.MANAGE)
    @DeleteMapping(value = "/deleteById/{courtId}")
    public Result deleteById(@PathVariable(value = "courtId") Long courtId){
        courtService.deleteById(courtId);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }

}
