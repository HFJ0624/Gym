package com.sau.gym.admin.agent.rewrite;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者:hfj
 * 功能:问题重写结果对象
 * 作用:
 * 保存问题重写后的结果，供后续直达路由、大模型调用和 Trace 记录使用。
 * 日期: 2026/5/26 13:55
 */
@Data
public class QuestionRewriteResult {

    /**
     * 用户原始问题。
     * 示例:
     * “那就预约这个”
     */
    private String originalQuestion;

    /**
     * 重写后的问题。
     * 示例:
     * “用户想生成预约草稿；当前场馆ID=1；当前场地ID=5；预约日期=2026-05-27；开始时间=19:00:00；原始问题=那就预约这个。”
     */
    private String rewrittenQuestion;

    /**
     * 是否真的发生了重写。
     * true:
     * 表示系统补充了上下文或把问题改成了更清晰的结构化表达。
     * false:
     * 表示原始问题已经足够清晰，不需要重写。
     */
    private boolean rewritten;

    /**
     * 重写原因。
     * 示例:
     * “检测到指代词，需要结合最近场地上下文”
     */
    private String reason;

    /**
     * 本次重写补充了哪些槽位。
     * 示例:
     * venueId、courtId、bookingDate、startTime、endTime
     */
    private List<String> filledSlots = new ArrayList<>();

    /**
     * 给大模型看的重写提示词。
     * 注意:
     * 这个字段不是返回给用户看的，而是拼进 Agent 输入中，
     * 用来告诉大模型系统已经做过问题重写。
     */
    private String rewritePrompt;

    /**
     * 调试信息。
     * 可以放 intent、effectiveVenueId、effectiveCourtId、lastIntent 等。
     * 后续 Trace 页面可以展示这些信息。
     */
    private Map<String, Object> debugInfo = new HashMap<>();

    /**
     * 构造未重写结果。
     *
     * @param originalQuestion 用户原始问题
     * @return 未重写结果
     */
    public static QuestionRewriteResult noChange(String originalQuestion) {
        QuestionRewriteResult result = new QuestionRewriteResult();
        result.setOriginalQuestion(originalQuestion);
        result.setRewrittenQuestion(originalQuestion);
        result.setRewritten(false);
        result.setReason("原始问题已经足够清晰，未进行重写");
        result.setRewritePrompt(buildRewritePrompt(result));
        return result;
    }

    /**
     * 构造已重写结果。
     *
     * @param originalQuestion  用户原始问题
     * @param rewrittenQuestion 重写后的问题
     * @param reason            重写原因
     * @param filledSlots       补充的槽位
     * @return 重写结果
     */
    public static QuestionRewriteResult changed(
            String originalQuestion,
            String rewrittenQuestion,
            String reason,
            List<String> filledSlots
    ) {
        QuestionRewriteResult result = new QuestionRewriteResult();
        result.setOriginalQuestion(originalQuestion);
        result.setRewrittenQuestion(rewrittenQuestion);
        result.setRewritten(true);
        result.setReason(reason);

        if (filledSlots != null) {
            result.setFilledSlots(filledSlots);
        }

        result.setRewritePrompt(buildRewritePrompt(result));
        return result;
    }

    /**
     * 构造给大模型看的重写提示词。
     * 作用:
     * 让大模型明确知道:
     * 1. 用户原话是什么
     * 2. 系统补充了哪些上下文
     * 3. 后续工具调用应以重写问题为准
     */
    private static String buildRewritePrompt(QuestionRewriteResult result) {
        StringBuilder builder = new StringBuilder();

        builder.append("〖问题重写结果〗\n");
        builder.append("用户原始问题：")
                .append(result.getOriginalQuestion())
                .append("\n");

        builder.append("系统重写问题：")
                .append(result.getRewrittenQuestion())
                .append("\n");

        builder.append("是否发生重写：")
                .append(result.isRewritten())
                .append("\n");

        builder.append("重写原因：")
                .append(result.getReason())
                .append("\n");

        if (result.getFilledSlots() != null && !result.getFilledSlots().isEmpty()) {
            builder.append("补充槽位：")
                    .append(String.join(",", result.getFilledSlots()))
                    .append("\n");
        }

        builder.append("处理建议：后续进行工具选择、RAG 查询或预约草稿生成时，应优先参考系统重写问题；但不能编造重写中没有出现的业务数据。\n");

        return builder.toString();
    }
}
