package com.example.payment_system.service;

import com.example.payment_system.entity.Order;
import com.example.payment_system.entity.User;
import com.example.payment_system.mapper.OrderMapper;
import com.example.payment_system.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class PayService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderCountService orderCountService;

    //事务保证扣钱与生成订单统一性
    @Transactional(rollbackFor = Exception.class)
    public String doPay(Long userId, BigDecimal amount, String ip, String description) {
        User user = userMapper.findById(userId);
        if (user == null) {
            return "user_not_found";
        }
        if (user.getBalance().compareTo(amount) < 0) {
            return "insufficient_balance";
        }

        int affected = userMapper.deductBalance(userId, amount);
        if (affected == 0) {
            return "insufficient_balance";
        }
        Order order = new Order();
        order.setUserId(userId.intValue());
        order.setAmount(amount);
        order.setStatus("SUCCESS");
        order.setDescription(description);
        orderMapper.insert(order);
        orderCountService.recordOrder(userId);
        return "success";
    }

    @Transactional
    public boolean recharge(Long userId, BigDecimal amount) {
        User user = userMapper.findById(userId);
        if (user == null) {
            return false;
        }
        return userMapper.addBalance(userId, amount) > 0;
    }
}
