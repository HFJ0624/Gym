package com.sau.gym.admin.agent.service;

import com.github.pagehelper.PageInfo;
import com.sau.gym.model.dto.agent.AgentToolLogQueryDto;
import com.sau.gym.model.entity.agent.AgentToolLog;

public interface AgentToolLogManageService {

    /**
     * 分页查询工具调用日志。
     *
     * @param queryDto 查询参数
     * @return 分页结果
     */
    PageInfo<AgentToolLog> page(AgentToolLogQueryDto queryDto);

    /**
     * 查询工具调用日志详情。
     *
     * @param id 日志ID
     * @return 日志详情
     */
    AgentToolLog detail(Long id);
}
