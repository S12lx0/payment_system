package com.example.payment_system.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayRequest {
    private Long userId;
    private BigDecimal amount;
    private String ip;
    private String description;
}
