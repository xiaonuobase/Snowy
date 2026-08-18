---
name: code-generator
description: Snowy 自带 Beetl 代码生成器的使用规范：入口流程、GenBasic 字段填写、5 种模板形态选择、sqlend 菜单 SQL、生成后修补清单。触发场景：1) 用户想用代码生成器生成功能 2) /dev 命令模式 B 引导用户走生成器 3) 选择生成模板形态（表格/树/左树右表/主子表）4) 生成菜单资源 SQL。触发词：代码生成、生成器、gen、Beetl、模板、genType、sqlend、导入表、一键生成。注意：AI 直接手写六件套的规范见 crud-development；本技能讲平台自带生成器的正确用法。
---

# Snowy 代码生成器使用规范

## 概览

Snowy 自带 Beetl 模板代码生成器（`snowy-plugin-gen`），能从数据库表生成**后端六件套 + 前端三件 + 菜单资源 SQL** 的全套代码。这是开发标准 CRUD 的首选方式（对应 `/dev` 命令的**模式 B**）。

**入口**：启动后端 → 登录前端 → 开发工具 → 代码生成。数据存在 `GEN_BASIC` / `GEN_CONFIG` 表。

**生成产物 = 本项目 CRUD 标准结构**（生成器模板就是官方规范的定义源，手写代码要与生成产物一致）。

## 使用流程（5 步）

```
1. 建表（按 database-ops 规范：BIZ_ 前缀、大写、String 雪花主键、审计字段齐全）
2. 代码生成页面 → 导入表（选择刚建的表，可按住 Ctrl 选主表+子表做主子表）
3. 填写 GenBasic 基础配置（下表）
4. 在字段配置页调整每个字段的：是否查询/显示/必填、控件类型、字典编码等
5. 点"生成代码"→ zip 下载（或直接写入项目，取决于生成方式）→ 按"生成后必做"落地
```

## GenBasic 关键字段填写指南

| 字段 | 说明 | 业务插件二开的推荐值 |
|---|---|---|
| `dbTable` / `dbTableKey` | 主表名 / 主键（导入时自动带出） | — |
| `pluginName` | 生成代码归属插件 | `biz`（业务代码进 biz 插件） |
| `moduleName` | 模块名（URL 第一段也用它） | 一般 `biz` |
| `tablePrefix` | 表前缀移除（生成类名时去掉的前缀） | `BIZ_` |
| `generateType` | 生成方式：ZIP 下载 / 项目路径写入 | 二开推荐 ZIP 后自查合入 |
| `module` | 所属系统模块（SYS_RESOURCE 的 MODULE，决定菜单挂在哪） | 选业务所属模块 |
| `menuPid` | 上级菜单/目录 id | 选业务菜单挂载点 |
| `mobileModule` | 移动端所属模块（留空则不生成移动端） | 一般留空 |
| `functionName` | 功能名（中文，用于菜单标题/日志） | 如 `供应商` |
| `busName` | 业务名（小写，URL 与前端目录名） | 如 `supplier` → `/biz/supplier/page` |
| `className` | 类名（不含前缀，生成器自动拼 Biz 前缀） | 如 `Supplier` → `BizSupplier` |
| `formLayout` | 表单布局 | 按需 |
| `gridWhether` | 是否栅格布局 | 按需 |
| `packageName` | 包名 | 默认 `vip.xiaonuo` |
| `authorName` | 作者（写进 Javadoc @author） | 你的名字 |
| `genType` | 模板形态（见下表） | 按业务选 |
| `treeParentField` / `treeNameField` | 树形态：父字段 / 显示名字段 | 仅 TREE 形态填 |
| `subDbTable` / `subDbTableKey` / `subForeignKey` / `subClassName` | 主子表：子表名/主键/关联外键/子类名 | 仅 MASTER_DETAIL 填 |

## 模板形态选择（genType）

| genType | 形态 | 适用 | 典型例子 |
|---|---|---|---|
| `TABLE` | 普通表格 | 标准 CRUD 列表 | 供应商管理、公告管理 |
| `TREE` | 树形表格 | 有 parent_id 的层级数据 | 分类树、地区树 |
| `LEFT_TREE_TABLE` | 左树右表 | 左边选分类右边列数据 | 按分类组织的商品 |
| `MASTER_DETAIL` | 主子表 | 一对多，主表+子表一起编辑 | 订单+订单明细 |

模板目录：`snowy-plugin/snowy-plugin-gen/src/main/resources/` 下
`backend-{table,tree,left-tree-table,master-detail}` + `frontend-{table,tree,left-tree-table,master-detail}` + `mobile` + `sqlend`。

