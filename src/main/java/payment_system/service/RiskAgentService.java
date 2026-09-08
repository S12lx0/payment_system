package com.example.payment_system.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
public class RiskAgentService {

    @Value("${deepseek.api.key}")
    private String apiKey;

    @Value("${deepseek.api.url}")
    private String apiUrl;

    @Autowired
    private OrderCountService orderCountService;

    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 【核心方法】Agent决策入口
     * 返回：BLOCK / SMS_VERIFY / ALLOW
     */
    public String getAgentAction(Long userId, BigDecimal amount, String ip) {
        // 硬编码安全护栏
        // 规则1：大额或IP异常，直接拦
        if (amount.compareTo(BigDecimal.valueOf(5000)) >= 0 || !ip.startsWith("192.168.1.")) {
            log.info("【安全护栏触发】大额或非白名单IP，直接BLOCK");
            return "BLOCK";
        }
        // 规则2：中额高频，强制短信验证
        int count = orderCountService.getOrderCount(userId);
        if (amount.compareTo(BigDecimal.valueOf(2000)) >= 0 && count >= 5) {
            log.info("【安全护栏触发】中额高频，强制SMS_VERIFY");
            return "SMS_VERIFY";
        }

        // 护栏通过，AI辅助推理
        try {
            // 构造提示词
            String prompt = buildAgentPrompt(userId, amount, ip, count);
            // 调用DeepSeek API接口
            String response = callDeepSeek(prompt);
            // 解析AI返回的JSON，提取action字段
            JsonNode node = objectMapper.readTree(response);
            String action = node.path("action").asText("ALLOW");
            log.info("Agent下发指令：{}", action);
            return action;
        } catch (Exception e) {
            // AI挂了或超时，降级放行
            log.error("Agent调用失败，降级为ALLOW（放行）", e);
            return "ALLOW";
        }
    }

    //构造Prompt
    private String buildAgentPrompt(Long userId, BigDecimal amount, String ip, int count) {
        return String.format(
                "你是一个支付风控Agent。请根据规则输出JSON指令，不要包含任何其他文字。\n" +
                        "规则：\n" +
                        "1. 时间异常：如果交易时间在凌晨 00:00-05:00，且金额 > 1500，输出 {\"action\":\"SMS_VERIFY\", \"reason\":\"深夜大额\"}\n" +
                        "2. 金额敏感：如果金额是 4999、3999、2999 这类“刚好低于5000”的试探性数字，输出 {\"action\":\"SMS_VERIFY\", \"reason\":\"试探性大额\"}\n" +
                        "3. 正常情况：其他所有情况，输出 {\"action\":\"ALLOW\", \"reason\":\"正常\"}\n" +
                        "当前交易：用户%d，金额%.2f元，IP=%s，近1小时下单%d次\n" +
                        "只返回JSON：",
                userId, amount, ip, count
        );
    }

    //调用DeepSeek API
    private String callDeepSeek(String prompt) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("model", "deepseek-chat");
        body.put("stream", false);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", "你是风控Agent，只输出JSON指令。"));
        messages.add(Map.of("role", "user", "content", prompt));
        body.put("messages", messages);

        String json = objectMapper.writeValueAsString(body);

        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(json, MediaType.parse("application/json")))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String resp = response.body().string();
            if (!response.isSuccessful()) {
                log.error("AI接口失败，状态码：{}", response.code());
                return "{\"action\":\"ALLOW\"}"; // 降级
            }
            return resp;
        }
    }
}
