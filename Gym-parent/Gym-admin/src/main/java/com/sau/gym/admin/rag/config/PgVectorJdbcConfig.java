package com.sau.gym.admin.rag.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * 作者:hfj
 * 功能:pgvector的PostgreSQL数据源配置
 * RAG 向量删除、增量索引用这个 PostgreSQL JdbcTemplate。
 * 日期: 2026/5/6 14:33
 */
@Configuration
public class PgVectorJdbcConfig {

    @Value("${gym.rag.pgvector.datasource.jdbc-url}")
    private String jdbcUrl;

    @Value("${gym.rag.pgvector.datasource.username}")
    private String username;

    @Value("${gym.rag.pgvector.datasource.password}")
    private String password;

    @Value("${gym.rag.pgvector.datasource.driver-class-name}")
    private String driverClassName;

    /**
     * pgvector 专用 JdbcTemplate。
     *
     * 这个 JdbcTemplate 只用于：
     * DELETE FROM gym_knowledge WHERE metadata ->> 'docId' = ?
     *
     * 不参与 MyBatis，不影响 MySQL 主数据源。
     */
    @Bean(name = "pgVectorJdbcTemplate")
    public JdbcTemplate pgVectorJdbcTemplate() {
        HikariDataSource dataSource = new HikariDataSource();

        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);

        /**
         * pgvector 删除旧向量只需要少量连接。
         */
        dataSource.setMaximumPoolSize(3);
        dataSource.setMinimumIdle(1);
        dataSource.setPoolName("PgVectorHikariPool");

        return new JdbcTemplate(dataSource);
    }
}
