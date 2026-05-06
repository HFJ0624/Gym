package com.sau.gym.admin.controller.ai;

import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.agent.service.AgentToolLogManageService;
import com.sau.gym.model.dto.agent.AgentToolLogQueryDto;
import com.sau.gym.model.entity.agent.AgentToolLog;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.vo.agent.AgentToolLogStatsVO;
import org.springframework.web.bind.annotation.*;

/**
 * 作者:hfj
 * 功能:管理端Agent工具调用日志接口
 * 日期: 2026/5/5 21:35
 */
@RestController
@RequestMapping("/admin/ai/toolLog")
public class AgentToolLogController {

    private final AgentToolLogManageService agentToolLogManageService;

    public AgentToolLogController(AgentToolLogManageService agentToolLogManageService) {
        this.agentToolLogManageService = agentToolLogManageService;
    }

    //分页查询Agent工具调用日志
    @PostMapping("/page")
    public Result page(@RequestBody AgentToolLogQueryDto queryDto) {
        PageInfo<AgentToolLog> pageInfo = agentToolLogManageService.page(queryDto);
        return Result.build(pageInfo, ResultCodeEnum.SUCCESS);
    }

    //查询工具调用日志详情
    @GetMapping("/{id}")
    public Result detail(@PathVariable Long id) {
        AgentToolLog log = agentToolLogManageService.detail(id);
        return Result.build(log, ResultCodeEnum.SUCCESS);
    }

    // 查询工具调用统计
    @PostMapping("/stats")
    public Result stats(@RequestBody AgentToolLogQueryDto queryDto) {
        AgentToolLogStatsVO stats = agentToolLogManageService.stats(queryDto);
        return Result.build(stats, ResultCodeEnum.SUCCESS);
    }
}
