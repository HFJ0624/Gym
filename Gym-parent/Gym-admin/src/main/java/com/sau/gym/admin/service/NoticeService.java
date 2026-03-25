package com.sau.gym.admin.service;

import com.github.pagehelper.PageInfo;
import com.sau.gym.model.dto.notice.NoticeDto;
import com.sau.gym.model.entity.notice.Notice;

public interface NoticeService {

    //体育场馆公告查询方法
    PageInfo<Notice> findByPage(Integer current, Integer limit, NoticeDto noticeDto);

    //添加公告
    void saveNotice(Notice notice);

    //修改公告
    void updateNotice(Notice notice);

    //删除公告
    void deleteById(Long noticeId);
}
