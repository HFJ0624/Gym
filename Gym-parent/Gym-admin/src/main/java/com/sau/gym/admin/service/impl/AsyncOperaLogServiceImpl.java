package com.sau.gym.admin.service.impl;

import com.sau.gym.admin.mapper.OperaLogMapper;
import com.sau.gym.common.log.service.AsyncOperaLogService;
import com.sau.gym.model.entity.system.OperaLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/10 16:28
 */
@Service
public class AsyncOperaLogServiceImpl implements AsyncOperaLogService {

    @Autowired
    private OperaLogMapper operaLogMapper;


    @Async // 异步执行保存日志操作
    @Override
    public void saveSysOperLog(OperaLog operaLog) {
        operaLogMapper.insert(operaLog);
    }
}
