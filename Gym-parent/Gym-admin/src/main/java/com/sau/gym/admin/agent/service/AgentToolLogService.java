package com.sau.gym.admin.agent.service;

public interface AgentToolLogService {

    /**
     * 记录工具调用日志。
     *
     * @param toolName 工具名称
     * @param toolDesc 工具描述
     * @param toolClass 工具类
     * @param methodName 方法名
     * @param argumentsJson 入参JSON
     * @param resultText 返回内容
     * @param status 状态：SUCCESS / FAIL
     * @param errorMessage 异常信息
     * @param durationMs 耗时
     */
    void record(String toolName,
                String toolDesc,
                String toolClass,
                String methodName,
                String argumentsJson,
                String resultText,
                String status,
                String errorMessage,
                Long durationMs);
}
