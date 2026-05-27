package com.sau.gym.admin.enums;

/**
 * 草稿类型枚举
 * 用来区分当前待确认的草稿是哪一种业务;
 */
public enum PendingDraftType {

    //场地预约草稿
    BOOKING,

    //商城下单草稿
    SHOPPING,

    //取消预约草稿
    CANCEL_BOOKING
}
