package com.sau.gym.model.dto.rag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/30 15:48
 */
@Data
@Schema(description = "用户RAG问答请求")
public class RagAskDto {

    @Schema(description = "用户问题")
    private String question;

    /**
     * 当前场馆ID，可为空。
     * 如果用户在某个场馆详情页提问，前端可以传 venueId，
     * 后端会优先使用该场馆相关知识回答。
     */
    @Schema(description = "当前场馆ID")
    private Long venueId;

    /**
     * 当前场地ID，可为空。
     * 如果用户在某个场地详情页提问，前端可以传 courtId，
     * 后端会优先使用该场地相关知识回答。
     */
    @Schema(description = "当前场地ID")
    private Long courtId;
}
