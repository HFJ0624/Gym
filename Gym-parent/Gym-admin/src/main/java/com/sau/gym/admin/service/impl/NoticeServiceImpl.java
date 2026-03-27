package com.sau.gym.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.mapper.NoticeCommentMapper;
import com.sau.gym.admin.mapper.NoticeMapper;
import com.sau.gym.admin.service.NoticeService;
import com.sau.gym.model.dto.notice.NoticeDto;
import com.sau.gym.model.entity.notice.Notice;
import com.sau.gym.model.entity.venue.Venue;
import com.sau.gym.model.vo.notice.NoticeCommentVO;
import com.sau.gym.model.vo.notice.NoticeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/24 17:29
 */
@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private NoticeCommentMapper noticeCommentMapper;

    //体育场馆公告查询方法
    @Override
    public PageInfo<NoticeVO> findByPage(Integer current, Integer limit, NoticeDto noticeDto) {

        PageHelper.startPage(current,limit);

        List<NoticeVO> list = noticeMapper.findByPage(noticeDto);

        PageInfo<NoticeVO> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    //添加公告
    @Override
    public void saveNotice(Notice notice) {
        noticeMapper.saveNotice(notice);
    }

    //修改公告
    @Override
    public void updateNotice(Notice notice) {
        noticeMapper.updateNotice(notice);
    }

    //删除公告
    @Override
    public void deleteById(Long noticeId) {
        //删除公告后,也需要把对公告的评论都删除
        noticeMapper.deleteById(noticeId);

        //删除对应公告评论
        noticeCommentMapper.deleteAllComment(noticeId);
    }

    //查找所有公告标题
    @Override
    public Map<String, Object> findAllNotice() {

        //查找所有公告标题
        List<Notice> allNotice = noticeMapper.findAllNotice();

        //构建返回对象
        HashMap<String, Object> resultMap = new HashMap<>();

        resultMap.put("allNotice",allNotice);

        return resultMap;
    }

    //查询所有发表公告
    @Override
    public Map<String, Object> getNotices() {

        //查找所有公告(前台)
        List<Notice> allNotices = noticeMapper.getNotices();

        //构建返回对象
        HashMap<String, Object> resultMap = new HashMap<>();

        resultMap.put("allNotices",allNotices);

        return resultMap;
    }

    //查询一个公告对应的所有评论和信息
    @Override
    public Map<String, Object> getNoticeById(Long noticeId) {
        //1.先查询这个公告的详情信息
        Notice notice = noticeMapper.getNoticeById(noticeId);

        //2.在查询该公告的所有评论
        List<NoticeCommentVO> allComments = noticeCommentMapper.getAllCommentById(noticeId);

        //3.构建返回对象
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("allComments",allComments);
        resultMap.put("notice",notice);
        return resultMap;
    }
}
