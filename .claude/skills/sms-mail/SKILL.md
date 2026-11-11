---
name: sms-mail
description: Snowy 短信与邮件规范：sms4j 短信（已接入阿里云/腾讯云）、DEV_SMS 发送记录、DevEmailApi 邮件、SMTP 配置、验证码发送流程与 auth 联动。触发场景：1) 业务需要发短信/邮件通知 2) 手机/邮箱验证码登录相关 3) 配置短信供应商或 SMTP。触发词：短信、SMS、sms4j、邮件、email、SMTP、验证码、通知发送、DEV_SMS、DEV_EMAIL。
---

# Snowy 短信与邮件规范

## 短信（sms4j）

- 引擎：org.dromara.sms4j（`sms4j-javase-plugin`，在代码里注册，不走 Spring 配置）。Snowy 只接入了**阿里云（ALIYUN）、腾讯云（TENCENT）两家**，其他厂商要自己扩展 `DevSms*Util`、引擎枚举 `DevSmsEngineTypeEnum` 和配置页
- 模块：`snowy-plugin-dev` 的 `modular/sms/`；发送记录界面：系统 → 基础工具 → 短信发送（DEV_SMS 表存发送记录）
- 供应商配置：系统 → 系统运维 → 系统配置 →「短信配置」页签（存 DEV_CONFIG，键 `SNOWY_SMS_ALIYUN_*` / `SNOWY_SMS_TENCENT_*`；默认引擎 `SNOWY_SYS_DEFAULT_SMS_ENGINE`），**不在 application.properties**

```java
// 业务发短信
@Resource
private DevSmsApi devSmsApi;        // snowy-plugin-api/snowy-plugin-dev-api

// 按系统配置的默认引擎发送，成功后写 DEV_SMS
devSmsApi.sendDynamicSms("13800000000", "SMS_123456", JSONUtil.createObj().set("code", "123456"));
```

通过 DevSmsApi / DevSmsService 发送，成功后写一条 DEV_SMS 记录（含 receiptInfo 回执）；失败直接抛 CommonException（消息是供应商返回的错误），不落库。短信发送界面可以查询、看详情、删除记录，也能手动发一条新短信（**没有**按记录重发的功能）。

## 邮件

```java
@Resource
private DevEmailApi devEmailApi;    // snowy-plugin-api/snowy-plugin-dev-api

devEmailApi.sendDynamicTxtEmail("a@x.com", "主题", "内容");                 // 按默认邮件引擎发送
devEmailApi.sendDynamicHtmlEmail("a@x.com", "主题", "<h1>HTML</h1>");
devEmailApi.sendTextEmailLocal("a@x.com", "主题", "内容", List.of(new File("/path/附件.pdf")));  // 带附件走本地 SMTP
// CommonEmailUtil 只做邮箱格式校验（isNotEmail / validEmail），没有发送方法
```

- SMTP 配置：系统 → 系统运维 → 系统配置 →「邮件配置」页签（存 DEV_CONFIG：本地 SMTP 用 `SNOWY_EMAIL_LOCAL_*`，阿里云/腾讯云邮件用 `SNOWY_EMAIL_ALIYUN_*` / `SNOWY_EMAIL_TENCENT_*`）
- 发送记录：DEV_EMAIL 表（系统 → 基础工具 → 邮件推送 里可查）

## 验证码流程（与 auth 联动）

```
1. 前端 /auth/b/getPhoneValidCode（或邮箱版）→ 先校验图形验证码 → CommonValidCodeUtil.checkSendFrequency 限流 → 后端生成 6 位码
2. 存 Redis（CommonCacheOperator，带过期时间，键含手机号）
3. 发送短信/邮件给用户
4. 用户提交登录/绑定 → 后端 getAndRemove 取码比对：取出即删除，**输错一次也作废**，需要重新获取
```

- 发送限流（CommonValidCodeUtil）：同一手机号或邮箱 60 秒一次、24 小时 10 次，同一 IP 每小时 30 次；B/C 端与各用途共用额度，阈值目前是代码常量
- 新增发验证码的入口必须同时做到：先过图形验证码、调 checkSendFrequency、存码带过期、校验用 getAndRemove；不要用 get + remove 两步（并发下同一个码能被用两次）

参考实现：`snowy-plugin-auth/modular/login/` 的验证码相关方法（AuthController / AuthServiceImpl）。

## 常见错误正误对照

| 错误 | 正确 |
|---|---|
| 手写 HttpClient 调短信商 API | DevSmsApi（`sendDynamicSms` / `sendSmsAliyun` / `sendSmsTencent`） |
| 验证码存数据库表 | Redis（CommonCacheOperator + 过期） |
| 邮件密码/SMTP 明文写代码里 | 系统配置「邮件配置」页签（DEV_CONFIG） |
| 发送失败时用户端报 500 或看到原始堆栈 | 平台工具已抛 CommonException，由全局异常处理返回提示；业务要留失败记录，需要自己 catch 后记录（DEV_SMS 只记成功的发送） |
| 业务自己 new JavaMail Session、硬编码 SMTP | 注入 DevEmailApi（`sendDynamicTxtEmail` / `sendDynamicHtmlEmail` 等），由平台按 DEV_CONFIG 发送并写 DEV_EMAIL 记录 |

## 检查清单

- [ ] 走 DevSmsApi / DevEmailApi，未自造 HTTP 客户端
- [ ] 验证码有过期时间与一次性消费（getAndRemove），发送前调用 CommonValidCodeUtil.checkSendFrequency
- [ ] 敏感配置在管理界面/配置文件，不在代码
- [ ] 发送结果可追溯（成功的记录在 DEV_SMS / DEV_EMAIL；失败要业务自己记）

## 参考实现

| 文件 | 说明 |
|---|---|
| `snowy-plugin/snowy-plugin-dev/src/main/java/vip/xiaonuo/dev/modular/sms/` | 短信模块全套 |
| `snowy-plugin-api/snowy-plugin-dev-api/src/main/java/vip/xiaonuo/dev/api/DevEmailApi.java` | 跨插件邮件接口（CommonEmailUtil 只做邮箱格式校验） |
| `snowy-plugin-api/snowy-plugin-dev-api/src/main/java/vip/xiaonuo/dev/api/DevSmsApi.java` | 跨插件短信接口 |
| `snowy-plugin/snowy-plugin-auth/src/main/java/vip/xiaonuo/auth/modular/login/` | 验证码登录流程 |
