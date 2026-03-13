package com.middleware.test.checker;

import com.middleware.test.CheckResult;
import com.middleware.test.MiddlewareConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class PostgresChecker implements MiddlewareChecker {

    @Override
    public String getName() {
        return "postgresql";
    }

    @Override
    public CheckResult check(MiddlewareConfig config) {
        String host     = config.getString("host", "127.0.0.1");
        int    port     = config.getInt("port", 5432);
        String database = config.getString("database", "postgres");
        String username = config.getString("username", "postgres");
        String password = config.getString("password", "");
        int    timeout  = config.getInt("timeout", 2000);

        String url = String.format("jdbc:postgresql://%s:%d/%s", host, port, database);

        Properties props = new Properties();
        props.setProperty("user", username);
        props.setProperty("password", password != null ? password : "");
        props.setProperty("connectTimeout", String.valueOf(timeout / 1000));  // 单位：秒
        props.setProperty("socketTimeout",  String.valueOf(timeout / 1000));

        long start = System.currentTimeMillis();
        try (Connection conn = DriverManager.getConnection(url, props);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT VERSION()")) {
            String version = rs.next() ? rs.getString(1) : "unknown";
            long elapsed = System.currentTimeMillis() - start;
            return CheckResult.success("postgresql", "VERSION = " + version, elapsed);
        } catch (Exception e) {
            return CheckResult.failure("postgresql", e.getMessage());
        }
    }
}
