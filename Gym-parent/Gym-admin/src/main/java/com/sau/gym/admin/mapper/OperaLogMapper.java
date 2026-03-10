package com.sau.gym.admin.mapper;

import com.sau.gym.model.dto.system.OperaLogDto;
import com.sau.gym.model.entity.system.OperaLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OperaLogMapper {

    //插入日志到数据库
    void insert(OperaLog operaLog);

    //操作日志列表查询
    List<OperaLog> findByPage(OperaLogDto operaLogDto);
}
