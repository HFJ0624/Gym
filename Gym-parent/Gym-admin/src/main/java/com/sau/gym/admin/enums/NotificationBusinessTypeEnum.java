package com.sau.gym.admin.enums;

/***
 * 业务类型枚举
 */
public enum NotificationBusinessTypeEnum {

    BOOKING_ORDER(1, "预约订单"),
    MALL_ORDER(2, "商城订单"),
    NOTICE(3, "公告");

    private final Integer code;
    private final String desc;

    NotificationBusinessTypeEnum(Integer code, String desc) {
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
