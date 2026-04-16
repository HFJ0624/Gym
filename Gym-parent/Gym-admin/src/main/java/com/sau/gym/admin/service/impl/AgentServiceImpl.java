package com.sau.gym.admin.service.impl;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sau.gym.admin.mapper.*;
import com.sau.gym.admin.service.AgentService;
import com.sau.gym.model.entity.chat.ChatRecord;
import com.sau.gym.model.entity.notice.Notice;
import com.sau.gym.model.entity.user.User;
import com.sau.gym.model.entity.venue.CourtBooking;
import com.sau.gym.model.entity.venue.Venue;
import com.sau.gym.model.vo.venue.VenueCommentVO;
import org.eclipse.jetty.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    private CourtBookingMapper courtBookingMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private VenueCommentMapper venueCommentMapper;

    @Autowired
    private ChatRecordMapper chatRecordMapper;

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
            String systemPrompt = "你是健身房专属智能客服，严格遵守以下规则：\n" +
                    "1. 只回答健身房相关问题（场馆、预约、公告、评价、会员）\n" +
                    "2. 必须使用【真实数据】回答，禁止编造信息\n" +
                    "3. 记住用户之前的对话内容，上下文连贯\n" +
                    "4. 回答简洁友好、口语化\n" +
                    "5. 无答案时回复：暂无相关信息，请咨询管理员\n\n" +
                    "【真实知识库】：\n" + knowledge;

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
            return aiReply;

        } catch (Exception e) {
            e.printStackTrace();
            return "AI服务异常，请稍后再试";
        }
    }
}
