package com.sau.gym.admin.mapper;

import com.sau.gym.model.dto.agent.AgentToolLogQueryDto;
import com.sau.gym.model.entity.agent.AgentToolLog;
import com.sau.gym.model.vo.agent.AgentToolLogStatsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AgentToolLogMapper {

    //工具调用日志。
    int insert(AgentToolLog log);

    /**
     * 分页查询 Agent 工具调用日志。
     *
     * @param queryDto 查询参数
     * @return 日志列表
     */
    List<AgentToolLog> selectPage(AgentToolLogQueryDto queryDto);

    /**
     * 根据ID查询详情。
     *
     * @param id 日志ID
     * @return 日志详情
     */
    AgentToolLog selectById(@Param("id") Long id);

    /**
     * 查询 Agent 工具调用统计。
     */
    AgentToolLogStatsVO selectStats(AgentToolLogQueryDto queryDto);
}
