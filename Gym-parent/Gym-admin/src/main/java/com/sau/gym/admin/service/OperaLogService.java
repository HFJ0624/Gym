package com.sau.gym.admin.service;

import com.github.pagehelper.PageInfo;
import com.sau.gym.model.dto.system.OperaLogDto;
import com.sau.gym.model.entity.system.OperaLog;

public interface OperaLogService {

    //操作日志列表查询
    PageInfo<OperaLog> findByPage(Integer current, Integer limit, OperaLogDto operaLogDto);

    //删除操作日志记录
    void deleteById(Long id);
}
