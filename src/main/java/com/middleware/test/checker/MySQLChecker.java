package com.middleware.test.checker;

import com.middleware.test.CheckResult;
import com.middleware.test.MiddlewareConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MySQLChecker implements MiddlewareChecker {

    @Override
    public String getName() {
        return "mysql";
    }

    @Override
    public CheckResult check(MiddlewareConfig config) {
        String host     = config.getString("host", "127.0.0.1");
        int    port     = config.getInt("port", 3306);
        String database = config.getString("database", "mysql");
        String username = config.getString("username", "root");
        String password = config.getString("password", "");
        int    timeout  = config.getInt("timeout", 2000);

        String url = String.format(
                "jdbc:mysql://%s:%d/%s?connectTimeout=%d&socketTimeout=%d&useSSL=false",
                host, port, database, timeout, timeout);

        long start = System.currentTimeMillis();
        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT VERSION()")) {
            String version = rs.next() ? rs.getString(1) : "unknown";
            long elapsed = System.currentTimeMillis() - start;
            return CheckResult.success("mysql", "VERSION = " + version, elapsed);
        } catch (Exception e) {
            return CheckResult.failure("mysql", e.getMessage());
        }
    }
}
