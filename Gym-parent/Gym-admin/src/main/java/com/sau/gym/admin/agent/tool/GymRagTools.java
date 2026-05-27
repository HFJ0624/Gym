package com.sau.gym.admin.agent.tool;

import com.alibaba.fastjson.JSON;
import com.sau.gym.admin.agent.service.AgentToolGuardService;
import com.sau.gym.admin.agent.tool.executor.AgentToolContextFactory;
import com.sau.gym.admin.agent.tool.executor.AgentToolExecuteContext;
import com.sau.gym.admin.agent.tool.executor.AgentToolExecuteResult;
import com.sau.gym.admin.agent.tool.executor.impl.RagKnowledgeToolExecutor;
import com.sau.gym.admin.agent.tool.registry.GymAgentToolRegistry;
import com.sau.gym.admin.agent.util.AgentUserContext;
import com.sau.gym.admin.enums.AgentRiskLevel;
import com.sau.gym.admin.rag.service.RagQaService;
import com.sau.gym.model.dto.rag.RagAskDto;
import com.sau.gym.model.vo.rag.RagAnswerVO;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

/**
 * 作者:hfj
 * 功能: Agent的RAG知识库工具
 * 说明:
 * 这个类现在只保留 LangChain4j @Tool 入口。
 * 具体执行流程:
 * 1. LangChain4j 调用 askGymKnowledge()
 * 2. 构造 AgentToolExecuteContext
 * 3. 交给 GymAgentToolRegistry
 * 4. Registry 根据 toolCode 找到 RagKnowledgeToolExecutor
 * 5. Executor 执行真正 RAG 查询
 * 日期: 2026/5/5 15:19
 */
@Component
public class GymRagTools {

    private final GymAgentToolRegistry gymAgentToolRegistry;

    private final AgentToolContextFactory agentToolContextFactory;

    public GymRagTools(GymAgentToolRegistry gymAgentToolRegistry,
                       AgentToolContextFactory agentToolContextFactory) {
        this.gymAgentToolRegistry = gymAgentToolRegistry;
        this.agentToolContextFactory = agentToolContextFactory;
    }

    /**
     * 查询体育场馆预约平台知识库。
     *
     * @param question 用户问题
     * @param venueId  当前场馆ID，可为空
     * @param courtId  当前场地ID，可为空
     * @return RAG 回答结果 JSON
     */
    @Tool("查询体育场馆预约平台知识库。适合回答预约规则、退款规则、停车说明、开放时间、场馆设施、场地设施、价格说明、公告、FAQ等知识类问题。")
    public String askGymKnowledge(
            @P("用户原始问题，例如：预约怎么取消、羽毛球馆可以停车吗、1号篮球场价格怎么算") String question,
            @P(value = "场馆ID，可为空。如果用户问题涉及某个具体场馆，应传入该场馆ID", required = false) Long venueId,
            @P(value = "场地ID，可为空。如果用户问题涉及某个具体场地，应传入该场地ID", required = false) Long courtId
    ) {

        // 1. 构造统一工具执行上下文。
        AgentToolExecuteContext context = agentToolContextFactory.createRagContext(question, venueId, courtId);

        //2. 通过工具注册器执行工具。
        AgentToolExecuteResult result = gymAgentToolRegistry.execute(
                AgentToolCodes.ASK_GYM_KNOWLEDGE,
                context
        );

        //3. 返回统一 JSON 给大模型。
        return JSON.toJSONString(result);
    }
}