## sqlend：菜单资源 SQL（生成的关键产物之一）

生成器会产出 `SYS_RESOURCE` 的 INSERT 语句（模板：`snowy-plugin/snowy-plugin-gen/src/main/resources/sqlend/Mysql.sql.btl`）：

- 1 条 MENU 行：`INSERT INTO SYS_RESOURCE VALUES ('${menuId}', '${parentId}', '${functionName}管理', '${busName}', '${menuCode}', 'MENU', '${moduleId}', 'MENU', '${menuPath}', '${menuComponent}', ...)` 
- N 条 BUTTON 行：按钮码为**驼峰式** `${classNameFirstLower}Add / Edit / Delete / Detail / BatchDelete / Import / Export`（如 `bizSupplierAdd`）——这些码就是前端 `hasPerm('bizSupplierAdd')` 用的按钮权限码
- 配置了 mobileModule 时另有 MOBILE_RESOURCE 行（按钮码 `mobile{ClassName}Add` 形态）

id 值由生成器渲染时生成（字符串雪花）。**菜单 SQL 必须执行入库**，否则前端看不到菜单、按钮权限全失效。

## 生成后必做（AI 协助修补清单）

1. **落位**：确认文件在 `snowy-plugin-biz/src/main/java/vip/xiaonuo/biz/modular/{busName}/` 与前端 `src/views/biz/{busName}/`、`src/api/biz/biz{ClassName}Api.js`
2. **版权头**：逐个 .java 检查 12 行 Apache 2.0 版权声明（生成器可能按 authorName 生成变体，缺失必补）
3. **执行菜单 SQL**：sqlend 产出的 INSERT 执行到 snowy 库
4. **重启后端 + 刷新前端** → 系统管理 → 角色管理 → 给目标角色勾选新菜单与按钮授权（否则普通用户 403/按钮不显示）
5. **核对生成代码**：URL 是否 `/biz/{busName}/{动作}`、@SaCheckPermission 值是否等于 URL、Param 校验消息是否中文
6. **删除生成痕迹**：不需要的 import、注释里的模板变量残留
7. 涉及树/主子表时核对 treeParentField 与 subForeignKey 的实际业务正确性

## 生成器 API（程序化调用，进阶）

`GenBasicController`（`snowy-plugin/snowy-plugin-gen/.../basic/controller/GenBasicController.java`）提供：分页/详情/添加/编辑/删除/预览 preview/执行生成 execGenZip 等，路径前缀 `/gen/basic/*`。AI 一般不直接调这些接口——引导用户走界面，AI 负责生成后的核对修补。

## 常见问题

| 问题 | 处理 |
|---|---|
| 导入表列表为空 | 检查数据库连接（application.properties 的 dynamic master 段）与表是否已建 |
| 生成后菜单不显示 | 菜单 SQL 没执行，或没给角色授权 |
| 按钮全不显示 | BUTTON 行的驼峰码与前端 hasPerm 不匹配，核对 SYS_RESOURCE 的 code 列 |
| 生成的类名不对 | 检查 tablePrefix 是否正确移除（如 BIZ_SUPPLIER + 前缀 BIZ_ + className Supplier → BizSupplier） |
| 想改生成模板 | 模板在 snowy-plugin-gen/resources/ 下（.btl Beetl 文件），改后重启；属于改框架，谨慎 |

## 参考实现

| 文件 | 说明 |
|---|---|
| `snowy-plugin/snowy-plugin-gen/src/main/java/vip/xiaonuo/gen/modular/basic/entity/GenBasic.java` | 生成基础配置实体（全部字段） |
| `snowy-plugin/snowy-plugin-gen/src/main/java/vip/xiaonuo/gen/modular/basic/service/impl/GenBasicServiceImpl.java` | 生成逻辑（Beetl 渲染） |
| `snowy-plugin/snowy-plugin-gen/src/main/resources/sqlend/Mysql.sql.btl` | 菜单/按钮 SQL 模板（SYS_RESOURCE INSERT 的权威格式） |
| `snowy-plugin/snowy-plugin-gen/src/main/resources/backend-table/` | 后端六件套模板 |
| `snowy-plugin/snowy-plugin-gen/src/main/resources/frontend-table/` | 前端三件模板 |
| `snowy-plugin/snowy-plugin-biz/src/main/java/vip/xiaonuo/biz/modular/notice/` | 与生成产物同构的手写范本 |
