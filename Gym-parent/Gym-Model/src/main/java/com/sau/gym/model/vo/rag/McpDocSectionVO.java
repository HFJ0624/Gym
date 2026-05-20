package com.sau.gym.model.vo.rag;

import lombok.Data;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/5/20 8:28
 */
@Data
public class McpDocSectionVO {

    /**
     * 章节ID。
     */
    private String sectionId;

    /**
     * 章节标题。
     */
    private String title;

    /**
     * 章节完整内容。
     */
    private String content;

    /**
     * 内容预览。
     */
    private String preview;

    /**
     * 字符数。
     */
    private Integer length;
}
