package com.example.payment_system.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RechargeRequest {
    private Long userId;
    private BigDecimal amount;
}
