package com.sau.gym.admin.enums;

/**
 * RAG知识来源类型枚举
 * 用于进一步描述知识内容属于哪一类。
 */
public enum KnowledgeSourceTypeEnum {

    PLATFORM_RULE(1, "平台规则"),
    BOOKING_RULE(2, "预约规则"),
    REFUND_RULE(3, "退款规则"),

    VENUE_INTRO(4, "场馆介绍"),
    VENUE_FACILITY(5, "场馆设施"),
    VENUE_PARKING(6, "停车说明"),
    VENUE_OPEN_TIME(7, "开放时间"),

    COURT_INTRO(8, "场地介绍"),
    COURT_FACILITY(9, "场地设施"),
    COURT_PRICE(10, "场地价格"),

    NOTICE(11, "公告"),
    FAQ(12, "常见问题");

    private final Integer code;
    private final String desc;

    KnowledgeSourceTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
