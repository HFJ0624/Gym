package com.sau.gym.model.entity.agent;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/5/5 20:22
 */
@Data
@Schema(description = "Agent工具调用日志实体")
public class AgentToolLog {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "一次Agent对话的追踪ID")
    private String traceId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户原始输入")
    private String userMessage;

    @Schema(description = "工具名称")
    private String toolName;

    @Schema(description = "工具描述")
    private String toolDesc;

    @Schema(description = "工具类名称")
    private String toolClass;

    @Schema(description = "工具方法名")
    private String methodName;

    @Schema(description = "工具入参 JSON")
    private String argumentsJson;

    @Schema(description = "工具返回内容")
    private String resultText;

    @Schema(description = "调用状态,SUCCESS：成功 FAIL：失败")
    private String status;

    @Schema(description = "异常信息")
    private String errorMessage;

    @Schema(description = "工具执行耗时，单位毫秒")
    private Long durationMs;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
