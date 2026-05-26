package com.sau.gym.admin.agent.intent;

import com.sau.gym.admin.agent.intent.classifier.AgentIntentClassifier;
import com.sau.gym.admin.agent.memory.AgentBusinessContext;
import com.sau.gym.admin.enums.AgentIntent;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 作者:hfj
 * 功能:基于规则的意图识别器
 * 作用:
 * 通过关键词 + 当前上下文判断用户意图。
 * 为什么第一版用规则:
 * 1. 体育场馆预约场景的意图比较固定。
 * 2. 规则识别速度快，不消耗大模型 Token。
 * 3. 结果可解释，方便写论文/简历/答辩。
 * 4. 出错时容易定位是哪个关键词或规则导致。
 * 日期: 2026/5/26 10:55
 */
@Component
public class RuleBasedAgentIntentClassifier implements AgentIntentClassifier {

    /**
     * 预约相关关键词。
     */
    private static final List<String> BOOKING_KEYWORDS = Arrays.asList(
            "预约", "预定", "预订", "订场", "订一下", "帮我订", "帮我约",
            "我要约", "我要订", "约这个", "订这个", "预约这个场地", "预约当前场地"
    );

    /**
     * 确认相关关键词。
     */
    private static final List<String> CONFIRM_KEYWORDS = Arrays.asList(
            "确认预约", "确认下单", "确认取消预约", "确认取消"
    );

    /**
     * 取消预约相关关键词。
     */
    private static final List<String> CANCEL_BOOKING_KEYWORDS = Arrays.asList(
            "取消预约", "取消订单", "退订", "不去了", "帮我取消", "取消我的预约",
            "撤销预约", "预约取消"
    );

    /**
     * 查询可预约时段相关关键词。
     */
    private static final List<String> SLOT_QUERY_KEYWORDS = Arrays.asList(
            "有没有空", "还有空", "空场", "空闲", "可预约", "能约吗",
            "什么时候能约", "几点有空", "剩余时段", "可用时段", "空余时间"
    );

    /**
     * 查询订单/预约记录相关关键词。
     */
    private static final List<String> ORDER_QUERY_KEYWORDS = Arrays.asList(
            "我的预约", "预约记录", "我的订单", "订单记录", "我订的",
            "查看订单", "查看预约", "历史预约", "预约详情"
    );

    /**
     * RAG 知识库问答关键词。
     */
    private static final List<String> RAG_KEYWORDS = Arrays.asList(
            "规则", "退款", "退费", "取消规则", "预约规则", "怎么取消",
            "开放时间", "营业时间", "停车", "设施", "注意事项", "公告",
            "FAQ", "常见问题", "价格说明", "收费规则", "使用说明"
    );

    /**
     * 场馆查询关键词。
     */
    private static final List<String> VENUE_QUERY_KEYWORDS = Arrays.asList(
            "场馆", "体育馆", "有哪些馆", "有什么馆", "附近", "在哪里",
            "地址", "位置", "导航", "开放的馆"
    );

    /**
     * 场地查询关键词。
     */
    private static final List<String> COURT_QUERY_KEYWORDS = Arrays.asList(
            "场地", "篮球场", "羽毛球场", "足球场", "网球场", "乒乓球",
            "几号场", "1号场", "2号场", "3号场", "这个场地"
    );

    /**
     * 商城相关关键词。
     */
    private static final List<String> SHOPPING_KEYWORDS = Arrays.asList(
            "商城", "商品", "购买", "买", "下单", "购物车", "球拍", "羽毛球",
            "篮球", "水", "饮料", "运动装备"
    );

    /**
     * 闲聊关键词。
     */
    private static final List<String> SMALL_TALK_KEYWORDS = Arrays.asList(
            "你好", "您好", "你是谁", "你能做什么", "谢谢", "感谢", "再见"
    );

    /**
     * 场地类型关键词。
     * 用于判断用户虽然没有给 courtId，但是否至少说了想预约什么类型的场地。
     */
    private static final List<String> COURT_TYPE_KEYWORDS = Arrays.asList(
            "篮球", "篮球场", "羽毛球", "羽毛球场", "足球", "足球场",
            "网球", "网球场", "乒乓球"
    );

