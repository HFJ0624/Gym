package com.sau.gym.admin.agent.intent;

import com.sau.gym.admin.agent.intent.classifier.AgentIntentClassifier;
import org.springframework.stereotype.Service;

/**
 * 作者:hfj
 * 功能:Gym Agent 意图路由器
 * 作用:
 * 对外提供统一的意图路由入口。
 * 为什么不让 AgentServiceImpl 直接调用 RuleBasedAgentIntentClassifier:
 * 1. AgentServiceImpl 是主流程，不应该关心具体分类器实现。
 * 2. 后续可以在这里加入“规则识别 + 大模型兜底识别”。
 * 3. 后续可以在这里加入意图路由策略，例如某些意图直接走工具，某些意图走 RAG。
 * 日期: 2026/5/26 10:57
 */
@Service
public class GymIntentRouter {

    /**
     * 意图分类器接口。
     * 当前注入的是 RuleBasedAgentIntentClassifier。
     * 后续可以替换为 LlmAgentIntentClassifier 或 HybridAgentIntentClassifier。
     */
    private final AgentIntentClassifier agentIntentClassifier;

    public GymIntentRouter(AgentIntentClassifier agentIntentClassifier) {
        this.agentIntentClassifier = agentIntentClassifier;
    }

    /**
     * 执行意图识别。
     *
     * @param request 意图识别请求
     * @return 意图识别结果
     */
    public IntentRouteResult route(IntentRouteRequest request) {
        IntentRouteResult result = agentIntentClassifier.classify(request);

        // 防御性兜底，避免分类器异常返回 null 导致主流程空指针。
        if (result == null) {
            return IntentRouteResult.clarify(
                    "意图分类器返回空结果",
                    "你想查询场馆、预约场地，还是咨询预约规则？"
            );
        }

        return result;
    }
}
