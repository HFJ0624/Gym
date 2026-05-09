package com.sau.gym.model.entity.rag;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 作者:hfj
 * 功能:RAG 评估用例实体
 * 日期: 2026/5/9 19:26
 */
@Data
public class RagEvalCase {

    private Long id;

    /**
     * 标准测试问题。
     */
    private String question;

    /**
     * 期望命中的知识文档ID，多个用英文逗号分隔。
     */
    private String expectedDocIds;

    /**
     * 期望命中的关键词，多个用英文逗号分隔。
     */
    private String expectedKeywords;

    /**
     * 是否期望无答案：0否 1是。
     */
    private Integer expectedNoAnswer;

    /**
     * 问题分类。
     */
    private String category;

    /**
     * 是否启用：1启用 0禁用。
     */
    private Integer enabled;

    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    private Integer isDeleted;
}
