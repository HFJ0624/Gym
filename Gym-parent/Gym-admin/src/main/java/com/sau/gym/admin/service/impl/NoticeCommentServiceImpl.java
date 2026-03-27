package com.sau.gym.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.mapper.NoticeCommentMapper;
import com.sau.gym.admin.service.NoticeCommentService;
import com.sau.gym.model.dto.notice.NoticeCommentDto;
import com.sau.gym.model.entity.notice.NoticeComment;
import com.sau.gym.model.vo.notice.NoticeCommentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/26 18:32
 */
@Service
public class NoticeCommentServiceImpl implements NoticeCommentService {

    @Autowired
    private NoticeCommentMapper noticeCommentMapper;

    //体育场馆公告评论查询方法
    @Override
    public PageInfo<NoticeCommentVO> findByPage(Integer current, Integer limit, NoticeCommentDto noticeCommentDto) {
        PageHelper.startPage(current,limit);

        List<NoticeCommentVO> list = noticeCommentMapper.findByPage(noticeCommentDto);

        PageInfo<NoticeCommentVO> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    //添加公告评论
    @Override
    public void saveNoticeComment(NoticeComment noticeComment) {
        //添加完公告评论,公告的评论数量要加一(已在SQL触发器实现,notice_comment表,触发器:trg_after_insert_notice_comment)
        noticeComment.setStatus(1);
        noticeCommentMapper.saveNoticeComment(noticeComment);
    }

    //修改公告评论
    @Override
    public void updateNoticeComment(NoticeComment noticeComment) {
        noticeCommentMapper.updateNoticeComment(noticeComment);
    }

    //删除公告评论
    @Override
    public void deleteById(Long id) {
        //删除完公告评论,公告的评论数量要减一(已在SQL触发器实现,notice_comment表,触发器:trg_after_update_delete_comment)
        noticeCommentMapper.deleteById(id);
    }
}
