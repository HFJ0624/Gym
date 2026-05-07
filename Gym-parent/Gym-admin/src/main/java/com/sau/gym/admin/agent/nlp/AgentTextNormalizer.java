package com.sau.gym.admin.agent.nlp;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 作者:hfj
 * 功能:Agent 文本标准化工具
 * 作用：
 * 把用户口语化输入转换成更适合数据库匹配的文本。
 * 日期: 2026/5/6 21:05
 */
@Component
public class AgentTextNormalizer {

    /**
     * 通用同义词映射。
     *
     * 注意：
     * 这里不要把所有场馆别名都写死。
     * 具体场馆别名应该放数据库 alias 表。
     */
    private static final Map<String, String> SYNONYM_MAP = new HashMap<>();

    static {
        // 预约相关
        SYNONYM_MAP.put("预定", "预约");
        SYNONYM_MAP.put("预订", "预约");
        SYNONYM_MAP.put("订场", "预约");
        SYNONYM_MAP.put("约场", "预约");
        SYNONYM_MAP.put("帮我约", "预约");

        // 场地类型相关
        SYNONYM_MAP.put("羽球", "羽毛球");
        SYNONYM_MAP.put("羽毛球馆", "羽毛球场");
        SYNONYM_MAP.put("篮球馆", "篮球场");
        SYNONYM_MAP.put("足球馆", "足球场");

        // 常见场馆词
        SYNONYM_MAP.put("体育馆", "体育场馆");
    }

    /**
     * 标准化文本。
     *
     * @param text 用户原始输入
     * @return 标准化后的文本
     */
    public String normalize(String text) {
        if (text == null) {
            return "";
        }

        String result = text.trim();

        // 英文统一小写
        result = result.toLowerCase();

        // 去掉常见标点和空格
        result = result.replaceAll("[\\s，。,.！？!；;：:、（）()【】\\[\\]{}]", "");

        // 中文数字归一化
        result = normalizeChineseNumber(result);

        // 同义词替换
        for (Map.Entry<String, String> entry : SYNONYM_MAP.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }

        return result;
    }

    /**
     * 中文数字转阿拉伯数字。
     *
     * 当前主要处理场地编号，不做复杂中文数字解析。
     */
    private String normalizeChineseNumber(String text) {
        if (text == null) {
            return "";
        }

        return text
                .replace("一号", "1号")
                .replace("二号", "2号")
                .replace("三号", "3号")
                .replace("四号", "4号")
                .replace("五号", "5号")
                .replace("六号", "6号")
                .replace("七号", "7号")
                .replace("八号", "8号")
                .replace("九号", "9号")
                .replace("十号", "10号")
                .replace("第一", "1")
                .replace("第二", "2")
                .replace("第三", "3")
                .replace("第四", "4")
                .replace("第五", "5")
                .replace("第六", "6")
                .replace("第七", "7")
                .replace("第八", "8")
                .replace("第九", "9")
                .replace("第十", "10");
    }
}
