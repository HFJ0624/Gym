package com.sau.gym.admin.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 作者:hfj
 * 功能:自定义的用户配置类
 * 日期: 2026/3/5 9:35
 */
@Data
@ConfigurationProperties(prefix = "gym.auth")// 前缀不能使用驼峰命名
public class UserAuthProperties {
    private List<String> noAuthUrls;
}
