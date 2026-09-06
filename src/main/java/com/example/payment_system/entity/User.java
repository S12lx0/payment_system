package com.example.payment_system.entity;

import com.example.payment_system.config.PhoneDesensitizer;
import lombok.Data;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class User {
    private Integer id;
    private String username;
    private String password;
    private BigDecimal balance;
    private LocalDateTime createdAt;

    //保证手机号在返回json时脱敏
    @JsonSerialize(using = PhoneDesensitizer.class)
    private String phone;
}

