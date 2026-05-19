package com.sau.gym.admin.task;

import com.sau.gym.admin.service.BeverageService;
import com.sau.gym.admin.utils.MailService;
import com.sau.gym.model.entity.shopping.Beverage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 作者:hfj
 * 功能:库存不足定时任务
 * 日期: 2026/5/3 20:00
 */
@Component
@Slf4j
public class StockCheckTask {

    @Resource
    private MailService mailService;

    @Resource
    private BeverageService beverageService;

    //每天凌晨0点执行排查库存
    @Scheduled(cron = "0 0 0 * * ?")
    public void sendOrderRemindMail() {
        log.info("=== 开始执行每日库存检查任务 ===");

        List<Beverage> beverageList = beverageService.findStock();

        if (beverageList == null){
            log.info("=== 未查到库存不足的商品 ===");
            return;
        }

        try {
            // 2.发送商品不足邮件
            StringBuilder content = new StringBuilder();
            content.append("【库存不足预警】\n");
            content.append("以下商品库存少于50，请及时补货：\n\n");
            for (Beverage beverage : beverageList) {
                content.append("商品ID：").append(beverage.getId()).append("\n");
                content.append("商品名称：").append(beverage.getGoodsName()).append("\n");
                content.append("当前库存：").append(beverage.getStock()).append("\n");
                content.append("------------------------\n");
            }

            String userEmail = "342586916@qq.com"; //固定邮箱先
            String subject = "商品库存不足";

            //发送邮件
            mailService.sendMail(userEmail,subject,content.toString());
            log.info("库存不足邮件提醒发送成功");

        }catch (Exception e){
            log.error("邮件发送失败：", e);
        }
    }
}
