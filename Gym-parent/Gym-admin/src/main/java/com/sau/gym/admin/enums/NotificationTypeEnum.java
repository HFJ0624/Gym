package com.sau.gym.admin.enums;

/***
 * 通知类型枚举类
 */
public enum NotificationTypeEnum {

    BOOKING(1, "预约通知"),
    PAYMENT(2, "支付通知"),
    REFUND(3, "退款通知"),
    NOTICE(4, "公告通知"),
    SYSTEM(5, "系统通知"),
    AGENT(6, "Agent通知"),
    MALL(7,"商城通知");



    private final Integer code;
    private final String desc;

    NotificationTypeEnum(Integer code, String desc) {
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
