package com.example.payment_system.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public class XssFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // 将请求包装成XSS安全的包装器
        HttpServletRequest req = (HttpServletRequest) request;
        chain.doFilter(new XssHttpServletRequestWrapper(req), response);
    }
}
