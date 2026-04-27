package com.sau.gym.admin.task;

import com.sau.gym.admin.mapper.CourtBookingMapper;
import com.sau.gym.admin.utils.MailService;
import com.sau.gym.model.entity.venue.CourtBooking;
import com.sau.gym.model.vo.court.CourtBookEmailVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/27 21:19
 */
@Component
@Slf4j
public class OrderRemindTask {

    @Resource
    private CourtBookingMapper courtBookingMapper;
    @Resource
    private MailService mailService;

    // 时间格式化（和你的数据库格式完全一致）
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Scheduled(cron = "0 0 */1 * * ?") //每小时执行一次,缓解数据库压力
    public void sendOrderRemindMail() {
        log.info("=== 开始执行预约邮件提醒任务 ===");

        // 当前时间（你也可以改成 plusHours(1) 提前1小时，自由控制）
        LocalDate now = LocalDate.now();
        // 格式化为字符串
        String targetTime = now.format(FORMATTER);

        // 1. 直接查询未来1小时内要开始的待提醒订单
        List<CourtBookEmailVO> orderList = courtBookingMapper.selectRemindOrders(targetTime);

        if (orderList.isEmpty()) {
            log.info("暂无需要提醒的订单");
            return;
        }

        // 2. 遍历订单发送邮件
        for (CourtBookEmailVO order : orderList) {
            try {
                String userEmail = order.getEmail();
                String username = order.getUsername();
                String courtName = order.getCourtName();

                // 邮件内容
                String subject = "【场地预约提醒】您的预约即将开始";
                String content = "尊敬的" + username + "用户：\n" +
                        "您的场地预约即将开始！\n" +
                        "场地名称：" + courtName + "\n" +
                        "预约时间：" + order.getBookingDate() + " " + order.getStartTime() + "-" + order.getEndTime() + "\n" +
                        "订单编号：" + order.getOrderNo() + "\n" +
                        "请您准时前往，感谢您的使用！";

                // 发送邮件
                mailService.sendMail(userEmail, subject, content);
                log.info("订单{} 邮件提醒发送成功", order.getId());

                // 3. 标记为已提醒，防止重复发送
                courtBookingMapper.updateRemindedStatus(order.getId());

            } catch (Exception e) {
                log.error("订单{} 邮件发送失败：", order.getId(), e);
            }
        }
    }
}