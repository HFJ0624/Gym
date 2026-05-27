package com.sau.gym.admin.agent.tool.executor;

import com.sau.gym.admin.enums.AgentToolExecuteStatus;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 作者:hfj
 * 功能:Agent 工具统一执行结果
 * 作用:
 * 所有工具都返回这个对象，而不是各自随便返回 String。
 * 日期: 2026/5/27 14:48
 */
@Data
public class AgentToolExecuteResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工具执行状态。
     */
    private AgentToolExecuteStatus status;

    /**
     * 是否执行成功。
     * 注意:
     * success = true 通常对应 status = SUCCESS。
     */
    private boolean success;

    /**
     * 给用户看的消息。
     * 示例:
     * “已为您生成预约草稿，请确认是否预约。”
     */
    private String message;

    /**
     * 给大模型看的结果。
     * 示例:
     * RAG 检索结果、预约草稿 JSON、订单详情等。
     */
    private Object data;

    /**
     * 错误码。
     */
    private String errorCode;

    /**
     * 错误详情。
     * 主要给 Trace 或后台日志使用，不一定直接展示给用户。
     */
    private String errorDetail;

    /**
     * 工具执行耗时，单位毫秒。
     */
    private Long costTimeMs;

    /**
     * 是否需要用户确认。
     * 示例:
     * 取消预约、确认预约、提交订单需要确认。
     */
    private boolean needConfirm;

    /**
     * 确认动作编码。
     * 示例:
     * confirm_booking
     * confirm_cancel_booking
     */
    private String confirmAction;

    /**
     * 扩展信息。
     * 可以放:
     * 1. draftId
     * 2. bookingId
     * 3. sourceIds
     * 4. similarity
     */
    private Map<String, Object> extra = new HashMap<>();

    /**
     * 构造成功结果。
     */
    public static AgentToolExecuteResult success(String message, Object data) {
        AgentToolExecuteResult result = new AgentToolExecuteResult();
        result.setStatus(AgentToolExecuteStatus.SUCCESS);
        result.setSuccess(true);
        result.setMessage(message);
        result.setData(data);
        result.setNeedConfirm(false);
        return result;
    }

    /**
     * 构造需要用户确认的结果。
     * 典型场景:
     * 1. 生成预约草稿后，要求用户点击确认。
     * 2. 取消预约前，要求用户二次确认。
     */
    public static AgentToolExecuteResult needConfirm(
            String message,
            Object data,
            String confirmAction
    ) {
        AgentToolExecuteResult result = new AgentToolExecuteResult();
        result.setStatus(AgentToolExecuteStatus.NEED_CONFIRM);
        result.setSuccess(true);
        result.setMessage(message);
        result.setData(data);
        result.setNeedConfirm(true);
        result.setConfirmAction(confirmAction);
        return result;
    }

    /**
     * 构造参数错误结果。
     */
    public static AgentToolExecuteResult paramError(String message, String errorDetail) {
        AgentToolExecuteResult result = new AgentToolExecuteResult();
        result.setStatus(AgentToolExecuteStatus.PARAM_ERROR);
        result.setSuccess(false);
        result.setMessage(message);
        result.setErrorCode("PARAM_ERROR");
        result.setErrorDetail(errorDetail);
        return result;
    }

    /**
     * 构造权限不足结果。
     */
    public static AgentToolExecuteResult permissionDenied(String message) {
        AgentToolExecuteResult result = new AgentToolExecuteResult();
        result.setStatus(AgentToolExecuteStatus.PERMISSION_DENIED);
        result.setSuccess(false);
        result.setMessage(message);
        result.setErrorCode("PERMISSION_DENIED");
        return result;
    }

    /**
     * 构造限流结果。
     */
    public static AgentToolExecuteResult rateLimited(String message) {
        AgentToolExecuteResult result = new AgentToolExecuteResult();
        result.setStatus(AgentToolExecuteStatus.RATE_LIMITED);
        result.setSuccess(false);
        result.setMessage(message);
        result.setErrorCode("RATE_LIMITED");
        return result;
    }

    /**
     * 构造普通失败结果。
     */
    public static AgentToolExecuteResult failed(String message, String errorDetail) {
        AgentToolExecuteResult result = new AgentToolExecuteResult();
        result.setStatus(AgentToolExecuteStatus.FAILED);
        result.setSuccess(false);
        result.setMessage(message);
        result.setErrorCode("FAILED");
        result.setErrorDetail(errorDetail);
        return result;
    }

    /**
     * 构造系统异常结果。
     */
    public static AgentToolExecuteResult error(String message, String errorDetail) {
        AgentToolExecuteResult result = new AgentToolExecuteResult();
        result.setStatus(AgentToolExecuteStatus.ERROR);
        result.setSuccess(false);
        result.setMessage(message);
        result.setErrorCode("ERROR");
        result.setErrorDetail(errorDetail);
        return result;
    }

    /**
     * 添加扩展信息。
     */
    public AgentToolExecuteResult addExtra(String key, Object value) {
        this.extra.put(key, value);
        return this;
    }
}
