package com.sau.gym.admin.agent.tool.registry;

import com.sau.gym.admin.agent.tool.executor.AgentToolParamDefinition;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者:hfj
 * 功能:Agent 工具元信息对象
 * 作用:
 * 用于描述一个工具的基础信息。
 * 日期: 2026/5/27 15:28
 */
@Data
public class AgentToolInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工具编码。
     */
    private String toolCode;

    /**
     * 工具名称。
     */
    private String toolName;

    /**
     * 工具描述。
     * 用于告诉大模型或后台管理员这个工具适合做什么。
     */
    private String description;

    /**
     * 工具风险等级。
     */
    private String riskLevel;

    /**
     * 是否需要登录。
     */
    private boolean needLogin;

    /**
     * 是否需要用户确认。
     */
    private boolean needConfirm;

    /**
     * 限流秒数。
     */
    private int rateLimitSeconds;

    /**
     * 工具是否启用。
     * 当前第一版默认启用。
     * 后续可以改成从数据库或配置文件读取工具开关。
     */
    private boolean enabled = true;

    /**
     * 参数定义列表。
     */
    private List<AgentToolParamDefinition> paramDefinitions = new ArrayList<>();
}
