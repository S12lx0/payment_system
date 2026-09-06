package com.example.payment_system.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    //手动审计日志
    public void log(String operation, Long userId, String ip, String result, long costMs) {
        log.info("【审计日志】操作={}, userId={}, IP={}, 耗时={}ms, 结果={}",
                operation, userId, ip, costMs, result);
    }
}
