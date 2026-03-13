package com.middleware.test;

public class CheckResult {

    private final String name;
    private final boolean success;
    private final String message;
    private final long latencyMs;

    private CheckResult(String name, boolean success, String message, long latencyMs) {
        this.name = name;
        this.success = success;
        this.message = message;
        this.latencyMs = latencyMs;
    }

    public static CheckResult success(String name, String message, long latencyMs) {
        return new CheckResult(name, true, message, latencyMs);
    }

    public static CheckResult failure(String name, String message) {
        return new CheckResult(name, false, message, -1);
    }

    public String getName()     { return name; }
    public boolean isSuccess()  { return success; }
    public String getMessage()  { return message; }
    public long getLatencyMs()  { return latencyMs; }
}
