package com.sau.gym.admin.service.impl;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sau.gym.admin.mapper.*;
import com.sau.gym.admin.service.AgentService;
import com.sau.gym.admin.service.CourtBookingService;
import com.sau.gym.admin.service.OrderService;
import com.sau.gym.model.dto.order.OrderDto;
import com.sau.gym.model.dto.venue.BookingDto;
import com.sau.gym.model.entity.chat.ChatRecord;
import com.sau.gym.model.entity.notice.Notice;
import com.sau.gym.model.entity.shopping.Beverage;
import com.sau.gym.model.entity.shopping.Cart;
import com.sau.gym.model.entity.user.User;
import com.sau.gym.model.entity.venue.CourtBooking;
import com.sau.gym.model.entity.venue.Venue;
import com.sau.gym.model.vo.court.CourtVO;
import com.sau.gym.model.vo.venue.VenueCommentVO;
import com.sau.gym.utils.AuthContextUtil;
import jakarta.annotation.PostConstruct;
import org.eclipse.jetty.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/30 20:48
 */
@Service
public class AgentServiceImpl implements AgentService {

    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private VenueMapper venueMapper;

    @Autowired
    private CourtBookingMapper courtBookingMapper; //预约业务

    @Autowired
    private CourtMapper courtMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private VenueCommentMapper venueCommentMapper;

    @Autowired
    private ChatRecordMapper chatRecordMapper;

    @Autowired
    private BeverageMapper beverageMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private OrderService orderService; //订单业务

    @Autowired
    private CourtBookingService courtBookingService;

    @Value("${doubao.api-key}")
    private String API_KEY;

    @Value("${doubao.api-url}")
    private String API_URL;

    // 记忆条数：保留最近10条对话（防止token超限）
    private static final int MEMORY_LIMIT = 10;

