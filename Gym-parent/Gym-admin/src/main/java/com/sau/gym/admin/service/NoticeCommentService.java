package com.sau.gym.admin.service;

import com.github.pagehelper.PageInfo;
import com.sau.gym.model.dto.notice.NoticeCommentDto;
import com.sau.gym.model.entity.notice.NoticeComment;
import com.sau.gym.model.vo.notice.NoticeCommentVO;

import java.util.Map;

public interface NoticeCommentService {

    //体育场馆公告评论查询方法
    PageInfo<NoticeCommentVO> findByPage(Integer current, Integer limit, NoticeCommentDto noticeCommentDto);

    //添加公告评论
    void saveNoticeComment(NoticeComment noticeComment);

    //修改公告评论
    void updateNoticeComment(NoticeComment noticeComment);

    //删除公告评论
    void deleteById(Long id);

    //获取最新的五条公告评论
    Map<String, Object> getRecentComment();
}
