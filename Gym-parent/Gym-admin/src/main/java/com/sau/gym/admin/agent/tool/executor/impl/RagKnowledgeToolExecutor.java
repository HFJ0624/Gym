package com.sau.gym.admin.agent.tool.executor.impl;

import com.sau.gym.admin.agent.service.AgentToolGuardService;
import com.sau.gym.admin.agent.tool.executor.AbstractGymAgentToolExecutor;
import com.sau.gym.admin.agent.tool.executor.AgentToolExecuteContext;
import com.sau.gym.admin.agent.tool.executor.AgentToolExecuteResult;
import com.sau.gym.admin.agent.tool.executor.AgentToolParamDefinition;
import com.sau.gym.admin.enums.AgentRiskLevel;
import com.sau.gym.admin.rag.service.RagQaService;
import com.sau.gym.model.dto.rag.RagAskDto;
import com.sau.gym.model.vo.rag.RagAnswerVO;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 作者:hfj
 * 功能:RAG 知识库问答工具执行器
 * 作用:
 * 把原来 GymRagTools 里的核心查询逻辑抽出来，
 * 变成统一 Executor 风格。
 * 日期: 2026/5/27 14:59
 */
@Component
public class RagKnowledgeToolExecutor extends AbstractGymAgentToolExecutor {

    private final RagQaService ragQaService;

    public RagKnowledgeToolExecutor(
            AgentToolGuardService agentToolGuardService,
            RagQaService ragQaService
    ) {
        super(agentToolGuardService);
        this.ragQaService = ragQaService;
    }

    /**
     * 工具编码。
     * 后续工具注册器会用这个编码作为 Map 的 key。
     */
    @Override
    public String toolCode() {
        return "ask_gym_knowledge";
    }

    /**
     * 工具名称。
     */
    @Override
    public String toolName() {
        return "RAG知识库问答";
    }

    /**
     * 工具描述。
     * 后续可以暴露给大模型，让大模型知道什么时候使用这个工具。
     */
    @Override
    public String description() {
        return "适合回答体育场馆预约平台的规则、退款、取消、停车、开放时间、设施、公告和FAQ等知识类问题。";
    }

    /**
     * RAG 查询是低风险工具。
     */
    @Override
    public AgentRiskLevel riskLevel() {
        return AgentRiskLevel.LOW;
    }

    /**
     * 知识库问答可以不登录。
     * 例如游客也可以问开放时间、预约规则。
     */
    @Override
    public boolean needLogin() {
        return false;
    }

    /**
     * RAG 查询不改变业务状态，不需要用户确认。
     */
    @Override
    public boolean needConfirm() {
        return false;
    }

    /**
     * RAG 工具限流。
     * 开发调试阶段可以设置为 0。
     * 正式环境建议设置为 3 或 5。
     */
    @Override
    public int rateLimitSeconds() {
        return 3;
    }

    /**
     * 参数定义。
     * question 必填。
     * venueId 和 courtId 可选。
     */
    @Override
    public List<AgentToolParamDefinition> paramDefinitions() {
        return Arrays.asList(
                new AgentToolParamDefinition(
                        "question",
                        "用户原始问题",
                        "String",
                        true,
                        "取消预约后可以退款吗"
                ),
                new AgentToolParamDefinition(
                        "venueId",
                        "场馆ID",
                        "Long",
                        false,
                        "1"
                ),
                new AgentToolParamDefinition(
                        "courtId",
                        "场地ID",
                        "Long",
                        false,
                        "10"
                )
        );
    }

    /**
     * 执行 RAG 知识库查询。
     */
    @Override
    protected AgentToolExecuteResult doExecute(AgentToolExecuteContext context) {

        //1. 从统一上下文中读取参数。
        String question = context.getStringParam("question");
        Long venueId = context.getLongParam("venueId");
        Long courtId = context.getLongParam("courtId");

        //2. 构造 RAG 查询 DTO。
        RagAskDto dto = new RagAskDto();
        dto.setQuestion(question);
        dto.setVenueId(venueId);
        dto.setCourtId(courtId);

        //3. 调用你现有的 RAG 服务。
        RagAnswerVO answerVO = ragQaService.ask(dto);

        //4. 返回统一工具结果。
        return AgentToolExecuteResult.success(
                "知识库查询成功。",
                answerVO
        );
    }
}
