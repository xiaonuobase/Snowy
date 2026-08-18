---
name: sms-mail
description: Snowy 短信与邮件规范：sms4j 多供应商短信、DEV_SMS 发送记录、CommonEmailUtil 邮件、SMTP 配置、验证码发送流程与 auth 联动。触发场景：1) 业务需要发短信/邮件通知 2) 手机/邮箱验证码登录相关 3) 配置短信供应商或 SMTP。触发词：短信、SMS、sms4j、邮件、email、SMTP、验证码、通知发送、DEV_SMS、DEV_EMAIL。
---

# Snowy 短信与邮件规范

## 短信（sms4j）

- 引擎：org.dromara.sms4j（多供应商：阿里/腾讯/华为/容联/京东等九家）
- 模块：`snowy-plugin-dev` 的 `modular/sms/`；管理界面：开发工具 → 短信管理（DEV_SMS 表存发送记录）
- 供应商配置：`application.properties` 的 `sms:` 段 + 短信管理界面（DEV_CONFIG 动态）

```java
// 业务发短信
@Resource
private DevSmsApi devSmsApi;        // snowy-plugin-api/snowy-plugin-dev-api

// 或使用 sms4j 原生（配置好的供应商）
SmsBlend smsBlend = SmsFactory.getSmsBlend("supplier1");
smsBlend.sendMessage("手机号", "模板id", Map.of("code", "123456"));
```

发送后记录自动落 DEV_SMS（含状态），短信管理界面可查/重发。

## 邮件

```java
// snowy-common 的 CommonEmailUtil（基于 dev 插件 DEV_EMAIL 配置）
CommonEmailUtil.sendTextEmail("收件人", "主题", "内容");
CommonEmailUtil.sendHtmlEmail("收件人", "主题", "<h1>HTML</h1>");
CommonEmailUtil.sendAttachmentEmail("收件人", "主题", "内容", "附件路径");
// 详见 snowy-common/src/main/java/vip/xiaonuo/common/util/CommonEmailUtil.java
```

- SMTP 配置：开发工具 → 邮件管理（DEV_EMAIL 表：主机/端口/账号/密码/SSL）
- 发送记录：DEV_EMAIL 相关查询接口

## 验证码流程（与 auth 联动）

```
1. 前端 /auth/b/getPhoneValidCode（或邮箱版）→ 后端生成 6 位码
2. 存 Redis（CommonCacheOperator，带过期时间，键含手机号）
3. 发送短信/邮件给用户
4. 用户提交登录/绑定 → 后端从缓存取码比对（一次性，验证即删）
5. 错误次数限制防爆破
```

参考实现：`snowy-plugin-auth/modular/login/` 的验证码相关方法（AuthController / AuthServiceImpl）。

## 常见错误正误对照

| ❌ | ✅ |
|---|---|
| 手写 HttpClient 调短信商 API | sms4j（SmsFactory.getSmsBlend） |
| 验证码存数据库表 | Redis（CommonCacheOperator + 过期） |
| 邮件密码/SMTP 明文写代码里 | 邮件管理界面配置（DEV_EMAIL） |
| 发送失败用户端报 500 | 捕获后 CommonException("短信发送失败，请稍后再试")，记录落 DEV_SMS |
| 每次发送 new 邮件连接 | CommonEmailUtil（连接池复用） |

## 检查清单

- [ ] 走 sms4j / CommonEmailUtil，未自造 HTTP 客户端
- [ ] 验证码有过期时间与一次性消费
- [ ] 敏感配置在管理界面/配置文件，不在代码
- [ ] 发送结果可追溯（DEV_SMS / DEV_EMAIL 记录）

## 参考实现

| 文件 | 说明 |
|---|---|
| `snowy-plugin/snowy-plugin-dev/src/main/java/vip/xiaonuo/dev/modular/sms/` | 短信模块全套 |
| `snowy-common/src/main/java/vip/xiaonuo/common/util/CommonEmailUtil.java` | 邮件工具 |
| `snowy-plugin-api/snowy-plugin-dev-api/src/main/java/vip/xiaonuo/dev/api/DevSmsApi.java` | 跨插件短信接口 |
| `snowy-plugin/snowy-plugin-auth/src/main/java/vip/xiaonuo/auth/modular/login/` | 验证码登录流程 |
| `snowy-web-app/src/main/resources/application.properties` | sms 供应商配置段 |
