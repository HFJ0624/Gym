package com.sau.gym.admin.service;

import com.github.pagehelper.PageInfo;
import com.sau.gym.model.entity.notice.Notification;
import com.sau.gym.model.vo.notice.NotificationVO;

public interface NotificationService {

    void createNotification(Notification notification);

    PageInfo<NotificationVO> getMyNotifications(Integer pageNum, Integer pageSize, Integer readStatus);

    Integer getUnreadCount();

    void markRead(Long id);

    void markAllRead();

    void deleteNotification(Long id);
}
