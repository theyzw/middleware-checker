package com.middleware.test;

import java.util.Collections;
import java.util.Map;

/**
 * 单个中间件的连接参数，通过 getString / getInt 方法取值。
 */
public class MiddlewareConfig {

    private final Map<String, Object> params;

    private MiddlewareConfig(Map<String, Object> params) {
        this.params = params;
    }

    public String getString(String key) {
        Object val = params.get(key);
        return val != null ? val.toString() : null;
    }

    public String getString(String key, String defaultValue) {
        String val = getString(key);
        return (val != null && !val.isEmpty()) ? val : defaultValue;
    }

    public int getInt(String key, int defaultValue) {
        Object val = params.get(key);
        if (val instanceof Number) return ((Number) val).intValue();
        if (val instanceof String) {
            try {
                return Integer.parseInt((String) val);
            } catch (NumberFormatException ignored) {
            }
        }
        return defaultValue;
    }

    @SuppressWarnings("unchecked")
    public static MiddlewareConfig fromMap(Object raw) {
        if (raw instanceof Map) {
            return new MiddlewareConfig((Map<String, Object>) raw);
        }
        return new MiddlewareConfig(Collections.emptyMap());
    }
}
