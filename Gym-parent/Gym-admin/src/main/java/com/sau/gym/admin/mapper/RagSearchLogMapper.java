package com.sau.gym.admin.mapper;

import com.sau.gym.model.entity.rag.RagSearchLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RagSearchLogMapper {

    /**
     * 插入 RAG 检索日志。
     *
     * @param log 检索日志
     * @return 影响行数
     */
    int insert(RagSearchLog log);

    /**
     * 根据 traceId 查询 RAG 检索日志。
     * 一个 Agent Trace 里可能触发多次 RAG 检索，
     * 所以这里返回 List。
     */
    List<RagSearchLog> selectByTraceId(@Param("traceId") String traceId);
}
