package com.sau.gym.admin.cache;

import com.sau.gym.model.entity.system.OperaLog;

import java.util.List;

/**
 * 作者:hfj
 * 功能:操作日志 全量缓存 每天凌晨2点更新一次
 * 日期: 2026/4/1 14:58
 */
public class OperaLogCache {

    // 缓存：所有访问量的数据
    public static List<OperaLog> ALL_OPERA_LOG_LIST;
}
