package com.sau.gym.admin.controller.rag;

import com.sau.gym.admin.mapper.RagSearchLogMapper;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.rag.RagSearchLog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 作者:hfj
 * 功能:后台RAG检索日志
 * 日期: 2026/5/9 15:58
 */
@RestController
@RequestMapping("/admin/rag/searchLog")
public class AdminRagSearchLogController {

    private final RagSearchLogMapper ragSearchLogMapper;

    public AdminRagSearchLogController(RagSearchLogMapper ragSearchLogMapper) {
        this.ragSearchLogMapper = ragSearchLogMapper;
    }

    /**
     * 根据 traceId 查询 RAG 检索日志。
     */
    @GetMapping("/trace/{traceId}")
    public Result listByTraceId(@PathVariable String traceId) {
        List<RagSearchLog> list = ragSearchLogMapper.selectByTraceId(traceId);

        return Result.build(list, ResultCodeEnum.SUCCESS);
    }
}
