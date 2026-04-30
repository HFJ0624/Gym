package com.sau.gym.admin.mapper;

import com.sau.gym.model.entity.rag.KnowledgeDocument;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface KnowledgeDocumentMapper {

    /**
     * 新增知识文档。
     * @param document 知识文档
     * @return 影响行数
     */
    int insert(KnowledgeDocument document);

    /**
     * 查询所有启用的知识文档。
     * 这些文档会被切分并写入 pgvector。
     * @return 启用的知识文档列表
     */
    List<KnowledgeDocument> selectEnabledDocuments();

    /**
     * 标记知识文档已完成向量索引。
     * @param id 知识文档ID
     * @return 影响行数
     */
    int markIndexed(@Param("id") Long id);
}
