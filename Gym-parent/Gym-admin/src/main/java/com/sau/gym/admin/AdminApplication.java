package com.sau.gym.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 作者:hfj
 * 功能:后端的主启动类
 * 日期: 2026/3/3 22:44
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.sau.gym"})
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class,args);
    }
}
