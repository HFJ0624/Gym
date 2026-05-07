package com.sau.gym.admin.agent.nlp;

/**
 * 作者:hfj
 * 功能:简单模糊匹配工具
 * 当前使用 Levenshtein 编辑距离。
 * 日期: 2026/5/6 21:06
 */
public class FuzzyMatchUtils {

    private FuzzyMatchUtils() {
    }

    /**
     * 计算字符串相似度。
     *
     * @return 0 ~ 100，分数越高越相似
     */
    public static int similarity(String a, String b) {
        if (a == null || b == null) {
            return 0;
        }

        if (a.isEmpty() || b.isEmpty()) {
            return 0;
        }

        if (a.equals(b)) {
            return 100;
        }

        int distance = levenshtein(a, b);
        int maxLen = Math.max(a.length(), b.length());

        if (maxLen == 0) {
            return 100;
        }

        double score = 1.0 - ((double) distance / maxLen);

        return Math.max(0, (int) Math.round(score * 100));
    }

    /**
     * Levenshtein 编辑距离。
     *
     * 表示把字符串 a 转成字符串 b 需要的最少编辑次数。
     */
    private static int levenshtein(String a, String b) {
        int n = a.length();
        int m = b.length();

        int[][] dp = new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= m; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= n; i++) {
            char ca = a.charAt(i - 1);

            for (int j = 1; j <= m; j++) {
                char cb = b.charAt(j - 1);

                int cost = ca == cb ? 0 : 1;

                dp[i][j] = Math.min(
                        Math.min(
                                dp[i - 1][j] + 1,
                                dp[i][j - 1] + 1
                        ),
                        dp[i - 1][j - 1] + cost
                );
            }
        }

        return dp[n][m];
    }
}
