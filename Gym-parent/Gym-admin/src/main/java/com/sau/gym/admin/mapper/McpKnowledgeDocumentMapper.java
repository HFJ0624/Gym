package com.sau.gym.admin.mapper;

import com.sau.gym.model.entity.rag.KnowledgeDocument;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface McpKnowledgeDocumentMapper {

    /**
     * 插入 MCP 导入的知识文档。
     */
    int insertMcpKnowledgeDocument(KnowledgeDocument document);

    /**
     * 更新知识文档索引状态。
     *
     * @param id 知识文档ID
     * @param indexedStatus 索引状态：0 未索引，1 已索引
     */
    int updateIndexedStatus(@Param("id") Long id, @Param("indexedStatus") Integer indexedStatus);
}
