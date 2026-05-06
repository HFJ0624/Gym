package com.sau.gym.admin.agent.parser;

import com.sau.gym.admin.agent.model.BookingTimeInfo;
import com.sau.gym.admin.agent.model.TimeRange;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 作者:hfj
 * 功能:预约时间解析器
 * 作用：
 * 从用户自然语言中解析预约日期、开始时间、结束时间。
 * 支持示例：
 * 1. 2026-05-08 19:00:00 到 20:00:00
 * 2. 2026-05-08 19:00 到 20:00
 * 3. 2026-05-08 19点到20点
 * 4. 明天晚上7点到8点
 * 5. 后天 14:00 到 16:00
 * 注意：
 * 这个解析器只做第一版简单规则解析。
 * 如果解析失败，返回 null，让请求继续交给大模型 Agent。
 * 日期: 2026/5/6 8:52
 */
@Component
public class BookingTimeParser {

    /**
     * 从用户输入中解析预约时间信息。
     *
     * @param message 用户输入
     * @return 解析成功返回 BookingTimeInfo，失败返回 null
     */
    public BookingTimeInfo parse(String message) {
        if (message == null || message.trim().isEmpty()) {
            return null;
        }

        String text = message.trim();

        LocalDate date = parseDate(text);
        if (date == null) {
            return null;
        }

        TimeRange timeRange = parseTimeRange(text);
        if (timeRange == null) {
            return null;
        }

        BookingTimeInfo info = new BookingTimeInfo();
        info.setDate(date.toString());
        info.setStartTime(timeRange.getStartTime());
        info.setEndTime(timeRange.getEndTime());

        return info;
    }

    /***
     *
     * @param text 预约日期
     * @return 解析日期
     */
    private LocalDate parseDate(String text) {
        Pattern datePattern = Pattern.compile("(\\d{4}-\\d{1,2}-\\d{1,2})");
        Matcher dateMatcher = datePattern.matcher(text);

        if (dateMatcher.find()) {
            try {
                return LocalDate.parse(dateMatcher.group(1));
            } catch (Exception e) {
                return null;
            }
        }

        LocalDate today = LocalDate.now();

        if (text.contains("今天")) {
            return today;
        }

        if (text.contains("明天")) {
            return today.plusDays(1);
        }

        if (text.contains("后天")) {
            return today.plusDays(2);
        }

        return null;
    }

    /***
     *
     * @param text 预约时间段
     * @return 解析时间段
     */
    private TimeRange parseTimeRange(String text) {
        boolean pmContext = text.contains("晚上")
                || text.contains("下午")
                || text.contains("傍晚");

        //采用正则表达式匹配
        Pattern colonPattern = Pattern.compile(
                "(\\d{1,2})(?::(\\d{1,2}))?(?::(\\d{1,2}))?\\s*(?:到|至|-|~)\\s*(\\d{1,2})(?::(\\d{1,2}))?(?::(\\d{1,2}))?"
        );

        Matcher colonMatcher = colonPattern.matcher(text);

        if (colonMatcher.find()) {
            String start = normalizeTime(
                    colonMatcher.group(1),
                    colonMatcher.group(2),
                    colonMatcher.group(3),
                    pmContext
            );

            String end = normalizeTime(
                    colonMatcher.group(4),
                    colonMatcher.group(5),
                    colonMatcher.group(6),
                    pmContext
            );

            if (isValidTimeRange(start, end)) {
                return new TimeRange(start, end);
            }
        }

        Pattern pointPattern = Pattern.compile(
                "(\\d{1,2})点\\s*(?:到|至|-|~)\\s*(\\d{1,2})点"
        );

        Matcher pointMatcher = pointPattern.matcher(text);

        if (pointMatcher.find()) {
            String start = normalizeTime(
                    pointMatcher.group(1),
                    null,
                    null,
                    pmContext
            );

            String end = normalizeTime(
                    pointMatcher.group(2),
                    null,
                    null,
                    pmContext
            );

            if (isValidTimeRange(start, end)) {
                return new TimeRange(start, end);
            }
        }

        return null;
    }

    /***
     *
     * @param hourText 小时
     * @param minuteText 分钟
     * @param secondText 秒钟
     * @param pmContext 判断标准
     * @return 标准化时间
     */
    private String normalizeTime(String hourText,
                                 String minuteText,
                                 String secondText,
                                 boolean pmContext) {
        int hour = Integer.parseInt(hourText);
        int minute = minuteText == null ? 0 : Integer.parseInt(minuteText);
        int second = secondText == null ? 0 : Integer.parseInt(secondText);

        //晚上/下午语境下：7点一般指 19点。
        if (pmContext && hour < 12) {
            hour += 12;
        }

        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    /**
     * 校验时间段是否合法。
     */
    private boolean isValidTimeRange(String startTime, String endTime) {
        try {
            LocalTime start = LocalTime.parse(startTime);
            LocalTime end = LocalTime.parse(endTime);

            return start.isBefore(end);
        } catch (Exception e) {
            return false;
        }
    }
}
