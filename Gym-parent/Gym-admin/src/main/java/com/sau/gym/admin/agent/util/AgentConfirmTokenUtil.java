package com.sau.gym.admin.agent.util;

import java.security.SecureRandom;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/5/5 19:20
 */
public class AgentConfirmTokenUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    private AgentConfirmTokenUtil() {
    }

    //生成 6 位数字确认码。
    public static String generateToken() {
        int number = 100000 + RANDOM.nextInt(900000);
        return String.valueOf(number);
    }
}
