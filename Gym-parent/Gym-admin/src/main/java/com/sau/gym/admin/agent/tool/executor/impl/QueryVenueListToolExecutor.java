package com.sau.gym.admin.agent.tool.executor.impl;

import com.sau.gym.admin.agent.service.AgentToolGuardService;
import com.sau.gym.admin.agent.tool.AgentToolCodes;
import com.sau.gym.admin.agent.tool.executor.AbstractGymAgentToolExecutor;
import com.sau.gym.admin.agent.tool.executor.AgentToolExecuteContext;
import com.sau.gym.admin.agent.tool.executor.AgentToolExecuteResult;
import com.sau.gym.admin.agent.tool.executor.AgentToolParamDefinition;
import com.sau.gym.admin.enums.AgentRiskLevel;
import com.sau.gym.admin.mapper.VenueMapper;
import com.sau.gym.model.entity.venue.Venue;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 作者:hfj
 * 功能:查询场馆列表工具执行器
 * 作用:
 * 1. 查询系统中的场馆列表。
 * 2. 支持按照场馆关键词模糊匹配。
 * 3. 这是低风险只读工具，不修改业务数据。
 * 日期: 2026/5/27 20:12
 */
@Component
public class QueryVenueListToolExecutor extends AbstractGymAgentToolExecutor {

    private final VenueMapper venueMapper;

    public QueryVenueListToolExecutor(
            AgentToolGuardService agentToolGuardService,
            VenueMapper venueMapper
    ) {
        super(agentToolGuardService);
        this.venueMapper = venueMapper;
    }

    @Override
    public String toolCode() {
        return AgentToolCodes.QUERY_VENUE_LIST;
    }

    @Override
    public String toolName() {
        return "查询场馆列表";
    }

    @Override
    public String description() {
        return "根据关键词查询体育场馆列表，返回场馆名称和地址。适合用户问有什么场馆、场馆地址、有哪些体育馆等问题。";
    }

    @Override
    public AgentRiskLevel riskLevel() {
        return AgentRiskLevel.LOW;
    }

    @Override
    public boolean needLogin() {
        // 场馆列表属于公开信息，不需要登录。
        return false;
    }

    @Override
    public boolean needConfirm() {
        // 查询类工具不需要用户确认。
        return false;
    }

    @Override
    public int rateLimitSeconds() {
        return 3;
    }

    @Override
    public List paramDefinitions() {
        return Collections.singletonList(
                new AgentToolParamDefinition(
                        "keyword",
                        "场馆关键词，可为空",
                        "String",
                        false,
                        "篮球"
                )
        );
    }

    @Override
    protected AgentToolExecuteResult doExecute(AgentToolExecuteContext context) {
        String keyword = context.getStringParam("keyword");

        List<Venue> venueList = venueMapper.findAllVenue();

        if (venueList == null || venueList.isEmpty()) {
            return AgentToolExecuteResult.success(
                    "当前系统中暂无可查询的场馆。",
                    Collections.emptyList()
            );
        }

        // 如果用户输入了关键词，就按场馆名称模糊过滤。
        if (StringUtils.hasText(keyword)) {
            venueList = venueList.stream()
                    .filter(v -> v != null
                            && StringUtils.hasText(v.getVenueName())
                            && v.getVenueName().contains(keyword))
                    .collect(Collectors.toList());
        }

        if (venueList.isEmpty()) {
            return AgentToolExecuteResult.success(
                    "当前没有查到符合条件的场馆。",
                    Collections.emptyList()
            );
        }

        int limit = Math.min(venueList.size(), 8);

        List<Map<String, Object>> dataList = new ArrayList<>();
        StringBuilder message = new StringBuilder("可选场馆如下：\n");

        for (int i = 0; i < limit; i++) {
            Venue venue = venueList.get(i);

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("venueId", venue.getId());
            item.put("venueName", venue.getVenueName());
            item.put("location", venue.getLocation());
            dataList.add(item);

            message.append(i + 1)
                    .append(". ")
                    .append(venue.getVenueName())
                    .append("，地址：")
                    .append(venue.getLocation())
                    .append("\n");
        }

        return AgentToolExecuteResult.success(message.toString(), dataList);
    }
}
