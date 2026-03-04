package com.sau.gym.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 作者:hfj
 * 功能:Swagger测试文档
 * 日期: 2026/3/4 15:01
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public GroupedOpenApi adminApi() {      // 创建了一个api接口的分组
        return GroupedOpenApi.builder()
                .group("admin-api")         // 分组名称
                .pathsToMatch("/admin/**")  // 接口请求路径规则
                .build();
    }

    /***
     * @description 自定义接口信息
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("体育场馆API文档接口")
                        .version("1.0")
                        .description("体育场馆API文档接口")
                        .contact(new Contact().name("黄锋杰"))); // 设定作者
    }
}
