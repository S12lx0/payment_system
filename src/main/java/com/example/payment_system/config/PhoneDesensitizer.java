package com.example.payment_system.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

public class PhoneDesensitizer extends JsonSerializer<String> {
    @Override
    public void serialize(String phone, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        if (phone == null || phone.length() < 11) {
            gen.writeString(phone);
            return;
        }
        gen.writeString(phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4));
    }
}
