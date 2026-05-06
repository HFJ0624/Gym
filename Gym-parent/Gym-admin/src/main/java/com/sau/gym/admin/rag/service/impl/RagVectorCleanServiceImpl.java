package com.sau.gym.admin.rag.service.impl;

import com.sau.gym.admin.rag.service.RagVectorCleanService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * 作者:hfj
 * 功能:RAG 向量清理服务实现
 * 用于删除 pgvector 中某个知识文档对应的旧向量。
 * 日期: 2026/5/6 14:42
 */
@Service
public class RagVectorCleanServiceImpl implements RagVectorCleanService {

    private static final Pattern SAFE_SQL_NAME = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*$");

    private final JdbcTemplate pgVectorJdbcTemplate;

    //pgvector 向量表名。
    @Value("${gym.rag.pgvector.table}")
    private String tableName;

    //metadata 字段名,默认 metadata。
    @Value("${gym.rag.pgvector.metadata-column:metadata}")
    private String metadataColumn;

    public RagVectorCleanServiceImpl(@Qualifier("pgVectorJdbcTemplate") JdbcTemplate pgVectorJdbcTemplate) {
        this.pgVectorJdbcTemplate = pgVectorJdbcTemplate;
    }

    /**
     * 删除某个 docId 的旧向量。
     */
    @Override
    public int deleteByDocId(Long docId) {
        if (docId == null) {
            return 0;
        }

        validateSqlName(tableName, "pgvector table");
        validateSqlName(metadataColumn, "metadata column");

        //这里用 metadata JSONB 删除。
        String sql = "DELETE FROM " + tableName
                + " WHERE " + metadataColumn + " ->> 'docId' = ?";

        return pgVectorJdbcTemplate.update(sql, String.valueOf(docId));
    }

    /**
     * 防止动态表名造成 SQL 注入。
     * 表名和字段名只能来自配置，但仍然做一次白名单校验。
     */
    private void validateSqlName(String value, String label) {
        if (value == null || !SAFE_SQL_NAME.matcher(value).matches()) {
            throw new RuntimeException(label + " 配置不合法：" + value);
        }
    }
}
