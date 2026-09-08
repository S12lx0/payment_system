package com.example.payment_system.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;

public class XssSafeStringDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String raw = p.getValueAsString();
        if (raw == null) {
            return null;
        }

        // 获取当前正在反序列化的字段名
        String fieldName = p.currentName();

        // 身份标识符（用户名、手机号）和密码——原样返回，绝对不转义
        if ("password".equals(fieldName) || "username".equals(fieldName) || "phone".equals(fieldName)) {
            return raw;
        }

        // 其余字段（描述、签名等）做 HTML 转义
        return HtmlUtils.htmlEscape(raw);
    }
}
