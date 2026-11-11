---
name: database-ops
description: Snowy 数据库设计规范：大写表名、表前缀 = 所在插件（biz 用 BIZ_，自建插件用插件名）、String 雪花主键、审计字段组、建表模板、菜单/按钮 SYS_RESOURCE SQL、SQL 脚本落点、多数据源。触发场景：1) 新功能设计建表 2) 写菜单/按钮资源 SQL 3) 查表结构或写查询 SQL 4) 配置数据源。触发词：数据库、建表、SQL、表设计、菜单SQL、SYS_RESOURCE、字典数据、主键、雪花、DELETE_FLAG、数据源、dynamic。
---

# Snowy 数据库设计规范

## 命名规范

| 项 | 规则 | 示例 |
|---|---|---|
| 表名 | **全大写下划线**，前缀 = 归属插件 | `BIZ_SUPPLIER`、`ORDER_PAY_RECORD`、`SYS_USER`、`DEV_CONFIG` |
| 字段名 | 全大写下划线 | `SUPPLIER_NAME`、`CREATE_TIME` |
| 新业务表前缀 | biz 插件用 **`BIZ_`**；自建插件用**插件名**（`snowy-plugin-order` → `ORDER_`），与类名前缀一致（`ORDER_PAY_RECORD` → `OrderPayRecord`）。出厂 biz 域复用 SYS_/DEV_ 表是历史设计，**新表不要模仿** | `BIZ_SUPPLIER`、`ORDER_PAY_RECORD` |
| 主键 | `ID varchar(20)` 字符串雪花（MyBatis-Plus ASSIGN_ID） | 禁止 bigint 自增 |
| 每表必有 | 表 COMMENT + 每字段 COMMENT（**中文**） | — |
| 字符集 | utf8mb4 / utf8mb4_general_ci，InnoDB | — |

## 标准建表模板（复制改业务字段）

```sql
CREATE TABLE `BIZ_XXX`  (
  `ID` varchar(20) NOT NULL COMMENT '主键',
  -- ↓↓↓ 业务字段区（全大写，带中文 COMMENT）↓↓↓
  `NAME` varchar(100) NULL DEFAULT NULL COMMENT '名称',
  `STATUS` varchar(10) NULL DEFAULT NULL COMMENT '状态',
  -- ↓↓↓ 尾部固定字段组（顺序保持一致）↓↓↓
  `SORT_CODE` int(11) NULL DEFAULT NULL COMMENT '排序',
  `REMARK` varchar(500) NULL DEFAULT NULL COMMENT '备注',
  `EXT_JSON` longtext NULL COMMENT '扩展信息',
  `DELETE_FLAG` varchar(255) NULL DEFAULT NULL COMMENT '删除标志',
  `CREATE_TIME` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `CREATE_USER` varchar(20) NULL DEFAULT NULL COMMENT '创建用户',
  `UPDATE_TIME` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `UPDATE_USER` varchar(20) NULL DEFAULT NULL COMMENT '更新用户',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'XXX表' ROW_FORMAT = Dynamic;
```

要点：
- 尾部字段中 DELETE_FLAG（逻辑删除，值 NOT_DELETE/DELETED）、CREATE_TIME、CREATE_USER、UPDATE_TIME、UPDATE_USER 五个由 `CommonEntity` 提供（逻辑删除 + 自动填充，代码里**不要**手动赋值）；SORT_CODE（默认排序）、REMARK、EXT_JSON（扩展 JSON）**不在** CommonEntity 中，Entity 要自己声明 sortCode / remark / extJson 属性（参考 BizNotice）
- 树形表加 `PARENT_ID varchar(20) NULL COMMENT '父id'`
- 类型映射：String→varchar(n)/text、Integer→int、BigDecimal→decimal(总长,小数)、日期时间→datetime、大文本→longtext
- 需要加密落库的字段（手机号等）正常建 varchar，加密由 `CommonSm4CbcTypeHandler` 在应用层做（密文更长，长度适当放宽）

## 逻辑删除

- 字段 `DELETE_FLAG`，有效值 `NOT_DELETE` / `DELETED`（见 `CommonDeleteFlagEnum`）
- Entity 继承 CommonEntity 自动带 @TableLogic，查询自动过滤、removeById 自动改 UPDATE
- 代码中**禁止**手写 `delete_flag` 条件、**禁止**物理 delete 语句（物理删除有专门的 CommonDeleteAbsoluteMapper，慎用）
- 注意：唯一索引与逻辑删除冲突时：不要直接对业务列建唯一索引（删除后同名记录无法再新增），默认在 Service 层查询判重
- 注意：Service 先查后写**挡不住并发**（两个请求同时通过判重）。名称/编码等强唯一需求，再加 MySQL 生成列唯一约束兜底，Service 捕获 `DuplicateKeyException` 转 `CommonException`：

