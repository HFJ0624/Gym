package com.sau.gym.admin.mapper;

import com.sau.gym.model.entity.system.OperaLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperaLogMapper {

    //插入日志到数据库
    void insert(OperaLog operaLog);
}
