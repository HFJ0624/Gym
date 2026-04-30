package com.sau.gym.admin.mapper;

import com.sau.gym.model.entity.notice.Notification;
import com.sau.gym.model.vo.notice.NotificationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface NotificationMapper {

    //插入通知消息
    int insert(Notification notification);

    //查询所有通知消息
    List<NotificationVO> selectUserNotificationList(@Param("userId") Long userId, @Param("readStatus") Integer readStatus);

    //统计未读通知消息
    int countUnread(@Param("userId") Long userId);

    int markRead(@Param("userId") Long userId, @Param("id") Long id,@Param("readTime") Date readTime);

    int markAllRead(@Param("userId") Long userId,@Param("readTime")Date readTime);

    int deleteById(@Param("userId") Long userId, @Param("id") Long id);
}
