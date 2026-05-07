package com.sau.gym.admin.agent.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者:hfj
 * 功能:AgentNLP分析结果
 * 作用：
 * 保存对用户自然语言进行 NLP 处理后的结果。
 * 日期: 2026/5/6 21:11
 */
@Data
public class AgentNlpResult {

    /**
     * 用户原始输入。
     */
    private String rawText;

    /**
     * 标准化后的文本。
     */
    private String normalizedText;

    /**
     * HanLP 分词结果。
     */
    private List<String> terms = new ArrayList<>();

    /**
     * 识别到的场地类型关键词。
     * 例如：篮球场、羽毛球场、足球场。
     */
    private List<String> courtTypeKeywords = new ArrayList<>();

    /**
     * 识别到的场地编号。
     * 例如：1、2、3。
     */
    private List<String> courtNumbers = new ArrayList<>();
}