    @Override
    public IntentRouteResult classify(IntentRouteRequest request) {
        String rawMessage = request == null ? null : request.getMessage();

        // 1. 消息为空，直接返回不确定意图。
        if (!StringUtils.hasText(rawMessage)) {
            return IntentRouteResult.clarify(
                    "用户消息为空",
                    "你可以告诉我想查询场馆、预约场地，还是咨询预约规则。"
            );
        }

        // 2. 标准化文本。
        // 当前先做简单标准化，后面可以接入 HanLP 或你已有的 AgentNlpService。
        String text = normalize(rawMessage);

        // 3. 确认类指令优先识别。
        // 注意: 当前项目 AgentServiceImpl#handlePendingAction 已经会优先处理确认类指令。
        // 这里识别它主要是为了 Trace 和后续统一路由。
        String confirmKeyword = hitKeyword(text, CONFIRM_KEYWORDS);
        if (confirmKeyword != null) {
            return buildResult(
                    AgentIntent.BOOKING_CONFIRM,
                    0.98,
                    "命中确认类关键词",
                    confirmKeyword,
                    request
            );
        }

        // 4. 知识问答特征前置判断。
        // 例如“取消预约后可以退款吗”不是要取消预约，而是问退款规则。
        if (looksLikeKnowledgeQuestion(text)) {
            String ragKeyword = hitKeyword(text, RAG_KEYWORDS);
            if (ragKeyword != null) {
                return buildResult(
                        AgentIntent.RAG_KNOWLEDGE,
                        0.90,
                        "命中知识库问答特征，优先按规则咨询处理",
                        ragKeyword,
                        request
                );
            }
        }


        // 5. 取消预约类意图优先级很高。
        String cancelKeyword = hitKeyword(text, CANCEL_BOOKING_KEYWORDS);
        if (cancelKeyword != null) {
            return buildResult(
                    AgentIntent.BOOKING_CANCEL,
                    0.95,
                    "命中取消预约关键词",
                    cancelKeyword,
                    request
            );
        }

        // 6. 查询订单/预约记录。
        // 这类操作通常涉及用户个人数据，需要登录。
        String orderKeyword = hitKeyword(text, ORDER_QUERY_KEYWORDS);
        if (orderKeyword != null) {
            return buildResult(
                    AgentIntent.ORDER_QUERY,
                    0.92,
                    "命中订单/预约记录查询关键词",
                    orderKeyword,
                    request
            );
        }

        // 7. 查询可预约时段。
        // 用户只是问有没有空，不一定要立刻创建预约草稿。
        String slotKeyword = hitKeyword(text, SLOT_QUERY_KEYWORDS);
        if (slotKeyword != null) {
            return buildResult(
                    AgentIntent.SLOT_QUERY,
                    0.90,
                    "命中可预约时段查询关键词",
                    slotKeyword,
                    request
            );
        }

        // 8. 创建预约草稿。
        // 这里要注意: 预约草稿不等于真实预约，真实预约仍然要用户确认。
        String bookingKeyword = hitKeyword(text, BOOKING_KEYWORDS);
        if (bookingKeyword != null) {

            // 如果用户说“预约一下”，但没有当前页面场馆/场地，也没有提到场地类型，
            // 那么直接追问，避免大模型胡乱猜测。
            if (bookingInfoTooWeak(text, request)) {
                return IntentRouteResult.clarify(
                        "用户有预约意图，但缺少场馆/场地信息",
                        "你想预约哪个场馆或哪类场地？例如：羽毛球场、篮球场，或者先进入某个场地详情页再说“预约这个场地”。"
                );
            }

            return buildResult(
                    AgentIntent.BOOKING_DRAFT,
                    0.93,
                    "命中预约关键词",
                    bookingKeyword,
                    request
            );
        }

        // 9. RAG 知识库问答。
        // 规则、退款、开放时间、停车、设施这类问题，不应该走预约工具。
        String ragKeyword = hitKeyword(text, RAG_KEYWORDS);
        if (ragKeyword != null) {
            return buildResult(
                    AgentIntent.RAG_KNOWLEDGE,
                    0.88,
                    "命中知识库问答关键词",
                    ragKeyword,
                    request
            );
        }

        // 10. 商城意图。
        // 注意: “买羽毛球场”这种句子不常见，“买羽毛球”更可能是商品。
        String shoppingKeyword = hitKeyword(text, SHOPPING_KEYWORDS);
        if (shoppingKeyword != null && looksLikeShopping(text)) {
            return buildResult(
                    AgentIntent.SHOPPING_QUERY,
                    0.86,
                    "命中商城/商品关键词",
                    shoppingKeyword,
                    request
            );
        }

        // 11. 场地查询。
        // 放在场馆查询前面，因为“羽毛球场”也包含“场”，但它更像场地。
        String courtKeyword = hitKeyword(text, COURT_QUERY_KEYWORDS);
        if (courtKeyword != null) {
            return buildResult(
                    AgentIntent.COURT_QUERY,
                    0.82,
                    "命中场地查询关键词",
                    courtKeyword,
                    request
            );
        }

        // 12. 场馆查询。
        String venueKeyword = hitKeyword(text, VENUE_QUERY_KEYWORDS);
        if (venueKeyword != null) {
            return buildResult(
                    AgentIntent.VENUE_QUERY,
                    0.80,
                    "命中场馆查询关键词",
                    venueKeyword,
                    request
            );
        }

        // 13. 闲聊。
        String smallTalkKeyword = hitKeyword(text, SMALL_TALK_KEYWORDS);
        if (smallTalkKeyword != null) {
            return buildResult(
                    AgentIntent.SMALL_TALK,
                    0.75,
                    "命中闲聊关键词",
                    smallTalkKeyword,
                    request
            );
        }

        // 14. 无法判断，返回 UNSURE。
        // 这类问题不要硬猜，最好交给大模型结合上下文判断，或者追问。
        return IntentRouteResult.of(
                AgentIntent.UNSURE,
                0.30,
                "未命中明确业务关键词",
                null
        );
    }

