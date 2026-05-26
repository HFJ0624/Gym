package com.sau.gym.admin.agent.intent;

import com.sau.gym.admin.enums.AgentIntent;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 作者:hfj
 * 功能:意图识别结果对象
 * 作用:
 * 保存本轮用户消息的意图识别结果。
 * 后续可以用于:
 * 1. Trace 日志记录
 * 2. 选择工具
 * 3. 构造提示词
 * 4. 判断是否需要追问
 * 日期: 2026/5/26 10:49
 */
@Data
public class IntentRouteResult {

    /**
     * 识别出的意图枚举。
     */
    private AgentIntent intent;

    /**
     * 置信度，取值范围 0.0 ~ 1.0。
     *
     * 说明:
     * 规则命中非常明确时可以给 0.90 以上。
     * 命中弱关键词或依赖上下文时可以给 0.60 ~ 0.80。
     * 无法判断时给 0.30 以下。
     */
    private double confidence;

    /**
     * 识别原因。
     * 示例:
     * “命中预约关键词: 预约”
     */
    private String reason;

    /**
     * 命中的关键词。
     * 示例:
     * “预约”“取消”“退款”
     */
    private String matchedKeyword;

    /**
     * 是否需要直接追问用户。
     *
     * 示例:
     * 用户说“帮我预约一下”，但没有当前场馆、场地、时间。
     * 这时不应该直接调用大模型瞎猜，而应该追问。
     */
    private boolean needClarify;

    /**
     * 需要追问用户时的问题。
     */
    private String clarifyQuestion;

    /**
     * 建议调用的工具名称。
     * 当前阶段只是提示主流程或大模型。
     * 后续做统一工具注册器时，可以直接根据这个字段调用工具。
     */
    private String suggestedToolName;

    /**
     * 当前意图是否需要登录。
     */
    private boolean needLogin;

    /**
     * 构造给大模型看的路由提示词。
     * 这样即使最终还是走 LangChain4j Agent，也能让模型更明确应该调用哪个工具。
     */
    private String routePrompt;

    /**
     * 调试信息。
     * 可以放 effectiveVenueId、effectiveCourtId、是否命中上下文等。
     */
    private Map<String, Object> debugInfo = new HashMap<>();

    /**
     * 构造普通识别结果。
     */
    public static IntentRouteResult of(
            AgentIntent intent,
            double confidence,
            String reason,
            String matchedKeyword
    ) {
        IntentRouteResult result = new IntentRouteResult();
        result.setIntent(intent);
        result.setConfidence(confidence);
        result.setReason(reason);
        result.setMatchedKeyword(matchedKeyword);
        result.setNeedClarify(false);
        result.setSuggestedToolName(intent.getSuggestedToolName());
        result.setNeedLogin(intent.isNeedLogin());
        result.setRoutePrompt(buildDefaultRoutePrompt(result));
        return result;
    }

    /**
     * 构造需要追问的识别结果。
     */
    public static IntentRouteResult clarify(String reason, String clarifyQuestion) {
        IntentRouteResult result = new IntentRouteResult();
        result.setIntent(AgentIntent.UNSURE);
        result.setConfidence(0.30);
        result.setReason(reason);
        result.setNeedClarify(true);
        result.setClarifyQuestion(clarifyQuestion);
        result.setSuggestedToolName(AgentIntent.UNSURE.getSuggestedToolName());
        result.setNeedLogin(false);
        result.setRoutePrompt(buildDefaultRoutePrompt(result));
        return result;
    }

    /**
     * 构造默认路由提示词。
     *
     * 这个提示词不是返回给用户看的，而是拼接到 Agent 输入里，
     * 让大模型知道系统已经初步判断了用户意图。
     */
    private static String buildDefaultRoutePrompt(IntentRouteResult result) {
        StringBuilder builder = new StringBuilder();
        builder.append("〖意图识别结果〗\n");
        builder.append("用户意图：").append(result.getIntent().name()).append("\n");
        builder.append("意图说明：").append(result.getIntent().getDescription()).append("\n");
        builder.append("置信度：").append(result.getConfidence()).append("\n");
        builder.append("建议工具：").append(result.getSuggestedToolName()).append("\n");
        builder.append("是否需要登录：").append(result.isNeedLogin()).append("\n");
        builder.append("识别原因：").append(result.getReason()).append("\n");

        if (result.isNeedClarify()) {
            builder.append("处理建议：当前信息不足，应先追问用户：")
                    .append(result.getClarifyQuestion())
                    .append("\n");
        } else {
            builder.append("处理建议：优先围绕该意图选择合适工具；缺少必要参数时继续追问，不要编造。\n");
        }

        return builder.toString();
    }
}
