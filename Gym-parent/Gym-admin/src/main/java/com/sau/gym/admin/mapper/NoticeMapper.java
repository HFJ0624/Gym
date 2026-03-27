package com.sau.gym.admin.mapper;

import com.sau.gym.model.dto.notice.NoticeDto;
import com.sau.gym.model.entity.notice.Notice;
import com.sau.gym.model.vo.notice.NoticeVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NoticeMapper {

    //体育场馆公告查询方法
    List<NoticeVO> findByPage(NoticeDto noticeDto);

    //添加公告
    void saveNotice(Notice notice);

    //修改公告
    void updateNotice(Notice notice);

    //删除公告
    void deleteById(Long noticeId);

    //查找所有公告标题
    List<Notice> findAllNotice();
}
