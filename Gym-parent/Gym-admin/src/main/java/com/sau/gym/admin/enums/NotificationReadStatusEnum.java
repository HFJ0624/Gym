package com.sau.gym.admin.enums;

/***
 * 通知阅读状态枚举类
 */
public enum NotificationReadStatusEnum {

    UNREAD(0, "未读"),
    READ(1, "已读");

    private final Integer code;
    private final String desc;

    NotificationReadStatusEnum(Integer code, String desc) {
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
