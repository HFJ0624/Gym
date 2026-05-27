package com.sau.gym.admin.agent.tool;

import com.alibaba.fastjson.JSON;
import com.sau.gym.admin.agent.tool.executor.AgentToolContextFactory;
import com.sau.gym.admin.agent.tool.executor.AgentToolExecuteContext;
import com.sau.gym.admin.agent.tool.executor.AgentToolExecuteResult;
import com.sau.gym.admin.agent.tool.registry.GymAgentToolRegistry;
import com.sau.gym.admin.agent.util.AgentUserContext;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

/**
 * 作者:hfj
 * 功能:查询场馆，查询公告工具
 * 日期: 2026/4/23 14:45
 */
@Component
public class GymQueryTools {

    private final GymAgentToolRegistry gymAgentToolRegistry;

    private final AgentToolContextFactory agentToolContextFactory;

    public GymQueryTools(GymAgentToolRegistry gymAgentToolRegistry,
                         AgentToolContextFactory agentToolContextFactory) {
        this.gymAgentToolRegistry = gymAgentToolRegistry;
        this.agentToolContextFactory = agentToolContextFactory;
    }

    /***
     * 查询场馆工具
     * P表示参数描述，方便模型理解这个参数是什么
     * @param keyword 关键词
     * @return 返回匹配关键词的场馆列表
     */
    @Tool("查询场馆列表。可以按场馆关键词模糊匹配，返回场馆名称和地址。")
    public String queryVenues(@P(value = "场馆关键词，可为空", required = false) String keyword) {

        // 1. 构造统一工具执行上下文。
        AgentToolExecuteContext context = agentToolContextFactory.createQueryVenueContext(keyword);

        //2. 通过工具注册器执行工具。
        AgentToolExecuteResult result = gymAgentToolRegistry.execute(
                AgentToolCodes.QUERY_VENUE_LIST,
                context
        );

        //3. 返回统一 JSON 给大模型。
        return JSON.toJSONString(result);
    }

    /***
     * 查询公告工具
     * @return 返回最近若干条公告标题和内容
     */
    @Tool("查询最新公告，返回最近若干条公告标题和内容。")
    public String queryNotices() {

        // 1. 构造统一工具执行上下文。
        AgentToolExecuteContext context = agentToolContextFactory.createQueryNoticeContext("查询最新公告");

        // 2. 通过工具注册器执行工具。
        AgentToolExecuteResult result = gymAgentToolRegistry.execute(
                AgentToolCodes.QUERY_NOTICE_LIST,
                context
        );

        // 3. 返回统一 JSON 给大模型。
        return JSON.toJSONString(result);
    }
}
