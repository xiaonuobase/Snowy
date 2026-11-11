---
name: code-generator
description: Snowy 自带 Beetl 代码生成器的使用规范：入口流程、GenBasic 字段填写、4 种模板形态选择（可另外附带 mobile 移动端模板）、sqlend 菜单 SQL、生成后修补清单。触发场景：1) 用户想用代码生成器生成功能 2) dev 流程技能模式 B 引导用户走生成器 3) 选择生成模板形态（表格/树/左树右表/主子表）4) 生成菜单资源 SQL。触发词：代码生成、生成器、gen、Beetl、模板、genType、sqlend、导入表、一键生成。注意：AI 直接手写六件套的规范见 crud-development；本技能讲平台自带生成器的正确用法。
---

# Snowy 代码生成器使用规范

## 概览

Snowy 自带 Beetl 模板代码生成器（`snowy-plugin-gen`），能从数据库表生成**后端六件套 + 前端三件 + 菜单资源 SQL** 的全套代码。这是开发标准 CRUD 的首选方式（对应 dev 流程技能（Claude Code、Qwen Code `/dev`，Codex `$dev`）的**模式 B**）。

**入口**：启动后端 → 登录前端 → 系统 → 在线开发 → 代码生成（路由 `/gen`）。数据存在 `GEN_BASIC` / `GEN_CONFIG` 表。

**生成产物 = 本项目 CRUD 标准结构**（生成器模板就是官方规范的定义源，手写代码要与生成产物一致）。**注意差异**：生成的 Entity **不继承** `CommonEntity`，而是逐字段声明审计字段（DELETE_FLAG 加 `@TableLogic`，审计字段加 `FieldFill`）；手写模式（crud-development）的 Entity 继承 CommonEntity。两种写法不要在同一个实体里混用。

## 使用流程（5 步）

```
1. 建表（按 database-ops 规范：表前缀 = 所在插件（biz 用 BIZ_，自建插件用插件名如 ORDER_）、大写、String 雪花主键、审计字段齐全）
2. 代码生成页面 → 导入表（选择刚建的表，可按住 Ctrl 选主表+子表做主子表）
3. 填写 GenBasic 基础配置（下表）
4. 在字段配置页调整每个字段的：是否查询/显示/必填、控件类型、字典编码等
5. 点"生成代码"→ zip 下载（或直接写入项目，取决于生成方式）→ 按"生成后必做"落地
```

## GenBasic 关键字段填写指南

| 字段 | 说明 | 业务插件二开的推荐值 |
|---|---|---|
| `dbTable` / `dbTableKey` | 主表名 / 主键（导入时自动带出） | — |
| `pluginName` | 插件模块目录名（PRO 项目内生成据此定位 `snowy-plugin/{pluginName}/src/main/java`，必须位于 snowy-plugin 目录内，不存在会报"后端代码生成位置不存在"） | `snowy-plugin-biz`（默认值，**不要**改成 `biz`）；自建插件填其目录名如 `snowy-plugin-order` |
| `moduleName` | 模块名：包名第二段、URL 第一段、前端 `src/api`/`src/views` 下的目录名、PRO 自动授权的接口前缀都用它 | 一般 `biz`；自建插件填插件缩写如 `order` |
| `tablePrefix` | 是否移除表前缀（Y/N 开关，只影响自动带出的 busName：Y 时去掉表名第一段，如 BIZ_SUPPLIER → busName `supplier`） | `Y`（默认） |
| `generateType` | 生成方式：ZIP 下载 / 项目路径写入 | 二开推荐 ZIP 后自查合入 |
| `module` | 所属系统模块（SYS_RESOURCE 的 MODULE，决定菜单挂在哪） | 选业务所属模块 |
| `menuPid` | 上级菜单/目录 id | 选业务菜单挂载点 |
| `mobileModule` | 移动端所属模块（留空则不生成移动端） | 一般留空 |
| `functionName` | 功能名（中文，用于菜单标题/日志） | 如 `供应商` |
| `busName` | 业务名（小写，URL 与前端目录名） | 如 `supplier` → `/biz/supplier/page` |
| `className` | 类名，原样用于所有类名和文件名，生成器**不会**自动加前缀；导入表时按完整表名自动带出（BIZ_SUPPLIER → BizSupplier，ORDER_PAY_RECORD → OrderPayRecord） | 表前缀与插件一致时直接用自动带出的值，**不要**删成 `Supplier` |
| `formLayout` | 表单布局 | 按需 |
| `gridWhether` | 是否栅格布局 | 按需 |
| `packageName` | 包名前缀，最终包 = `{packageName}.{moduleName}.modular.{busName}` | 默认 `vip.xiaonuo`（→ `vip.xiaonuo.biz.modular.*`）；可自定义如 `com.company`（→ `com.company.order.modular.*`），非 vip.xiaonuo 时须按 plugin-architecture 补扫描配置 |
| `authorName` | 作者（写进 Javadoc @author） | 你的名字 |
| `genType` | 模板形态（见下表） | 按业务选 |
| `treeParentField` / `treeNameField` | 树配置：父字段 / 显示名字段 | TREE、LEFT_TREE_TABLE（左树）填 |
| `subDbTable` / `subDbTableKey` / `subForeignKey` / `subClassName` / `subFunctionName` / `subBusName` | 子表（左树右表的右表）：表名/主键/关联外键/类名/功能名/业务名 | MASTER_DETAIL、LEFT_TREE_TABLE（右表）填 |

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
- N 条 BUTTON 行：按钮码为**驼峰式** `${classNameFirstLower}` + 动作（如 `bizSupplierAdd`）——这些码就是前端 `hasPerm('bizSupplierAdd')` 用的按钮权限码。按钮集合以 sqlend 模板为准：

