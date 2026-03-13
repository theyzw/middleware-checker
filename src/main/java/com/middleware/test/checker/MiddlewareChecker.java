package com.middleware.test.checker;

import com.middleware.test.CheckResult;
import com.middleware.test.MiddlewareConfig;

public interface MiddlewareChecker {

    /** 对应 config.yaml 中的顶层 key（小写），如 "redis"、"mysql" */
    String getName();

    CheckResult check(MiddlewareConfig config);
}
