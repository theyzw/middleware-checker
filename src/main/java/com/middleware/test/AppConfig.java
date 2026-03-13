package com.middleware.test;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 加载 config.yaml，解析 check 列表和各中间件连接参数。
 */
public class AppConfig {

    private final List<String> checkList;
    private final Map<String, Object> rawMap;

    private AppConfig(List<String> checkList, Map<String, Object> rawMap) {
        this.checkList = checkList;
        this.rawMap = rawMap;
    }

    /** check: 下配置的中间件列表（保留注释行不会出现，已被 YAML 解析器过滤） */
    public List<String> getCheckList() {
        return checkList;
    }

    /** 获取指定中间件的连接参数，找不到时返回空配置 */
    public MiddlewareConfig getConfig(String name) {
        return MiddlewareConfig.fromMap(rawMap.get(name.toLowerCase()));
    }

    @SuppressWarnings("unchecked")
    public static AppConfig load(String configPath) throws IOException {
        Yaml yaml = new Yaml();
        Map<String, Object> raw;

        // 优先读取文件系统路径，找不到再从 classpath（resources）读
        if (Files.exists(Paths.get(configPath))) {
            try (InputStream is = Files.newInputStream(Paths.get(configPath))) {
                raw = yaml.load(is);
            }
        } else {
            try (InputStream is = AppConfig.class.getClassLoader().getResourceAsStream(configPath)) {
                if (is == null) throw new IOException("找不到配置文件: " + configPath);
                raw = yaml.load(is);
            }
        }

        if (raw == null) return new AppConfig(Collections.emptyList(), Collections.emptyMap());

        List<String> checkList = (List<String>) raw.getOrDefault("check", Collections.emptyList());
        return new AppConfig(checkList, raw);
    }
}
