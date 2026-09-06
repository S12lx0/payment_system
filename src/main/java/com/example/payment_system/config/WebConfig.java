package com.example.payment_system.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private RateLimitInterceptor rateLimitInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 限流（先执行）
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/api/pay/doPay")
                .order(0);

        // JWT认证（后执行）
        registry.addInterceptor(new JwtInterceptor())
                .addPathPatterns("/api/pay/**", "/api/user/**")
                .excludePathPatterns("/api/user/login", "/api/user/register", "/api/user/sendCode")
                .order(1);
    }
}
