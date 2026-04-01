package com.sau.gym.admin.task;

import com.sau.gym.admin.cache.OperaLogCache;
import com.sau.gym.admin.mapper.OperaLogMapper;
import com.sau.gym.model.entity.system.OperaLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 作者:hfj
 * 功能:日志定时任务
 * 日期: 2026/4/1 10:46
 */
@Component
@Slf4j
public class OperaLogTask {

    @Autowired
    private OperaLogMapper operaLogMapper;

    /**
     * 每天凌晨两点执行一次，减少服务器压力
     * 查询所有操作日志
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void refreshAllOperaLogCache() {
        log.info("【定时任务】凌晨2点 → 查询全部场馆访问记录");

        // 1. 查询所有操作日志
        List<OperaLog> allList = operaLogMapper.selectALL();
        // 2. 放入缓存(jvm栈里)
        OperaLogCache.ALL_OPERA_LOG_LIST = allList;

        log.info("【定时任务-测试】缓存更新完成，共 {} 条记录", allList.size());
    }
}
