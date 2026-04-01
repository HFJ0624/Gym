package com.sau.gym.admin.cache;

import com.sau.gym.model.vo.venue.VenueVisitVO;

import java.util.List;

/**
 * 作者:hfj
 * 功能:场馆访问记录 全量缓存 每天凌晨2点更新一次
 * 日期: 2026/4/1 11:19
 */
public class VenueVisitCache {

    // 缓存：所有访问量的数据
    public static List<VenueVisitVO> ALL_VENUE_VISIT_LIST;
}