    /**
     * 判断用户是否在咨询规则，而不是要执行真实业务动作。
     *
     * 示例:
     * “取消预约后可以退款吗” 是问规则，不是要取消。
     * “帮我取消预约” 才是真正取消。
     */
    private boolean looksLikeKnowledgeQuestion(String text) {
        if (!StringUtils.hasText(text)) {
            return false;
        }

        return text.contains("吗")
                || text.contains("么")
                || text.contains("怎么")
                || text.contains("如何")
                || text.contains("能不能")
                || text.contains("可以")
                || text.contains("规则")
                || text.contains("退款")
                || text.contains("说明");
    }

    /**
     * 构造意图识别结果，并补充调试信息。
     */
    private IntentRouteResult buildResult(
            AgentIntent intent,
            double confidence,
            String reason,
            String matchedKeyword,
            IntentRouteRequest request
    ) {
        IntentRouteResult result = IntentRouteResult.of(intent, confidence, reason, matchedKeyword);

        if (request != null) {
            result.getDebugInfo().put("effectiveVenueId", request.getEffectiveVenueId());
            result.getDebugInfo().put("effectiveCourtId", request.getEffectiveCourtId());

            AgentBusinessContext context = request.getBusinessContext();
            if (context != null) {
                result.getDebugInfo().put("lastVenueId", context.getLastVenueId());
                result.getDebugInfo().put("lastCourtId", context.getLastCourtId());
                result.getDebugInfo().put("lastBookingDate", context.getLastBookingDate());
                result.getDebugInfo().put("lastStartTime", context.getLastStartTime());
                result.getDebugInfo().put("lastEndTime", context.getLastEndTime());
                result.getDebugInfo().put("lastIntent", context.getLastIntent());
            }
        }

        return result;
    }

    /**
     * 判断用户预约信息是否过弱。
     *
     * 什么叫过弱:
     * 1. 用户有预约意图。
     * 2. 但没有当前场馆ID。
     * 3. 也没有当前场地ID。
     * 4. 文本里也没有提到篮球/羽毛球/足球等场地类型。
     *
     * 这种情况下如果直接交给大模型，很容易让模型编造场馆或场地。
     */
    private boolean bookingInfoTooWeak(String text, IntentRouteRequest request) {
        boolean hasVenueId = request != null && request.getEffectiveVenueId() != null;
        boolean hasCourtId = request != null && request.getEffectiveCourtId() != null;
        boolean hasCourtType = hitKeyword(text, COURT_TYPE_KEYWORDS) != null;

        return !hasVenueId && !hasCourtId && !hasCourtType;
    }

    /**
     * 判断当前文本是否更像商城问题。
     *
     * 为什么需要这个方法:
     * “羽毛球”既可能是场地类型，也可能是商品。
     * 如果用户说“买羽毛球”，更像商城。
     * 如果用户说“预约羽毛球场”，更像预约。
     */
    private boolean looksLikeShopping(String text) {
        return text.contains("买")
                || text.contains("购买")
                || text.contains("商品")
                || text.contains("商城")
                || text.contains("下单")
                || text.contains("购物车");
    }

    /**
     * 命中关键词则返回命中的关键词，否则返回 null。
     */
    private String hitKeyword(String text, List<String> keywords) {
        if (!StringUtils.hasText(text) || keywords == null || keywords.isEmpty()) {
            return null;
        }

        for (String keyword : keywords) {
            if (StringUtils.hasText(keyword) && text.contains(keyword)) {
                return keyword;
            }
        }

        return null;
    }

    /**
     * 标准化用户输入。
     *
     * 当前做轻量处理:
     * 1. 去掉首尾空格。
     * 2. 转小写。
     * 3. 把中文标点替换成空格。
     *
     * 后续可以增强:
     * 1. 同义词归一化，例如“预定”和“预约”统一。
     * 2. 接入 HanLP 分词。
     * 3. 接入时间解析。
     */
    private String normalize(String message) {
        return message == null
                ? ""
                : message.trim()
                .toLowerCase()
                .replace("，", " ")
                .replace("。", " ")
                .replace("？", " ")
                .replace("！", " ")
                .replace(",", " ")
                .replace(".", " ")
                .replace("?", " ")
                .replace("!", " ");
    }
}
