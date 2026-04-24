package com.sau.gym.admin.agent.tool;

import com.sau.gym.admin.mapper.NoticeMapper;
import com.sau.gym.admin.mapper.VenueMapper;
import com.sau.gym.model.entity.notice.Notice;
import com.sau.gym.model.entity.venue.Venue;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;
/**
 * 作者:hfj
 * 功能:查询场馆，查询公告工具
 * 日期: 2026/4/23 14:45
 */
@Component
public class GymQueryTools {

    private final VenueMapper venueMapper;
    private final NoticeMapper noticeMapper;

    public GymQueryTools(VenueMapper venueMapper, NoticeMapper noticeMapper) {
        this.venueMapper = venueMapper;
        this.noticeMapper = noticeMapper;
    }

    /***
     * 查询场馆工具
     * P表示参数描述，方便模型理解这个参数是什么
     * @param keyword 关键词
     * @return 返回匹配关键词的场馆列表
     */
    @Tool("查询场馆列表。可以按场馆关键词模糊匹配，返回场馆名称和地址。")
    public String queryVenues(@P(value = "场馆关键词，可为空", required = false) String keyword) {
        List<Venue> venueList = venueMapper.findAllVenue();

        // 如果用户给了关键词，就做简单过滤
        if (StringUtils.hasText(keyword)) {
            venueList = venueList.stream()
                    .filter(v -> v.getVenueName() != null && v.getVenueName().contains(keyword))
                    .collect(Collectors.toList());
        }

        if (venueList.isEmpty()) {
            return "当前没有查到符合条件的场馆。";
        }

        // 把结果拼成自然语言返回给模型
        StringBuilder sb = new StringBuilder("可选场馆如下：\n");
        int limit = Math.min(venueList.size(), 8);
        for (int i = 0; i < limit; i++) {
            Venue v = venueList.get(i);
            sb.append(i + 1)
                    .append(". ")
                    .append(v.getVenueName())
                    .append("，地址：")
                    .append(v.getLocation())
                    .append("\n");
        }
        return sb.toString();
    }

    /***
     * 查询公告工具
     * @return 返回最近若干条公告标题和内容
     */
    @Tool("查询最新公告，返回最近若干条公告标题和内容。")
    public String queryNotices() {
        List<Notice> noticeList = noticeMapper.findAllNotice();
        if (noticeList == null || noticeList.isEmpty()) {
            return "当前没有公告。";
        }

        StringBuilder sb = new StringBuilder("最新公告如下：\n");
        int limit = Math.min(noticeList.size(), 5);
        for (int i = 0; i < limit; i++) {
            Notice n = noticeList.get(i);
            sb.append(i + 1)
                    .append(". ")
                    .append(n.getTitle())
                    .append("：")
                    .append(n.getContent())
                    .append("\n");
        }
        return sb.toString();
    }
}
