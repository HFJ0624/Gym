package com.sau.gym.admin.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 作者:hfj
 * 功能:创建一个 Spring 上下文工具类
 * 日期: 2026/4/13 16:14
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    // 改成静态方法
    public static ApplicationContext getContext() {
        return context;
    }

    // 关键：加 null 判断
    public static <T> T getBean(Class<T> clazz) {
        if (context == null) {
            throw new RuntimeException("Spring context 未初始化！");
        }
        return context.getBean(clazz);
    }
}
