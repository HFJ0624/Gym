package com.sau.gym.admin.service.impl;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sau.gym.admin.mapper.*;
import com.sau.gym.admin.service.AgentService;
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

    @Value("${doubao.api-key}")
    private String API_KEY;

    @Value("${doubao.api-url}")
    private String API_URL;

    //构建agent智能聊天
    @Override
    public String chat(String userMessage) {
        try {
            // 1. 查询真实数据
            List<Notice> noticeList = noticeMapper.findAllNotice();
            List<Venue> venueList = venueMapper.findAllVenue();
            List<CourtBooking> courtBookingList = courtBookingMapper.countAllBook();
            List<User> userList = userMapper.findByPage(null);
            List<VenueCommentVO> venueCommentVOList = venueCommentMapper.findByPage(null);

            // ===================== 核心：按场馆名称统计好评 =====================
            Map<String, Integer> totalCommentMap = new HashMap<>();  // 场馆名 -> 总评论数
            Map<String, Integer> goodCommentMap = new HashMap<>();   // 场馆名 -> 好评数(5分)

            for (VenueCommentVO vo : venueCommentVOList) {
                String venueName = vo.getVenueName();
                Integer esteem = vo.getEsteem();
                // 跳过空数据
                if (venueName == null || StringUtil.isBlank(venueName)) continue;

                // 总评论数 +1
                totalCommentMap.put(venueName, totalCommentMap.getOrDefault(venueName, 0) + 1);

                //
                if (esteem != null && esteem >= 4) {
                    goodCommentMap.put(venueName, goodCommentMap.getOrDefault(venueName, 0) + 1);
                }
            }

            // 2. 构建健身房知识库
            StringBuilder knowledge = new StringBuilder();
            knowledge.append("【健身房真实信息】\n");
            knowledge.append("用户总数：").append(userList.size()).append("\n");
            knowledge.append("场馆总数：").append(venueList.size()).append("\n");
            knowledge.append("预约总数：").append(courtBookingList.size()).append("\n\n");

            knowledge.append("最新公告：\n");
            for (Notice n : noticeList) {
                knowledge.append("- ").append(n.getTitle()).append("\n");
            }

            // 场馆 + 好评率（AI参考依据）
            knowledge.append("\n=== 场馆信息（含好评率）===\n");
            for (Venue v : venueList) {
                String name = v.getVenueName();
                int total = totalCommentMap.getOrDefault(name, 0);
                int good = goodCommentMap.getOrDefault(name, 0);
                double rate = total == 0 ? 0 : (good * 100.0 / total);

                knowledge.append("- ").append(name)
                        .append(" | 好评率：").append(String.format("%.1f", rate)).append("%")
                        .append(total == 0 ? "（暂无评价）" : "")
                        .append("\n");
            }

            // 3. 系统提示词
            String systemPrompt =
            "你是一个专业的健身房管理系统智能客服助手。\n" +
                    "你的任务是：基于提供的【健身房真实信息】回答用户的问题。\n\n" +
                    "回答规则：\n" +
                    "1. 只回答与健身房相关的问题（场馆、预约、公告、会员、课程等）。\n" +
                    "2. 不要编造任何不在知识库中的信息。\n" +
                    "3. 如果不知道答案，回答“暂无相关信息”，并礼貌建议咨询管理员。\n" +
                    "4. 回答要简洁、口语化、友好、有耐心。\n" +
                    "5. 信息来源于知识库，不要依赖外部知识。\n\n" +
                    "【健身房真实信息如下】：\n" + knowledge;

            // 4. 调用豆包
            JSONObject body = new JSONObject();
            body.put("model", "doubao-seed-1-8-251228");

            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", systemPrompt);

            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);

            messages.add(systemMsg);
            messages.add(userMsg);
            body.put("messages", messages);

            // 发送请求
            String resp = HttpRequest.post(API_URL)
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .body(body.toJSONString())
                    .execute()
                    .body();

            // 安全解析（修复空指针）
            JSONObject res = JSON.parseObject(resp);
            System.out.println("豆包返回：" + resp);

            if (res.containsKey("choices")) {
                return res.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");
            } else {
                return "AI服务异常：" + resp;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "AI服务异常，请稍后再试";
        }
    }
}
