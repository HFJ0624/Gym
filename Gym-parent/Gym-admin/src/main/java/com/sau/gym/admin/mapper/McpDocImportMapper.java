package com.sau.gym.admin.mapper;

import com.sau.gym.model.entity.rag.RagImportMcpDoc;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface McpDocImportMapper {

    /**
     * 插入导入记录。
     *
     * @return 影响行数
     */
    int insertImportRecord(RagImportMcpDoc ragImportMcpDoc);
}
