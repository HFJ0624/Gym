package com.sau.gym.admin.controller.front;

import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.service.NotificationService;
import com.sau.gym.common.log.annotation.Log;
import com.sau.gym.common.log.enums.OperatorType;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.vo.notice.NotificationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 作者:hfj
 * 功能:系统通知消息基础功能
 * 日期: 2026/4/29 16:23
 */
@RestController
@RequestMapping("/front/notification")
public class FrontNotificationController {

    @Autowired
    private NotificationService notificationService;

    /***
     *
     * @param pageNum 分页数
     * @param pageSize 分页大小
     * @param readStatus 阅读状态
     * @return 获取自己的通知消息列表
     */
    @GetMapping("/my/{pageNum}/{pageSize}")
    public Result getMyNotifications(@PathVariable Integer pageNum,
                                     @PathVariable Integer pageSize,
                                     @RequestParam(required = false) Integer readStatus) {
        PageInfo<NotificationVO> pageInfo = notificationService.getMyNotifications(pageNum, pageSize, readStatus);
        return Result.build(pageInfo, ResultCodeEnum.SUCCESS);
    }

    /***
     *
     * @return 获得未读数量
     */
    @GetMapping("/unread/count")
    public Result getUnreadCount() {
        Integer count = notificationService.getUnreadCount();
        return Result.build(count, ResultCodeEnum.SUCCESS);
    }

    /***
     *
     * @param id 通知消息id
     * @return 标记单条已读
     */
    @Log(title = "标记单条已读",businessType = 0,operatorType = OperatorType.MOBILE)
    @PostMapping("/read/{id}")
    public Result markRead(@PathVariable Long id) {
        notificationService.markRead(id);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    /***
     *
     * @return 全部标记已读
     */
    @Log(title = "全部标记已读",businessType = 0,operatorType = OperatorType.MOBILE)
    @PostMapping("/read/all")
    public Result markAllRead() {
        notificationService.markAllRead();
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    /***
     *
     * @param id 通知消息id
     * @return 删除通知
     */
    @Log(title = "删除通知",businessType = 3,operatorType = OperatorType.MOBILE)
    @DeleteMapping("/{id}")
    public Result deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }
}
