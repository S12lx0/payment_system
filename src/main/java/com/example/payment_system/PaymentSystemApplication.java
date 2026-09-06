package com.example.payment_system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.payment_system.mapper")
public class PaymentSystemApplication {
	public static void main(String[] args) {
		SpringApplication.run(PaymentSystemApplication.class, args);
	}
}
