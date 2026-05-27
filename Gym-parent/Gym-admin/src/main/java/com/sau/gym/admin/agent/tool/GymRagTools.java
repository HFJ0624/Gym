package com.sau.gym.admin.agent.tool;

import com.alibaba.fastjson.JSON;
import com.sau.gym.admin.agent.service.AgentToolGuardService;
import com.sau.gym.admin.agent.tool.executor.AgentToolExecuteContext;
import com.sau.gym.admin.agent.tool.executor.AgentToolExecuteResult;
import com.sau.gym.admin.agent.tool.executor.impl.RagKnowledgeToolExecutor;
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
 * 作用:
 * 让现有智能 Agent 可以调用你刚搭建好的 RAG 知识库。
 * 使用场景:
 * 1. 用户问预约规则
 * 2. 用户问取消退款规则
 * 3. 用户问场馆设施
 * 4. 用户问停车说明
 * 5. 用户问开放时间
 * 6. 用户问公告、FAQ、注意事项
 * 注意:
 * 这个工具只负责“查知识”，不负责创建预约、不负责下单。
 * 真正预约仍然交给 GymBookingTools。
 * 日期: 2026/5/5 15:19
 */
@Component
public class GymRagTools {

    private final RagKnowledgeToolExecutor ragKnowledgeToolExecutor;

    public GymRagTools(RagKnowledgeToolExecutor ragKnowledgeToolExecutor) {
        this.ragKnowledgeToolExecutor = ragKnowledgeToolExecutor;
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
        //1. 构造统一工具执行上下文。
        AgentToolExecuteContext context = new AgentToolExecuteContext();

        //2. 从 ThreadLocal 中获取当前用户ID。
        context.setUserId(AgentUserContext.getUserId());

        //3. 设置原始问题。
        context.setOriginalQuestion(question);

        //4. 设置工具参数。
        context.addParam("question", question);
        context.addParam("venueId", venueId);
        context.addParam("courtId", courtId);

        //5. 统一调用 Executor。
        AgentToolExecuteResult result = ragKnowledgeToolExecutor.execute(context);

        //6. 返回 JSON 字符串给大模型。
        return JSON.toJSONString(result);
    }
}
