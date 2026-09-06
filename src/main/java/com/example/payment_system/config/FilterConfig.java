package com.example.payment_system.config;

import com.example.payment_system.filter.XssFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<XssFilter> xssFilterRegistration() {
        FilterRegistrationBean<XssFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new XssFilter());       // 注册过滤器
        registration.addUrlPatterns("/*");            // 拦截所有请求
        registration.setName("xssFilter");            // 命名
        registration.setOrder(1);                     // 数字越小越先执行
        return registration;
    }
}
