package com.sau.gym.admin;

import com.sau.gym.admin.properties.MinioProperties;
import com.sau.gym.admin.properties.UserAuthProperties;
import com.sau.gym.common.log.annotation.EnableLogAspect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 作者:hfj
 * 功能:后端的主启动类
 * 日期: 2026/3/3 22:44
 */
@EnableLogAspect
@SpringBootApplication
@ComponentScan(basePackages = {"com.sau.gym"})
@EnableConfigurationProperties(value = {UserAuthProperties.class, MinioProperties.class}) //自定义配置类的注解
@EnableScheduling //开启定时任务注解
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class,args);
    }
}
