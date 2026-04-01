package com.sau.gym.admin.task;

import com.sau.gym.admin.cache.VenueVisitCache;
import com.sau.gym.admin.mapper.VenueVisitMapper;
import com.sau.gym.model.vo.venue.VenueVisitVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/1 11:17
 */
@Component
@Slf4j
public class VenueVisitTask {

    @Autowired
    private VenueVisitMapper venueVisitMapper;

    /**
     * 每天3小时执行一次,可以给服务器减少压力
     * 查询所有场馆访问记录
     */
    @Scheduled(cron = "0 0 */3 * * ?")
    public void refreshAllVenueVisitCache() {
        log.info("【定时任务】凌晨2点 → 查询全部场馆访问记录");

        // 1. 查询所有数据
        List<VenueVisitVO> allList = venueVisitMapper.selectALL();
        // 2. 放入缓存(jvm栈里)
        VenueVisitCache.ALL_VENUE_VISIT_LIST = allList;

        log.info("【定时任务-测试】缓存更新完成，共 {} 条记录", allList.size());
    }
}
