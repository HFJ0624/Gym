package com.sau.gym.admin.agent.intent.classifier;

import com.sau.gym.admin.agent.intent.IntentRouteRequest;
import com.sau.gym.admin.agent.intent.IntentRouteResult;

/**
 * 作者: hfj
 * 功能: 意图分类器接口
 * 作用:
 * 定义“输入一段用户消息，输出一个意图识别结果”的统一规范。
 * 为什么要定义接口:
 * 1. 当前第一版可以用规则识别。
 * 2. 后续如果要改成大模型识别、规则 + 大模型混合识别，不需要改主流程。
 * 3. 方便单元测试和替换实现。
 */
public interface AgentIntentClassifier {

    /**
     * 对用户本轮消息进行意图识别。
     *
     * @param request 意图识别请求，包含用户消息、有效场馆ID、有效场地ID、业务上下文等
     * @return 意图识别结果
     */
    IntentRouteResult classify(IntentRouteRequest request);
}
