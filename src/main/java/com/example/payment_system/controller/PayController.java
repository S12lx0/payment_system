package com.example.payment_system.controller;

import com.example.payment_system.dto.PayRequest;
import com.example.payment_system.dto.RechargeRequest;
import com.example.payment_system.entity.User;
import com.example.payment_system.mapper.UserMapper;
import com.example.payment_system.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/pay")
public class PayController {

    @Autowired
    private RiskAgentService riskAgentService;

    @Autowired
    private PayService payService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SmsService smsService;

    @Autowired
    private AuditService auditService;

    @PostMapping("/doPay")
    public String doPay(@RequestBody PayRequest request,
                        @RequestParam(required = false) String smsCode) {
        long start = System.currentTimeMillis();
        String resultMsg = "";
        String status = "SUCCESS";
        try {
            // 1. 获取Agent下发的指令
            String action = riskAgentService.getAgentAction(
                    request.getUserId(),
                    request.getAmount(),
                    request.getIp()
            );

            // 2. Java后端根据Agent的指令，执行对应的本地工具
            switch (action) {
                case "BLOCK":
                    resultMsg = "支付被安全护栏拦截：触发Agent风控策略，交易已终止";
                    return resultMsg;

                case "SMS_VERIFY":
                    User user = userMapper.findById(request.getUserId());
                    if (user == null || user.getPhone() == null) {
                        resultMsg = "需二次验证，但用户手机号未绑定";
                        return resultMsg;
                    }
                    // 检查是否传入了验证码
                    if (smsCode == null || smsCode.isEmpty()) {
                        // Agent要求验证，Java调用SmsService发送短信
                        smsService.sendCode(user.getPhone());
                        resultMsg = "Agent触发中风险，验证码已发送至 " + desensitizePhone(user.getPhone());
                        return resultMsg;
                    }
                    // 如果传了验证码，就校验
                    if (!smsService.verifyCode(user.getPhone(), smsCode)) {
                        resultMsg = "验证码错误，请重新输入";
                        return resultMsg;
                    }
                    // 验证码正确，放行继续往下执行支付
                    break;

                case "ALLOW":
                default:
                    // 放行，跳出switch去执行支付
                    break;
            }

            // 3. 执行支付
            String payResult = payService.doPay(
                    request.getUserId(),
                    request.getAmount(),
                    request.getIp(),
                    request.getDescription()
            );
            switch (payResult) {
                case "success":
                    resultMsg = "支付成功！";
                    break;
                case "insufficient_balance":
                    resultMsg = "支付失败：余额不足";
                    break;
                case "user_not_found":
                    resultMsg = "支付失败：用户不存在";
                    break;
                default:
                    resultMsg = "支付失败：系统异常";
            }
            return resultMsg;
        } catch (Exception e) {
            status = "FAIL: " + e.getMessage();
            throw e;
        } finally {
            long cost = System.currentTimeMillis() - start;
            auditService.log("支付操作", request.getUserId(), request.getIp(), status, cost);
        }
    }

    //手机号脱敏
    private String desensitizePhone(String phone) {
        if (phone == null || phone.length() < 11) return phone;
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    @PostMapping("/recharge")
    public String recharge(@RequestBody RechargeRequest request) {
        long start = System.currentTimeMillis();
        String result = "";
        try {
            boolean success = payService.recharge(request.getUserId(), request.getAmount());
            result = success ? "充值成功！" : "充值失败：用户不存在";
            return result;
        } finally {
            long cost = System.currentTimeMillis() - start;
            auditService.log("充值操作", request.getUserId(), "系统内部", result, cost);
        }
    }
}
