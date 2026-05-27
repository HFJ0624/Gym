package com.sau.gym.admin.controller.agent;

import com.sau.gym.admin.agent.tool.registry.AgentToolInfo;
import com.sau.gym.admin.agent.tool.registry.GymAgentToolRegistry;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 作者:hfj
 * 功能:Agent工具注册表接口
 * 日期: 2026/5/27 16:22
 */
@RestController
@RequestMapping("/admin/agent/tools")
public class AdminAgentToolRegistryController {

    private final GymAgentToolRegistry gymAgentToolRegistry;

    public AdminAgentToolRegistryController(GymAgentToolRegistry gymAgentToolRegistry) {
        this.gymAgentToolRegistry = gymAgentToolRegistry;
    }

    /**
     * 查询当前系统已注册的 Agent 工具列表。
     */
    @GetMapping("/list")
    public Result<List<AgentToolInfo>> listTools() {
        return Result.build(gymAgentToolRegistry.listTools(), ResultCodeEnum.SUCCESS);
    }
}
