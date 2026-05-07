package com.sau.gym.admin.agent.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/5/6 21:02
 */
@Data
@Schema(description = "Agent实体解析结果")
public class AgentEntityResolveResult {

    /**
     * 是否成功解析到有效实体。
     */
    private boolean resolved;

    /**
     * 是否存在歧义。
     *
     * true 表示有多个候选都比较像，系统不应该直接确定，
     * 应该让 Agent 追问用户进一步选择。
     */
    private boolean ambiguous;

    /**
     * 解析到的场馆ID。
     */
    private Long venueId;

    /**
     * 解析到的场馆名称。
     */
    private String venueName;

    /**
     * 解析到的场地ID。
     */
    private Long courtId;

    /**
     * 解析到的场地名称。
     */
    private String courtName;

    /**
     * 解析到的场地类型。
     */
    private String courtType;

    /**
     * 匹配分数。
     */
    private int score;

    /**
     * 歧义候选。
     */
    private List<String> candidates = new ArrayList<>();

    /**
     * 未解析成功时返回空结果。
     */
    public static AgentEntityResolveResult empty() {
        AgentEntityResolveResult result = new AgentEntityResolveResult();
        result.setResolved(false);
        result.setAmbiguous(false);
        result.setScore(0);
        return result;
    }
}
