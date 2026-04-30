package com.sau.gym.admin.mapper;

import com.sau.gym.model.entity.rag.RagSearchLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RagSearchLogMapper {

    /**
     * 插入 RAG 检索日志。
     *
     * @param log 检索日志
     * @return 影响行数
     */
    int insert(RagSearchLog log);
}
