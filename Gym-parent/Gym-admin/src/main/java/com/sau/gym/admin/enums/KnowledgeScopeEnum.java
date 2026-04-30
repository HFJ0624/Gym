package com.sau.gym.admin.enums;

/**
 * RAG 知识范围枚举类
 * 用于区分一条知识是平台级、场馆级、场地级、公告级还是 FAQ。
 */
public enum KnowledgeScopeEnum {

    PLATFORM(1, "平台级知识"),
    VENUE(2, "场馆级知识"),
    COURT(3, "场地级知识"),
    NOTICE(4, "公告级知识"),
    FAQ(5, "常见问题");

    private final Integer code;
    private final String desc;

    KnowledgeScopeEnum(Integer code, String desc) {
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
