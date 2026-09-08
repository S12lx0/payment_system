# Full-Link Secure Payment System (类Agent风控辅助)

> 

> **English Summary**: 
> This is a full-link payment system built with Spring Boot, featuring a hybrid risk control architecture that combines hard-coded guardrails with AI-assisted decision-making (DeepSeek API). The system implements multi-layer security defenses including XSS filtering, JWT authentication, BCrypt encryption, rate limiting (Guava RateLimiter), data desensitization, audit logging, and Docker containerization. The project demonstrates end-to-end delivery capability from requirement analysis to containerized deployment on Linux with security hardening.
> 
> **Tech Stack**: Java 17, Spring Boot 4.0.8, MyBatis, MySQL 8.0.46, Redis, JWT, BCrypt, DeepSeek API, Docker, Linux

---

## 项目背景 (Background)

在真实支付场景中，安全性覆盖各个层面，本项目针对以下三类风险进行逐层防御：

- **安全风险**：SQL注入、XSS攻击、敏感数据泄露
- **业务风险**：大额异常交易、高频刷单
- **工程风险**：未授权访问、越权操作、手机号明文暴露

在此基础上，以硬编码安全护栏为第一道防线，DeepSeek API作为辅助推理引擎，构建混合驱动风控决策体系。

---

## 技术栈 (Tech Stack)

| 模块    | 技术选型                            |
| ----- | ------------------------------- |
| 核心框架  | Spring Boot 4.0.8               |
| 数据访问  | MyBatis + MySQL 8.0.46          |
| 缓存与限流 | Redis + Guava RateLimiter       |
| 安全加密  | BCrypt（密码）、JWT（认证）              |
| AI风控  | DeepSeek API + OkHttp + Jackson |
| 工具链   | Lombok、Spring Boot DevTools     |
| 部署    | Linux服务加固 + Docker容器化           |

---

## 核心功能 (Core Features)

- **用户模块**：手机验证码注册（模拟）、BCrypt加密存储、JWT登录认证
- **账户模块**：余额充值、查询
- **支付模块**：护栏优先 → AI辅助 → 扣款下单（事务保证一致性）

---

## 安全防御 (Security Defenses)

- XSS过滤（全局Json反序列化，对展示型文本请求参数转义）
- MyBatis `#{}` 预编译防 SQL 注入
- 手机号 JSON 序列化脱敏（`@JsonSerialize`）
- IP级别接口限流（Guava RateLimiter，5 次/秒）
- 审计日志：手动记录操作耗时与结果（预留AOP升级空间）

---

## 类Agent混合驱动风控架构

支付请求进入后，先执行硬编码护栏，未触发护栏才调用AI推理：

| 决策层               | 触发条件                        | 输出指令                                |
| ----------------- | --------------------------- | ----------------------------------- |
| **硬编码护栏（优先）**     | 金额 ≥ 5000元 或 IP非192.168.1.* | `BLOCK`（直接拦截）                       |
| **硬编码护栏（优先）**     | 金额 ≥ 2000元 且 近1小时下单 ≥ 5次    | `SMS_VERIFY`（强制短信验证）                |
| **AI辅助推理（护栏通过后）** | 上述规则均未命中                    | 调用DeepSeek API，输出 `ALLOW` 或 `BLOCK` |
| **降级兜底**          | AI接口超时或异常                   | 自动降级为 `ALLOW`（保支付链路可用）              |

**设计理念**：绝对风险由硬编码 100% 拦截，AI作为辅助引擎降低误判，工业级可靠性优先。

---

## 项目结构 (Project Structure)

```
payment-system/
├── src/main/java/com/example/payment-system/
│   ├── controller/          # 用户接口、支付接口
│   ├── service/             # 核心业务：风控、支付、订单、审计
│   │   ├── RiskAgentService.java   # 护栏优先 + AI辅助决策
│   │   ├── PayService.java         # 支付与事务管理
│   │   └── OrderCountService.java  # Redis频次计数
│   ├── mapper/              # MyBatis数据访问（预编译防注入）
│   ├── entity/              # 实体类（含手机号脱敏注解）
│   └── config/              # JWT拦截器、限流拦截器、脱敏器、XSS过滤豁免
├── src/main/resources/
│   ├── application.properties       # 本地配置（含敏感信息不提交）
│   └── application-example.properties # 配置模板
└── README.md
```

---

## Linux服务加固 + Docker部署

- **SSH安全**：修改默认端口、禁用root远程登录、创建专用部署账号
- **防火墙**：UFW放行新SSH端口与8080服务端口
- **容器化编排**：`docker-compose.yml` 编排 Spring Boot + MySQL + Redis 三服务
- **环境一致**：Docker镜像打包解决开发/生产环境不一致问题

---

## 详细文档

完整的设计思路、架构图、代码逐行解析、Postman测试截图、Linux加固与Docker部署教程，请移步我的博客：

**https://s12lx0.github.io/**

---

## 许可证 (License)

MIT © 2026 S12lx0
