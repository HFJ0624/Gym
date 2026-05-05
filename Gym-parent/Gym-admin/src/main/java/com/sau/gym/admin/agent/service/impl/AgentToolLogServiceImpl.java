package com.sau.gym.admin.agent.service.impl;

import com.sau.gym.admin.agent.context.AgentTraceContext;
import com.sau.gym.admin.agent.context.AgentTraceInfo;
import com.sau.gym.admin.agent.service.AgentToolLogService;
import com.sau.gym.admin.mapper.AgentToolLogMapper;
import com.sau.gym.model.entity.agent.AgentToolLog;
import org.springframework.stereotype.Service;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/5/5 20:32
 */
@Service
public class AgentToolLogServiceImpl implements AgentToolLogService {

    private final AgentToolLogMapper agentToolLogMapper;

    public AgentToolLogServiceImpl(AgentToolLogMapper agentToolLogMapper) {
        this.agentToolLogMapper = agentToolLogMapper;
    }

    //记录工具调用日志。
    @Override
    public void record(String toolName, String toolDesc, String toolClass, String methodName, String argumentsJson, String resultText, String status, String errorMessage, Long durationMs) {
        try {
            AgentTraceInfo traceInfo = AgentTraceContext.get();

            AgentToolLog log = new AgentToolLog();

            if (traceInfo != null) {
                log.setTraceId(traceInfo.getTraceId());
                log.setUserId(traceInfo.getUserId());
                log.setUserMessage(traceInfo.getUserMessage());
            }

            log.setToolName(toolName);
            log.setToolDesc(toolDesc);
            log.setToolClass(toolClass);
            log.setMethodName(methodName);
            log.setArgumentsJson(argumentsJson);
            log.setResultText(resultText);
            log.setStatus(status);
            log.setErrorMessage(errorMessage);
            log.setDurationMs(durationMs);

            agentToolLogMapper.insert(log);

        } catch (Exception e) {
            //记录日志失败不能影响 Agent 主流程。
            System.out.println("[AgentToolLog] 记录工具日志失败：" + e.getMessage());
        }
    }
}
