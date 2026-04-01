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

    //删除操作日志记录
    void deleteById(Long id);

    //查询所有操作日志
    List<OperaLog> selectALL();
}
