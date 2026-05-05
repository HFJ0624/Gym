package com.sau.gym.admin.mapper;

import com.sau.gym.model.entity.agent.AgentToolLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AgentToolLogMapper {

    //工具调用日志。
    int insert(AgentToolLog log);
}
