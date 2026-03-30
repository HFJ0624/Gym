package com.sau.gym.admin.controller;

import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.service.NoticeCommentService;
import com.sau.gym.common.log.annotation.Log;
import com.sau.gym.common.log.enums.OperatorType;
import com.sau.gym.model.dto.notice.NoticeCommentDto;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.notice.NoticeComment;
import com.sau.gym.model.vo.notice.NoticeCommentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 作者:hfj
 * 功能:体育场馆公告评论的基本功能
 * 日期: 2026/3/26 18:31
 */
@RestController
@RequestMapping(value = "/admin/bulletin/noticeComment")
public class NoticeCommentController {

    @Autowired
    private NoticeCommentService noticeCommentService;

    //体育场馆公告评论查询方法
    //current:当前页 limit:每页显示的数量
    @PostMapping(value = "/findByPage/{current}/{limit}")
    public Result<PageInfo<NoticeCommentVO>> findByPage(@PathVariable(value = "current") Integer current, @PathVariable(value = "limit") Integer limit, @RequestBody NoticeCommentDto noticeCommentDto){
        PageInfo<NoticeCommentVO> pageInfo = noticeCommentService.findByPage(current,limit,noticeCommentDto);
        return Result.build(pageInfo, ResultCodeEnum.SUCCESS);
    }

    //添加公告评论
    @Log(title = "添加公告评论",businessType = 1,operatorType = OperatorType.MANAGE)
    @PostMapping(value = "/saveNoticeComment")
    public Result saveNoticeComment(@RequestBody NoticeComment noticeComment){
        noticeCommentService.saveNoticeComment(noticeComment);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }

    //修改公告评论
    @Log(title = "修改公告评论",businessType = 2,operatorType = OperatorType.MANAGE)
    @PutMapping(value = "/updateNoticeComment")
    public Result updateNoticeComment(@RequestBody NoticeComment noticeComment){
        noticeCommentService.updateNoticeComment(noticeComment);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }

    //删除公告评论
    @Log(title = "删除公告评论",businessType = 3,operatorType = OperatorType.MANAGE)
    @DeleteMapping(value = "/deleteById/{id}")
    public Result deleteById(@PathVariable(value = "id") Long id){
        noticeCommentService.deleteById(id);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }

    //获取最新的五条公告评论
    @GetMapping(value = "/getRecentComment")
    public Result<Map<String,Object>> getRecentComment(){
        Map<String, Object> resultMap = noticeCommentService.getRecentComment();
        return Result.build(resultMap,ResultCodeEnum.SUCCESS);
    }
}
