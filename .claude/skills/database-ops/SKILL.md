---
name: database-ops
description: Snowy 数据库设计规范：大写表名、BIZ_ 前缀、String 雪花主键、审计字段组、建表模板、菜单/按钮 SYS_RESOURCE SQL、SQL 脚本落点、多数据源。触发场景：1) 新功能设计建表 2) 写菜单/按钮资源 SQL 3) 查表结构或写查询 SQL 4) 配置数据源。触发词：数据库、建表、SQL、表设计、菜单SQL、SYS_RESOURCE、字典数据、主键、雪花、DELETE_FLAG、数据源、dynamic。
---

# Snowy 数据库设计规范

## 命名规范

| 项 | 规则 | 示例 |
|---|---|---|
| 表名 | **全大写下划线**，前缀 = 归属插件 | `BIZ_SUPPLIER`、`SYS_USER`、`DEV_CONFIG` |
| 字段名 | 全大写下划线 | `SUPPLIER_NAME`、`CREATE_TIME` |
| 新业务表前缀 | **`BIZ_`**（出厂 biz 域复用 SYS_/DEV_ 表是历史设计，**新表不要模仿**） | `BIZ_ORDER` |
| 主键 | `ID varchar(20)` 字符串雪花（MyBatis-Plus ASSIGN_ID） | ❌ bigint 自增 |
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
- 尾部字段组与 `CommonEntity` 一一对应：DELETE_FLAG（逻辑删除，值 NOT_DELETE/DELETED）、CREATE_TIME/CREATE_USER/UPDATE_TIME/UPDATE_USER（自动填充，代码里**不要**手动赋值）、SORT_CODE（默认排序）、REMARK、EXT_JSON（扩展 JSON）
- 树形表加 `PARENT_ID varchar(20) NULL COMMENT '父id'`
- 类型映射：String→varchar(n)/text、Integer→int、BigDecimal→decimal(总长,小数)、日期时间→datetime、大文本→longtext
- 需要加密落库的字段（手机号等）正常建 varchar，加密由 `CommonSm4CbcTypeHandler` 在应用层做（密文更长，长度适当放宽）

## 逻辑删除

- 字段 `DELETE_FLAG`，有效值 `NOT_DELETE` / `DELETED`（见 `CommonDeleteFlagEnum`）
- Entity 继承 CommonEntity 自动带 @TableLogic，查询自动过滤、removeById 自动改 UPDATE
- 代码中**禁止**手写 `delete_flag` 条件、**禁止**物理 delete 语句（物理删除有专门的 CommonDeleteAbsoluteMapper，慎用）
- ⚠️ 唯一索引与逻辑删除冲突时：不要建数据库唯一索引，改为 Service 层查询判重

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

- 按钮码固定集合：`{classNameFirstLower}Add / Edit / Delete / Detail / BatchDelete / Import / Export`（+ 业务动作码如 `UpdateStatus`）
- 需要字典数据时：`INSERT INTO DEV_DICT`（系统字典）或 BIZ 字典管理界面维护
- 执行后：重启后端 → 角色管理给角色授权 → 前端可见

### 菜单挂载点速查（出厂种子的真实 ID，写 SQL 直接用）

新业务功能默认挂 **业务模块**（MODULE_ID=`1548901111999773976`）：

| 挂载点 | ID | 说明 |
|---|---|---|
| 业务模块（MODULE） | `1548901111999773976` | 新业务菜单的 MODULE_ID 用这个 |
| 公司架构目录（CATALOG） | `1548901111999773977` | 组织/人员类业务挂这个目录下 |
| 业务模块顶层 | PARENT_ID 填 `'0'` | 通用做法：独立业务直接挂模块顶层（同"通知公告""业务字典"） |

系统模块（`1548901111999770525`）下的目录仅平台功能使用：组织架构 `1548901111999770726`、权限管控 `1548901111999771126`、基础工具 `1548901111999771626`、系统运维 `1548901111999772126`、在线开发 `1548901111999773250`。

> 依据：`snowy_mysql.sql` 种子数据（全新导入即生效）。若库已被人工调整过，用 `SELECT ID, TITLE, CATEGORY, P_ID FROM SYS_RESOURCE` 核实后再用。前端菜单 component 路径 `biz/{域名}/index` 必须与 views 目录一致。

## 数据库连接与多数据源

- 连接配置：`snowy-web-app/src/main/resources/application.properties` 的 `spring.datasource.dynamic.master` 段（**AI 需要连接串时从这里动态解析，禁止硬编码**）
- 多数据源：dynamic-datasource，默认 master；pgSql/oracle/oracleLake/mssql/dm/kingbase 配置已注释预留，取消注释即可加库
- Druid 监控页：`/druid`（账号见配置）
- 初始化脚本：`snowy-web-app/src/main/resources/_sql/snowy_mysql.sql`（33 张表）；新增表的 DDL 也建议在 `_sql/` 下追加增量脚本文件

## AI 执行 SQL 的方式（降级链）

1. mysql CLI 可用 → 直接执行（从 application.properties 解析连接）
2. CLI 不可用 → SQL 写入 `docs/sql-pending/{日期}-{功能}.sql`，明确提示用户手动执行
3. ❌禁止在没有明确用户确认时执行 DROP/TRUNCATE/ALTER 之类破坏性语句

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

-- 字典建好了吗
SELECT * FROM DEV_DICT WHERE CODE = 'XXX_DICT_CODE';
-- 默认密码配置（出厂值 Snowy@2026!）
SELECT CONFIG_VALUE FROM DEV_CONFIG WHERE CONFIG_KEY = 'SNOWY_SYS_DEFAULT_PASSWORD_FOR_B';
```

## 现有 33 表速查（按前缀分组）

| 前缀 | 表 |
|---|---|
| SYS_ | SYS_USER、SYS_USER_EXT、SYS_ORG、SYS_POSITION、SYS_ROLE、SYS_RESOURCE、SYS_MODULE、SYSRelation 相关（SYS_USER_ROLE、SYS_ROLE_MENU 等）、SYS_USER_DATA_SCOPE(_MAP)、SYS_GROUP、SYS_GROUP_USER |
| BIZ_ | BIZ_NOTICE |
| DEV_ | DEV_CONFIG、DEV_DICT、DEV_EMAIL、DEV_FILE、DEV_JOB、DEV_LOG、DEV_MESSAGE、DEV_PUSH、DEV_SMS、DEV_SLIDESHOW、DEV_WEAK_PASSWORD |
| GEN_ | GEN_BASIC、GEN_CONFIG |
| CLIENT_ | CLIENT_USER |
| MOBILE_ | MOBILE_RESOURCE、（移动端模块表） |
| AUTH_ | AUTH_*（三方登录/关系相关） |

（准确清单以 `_sql/snowy_mysql.sql` 为准，可 grep `CREATE TABLE`）

## 检查清单

- [ ] 表名 BIZ_ 前缀全大写，有表 COMMENT
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
