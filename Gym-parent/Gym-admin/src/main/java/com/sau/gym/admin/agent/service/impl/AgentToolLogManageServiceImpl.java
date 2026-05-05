package com.sau.gym.admin.agent.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.agent.service.AgentToolLogManageService;
import com.sau.gym.admin.mapper.AgentToolLogMapper;
import com.sau.gym.model.dto.agent.AgentToolLogQueryDto;
import com.sau.gym.model.entity.agent.AgentToolLog;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 作者:hfj
 * 功能:Agent工具调用日志管理服务实现
 * 日期: 2026/5/5 21:33
 */
@Service
public class AgentToolLogManageServiceImpl implements AgentToolLogManageService {

    private final AgentToolLogMapper agentToolLogMapper;

    public AgentToolLogManageServiceImpl(AgentToolLogMapper agentToolLogMapper) {
        this.agentToolLogMapper = agentToolLogMapper;
    }

    /**
     * 分页查询工具调用日志。
     */
    @Override
    public PageInfo<AgentToolLog> page(AgentToolLogQueryDto queryDto) {
        if (queryDto == null) {
            queryDto = new AgentToolLogQueryDto();
        }

        if (queryDto.getPageNum() == null || queryDto.getPageNum() <= 0) {
            queryDto.setPageNum(1);
        }

        if (queryDto.getPageSize() == null || queryDto.getPageSize() <= 0) {
            queryDto.setPageSize(10);
        }

        /**
         * 使用 PageHelper 做分页。
         */
        PageHelper.startPage(queryDto.getPageNum(), queryDto.getPageSize());

        List<AgentToolLog> list = agentToolLogMapper.selectPage(queryDto);

        return new PageInfo<>(list);
    }

    /**
     * 查询日志详情。
     */
    @Override
    public AgentToolLog detail(Long id) {
        if (id == null) {
            throw new RuntimeException("日志ID不能为空");
        }

        AgentToolLog log = agentToolLogMapper.selectById(id);

        if (log == null) {
            throw new RuntimeException("Agent工具调用日志不存在");
        }

        return log;
    }
}
