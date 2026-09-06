package com.example.payment_system.config;

import com.google.common.util.concurrent.RateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    private final ConcurrentHashMap<String, RateLimiter> limiters = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //ip限制请求频次（5次/s）
        String ip = request.getRemoteAddr();
        RateLimiter limiter = limiters.computeIfAbsent(ip, k -> RateLimiter.create(5.0));
        if (!limiter.tryAcquire()) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"msg\":\"请求过于频繁，请稍后重试\"}");
            //返回429
            response.setStatus(429);
            return false;
        }
        return true;
    }
}
