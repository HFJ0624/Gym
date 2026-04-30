package com.sau.gym.admin.listener;


import com.sau.gym.admin.enums.NotificationReadStatusEnum;
import com.sau.gym.admin.service.NotificationService;
import com.sau.gym.model.entity.event.NotificationEvent;
import com.sau.gym.model.entity.notice.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 作者:hfj
 * 功能:系统通知监听类
 * 日期: 2026/4/29 16:40
 */
@Component
public class NotificationEventListener {

    @Autowired
    private NotificationService notificationService;

    //只有业务事务提交成功后才发通知
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNotificationEvent(NotificationEvent event) {
        if (event == null || event.getUserId() == null) {
            return;
        }

        Notification notification = new Notification();

        notification.setUserId(event.getUserId());
        notification.setTitle(event.getTitle());
        notification.setContent(event.getContent());
        notification.setType(event.getType());
        notification.setBusinessId(event.getBusinessId());
        notification.setBusinessNo(event.getBusinessNo());
        notification.setBusinessType(event.getBusinessType());
        notification.setReadStatus(NotificationReadStatusEnum.UNREAD.getCode());

        notificationService.createNotification(notification);
    }
}
