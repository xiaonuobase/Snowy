---
description: 基于已有数据库表快速生成 CRUD 代码
---

# /crud —— 已有表快速生成 CRUD

前提：数据库里已经存在业务表（用户手动建的或旧系统迁移的）。目标：按 Snowy 规范生成后端六件套 + 前端三件 + 菜单 SQL。

## 流程

### 1. 获取表结构

数据库连接从 `snowy-web-app/src/main/resources/application.properties` 的 dynamic master 段解析（禁止硬编码）：

```bash
mysql -h{host} -u{user} -p{pwd} {db} -e "SHOW CREATE TABLE {表名};"
```

mysql CLI 不可用 → 请用户提供表结构（DDL 粘贴或描述字段），继续走下面步骤。

### 2. 结构分析

- 表前缀归插件（BIZ_ → biz；SYS_/DEV_ 等系统前缀表**不要生成新 CRUD**，那是平台在用）
- 字段类型 → Java 类型映射：varchar→String、int→Integer、datetime→String(现有风格)/Date、decimal→BigDecimal、text/longtext→String
- 识别特殊字段：
  - `PARENT_ID` 存在 → 树形（生成树模板形态）
  - 审计字段组（DELETE_FLAG/CREATE_TIME 等）齐全 → Entity 继承 CommonEntity，不重复声明
  - 审计字段缺失 → 提示用户补列（给 ALTER SQL），逻辑删除/审计是硬性要求

### 3. 生成代码（模式 A 直写）

**先 Read 范本** `snowy-plugin/snowy-plugin-biz/.../modular/notice/`，然后按 crud-development 技能生成：
- 类名：表名去前缀转驼峰 + Biz 前缀（BIZ_SUPPLIER → BizSupplier）
- 落位：`snowy-plugin-biz/.../modular/{域名}/`
- URL：`/biz/{域名}/{page|add|edit|delete|detail}`
- 查询字段：varchar 的做 like 或 eq（问用户或按字段语义：名称类 like、类型/状态类 eq）

### 4. 前端三件 + 菜单 SQL

同 /dev 第 6 步模式 A。

### 5. 输出清单与收尾

- 生成文件列表（后端 + 前端）
- 菜单/按钮 SQL（执行或落盘 docs/sql-pending/）
- 提醒：重启 → 授权 → 刷新 → /check
