package com.example.payment_system.service;

import com.example.payment_system.entity.User;
import com.example.payment_system.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    // BCrypt密码加密
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private SmsService smsService;

    public boolean register(String username, String password,String phone,String code) {
        if (!smsService.verifyCode(phone,code)) {
            return false;
        }
        if (userMapper.findByPhone(phone) != null) {
            return false;
        }
        if (userMapper.findByUsername(username) != null) {
            return false;
        }

        User user = new User();
        user.setUsername(username);
        // 密码用BCrypt加密后存储
        user.setPassword(passwordEncoder.encode(password));
        user.setPhone(phone);
        user.setBalance(java.math.BigDecimal.ZERO);
        return userMapper.insert(user) > 0;
    }

    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        //对比明文密文
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }
}
