package com.sau.gym.common.log.service;

import com.sau.gym.model.entity.system.OperaLog;

public interface AsyncOperaLogService {

    //保存日志数据
    void saveSysOperLog(OperaLog operaLog);
}
