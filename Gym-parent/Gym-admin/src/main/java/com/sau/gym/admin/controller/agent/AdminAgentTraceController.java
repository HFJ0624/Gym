package com.sau.gym.admin.controller.agent;

import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.agent.model.AgentTrace;
import com.sau.gym.admin.agent.model.AgentTraceStep;
import com.sau.gym.admin.agent.service.AgentTraceService;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/5/9 14:49
 */
@RestController
@RequestMapping("/admin/agent/trace")
public class AdminAgentTraceController {

    private final AgentTraceService agentTraceService;

    public AdminAgentTraceController(AgentTraceService agentTraceService) {
        this.agentTraceService = agentTraceService;
    }

    /**
     * 分页查询 Trace。
     */
    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") Integer current,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(required = false) Long userId,
                       @RequestParam(required = false) String status,
                       @RequestParam(required = false) String keyword) {

        PageInfo<AgentTrace> pageInfo = agentTraceService.page(current, limit, userId, status, keyword);

        return Result.build(pageInfo, ResultCodeEnum.SUCCESS);
    }

    /**
     * 查询 Trace 详情。
     */
    @GetMapping("/detail/{traceId}")
    public Result detail(@PathVariable String traceId) {
        AgentTrace trace = agentTraceService.detail(traceId);
        List<AgentTraceStep> steps = agentTraceService.steps(traceId);

        Map<String, Object> result = new HashMap<>();

        result.put("trace", trace);
        result.put("steps", steps);

        return Result.build(result, ResultCodeEnum.SUCCESS);
    }

    /**
     * 只查询步骤。
     */
    @GetMapping("/steps/{traceId}")
    public Result steps(@PathVariable String traceId) {
        List<AgentTraceStep> steps = agentTraceService.steps(traceId);
        return Result.build(steps, ResultCodeEnum.SUCCESS);
    }
}
