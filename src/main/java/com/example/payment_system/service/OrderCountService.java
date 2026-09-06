package com.example.payment_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class OrderCountService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "user:orders:";

    public void recordOrder(Long userId) {
        String key = KEY_PREFIX + userId;
        // 自增1
        redisTemplate.opsForValue().increment(key);
        // 设置过期时间为1小时（3600秒）
        redisTemplate.expire(key, 3600, TimeUnit.SECONDS);
    }

    public int getOrderCount(Long userId) {
        String key = KEY_PREFIX + userId;
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return 0;
        }
        return Integer.parseInt(value);
    }

    //测试的时候等不了1h自动清理，可手动清理
    public void clearOrderCount(Long userId) {
        String key = KEY_PREFIX + userId;
        redisTemplate.delete(key);
    }
}
