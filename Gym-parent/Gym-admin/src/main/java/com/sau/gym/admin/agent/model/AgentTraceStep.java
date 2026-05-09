package com.sau.gym.admin.agent.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 作者:hfj
 * 功能:Agent 调用链步骤实体
 * 日期: 2026/5/9 14:42
 */
@Data
public class AgentTraceStep {

    private Long id;

    /**
     * 调用链ID。
     */
    private String traceId;

    /**
     * 步骤类型。
     *
     * 例如：
     * TRACE_START
     * CONTEXT_PREPARE
     * PENDING_ACTION
     * DIRECT_ROUTE
     * LLM_CALL
     * FINAL_REPLY
     * TRACE_FAILED
     */
    private String stepType;

    /**
     * 步骤名称。
     */
    private String stepName;

    /**
     * 输入数据。
     */
    private String inputData;

    /**
     * 输出数据。
     */
    private String outputData;

    /**
     * 状态：SUCCESS / FAILED。
     */
    private String status;

    /**
     * 异常信息。
     */
    private String errorMessage;

    /**
     * 当前步骤耗时。
     */
    private Long costMs;

    /**
     * 排序号。
     */
    private Integer sortOrder;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
