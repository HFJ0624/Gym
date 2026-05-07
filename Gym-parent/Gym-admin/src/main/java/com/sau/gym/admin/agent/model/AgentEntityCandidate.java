package com.sau.gym.admin.agent.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/5/6 21:04
 */
@Data
@Schema(description = "Agent实体候选对象")
public class AgentEntityCandidate {

    /**
     * 实体类型。
     *
     * VENUE：场馆
     * COURT：场地
     */
    private String type;

    /**
     * 场馆ID。
     *
     * 如果 type = VENUE，表示当前场馆 ID。
     * 如果 type = COURT，表示当前场地所属场馆 ID。
     */
    private Long venueId;

    /**
     * 场馆名称。
     */
    private String venueName;

    /**
     * 场地ID。
     */
    private Long courtId;

    /**
     * 场地名称。
     */
    private String courtName;

    /**
     * 场地类型。
     *
     * 例如：篮球场、羽毛球场。
     */
    private String courtType;

    /**
     * 用于匹配的原始名称。
     *
     * 可能是：
     * 1. 场馆真实名称
     * 2. 场馆别名
     * 3. 场地真实名称
     * 4. 场地别名
     */
    private String matchName;

    /**
     * 标准化后的匹配名称。
     */
    private String normalizedMatchName;

    /**
     * 是否来自别名表。
     *
     * true：来自 venue_alias / court_alias
     * false：来自 venue / court 真实名称
     */
    private Boolean alias;
}
