package com.sau.gym.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.enums.NotificationReadStatusEnum;
import com.sau.gym.admin.mapper.NotificationMapper;
import com.sau.gym.admin.service.NotificationService;
import com.sau.gym.common.exception.SauException;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.notice.Notification;
import com.sau.gym.model.entity.user.User;
import com.sau.gym.model.vo.notice.NotificationVO;
import com.sau.gym.utils.AuthContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/29 16:11
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    public void createNotification(Notification notification) {
        if (notification == null) {
            return;
        }

        if (notification.getUserId() == null) {
            return;
        }

        if (notification.getReadStatus() == null) {
            notification.setReadStatus(NotificationReadStatusEnum.UNREAD.getCode());
        }
        Date date = new Date();
        notification.setCreateTime(date);

        notificationMapper.insert(notification);
    }

    @Override
    public PageInfo<NotificationVO> getMyNotifications(Integer pageNum, Integer pageSize, Integer readStatus) {
        User user = AuthContextUtil.get();

        if (user == null) {
            throw new SauException(ResultCodeEnum.LOGIN_AUTH);
        }

        PageHelper.startPage(pageNum, pageSize);

        //获取通知消息列表
        List<NotificationVO> list = notificationMapper.selectUserNotificationList(user.getId(), readStatus);
        return new PageInfo<>(list);
    }

    @Override
    public Integer getUnreadCount() {
        User user = AuthContextUtil.get();

        if (user == null) {
            throw new SauException(ResultCodeEnum.LOGIN_AUTH);
        }

        return notificationMapper.countUnread(user.getId());
    }

    @Override
    public void markRead(Long id) {
        User user = AuthContextUtil.get();

        if (user == null) {
            throw new SauException(ResultCodeEnum.LOGIN_AUTH);
        }
        Date date = new Date();
        notificationMapper.markRead(user.getId(), id,date);
    }

    @Override
    public void markAllRead() {
        User user = AuthContextUtil.get();

        if (user == null) {
            throw new SauException(ResultCodeEnum.LOGIN_AUTH);
        }

        Date date = new Date();
        notificationMapper.markAllRead(user.getId(),date);
    }

    @Override
    public void deleteNotification(Long id) {
        User user = AuthContextUtil.get();

        if (user == null) {
            throw new SauException(ResultCodeEnum.LOGIN_AUTH);
        }

        notificationMapper.deleteById(user.getId(), id);
    }
}
