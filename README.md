# 💳 全链路安全防御支付系统（类Agent 风控辅助）

一个从零开始基于 Spring Boot 的支付系统实战项目，带有类Agent **“护栏优先 + AI 辅助”** 的混合驱动风控架构，集成 XSS 防御、JWT 认证、接口限流、数据脱敏、审计日志、Redis 缓存、Docker 容器化部署等完整安全链路。

---

## 📌 项目背景

在真实支付场景中，安全性覆盖各个层面，本项目针对以下三类风险进行逐层防御：

- **安全风险**：SQL 注入、XSS 攻击、敏感数据泄露
- **业务风险**：大额异常交易、高频刷单
- **工程风险**：未授权访问、越权操作、手机号明文暴露

在此基础上，以 **硬编码安全护栏为第一道防线**，**DeepSeek API 作为辅助推理引擎**，构建混合驱动风控决策体系。

---

## 🛠️ 技术栈

| 模块    | 技术选型                            |
| ----- | ------------------------------- |
| 核心框架  | Spring Boot 4.0.8               |
| 数据访问  | MyBatis + MySQL 8.0.46          |
| 缓存与限流 | Redis + Guava RateLimiter       |
| 安全加密  | BCrypt（密码）、JWT（认证）              |
| AI 风控 | DeepSeek API + OkHttp + Jackson |
| 工具链   | Lombok、Spring Boot DevTools     |
| 部署    | Linux服务加固 + Docker容器化           |

---

## 🚀 核心功能

- **用户模块**：手机验证码注册（模拟）、BCrypt 加密存储、JWT 登录认证
- **账户模块**：余额充值、查询
- **支付模块**：护栏优先 → AI 辅助 → 扣款下单（事务保证一致性）
- **安全防御**：
  - XSS 过滤器（请求参数转义）
  - MyBatis `#{}`预编译防 SQL 注入
  - 手机号 JSON 序列化脱敏（`@JsonSerialize`）
  - IP 级别接口限流（Guava RateLimiter，5 次/秒）
- **审计日志**：手动记录操作耗时与结果（预留 AOP 升级空间）

---

## 🤖 类Agent 混合驱动风控架构

支付请求进入后，**先执行硬编码护栏，未触发护栏才调用 AI 推理**：

| 决策层               | 触发条件                           | 输出指令                                 |
| ----------------- | ------------------------------ | ------------------------------------ |
| 🔴 硬编码护栏（优先）      | 金额 ≥ 5000 元 或 IP 非 192.168.1.* | `BLOCK`（直接拦截）                        |
| 🟡 硬编码护栏（优先）      | 金额 ≥ 2000 元 且 近1小时下单 ≥ 5次      | `SMS_VERIFY`（强制短信验证）                 |
| 🟢 AI 辅助推理（护栏通过后） | 上述规则均未命中                       | 调用 DeepSeek API，输出 `ALLOW` 或 `BLOCK` |
| ⚪ 降级兜底            | AI 接口超时或异常                     | 自动降级为 `ALLOW`（保支付链路可用）               |

> **设计理念**：绝对风险由硬编码 100% 拦截，AI 作为辅助引擎降低误判，工业级可靠性优先

---

## 📁 项目结构（简略）

payment-system/
├── src/main/java/com/example/payment_system/
│   ├── controller/      # 用户接口、支付接口
│   ├── service/         # 核心业务：风控、支付、订单、审计
│   │   ├── RiskAgentService.java   # 护栏优先 + AI 辅助决策
│   │   ├── PayService.java         # 支付与事务管理
│   │   └── OrderCountService.java  # Redis 频次计数
│   ├── mapper/          # MyBatis 数据访问（预编译防注入）
│   ├── entity/          # 实体类（含手机号脱敏注解）
│   ├── filter/          # XSS 过滤器
│   └── config/          # JWT 拦截器、限流拦截器、脱敏器
├── src/main/resources/
│   ├── application.properties       # 本地配置（含敏感信息不提交）
│   └── application-example.properties  # 配置模板
└── README.md

---

## 🐳 Linux 服务加固 + Docker 部署

- **SSH 安全**：修改默认端口、禁用 root 远程登录、创建专用部署账号
- **防火墙**：UFW 放行新 SSH 端口与 8080 服务端口
- **容器化编排**：`docker-compose.yml` 编排 Spring Boot + MySQL + Redis 三服务
- **环境一致**：Docker 镜像打包解决开发/生产环境不一致问题

---

## 📝 详细文档

完整的设计思路、架构图、代码逐行解析、Postman 测试截图、Linux 加固与 Docker 部署教程，请移步我的博客：  
👉 **[https://s12lx0.github.io/](https://s121x0.github.io/)**

---

## 📄 许可证

[MIT](LICENSE) © 2026 s121x0

---
