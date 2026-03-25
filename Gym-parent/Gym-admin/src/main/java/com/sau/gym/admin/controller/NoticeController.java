package com.sau.gym.admin.controller;

import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.service.NoticeService;
import com.sau.gym.common.log.annotation.Log;
import com.sau.gym.common.log.enums.OperatorType;
import com.sau.gym.model.dto.notice.NoticeDto;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.notice.Notice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 作者:hfj
 * 功能:体育场馆公告的基本功能
 * 日期: 2026/3/24 17:23
 */
@RestController
@RequestMapping(value = "/admin/bulletin/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    //体育场馆公告查询方法
    //current:当前页 limit:每页显示的数量
    @PostMapping(value = "/findByPage/{current}/{limit}")
    public Result<PageInfo<Notice>> findByPage(@PathVariable(value = "current") Integer current, @PathVariable(value = "limit") Integer limit, @RequestBody NoticeDto noticeDto){
        PageInfo<Notice> pageInfo = noticeService.findByPage(current,limit,noticeDto);
        return Result.build(pageInfo, ResultCodeEnum.SUCCESS);
    }

    //添加公告
    @Log(title = "添加公告",businessType = 1,operatorType = OperatorType.MANAGE)
    @PostMapping(value = "/saveNotice")
    public Result saveNotice(@RequestBody Notice notice){
        noticeService.saveNotice(notice);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }

    //修改公告
    @Log(title = "修改公告",businessType = 2,operatorType = OperatorType.MANAGE)
    @PutMapping(value = "/updateNotice")
    public Result updateNotice(@RequestBody Notice notice){
        noticeService.updateNotice(notice);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }

    //删除公告
    @Log(title = "删除公告",businessType = 3,operatorType = OperatorType.MANAGE)
    @DeleteMapping(value = "/deleteById/{noticeId}")
    public Result deleteById(@PathVariable(value = "noticeId") Long noticeId){
        noticeService.deleteById(noticeId);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }
}
