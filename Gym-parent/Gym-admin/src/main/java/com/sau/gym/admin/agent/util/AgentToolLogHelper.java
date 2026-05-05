package com.sau.gym.admin.agent.util;

import com.alibaba.fastjson.JSON;
import com.sau.gym.admin.agent.service.AgentToolLogService;
import org.springframework.stereotype.Component;

import java.util.Map;
/**
 * 作者:hfj
 * 功能:Agent 工具日志辅助类。
 * 为什么不用 AOP？
 * 因为 LangChain4j 的 .tools(...) 对 Spring CGLIB 代理对象不友好。
 * 使用 AOP 后，工具类会变成 XxxTools$$SpringCGLIB$$0，
 * LangChain4j 可能扫描不到 @Tool 注解，导致启动失败。
 * 所以这里采用手动日志方式，稳定优先。
 * 日期: 2026/5/5 20:47
 */
@Component
public class AgentToolLogHelper {

    private final AgentToolLogService agentToolLogService;

    public AgentToolLogHelper(AgentToolLogService agentToolLogService) {
        this.agentToolLogService = agentToolLogService;
    }

    /**
     * 记录成功日志。
     *
     * @param toolName 工具名称
     * @param toolDesc 工具描述
     * @param toolClass 工具类名
     * @param methodName 方法名
     * @param arguments 参数
     * @param result 返回结果
     * @param durationMs 耗时
     */
    public void success(String toolName,
                        String toolDesc,
                        String toolClass,
                        String methodName,
                        Map<String, Object> arguments,
                        String result,
                        Long durationMs) {
        agentToolLogService.record(
                toolName,
                toolDesc,
                toolClass,
                methodName,
                safeJson(arguments),
                result,
                "SUCCESS",
                null,
                durationMs
        );
    }

    /**
     * 记录失败日志。
     *
     * @param toolName 工具名称
     * @param toolDesc 工具描述
     * @param toolClass 工具类名
     * @param methodName 方法名
     * @param arguments 参数
     * @param error 异常
     * @param durationMs 耗时
     */
    public void fail(String toolName,
                     String toolDesc,
                     String toolClass,
                     String methodName,
                     Map<String, Object> arguments,
                     Throwable error,
                     Long durationMs) {
        agentToolLogService.record(
                toolName,
                toolDesc,
                toolClass,
                methodName,
                safeJson(arguments),
                null,
                "FAIL",
                error == null ? null : error.getMessage(),
                durationMs
        );
    }

    /**
     * 安全 JSON 序列化。
     */
    private String safeJson(Object value) {
        try {
            return JSON.toJSONString(value);
        } catch (Exception e) {
            return String.valueOf(value);
        }
    }
}
