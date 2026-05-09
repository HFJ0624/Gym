package com.sau.gym.admin.agent.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.agent.model.AgentTrace;
import com.sau.gym.admin.agent.model.AgentTraceStep;
import com.sau.gym.admin.agent.service.AgentTraceService;
import com.sau.gym.admin.agent.trace.AgentTraceContext;
import com.sau.gym.admin.mapper.AgentTraceMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * 作者:hfj
 * 功能:Agent Trace 服务实现
 * 第一版目标：
 * 1. 能创建 trace
 * 2. 能记录步骤
 * 3. 能标记成功 / 失败
 * 4. 能分页查询
 * 日期: 2026/5/9 14:47
 */
@Service
public class AgentTraceServiceImpl implements AgentTraceService {

    private final AgentTraceMapper agentTraceMapper;

    public AgentTraceServiceImpl(AgentTraceMapper agentTraceMapper) {
        this.agentTraceMapper = agentTraceMapper;
    }

    /**
     * 开始一次 Trace。
     */
    @Override
    public void startTrace(String traceId, Long userId, String username, String userMessage) {
        if (traceId == null || traceId.trim().isEmpty()) {
            return;
        }

        AgentTrace trace = new AgentTrace();

        trace.setTraceId(traceId);
        trace.setUserId(userId);
        trace.setUsername(username);
        trace.setUserMessage(limitText(userMessage));
        trace.setStatus("RUNNING");

        agentTraceMapper.insertTrace(trace);
    }

    /**
     * 记录 Trace 步骤。
     */
    @Override
    public void addStep(String stepType,
                        String stepName,
                        String inputData,
                        String outputData,
                        String status,
                        String errorMessage,
                        Long costMs) {

        String traceId = AgentTraceContext.getTraceId();

        if (traceId == null || traceId.trim().isEmpty()) {
            return;
        }

        AgentTraceStep step = new AgentTraceStep();

        step.setTraceId(traceId);
        step.setStepType(stepType);
        step.setStepName(stepName);
        step.setInputData(limitText(inputData));
        step.setOutputData(limitText(outputData));
        step.setStatus(status == null ? "SUCCESS" : status);
        step.setErrorMessage(limitText(errorMessage));
        step.setCostMs(costMs == null ? 0L : costMs);
        step.setSortOrder(AgentTraceContext.nextOrder());

        agentTraceMapper.insertStep(step);
    }

    /**
     * 成功结束 Trace。
     */
    @Override
    public void finishSuccess(String traceId, String finalReply, Long totalCostMs) {
        if (traceId == null || traceId.trim().isEmpty()) {
            return;
        }

        int toolCount = agentTraceMapper.countToolSteps(traceId);

        agentTraceMapper.finishTrace(
                traceId,
                limitText(finalReply),
                "SUCCESS",
                null,
                totalCostMs == null ? 0L : totalCostMs,
                toolCount
        );
    }

    /**
     * 失败结束 Trace。
     */
    @Override
    public void finishFailed(String traceId, String finalReply, String errorMessage, Long totalCostMs) {
        if (traceId == null || traceId.trim().isEmpty()) {
            return;
        }

        int toolCount = agentTraceMapper.countToolSteps(traceId);

        agentTraceMapper.finishTrace(
                traceId,
                limitText(finalReply),
                "FAILED",
                limitText(errorMessage),
                totalCostMs == null ? 0L : totalCostMs,
                toolCount
        );
    }

    @Override
    public PageInfo<AgentTrace> page(Integer current, Integer limit, Long userId, String status, String keyword) {
        PageHelper.startPage(current, limit);

        List<AgentTrace> list = agentTraceMapper.selectTracePage(userId, status, keyword);

        return new PageInfo<>(list);
    }

    @Override
    public AgentTrace detail(String traceId) {
        return agentTraceMapper.selectTraceByTraceId(traceId);
    }

    @Override
    public List<AgentTraceStep> steps(String traceId) {
        return agentTraceMapper.selectStepsByTraceId(traceId);
    }

    /**
     * 生成 traceId。
     */
    private String generateTraceId() {
        return "AGENT-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * 限制文本长度。
     *
     * 避免大模型返回过长内容导致数据库字段过大。
     */
    private String limitText(String text) {
        if (text == null) {
            return null;
        }

        int maxLength = 6000;

        if (text.length() <= maxLength) {
            return text;
        }

        return text.substring(0, maxLength) + "...[内容过长，已截断]";
    }
}
