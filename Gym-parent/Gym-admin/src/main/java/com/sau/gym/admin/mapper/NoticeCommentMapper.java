package com.sau.gym.admin.mapper;

import com.sau.gym.model.dto.notice.NoticeCommentDto;
import com.sau.gym.model.entity.notice.NoticeComment;
import com.sau.gym.model.vo.notice.NoticeCommentVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NoticeCommentMapper {

    //体育场馆公告评论查询方法
    List<NoticeCommentVO> findByPage(NoticeCommentDto noticeCommentDto);

    //添加公告评论
    void saveNoticeComment(NoticeComment noticeComment);

    //修改公告评论
    void updateNoticeComment(NoticeComment noticeComment);

    //删除公告评论
    void deleteById(Long id);

    //删除对应公告评论
    void deleteAllComment(Long noticeId);

    //2.在查询该公告的所有评论
    List<NoticeCommentVO> getAllCommentById(Long noticeId);

    //查询最新的五条评论
    List<NoticeCommentVO> getRecentComment();
}
