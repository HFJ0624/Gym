package com.sau.gym.admin.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 作者:hfj
 * 功能:自定义的minio配置类
 * 日期: 2026/3/6 15:02
 */
@Data
@ConfigurationProperties(prefix="gym.minio") //读取节点
public class MinioProperties {

    private String endpointUrl;
    private String accessKey;
    private String secreKey;
    private String bucketName;
}
