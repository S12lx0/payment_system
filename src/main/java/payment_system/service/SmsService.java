package com.example.payment_system.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SmsService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String CODE_PREFIX = "sms:code:";

    //SecureRandom不可预测性更强，更有利于预防暴力破解
    private final SecureRandom secureRandom = new SecureRandom();

    public String sendCode(String phone) {
        String code = String.format("%06d", secureRandom.nextInt(1000000));
        String key = CODE_PREFIX + phone;

        //Redis设置5分钟验证码过期，模拟真实业务环境
        redisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);

        //采用控制台打印，整个项目的打印都是统一的，看起来更舒服
        log.info(" 验证码已发送至 " + phone + "：" + code);
        return code;
    }

    public boolean verifyCode(String phone, String code) {
        String key = CODE_PREFIX + phone;
        String saved = redisTemplate.opsForValue().get(key);
        return code != null && code.equals(saved);
    }
}
