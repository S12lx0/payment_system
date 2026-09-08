package com.example.payment_system.controller;

import com.example.payment_system.dto.LoginRequest;
import com.example.payment_system.dto.RegisterRequest;
import com.example.payment_system.dto.SendCodeRequest;
import com.example.payment_system.entity.User;
import com.example.payment_system.mapper.UserMapper;
import com.example.payment_system.service.SmsService;
import com.example.payment_system.service.UserService;
import com.example.payment_system.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SmsService smsService;

    @PostMapping("/sendCode")
    public String sendCode(@RequestBody SendCodeRequest request) {
        String phone = request.getPhone();

        if (userMapper.findByPhone(phone) != null) {
            return "手机号已被注册";
        }

        if (!phone.matches("^1[3-9]\\d{9}$")) {
            return "手机号格式不正确";
        }

        smsService.sendCode(phone);
        return "验证码已发送（模拟），请查看控制台";
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        boolean success = userService.register(
                request.getUsername(),
                request.getPassword(),
                request.getPhone(),
                request.getCode()
        );
        return success ? "注册成功！" : "注册失败：验证码错误或手机号已存在";
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        User user = userService.login(request.getUsername(), request.getPassword());
        if (user == null) {
            return "登录失败：用户名或密码错误";
        }
        // 登录成功，生成 JWT Token
        String token = JwtUtil.generate(user.getId().longValue());
        return "登录成功！用户ID：" + user.getId() + "，Token: " + token;
    }
}
