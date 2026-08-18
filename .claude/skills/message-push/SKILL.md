---
name: message-push
description: Snowy 消息与推送规范：站内信 DEV_MESSAGE、WebSocket 实时通知、钉钉/企微/飞书推送 DEV_PUSH、前端消息中心联动。触发场景：1) 业务需要给用户发通知 2) 需要实时推送（WebSocket）3) 对接钉钉/企业微信/飞书机器人。触发词：消息、通知、站内信、推送、WebSocket、实时、钉钉、企业微信、飞书、DEV_MESSAGE、DEV_PUSH、消息中心。
---

# Snowy 消息与推送规范

## 能力矩阵

| 需求 | 用什么 | 模块 |
|---|---|---|
| 站内信（应用内通知） | DevMessageApi 发消息 → 用户点铃铛查看 | `dev/modular/message/`（DEV_MESSAGE 表） |
| 实时弹到页面 | WebSocket（站内信实时通道） | `dev/modular/message/websocket/` |
| 推到钉钉/企微/飞书群机器人 | DevPushApi | `dev/modular/push/`（DEV_PUSH 表） |
| 短信/邮件 | 见 sms-mail 技能 | `dev/modular/sms/`、`email/` |

## 站内信（最常用）

```java
// 跨插件调用
@Resource
private DevMessageApi devMessageApi;      // snowy-plugin-api/snowy-plugin-dev-api

// 发给指定用户（消息自动入库 DEV_MESSAGE + WebSocket 实时推送在线用户）
devMessageApi.saveMessage(...)            // 具体 API 见 DevMessageApi 定义
```

前端：顶栏消息中心（`snowy-admin-web/src/views/index/` 相关），未读数角标自动更新。

管理界面：开发工具 → 站内信（可查/批量发）。

## WebSocket（实时通知）

- 服务端：`dev/modular/message/websocket/`（Snowy 自封装，基于 Redis 发布订阅支持多实例）
- 触发时机：发站内信时对在线用户自动推送；业务自定义事件也可推
- 前端：WebSocket 客户端逻辑在 `snowy-admin-web/src/layout/components/message.vue`（顶栏消息中心，含连接与未读数更新）

业务一般**不直接操作 WebSocket**——发站内信即自动实时推送。只有自定义实时场景（如大屏数据刷新）才直接用。

## 钉钉/企微/飞书推送

- 配置：开发工具 → 消息推送（DEV_PUSH 表：webhook 地址、密钥）
- 使用：

```java
@Resource
private DevPushApi devPushApi;            // 推送到已配置的机器人
```

## 常见错误正误对照

| ❌ | ✅ |
|---|---|
| 自建消息表 + 轮询 | DEV_MESSAGE 站内信 + WebSocket 自动实时 |
| 每个业务自己开 WebSocket 端点 | 复用 dev 的 websocket 模块（站内信通道） |
| 直接 HTTP 调钉钉 webhook | DevPushApi（可管理/可切换/有记录） |
| 消息内容存 HTML 富文本拼 SQL | 模板 + 参数（参考现有消息类型） |

## 检查清单

- [ ] 站内信走 DevMessageApi（自动入库 + 实时推送）
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