| genType | 按钮码后缀 |
|---|---|
| TABLE / TREE | `Add / Edit / Delete / Detail / BatchDelete / Import / Export` |
| LEFT_TREE_TABLE | 左树：`TreeAdd / TreeEdit / TreeDelete / TreeDetail`；右表：`Add / Edit / Delete / Detail / BatchDelete / Import / Export` |
| MASTER_DETAIL | 主表：`Add / Edit / Delete / Detail / BatchDelete / Import / Export`；子表：`SubAdd / SubEdit / SubDelete / SubDetail / SubBatchDelete / SubImport / SubExport` |

- 生成器同时产出 detail.vue 详情页（主子表另有 subDetail.vue），对应 Detail / SubDetail 按钮
- 配置了 mobileModule 时另有 MOBILE_RESOURCE 行（按钮码 `mobile{ClassName}Add` 形态）

id 值由生成器渲染时生成（字符串雪花）。**ZIP 方式**：sqlend 产出的菜单 SQL 必须手动执行入库，否则前端看不到菜单、按钮权限全失效；**PRO（项目内）方式**：生成器直接写入菜单和按钮，并授权给内置超管角色，不需要再执行 SQL（重复执行会产生重复菜单）。

## 生成后必做（AI 协助修补清单）

1. **落位**：后端在 `snowy-plugin/{pluginName}/src/main/java/{packageName 转路径}/{moduleName}/modular/{busName}/`（默认即 `snowy-plugin-biz/src/main/java/vip/xiaonuo/biz/modular/{busName}/`），前端在 `src/views/{moduleName}/{busName}/`、`src/api/{moduleName}/{classNameFirstLower}Api.js`（如 `bizSupplierApi.js`）
2. **版权头**：生成器后端 Java 模板自带小诺 12 行版权头（Mapper XML 无），不需要补；生成产物属于用户业务代码，不强制保留，用户有自己的版权头规范时按用户的来替换或删除
3. **扫描配置（仅自定义包名）**：packageName 不以 vip.xiaonuo 开头时，按 plugin-architecture"使用自定义包名"补齐 snowy-web-app 的 3 处配置，否则重启后 Bean/Mapper 不生效
4. **执行菜单 SQL（仅 ZIP）**：sqlend 产出的 INSERT 执行到 snowy 库
5. **重启后端 + 刷新前端** → 角色管理 → 给超管以外的目标角色「授权资源」（新菜单与按钮）并「授权权限」（勾选生成的接口 URL，否则 403/按钮不显示）。PRO 方式已自动给超管（`superAdmin`）授权菜单、按钮和生成的接口 URL；ZIP 方式不会给任何角色授权，超管也要按上述两步手动授权
6. **核对生成代码**：URL 是否 `/{moduleName}/{busName}/{动作}`、@SaCheckPermission 值是否等于 URL、Param 校验消息是否中文
7. **删除生成痕迹**：不需要的 import、注释里的模板变量残留
8. 涉及树/主子表时核对 treeParentField 与 subForeignKey 的实际业务正确性

## 生成器 API（程序化调用，进阶）

`GenBasicController`（`snowy-plugin/snowy-plugin-gen/.../basic/controller/GenBasicController.java`）提供：分页/详情/添加/编辑/删除/预览 previewGen/ZIP 生成 execGenZip/项目内生成 execGenPro 等，路径前缀 `/gen/basic/*`。AI 一般不直接调这些接口——引导用户走界面，AI 负责生成后的核对修补。

## 常见问题

| 问题 | 处理 |
|---|---|
| 导入表列表为空 | 检查数据库连接（application.properties 的 dynamic master 段）与表是否已建 |
| 生成后菜单不显示 | 菜单 SQL 没执行（ZIP 方式），或没给角色「授权资源」 |
| 按钮全不显示 | BUTTON 行的驼峰码与前端 hasPerm 不匹配，核对 SYS_RESOURCE 的 code 列 |
| 生成的类名不对 | 检查 className 是否带所在插件缩写前缀（biz 为 Biz；生成器原样使用 className，不受 tablePrefix 影响） |
| 自定义包名生成后接口 404 / Mapper 注入失败 | 没补扫描配置，见 plugin-architecture"使用自定义包名" |
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
