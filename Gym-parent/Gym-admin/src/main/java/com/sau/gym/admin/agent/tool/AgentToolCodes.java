package com.sau.gym.admin.agent.tool;

/**
 * 作者:hfj
 * 功能:Agent 工具编码常量类
 * 作用:
 * 统一维护所有工具编码，避免在代码里到处写字符串。
 * 日期: 2026/5/27 16:31
 */
public final class AgentToolCodes {

    /**
     * 私有构造方法，防止工具类被实例化。
     */
    private AgentToolCodes() {
    }

    /**
     * RAG 知识库问答工具。
     */
    public static final String ASK_GYM_KNOWLEDGE = "ask_gym_knowledge";

    /**
     * 生成预约草稿工具。
     */
    public static final String CREATE_BOOKING_DRAFT = "create_booking_draft";

    /**
     * 确认预约工具。
     */
    public static final String CONFIRM_BOOKING = "confirm_booking";

    /**
     * 取消预约工具。
     */
    public static final String CANCEL_BOOKING = "cancel_booking";

    /**
     * 查询可预约时段工具。
     */
    public static final String QUERY_AVAILABLE_SLOT = "query_available_slot";

    /**
     * 查询场馆工具。
     */
    public static final String QUERY_VENUE = "query_venue";

    /**
     * 查询场地工具。
     */
    public static final String QUERY_COURT = "query_court";

    /**
     * 查询订单/预约记录工具。
     */
    public static final String QUERY_ORDER = "query_order";

    /**
     * 商城查询工具。
     */
    public static final String SHOPPING_QUERY = "shopping_query";
}
