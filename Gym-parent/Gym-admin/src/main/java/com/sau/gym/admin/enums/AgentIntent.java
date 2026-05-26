package com.sau.gym.admin.enums;

public enum AgentIntent {

    /**
     * 查询场馆。
     * 示例:
     * - 有哪些体育馆
     * - 附近有什么场馆
     * - 学校体育馆在哪
     */
    VENUE_QUERY(
            "查询场馆",
            "query_venue",
            false
    ),

    /**
     * 查询场地。
     * 示例:
     * - 有哪些篮球场
     * - 1号羽毛球场怎么样
     * - 这个场馆有什么场地
     */
    COURT_QUERY(
            "查询场地",
            "query_court",
            false
    ),

    /**
     * 查询可预约时间段。
     * 示例:
     * - 明晚还有空场吗
     * - 今天7点到9点有没有空
     * - 羽毛球场什么时候能约
     */
    SLOT_QUERY(
            "查询可预约时段",
            "query_available_slot",
            false
    ),

    /**
     * 生成预约草稿。
     * 注意:
     * 这里只是生成草稿，不是真正下单。
     * 真正预约必须等用户确认。
     *
     * 示例:
     * - 帮我预约这个场地
     * - 明晚7点到8点订一下
     * - 我要预约羽毛球场
     */
    BOOKING_DRAFT(
            "生成预约草稿",
            "create_booking_draft",
            true
    ),

    /**
     * 确认预约。
     * 示例:
     * - 确认预约
     * - 确认预约 ABC123
     *
     * 当前项目里这类指令已经在 AgentServiceImpl#handlePendingAction 中处理。
     */
    BOOKING_CONFIRM(
            "确认预约",
            "confirm_booking",
            true
    ),

    /**
     * 取消预约。
     * 示例:
     * - 取消我的预约
     * - 我不去了，帮我取消
     * - 取消明天的羽毛球预约
     */
    BOOKING_CANCEL(
            "取消预约",
            "cancel_booking",
            true
    ),

    /**
     * 查询订单或预约记录。
     * 示例:
     * - 我的预约记录
     * - 我订了哪个场地
     * - 查看我的订单
     */
    ORDER_QUERY(
            "查询订单/预约记录",
            "query_order",
            true
    ),

    /**
     * RAG 知识库问答。
     * 示例:
     * - 预约规则是什么
     * - 取消后能退款吗
     * - 体育馆可以停车吗
     * - 开放时间是什么
     *
     * 当前项目里 GymRagTools 已经有 askGymKnowledge 工具。
     */
    RAG_KNOWLEDGE(
            "知识库问答",
            "ask_gym_knowledge",
            false
    ),

    /**
     * 商城相关。
     * 示例:
     * - 买瓶水
     * - 我要买羽毛球拍
     * - 查看商城商品
     */
    SHOPPING_QUERY(
            "商城相关",
            "shopping_query",
            true
    ),

    /**
     * 普通闲聊。
     * 示例:
     * - 你好
     * - 你是谁
     * - 你能做什么
     */
    SMALL_TALK(
            "闲聊",
            "small_talk",
            false
    ),

    /**
     * 不确定意图。
     * 示例:
     * - 那个呢
     * - 帮我弄一下
     * - 这个可以吗
     *
     * 这类问题通常需要结合上下文，或者直接追问用户。
     */
    UNSURE(
            "不确定意图",
            "clarify",
            false
    );

    /**
     * 给人看的中文描述。
     */
    private final String description;

    /**
     * 建议走的工具名称。
     * 这里先用逻辑名称，不强绑定具体 Java 方法。
     * 后续做统一工具注册器时，可以直接用这个字段匹配工具。
     */
    private final String suggestedToolName;

    /**
     * 该意图是否默认需要登录。
     * 例如查询规则不需要登录，但查询我的预约、生成预约草稿需要登录。
     */
    private final boolean needLogin;

    AgentIntent(String description, String suggestedToolName, boolean needLogin) {
        this.description = description;
        this.suggestedToolName = suggestedToolName;
        this.needLogin = needLogin;
    }

    public String getDescription() {
        return description;
    }

    public String getSuggestedToolName() {
        return suggestedToolName;
    }

    public boolean isNeedLogin() {
        return needLogin;
    }
}