```sql
`ACTIVE_NAME` varchar(100) GENERATED ALWAYS AS (CASE WHEN `DELETE_FLAG` = 'NOT_DELETE' THEN `NAME` ELSE NULL END) VIRTUAL COMMENT '未删除名称唯一约束辅助列',
UNIQUE KEY `UK_BIZ_XXX_ACTIVE_NAME` (`ACTIVE_NAME`)
```

  生成列**不要**在 Entity 里声明（MP 会尝试写入而报错）；需 MySQL 5.7+，切换其他数据库时需改写

## 菜单与按钮资源 SQL（SYS_RESOURCE）

新功能的菜单 + 按钮权限写 INSERT（权威格式见 gen 插件 sqlend 模板）：

```sql
-- 菜单（MENU 行）
INSERT INTO `SYS_RESOURCE` VALUES ('菜单id雪花串', '父目录id', '供应商管理', 'supplier', '菜单编码', 'MENU', '所属模块id', 'MENU', '/biz/supplier', 'biz/supplier/index', '图标', NULL, 'YES', 'YES', 'YES', 99, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
-- 按钮（BUTTON 行，parent = 菜单id；code = 驼峰按钮码，前端 hasPerm 用）
INSERT INTO `SYS_RESOURCE` VALUES ('按钮id雪花串', '菜单id雪花串', '新增供应商', NULL, 'bizSupplierAdd', 'BUTTON', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO `SYS_RESOURCE` VALUES ('...', '菜单id雪花串', '编辑供应商', NULL, 'bizSupplierEdit', 'BUTTON', ...);
INSERT INTO `SYS_RESOURCE` VALUES ('...', '菜单id雪花串', '删除供应商', NULL, 'bizSupplierDelete', 'BUTTON', ...);
```

- 按钮码集合以 gen 插件 sqlend 模板为准：单表/树/主表为 `{classNameFirstLower}Add / Edit / Delete / Detail / BatchDelete / Import / Export`；左树右表另有 `TreeAdd / TreeEdit / TreeDelete / TreeDetail`；主子表另有 `SubAdd / SubEdit / SubDelete / SubDetail / SubBatchDelete / SubImport / SubExport`（另加业务动作码，如 `UpdateStatus`）
- 需要字典数据时：`INSERT INTO DEV_DICT`（系统字典 CATEGORY=FRM、业务字典 CATEGORY=BIZ，同一张表；字典编码存在 PARENT_ID='0' 那一行的 DICT_VALUE），或在 系统运维 → 数据字典 界面新建（详见 dict-config）
- 执行后：重启后端 → 角色管理给角色「授权资源」（菜单/按钮，前端可见）并「授权权限」（勾选新接口 URL，否则 403；手写的新接口超管也要勾选）

### 菜单挂载点速查（出厂种子的真实 ID，写 SQL 直接用）

新业务功能默认挂 **业务模块**（`MODULE` 列 = `1548901111999773976`；SYS_RESOURCE 没有 MODULE_ID 列）：

| 挂载点 | ID | 说明 |
|---|---|---|
| 业务模块（MODULE） | `1548901111999773976` | 新业务菜单的 MODULE 列用这个 |
| 公司架构目录（CATALOG） | `1548901111999773977` | 组织/人员类业务挂这个目录下 |
| 业务模块顶层 | PARENT_ID 填 `'0'` | 通用做法：独立业务直接挂模块顶层（同"通知公告""业务字典"） |

系统模块（`1548901111999770525`）下的目录仅平台功能使用：组织架构 `1548901111999770726`、权限管控 `1548901111999771126`、基础工具 `1548901111999771626`、系统运维 `1548901111999772126`、在线开发 `1548901111999773250`。

> 依据：`snowy_mysql.sql` 种子数据（全新导入即生效）。若库已被人工调整过，用 `SELECT ID, TITLE, CATEGORY, PARENT_ID, MODULE FROM SYS_RESOURCE WHERE CATEGORY IN ('MODULE','MENU')` 核实后再用。前端菜单 component 路径 `{插件缩写}/{域名}/index`（biz 插件为 `biz/{域名}/index`）必须与 views 目录一致。

## 数据库连接与多数据源

- 连接配置：`snowy-web-app/src/main/resources/application.properties` 的 `spring.datasource.dynamic.master` 段（**AI 需要连接串时从这里动态解析，禁止硬编码**）
- 多数据源：dynamic-datasource，默认 master。配置里 postgres/oracle/mssql/dm/kingbase 的注释块是 **master 的换库模板**（键名同为 `spring.datasource.dynamic.datasource.master.*`，启用时要同时注释掉 mysql 段），用于切换数据库类型，不是新增数据源。真要加第二个库，新增 `spring.datasource.dynamic.datasource.{名称}.*`，再在 Service/Mapper 上用 `@DS("{名称}")` 切换
- Druid 监控页：`/druid`（账号见配置）
- 初始化脚本：`snowy-web-app/src/main/resources/_sql/snowy_mysql.sql`（33 张表）；新增表的 DDL 也建议在 `_sql/` 下追加增量脚本文件

## AI 执行 SQL 的方式（降级链）

