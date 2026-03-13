package com.middleware.test;

import com.middleware.test.checker.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 入口：按 config.yaml 中 check 列表依次测试各中间件连接状态。
 *
 * 用法：
 *   java -jar middleware-test.jar [config路径]   （默认读 classpath 下 config.yaml）
 */
public class Main {

    public static void main(String[] args) throws Exception {
        String configPath = args.length > 0 ? args[0] : "config.yaml";

        AppConfig appConfig = AppConfig.load(configPath);

        // 注册所有支持的检测器，key 与 config.yaml 中的中间件名称对应（小写）
        Map<String, MiddlewareChecker> checkers = new LinkedHashMap<>();
        register(checkers, new RedisChecker());
        register(checkers, new MySQLChecker());
        register(checkers, new PostgresChecker());

        System.out.println("========== 中间件连接测试 ==========");
        System.out.printf("配置文件: %s%n%n", configPath);

        if (appConfig.getCheckList().isEmpty()) {
            System.out.println("check 列表为空，没有需要测试的中间件。");
            System.out.println("请在 config.yaml 的 check 下添加要测试的中间件名称。");
            return;
        }

        for (String name : appConfig.getCheckList()) {
            String key = name.toLowerCase();
            MiddlewareChecker checker = checkers.get(key);
            if (checker == null) {
                System.out.printf("[WARN] %-12s 未找到对应的检测器，请确认名称拼写%n", name);
                continue;
            }

            MiddlewareConfig config = appConfig.getConfig(key);
            CheckResult result = checker.check(config);
            if (result.isSuccess()) {
                System.out.printf("[ OK ] %-12s %s  (耗时 %dms)%n",
                        name, result.getMessage(), result.getLatencyMs());
            } else {
                System.out.printf("[FAIL] %-12s %s%n", name, result.getMessage());
            }
        }

        System.out.println("\n========== 测试完成 ==========");
    }

    private static void register(Map<String, MiddlewareChecker> map, MiddlewareChecker checker) {
        map.put(checker.getName(), checker);
    }
}
