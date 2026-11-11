---
name: message-push
description: Snowy 消息与推送规范：站内信 DEV_MESSAGE、WebSocket 实时通知、钉钉/企微/飞书推送 DEV_PUSH、前端消息中心联动。触发场景：1) 业务需要给用户发通知 2) 需要实时推送（WebSocket）3) 对接钉钉/企业微信/飞书机器人。触发词：消息、通知、站内信、推送、WebSocket、实时、钉钉、企业微信、飞书、DEV_MESSAGE、DEV_PUSH、消息中心。
---

# Snowy 消息与推送规范

## 能力矩阵

| 需求 | 用什么 | 模块 |
|---|---|---|
| 站内信（应用内通知） | DevMessageApi 发消息 → 用户点铃铛查看 | `dev/modular/message/`（DEV_MESSAGE 表） |
| 未读数实时刷新 | WebSocket（发站内信后自动把未读数推到顶栏角标） | `dev/modular/message/websocket/` |
| 推到钉钉/企微/飞书群机器人 | DevPushApi | `dev/modular/push/`（DEV_PUSH 表） |
| 短信/邮件 | 见 sms-mail 技能 | `dev/modular/sms/`、`email/` |

## 站内信（最常用）

```java
// 跨插件调用
@Resource
private DevMessageApi devMessageApi;      // snowy-plugin-api/snowy-plugin-dev-api

// 发给指定用户（消息自动入库 DEV_MESSAGE + WebSocket 给在线接收人推未读数）
// category 取 DevMessageCategoryEnum：SYS / BIZ；不传 category 的重载默认 SYS，不传 content 的重载只发主题
devMessageApi.sendMessageWithContent(receiverIdList, "BIZ", "标题", "正文");
```

前端：顶栏消息中心 `snowy-admin-web/src/layout/components/message.vue`（铃铛 + 未读角标，由 WebSocket 自动更新）；个人中心的站内信 `views/sys/user/userTab/userMessage.vue` 可按分类查看。

管理界面：系统 → 基础工具 → 站内信息（可查/批量发）。

## WebSocket（实时通知）

- 服务端：`dev/modular/message/websocket/DevMessageWebSocket`（Snowy 自封装，Session 存在当前 JVM 内存的 ConcurrentHashMap 里，只能推给连在本实例上的用户；多实例部署需要自己加 Redis 发布订阅或粘性会话）
- 触发时机：站内信发送成功后，给接收人推最新未读数（载荷固定为 `{code:200,data:未读数}`），不推正文，也不支持业务自定义事件
- 前端：WebSocket 客户端逻辑在 `snowy-admin-web/src/layout/components/message.vue`（顶栏消息中心，含连接与未读数更新）

业务**不直接操作 WebSocket**，发站内信就会自动刷新未读数。大屏数据刷新这类自定义实时场景，现有通道做不到，要新增端点和前端处理，并按框架改动流程评估。

## 钉钉/企微/飞书推送

- 配置：系统 → 系统运维 → 系统配置 →「推送配置」页签（存 DEV_CONFIG，键 `SNOWY_PUSH_DINGTALK_TOKEN_ID/SIGN`、`SNOWY_PUSH_WORKWECHAT_TOKEN_ID`、`SNOWY_PUSH_FEISHU_TOKEN_ID`）
- 推送记录：DEV_PUSH 表（系统 → 基础工具 → 消息推送，可查记录、可手动推送）
- 使用：

```java
@Resource
private DevPushApi devPushApi;            // 推送到已配置的机器人
```

## 常见错误正误对照

| 错误 | 正确 |
|---|---|
| 自建消息表 + 轮询 | DEV_MESSAGE 站内信 + WebSocket 自动实时 |
| 每个业务自己开 WebSocket 端点 | 复用 dev 的 websocket 模块（站内信通道） |
| 直接 HTTP 调钉钉 webhook | DevPushApi（可管理/可切换/有记录） |
| 拼 SQL 存消息内容，或业务自建消息表 | 调 `DevMessageApi.sendMessageWithContent`，subject/content 作为参数传入（MyBatis-Plus 参数化入库），分类用 `DevMessageCategoryEnum`（SYS/BIZ）；正文要模板化，就在业务侧渲染好再传 content |

## 检查清单

- [ ] 站内信走 DevMessageApi（自动入库 + 实时刷新未读数）
- [ ] 群机器人推送走 DevPushApi
- [ ] 没有自建轮询接口
- [ ] 消息有类型分类（用户可在前端按类型筛）

## 参考实现

| 文件 | 说明 |
|---|---|
| `snowy-plugin/snowy-plugin-dev/src/main/java/vip/xiaonuo/dev/modular/message/` | 站内信模块（含 websocket/） |
| `snowy-plugin/snowy-plugin-dev/src/main/java/vip/xiaonuo/dev/modular/push/` | 推送模块 |
| `snowy-plugin-api/snowy-plugin-dev-api/src/main/java/vip/xiaonuo/dev/api/DevMessageApi.java` | 站内信跨插件接口 |
| `snowy-plugin-api/snowy-plugin-dev-api/src/main/java/vip/xiaonuo/dev/api/DevPushApi.java` | 推送跨插件接口 |
| `snowy-admin-web/src/layout/components/message.vue` | 前端消息中心（WS 客户端） |
