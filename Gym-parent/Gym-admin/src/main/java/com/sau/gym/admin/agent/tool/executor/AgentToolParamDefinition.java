package com.sau.gym.admin.agent.tool.executor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 作者:hfj
 * 功能:Agent 工具参数定义
 * 作用:
 * 描述某个工具需要哪些参数。
 * 日期: 2026/5/27 13:53
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentToolParamDefinition implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 参数名称。
     */
    private String name;

    /**
     * 参数中文说明。
     */
    private String description;

    /**
     * 参数类型。
     */
    private String type;

    /**
     * 是否必填。
     */
    private boolean required;

    /**
     * 示例值。
     */
    private String example;
}