    /**
     * 升级版智能聊天：记忆功能 + 功能增强 + 聊天记录入库
     * @param userId 用户ID（必须！用于区分记忆）
     * @param userMessage 用户消息
     * @return AI回复
     */
    @Override
    public String chat(Long userId, String userMessage) {
        try {
            // ===================== 1. 查询真实业务数据 =====================
            List<Notice> noticeList = noticeMapper.findAllNotice();
            List<Venue> venueList = venueMapper.findAllVenue();
            List<CourtBooking> bookingList = courtBookingMapper.countAllBook();
            List<User> userList = userMapper.findByPage(null);
            List<VenueCommentVO> commentList = venueCommentMapper.findByPage(null);

            // 按场馆统计：总评论数、好评数（4-5分）
            Map<String, Integer> totalCommentMap = new HashMap<>();
            Map<String, Integer> goodCommentMap = new HashMap<>();
            for (VenueCommentVO vo : commentList) {
                String name = vo.getVenueName();
                Integer esteem = vo.getEsteem();
                if (name == null || StringUtil.isBlank(name)) continue;

                totalCommentMap.put(name, totalCommentMap.getOrDefault(name, 0) + 1);
                if (esteem != null && esteem >= 4) {
                    goodCommentMap.put(name, goodCommentMap.getOrDefault(name, 0) + 1);
                }
            }

            // ===================== 2. 【增强版】构建健身房知识库 =====================
            StringBuilder knowledge = new StringBuilder();
            knowledge.append("【健身房实时真实数据】\n");
            knowledge.append("👥 用户总数：").append(userList.size()).append(" 人\n");
            knowledge.append("🏟️  场馆总数：").append(venueList.size()).append(" 个\n");
            knowledge.append("📅 预约总数：").append(bookingList.size()).append(" 单\n\n");

            // 最新公告
            knowledge.append("📢 最新公告：\n");
            for (Notice n : noticeList) {
                knowledge.append("- ").append(n.getTitle()).append("：").append(n.getContent()).append("\n");
            }

            // 场馆信息（好评率+排序）
            knowledge.append("\n🏆 场馆信息与好评率：\n");
            for (Venue v : venueList) {
                String name = v.getVenueName();
                int total = totalCommentMap.getOrDefault(name, 0);
                int good = goodCommentMap.getOrDefault(name, 0);
                double rate = total == 0 ? 0 : (good * 100.0 / total);
                knowledge.append("- ").append(name)
                        .append(" | 好评率：").append(String.format("%.1f%%", rate))
                        .append(" | 地址：").append(v.getLocation()).append("\n");
            }

            // ===================== 3. 核心：上下文记忆（加载用户历史对话） =====================
            List<Map<String, String>> messages = new ArrayList<>();
            // 系统提示词（增强版规则）
            String systemPrompt = "你是体育场馆智能助手，支持：聊天咨询、场馆预约、商城下单（购物车结算）\n" +
                    "==================== 预约场馆 强制3步流程 ====================\n" +
                    "必须严格按顺序追问，**缺少参数绝对不能生成JSON**：\n" +
                    "第1步：询问用户【要预约的体育场馆名称】\n" +
                    "第2步：询问用户【要预约的体育场馆场地名称】\n" +
                    "第3步：询问用户【预约日期，格式：yyyy-MM-dd】\n" +
                    "\n" +
                    "==================== 规则说明 ====================\n" +
                    "1. 预约必填3个参数：venueName(场馆名)、courtName(场地名称)、date(日期)\n" +
                    "2. 商城下单必填：productName(商品名)、quantity(数量)\n" +
                    "3. 参数不全**只能自然语言追问**，禁止生成JSON\n" +
                    "4. 参数齐全后，**仅输出纯JSON**，不要任何多余文字\n" +
                    "\n" +
                    "==================== JSON输出格式 ====================\n" +
                    "预约场馆：{\"action\":\"booking\",\"params\":{\"venueName\":\"\",\"courtName\":\"\",\"date\":\"\"}}\n" +
                    "商城下单：{\"action\":\"shopping\",\"params\":{\"productName\":\"\",\"quantity\":1}}\n" +
                    "\n" +
                    "【健身房真实知识库】：\n" + knowledge;

            // 添加系统角色
            Map<String, String> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", systemPrompt);
            messages.add(systemMsg);

            // 加载用户历史记忆 → 加入对话上下文
            List<ChatRecord> historyList = chatRecordMapper.selectUserRecentHistory(userId, MEMORY_LIMIT);
            // 倒序插入（保证对话顺序正确）
            for (int i = historyList.size() - 1; i >= 0; i--) {
                ChatRecord record = historyList.get(i);
                // 用户历史消息
                Map<String, String> userHistory = new HashMap<>();
                userHistory.put("role", "user");
                userHistory.put("content", record.getUserMessage());
                messages.add(userHistory);
                // AI历史回复
                Map<String, String> aiHistory = new HashMap<>();
                aiHistory.put("role", "assistant");
                aiHistory.put("content", record.getAiReply());
                messages.add(aiHistory);
            }

            // 添加当前用户消息
            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            messages.add(userMsg);

            // ===================== 4. 调用豆包AI =====================
            JSONObject body = new JSONObject();
            body.put("model", "doubao-seed-1-8-251228");
            body.put("messages", messages);
            body.put("temperature", 0.1); // 低温度=更严谨（适合客服）

            String resp = HttpRequest.post(API_URL)
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .body(body.toJSONString())
                    .execute()
                    .body();

            JSONObject res = JSON.parseObject(resp);
            String aiReply;
            if (res.containsKey("choices")) {
                aiReply = res.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");
            } else {
                aiReply = "AI服务异常：" + resp;
            }

            // 6. 解析AI指令，执行业务
            String finalReply = aiReply;
            try {
                String jsonStr = aiReply.trim();
                if (jsonStr.startsWith("{")) {
                    JSONObject toolCall = JSON.parseObject(jsonStr);
                    String action = toolCall.getString("action");
                    JSONObject params = toolCall.getJSONObject("params");
                    if ("booking".equals(action)){
                        finalReply = handleVenueBooking(userId, params);
                    }
                    if ("shopping".equals(action)){
                        finalReply = handleShopping(userId, params);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // ===================== 5. 聊天记录入库（持久化） =====================
            ChatRecord record = new ChatRecord();
            record.setUserId(userId);
            User user = userMapper.selectById(userId);
            record.setUsername(user.getUsername());
            record.setSessionId(UUID.randomUUID().toString()); // 自动生成会话ID
            record.setUserMessage(userMessage);
            record.setAiReply(aiReply);
            record.setCreateTime(LocalDateTime.now());
            chatRecordMapper.insertChatRecord(record);

            //返回回复内容
            return finalReply;

        } catch (Exception e) {
            e.printStackTrace();
            return "AI服务异常，请稍后再试";
        }
    }

    // ================== 【无感购物车】自动加购+自动结算 ==================
    @Transactional
    public String handleShopping(Long userId, JSONObject params) {
        try {
            String productName = params.getString("productName");
            Integer quantity = params.getInteger("quantity");

            // 1. 根据商品名查数据库商品信息
            Beverage beverage = beverageMapper.selectByName(productName);
            if (beverage == null) {
                return "❌ 下单失败：未找到商品【" + productName + "】，请确认商品名称";
            }

            // 2. 校验库存（先检查，避免白加购物车）
            if (beverage.getStock() < quantity) {
                return "❌ 下单失败：【" + productName + "】库存不足，当前库存：" + beverage.getStock();
            }

            // 3.校验是否上架
            if (beverage.getStatus() == 2){
                return "❌ 下单失败：商品【" + productName + "】已下架";
            }

            // 3. 【核心】自动后台插入购物车
            Cart autoCart = new Cart();
            autoCart.setUserId(userId);
            autoCart.setGoodsId(beverage.getId());
            autoCart.setGoodsName(beverage.getGoodsName());
            autoCart.setPrice(beverage.getPrice());
            autoCart.setQuantity(quantity);
            autoCart.setImage(beverage.getImage()); // 假设Beverage有image字段
            cartMapper.insert(autoCart); // 插入购物车，获取自增ID

            // 4. 封装 OrderDto（只用刚才自动生成的这一个 cartId）
            OrderDto orderDto = new OrderDto();
            orderDto.setCartIds(Collections.singletonList(autoCart.getId()));
            orderDto.setRemark("AI智能下单-自动加购");

            // ================== 【修复空指针】手动将用户放入上下文 ==================
            User user = userMapper.selectById(userId);
            AuthContextUtil.set(user);

            // 5. 调用你原有的下单方法（扣余额+减库存+清购物车）
            orderService.CreateShoppingOrder(orderDto);

            return "🛒 下单成功！\n" +
                    "商品：" + beverage.getGoodsName() + "\n" +
                    "数量：" + quantity + "\n" +
                    "总价：" + beverage.getPrice().multiply(BigDecimal.valueOf(quantity)) + "\n" +
                    "已自动结算，余额/库存已更新！";

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ 下单失败：" + e.getMessage();
        }
    }

    // ================== 【预约】场馆+场地类型+日期 自动扣费 ==================
    @Transactional
    public String handleVenueBooking(Long userId, JSONObject params) {
        try {
            // 接收3个必填参数
            String venueName = params.getString("venueName");
            String courtName = params.getString("courtName");
            String dateStr = params.getString("date");

            // 1. 校验场馆是否存在
            Venue targetVenue = venueList.stream()
                    .filter(v -> v.getVenueName().equals(venueName))
                    .findFirst()
                    .orElse(null);
            if (targetVenue == null) {
                return "❌ 预约失败：不存在【" + venueName + "】这个场馆，请重新选择";
            }

            System.out.println("=============================================" + targetVenue);

            // 3. 查询场地 + 模糊匹配名称
            List<CourtVO> courts = courtMapper.getAllCourt(targetVenue.getId());
            if (courts == null || courts.isEmpty()) {
                return "❌ 预约失败：【" + venueName + "】暂无可用场地";
            }

            System.out.println("=============================================" + courts);

            // 模糊匹配场地（包含即匹配）
            CourtVO c = new CourtVO();
            for (CourtVO courtVO : courts){
                if (courtVO.getName().equals(courtName)){
                    c = courtVO;
                    break;
                }
            }
            if (StringUtil.isBlank(c.getName())) {
                return "❌ 预约失败：该【" + venueName + "】场馆的" + "【" + courtName + "】场地不存在";
            }

            // 3. 日期格式转换
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date bookingDate = sdf.parse(dateStr);


            // 4. 封装你的原生 BookingDto（完全兼容你的代码）
            BookingDto bookingDto = new BookingDto();
            bookingDto.setCourtId(c.getId());
            bookingDto.setCourtId(targetVenue.getId());
            bookingDto.setBookingDate(bookingDate);
            bookingDto.setUserId(userId);
            bookingDto.setTotalPrice(c.getPrice());
            bookingDto.setRemark("AI预约-" + venueName + "-" + c.getType());

            // 5. 调用你原有预约接口（自动扣余额、校验库存）
            courtBookingService.saveCourtBook(bookingDto);

            // 6. 友好返回结果
            return "✅ 预约成功！\n" +
                    "场馆：" + venueName + "\n" +
                    "场地：" + courtName + "\n" +
                    "场地类型：" + c.getType() + "\n" +
                    "日期：" + dateStr + "\n" +
                    "位置：" + targetVenue.getLocation() + "\n" +
                    "已自动扣除账户余额，预约生效！";

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ 预约异常：" + e.getMessage();
        }
    }

    // 懒加载场馆列表
    private List<Venue> venueList;

    @PostConstruct
    public void initVenue() {
        venueList = venueMapper.findAllVenue();
    }

}
