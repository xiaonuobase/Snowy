---
name: crud
description: 基于已有数据库表快速生成 CRUD 代码。仅在用户显式调用时执行：Claude Code、Qwen Code 输入 /crud，Codex 输入 $crud。
disable-model-invocation: true
---

# crud —— 已有表快速生成 CRUD

前提：数据库里已经存在业务表（用户手动建的或旧系统迁移的）。目标：按 Snowy 规范生成后端六件套 + 前端三件 + 菜单 SQL。

## 流程

### 1. 获取表结构

数据库连接从 `snowy-web-app/src/main/resources/application.properties` 的 dynamic master 段解析（禁止硬编码）：

```bash
mysql -h{host} -u{user} -p{pwd} {db} -e "SHOW CREATE TABLE {表名};"
```

mysql CLI 不可用 → 请用户提供表结构（DDL 粘贴或描述字段），继续走下面步骤。

### 2. 结构分析

- 表前缀归属：表前缀 = 所在插件，BIZ_ 表生成到 snowy-plugin-biz，自建插件的表（如 ORDER_）生成到对应插件（snowy-plugin-order）；SYS_/DEV_ 等系统前缀表**不要生成新 CRUD**，那是平台在用
- 目标位置与包名：向用户确认放 snowy-plugin-biz 还是自建业务插件、用什么包名（未说明则默认 snowy-plugin-biz + `vip.xiaonuo.biz`）；包名不以 vip.xiaonuo 开头时按 plugin-architecture"使用自定义包名"补 snowy-web-app 扫描配置
- 字段类型 → Java 类型映射：varchar→String、int→Integer、datetime→Date（与 CommonEntity 审计字段、生成器 GenDbTypeUtil 一致；只有按字符串存的日期列，即 varchar 类型的如 BIRTHDAY、ENTRY_DATE，才用 String）、decimal→BigDecimal、text/longtext→String
- 识别特殊字段：
  - `PARENT_ID` 存在 → 树形（生成树模板形态）
  - 审计字段组（DELETE_FLAG/CREATE_TIME 等）齐全 → Entity 继承 CommonEntity，不重复声明
  - 审计字段缺失 → 提示用户补列（给 ALTER SQL），逻辑删除/审计是硬性要求

### 3. 生成代码（模式 A 直写）

**先 Read 范本** `snowy-plugin/snowy-plugin-biz/.../modular/notice/`，然后按 crud-development 技能生成：
- 类名：完整表名转驼峰，表前缀即插件缩写（BIZ_SUPPLIER → BizSupplier；ORDER_PAY_RECORD → OrderPayRecord）；表前缀与目标插件不一致时先向用户确认
- 落位：目标插件的 `.../modular/{域名}/`（默认 `snowy-plugin-biz/.../modular/{域名}/`），包名按上一步确认结果
- URL：`/{插件}/{域名}/{page|add|edit|delete|detail}`（biz 插件即 `/biz/...`）
- 版权头：业务代码不加小诺版权头（用户有版权头规范按用户的来）
- 查询字段：varchar 的做 like 或 eq（问用户或按字段语义：名称类 like、类型/状态类 eq）

### 4. 前端三件 + 菜单 SQL

同 dev 流程技能（Claude Code、Qwen Code `/dev`，Codex `$dev`）第 6 步模式 A。

### 5. 输出清单与收尾

- 生成文件列表（后端 + 前端）
- 菜单/按钮 SQL（执行或落盘 docs/sql-pending/）
- 提醒：重启 → 授权（角色管理 → 授权资源 勾菜单/按钮 + 授权权限 勾新接口 URL，超管也要授权权限）→ 刷新 → 调用 check 流程技能（Claude Code、Qwen Code `/check`，Codex `$check`）
