package com.middleware.test.checker;

import com.middleware.test.CheckResult;
import com.middleware.test.MiddlewareConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.UUID;

public class RedisChecker implements MiddlewareChecker {

    @Override
    public String getName() {
        return "redis";
    }

    @Override
    public CheckResult check(MiddlewareConfig config) {
        String host = config.getString("host", "127.0.0.1");
        int port = config.getInt("port", 6379);
        int timeout = config.getInt("timeout", 2000);
        String password = config.getString("password");

        long start = System.currentTimeMillis();
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(1);

        int database = config.getInt("database", 0);

        try (JedisPool pool = new JedisPool(poolConfig, host, port, timeout,
                (password != null && !password.isEmpty()) ? password : null, database)) {
            try (Jedis jedis = pool.getResource()) {
                String pong = jedis.ping();

                String key = "mw-check:" + UUID.randomUUID();
                String value = UUID.randomUUID().toString();
                jedis.set(key, value);
                String got = jedis.get(key);
                jedis.del(key);
                boolean rwOk = value.equals(got);

                long elapsed = System.currentTimeMillis() - start;
                return CheckResult.success("redis", "PING => " + pong + ", SET/GET/DEL => " + (rwOk ? "OK" : "FAIL"), elapsed);
            }
        } catch (Exception e) {
            return CheckResult.failure("redis", e.getMessage());
        }
    }
}
