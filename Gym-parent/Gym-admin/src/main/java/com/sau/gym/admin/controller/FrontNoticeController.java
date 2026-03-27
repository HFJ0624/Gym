package com.sau.gym.admin.controller;

import com.sau.gym.admin.service.NoticeCommentService;
import com.sau.gym.admin.service.NoticeService;
import com.sau.gym.model.dto.notice.CommentDto;
import com.sau.gym.model.dto.notice.NoticeCommentDto;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.notice.NoticeComment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/27 14:11
 */
@RestController
@RequestMapping(value = "/front/notice")
public class FrontNoticeController {

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private NoticeCommentService noticeCommentService;

    //查询一个公告对应的所有评论和信息
    @GetMapping(value = "/{noticeId}")
    public Result<Map<String,Object>> getNoticeById(@PathVariable(value = "noticeId") Long noticeId){
        Map<String, Object> resultMap = noticeService.getNoticeById(noticeId);
        return Result.build(resultMap, ResultCodeEnum.SUCCESS);
    }

    @PostMapping(value = "/comment")
    public Result<Map<String,Object>> submitComment(@RequestBody NoticeComment noticeComment){
        noticeCommentService.saveNoticeComment(noticeComment);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }
}