1. mysql CLI 可用 → 直接执行（从 application.properties 解析连接）
2. CLI 不可用 → SQL 写入 `docs/sql-pending/{日期}-{功能}.sql`，明确提示用户手动执行
3. 禁止在没有明确用户确认时执行 DROP/TRUNCATE/ALTER 之类破坏性语句

## AI 自检 SQL 集（开发流程中随用随查）

```sql
-- 表建好了吗
SHOW TABLES LIKE 'BIZ_XXX';
-- 表结构（写 Entity 前核对字段）
SHOW CREATE TABLE BIZ_XXX;

-- 菜单/按钮插进去了吗（配合挂载点速查的 ID）
SELECT ID, TITLE, CATEGORY, PARENT_ID, CODE FROM SYS_RESOURCE WHERE CODE LIKE 'bizXxx%' OR ID = '菜单id';
-- 按钮码与前端 hasPerm 是否一一对应
SELECT CODE, TITLE FROM SYS_RESOURCE WHERE CATEGORY = 'BUTTON' AND PARENT_ID = '菜单id';

-- 验证落库与逻辑删除（配合 api-verify 接口自测）
SELECT ID, NAME, DELETE_FLAG, CREATE_TIME FROM BIZ_XXX ORDER BY CREATE_TIME DESC LIMIT 3;

-- 字典建好了吗（字典编码在顶层行的 DICT_VALUE，CODE 列是随机串，不要按 CODE 查）
SELECT ID, DICT_LABEL, DICT_VALUE, CATEGORY FROM DEV_DICT WHERE PARENT_ID = '0' AND DICT_VALUE = 'XXX_DICT_CODE';
-- 默认密码配置（出厂值 Snowy@2026!；2026-06-26 之前导入的旧库为 123456）
SELECT CONFIG_VALUE FROM DEV_CONFIG WHERE CONFIG_KEY = 'SNOWY_SYS_DEFAULT_PASSWORD_FOR_B';
```

## 现有 33 表速查（按前缀分组）

| 前缀 | 表 |
|---|---|
| SYS_（12） | SYS_USER、SYS_USER_EXT、SYS_USER_PASSWORD、SYS_USER_DATA_SCOPE、SYS_USER_DATA_SCOPE_MAP、SYS_ORG、SYS_ORG_EXT、SYS_POSITION、SYS_ROLE、SYS_GROUP、SYS_RESOURCE（模块/菜单/按钮都在这张表，用 CATEGORY 区分）、SYS_RELATION（用户-角色、角色-资源、用户-组等各类关系统一存放，CATEGORY 如 SYS_USER_HAS_ROLE / SYS_ROLE_HAS_RESOURCE / SYS_USER_HAS_GROUP） |
| BIZ_（1） | BIZ_NOTICE |
| DEV_（12） | DEV_CONFIG、DEV_DICT、DEV_EMAIL、DEV_FILE、DEV_JOB、DEV_LOG、DEV_MESSAGE、DEV_PUSH、DEV_RELATION、DEV_SLIDESHOW、DEV_SMS、DEV_WEAK_PASSWORD |
| GEN_（2） | GEN_BASIC、GEN_CONFIG |
| CLIENT_（4） | CLIENT_USER、CLIENT_USER_EXT、CLIENT_USER_PASSWORD、CLIENT_RELATION |
| MOBILE_（1） | MOBILE_RESOURCE |
| AUTH_（1） | AUTH_THIRD_USER |

没有单独的模块表、用户角色表、角色菜单表、用户组关系表（准确清单以 `_sql/snowy_mysql.sql` 为准，可 grep `CREATE TABLE`）

## 检查清单

- [ ] 表名前缀 = 所在插件（biz 用 BIZ_，自建插件用插件名如 ORDER_）、全大写，有表 COMMENT
- [ ] ID varchar(20) 主键，非自增
- [ ] 尾部审计字段组齐全且顺序标准
- [ ] 每字段中文 COMMENT
- [ ] 树形表有 PARENT_ID；一对多有外键字段（varchar(20)）
- [ ] 菜单/按钮 SQL 与代码 URL、前端 hasPerm 码一致
- [ ] DDL 增量脚本落 `_sql/`

## 参考实现

| 文件 | 说明 |
|---|---|
| `snowy-web-app/src/main/resources/_sql/snowy_mysql.sql` | 全部建表语句（BIZ_NOTICE 是标准范本） |
| `snowy-plugin/snowy-plugin-biz/src/main/java/vip/xiaonuo/biz/modular/notice/entity/BizNotice.java` | Entity 与表的映射范本 |
| `snowy-common/src/main/java/vip/xiaonuo/common/pojo/CommonEntity.java` | 审计字段基类 |
| `snowy-common/src/main/java/vip/xiaonuo/common/enums/CommonDeleteFlagEnum.java` | 逻辑删除枚举 |
| `snowy-plugin/snowy-plugin-gen/src/main/resources/sqlend/Mysql.sql.btl` | 菜单/按钮 SQL 权威模板 |
| `snowy-web-app/src/main/resources/application.properties` | 数据源配置 |
