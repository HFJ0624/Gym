package com.sau.gym.admin.agent.store;

/**
 * 草稿类型枚举
 * 用来区分当前待确认的草稿是哪一种业务：
 * - BOOKING：预约草稿
 * - SHOPPING：商城下单草稿
 */
public enum PendingDraftType {
    BOOKING,
    SHOPPING
}
