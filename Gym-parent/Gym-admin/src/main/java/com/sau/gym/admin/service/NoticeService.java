package com.sau.gym.admin.service;

import com.github.pagehelper.PageInfo;
import com.sau.gym.model.dto.notice.NoticeDto;
import com.sau.gym.model.entity.notice.Notice;
import com.sau.gym.model.vo.notice.NoticeVO;

import java.util.Map;

public interface NoticeService {

    //体育场馆公告查询方法
    PageInfo<NoticeVO> findByPage(Integer current, Integer limit, NoticeDto noticeDto);

    //添加公告
    void saveNotice(Notice notice);

    //修改公告
    void updateNotice(Notice notice);

    //删除公告
    void deleteById(Long noticeId);

    //查找所有公告标题
    Map<String, Object> findAllNotice();

    //查询所有发表公告
    Map<String, Object> getNotices();
}
