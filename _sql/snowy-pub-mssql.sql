/*
 Navicat Premium Data Transfer

 Source Server         : 本机sqlServer
 Source Server Type    : SQL Server
 Source Server Version : 15002000
 Source Host           : localhost:1433
 Source Catalog        : snowy-pub
 Source Schema         : dbo

 Target Server Type    : SQL Server
 Target Server Version : 15002000
 File Encoding         : 65001

 Date: 09/04/2021 19:09:19
*/


-- ----------------------------
-- Table structure for sys_app
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[sys_app]') AND type IN ('U'))
	DROP TABLE [dbo].[sys_app]
GO

CREATE TABLE [dbo].[sys_app] (
  [id] bigint  NOT NULL,
  [name] nvarchar(100) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [code] nvarchar(50) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [active] nvarchar(1) COLLATE Chinese_PRC_CI_AS  NULL,
  [status] tinyint  NOT NULL,
  [create_time] datetime2(7)  NULL,
  [create_user] bigint  NULL,
  [update_time] datetime2(7)  NULL,
  [update_user] bigint  NULL
)
GO

ALTER TABLE [dbo].[sys_app] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
'MS_Description', N'主键id',
'SCHEMA', N'dbo',
'TABLE', N'sys_app',
'COLUMN', N'id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'应用名称',
'SCHEMA', N'dbo',
'TABLE', N'sys_app',
'COLUMN', N'name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'编码',
'SCHEMA', N'dbo',
'TABLE', N'sys_app',
'COLUMN', N'code'
GO

EXEC sp_addextendedproperty
'MS_Description', N'是否默认激活（Y-是，N-否）',
'SCHEMA', N'dbo',
'TABLE', N'sys_app',
'COLUMN', N'active'
GO

EXEC sp_addextendedproperty
'MS_Description', N'状态（字典 0正常 1停用 2删除）',
'SCHEMA', N'dbo',
'TABLE', N'sys_app',
'COLUMN', N'status'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建时间',
'SCHEMA', N'dbo',
'TABLE', N'sys_app',
'COLUMN', N'create_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建人',
'SCHEMA', N'dbo',
'TABLE', N'sys_app',
'COLUMN', N'create_user'
GO

EXEC sp_addextendedproperty
'MS_Description', N'修改时间',
'SCHEMA', N'dbo',
'TABLE', N'sys_app',
'COLUMN', N'update_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'修改人',
'SCHEMA', N'dbo',
'TABLE', N'sys_app',
'COLUMN', N'update_user'
GO

EXEC sp_addextendedproperty
'MS_Description', N'系统应用表',
'SCHEMA', N'dbo',
'TABLE', N'sys_app'
GO


-- ----------------------------
-- Records of sys_app
-- ----------------------------
INSERT INTO [dbo].[sys_app]  VALUES (N'1265476890672672821', N'系统应用', N'system', N'Y', N'0', N'2020-03-25 19:07:00.0000000', N'1265476890672672808', N'2020-08-15 15:23:05.0000000', N'1280709549107552257')
GO

INSERT INTO [dbo].[sys_app]  VALUES (N'1265476890672672822', N'业务应用', N'business', N'N', N'2', N'2020-03-26 08:40:33.0000000', N'1265476890672672808', N'2020-09-23 22:00:01.0000000', N'1265476890672672808')
GO

INSERT INTO [dbo].[sys_app]  VALUES (N'1342445032647098369', N'系统工具', N'system_tool', N'N', N'0', N'2020-12-25 20:20:12.0000000', N'1265476890672672808', NULL, NULL)
GO


-- ----------------------------
-- Table structure for sys_code_generate
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[sys_code_generate]') AND type IN ('U'))
	DROP TABLE [dbo].[sys_code_generate]
GO

CREATE TABLE [dbo].[sys_code_generate] (
  [id] bigint  NOT NULL,
  [author_name] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [class_name] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [table_prefix] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [generate_type] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [table_name] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [package_name] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [bus_name] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [table_comment] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [create_user] bigint  NULL,
  [create_time] datetime2(7)  NULL,
  [update_user] bigint  NULL,
  [update_time] datetime2(7)  NULL
)
GO

ALTER TABLE [dbo].[sys_code_generate] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
'MS_Description', N'主键',
'SCHEMA', N'dbo',
'TABLE', N'sys_code_generate',
'COLUMN', N'id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'作者姓名',
'SCHEMA', N'dbo',
'TABLE', N'sys_code_generate',
'COLUMN', N'author_name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'类名',
'SCHEMA', N'dbo',
'TABLE', N'sys_code_generate',
'COLUMN', N'class_name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'是否移除表前缀',
'SCHEMA', N'dbo',
'TABLE', N'sys_code_generate',
'COLUMN', N'table_prefix'
GO

EXEC sp_addextendedproperty
'MS_Description', N'生成位置类型',
'SCHEMA', N'dbo',
'TABLE', N'sys_code_generate',
'COLUMN', N'generate_type'
GO

EXEC sp_addextendedproperty
'MS_Description', N'数据库表名',
'SCHEMA', N'dbo',
'TABLE', N'sys_code_generate',
'COLUMN', N'table_name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'包名称',
'SCHEMA', N'dbo',
'TABLE', N'sys_code_generate',
'COLUMN', N'package_name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'业务名',
'SCHEMA', N'dbo',
'TABLE', N'sys_code_generate',
'COLUMN', N'bus_name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'功能名',
'SCHEMA', N'dbo',
'TABLE', N'sys_code_generate',
'COLUMN', N'table_comment'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建人',
'SCHEMA', N'dbo',
'TABLE', N'sys_code_generate',
'COLUMN', N'create_user'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建时间',
'SCHEMA', N'dbo',
'TABLE', N'sys_code_generate',
'COLUMN', N'create_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'更新人',
'SCHEMA', N'dbo',
'TABLE', N'sys_code_generate',
'COLUMN', N'update_user'
GO

EXEC sp_addextendedproperty
'MS_Description', N'更新时间',
'SCHEMA', N'dbo',
'TABLE', N'sys_code_generate',
'COLUMN', N'update_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'代码生成基础配置',
'SCHEMA', N'dbo',
'TABLE', N'sys_code_generate'
GO


-- ----------------------------
-- Records of sys_code_generate
-- ----------------------------
INSERT INTO [dbo].[sys_code_generate]  VALUES (N'1362310959781744641', N'yubaoshan、xuyuxiang、dongxiayu', N'CodeGenTest', N'Y', N'2', N'xn_code_gen_test', N'vip.xiaonuo', N'codegentest', N'测试', N'1265476890672672808', N'2021-02-18 16:00:18.0000000', NULL, NULL)
GO


-- ----------------------------
-- Table structure for sys_code_generate_config
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[sys_code_generate_config]') AND type IN ('U'))
	DROP TABLE [dbo].[sys_code_generate_config]
GO

CREATE TABLE [dbo].[sys_code_generate_config] (
  [id] bigint  NOT NULL,
  [code_gen_id] bigint  NULL,
  [column_name] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [java_name] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [data_type] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [column_comment] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [java_type] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [effect_type] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [dict_type_code] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [whether_table] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [whether_add_update] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [whether_retract] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [whether_required] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [query_whether] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [query_type] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [column_key] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [column_key_name] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [whether_common] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [create_time] datetime2(7)  NULL,
  [create_user] bigint  NULL,
  [update_time] datetime2(7)  NULL,
  [update_user] bigint  NULL
)
GO

ALTER TABLE [dbo].[sys_code_generate_config] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
'MS_Description', N'主键',
'SCHEMA', N'dbo',
'TABLE', N'sys_code_generate_config',
'COLUMN', N'id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'代码生成主表ID',
'SCHEMA', N'dbo',
'TABLE', N'sys_code_generate_config',
'COLUMN', N'code_gen_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'数据库字段名',
'SCHEMA', N'dbo',
'TABLE', N'sys_code_generate_config',
'COLUMN', N'column_name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'java类字段名',
'SCHEMA', N'dbo',
'TABLE', N'sys_code_generate_config',
'COLUMN', N'java_name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'物理类型',
'SCHEMA', N'dbo',
'TABLE', N'sys_code_generate_config',
'COLUMN', N'data_type'
GO

EXEC sp_addextendedproperty
'MS_Description', N'字段描述',
'SCHEMA', N'dbo',
'TABLE', N'sys_code_generate_config',
'COLUMN', N'column_comment'
GO

EXEC sp_addextendedproperty
'MS_Description', N'java类型',
'SCHEMA', N'dbo',
'TABLE', N'sys_code_generate_config',
'COLUMN', N'java_type'
GO

EXEC sp_addextendedproperty
'MS_Description', N'作用类型（字典）',
'SCHEMA', N'dbo',
'TABLE', N'sys_code_generate_config',
'COLUMN', N'effect_type'
GO

EXEC sp_addextendedproperty
'MS_Description', N'字典code',
'SCHEMA', N'dbo',
'TABLE', N'sys_code_generate_config',
'COLUMN', N'dict_type_code'
GO

EXEC sp_addextendedproperty
'MS_Description', N'列表展示',
'SCHEMA', N'dbo',
'TABLE', N'sys_code_generate_config',
'COLUMN', N'whether_table'
GO

EXEC sp_addextendedproperty
'MS_Description', N'增改',
'SCHEMA', N'dbo',
'TABLE', N'sys_code_generate_config',
'COLUMN', N'whether_add_update'
GO

EXEC sp_addextendedproperty
'MS_Description', N'列表是否缩进（字典）',
'SCHEMA', N'dbo',
'TABLE', N'sys_code_generate_config',
'COLUMN', N'whether_retract'
GO

EXEC sp_addextendedproperty
'MS_Description', N'是否必填（字典）',
'SCHEMA', N'dbo',
'TABLE', N'sys_code_generate_config',
'COLUMN', N'whether_required'
GO

EXEC sp_addextendedproperty
'MS_Description', N'是否是查询条件',
'SCHEMA', N'dbo',
'TABLE', N'sys_code_generate_config',
'COLUMN', N'query_whether'
GO

EXEC sp_addextendedproperty
'MS_Description', N'查询方式',
'SCHEMA', N'dbo',
'TABLE', N'sys_code_generate_config',
'COLUMN', N'query_type'
GO

EXEC sp_addextendedproperty
'MS_Description', N'主键',
'SCHEMA', N'dbo',
'TABLE', N'sys_code_generate_config',
'COLUMN', N'column_key'
GO

EXEC sp_addextendedproperty
'MS_Description', N'主外键名称',
'SCHEMA', N'dbo',
'TABLE', N'sys_code_generate_config',
'COLUMN', N'column_key_name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'是否是通用字段',
'SCHEMA', N'dbo',
'TABLE', N'sys_code_generate_config',
'COLUMN', N'whether_common'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建时间',
'SCHEMA', N'dbo',
'TABLE', N'sys_code_generate_config',
'COLUMN', N'create_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建人',
'SCHEMA', N'dbo',
'TABLE', N'sys_code_generate_config',
'COLUMN', N'create_user'
GO

EXEC sp_addextendedproperty
'MS_Description', N'修改时间',
'SCHEMA', N'dbo',
'TABLE', N'sys_code_generate_config',
'COLUMN', N'update_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'修改人',
'SCHEMA', N'dbo',
'TABLE', N'sys_code_generate_config',
'COLUMN', N'update_user'
GO

EXEC sp_addextendedproperty
'MS_Description', N'代码生成详细配置',
'SCHEMA', N'dbo',
'TABLE', N'sys_code_generate_config'
GO


-- ----------------------------
-- Records of sys_code_generate_config
-- ----------------------------
INSERT INTO [dbo].[sys_code_generate_config]  VALUES (N'1362310959903379458', N'1362310959781744641', N'id', N'id', N'bigint', N'主键', N'Long', N'input', NULL, N'N', N'N', N'N', N'N', N'N', N'eq', N'PRI', N'Id', N'N', N'2021-02-18 16:00:18.0000000', N'1265476890672672808', N'2021-02-18 16:02:31.0000000', N'1265476890672672808')
GO

INSERT INTO [dbo].[sys_code_generate_config]  VALUES (N'1362310959941128193', N'1362310959781744641', N'name', N'name', N'varchar', N'姓名', N'String', N'input', NULL, N'Y', N'Y', N'N', N'Y', N'Y', N'like', N'', N'Name', N'N', N'2021-02-18 16:00:18.0000000', N'1265476890672672808', N'2021-02-18 16:02:31.0000000', N'1265476890672672808')
GO

INSERT INTO [dbo].[sys_code_generate_config]  VALUES (N'1362310959978876929', N'1362310959781744641', N'age', N'age', N'int', N'年龄', N'Integer', N'input', NULL, N'Y', N'Y', N'N', N'Y', N'Y', N'eq', N'', N'Age', N'N', N'2021-02-18 16:00:18.0000000', N'1265476890672672808', N'2021-02-18 16:02:31.0000000', N'1265476890672672808')
GO

INSERT INTO [dbo].[sys_code_generate_config]  VALUES (N'1362310960008237058', N'1362310959781744641', N'interest', N'interest', N'varchar', N'兴趣', N'String', N'checkbox', N'sex', N'Y', N'Y', N'N', N'Y', N'N', N'eq', N'', N'Interest', N'N', N'2021-02-18 16:00:18.0000000', N'1265476890672672808', N'2021-02-18 16:02:31.0000000', N'1265476890672672808')
GO

INSERT INTO [dbo].[sys_code_generate_config]  VALUES (N'1362310960033402881', N'1362310959781744641', N'switchTest', N'switchtest', N'varchar', N'开关', N'String', N'radio', N'yes_or_no', N'Y', N'Y', N'N', N'Y', N'Y', N'eq', N'', N'Switchtest', N'N', N'2021-02-18 16:00:18.0000000', N'1265476890672672808', N'2021-02-18 16:02:31.0000000', N'1265476890672672808')
GO

INSERT INTO [dbo].[sys_code_generate_config]  VALUES (N'1362310960062763009', N'1362310959781744641', N'birthday', N'birthday', N'date', N'日期', N'Date', N'datepicker', NULL, N'Y', N'Y', N'N', N'Y', N'Y', N'eq', N'', N'Birthday', N'N', N'2021-02-18 16:00:18.0000000', N'1265476890672672808', N'2021-02-18 16:02:31.0000000', N'1265476890672672808')
GO

INSERT INTO [dbo].[sys_code_generate_config]  VALUES (N'1362310960096317442', N'1362310959781744641', N'whether', N'whether', N'varchar', N'是否已婚', N'String', N'select', N'yes_or_no', N'Y', N'Y', N'N', N'Y', N'Y', N'eq', N'', N'Whether', N'N', N'2021-02-18 16:00:18.0000000', N'1265476890672672808', N'2021-02-18 16:02:31.0000000', N'1265476890672672808')
GO

INSERT INTO [dbo].[sys_code_generate_config]  VALUES (N'1362310960121483266', N'1362310959781744641', N'explainTest', N'explaintest', N'varchar', N'简介', N'String', N'textarea', NULL, N'N', N'Y', N'N', N'Y', N'Y', N'eq', N'', N'Explain', N'N', N'2021-02-18 16:00:18.0000000', N'1265476890672672808', N'2021-02-18 16:02:31.0000000', N'1265476890672672808')
GO

INSERT INTO [dbo].[sys_code_generate_config]  VALUES (N'1362310960150843393', N'1362310959781744641', N'create_time', N'createTime', N'datetime', N'创建时间', N'Date', N'datepicker', NULL, N'N', N'N', N'N', N'N', N'N', N'eq', N'', N'CreateTime', N'Y', N'2021-02-18 16:00:18.0000000', N'1265476890672672808', N'2021-02-18 16:02:31.0000000', N'1265476890672672808')
GO

INSERT INTO [dbo].[sys_code_generate_config]  VALUES (N'1362310960176009218', N'1362310959781744641', N'create_user', N'createUser', N'bigint', N'', N'Long', N'input', NULL, N'N', N'N', N'N', N'N', N'N', N'eq', N'', N'CreateUser', N'Y', N'2021-02-18 16:00:18.0000000', N'1265476890672672808', N'2021-02-18 16:02:31.0000000', N'1265476890672672808')
GO

INSERT INTO [dbo].[sys_code_generate_config]  VALUES (N'1362310960201175041', N'1362310959781744641', N'update_time', N'updateTime', N'datetime', N'', N'Date', N'datepicker', NULL, N'N', N'N', N'N', N'N', N'N', N'eq', N'', N'UpdateTime', N'Y', N'2021-02-18 16:00:18.0000000', N'1265476890672672808', N'2021-02-18 16:02:31.0000000', N'1265476890672672808')
GO

INSERT INTO [dbo].[sys_code_generate_config]  VALUES (N'1362310960234729473', N'1362310959781744641', N'update_user', N'updateUser', N'bigint', N'', N'Long', N'input', NULL, N'N', N'N', N'N', N'N', N'N', N'eq', N'', N'UpdateUser', N'Y', N'2021-02-18 16:00:18.0000000', N'1265476890672672808', N'2021-02-18 16:02:31.0000000', N'1265476890672672808')
GO


-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[sys_config]') AND type IN ('U'))
	DROP TABLE [dbo].[sys_config]
GO

CREATE TABLE [dbo].[sys_config] (
  [id] bigint  NOT NULL,
  [name] nvarchar(100) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [code] nvarchar(50) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [value] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [sys_flag] nchar(1) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [remark] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [status] tinyint  NOT NULL,
  [group_code] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [create_time] datetime2(7)  NULL,
  [create_user] bigint  NULL,
  [update_time] datetime2(7)  NULL,
  [update_user] bigint  NULL
)
GO

ALTER TABLE [dbo].[sys_config] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
'MS_Description', N'主键',
'SCHEMA', N'dbo',
'TABLE', N'sys_config',
'COLUMN', N'id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'名称',
'SCHEMA', N'dbo',
'TABLE', N'sys_config',
'COLUMN', N'name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'编码',
'SCHEMA', N'dbo',
'TABLE', N'sys_config',
'COLUMN', N'code'
GO

EXEC sp_addextendedproperty
'MS_Description', N'值',
'SCHEMA', N'dbo',
'TABLE', N'sys_config',
'COLUMN', N'value'
GO

EXEC sp_addextendedproperty
'MS_Description', N'是否是系统参数（Y-是，N-否）',
'SCHEMA', N'dbo',
'TABLE', N'sys_config',
'COLUMN', N'sys_flag'
GO

EXEC sp_addextendedproperty
'MS_Description', N'备注',
'SCHEMA', N'dbo',
'TABLE', N'sys_config',
'COLUMN', N'remark'
GO

EXEC sp_addextendedproperty
'MS_Description', N'状态（字典 0正常 1停用 2删除）',
'SCHEMA', N'dbo',
'TABLE', N'sys_config',
'COLUMN', N'status'
GO

EXEC sp_addextendedproperty
'MS_Description', N'常量所属分类的编码，来自于“常量的分类”字典',
'SCHEMA', N'dbo',
'TABLE', N'sys_config',
'COLUMN', N'group_code'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建时间',
'SCHEMA', N'dbo',
'TABLE', N'sys_config',
'COLUMN', N'create_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建人',
'SCHEMA', N'dbo',
'TABLE', N'sys_config',
'COLUMN', N'create_user'
GO

EXEC sp_addextendedproperty
'MS_Description', N'更新时间',
'SCHEMA', N'dbo',
'TABLE', N'sys_config',
'COLUMN', N'update_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'更新人',
'SCHEMA', N'dbo',
'TABLE', N'sys_config',
'COLUMN', N'update_user'
GO

EXEC sp_addextendedproperty
'MS_Description', N'系统参数配置表',
'SCHEMA', N'dbo',
'TABLE', N'sys_config'
GO


-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO [dbo].[sys_config]  VALUES (N'1265117443880853506', N'jwt密钥', N'SNOWY_JWT_SECRET', N'snowy', N'Y', N'（重要）jwt密钥，默认为空，自行设置', N'0', N'DEFAULT', N'2020-05-26 06:35:19.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_config]  VALUES (N'1265117443880853507', N'默认密码', N'SNOWY_DEFAULT_PASSWORD', N'123456', N'Y', N'默认密码', N'0', N'DEFAULT', N'2020-05-26 06:37:56.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_config]  VALUES (N'1265117443880853508', N'token过期时间', N'SNOWY_TOKEN_EXPIRE', N'86400', N'Y', N'token过期时间（单位：秒）', N'0', N'DEFAULT', N'2020-05-27 11:54:49.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_config]  VALUES (N'1265117443880853509', N'session会话过期时间', N'SNOWY_SESSION_EXPIRE', N'7200', N'Y', N'session会话过期时间（单位：秒）', N'0', N'DEFAULT', N'2020-05-27 11:54:49.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_config]  VALUES (N'1265117443880853519', N'阿里云短信keyId', N'SNOWY_ALIYUN_SMS_ACCESSKEY_ID', N'你的keyId', N'Y', N'阿里云短信keyId', N'0', N'ALIYUN_SMS', N'2020-06-07 16:27:11.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_config]  VALUES (N'1269547042242371585', N'阿里云短信secret', N'SNOWY_ALIYUN_SMS_ACCESSKEY_SECRET', N'你的secret', N'Y', N'阿里云短信secret', N'0', N'ALIYUN_SMS', N'2020-06-07 16:29:37.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_config]  VALUES (N'1269547130041737217', N'阿里云短信签名', N'SNOWY_ALIYUN_SMS_SIGN_NAME', N'Snowy快速开发平台', N'Y', N'阿里云短信签名', N'0', N'ALIYUN_SMS', N'2020-06-07 16:29:58.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_config]  VALUES (N'1269547279530926081', N'阿里云短信-登录模板号', N'SNOWY_ALIYUN_SMS_LOGIN_TEMPLATE_CODE', N'SMS_1877123456', N'Y', N'阿里云短信-登录模板号', N'0', N'ALIYUN_SMS', N'2020-06-07 16:30:33.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_config]  VALUES (N'1269547410879750145', N'阿里云短信默认失效时间', N'SNOWY_ALIYUN_SMS_INVALIDATE_MINUTES', N'5', N'Y', N'阿里云短信默认失效时间（单位：分钟）', N'0', N'ALIYUN_SMS', N'2020-06-07 16:31:04.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_config]  VALUES (N'1269575927357071361', N'腾讯云短信secretId', N'SNOWY_TENCENT_SMS_SECRET_ID', N'你的secretId', N'Y', N'腾讯云短信secretId', N'0', N'TENCENT_SMS', N'2020-06-07 18:24:23.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_config]  VALUES (N'1269575991693500418', N'腾讯云短信secretKey', N'SNOWY_TENCENT_SMS_SECRET_KEY', N'你的secretkey', N'Y', N'腾讯云短信secretKey', N'0', N'TENCENT_SMS', N'2020-06-07 18:24:39.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_config]  VALUES (N'1269576044084551682', N'腾讯云短信sdkAppId', N'SNOWY_TENCENT_SMS_SDK_APP_ID', N'1400375123', N'Y', N'腾讯云短信sdkAppId', N'0', N'TENCENT_SMS', N'2020-06-07 18:24:51.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_config]  VALUES (N'1269576089294954497', N'腾讯云短信签名', N'SNOWY_TENCENT_SMS_SIGN', N'Snowy快速开发平台', N'Y', N'腾讯云短信签名', N'0', N'TENCENT_SMS', N'2020-06-07 18:25:02.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_config]  VALUES (N'1270378172860403713', N'邮箱host', N'SNOWY_EMAIL_HOST', N'smtp.126.com', N'Y', N'邮箱host', N'0', N'EMAIL', N'2020-06-09 23:32:14.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_config]  VALUES (N'1270378295543795714', N'邮箱用户名', N'SNOWY_EMAIL_USERNAME', N'test@126.com', N'Y', N'邮箱用户名', N'0', N'EMAIL', N'2020-06-09 23:32:43.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_config]  VALUES (N'1270378340510928897', N'邮箱密码', N'SNOWY_EMAIL_PASSWORD', N'你的邮箱密码', N'Y', N'邮箱密码', N'0', N'EMAIL', N'2020-06-09 23:32:54.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_config]  VALUES (N'1270378527358783489', N'邮箱端口', N'SNOWY_EMAIL_PORT', N'465', N'Y', N'邮箱端口', N'0', N'EMAIL', N'2020-06-09 23:33:38.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_config]  VALUES (N'1270378790035460097', N'邮箱是否开启ssl', N'SNOWY_EMAIL_SSL', N'true', N'Y', N'邮箱是否开启ssl', N'0', N'EMAIL', N'2020-06-09 23:34:41.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_config]  VALUES (N'1270380786649972737', N'邮箱发件人', N'SNOWY_EMAIL_FROM', N'test@126.com', N'Y', N'邮箱发件人', N'0', N'EMAIL', N'2020-06-09 23:42:37.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_config]  VALUES (N'1270380786649972738', N'win本地上传文件路径', N'SNOWY_FILE_UPLOAD_PATH_FOR_WINDOWS', N'd:/tmp', N'Y', N'win本地上传文件路径', N'0', N'FILE_PATH', N'2020-06-09 23:42:37.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_config]  VALUES (N'1270380786649972739', N'linux/mac本地上传文件路径', N'SNOWY_FILE_UPLOAD_PATH_FOR_LINUX', N'/tmp', N'Y', N'linux/mac本地上传文件路径', N'0', N'FILE_PATH', N'2020-06-09 23:42:37.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_config]  VALUES (N'1270380786649982740', N'Snowy演示环境', N'SNOWY_DEMO_ENV_FLAG', N'false', N'Y', N'Snowy演示环境的开关，true-打开，false-关闭，如果演示环境开启，则只能读数据不能写数据', N'0', N'DEFAULT', N'2020-06-09 23:42:37.0000000', N'1265476890672672808', N'2020-09-03 14:38:17.0000000', N'1265476890672672808')
GO

INSERT INTO [dbo].[sys_config]  VALUES (N'1270380786649982741', N'Snowy放开XSS过滤的接口', N'SNOWY_UN_XSS_FILTER_URL', N'/demo/xssfilter,/demo/unxss', N'Y', N'多个url可以用英文逗号隔开', N'0', N'DEFAULT', N'2020-06-09 23:42:37.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_config]  VALUES (N'1270380786649982742', N'单用户登陆的开关', N'SNOWY_ENABLE_SINGLE_LOGIN', N'false', N'Y', N'单用户登陆的开关，true-打开，false-关闭，如果一个人登录两次，就会将上一次登陆挤下去', N'0', N'DEFAULT', N'2020-06-09 23:42:37.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_config]  VALUES (N'1270380786649982743', N'登录验证码的开关', N'SNOWY_CAPTCHA_OPEN', N'true', N'Y', N'登录验证码的开关，true-打开，false-关闭', N'0', N'DEFAULT', N'2020-06-09 23:42:37.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_config]  VALUES (N'1280694183769792514', N'druid监控登录账号', N'SNOWY_DRUID_USERNAME', N'superAdmin', N'Y', N'druid监控登录账号', N'0', N'DEFAULT', N'2020-07-08 10:44:22.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_config]  VALUES (N'1280694281648070658', N'druid监控界面登录密码', N'SNOWY_DRUID_PASSWORD', N'123456', N'Y', N'druid监控登录密码', N'0', N'DEFAULT', N'2020-07-08 10:44:46.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_config]  VALUES (N'1280694281648070659', N'阿里云定位api接口地址', N'SNOWY_IP_GEO_API', N'http://api01.aliyun.venuscn.com/ip?ip=%s', N'Y', N'阿里云定位api接口地址', N'0', N'DEFAULT', N'2020-07-20 10:44:46.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_config]  VALUES (N'1280694281648070660', N'阿里云定位appCode', N'SNOWY_IP_GEO_APP_CODE', N'461535aabeae4f34861884d392f5d452', N'Y', N'阿里云定位appCode', N'0', N'DEFAULT', N'2020-07-20 10:44:46.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_config]  VALUES (N'1288309751255412737', N'Oauth用户登录的开关', N'SNOWY_ENABLE_OAUTH_LOGIN', N'true', N'Y', N'Oauth用户登录的开关', N'0', N'OAUTH', N'2020-07-29 11:05:55.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_config]  VALUES (N'1288310043346743297', N'Oauth码云登录ClientId', N'SNOWY_OAUTH_GITEE_CLIENT_ID', N'你的clientId', N'Y', N'Oauth码云登录ClientId', N'0', N'OAUTH', N'2020-07-29 11:07:05.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_config]  VALUES (N'1288310157876408321', N'Oauth码云登录ClientSecret', N'SNOWY_OAUTH_GITEE_CLIENT_SECRET', N'你的clientSecret', N'Y', N'Oauth码云登录ClientSecret', N'0', N'OAUTH', N'2020-07-29 11:07:32.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_config]  VALUES (N'1288310280056483841', N'Oauth码云登录回调地址', N'SNOWY_OAUTH_GITEE_REDIRECT_URI', N'http://localhost:83/oauth/callback/gitee', N'Y', N'Oauth码云登录回调地址', N'0', N'OAUTH', N'2020-07-29 11:08:01.0000000', N'1265476890672672808', NULL, NULL)
GO


-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[sys_dict_data]') AND type IN ('U'))
	DROP TABLE [dbo].[sys_dict_data]
GO

CREATE TABLE [dbo].[sys_dict_data] (
  [id] bigint  NOT NULL,
  [type_id] bigint  NOT NULL,
  [value] nvarchar(max) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [code] nvarchar(50) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [sort] int  NOT NULL,
  [remark] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [status] tinyint  NOT NULL,
  [create_time] datetime2(7)  NULL,
  [create_user] bigint  NULL,
  [update_time] datetime2(7)  NULL,
  [update_user] bigint  NULL
)
GO

ALTER TABLE [dbo].[sys_dict_data] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
'MS_Description', N'主键',
'SCHEMA', N'dbo',
'TABLE', N'sys_dict_data',
'COLUMN', N'id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'字典类型id',
'SCHEMA', N'dbo',
'TABLE', N'sys_dict_data',
'COLUMN', N'type_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'值',
'SCHEMA', N'dbo',
'TABLE', N'sys_dict_data',
'COLUMN', N'value'
GO

EXEC sp_addextendedproperty
'MS_Description', N'编码',
'SCHEMA', N'dbo',
'TABLE', N'sys_dict_data',
'COLUMN', N'code'
GO

EXEC sp_addextendedproperty
'MS_Description', N'排序',
'SCHEMA', N'dbo',
'TABLE', N'sys_dict_data',
'COLUMN', N'sort'
GO

EXEC sp_addextendedproperty
'MS_Description', N'备注',
'SCHEMA', N'dbo',
'TABLE', N'sys_dict_data',
'COLUMN', N'remark'
GO

EXEC sp_addextendedproperty
'MS_Description', N'状态（字典 0正常 1停用 2删除）',
'SCHEMA', N'dbo',
'TABLE', N'sys_dict_data',
'COLUMN', N'status'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建时间',
'SCHEMA', N'dbo',
'TABLE', N'sys_dict_data',
'COLUMN', N'create_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建人',
'SCHEMA', N'dbo',
'TABLE', N'sys_dict_data',
'COLUMN', N'create_user'
GO

EXEC sp_addextendedproperty
'MS_Description', N'更新时间',
'SCHEMA', N'dbo',
'TABLE', N'sys_dict_data',
'COLUMN', N'update_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'更新人',
'SCHEMA', N'dbo',
'TABLE', N'sys_dict_data',
'COLUMN', N'update_user'
GO

EXEC sp_addextendedproperty
'MS_Description', N'系统字典值表',
'SCHEMA', N'dbo',
'TABLE', N'sys_dict_data'
GO


-- ----------------------------
-- Records of sys_dict_data
-- ----------------------------
INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1265216536659087357', N'1265216211667636234', N'男', N'1', N'100', N'男性', N'0', N'2020-04-01 10:23:29.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1265216536659087358', N'1265216211667636234', N'女', N'2', N'100', N'女性', N'0', N'2020-04-01 10:23:49.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1265216536659087359', N'1265216211667636234', N'未知', N'3', N'100', N'未知性别', N'0', N'2020-04-01 10:24:01.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1265216536659087361', N'1265216211667636235', N'默认常量', N'DEFAULT', N'100', N'默认常量，都以SNOWY_开头的', N'0', N'2020-04-14 23:25:45.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1265216536659087363', N'1265216211667636235', N'阿里云短信', N'ALIYUN_SMS', N'100', N'阿里云短信配置', N'0', N'2020-04-14 23:25:45.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1265216536659087364', N'1265216211667636235', N'腾讯云短信', N'TENCENT_SMS', N'100', N'腾讯云短信', N'0', N'2020-04-14 23:25:45.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1265216536659087365', N'1265216211667636235', N'邮件配置', N'EMAIL', N'100', N'邮箱配置', N'0', N'2020-04-14 23:25:45.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1265216536659087366', N'1265216211667636235', N'文件上传路径', N'FILE_PATH', N'100', N'文件上传路径', N'0', N'2020-04-14 23:25:45.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1265216536659087367', N'1265216211667636235', N'Oauth配置', N'OAUTH', N'100', N'Oauth配置', N'0', N'2020-04-14 23:25:45.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1265216617500102656', N'1265216211667636226', N'正常', N'0', N'100', N'正常', N'0', N'2020-05-26 17:41:44.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1265216617500102657', N'1265216211667636226', N'停用', N'1', N'100', N'停用', N'0', N'2020-05-26 17:42:03.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1265216938389524482', N'1265216211667636226', N'删除', N'2', N'100', N'删除', N'0', N'2020-05-26 17:43:19.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1265217669028892673', N'1265217074079453185', N'否', N'N', N'100', N'否', N'0', N'2020-05-26 17:46:14.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1265217706584690689', N'1265217074079453185', N'是', N'Y', N'100', N'是', N'0', N'2020-05-26 17:46:23.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1265220776437731330', N'1265217846770913282', N'登录', N'1', N'100', N'登录', N'0', N'2020-05-26 17:58:34.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1265220806070489090', N'1265217846770913282', N'登出', N'2', N'100', N'登出', N'0', N'2020-05-26 17:58:41.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1265221129564573697', N'1265221049302372354', N'目录', N'0', N'100', N'目录', N'0', N'2020-05-26 17:59:59.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1265221163119005697', N'1265221049302372354', N'菜单', N'1', N'100', N'菜单', N'0', N'2020-05-26 18:00:07.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1265221188091891713', N'1265221049302372354', N'按钮', N'2', N'100', N'按钮', N'0', N'2020-05-26 18:00:13.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1265466389204967426', N'1265466149622128641', N'未发送', N'0', N'100', N'未发送', N'0', N'2020-05-27 10:14:33.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1265466432670539778', N'1265466149622128641', N'发送成功', N'1', N'100', N'发送成功', N'0', N'2020-05-27 10:14:43.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1265466486097584130', N'1265466149622128641', N'发送失败', N'2', N'100', N'发送失败', N'0', N'2020-05-27 10:14:56.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1265466530477514754', N'1265466149622128641', N'失效', N'3', N'100', N'失效', N'0', N'2020-05-27 10:15:07.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1265466835009150978', N'1265466752209395713', N'无', N'0', N'100', N'无', N'0', N'2020-05-27 10:16:19.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1265466874758569986', N'1265466752209395713', N'组件', N'1', N'100', N'组件', N'0', N'2020-05-27 10:16:29.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1265466925476093953', N'1265466752209395713', N'内链', N'2', N'100', N'内链', N'0', N'2020-05-27 10:16:41.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1265466962209808385', N'1265466752209395713', N'外链', N'3', N'100', N'外链', N'0', N'2020-05-27 10:16:50.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1265467428423475202', N'1265467337566461954', N'系统权重', N'1', N'100', N'系统权重', N'0', N'2020-05-27 10:18:41.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1265467503090475009', N'1265467337566461954', N'业务权重', N'2', N'100', N'业务权重', N'0', N'2020-05-27 10:18:59.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1265468138431062018', N'1265468028632571905', N'全部数据', N'1', N'100', N'全部数据', N'0', N'2020-05-27 10:21:30.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1265468194928336897', N'1265468028632571905', N'本部门及以下数据', N'2', N'100', N'本部门及以下数据', N'0', N'2020-05-27 10:21:44.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1265468241992622082', N'1265468028632571905', N'本部门数据', N'3', N'100', N'本部门数据', N'0', N'2020-05-27 10:21:55.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1265468273634451457', N'1265468028632571905', N'仅本人数据', N'4', N'100', N'仅本人数据', N'0', N'2020-05-27 10:22:02.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1265468302046666753', N'1265468028632571905', N'自定义数据', N'5', N'100', N'自定义数据', N'0', N'2020-05-27 10:22:09.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1265468508100239362', N'1265468437904367618', N'app', N'1', N'100', N'app', N'0', N'2020-05-27 10:22:58.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1265468543433056258', N'1265468437904367618', N'pc', N'2', N'100', N'pc', N'0', N'2020-05-27 10:23:07.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1265468576874242050', N'1265468437904367618', N'其他', N'3', N'100', N'其他', N'0', N'2020-05-27 10:23:15.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1275617233011335170', N'1275617093517172738', N'其它', N'0', N'100', N'其它', N'0', N'2020-06-24 10:30:23.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1275617295355469826', N'1275617093517172738', N'增加', N'1', N'100', N'增加', N'0', N'2020-06-24 10:30:38.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1275617348610547714', N'1275617093517172738', N'删除', N'2', N'100', N'删除', N'0', N'2020-06-24 10:30:50.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1275617395515449346', N'1275617093517172738', N'编辑', N'3', N'100', N'编辑', N'0', N'2020-06-24 10:31:02.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1275617433612312577', N'1275617093517172738', N'更新', N'4', N'100', N'更新', N'0', N'2020-06-24 10:31:11.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1275617472707420161', N'1275617093517172738', N'查询', N'5', N'100', N'查询', N'0', N'2020-06-24 10:31:20.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1275617502973517826', N'1275617093517172738', N'详情', N'6', N'100', N'详情', N'0', N'2020-06-24 10:31:27.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1275617536959963137', N'1275617093517172738', N'树', N'7', N'100', N'树', N'0', N'2020-06-24 10:31:35.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1275617619524837377', N'1275617093517172738', N'导入', N'8', N'100', N'导入', N'0', N'2020-06-24 10:31:55.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1275617651816783873', N'1275617093517172738', N'导出', N'9', N'100', N'导出', N'0', N'2020-06-24 10:32:03.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1275617683475390465', N'1275617093517172738', N'授权', N'10', N'100', N'授权', N'0', N'2020-06-24 10:32:10.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1275617709928865793', N'1275617093517172738', N'强退', N'11', N'100', N'强退', N'0', N'2020-06-24 10:32:17.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1275617739091861505', N'1275617093517172738', N'清空', N'12', N'100', N'清空', N'0', N'2020-06-24 10:32:23.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1275617788601425921', N'1275617093517172738', N'修改状态', N'13', N'100', N'修改状态', N'0', N'2020-06-24 10:32:35.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1277774590944317441', N'1277774529430654977', N'阿里云', N'1', N'100', N'阿里云', N'0', N'2020-06-30 09:22:57.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1277774666055913474', N'1277774529430654977', N'腾讯云', N'2', N'100', N'腾讯云', N'0', N'2020-06-30 09:23:15.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1277774695168577538', N'1277774529430654977', N'minio', N'3', N'100', N'minio', N'0', N'2020-06-30 09:23:22.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1277774726835572737', N'1277774529430654977', N'本地', N'4', N'100', N'本地', N'0', N'2020-06-30 09:23:29.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1278607123583868929', N'1278606951432855553', N'运行', N'1', N'100', N'运行', N'0', N'2020-07-02 16:31:08.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1278607162943217666', N'1278606951432855553', N'停止', N'2', N'100', N'停止', N'0', N'2020-07-02 16:31:18.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1278939265862004738', N'1278911800547147777', N'通知', N'1', N'100', N'通知', N'0', N'2020-07-03 14:30:57.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1278939319922388994', N'1278911800547147777', N'公告', N'2', N'100', N'公告', N'0', N'2020-07-03 14:31:10.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1278939399001796609', N'1278911952657776642', N'草稿', N'0', N'100', N'草稿', N'0', N'2020-07-03 14:31:29.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1278939432686252034', N'1278911952657776642', N'发布', N'1', N'100', N'发布', N'0', N'2020-07-03 14:31:37.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1278939458804183041', N'1278911952657776642', N'撤回', N'2', N'100', N'撤回', N'0', N'2020-07-03 14:31:43.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1278939485878415362', N'1278911952657776642', N'删除', N'3', N'100', N'删除', N'0', N'2020-07-03 14:31:50.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1291390260160299009', N'1291390159941599233', N'是', N'true', N'100', N'是', N'2', N'2020-08-06 23:06:46.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1291390315437031426', N'1291390159941599233', N'否', N'false', N'100', N'否', N'2', N'2020-08-06 23:06:59.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1342446007168466945', N'1342445962104864770', N'下载压缩包', N'1', N'100', N'下载压缩包', N'0', N'2020-12-25 20:24:04.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1342446035433881601', N'1342445962104864770', N'生成到本项目', N'2', N'100', N'生成到本项目', N'0', N'2020-12-25 20:24:11.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1358094655567454210', N'1358094419419750401', N'输入框', N'input', N'100', N'输入框', N'0', N'2021-02-07 00:46:13.0000000', N'1265476890672672808', N'2021-02-08 01:01:28.0000000', N'1265476890672672808')
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1358094740510498817', N'1358094419419750401', N'时间选择', N'datepicker', N'100', N'时间选择', N'0', N'2021-02-07 00:46:33.0000000', N'1265476890672672808', N'2021-02-08 01:04:07.0000000', N'1265476890672672808')
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1358094793149014017', N'1358094419419750401', N'下拉框', N'select', N'100', N'下拉框', N'0', N'2021-02-07 00:46:46.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1358095496009506817', N'1358094419419750401', N'单选框', N'radio', N'100', N'单选框', N'0', N'2021-02-07 00:49:33.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1358095673084633090', N'1358094419419750401', N'开关', N'switch', N'100', N'开关', N'2', N'2021-02-07 00:50:15.0000000', N'1265476890672672808', N'2021-02-11 19:07:18.0000000', N'1265476890672672808')
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1358458689433190402', N'1358457818733428737', N'等于', N'eq', N'1', N'等于', N'0', N'2021-02-08 00:52:45.0000000', N'1265476890672672808', N'2021-02-13 23:35:36.0000000', N'1265476890672672808')
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1358458785168179202', N'1358457818733428737', N'模糊', N'like', N'2', N'模糊', N'0', N'2021-02-08 00:53:08.0000000', N'1265476890672672808', N'2021-02-13 23:35:46.0000000', N'1265476890672672808')
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1358460475682406401', N'1358094419419750401', N'多选框', N'checkbox', N'100', N'多选框', N'0', N'2021-02-08 00:59:51.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1358460819019743233', N'1358094419419750401', N'数字输入框', N'inputnumber', N'100', N'数字输入框', N'0', N'2021-02-08 01:01:13.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1358470210267725826', N'1358470065111252994', N'Long', N'Long', N'100', N'Long', N'0', N'2021-02-08 01:38:32.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1358470239351029762', N'1358470065111252994', N'String', N'String', N'100', N'String', N'0', N'2021-02-08 01:38:39.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1358470265640927233', N'1358470065111252994', N'Date', N'Date', N'100', N'Date', N'0', N'2021-02-08 01:38:45.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1358470300168437761', N'1358470065111252994', N'Integer', N'Integer', N'100', N'Integer', N'0', N'2021-02-08 01:38:53.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1358470697377415169', N'1358470065111252994', N'boolean', N'boolean', N'100', N'boolean', N'0', N'2021-02-08 01:40:28.0000000', N'1265476890672672808', N'2021-02-08 01:40:47.0000000', N'1265476890672672808')
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1358471133434036226', N'1358470065111252994', N'int', N'int', N'100', N'int', N'0', N'2021-02-08 01:42:12.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1358471188291338241', N'1358470065111252994', N'double', N'double', N'100', N'double', N'0', N'2021-02-08 01:42:25.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1358756511688761346', N'1358457818733428737', N'大于', N'gt', N'3', N'大于', N'0', N'2021-02-08 20:36:12.0000000', N'1265476890672672808', N'2021-02-13 23:45:24.0000000', N'1265476890672672808')
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1358756547159990274', N'1358457818733428737', N'小于', N'lt', N'4', N'大于', N'0', N'2021-02-08 20:36:20.0000000', N'1265476890672672808', N'2021-02-13 23:45:29.0000000', N'1265476890672672808')
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1358756609990664193', N'1358457818733428737', N'不等于', N'ne', N'7', N'不等于', N'0', N'2021-02-08 20:36:35.0000000', N'1265476890672672808', N'2021-02-13 23:45:46.0000000', N'1265476890672672808')
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1358756685030957057', N'1358457818733428737', N'大于等于', N'ge', N'5', N'大于等于', N'0', N'2021-02-08 20:36:53.0000000', N'1265476890672672808', N'2021-02-13 23:45:35.0000000', N'1265476890672672808')
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1358756800525312001', N'1358457818733428737', N'小于等于', N'le', N'6', N'小于等于', N'0', N'2021-02-08 20:37:20.0000000', N'1265476890672672808', N'2021-02-13 23:45:40.0000000', N'1265476890672672808')
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1360529773814083586', N'1358094419419750401', N'文本域', N'textarea', N'100', N'文本域', N'0', N'2021-02-13 18:02:30.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_data]  VALUES (N'1360606105914732545', N'1358457818733428737', N'不为空', N'isNotNull', N'8', N'不为空', N'0', N'2021-02-13 23:05:49.0000000', N'1265476890672672808', N'2021-02-13 23:45:50.0000000', N'1265476890672672808')
GO


-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[sys_dict_type]') AND type IN ('U'))
	DROP TABLE [dbo].[sys_dict_type]
GO

CREATE TABLE [dbo].[sys_dict_type] (
  [id] bigint  NOT NULL,
  [name] nvarchar(100) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [code] nvarchar(50) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [sort] int  NOT NULL,
  [remark] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [status] tinyint  NOT NULL,
  [create_time] datetime2(7)  NULL,
  [create_user] bigint  NULL,
  [update_time] datetime2(7)  NULL,
  [update_user] bigint  NULL
)
GO

ALTER TABLE [dbo].[sys_dict_type] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
'MS_Description', N'主键',
'SCHEMA', N'dbo',
'TABLE', N'sys_dict_type',
'COLUMN', N'id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'名称',
'SCHEMA', N'dbo',
'TABLE', N'sys_dict_type',
'COLUMN', N'name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'编码',
'SCHEMA', N'dbo',
'TABLE', N'sys_dict_type',
'COLUMN', N'code'
GO

EXEC sp_addextendedproperty
'MS_Description', N'排序',
'SCHEMA', N'dbo',
'TABLE', N'sys_dict_type',
'COLUMN', N'sort'
GO

EXEC sp_addextendedproperty
'MS_Description', N'备注',
'SCHEMA', N'dbo',
'TABLE', N'sys_dict_type',
'COLUMN', N'remark'
GO

EXEC sp_addextendedproperty
'MS_Description', N'状态（字典 0正常 1停用 2删除）',
'SCHEMA', N'dbo',
'TABLE', N'sys_dict_type',
'COLUMN', N'status'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建时间',
'SCHEMA', N'dbo',
'TABLE', N'sys_dict_type',
'COLUMN', N'create_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建人',
'SCHEMA', N'dbo',
'TABLE', N'sys_dict_type',
'COLUMN', N'create_user'
GO

EXEC sp_addextendedproperty
'MS_Description', N'更新时间',
'SCHEMA', N'dbo',
'TABLE', N'sys_dict_type',
'COLUMN', N'update_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'更新人',
'SCHEMA', N'dbo',
'TABLE', N'sys_dict_type',
'COLUMN', N'update_user'
GO

EXEC sp_addextendedproperty
'MS_Description', N'系统字典类型表',
'SCHEMA', N'dbo',
'TABLE', N'sys_dict_type'
GO


-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO [dbo].[sys_dict_type]  VALUES (N'1265216211667636226', N'通用状态', N'common_status', N'100', N'通用状态', N'0', N'2020-05-26 17:40:26.0000000', N'1265476890672672808', N'2020-06-08 11:31:47.0000000', N'1265476890672672808')
GO

INSERT INTO [dbo].[sys_dict_type]  VALUES (N'1265216211667636234', N'性别', N'sex', N'100', N'性别字典', N'0', N'2020-04-01 10:12:30.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_type]  VALUES (N'1265216211667636235', N'常量的分类', N'consts_type', N'100', N'常量的分类，用于区别一组配置', N'0', N'2020-04-14 23:24:13.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_type]  VALUES (N'1265217074079453185', N'是否', N'yes_or_no', N'100', N'是否', N'0', N'2020-05-26 17:43:52.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_type]  VALUES (N'1265217846770913282', N'访问类型', N'vis_type', N'100', N'访问类型', N'0', N'2020-05-26 17:46:56.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_type]  VALUES (N'1265221049302372354', N'菜单类型', N'menu_type', N'100', N'菜单类型', N'0', N'2020-05-26 17:59:39.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_type]  VALUES (N'1265466149622128641', N'发送类型', N'send_type', N'100', N'发送类型', N'0', N'2020-05-27 10:13:36.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_type]  VALUES (N'1265466752209395713', N'打开方式', N'open_type', N'100', N'打开方式', N'0', N'2020-05-27 10:16:00.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_type]  VALUES (N'1265467337566461954', N'菜单权重', N'menu_weight', N'100', N'菜单权重', N'0', N'2020-05-27 10:18:19.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_type]  VALUES (N'1265468028632571905', N'数据范围类型', N'data_scope_type', N'100', N'数据范围类型', N'0', N'2020-05-27 10:21:04.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_type]  VALUES (N'1265468437904367618', N'短信发送来源', N'sms_send_source', N'100', N'短信发送来源', N'0', N'2020-05-27 10:22:42.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_type]  VALUES (N'1275617093517172738', N'操作类型', N'op_type', N'100', N'操作类型', N'0', N'2020-06-24 10:29:50.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_type]  VALUES (N'1277774529430654977', N'文件存储位置', N'file_storage_location', N'100', N'文件存储位置', N'0', N'2020-06-30 09:22:42.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_type]  VALUES (N'1278606951432855553', N'运行状态', N'run_status', N'100', N'定时任务运行状态', N'0', N'2020-07-02 16:30:27.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_type]  VALUES (N'1278911800547147777', N'通知公告类型', N'notice_type', N'100', N'通知公告类型', N'0', N'2020-07-03 12:41:49.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_type]  VALUES (N'1278911952657776642', N'通知公告状态', N'notice_status', N'100', N'通知公告状态', N'0', N'2020-07-03 12:42:25.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_type]  VALUES (N'1291390159941599233', N'是否boolean', N'yes_true_false', N'100', N'是否boolean', N'2', N'2020-08-06 23:06:22.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_type]  VALUES (N'1342445962104864770', N'代码生成方式', N'code_gen_create_type', N'100', N'代码生成方式', N'0', N'2020-12-25 20:23:53.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_type]  VALUES (N'1358094419419750401', N'代码生成作用类型', N'code_gen_effect_type', N'100', N'代码生成作用类型', N'0', N'2021-02-07 00:45:16.0000000', N'1265476890672672808', N'2021-02-08 00:47:48.0000000', N'1265476890672672808')
GO

INSERT INTO [dbo].[sys_dict_type]  VALUES (N'1358457818733428737', N'代码生成查询类型', N'code_gen_query_type', N'100', N'代码生成查询类型', N'0', N'2021-02-08 00:49:18.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_dict_type]  VALUES (N'1358470065111252994', N'代码生成java类型', N'code_gen_java_type', N'100', N'代码生成java类型', N'0', N'2021-02-08 01:37:57.0000000', N'1265476890672672808', NULL, NULL)
GO


-- ----------------------------
-- Table structure for sys_emp
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[sys_emp]') AND type IN ('U'))
	DROP TABLE [dbo].[sys_emp]
GO

CREATE TABLE [dbo].[sys_emp] (
  [id] bigint  NOT NULL,
  [job_num] nvarchar(100) COLLATE Chinese_PRC_CI_AS  NULL,
  [org_id] bigint  NOT NULL,
  [org_name] nvarchar(100) COLLATE Chinese_PRC_CI_AS  NOT NULL
)
GO

ALTER TABLE [dbo].[sys_emp] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
'MS_Description', N'主键',
'SCHEMA', N'dbo',
'TABLE', N'sys_emp',
'COLUMN', N'id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'工号',
'SCHEMA', N'dbo',
'TABLE', N'sys_emp',
'COLUMN', N'job_num'
GO

EXEC sp_addextendedproperty
'MS_Description', N'所属机构id',
'SCHEMA', N'dbo',
'TABLE', N'sys_emp',
'COLUMN', N'org_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'所属机构名称',
'SCHEMA', N'dbo',
'TABLE', N'sys_emp',
'COLUMN', N'org_name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'员工表',
'SCHEMA', N'dbo',
'TABLE', N'sys_emp'
GO


-- ----------------------------
-- Records of sys_emp
-- ----------------------------
INSERT INTO [dbo].[sys_emp]  VALUES (N'1275735541155614721', N'102', N'1265476890672672769', N'华夏集团北京分公司')
GO

INSERT INTO [dbo].[sys_emp]  VALUES (N'1280700700074041345', N'110', N'1265476890672672771', N'研发部')
GO

INSERT INTO [dbo].[sys_emp]  VALUES (N'1280709549107552257', N'100', N'1265476890672672770', N'华夏集团成都分公司')
GO


-- ----------------------------
-- Table structure for sys_emp_ext_org_pos
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[sys_emp_ext_org_pos]') AND type IN ('U'))
	DROP TABLE [dbo].[sys_emp_ext_org_pos]
GO

CREATE TABLE [dbo].[sys_emp_ext_org_pos] (
  [id] bigint  NOT NULL,
  [emp_id] bigint  NOT NULL,
  [org_id] bigint  NOT NULL,
  [pos_id] bigint  NOT NULL
)
GO

ALTER TABLE [dbo].[sys_emp_ext_org_pos] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
'MS_Description', N'主键',
'SCHEMA', N'dbo',
'TABLE', N'sys_emp_ext_org_pos',
'COLUMN', N'id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'员工id',
'SCHEMA', N'dbo',
'TABLE', N'sys_emp_ext_org_pos',
'COLUMN', N'emp_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'机构id',
'SCHEMA', N'dbo',
'TABLE', N'sys_emp_ext_org_pos',
'COLUMN', N'org_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'岗位id',
'SCHEMA', N'dbo',
'TABLE', N'sys_emp_ext_org_pos',
'COLUMN', N'pos_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'员工附属机构岗位表',
'SCHEMA', N'dbo',
'TABLE', N'sys_emp_ext_org_pos'
GO


-- ----------------------------
-- Table structure for sys_emp_pos
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[sys_emp_pos]') AND type IN ('U'))
	DROP TABLE [dbo].[sys_emp_pos]
GO

CREATE TABLE [dbo].[sys_emp_pos] (
  [id] bigint  NOT NULL,
  [emp_id] bigint  NOT NULL,
  [pos_id] bigint  NOT NULL
)
GO

ALTER TABLE [dbo].[sys_emp_pos] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
'MS_Description', N'主键',
'SCHEMA', N'dbo',
'TABLE', N'sys_emp_pos',
'COLUMN', N'id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'员工id',
'SCHEMA', N'dbo',
'TABLE', N'sys_emp_pos',
'COLUMN', N'emp_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'职位id',
'SCHEMA', N'dbo',
'TABLE', N'sys_emp_pos',
'COLUMN', N'pos_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'员工职位关联表',
'SCHEMA', N'dbo',
'TABLE', N'sys_emp_pos'
GO


-- ----------------------------
-- Records of sys_emp_pos
-- ----------------------------
INSERT INTO [dbo].[sys_emp_pos]  VALUES (N'1280710811995709441', N'1275735541155614721', N'1265476890672672787')
GO

INSERT INTO [dbo].[sys_emp_pos]  VALUES (N'1280710828479324161', N'1280700700074041345', N'1265476890672672790')
GO

INSERT INTO [dbo].[sys_emp_pos]  VALUES (N'1281042262003867649', N'1280709549107552257', N'1265476890672672787')
GO


-- ----------------------------
-- Table structure for sys_file_info
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[sys_file_info]') AND type IN ('U'))
	DROP TABLE [dbo].[sys_file_info]
GO

CREATE TABLE [dbo].[sys_file_info] (
  [id] bigint  NOT NULL,
  [file_location] tinyint  NOT NULL,
  [file_bucket] nvarchar(1000) COLLATE Chinese_PRC_CI_AS  NULL,
  [file_origin_name] nvarchar(100) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [file_suffix] nvarchar(50) COLLATE Chinese_PRC_CI_AS  NULL,
  [file_size_kb] bigint  NULL,
  [file_size_info] nvarchar(100) COLLATE Chinese_PRC_CI_AS  NULL,
  [file_object_name] nvarchar(100) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [file_path] nvarchar(1000) COLLATE Chinese_PRC_CI_AS  NULL,
  [create_time] datetime2(7)  NULL,
  [create_user] bigint  NULL,
  [update_time] datetime2(7)  NULL,
  [update_user] bigint  NULL
)
GO

ALTER TABLE [dbo].[sys_file_info] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
'MS_Description', N'主键id',
'SCHEMA', N'dbo',
'TABLE', N'sys_file_info',
'COLUMN', N'id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'文件存储位置（1:阿里云，2:腾讯云，3:minio，4:本地）',
'SCHEMA', N'dbo',
'TABLE', N'sys_file_info',
'COLUMN', N'file_location'
GO

EXEC sp_addextendedproperty
'MS_Description', N'文件仓库',
'SCHEMA', N'dbo',
'TABLE', N'sys_file_info',
'COLUMN', N'file_bucket'
GO

EXEC sp_addextendedproperty
'MS_Description', N'文件名称（上传时候的文件名）',
'SCHEMA', N'dbo',
'TABLE', N'sys_file_info',
'COLUMN', N'file_origin_name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'文件后缀',
'SCHEMA', N'dbo',
'TABLE', N'sys_file_info',
'COLUMN', N'file_suffix'
GO

EXEC sp_addextendedproperty
'MS_Description', N'文件大小kb',
'SCHEMA', N'dbo',
'TABLE', N'sys_file_info',
'COLUMN', N'file_size_kb'
GO

EXEC sp_addextendedproperty
'MS_Description', N'文件大小信息，计算后的',
'SCHEMA', N'dbo',
'TABLE', N'sys_file_info',
'COLUMN', N'file_size_info'
GO

EXEC sp_addextendedproperty
'MS_Description', N'存储到bucket的名称（文件唯一标识id）',
'SCHEMA', N'dbo',
'TABLE', N'sys_file_info',
'COLUMN', N'file_object_name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'存储路径',
'SCHEMA', N'dbo',
'TABLE', N'sys_file_info',
'COLUMN', N'file_path'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建时间',
'SCHEMA', N'dbo',
'TABLE', N'sys_file_info',
'COLUMN', N'create_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建用户',
'SCHEMA', N'dbo',
'TABLE', N'sys_file_info',
'COLUMN', N'create_user'
GO

EXEC sp_addextendedproperty
'MS_Description', N'修改时间',
'SCHEMA', N'dbo',
'TABLE', N'sys_file_info',
'COLUMN', N'update_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'修改用户',
'SCHEMA', N'dbo',
'TABLE', N'sys_file_info',
'COLUMN', N'update_user'
GO

EXEC sp_addextendedproperty
'MS_Description', N'文件信息表',
'SCHEMA', N'dbo',
'TABLE', N'sys_file_info'
GO


-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[sys_menu]') AND type IN ('U'))
	DROP TABLE [dbo].[sys_menu]
GO

CREATE TABLE [dbo].[sys_menu] (
  [id] bigint  NOT NULL,
  [pid] bigint  NOT NULL,
  [pids] nvarchar(max) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [name] nvarchar(100) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [code] nvarchar(50) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [type] tinyint  NOT NULL,
  [icon] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [router] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [component] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [permission] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [application] nvarchar(50) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [open_type] tinyint  NOT NULL,
  [visible] nchar(1) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [link] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [redirect] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [weight] tinyint  NULL,
  [sort] int  NOT NULL,
  [remark] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [status] tinyint  NOT NULL,
  [create_time] datetime2(7)  NULL,
  [create_user] bigint  NULL,
  [update_time] datetime2(7)  NULL,
  [update_user] bigint  NULL
)
GO

ALTER TABLE [dbo].[sys_menu] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
'MS_Description', N'主键',
'SCHEMA', N'dbo',
'TABLE', N'sys_menu',
'COLUMN', N'id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'父id',
'SCHEMA', N'dbo',
'TABLE', N'sys_menu',
'COLUMN', N'pid'
GO

EXEC sp_addextendedproperty
'MS_Description', N'父ids',
'SCHEMA', N'dbo',
'TABLE', N'sys_menu',
'COLUMN', N'pids'
GO

EXEC sp_addextendedproperty
'MS_Description', N'名称',
'SCHEMA', N'dbo',
'TABLE', N'sys_menu',
'COLUMN', N'name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'编码',
'SCHEMA', N'dbo',
'TABLE', N'sys_menu',
'COLUMN', N'code'
GO

EXEC sp_addextendedproperty
'MS_Description', N'菜单类型（字典 0目录 1菜单 2按钮）',
'SCHEMA', N'dbo',
'TABLE', N'sys_menu',
'COLUMN', N'type'
GO

EXEC sp_addextendedproperty
'MS_Description', N'图标',
'SCHEMA', N'dbo',
'TABLE', N'sys_menu',
'COLUMN', N'icon'
GO

EXEC sp_addextendedproperty
'MS_Description', N'路由地址',
'SCHEMA', N'dbo',
'TABLE', N'sys_menu',
'COLUMN', N'router'
GO

EXEC sp_addextendedproperty
'MS_Description', N'组件地址',
'SCHEMA', N'dbo',
'TABLE', N'sys_menu',
'COLUMN', N'component'
GO

EXEC sp_addextendedproperty
'MS_Description', N'权限标识',
'SCHEMA', N'dbo',
'TABLE', N'sys_menu',
'COLUMN', N'permission'
GO

EXEC sp_addextendedproperty
'MS_Description', N'应用分类（应用编码）',
'SCHEMA', N'dbo',
'TABLE', N'sys_menu',
'COLUMN', N'application'
GO

EXEC sp_addextendedproperty
'MS_Description', N'打开方式（字典 0无 1组件 2内链 3外链）',
'SCHEMA', N'dbo',
'TABLE', N'sys_menu',
'COLUMN', N'open_type'
GO

EXEC sp_addextendedproperty
'MS_Description', N'是否可见（Y-是，N-否）',
'SCHEMA', N'dbo',
'TABLE', N'sys_menu',
'COLUMN', N'visible'
GO

EXEC sp_addextendedproperty
'MS_Description', N'链接地址',
'SCHEMA', N'dbo',
'TABLE', N'sys_menu',
'COLUMN', N'link'
GO

EXEC sp_addextendedproperty
'MS_Description', N'重定向地址',
'SCHEMA', N'dbo',
'TABLE', N'sys_menu',
'COLUMN', N'redirect'
GO

EXEC sp_addextendedproperty
'MS_Description', N'权重（字典 1系统权重 2业务权重）',
'SCHEMA', N'dbo',
'TABLE', N'sys_menu',
'COLUMN', N'weight'
GO

EXEC sp_addextendedproperty
'MS_Description', N'排序',
'SCHEMA', N'dbo',
'TABLE', N'sys_menu',
'COLUMN', N'sort'
GO

EXEC sp_addextendedproperty
'MS_Description', N'备注',
'SCHEMA', N'dbo',
'TABLE', N'sys_menu',
'COLUMN', N'remark'
GO

EXEC sp_addextendedproperty
'MS_Description', N'状态（字典 0正常 1停用 2删除）',
'SCHEMA', N'dbo',
'TABLE', N'sys_menu',
'COLUMN', N'status'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建时间',
'SCHEMA', N'dbo',
'TABLE', N'sys_menu',
'COLUMN', N'create_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建人',
'SCHEMA', N'dbo',
'TABLE', N'sys_menu',
'COLUMN', N'create_user'
GO

EXEC sp_addextendedproperty
'MS_Description', N'修改时间',
'SCHEMA', N'dbo',
'TABLE', N'sys_menu',
'COLUMN', N'update_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'修改人',
'SCHEMA', N'dbo',
'TABLE', N'sys_menu',
'COLUMN', N'update_user'
GO

EXEC sp_addextendedproperty
'MS_Description', N'系统菜单表',
'SCHEMA', N'dbo',
'TABLE', N'sys_menu'
GO


-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255311', N'0', N'[0],', N'主控面板', N'system_index', N'0', N'home', N'/', N'RouteView', NULL, N'system', N'0', N'Y', NULL, N'/analysis', N'1', N'1', NULL, N'0', N'2020-05-25 02:19:24.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255321', N'1264622039642255311', N'[0],[1264622039642255311],', N'分析页', N'system_index_dashboard', N'1', NULL, N'analysis', N'system/dashboard/Analysis', NULL, N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-05-25 02:21:55.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255331', N'1264622039642255311', N'[0],[1264622039642255311],', N'工作台', N'system_index_workplace', N'1', NULL, N'workplace', N'system/dashboard/Workplace', NULL, N'system', N'0', N'Y', NULL, NULL, N'1', N'2', NULL, N'0', N'2020-05-25 02:23:48.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255341', N'0', N'[0],', N'组织架构', N'sys_mgr', N'0', N'team', N'/sys', N'PageView', NULL, N'system', N'0', N'Y', NULL, NULL, N'1', N'2', NULL, N'0', N'2020-03-27 15:58:16.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255351', N'1264622039642255341', N'[0],[1264622039642255341],', N'用户管理', N'sys_user_mgr', N'1', NULL, N'/mgr_user', N'system/user/index', NULL, N'system', N'1', N'Y', NULL, NULL, N'1', N'3', NULL, N'0', N'2020-03-27 16:10:21.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255361', N'1264622039642255351', N'[0],[1264622039642255341],[1264622039642255351],', N'用户查询', N'sys_user_mgr_page', N'2', NULL, NULL, NULL, N'sysUser:page', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-03-27 16:36:49.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255371', N'1264622039642255351', N'[0],[1264622039642255341],[1264622039642255351],', N'用户编辑', N'sys_user_mgr_edit', N'2', NULL, NULL, NULL, N'sysUser:edit', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-07-02 12:20:23.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255381', N'1264622039642255351', N'[0],[1264622039642255341],[1264622039642255351],', N'用户增加', N'sys_user_mgr_add', N'2', NULL, NULL, NULL, N'sysUser:add', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-03-27 16:37:35.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255391', N'1264622039642255351', N'[0],[1264622039642255341],[1264622039642255351],', N'用户删除', N'sys_user_mgr_delete', N'2', NULL, NULL, NULL, N'sysUser:delete', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-03-27 16:37:58.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255401', N'1264622039642255351', N'[0],[1264622039642255341],[1264622039642255351],', N'用户详情', N'sys_user_mgr_detail', N'2', NULL, NULL, NULL, N'sysUser:detail', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-03-27 16:38:25.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255411', N'1264622039642255351', N'[0],[1264622039642255341],[1264622039642255351],', N'用户导出', N'sys_user_mgr_export', N'2', NULL, NULL, NULL, N'sysUser:export', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-07-02 12:21:59.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255421', N'1264622039642255351', N'[0],[1264622039642255341],[1264622039642255351],', N'用户选择器', N'sys_user_mgr_selector', N'2', NULL, NULL, NULL, N'sysUser:selector', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-07-03 13:30:14.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255431', N'1264622039642255351', N'[0],[1264622039642255341],[1264622039642255351],', N'用户授权角色', N'sys_user_mgr_grant_role', N'2', NULL, NULL, NULL, N'sysUser:grantRole', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-04-01 09:22:01.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255441', N'1264622039642255351', N'[0],[1264622039642255341],[1264622039642255351],', N'用户拥有角色', N'sys_user_mgr_own_role', N'2', NULL, NULL, NULL, N'sysUser:ownRole', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-05-29 14:27:22.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255451', N'1264622039642255351', N'[0],[1264622039642255341],[1264622039642255351],', N'用户授权数据', N'sys_user_mgr_grant_data', N'2', NULL, NULL, NULL, N'sysUser:grantData', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-04-01 09:22:13.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255461', N'1264622039642255351', N'[0],[1264622039642255341],[1264622039642255351],', N'用户拥有数据', N'sys_user_mgr_own_data', N'2', NULL, NULL, NULL, N'sysUser:ownData', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-05-29 14:27:41.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255471', N'1264622039642255351', N'[0],[1264622039642255341],[1264622039642255351],', N'用户更新信息', N'sys_user_mgr_update_info', N'2', NULL, NULL, NULL, N'sysUser:updateInfo', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-04-01 16:19:32.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255481', N'1264622039642255351', N'[0],[1264622039642255341],[1264622039642255351],', N'用户修改密码', N'sys_user_mgr_update_pwd', N'2', NULL, NULL, NULL, N'sysUser:updatePwd', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-04-01 16:20:25.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255491', N'1264622039642255351', N'[0],[1264622039642255341],[1264622039642255351],', N'用户修改状态', N'sys_user_mgr_change_status', N'2', NULL, NULL, NULL, N'sysUser:changeStatus', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-06-23 11:13:14.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255501', N'1264622039642255351', N'[0],[1264622039642255341],[1264622039642255351],', N'用户修改头像', N'sys_user_mgr_update_avatar', N'2', NULL, NULL, NULL, N'sysUser:updateAvatar', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-07-02 12:21:42.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255511', N'1264622039642255351', N'[0],[1264622039642255341],[1264622039642255351],', N'用户重置密码', N'sys_user_mgr_reset_pwd', N'2', NULL, NULL, NULL, N'sysUser:resetPwd', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-05-29 15:01:51.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255521', N'1264622039642255341', N'[0],[1264622039642255341],', N'机构管理', N'sys_org_mgr', N'1', NULL, N'/org', N'system/org/index', NULL, N'system', N'1', N'Y', NULL, NULL, N'1', N'4', NULL, N'0', N'2020-03-27 17:15:39.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255531', N'1264622039642255521', N'[0],[1264622039642255341],[1264622039642255521]', N'机构查询', N'sys_org_mgr_page', N'2', NULL, NULL, NULL, N'sysOrg:page', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-03-27 17:17:37.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255541', N'1264622039642255521', N'[0],[1264622039642255341],[1264622039642255521]', N'机构列表', N'sys_org_mgr_list', N'2', NULL, NULL, NULL, N'sysOrg:list', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-07-02 11:54:26.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255551', N'1264622039642255521', N'[0],[1264622039642255341],[1264622039642255521]', N'机构增加', N'sys_org_mgr_add', N'2', NULL, NULL, NULL, N'sysOrg:add', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-03-27 17:19:53.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255561', N'1264622039642255521', N'[0],[1264622039642255341],[1264622039642255521]', N'机构编辑', N'sys_org_mgr_edit', N'2', NULL, NULL, NULL, N'sysOrg:edit', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-07-02 11:54:37.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255571', N'1264622039642255521', N'[0],[1264622039642255341],[1264622039642255521]', N'机构删除', N'sys_org_mgr_delete', N'2', NULL, NULL, NULL, N'sysOrg:delete', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-03-27 17:20:48.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255581', N'1264622039642255521', N'[0],[1264622039642255341],[1264622039642255521]', N'机构详情', N'sys_org_mgr_detail', N'2', NULL, NULL, NULL, N'sysOrg:detail', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-03-27 17:21:15.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255591', N'1264622039642255521', N'[0],[1264622039642255341],[1264622039642255521]', N'机构树', N'sys_org_mgr_tree', N'2', NULL, NULL, NULL, N'sysOrg:tree', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-03-27 17:21:58.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255601', N'1264622039642255341', N'[0],[1264622039642255341],', N'职位管理', N'sys_pos_mgr', N'1', NULL, N'/pos', N'system/pos/index', NULL, N'system', N'1', N'Y', NULL, NULL, N'1', N'5', NULL, N'0', N'2020-03-27 18:38:31.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255611', N'1264622039642255601', N'[0],[1264622039642255341],[1264622039642255601],', N'职位查询', N'sys_pos_mgr_page', N'2', NULL, NULL, NULL, N'sysPos:page', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-03-27 18:41:48.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255621', N'1264622039642255601', N'[0],[1264622039642255341],[1264622039642255601],', N'职位列表', N'sys_pos_mgr_list', N'2', NULL, NULL, NULL, N'sysPos:list', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-07-02 11:55:57.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255631', N'1264622039642255601', N'[0],[1264622039642255341],[1264622039642255601],', N'职位增加', N'sys_pos_mgr_add', N'2', NULL, NULL, NULL, N'sysPos:add', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-03-27 18:42:20.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255641', N'1264622039642255601', N'[0],[1264622039642255341],[1264622039642255601],', N'职位编辑', N'sys_pos_mgr_edit', N'2', NULL, NULL, NULL, N'sysPos:edit', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-07-02 11:56:08.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255651', N'1264622039642255601', N'[0],[1264622039642255341],[1264622039642255601],', N'职位删除', N'sys_pos_mgr_delete', N'2', NULL, NULL, NULL, N'sysPos:delete', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-03-27 18:42:39.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255661', N'1264622039642255601', N'[0],[1264622039642255341],[1264622039642255601],', N'职位详情', N'sys_pos_mgr_detail', N'2', NULL, NULL, NULL, N'sysPos:detail', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-03-27 18:43:00.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255671', N'0', N'[0],', N'权限管理', N'auth_manager', N'0', N'safety-certificate', N'/auth', N'PageView', NULL, N'system', N'0', N'Y', NULL, NULL, N'1', N'3', NULL, N'0', N'2020-07-15 15:51:57.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255681', N'1264622039642255671', N'[0],[1264622039642255671],', N'应用管理', N'sys_app_mgr', N'1', NULL, N'/app', N'system/app/index', NULL, N'system', N'1', N'Y', NULL, NULL, N'1', N'6', NULL, N'0', N'2020-03-27 16:40:21.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255691', N'1264622039642255681', N'[0],[1264622039642255671],[1264622039642255681],', N'应用查询', N'sys_app_mgr_page', N'2', NULL, NULL, NULL, N'sysApp:page', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-03-27 16:41:58.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255701', N'1264622039642255681', N'[0],[1264622039642255671],[1264622039642255681],', N'应用列表', N'sys_app_mgr_list', N'2', NULL, NULL, NULL, N'sysApp:list', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-07-02 10:04:59.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255711', N'1264622039642255681', N'[0],[1264622039642255671],[1264622039642255681],', N'应用增加', N'sys_app_mgr_add', N'2', NULL, NULL, NULL, N'sysApp:add', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-03-27 16:44:10.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255721', N'1264622039642255681', N'[0],[1264622039642255671],[1264622039642255681],', N'应用编辑', N'sys_app_mgr_edit', N'2', NULL, NULL, NULL, N'sysApp:edit', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-07-02 10:04:34.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255731', N'1264622039642255681', N'[0],[1264622039642255671],[1264622039642255681],', N'应用删除', N'sys_app_mgr_delete', N'2', NULL, NULL, NULL, N'sysApp:delete', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-03-27 17:14:29.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255741', N'1264622039642255681', N'[0],[1264622039642255671],[1264622039642255681],', N'应用详情', N'sys_app_mgr_detail', N'2', NULL, NULL, NULL, N'sysApp:detail', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-03-27 17:14:56.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255751', N'1264622039642255681', N'[0],[1264622039642255671],[1264622039642255681],', N'设为默认应用', N'sys_app_mgr_set_as_default', N'2', NULL, NULL, NULL, N'sysApp:setAsDefault', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-03-27 17:14:56.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255761', N'1264622039642255671', N'[0],[1264622039642255671],', N'菜单管理', N'sys_menu_mgr', N'1', NULL, N'/menu', N'system/menu/index', NULL, N'system', N'1', N'Y', NULL, NULL, N'1', N'7', NULL, N'0', N'2020-03-27 18:44:35.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255771', N'1264622039642255761', N'[0],[1264622039642255671],[1264622039642255761],', N'菜单列表', N'sys_menu_mgr_list', N'2', NULL, NULL, NULL, N'sysMenu:list', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-03-27 18:45:20.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255781', N'1264622039642255761', N'[0],[1264622039642255671],[1264622039642255761],', N'菜单增加', N'sys_menu_mgr_add', N'2', NULL, NULL, NULL, N'sysMenu:add', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-03-27 18:45:37.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255791', N'1264622039642255761', N'[0],[1264622039642255671],[1264622039642255761],', N'菜单编辑', N'sys_menu_mgr_edit', N'2', NULL, NULL, NULL, N'sysMenu:edit', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-07-02 11:52:00.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255801', N'1264622039642255761', N'[0],[1264622039642255671],[1264622039642255761],', N'菜单删除', N'sys_menu_mgr_delete', N'2', NULL, NULL, NULL, N'sysMenu:delete', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-03-27 18:46:01.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255811', N'1264622039642255761', N'[0],[1264622039642255671],[1264622039642255761],', N'菜单详情', N'sys_menu_mgr_detail', N'2', NULL, NULL, NULL, N'sysMenu:detail', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-03-27 18:46:22.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255821', N'1264622039642255761', N'[0],[1264622039642255671],[1264622039642255761],', N'菜单授权树', N'sys_menu_mgr_grant_tree', N'2', NULL, NULL, NULL, N'sysMenu:treeForGrant', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-06-03 09:50:31.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255831', N'1264622039642255761', N'[0],[1264622039642255671],[1264622039642255761],', N'菜单树', N'sys_menu_mgr_tree', N'2', NULL, NULL, NULL, N'sysMenu:tree', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-03-27 18:47:50.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255841', N'1264622039642255761', N'[0],[1264622039642255671],[1264622039642255761],', N'菜单切换', N'sys_menu_mgr_change', N'2', NULL, NULL, NULL, N'sysMenu:change', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-06-03 09:51:43.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255851', N'1264622039642255671', N'[0],[1264622039642255671],', N'角色管理', N'sys_role_mgr', N'1', NULL, N'/role', N'system/role/index', NULL, N'system', N'1', N'Y', NULL, NULL, N'1', N'8', NULL, N'0', N'2020-03-28 16:01:09.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255861', N'1264622039642255851', N'[0],[1264622039642255671],[1264622039642255851],', N'角色查询', N'sys_role_mgr_page', N'2', NULL, NULL, NULL, N'sysRole:page', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-03-28 16:02:09.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255871', N'1264622039642255851', N'[0],[1264622039642255671],[1264622039642255851],', N'角色增加', N'sys_role_mgr_add', N'2', NULL, NULL, NULL, N'sysRole:add', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-03-28 16:02:27.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255881', N'1264622039642255851', N'[0],[1264622039642255671],[1264622039642255851],', N'角色编辑', N'sys_role_mgr_edit', N'2', NULL, NULL, NULL, N'sysRole:edit', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-07-02 11:57:27.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255891', N'1264622039642255851', N'[0],[1264622039642255671],[1264622039642255851],', N'角色删除', N'sys_role_mgr_delete', N'2', NULL, NULL, NULL, N'sysRole:delete', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-03-28 16:02:46.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255901', N'1264622039642255851', N'[0],[1264622039642255671],[1264622039642255851],', N'角色详情', N'sys_role_mgr_detail', N'2', NULL, NULL, NULL, N'sysRole:detail', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-03-28 16:03:01.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255911', N'1264622039642255851', N'[0],[1264622039642255671],[1264622039642255851],', N'角色下拉', N'sys_role_mgr_drop_down', N'2', NULL, NULL, NULL, N'sysRole:dropDown', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-05-29 15:45:39.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255921', N'1264622039642255851', N'[0],[1264622039642255671],[1264622039642255851],', N'角色授权菜单', N'sys_role_mgr_grant_menu', N'2', NULL, NULL, NULL, N'sysRole:grantMenu', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-04-01 09:16:27.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255931', N'1264622039642255851', N'[0],[1264622039642255671],[1264622039642255851],', N'角色拥有菜单', N'sys_role_mgr_own_menu', N'2', NULL, NULL, NULL, N'sysRole:ownMenu', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-05-29 14:21:54.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255941', N'1264622039642255851', N'[0],[1264622039642255671],[1264622039642255851],', N'角色授权数据', N'sys_role_mgr_grant_data', N'2', NULL, NULL, NULL, N'sysRole:grantData', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-04-01 09:16:56.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255951', N'1264622039642255851', N'[0],[1264622039642255671],[1264622039642255851],', N'角色拥有数据', N'sys_role_mgr_own_data', N'2', NULL, NULL, NULL, N'sysRole:ownData', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-05-29 14:23:08.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255961', N'0', N'[0],', N'开发管理', N'system_tools', N'0', N'euro', N'/tools', N'PageView', NULL, N'system', N'1', N'Y', NULL, NULL, N'1', N'4', NULL, N'0', N'2020-05-25 02:10:55.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255971', N'1264622039642255961', N'[0],[1264622039642255961],', N'系统配置', N'system_tools_config', N'1', NULL, N'/config', N'system/config/index', NULL, N'system', N'1', N'Y', NULL, NULL, N'1', N'9', NULL, N'0', N'2020-05-25 02:12:56.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255981', N'1264622039642255971', N'[0],[1264622039642255961],[1264622039642255971],', N'配置查询', N'system_tools_config_page', N'2', NULL, NULL, NULL, N'sysConfig:page', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-05-27 17:02:22.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642255991', N'1264622039642255971', N'[0],[1264622039642255961],[1264622039642255971],', N'配置列表', N'system_tools_config_list', N'2', NULL, NULL, NULL, N'sysConfig:list', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-05-27 17:02:42.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256001', N'1264622039642255971', N'[0],[1264622039642255961],[1264622039642255971],', N'配置增加', N'system_tools_config_add', N'2', NULL, NULL, NULL, N'sysConfig:add', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-05-27 17:03:31.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256011', N'1264622039642255971', N'[0],[1264622039642255961],[1264622039642255971],', N'配置编辑', N'system_tools_config_edit', N'2', NULL, NULL, NULL, N'sysConfig:edit', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-05-27 17:03:55.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256021', N'1264622039642255971', N'[0],[1264622039642255961],[1264622039642255971],', N'配置删除', N'system_tools_config_delete', N'2', NULL, NULL, NULL, N'sysConfig:delete', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-05-27 17:03:44.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256031', N'1264622039642255971', N'[0],[1264622039642255961],[1264622039642255971],', N'配置详情', N'system_tools_config_detail', N'2', NULL, NULL, NULL, N'sysConfig:detail', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-05-27 17:02:59.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256041', N'1264622039642255961', N'[0],[1264622039642255961],', N'邮件发送', N'sys_email_mgr', N'1', NULL, N'/email', N'system/email/index', NULL, N'system', N'1', N'Y', NULL, NULL, N'1', N'10', NULL, N'0', N'2020-07-02 11:44:21.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256051', N'1264622039642256041', N'[0],[1264622039642255961],[1264622039642256041],', N'发送文本邮件', N'sys_email_mgr_send_email', N'2', NULL, NULL, NULL, N'email:sendEmail', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-07-02 11:45:39.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256061', N'1264622039642256041', N'[0],[1264622039642255961],[1264622039642256041],', N'发送html邮件', N'sys_email_mgr_send_email_html', N'2', NULL, NULL, NULL, N'email:sendEmailHtml', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-07-02 11:45:57.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256071', N'1264622039642255961', N'[0],[1264622039642255961],', N'短信管理', N'sys_sms_mgr', N'1', NULL, N'/sms', N'system/sms/index', NULL, N'system', N'1', N'Y', NULL, NULL, N'1', N'11', NULL, N'0', N'2020-07-02 12:00:12.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256081', N'1264622039642256071', N'[0],[1264622039642255961],[1264622039642256071],', N'短信发送查询', N'sys_sms_mgr_page', N'2', NULL, NULL, NULL, N'sms:page', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-07-02 12:16:56.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256091', N'1264622039642256071', N'[0],[1264622039642255961],[1264622039642256071],', N'发送验证码短信', N'sys_sms_mgr_send_login_message', N'2', NULL, NULL, NULL, N'sms:sendLoginMessage', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-07-02 12:02:31.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256101', N'1264622039642256071', N'[0],[1264622039642255961],[1264622039642256071],', N'验证短信验证码', N'sys_sms_mgr_validate_message', N'2', NULL, NULL, NULL, N'sms:validateMessage', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-07-02 12:02:50.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256111', N'1264622039642255961', N'[0],[1264622039642255961],', N'字典管理', N'sys_dict_mgr', N'1', NULL, N'/dict', N'system/dict/index', NULL, N'system', N'1', N'Y', NULL, NULL, N'1', N'12', NULL, N'0', N'2020-04-01 11:17:26.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256121', N'1264622039642256111', N'[0],[1264622039642255961],[1264622039642256111],', N'字典类型查询', N'sys_dict_mgr_dict_type_page', N'2', NULL, NULL, NULL, N'sysDictType:page', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-04-01 11:20:22.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256131', N'1264622039642256111', N'[0],[1264622039642255961],[1264622039642256111],', N'字典类型列表', N'sys_dict_mgr_dict_type_list', N'2', NULL, NULL, NULL, N'sysDictType:list', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-05-29 15:12:35.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256141', N'1264622039642256111', N'[0],[1264622039642255961],[1264622039642256111],', N'字典类型增加', N'sys_dict_mgr_dict_type_add', N'2', NULL, NULL, NULL, N'sysDictType:add', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-04-01 11:19:58.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256151', N'1264622039642256111', N'[0],[1264622039642255961],[1264622039642256111],', N'字典类型删除', N'sys_dict_mgr_dict_type_delete', N'2', NULL, NULL, NULL, N'sysDictType:delete', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-04-01 11:21:30.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256161', N'1264622039642256111', N'[0],[1264622039642255961],[1264622039642256111],', N'字典类型编辑', N'sys_dict_mgr_dict_type_edit', N'2', NULL, NULL, NULL, N'sysDictType:edit', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-04-01 11:21:42.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256171', N'1264622039642256111', N'[0],[1264622039642255961],[1264622039642256111],', N'字典类型详情', N'sys_dict_mgr_dict_type_detail', N'2', NULL, NULL, NULL, N'sysDictType:detail', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-04-01 11:22:06.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256181', N'1264622039642256111', N'[0],[1264622039642255961],[1264622039642256111],', N'字典类型下拉', N'sys_dict_mgr_dict_type_drop_down', N'2', NULL, NULL, NULL, N'sysDictType:dropDown', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-04-01 11:22:23.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256191', N'1264622039642256111', N'[0],[1264622039642255961],[1264622039642256111],', N'字典类型修改状态', N'sys_dict_mgr_dict_type_change_status', N'2', NULL, NULL, NULL, N'sysDictType:changeStatus', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-06-23 11:15:50.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256201', N'1264622039642256111', N'[0],[1264622039642255961],[1264622039642256111],', N'字典值查询', N'sys_dict_mgr_dict_page', N'2', NULL, NULL, NULL, N'sysDictData:page', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-04-01 11:23:11.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256211', N'1264622039642256111', N'[0],[1264622039642255961],[1264622039642256111],', N'字典值列表', N'sys_dict_mgr_dict_list', N'2', NULL, NULL, NULL, N'sysDictData:list', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-04-01 11:24:58.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256221', N'1264622039642256111', N'[0],[1264622039642255961],[1264622039642256111],', N'字典值增加', N'sys_dict_mgr_dict_add', N'2', NULL, NULL, NULL, N'sysDictData:add', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-04-01 11:22:51.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256231', N'1264622039642256111', N'[0],[1264622039642255961],[1264622039642256111],', N'字典值删除', N'sys_dict_mgr_dict_delete', N'2', NULL, NULL, NULL, N'sysDictData:delete', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-04-01 11:23:26.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256241', N'1264622039642256111', N'[0],[1264622039642255961],[1264622039642256111],', N'字典值编辑', N'sys_dict_mgr_dict_edit', N'2', NULL, NULL, NULL, N'sysDictData:edit', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-04-01 11:24:21.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256251', N'1264622039642256111', N'[0],[1264622039642255961],[1264622039642256111],', N'字典值详情', N'sys_dict_mgr_dict_detail', N'2', NULL, NULL, NULL, N'sysDictData:detail', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-04-01 11:24:42.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256261', N'1264622039642256111', N'[0],[1264622039642255961],[1264622039642256111],', N'字典值修改状态', N'sys_dict_mgr_dict_change_status', N'2', NULL, NULL, NULL, N'sysDictData:changeStatus', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-06-23 11:17:53.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256271', N'1264622039642255961', N'[0],[1264622039642255961],', N'接口文档', N'sys_swagger_mgr', N'1', NULL, N'/swagger', N'Iframe', NULL, N'system', N'2', N'Y', N'http://localhost:82/doc.html', NULL, N'1', N'13', NULL, N'0', N'2020-07-02 12:16:56.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256281', N'0', N'[0],', N'日志管理', N'sys_log_mgr', N'0', N'read', N'/log', N'PageView', NULL, N'system', N'1', N'Y', NULL, NULL, N'1', N'5', NULL, N'0', N'2020-04-01 09:25:01.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256291', N'1264622039642256281', N'[0],[1264622039642256281],', N'访问日志', N'sys_log_mgr_vis_log', N'1', NULL, N'/vislog', N'system/log/vislog/index', NULL, N'system', N'0', N'Y', NULL, NULL, N'1', N'14', NULL, N'0', N'2020-04-01 09:26:40.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256301', N'1264622039642256291', N'[0],[1264622039642256281],[1264622039642256291],', N'访问日志查询', N'sys_log_mgr_vis_log_page', N'2', NULL, NULL, NULL, N'sysVisLog:page', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-07-02 09:55:51.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256311', N'1264622039642256291', N'[0],[1264622039642256281],[1264622039642256291],', N'访问日志清空', N'sys_log_mgr_vis_log_delete', N'2', NULL, NULL, NULL, N'sysVisLog:delete', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-07-02 09:56:57.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256321', N'1264622039642256281', N'[0],[1264622039642256281],', N'操作日志', N'sys_log_mgr_op_log', N'1', NULL, N'/oplog', N'system/log/oplog/index', NULL, N'system', N'0', N'Y', NULL, NULL, N'1', N'15', NULL, N'0', N'2020-04-01 09:26:59.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256331', N'1264622039642256321', N'[0],[1264622039642256281],[1264622039642256321],', N'操作日志查询', N'sys_log_mgr_op_log_page', N'2', NULL, NULL, NULL, N'sysOpLog:page', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-07-02 09:57:39.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256341', N'1264622039642256321', N'[0],[1264622039642256281],[1264622039642256321],', N'操作日志清空', N'sys_log_mgr_op_log_delete', N'2', NULL, NULL, NULL, N'sysOpLog:delete', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-07-02 09:58:13.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256351', N'0', N'[0],', N'系统监控', N'sys_monitor_mgr', N'0', N'deployment-unit', N'/monitor', N'PageView', NULL, N'system', N'1', N'Y', NULL, NULL, N'1', N'6', NULL, N'0', N'2020-06-05 16:00:50.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256361', N'1264622039642256351', N'[0],[1264622039642256351],', N'服务监控', N'sys_monitor_mgr_machine_monitor', N'1', NULL, N'/machine', N'system/machine/index', NULL, N'system', N'1', N'Y', NULL, NULL, N'1', N'16', NULL, N'0', N'2020-06-05 16:02:38.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256371', N'1264622039642256361', N'[0],[1264622039642256351],[1264622039642256361],', N'服务监控查询', N'sys_monitor_mgr_machine_monitor_query', N'2', NULL, NULL, NULL, N'sysMachine:query', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-06-05 16:05:33.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256381', N'1264622039642256351', N'[0],[1264622039642256351],', N'在线用户', N'sys_monitor_mgr_online_user', N'1', NULL, N'/onlineUser', N'system/onlineUser/index', NULL, N'system', N'1', N'Y', NULL, NULL, N'1', N'17', NULL, N'0', N'2020-06-05 16:01:55.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256391', N'1264622039642256381', N'[0],[1264622039642256351],[1264622039642256381],', N'在线用户列表', N'sys_monitor_mgr_online_user_list', N'2', NULL, NULL, NULL, N'sysOnlineUser:list', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-06-05 16:03:46.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256401', N'1264622039642256381', N'[0],[1264622039642256351],[1264622039642256381],', N'在线用户强退', N'sys_monitor_mgr_online_user_force_exist', N'2', NULL, NULL, NULL, N'sysOnlineUser:forceExist', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-06-05 16:04:16.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256411', N'1264622039642256351', N'[0],[1264622039642256351],', N'数据监控', N'sys_monitor_mgr_druid', N'1', NULL, N'/druid', N'Iframe', NULL, N'system', N'2', N'Y', N'http://localhost:82/druid/login.html', NULL, N'1', N'18', NULL, N'0', N'2020-06-28 16:15:07.0000000', N'1265476890672672808', N'2020-09-13 09:39:10.0000000', N'1265476890672672808')
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256421', N'0', N'[0],', N'通知公告', N'sys_notice', N'0', N'sound', N'/notice', N'PageView', NULL, N'system', N'1', N'Y', NULL, NULL, N'1', N'7', NULL, N'0', N'2020-06-29 15:41:53.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256431', N'1264622039642256421', N'[0],[1264622039642256421],', N'公告管理', N'sys_notice_mgr', N'1', NULL, N'/notice', N'system/notice/index', NULL, N'system', N'1', N'Y', NULL, NULL, N'1', N'19', NULL, N'0', N'2020-06-29 15:44:24.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256441', N'1264622039642256431', N'[0],[1264622039642256421],[1264622039642256431],', N'公告查询', N'sys_notice_mgr_page', N'2', NULL, NULL, NULL, N'sysNotice:page', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-06-29 15:45:30.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256451', N'1264622039642256431', N'[0],[1264622039642256421],[1264622039642256431],', N'公告增加', N'sys_notice_mgr_add', N'2', NULL, NULL, NULL, N'sysNotice:add', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-06-29 15:45:57.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256461', N'1264622039642256431', N'[0],[1264622039642256421],[1264622039642256431],', N'公告编辑', N'sys_notice_mgr_edit', N'2', NULL, NULL, NULL, N'sysNotice:edit', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-06-29 15:46:22.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256471', N'1264622039642256431', N'[0],[1264622039642256421],[1264622039642256431],', N'公告删除', N'sys_notice_mgr_delete', N'2', NULL, NULL, NULL, N'sysNotice:delete', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-06-29 15:46:11.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256481', N'1264622039642256431', N'[0],[1264622039642256421],[1264622039642256431],', N'公告查看', N'sys_notice_mgr_detail', N'2', NULL, NULL, NULL, N'sysNotice:detail', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-06-29 15:46:33.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256491', N'1264622039642256431', N'[0],[1264622039642256421],[1264622039642256431],', N'公告修改状态', N'sys_notice_mgr_changeStatus', N'2', NULL, NULL, NULL, N'sysNotice:changeStatus', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-06-29 15:46:50.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256501', N'1264622039642256421', N'[0],[1264622039642256421],', N'已收公告', N'sys_notice_mgr_received', N'1', NULL, N'/noticeReceived', N'system/noticeReceived/index', NULL, N'system', N'1', N'Y', NULL, NULL, N'1', N'20', NULL, N'0', N'2020-06-29 16:32:53.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256511', N'1264622039642256501', N'[0],[1264622039642256421],[1264622039642256501],', N'已收公告查询', N'sys_notice_mgr_received_page', N'2', NULL, NULL, NULL, N'sysNotice:received', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-06-29 16:33:43.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256521', N'0', N'[0],', N'文件管理', N'sys_file_mgr', N'0', N'file', N'/file', N'PageView', NULL, N'system', N'1', N'Y', NULL, NULL, N'1', N'8', NULL, N'0', N'2020-06-24 17:31:10.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256531', N'1264622039642256521', N'[0],[1264622039642256521],', N'系统文件', N'sys_file_mgr_sys_file', N'1', NULL, N'/file', N'system/file/index', NULL, N'system', N'1', N'Y', NULL, NULL, N'1', N'21', NULL, N'0', N'2020-06-24 17:32:57.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256541', N'1264622039642256531', N'[0],[1264622039642256521],[1264622039642256531],', N'文件查询', N'sys_file_mgr_sys_file_page', N'2', NULL, NULL, NULL, N'sysFileInfo:page', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-06-24 17:35:38.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256551', N'1264622039642256531', N'[0],[1264622039642256521],[1264622039642256531],', N'文件列表', N'sys_file_mgr_sys_file_list', N'2', NULL, NULL, NULL, N'sysFileInfo:list', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-06-24 17:35:49.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256561', N'1264622039642256531', N'[0],[1264622039642256521],[1264622039642256531],', N'文件删除', N'sys_file_mgr_sys_file_delete', N'2', NULL, NULL, NULL, N'sysFileInfo:delete', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-06-24 17:36:11.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256571', N'1264622039642256531', N'[0],[1264622039642256521],[1264622039642256531],', N'文件详情', N'sys_file_mgr_sys_file_detail', N'2', NULL, NULL, NULL, N'sysFileInfo:detail', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-06-24 17:36:01.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256581', N'1264622039642256531', N'[0],[1264622039642256521],[1264622039642256531],', N'文件上传', N'sys_file_mgr_sys_file_upload', N'2', NULL, NULL, NULL, N'sysFileInfo:upload', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-06-24 17:34:29.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256591', N'1264622039642256531', N'[0],[1264622039642256521],[1264622039642256531],', N'文件下载', N'sys_file_mgr_sys_file_download', N'2', NULL, NULL, NULL, N'sysFileInfo:download', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-06-24 17:34:55.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256601', N'1264622039642256531', N'[0],[1264622039642256521],[1264622039642256531],', N'图片预览', N'sys_file_mgr_sys_file_preview', N'2', NULL, NULL, NULL, N'sysFileInfo:preview', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-06-24 17:35:19.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256611', N'0', N'[0],', N'定时任务', N'sys_timers', N'0', N'dashboard', N'/timers', N'PageView', NULL, N'system', N'1', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-07-01 17:17:20.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256621', N'1264622039642256611', N'[0],[1264622039642256611],', N'任务管理', N'sys_timers_mgr', N'1', NULL, N'/timers', N'system/timers/index', NULL, N'system', N'1', N'Y', NULL, NULL, N'1', N'22', NULL, N'0', N'2020-07-01 17:18:53.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256631', N'1264622039642256621', N'[0],[1264622039642256611],[1264622039642256621],', N'定时任务查询', N'sys_timers_mgr_page', N'2', NULL, NULL, NULL, N'sysTimers:page', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-07-01 17:19:43.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256641', N'1264622039642256621', N'[0],[1264622039642256611],[1264622039642256621],', N'定时任务列表', N'sys_timers_mgr_list', N'2', NULL, NULL, NULL, N'sysTimers:list', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-07-01 17:19:56.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256651', N'1264622039642256621', N'[0],[1264622039642256611],[1264622039642256621],', N'定时任务详情', N'sys_timers_mgr_detail', N'2', NULL, NULL, NULL, N'sysTimers:detail', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-07-01 17:20:10.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256661', N'1264622039642256621', N'[0],[1264622039642256611],[1264622039642256621],', N'定时任务增加', N'sys_timers_mgr_add', N'2', NULL, NULL, NULL, N'sysTimers:add', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-07-01 17:20:23.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256671', N'1264622039642256621', N'[0],[1264622039642256611],[1264622039642256621],', N'定时任务删除', N'sys_timers_mgr_delete', N'2', NULL, NULL, NULL, N'sysTimers:delete', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-07-01 17:20:33.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256681', N'1264622039642256621', N'[0],[1264622039642256611],[1264622039642256621],', N'定时任务编辑', N'sys_timers_mgr_edit', N'2', NULL, NULL, NULL, N'sysTimers:edit', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-07-01 17:20:43.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256691', N'1264622039642256621', N'[0],[1264622039642256611],[1264622039642256621],', N'定时任务可执行列表', N'sys_timers_mgr_get_action_classes', N'2', NULL, NULL, NULL, N'sysTimers:getActionClasses', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-07-01 17:22:16.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256701', N'1264622039642256621', N'[0],[1264622039642256611],[1264622039642256621],', N'定时任务启动', N'sys_timers_mgr_start', N'2', NULL, NULL, NULL, N'sysTimers:start', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-07-01 17:22:32.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1264622039642256711', N'1264622039642256621', N'[0],[1264622039642256611],[1264622039642256621],', N'定时任务关闭', N'sys_timers_mgr_stop', N'2', NULL, NULL, NULL, N'sysTimers:stop', N'system', N'0', N'Y', NULL, NULL, N'1', N'100', NULL, N'0', N'2020-07-01 17:22:43.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_menu]  VALUES (N'1342445437296771074', N'0', N'[0],', N'代码生成', N'code_gen', N'1', N'thunderbolt', N'/codeGenerate/index', N'gen/codeGenerate/index', N'', N'system_tool', N'1', N'Y', NULL, N'', N'1', N'100', N'代码生成', N'0', N'2020-12-25 20:21:48.0000000', N'1265476890672672808', NULL, NULL)
GO


-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[sys_notice]') AND type IN ('U'))
	DROP TABLE [dbo].[sys_notice]
GO

CREATE TABLE [dbo].[sys_notice] (
  [id] bigint  NOT NULL,
  [title] nvarchar(1000) COLLATE Chinese_PRC_CI_AS  NULL,
  [content] nvarchar(max) COLLATE Chinese_PRC_CI_AS  NULL,
  [type] tinyint  NOT NULL,
  [public_user_id] bigint  NOT NULL,
  [public_user_name] nvarchar(100) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [public_org_id] bigint  NULL,
  [public_org_name] nvarchar(50) COLLATE Chinese_PRC_CI_AS  NULL,
  [public_time] datetime2(7)  NULL,
  [cancel_time] datetime2(7)  NULL,
  [status] tinyint  NOT NULL,
  [create_time] datetime2(7)  NULL,
  [create_user] bigint  NULL,
  [update_time] datetime2(7)  NULL,
  [update_user] bigint  NULL
)
GO

ALTER TABLE [dbo].[sys_notice] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
'MS_Description', N'主键',
'SCHEMA', N'dbo',
'TABLE', N'sys_notice',
'COLUMN', N'id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'标题',
'SCHEMA', N'dbo',
'TABLE', N'sys_notice',
'COLUMN', N'title'
GO

EXEC sp_addextendedproperty
'MS_Description', N'内容',
'SCHEMA', N'dbo',
'TABLE', N'sys_notice',
'COLUMN', N'content'
GO

EXEC sp_addextendedproperty
'MS_Description', N'类型（字典 1通知 2公告）',
'SCHEMA', N'dbo',
'TABLE', N'sys_notice',
'COLUMN', N'type'
GO

EXEC sp_addextendedproperty
'MS_Description', N'发布人id',
'SCHEMA', N'dbo',
'TABLE', N'sys_notice',
'COLUMN', N'public_user_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'发布人姓名',
'SCHEMA', N'dbo',
'TABLE', N'sys_notice',
'COLUMN', N'public_user_name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'发布机构id',
'SCHEMA', N'dbo',
'TABLE', N'sys_notice',
'COLUMN', N'public_org_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'发布机构名称',
'SCHEMA', N'dbo',
'TABLE', N'sys_notice',
'COLUMN', N'public_org_name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'发布时间',
'SCHEMA', N'dbo',
'TABLE', N'sys_notice',
'COLUMN', N'public_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'撤回时间',
'SCHEMA', N'dbo',
'TABLE', N'sys_notice',
'COLUMN', N'cancel_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'状态（字典 0草稿 1发布 2撤回 3删除）',
'SCHEMA', N'dbo',
'TABLE', N'sys_notice',
'COLUMN', N'status'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建时间',
'SCHEMA', N'dbo',
'TABLE', N'sys_notice',
'COLUMN', N'create_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建人',
'SCHEMA', N'dbo',
'TABLE', N'sys_notice',
'COLUMN', N'create_user'
GO

EXEC sp_addextendedproperty
'MS_Description', N'修改时间',
'SCHEMA', N'dbo',
'TABLE', N'sys_notice',
'COLUMN', N'update_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'修改人',
'SCHEMA', N'dbo',
'TABLE', N'sys_notice',
'COLUMN', N'update_user'
GO

EXEC sp_addextendedproperty
'MS_Description', N'通知表',
'SCHEMA', N'dbo',
'TABLE', N'sys_notice'
GO

-- ----------------------------
-- Table structure for sys_notice_user
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[sys_notice_user]') AND type IN ('U'))
	DROP TABLE [dbo].[sys_notice_user]
GO

CREATE TABLE [dbo].[sys_notice_user] (
  [id] bigint  NOT NULL,
  [notice_id] bigint  NOT NULL,
  [user_id] bigint  NOT NULL,
  [status] tinyint  NOT NULL,
  [read_time] datetime2(7)  NULL
)
GO

ALTER TABLE [dbo].[sys_notice_user] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
'MS_Description', N'主键',
'SCHEMA', N'dbo',
'TABLE', N'sys_notice_user',
'COLUMN', N'id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'通知公告id',
'SCHEMA', N'dbo',
'TABLE', N'sys_notice_user',
'COLUMN', N'notice_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'用户id',
'SCHEMA', N'dbo',
'TABLE', N'sys_notice_user',
'COLUMN', N'user_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'状态（字典 0未读 1已读）',
'SCHEMA', N'dbo',
'TABLE', N'sys_notice_user',
'COLUMN', N'status'
GO

EXEC sp_addextendedproperty
'MS_Description', N'阅读时间',
'SCHEMA', N'dbo',
'TABLE', N'sys_notice_user',
'COLUMN', N'read_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'系统用户数据范围表',
'SCHEMA', N'dbo',
'TABLE', N'sys_notice_user'
GO

-- ----------------------------
-- Table structure for sys_oauth_user
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[sys_oauth_user]') AND type IN ('U'))
	DROP TABLE [dbo].[sys_oauth_user]
GO

CREATE TABLE [dbo].[sys_oauth_user] (
  [id] bigint  NOT NULL,
  [uuid] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [access_token] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [nick_name] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [avatar] nvarchar(500) COLLATE Chinese_PRC_CI_AS  NULL,
  [blog] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [company] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [location] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [email] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [gender] nvarchar(50) COLLATE Chinese_PRC_CI_AS  NULL,
  [source] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [remark] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [create_time] datetime2(7)  NULL,
  [create_user] bigint  NULL,
  [update_time] datetime2(7)  NULL,
  [update_user] bigint  NULL
)
GO

ALTER TABLE [dbo].[sys_oauth_user] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
'MS_Description', N'主键',
'SCHEMA', N'dbo',
'TABLE', N'sys_oauth_user',
'COLUMN', N'id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'第三方平台的用户唯一id',
'SCHEMA', N'dbo',
'TABLE', N'sys_oauth_user',
'COLUMN', N'uuid'
GO

EXEC sp_addextendedproperty
'MS_Description', N'用户授权的token',
'SCHEMA', N'dbo',
'TABLE', N'sys_oauth_user',
'COLUMN', N'access_token'
GO

EXEC sp_addextendedproperty
'MS_Description', N'昵称',
'SCHEMA', N'dbo',
'TABLE', N'sys_oauth_user',
'COLUMN', N'nick_name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'头像',
'SCHEMA', N'dbo',
'TABLE', N'sys_oauth_user',
'COLUMN', N'avatar'
GO

EXEC sp_addextendedproperty
'MS_Description', N'用户网址',
'SCHEMA', N'dbo',
'TABLE', N'sys_oauth_user',
'COLUMN', N'blog'
GO

EXEC sp_addextendedproperty
'MS_Description', N'所在公司',
'SCHEMA', N'dbo',
'TABLE', N'sys_oauth_user',
'COLUMN', N'company'
GO

EXEC sp_addextendedproperty
'MS_Description', N'位置',
'SCHEMA', N'dbo',
'TABLE', N'sys_oauth_user',
'COLUMN', N'location'
GO

EXEC sp_addextendedproperty
'MS_Description', N'邮箱',
'SCHEMA', N'dbo',
'TABLE', N'sys_oauth_user',
'COLUMN', N'email'
GO

EXEC sp_addextendedproperty
'MS_Description', N'性别',
'SCHEMA', N'dbo',
'TABLE', N'sys_oauth_user',
'COLUMN', N'gender'
GO

EXEC sp_addextendedproperty
'MS_Description', N'用户来源',
'SCHEMA', N'dbo',
'TABLE', N'sys_oauth_user',
'COLUMN', N'source'
GO

EXEC sp_addextendedproperty
'MS_Description', N'用户备注（各平台中的用户个人介绍）',
'SCHEMA', N'dbo',
'TABLE', N'sys_oauth_user',
'COLUMN', N'remark'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建时间',
'SCHEMA', N'dbo',
'TABLE', N'sys_oauth_user',
'COLUMN', N'create_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建用户',
'SCHEMA', N'dbo',
'TABLE', N'sys_oauth_user',
'COLUMN', N'create_user'
GO

EXEC sp_addextendedproperty
'MS_Description', N'更新时间',
'SCHEMA', N'dbo',
'TABLE', N'sys_oauth_user',
'COLUMN', N'update_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'更新用户',
'SCHEMA', N'dbo',
'TABLE', N'sys_oauth_user',
'COLUMN', N'update_user'
GO

EXEC sp_addextendedproperty
'MS_Description', N'第三方认证用户信息表',
'SCHEMA', N'dbo',
'TABLE', N'sys_oauth_user'
GO


-- ----------------------------
-- Table structure for sys_op_log
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[sys_op_log]') AND type IN ('U'))
	DROP TABLE [dbo].[sys_op_log]
GO

CREATE TABLE [dbo].[sys_op_log] (
  [id] bigint  NOT NULL,
  [name] nvarchar(50) COLLATE Chinese_PRC_CI_AS  NULL,
  [op_type] tinyint  NULL,
  [success] nchar(1) COLLATE Chinese_PRC_CI_AS  NULL,
  [message] nvarchar(max) COLLATE Chinese_PRC_CI_AS  NULL,
  [ip] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [location] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [browser] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [os] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [url] nvarchar(500) COLLATE Chinese_PRC_CI_AS  NULL,
  [class_name] nvarchar(500) COLLATE Chinese_PRC_CI_AS  NULL,
  [method_name] nvarchar(500) COLLATE Chinese_PRC_CI_AS  NULL,
  [req_method] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [param] nvarchar(max) COLLATE Chinese_PRC_CI_AS  NULL,
  [result] nvarchar(max) COLLATE Chinese_PRC_CI_AS  NULL,
  [op_time] datetime2(7)  NULL,
  [account] nvarchar(50) COLLATE Chinese_PRC_CI_AS  NULL
)
GO

ALTER TABLE [dbo].[sys_op_log] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
'MS_Description', N'主键',
'SCHEMA', N'dbo',
'TABLE', N'sys_op_log',
'COLUMN', N'id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'名称',
'SCHEMA', N'dbo',
'TABLE', N'sys_op_log',
'COLUMN', N'name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'操作类型',
'SCHEMA', N'dbo',
'TABLE', N'sys_op_log',
'COLUMN', N'op_type'
GO

EXEC sp_addextendedproperty
'MS_Description', N'是否执行成功（Y-是，N-否）',
'SCHEMA', N'dbo',
'TABLE', N'sys_op_log',
'COLUMN', N'success'
GO

EXEC sp_addextendedproperty
'MS_Description', N'具体消息',
'SCHEMA', N'dbo',
'TABLE', N'sys_op_log',
'COLUMN', N'message'
GO

EXEC sp_addextendedproperty
'MS_Description', N'ip',
'SCHEMA', N'dbo',
'TABLE', N'sys_op_log',
'COLUMN', N'ip'
GO

EXEC sp_addextendedproperty
'MS_Description', N'地址',
'SCHEMA', N'dbo',
'TABLE', N'sys_op_log',
'COLUMN', N'location'
GO

EXEC sp_addextendedproperty
'MS_Description', N'浏览器',
'SCHEMA', N'dbo',
'TABLE', N'sys_op_log',
'COLUMN', N'browser'
GO

EXEC sp_addextendedproperty
'MS_Description', N'操作系统',
'SCHEMA', N'dbo',
'TABLE', N'sys_op_log',
'COLUMN', N'os'
GO

EXEC sp_addextendedproperty
'MS_Description', N'请求地址',
'SCHEMA', N'dbo',
'TABLE', N'sys_op_log',
'COLUMN', N'url'
GO

EXEC sp_addextendedproperty
'MS_Description', N'类名称',
'SCHEMA', N'dbo',
'TABLE', N'sys_op_log',
'COLUMN', N'class_name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'方法名称',
'SCHEMA', N'dbo',
'TABLE', N'sys_op_log',
'COLUMN', N'method_name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'请求方式（GET POST PUT DELETE)',
'SCHEMA', N'dbo',
'TABLE', N'sys_op_log',
'COLUMN', N'req_method'
GO

EXEC sp_addextendedproperty
'MS_Description', N'请求参数',
'SCHEMA', N'dbo',
'TABLE', N'sys_op_log',
'COLUMN', N'param'
GO

EXEC sp_addextendedproperty
'MS_Description', N'返回结果',
'SCHEMA', N'dbo',
'TABLE', N'sys_op_log',
'COLUMN', N'result'
GO

EXEC sp_addextendedproperty
'MS_Description', N'操作时间',
'SCHEMA', N'dbo',
'TABLE', N'sys_op_log',
'COLUMN', N'op_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'操作账号',
'SCHEMA', N'dbo',
'TABLE', N'sys_op_log',
'COLUMN', N'account'
GO

EXEC sp_addextendedproperty
'MS_Description', N'系统操作日志表',
'SCHEMA', N'dbo',
'TABLE', N'sys_op_log'
GO


-- ----------------------------
-- Table structure for sys_org
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[sys_org]') AND type IN ('U'))
	DROP TABLE [dbo].[sys_org]
GO

CREATE TABLE [dbo].[sys_org] (
  [id] bigint  NOT NULL,
  [pid] bigint  NOT NULL,
  [pids] nvarchar(max) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [name] nvarchar(100) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [code] nvarchar(50) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [sort] int  NOT NULL,
  [remark] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [status] tinyint  NOT NULL,
  [create_time] datetime2(7)  NULL,
  [create_user] bigint  NULL,
  [update_time] datetime2(7)  NULL,
  [update_user] bigint  NULL
)
GO

ALTER TABLE [dbo].[sys_org] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
'MS_Description', N'主键',
'SCHEMA', N'dbo',
'TABLE', N'sys_org',
'COLUMN', N'id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'父id',
'SCHEMA', N'dbo',
'TABLE', N'sys_org',
'COLUMN', N'pid'
GO

EXEC sp_addextendedproperty
'MS_Description', N'父ids',
'SCHEMA', N'dbo',
'TABLE', N'sys_org',
'COLUMN', N'pids'
GO

EXEC sp_addextendedproperty
'MS_Description', N'名称',
'SCHEMA', N'dbo',
'TABLE', N'sys_org',
'COLUMN', N'name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'编码',
'SCHEMA', N'dbo',
'TABLE', N'sys_org',
'COLUMN', N'code'
GO

EXEC sp_addextendedproperty
'MS_Description', N'排序',
'SCHEMA', N'dbo',
'TABLE', N'sys_org',
'COLUMN', N'sort'
GO

EXEC sp_addextendedproperty
'MS_Description', N'描述',
'SCHEMA', N'dbo',
'TABLE', N'sys_org',
'COLUMN', N'remark'
GO

EXEC sp_addextendedproperty
'MS_Description', N'状态（字典 0正常 1停用 2删除）',
'SCHEMA', N'dbo',
'TABLE', N'sys_org',
'COLUMN', N'status'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建时间',
'SCHEMA', N'dbo',
'TABLE', N'sys_org',
'COLUMN', N'create_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建人',
'SCHEMA', N'dbo',
'TABLE', N'sys_org',
'COLUMN', N'create_user'
GO

EXEC sp_addextendedproperty
'MS_Description', N'更新时间',
'SCHEMA', N'dbo',
'TABLE', N'sys_org',
'COLUMN', N'update_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'更新人',
'SCHEMA', N'dbo',
'TABLE', N'sys_org',
'COLUMN', N'update_user'
GO

EXEC sp_addextendedproperty
'MS_Description', N'系统组织机构表',
'SCHEMA', N'dbo',
'TABLE', N'sys_org'
GO


-- ----------------------------
-- Records of sys_org
-- ----------------------------
INSERT INTO [dbo].[sys_org]  VALUES (N'1265476890651701250', N'0', N'[0],', N'华夏集团', N'hxjt', N'100', N'华夏集团总公司', N'0', N'2020-03-26 16:50:53.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_org]  VALUES (N'1265476890672672769', N'1265476890651701250', N'[0],[1265476890651701250],', N'华夏集团北京分公司', N'hxjt_bj', N'100', N'华夏集团北京分公司', N'0', N'2020-03-26 16:55:42.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_org]  VALUES (N'1265476890672672770', N'1265476890651701250', N'[0],[1265476890651701250],', N'华夏集团成都分公司', N'hxjt_cd', N'100', N'华夏集团成都分公司', N'0', N'2020-03-26 16:56:02.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_org]  VALUES (N'1265476890672672771', N'1265476890672672769', N'[0],[1265476890651701250],[1265476890672672769],', N'研发部', N'hxjt_bj_yfb', N'100', N'华夏集团北京分公司研发部', N'0', N'2020-03-26 16:56:36.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_org]  VALUES (N'1265476890672672772', N'1265476890672672769', N'[0],[1265476890651701250],[1265476890672672769],', N'企划部', N'hxjt_bj_qhb', N'100', N'华夏集团北京分公司企划部', N'0', N'2020-03-26 16:57:06.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_org]  VALUES (N'1265476890672672773', N'1265476890672672770', N'[0],[1265476890651701250],[1265476890672672770],', N'市场部', N'hxjt_cd_scb', N'100', N'华夏集团成都分公司市场部', N'0', N'2020-03-26 16:57:35.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_org]  VALUES (N'1265476890672672774', N'1265476890672672770', N'[0],[1265476890651701250],[1265476890672672770],', N'财务部', N'hxjt_cd_cwb', N'100', N'华夏集团成都分公司财务部', N'0', N'2020-03-26 16:58:01.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_org]  VALUES (N'1265476890672672775', N'1265476890672672773', N'[0],[1265476890651701250],[1265476890672672770],[1265476890672672773],', N'市场部二部', N'hxjt_cd_scb_2b', N'100', N'华夏集团成都分公司市场部二部', N'0', N'2020-04-06 15:36:50.0000000', N'1265476890672672808', NULL, NULL)
GO


-- ----------------------------
-- Table structure for sys_pos
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[sys_pos]') AND type IN ('U'))
	DROP TABLE [dbo].[sys_pos]
GO

CREATE TABLE [dbo].[sys_pos] (
  [id] bigint  NOT NULL,
  [name] nvarchar(100) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [code] nvarchar(50) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [sort] int  NOT NULL,
  [remark] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [status] tinyint  NOT NULL,
  [create_time] datetime2(7)  NULL,
  [create_user] bigint  NULL,
  [update_time] datetime2(7)  NULL,
  [update_user] bigint  NULL
)
GO

ALTER TABLE [dbo].[sys_pos] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
'MS_Description', N'主键',
'SCHEMA', N'dbo',
'TABLE', N'sys_pos',
'COLUMN', N'id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'名称',
'SCHEMA', N'dbo',
'TABLE', N'sys_pos',
'COLUMN', N'name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'编码',
'SCHEMA', N'dbo',
'TABLE', N'sys_pos',
'COLUMN', N'code'
GO

EXEC sp_addextendedproperty
'MS_Description', N'排序',
'SCHEMA', N'dbo',
'TABLE', N'sys_pos',
'COLUMN', N'sort'
GO

EXEC sp_addextendedproperty
'MS_Description', N'备注',
'SCHEMA', N'dbo',
'TABLE', N'sys_pos',
'COLUMN', N'remark'
GO

EXEC sp_addextendedproperty
'MS_Description', N'状态（字典 0正常 1停用 2删除）',
'SCHEMA', N'dbo',
'TABLE', N'sys_pos',
'COLUMN', N'status'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建时间',
'SCHEMA', N'dbo',
'TABLE', N'sys_pos',
'COLUMN', N'create_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建人',
'SCHEMA', N'dbo',
'TABLE', N'sys_pos',
'COLUMN', N'create_user'
GO

EXEC sp_addextendedproperty
'MS_Description', N'更新时间',
'SCHEMA', N'dbo',
'TABLE', N'sys_pos',
'COLUMN', N'update_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'更新人',
'SCHEMA', N'dbo',
'TABLE', N'sys_pos',
'COLUMN', N'update_user'
GO

EXEC sp_addextendedproperty
'MS_Description', N'系统职位表',
'SCHEMA', N'dbo',
'TABLE', N'sys_pos'
GO


-- ----------------------------
-- Records of sys_pos
-- ----------------------------
INSERT INTO [dbo].[sys_pos]  VALUES (N'1265476890672672787', N'总经理', N'zjl', N'100', N'总经理职位', N'0', N'2020-03-26 19:28:54.0000000', N'1265476890672672808', N'2020-06-02 21:01:04.0000000', N'1265476890672672808')
GO

INSERT INTO [dbo].[sys_pos]  VALUES (N'1265476890672672788', N'副总经理', N'fzjl', N'100', N'副总经理职位', N'0', N'2020-03-26 19:29:57.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_pos]  VALUES (N'1265476890672672789', N'部门经理', N'bmjl', N'100', N'部门经理职位', N'0', N'2020-03-26 19:31:49.0000000', N'1265476890672672808', NULL, NULL)
GO

INSERT INTO [dbo].[sys_pos]  VALUES (N'1265476890672672790', N'工作人员', N'gzry', N'100', N'工作人员职位', N'0', N'2020-05-27 11:32:00.0000000', N'1265476890672672808', N'2020-06-01 10:51:35.0000000', N'1265476890672672808')
GO


-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[sys_role]') AND type IN ('U'))
	DROP TABLE [dbo].[sys_role]
GO

CREATE TABLE [dbo].[sys_role] (
  [id] bigint  NOT NULL,
  [name] nvarchar(100) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [code] nvarchar(50) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [sort] int  NOT NULL,
  [data_scope_type] tinyint  NOT NULL,
  [remark] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [status] tinyint  NOT NULL,
  [create_time] datetime2(7)  NULL,
  [create_user] bigint  NULL,
  [update_time] datetime2(7)  NULL,
  [update_user] bigint  NULL
)
GO

ALTER TABLE [dbo].[sys_role] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
'MS_Description', N'主键id',
'SCHEMA', N'dbo',
'TABLE', N'sys_role',
'COLUMN', N'id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'名称',
'SCHEMA', N'dbo',
'TABLE', N'sys_role',
'COLUMN', N'name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'编码',
'SCHEMA', N'dbo',
'TABLE', N'sys_role',
'COLUMN', N'code'
GO

EXEC sp_addextendedproperty
'MS_Description', N'序号',
'SCHEMA', N'dbo',
'TABLE', N'sys_role',
'COLUMN', N'sort'
GO

EXEC sp_addextendedproperty
'MS_Description', N'数据范围类型（字典 1全部数据 2本部门及以下数据 3本部门数据 4仅本人数据 5自定义数据）',
'SCHEMA', N'dbo',
'TABLE', N'sys_role',
'COLUMN', N'data_scope_type'
GO

EXEC sp_addextendedproperty
'MS_Description', N'备注',
'SCHEMA', N'dbo',
'TABLE', N'sys_role',
'COLUMN', N'remark'
GO

EXEC sp_addextendedproperty
'MS_Description', N'状态（字典 0正常 1停用 2删除）',
'SCHEMA', N'dbo',
'TABLE', N'sys_role',
'COLUMN', N'status'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建时间',
'SCHEMA', N'dbo',
'TABLE', N'sys_role',
'COLUMN', N'create_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建人',
'SCHEMA', N'dbo',
'TABLE', N'sys_role',
'COLUMN', N'create_user'
GO

EXEC sp_addextendedproperty
'MS_Description', N'更新时间',
'SCHEMA', N'dbo',
'TABLE', N'sys_role',
'COLUMN', N'update_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'更新人',
'SCHEMA', N'dbo',
'TABLE', N'sys_role',
'COLUMN', N'update_user'
GO

EXEC sp_addextendedproperty
'MS_Description', N'系统角色表',
'SCHEMA', N'dbo',
'TABLE', N'sys_role'
GO


-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO [dbo].[sys_role]  VALUES (N'1265476890672672817', N'组织架构管理员', N'ent_manager_role', N'100', N'1', N'组织架构管理员', N'0', N'2020-04-02 19:27:26.0000000', N'1265476890672672808', N'2020-09-12 15:54:07.0000000', N'1265476890672672808')
GO

INSERT INTO [dbo].[sys_role]  VALUES (N'1265476890672672818', N'权限管理员', N'auth_role', N'101', N'5', N'权限管理员', N'0', N'2020-04-02 19:28:40.0000000', N'1265476890672672808', N'2020-07-16 10:52:21.0000000', N'1265476890672672808')
GO


-- ----------------------------
-- Table structure for sys_role_data_scope
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[sys_role_data_scope]') AND type IN ('U'))
	DROP TABLE [dbo].[sys_role_data_scope]
GO

CREATE TABLE [dbo].[sys_role_data_scope] (
  [id] bigint  NOT NULL,
  [role_id] bigint  NOT NULL,
  [org_id] bigint  NOT NULL
)
GO

ALTER TABLE [dbo].[sys_role_data_scope] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
'MS_Description', N'主键',
'SCHEMA', N'dbo',
'TABLE', N'sys_role_data_scope',
'COLUMN', N'id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'角色id',
'SCHEMA', N'dbo',
'TABLE', N'sys_role_data_scope',
'COLUMN', N'role_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'机构id',
'SCHEMA', N'dbo',
'TABLE', N'sys_role_data_scope',
'COLUMN', N'org_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'系统角色数据范围表',
'SCHEMA', N'dbo',
'TABLE', N'sys_role_data_scope'
GO


-- ----------------------------
-- Records of sys_role_data_scope
-- ----------------------------
INSERT INTO [dbo].[sys_role_data_scope]  VALUES (N'1277435908822102018', N'1265476890672672818', N'1265476890651701250')
GO

INSERT INTO [dbo].[sys_role_data_scope]  VALUES (N'1277435909635796993', N'1265476890672672818', N'1265476890672672769')
GO

INSERT INTO [dbo].[sys_role_data_scope]  VALUES (N'1277435910432714754', N'1265476890672672818', N'1265476890672672771')
GO

INSERT INTO [dbo].[sys_role_data_scope]  VALUES (N'1277435911233826818', N'1265476890672672818', N'1265476890672672772')
GO

INSERT INTO [dbo].[sys_role_data_scope]  VALUES (N'1277435912018161666', N'1265476890672672818', N'1265476890672672770')
GO

INSERT INTO [dbo].[sys_role_data_scope]  VALUES (N'1277435912810885122', N'1265476890672672818', N'1265476890672672773')
GO

INSERT INTO [dbo].[sys_role_data_scope]  VALUES (N'1277435913595219970', N'1265476890672672818', N'1265476890672672775')
GO

INSERT INTO [dbo].[sys_role_data_scope]  VALUES (N'1277435914392137730', N'1265476890672672818', N'1265476890672672774')
GO

INSERT INTO [dbo].[sys_role_data_scope]  VALUES (N'1292060127645429762', N'1265476890672672819', N'1265476890672672774')
GO


-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[sys_role_menu]') AND type IN ('U'))
	DROP TABLE [dbo].[sys_role_menu]
GO

CREATE TABLE [dbo].[sys_role_menu] (
  [id] bigint  NOT NULL,
  [role_id] bigint  NOT NULL,
  [menu_id] bigint  NOT NULL
)
GO

ALTER TABLE [dbo].[sys_role_menu] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
'MS_Description', N'主键',
'SCHEMA', N'dbo',
'TABLE', N'sys_role_menu',
'COLUMN', N'id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'角色id',
'SCHEMA', N'dbo',
'TABLE', N'sys_role_menu',
'COLUMN', N'role_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'菜单id',
'SCHEMA', N'dbo',
'TABLE', N'sys_role_menu',
'COLUMN', N'menu_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'系统角色菜单表',
'SCHEMA', N'dbo',
'TABLE', N'sys_role_menu'
GO


-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366872187256834', N'1265476890672672818', N'1264622039642255671')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366872602492929', N'1265476890672672818', N'1264622039642255681')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366873026117634', N'1265476890672672818', N'1264622039642255761')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366873449742337', N'1265476890672672818', N'1264622039642255851')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366873864978433', N'1265476890672672818', N'1264622039642255691')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366874284408834', N'1265476890672672818', N'1264622039642255701')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366874703839233', N'1265476890672672818', N'1264622039642255711')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366875119075330', N'1265476890672672818', N'1264622039642255721')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366875538505730', N'1265476890672672818', N'1264622039642255731')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366875962130433', N'1265476890672672818', N'1264622039642255741')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366876377366529', N'1265476890672672818', N'1264622039642255751')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366876800991233', N'1265476890672672818', N'1264622039642255771')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366877216227330', N'1265476890672672818', N'1264622039642255781')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366877639852033', N'1265476890672672818', N'1264622039642255791')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366878067671041', N'1265476890672672818', N'1264622039642255801')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366878487101441', N'1265476890672672818', N'1264622039642255811')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366878898143233', N'1265476890672672818', N'1264622039642255821')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366879325962242', N'1265476890672672818', N'1264622039642255831')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366879745392641', N'1265476890672672818', N'1264622039642255841')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366880160628738', N'1265476890672672818', N'1264622039642255881')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366880580059138', N'1265476890672672818', N'1264622039642255891')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366880999489537', N'1265476890672672818', N'1264622039642255901')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366881423114242', N'1265476890672672818', N'1264622039642255911')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366881838350338', N'1265476890672672818', N'1264622039642255921')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366882261975042', N'1265476890672672818', N'1264622039642255931')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366882685599745', N'1265476890672672818', N'1264622039642255941')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366883100835842', N'1265476890672672818', N'1264622039642255951')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366883520266242', N'1265476890672672818', N'1264622039642255861')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366883939696642', N'1265476890672672818', N'1264622039642255871')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366884363321346', N'1265476890672672818', N'1264622039642257021')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366884782751746', N'1265476890672672818', N'1264622039642257031')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366885197987842', N'1265476890672672818', N'1264622039642256831')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366885617418242', N'1265476890672672818', N'1264622039642257261')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366886045237250', N'1265476890672672818', N'1264622039642257271')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366886473056258', N'1265476890672672818', N'1264622039642257301')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366886884098050', N'1265476890672672818', N'1264622039642257321')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366887307722754', N'1265476890672672818', N'1264622039642257331')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366887722958850', N'1265476890672672818', N'1264622039642257471')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366888142389250', N'1265476890672672818', N'1264622039642257481')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366888566013954', N'1265476890672672818', N'1264622039642257341')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366888981250049', N'1265476890672672818', N'1264622039642257411')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366889404874754', N'1265476890672672818', N'1264622039642257421')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366889820110850', N'1265476890672672818', N'1264622039642257431')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366890235346946', N'1265476890672672818', N'1264622039642257441')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366890663165954', N'1265476890672672818', N'1264622039642257451')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366891082596354', N'1265476890672672818', N'1264622039642257461')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366891506221057', N'1265476890672672818', N'1264622039642257351')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366891925651458', N'1265476890672672818', N'1264622039642257361')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366892345081858', N'1265476890672672818', N'1264622039642257371')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366892764512258', N'1265476890672672818', N'1264622039642257381')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366893183942658', N'1265476890672672818', N'1264622039642257391')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366893607567361', N'1265476890672672818', N'1264622039642257401')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366894031192065', N'1265476890672672818', N'1264622039642257491')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366894446428162', N'1265476890672672818', N'1264622039642257501')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366894865858562', N'1265476890672672818', N'1264622039642257511')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366895285288961', N'1265476890672672818', N'1264622039642255591')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366895708913665', N'1265476890672672818', N'1264622039642257061')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366896132538369', N'1265476890672672818', N'1264622039642257462')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366896556163074', N'1265476890672672818', N'1264622039642256912')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366896979787777', N'1265476890672672818', N'1264622039642255421')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366897399218178', N'1265476890672672818', N'1264622039642257022')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366897827037185', N'1265476890672672818', N'1264622039642256821')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366898242273282', N'1265476890672672818', N'1264622039642257311')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366898670092290', N'1265476890672672818', N'1264622039642257312')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366899089522690', N'1265476890672672818', N'1264622039642257313')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366899508953089', N'1265476890672672818', N'1264622039642257314')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366899932577793', N'1265476890672672818', N'1264622039642257522')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366900352008193', N'1265476890672672818', N'1264622039642257523')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366900771438594', N'1265476890672672818', N'1264622039642257524')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366901190868994', N'1265476890672672818', N'1264622039642257525')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366901610299394', N'1265476890672672818', N'1264622039642257531')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1304366902033924097', N'1265476890672672818', N'1264622039642257532')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864929906434049', N'1265476890672672817', N'1264622039642255311')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864930338447362', N'1265476890672672817', N'1264622039642255331')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864930753683457', N'1265476890672672817', N'1264622039642255321')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864931181502465', N'1265476890672672817', N'1264622039642255341')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864931596738561', N'1265476890672672817', N'1264622039642255351')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864932020363266', N'1265476890672672817', N'1264622039642255361')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864932439793666', N'1265476890672672817', N'1264622039642255371')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864932863418369', N'1265476890672672817', N'1264622039642255381')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864933287043073', N'1265476890672672817', N'1264622039642255391')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864933706473474', N'1265476890672672817', N'1264622039642255401')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864934130098177', N'1265476890672672817', N'1264622039642255411')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864934553722881', N'1265476890672672817', N'1264622039642255421')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864934973153281', N'1265476890672672817', N'1264622039642255431')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864935392583681', N'1265476890672672817', N'1264622039642255441')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864935820402689', N'1265476890672672817', N'1264622039642255451')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864936239833090', N'1265476890672672817', N'1264622039642255461')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864936663457793', N'1265476890672672817', N'1264622039642255471')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864937087082498', N'1265476890672672817', N'1264622039642255481')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864937506512898', N'1265476890672672817', N'1264622039642255491')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864937938526210', N'1265476890672672817', N'1264622039642255501')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864938357956610', N'1265476890672672817', N'1264622039642255511')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864938777387010', N'1265476890672672817', N'1264622039642255521')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864939201011713', N'1265476890672672817', N'1264622039642255531')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864939624636418', N'1265476890672672817', N'1264622039642255541')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864940044066817', N'1265476890672672817', N'1264622039642255551')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864940467691522', N'1265476890672672817', N'1264622039642255561')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864940933259265', N'1265476890672672817', N'1264622039642255571')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864941356883970', N'1265476890672672817', N'1264622039642255581')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864941776314369', N'1265476890672672817', N'1264622039642255591')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864942195744769', N'1265476890672672817', N'1264622039642255601')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864942619369473', N'1265476890672672817', N'1264622039642255621')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864943042994178', N'1265476890672672817', N'1264622039642255631')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864943462424577', N'1265476890672672817', N'1264622039642255641')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864943886049282', N'1265476890672672817', N'1264622039642255651')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864944309673986', N'1265476890672672817', N'1264622039642255661')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864944733298690', N'1265476890672672817', N'1264622039642255611')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864945156923393', N'1265476890672672817', N'1264622039642257321')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864945576353793', N'1265476890672672817', N'1264622039642257331')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864945999978497', N'1265476890672672817', N'1264622039642257471')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864946423603201', N'1265476890672672817', N'1264622039642257481')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864946847227905', N'1265476890672672817', N'1264622039642257341')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864947266658305', N'1265476890672672817', N'1264622039642257411')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864947681894402', N'1265476890672672817', N'1264622039642257421')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864948109713409', N'1265476890672672817', N'1264622039642257431')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864948529143810', N'1265476890672672817', N'1264622039642257441')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864948952768513', N'1265476890672672817', N'1264622039642257451')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864949380587522', N'1265476890672672817', N'1264622039642257461')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864949804212225', N'1265476890672672817', N'1264622039642257351')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864950223642626', N'1265476890672672817', N'1264622039642257361')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864950638878721', N'1265476890672672817', N'1264622039642257371')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864951066697729', N'1265476890672672817', N'1264622039642257381')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864951490322433', N'1265476890672672817', N'1264622039642257391')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864951913947137', N'1265476890672672817', N'1264622039642257401')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864952329183233', N'1265476890672672817', N'1264622039642257491')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864952757002241', N'1265476890672672817', N'1264622039642257501')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864953176432642', N'1265476890672672817', N'1264622039642257511')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864953600057346', N'1265476890672672817', N'1264622039642256831')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864954019487746', N'1265476890672672817', N'1264622039642257031')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864954447306754', N'1265476890672672817', N'1264622039642257021')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864954870931458', N'1265476890672672817', N'1264622039642257061')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864955290361857', N'1265476890672672817', N'1264622039642257261')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864955709792258', N'1265476890672672817', N'1264622039642257301')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864956133416962', N'1265476890672672817', N'1264622039642257271')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864956557041665', N'1265476890672672817', N'1264622039642257462')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864956976472066', N'1265476890672672817', N'1264622039642256912')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864957400096770', N'1265476890672672817', N'1264622039642255911')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864957861470210', N'1265476890672672817', N'1264622039642257522')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864958280900610', N'1265476890672672817', N'1264622039642257523')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864958704525314', N'1265476890672672817', N'1264622039642257524')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864959132344321', N'1265476890672672817', N'1264622039642257525')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864959555969026', N'1265476890672672817', N'1264622039642257532')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864959975399425', N'1265476890672672817', N'1264622039642257531')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864960399024129', N'1265476890672672817', N'1264622039642257311')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864960822648833', N'1265476890672672817', N'1264622039642257312')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864961242079233', N'1265476890672672817', N'1264622039642257313')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864961657315330', N'1265476890672672817', N'1264622039642257314')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864962085134337', N'1265476890672672817', N'1264622039642256821')
GO

INSERT INTO [dbo].[sys_role_menu]  VALUES (N'1307864962504564737', N'1265476890672672817', N'1264622039642257022')
GO


-- ----------------------------
-- Table structure for sys_sms
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[sys_sms]') AND type IN ('U'))
	DROP TABLE [dbo].[sys_sms]
GO

CREATE TABLE [dbo].[sys_sms] (
  [id] bigint  NOT NULL,
  [phone_numbers] nvarchar(200) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [validate_code] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [template_code] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [biz_id] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [status] tinyint  NOT NULL,
  [source] tinyint  NOT NULL,
  [invalid_time] datetime2(7)  NULL,
  [create_time] datetime2(7)  NULL,
  [create_user] bigint  NULL,
  [update_time] datetime2(7)  NULL,
  [update_user] bigint  NULL
)
GO

ALTER TABLE [dbo].[sys_sms] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
'MS_Description', N'主键',
'SCHEMA', N'dbo',
'TABLE', N'sys_sms',
'COLUMN', N'id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'手机号',
'SCHEMA', N'dbo',
'TABLE', N'sys_sms',
'COLUMN', N'phone_numbers'
GO

EXEC sp_addextendedproperty
'MS_Description', N'短信验证码',
'SCHEMA', N'dbo',
'TABLE', N'sys_sms',
'COLUMN', N'validate_code'
GO

EXEC sp_addextendedproperty
'MS_Description', N'短信模板ID',
'SCHEMA', N'dbo',
'TABLE', N'sys_sms',
'COLUMN', N'template_code'
GO

EXEC sp_addextendedproperty
'MS_Description', N'回执id，可根据该id查询具体的发送状态',
'SCHEMA', N'dbo',
'TABLE', N'sys_sms',
'COLUMN', N'biz_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'发送状态（字典 0 未发送，1 发送成功，2 发送失败，3 失效）',
'SCHEMA', N'dbo',
'TABLE', N'sys_sms',
'COLUMN', N'status'
GO

EXEC sp_addextendedproperty
'MS_Description', N'来源（字典 1 app， 2 pc， 3 其他）',
'SCHEMA', N'dbo',
'TABLE', N'sys_sms',
'COLUMN', N'source'
GO

EXEC sp_addextendedproperty
'MS_Description', N'失效时间',
'SCHEMA', N'dbo',
'TABLE', N'sys_sms',
'COLUMN', N'invalid_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建时间',
'SCHEMA', N'dbo',
'TABLE', N'sys_sms',
'COLUMN', N'create_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建人',
'SCHEMA', N'dbo',
'TABLE', N'sys_sms',
'COLUMN', N'create_user'
GO

EXEC sp_addextendedproperty
'MS_Description', N'更新时间',
'SCHEMA', N'dbo',
'TABLE', N'sys_sms',
'COLUMN', N'update_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'更新人',
'SCHEMA', N'dbo',
'TABLE', N'sys_sms',
'COLUMN', N'update_user'
GO

EXEC sp_addextendedproperty
'MS_Description', N'短信信息发送表',
'SCHEMA', N'dbo',
'TABLE', N'sys_sms'
GO


-- ----------------------------
-- Table structure for sys_timers
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[sys_timers]') AND type IN ('U'))
	DROP TABLE [dbo].[sys_timers]
GO

CREATE TABLE [dbo].[sys_timers] (
  [id] bigint  NOT NULL,
  [timer_name] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [action_class] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [cron] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [job_status] tinyint  NULL,
  [remark] nvarchar(1000) COLLATE Chinese_PRC_CI_AS  NULL,
  [create_time] datetime2(7)  NULL,
  [create_user] bigint  NULL,
  [update_time] datetime2(7)  NULL,
  [update_user] bigint  NULL
)
GO

ALTER TABLE [dbo].[sys_timers] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
'MS_Description', N'定时器id',
'SCHEMA', N'dbo',
'TABLE', N'sys_timers',
'COLUMN', N'id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'任务名称',
'SCHEMA', N'dbo',
'TABLE', N'sys_timers',
'COLUMN', N'timer_name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'执行任务的class的类名（实现了TimerTaskRunner接口的类的全称）',
'SCHEMA', N'dbo',
'TABLE', N'sys_timers',
'COLUMN', N'action_class'
GO

EXEC sp_addextendedproperty
'MS_Description', N'定时任务表达式',
'SCHEMA', N'dbo',
'TABLE', N'sys_timers',
'COLUMN', N'cron'
GO

EXEC sp_addextendedproperty
'MS_Description', N'状态（字典 1运行  2停止）',
'SCHEMA', N'dbo',
'TABLE', N'sys_timers',
'COLUMN', N'job_status'
GO

EXEC sp_addextendedproperty
'MS_Description', N'备注信息',
'SCHEMA', N'dbo',
'TABLE', N'sys_timers',
'COLUMN', N'remark'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建时间',
'SCHEMA', N'dbo',
'TABLE', N'sys_timers',
'COLUMN', N'create_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建人',
'SCHEMA', N'dbo',
'TABLE', N'sys_timers',
'COLUMN', N'create_user'
GO

EXEC sp_addextendedproperty
'MS_Description', N'更新时间',
'SCHEMA', N'dbo',
'TABLE', N'sys_timers',
'COLUMN', N'update_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'更新人',
'SCHEMA', N'dbo',
'TABLE', N'sys_timers',
'COLUMN', N'update_user'
GO

EXEC sp_addextendedproperty
'MS_Description', N'定时任务',
'SCHEMA', N'dbo',
'TABLE', N'sys_timers'
GO


-- ----------------------------
-- Records of sys_timers
-- ----------------------------
INSERT INTO [dbo].[sys_timers]  VALUES (N'1288760324837851137', N'定时同步缓存常量', N'vip.xiaonuo.sys.modular.timer.tasks.RefreshConstantsTaskRunner', N'0 0/1 * * * ?', N'1', N'定时同步sys_config表的数据到缓存常量中', N'2020-07-30 16:56:20.0000000', N'1265476890672672808', N'2020-07-30 16:58:52.0000000', N'1265476890672672808')
GO

INSERT INTO [dbo].[sys_timers]  VALUES (N'1304971718170832898', N'定时打印一句话', N'vip.xiaonuo.sys.modular.timer.tasks.SystemOutTaskRunner', N'0 0 * * * ? *', N'2', N'定时打印一句话', N'2020-09-13 10:34:37.0000000', N'1265476890672672808', N'2020-09-23 20:37:48.0000000', N'1265476890672672808')
GO


-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[sys_user]') AND type IN ('U'))
	DROP TABLE [dbo].[sys_user]
GO

CREATE TABLE [dbo].[sys_user] (
  [id] bigint  NOT NULL,
  [account] nvarchar(50) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [password] nvarchar(100) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [nick_name] nvarchar(50) COLLATE Chinese_PRC_CI_AS  NULL,
  [name] nvarchar(100) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [avatar] bigint  NULL,
  [birthday] date  NULL,
  [sex] tinyint  NOT NULL,
  [email] nvarchar(50) COLLATE Chinese_PRC_CI_AS  NULL,
  [phone] nvarchar(50) COLLATE Chinese_PRC_CI_AS  NULL,
  [tel] nvarchar(50) COLLATE Chinese_PRC_CI_AS  NULL,
  [last_login_ip] nvarchar(100) COLLATE Chinese_PRC_CI_AS  NULL,
  [last_login_time] datetime2(7)  NULL,
  [admin_type] tinyint  NOT NULL,
  [status] tinyint  NOT NULL,
  [create_time] datetime2(7)  NULL,
  [create_user] bigint  NULL,
  [update_time] datetime2(7)  NULL,
  [update_user] bigint  NULL
)
GO

ALTER TABLE [dbo].[sys_user] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
'MS_Description', N'主键',
'SCHEMA', N'dbo',
'TABLE', N'sys_user',
'COLUMN', N'id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'账号',
'SCHEMA', N'dbo',
'TABLE', N'sys_user',
'COLUMN', N'account'
GO

EXEC sp_addextendedproperty
'MS_Description', N'密码',
'SCHEMA', N'dbo',
'TABLE', N'sys_user',
'COLUMN', N'password'
GO

EXEC sp_addextendedproperty
'MS_Description', N'昵称',
'SCHEMA', N'dbo',
'TABLE', N'sys_user',
'COLUMN', N'nick_name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'姓名',
'SCHEMA', N'dbo',
'TABLE', N'sys_user',
'COLUMN', N'name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'头像',
'SCHEMA', N'dbo',
'TABLE', N'sys_user',
'COLUMN', N'avatar'
GO

EXEC sp_addextendedproperty
'MS_Description', N'生日',
'SCHEMA', N'dbo',
'TABLE', N'sys_user',
'COLUMN', N'birthday'
GO

EXEC sp_addextendedproperty
'MS_Description', N'性别(字典 1男 2女 3未知)',
'SCHEMA', N'dbo',
'TABLE', N'sys_user',
'COLUMN', N'sex'
GO

EXEC sp_addextendedproperty
'MS_Description', N'邮箱',
'SCHEMA', N'dbo',
'TABLE', N'sys_user',
'COLUMN', N'email'
GO

EXEC sp_addextendedproperty
'MS_Description', N'手机',
'SCHEMA', N'dbo',
'TABLE', N'sys_user',
'COLUMN', N'phone'
GO

EXEC sp_addextendedproperty
'MS_Description', N'电话',
'SCHEMA', N'dbo',
'TABLE', N'sys_user',
'COLUMN', N'tel'
GO

EXEC sp_addextendedproperty
'MS_Description', N'最后登陆IP',
'SCHEMA', N'dbo',
'TABLE', N'sys_user',
'COLUMN', N'last_login_ip'
GO

EXEC sp_addextendedproperty
'MS_Description', N'最后登陆时间',
'SCHEMA', N'dbo',
'TABLE', N'sys_user',
'COLUMN', N'last_login_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'管理员类型（0超级管理员 1非管理员）',
'SCHEMA', N'dbo',
'TABLE', N'sys_user',
'COLUMN', N'admin_type'
GO

EXEC sp_addextendedproperty
'MS_Description', N'状态（字典 0正常 1冻结 2删除）',
'SCHEMA', N'dbo',
'TABLE', N'sys_user',
'COLUMN', N'status'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建时间',
'SCHEMA', N'dbo',
'TABLE', N'sys_user',
'COLUMN', N'create_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建人',
'SCHEMA', N'dbo',
'TABLE', N'sys_user',
'COLUMN', N'create_user'
GO

EXEC sp_addextendedproperty
'MS_Description', N'更新时间',
'SCHEMA', N'dbo',
'TABLE', N'sys_user',
'COLUMN', N'update_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'更新人',
'SCHEMA', N'dbo',
'TABLE', N'sys_user',
'COLUMN', N'update_user'
GO

EXEC sp_addextendedproperty
'MS_Description', N'系统用户表',
'SCHEMA', N'dbo',
'TABLE', N'sys_user'
GO


-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO [dbo].[sys_user]  VALUES (N'1265476890672672808', N'superAdmin', N'$2a$09$PiCiFNspSlTBE9CakVs8ZOqx0xa03X9wOm01gMasHch4929TpEWCC', N'超级管理员', N'超级管理员', NULL, N'2020-03-18', N'1', N'superAdmin@qq.com', N'15228937093', N'1234567890', N'127.0.0.1', N'2020-12-25 20:24:27.0000000', N'1', N'0', N'2020-05-29 16:39:28.0000000', N'-1', N'2020-12-25 20:24:27.0000000', N'-1')
GO

INSERT INTO [dbo].[sys_user]  VALUES (N'1275735541155614721', N'yubaoshan', N'$2a$09$PiCiFNspSlTBE9CakVs8ZOqx0xa03X9wOm01gMasHch4929TpEWCC', N'俞宝山', N'俞宝山', NULL, N'1992-10-03', N'1', N'await183@qq.com', N'18200001102', N'', N'127.0.0.1', N'2020-09-23 10:15:10.0000000', N'2', N'0', N'2020-06-24 18:20:30.0000000', N'1265476890672672808', N'2020-09-23 10:15:10.0000000', N'-1')
GO

INSERT INTO [dbo].[sys_user]  VALUES (N'1280709549107552257', N'xuyuxiang', N'$2a$09$PiCiFNspSlTBE9CakVs8ZOqx0xa03X9wOm01gMasHch4929TpEWCC', N'就是那个锅', N'徐玉祥', NULL, N'2020-07-01', N'1', NULL, N'18200001100', NULL, N'127.0.0.1', N'2020-09-23 10:16:54.0000000', N'2', N'0', N'2020-07-08 11:45:26.0000000', N'1265476890672672808', N'2020-09-23 10:16:54.0000000', N'-1')
GO


-- ----------------------------
-- Table structure for sys_user_data_scope
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[sys_user_data_scope]') AND type IN ('U'))
	DROP TABLE [dbo].[sys_user_data_scope]
GO

CREATE TABLE [dbo].[sys_user_data_scope] (
  [id] bigint  NOT NULL,
  [user_id] bigint  NOT NULL,
  [org_id] bigint  NOT NULL
)
GO

ALTER TABLE [dbo].[sys_user_data_scope] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
'MS_Description', N'主键',
'SCHEMA', N'dbo',
'TABLE', N'sys_user_data_scope',
'COLUMN', N'id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'用户id',
'SCHEMA', N'dbo',
'TABLE', N'sys_user_data_scope',
'COLUMN', N'user_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'机构id',
'SCHEMA', N'dbo',
'TABLE', N'sys_user_data_scope',
'COLUMN', N'org_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'系统用户数据范围表',
'SCHEMA', N'dbo',
'TABLE', N'sys_user_data_scope'
GO


-- ----------------------------
-- Records of sys_user_data_scope
-- ----------------------------
INSERT INTO [dbo].[sys_user_data_scope]  VALUES (N'1277459951742840834', N'1266277099455635457', N'1265476890672672770')
GO

INSERT INTO [dbo].[sys_user_data_scope]  VALUES (N'1277459952577507330', N'1266277099455635457', N'1265476890672672773')
GO

INSERT INTO [dbo].[sys_user_data_scope]  VALUES (N'1277459953424756737', N'1266277099455635457', N'1265476890672672775')
GO

INSERT INTO [dbo].[sys_user_data_scope]  VALUES (N'1277459954267811841', N'1266277099455635457', N'1265476890672672774')
GO

INSERT INTO [dbo].[sys_user_data_scope]  VALUES (N'1280712071570366466', N'1275735541155614721', N'1265476890672672769')
GO

INSERT INTO [dbo].[sys_user_data_scope]  VALUES (N'1280712071570366467', N'1275735541155614721', N'1265476890672672771')
GO

INSERT INTO [dbo].[sys_user_data_scope]  VALUES (N'1280712071578755074', N'1275735541155614721', N'1265476890672672772')
GO


-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[sys_user_role]') AND type IN ('U'))
	DROP TABLE [dbo].[sys_user_role]
GO

CREATE TABLE [dbo].[sys_user_role] (
  [id] bigint  NOT NULL,
  [user_id] bigint  NOT NULL,
  [role_id] bigint  NOT NULL
)
GO

ALTER TABLE [dbo].[sys_user_role] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
'MS_Description', N'主键',
'SCHEMA', N'dbo',
'TABLE', N'sys_user_role',
'COLUMN', N'id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'用户id',
'SCHEMA', N'dbo',
'TABLE', N'sys_user_role',
'COLUMN', N'user_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'角色id',
'SCHEMA', N'dbo',
'TABLE', N'sys_user_role',
'COLUMN', N'role_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'系统用户角色表',
'SCHEMA', N'dbo',
'TABLE', N'sys_user_role'
GO


-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO [dbo].[sys_user_role]  VALUES (N'1283596900713574402', N'1275735541155614721', N'1265476890672672817')
GO

INSERT INTO [dbo].[sys_user_role]  VALUES (N'1283596949627547649', N'1280709549107552257', N'1265476890672672818')
GO


-- ----------------------------
-- Table structure for sys_vis_log
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[sys_vis_log]') AND type IN ('U'))
	DROP TABLE [dbo].[sys_vis_log]
GO

CREATE TABLE [dbo].[sys_vis_log] (
  [id] bigint  NOT NULL,
  [name] nvarchar(50) COLLATE Chinese_PRC_CI_AS  NULL,
  [success] nchar(1) COLLATE Chinese_PRC_CI_AS  NULL,
  [message] nvarchar(max) COLLATE Chinese_PRC_CI_AS  NULL,
  [ip] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [location] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [browser] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [os] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [vis_type] tinyint  NOT NULL,
  [vis_time] datetime2(7)  NULL,
  [account] nvarchar(50) COLLATE Chinese_PRC_CI_AS  NULL
)
GO

ALTER TABLE [dbo].[sys_vis_log] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
'MS_Description', N'主键',
'SCHEMA', N'dbo',
'TABLE', N'sys_vis_log',
'COLUMN', N'id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'名称',
'SCHEMA', N'dbo',
'TABLE', N'sys_vis_log',
'COLUMN', N'name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'是否执行成功（Y-是，N-否）',
'SCHEMA', N'dbo',
'TABLE', N'sys_vis_log',
'COLUMN', N'success'
GO

EXEC sp_addextendedproperty
'MS_Description', N'具体消息',
'SCHEMA', N'dbo',
'TABLE', N'sys_vis_log',
'COLUMN', N'message'
GO

EXEC sp_addextendedproperty
'MS_Description', N'ip',
'SCHEMA', N'dbo',
'TABLE', N'sys_vis_log',
'COLUMN', N'ip'
GO

EXEC sp_addextendedproperty
'MS_Description', N'地址',
'SCHEMA', N'dbo',
'TABLE', N'sys_vis_log',
'COLUMN', N'location'
GO

EXEC sp_addextendedproperty
'MS_Description', N'浏览器',
'SCHEMA', N'dbo',
'TABLE', N'sys_vis_log',
'COLUMN', N'browser'
GO

EXEC sp_addextendedproperty
'MS_Description', N'操作系统',
'SCHEMA', N'dbo',
'TABLE', N'sys_vis_log',
'COLUMN', N'os'
GO

EXEC sp_addextendedproperty
'MS_Description', N'操作类型（字典 1登入 2登出）',
'SCHEMA', N'dbo',
'TABLE', N'sys_vis_log',
'COLUMN', N'vis_type'
GO

EXEC sp_addextendedproperty
'MS_Description', N'访问时间',
'SCHEMA', N'dbo',
'TABLE', N'sys_vis_log',
'COLUMN', N'vis_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'访问账号',
'SCHEMA', N'dbo',
'TABLE', N'sys_vis_log',
'COLUMN', N'account'
GO

EXEC sp_addextendedproperty
'MS_Description', N'系统访问日志表',
'SCHEMA', N'dbo',
'TABLE', N'sys_vis_log'
GO


-- ----------------------------
-- Table structure for xn_code_gen_test
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[xn_code_gen_test]') AND type IN ('U'))
	DROP TABLE [dbo].[xn_code_gen_test]
GO

CREATE TABLE [dbo].[xn_code_gen_test] (
  [id] bigint  NOT NULL,
  [name] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [age] int  NULL,
  [interest] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [switchTest] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [birthday] date  NULL,
  [whether] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [explain_test] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [create_time] datetime2(7)  NULL,
  [create_user] bigint  NULL,
  [update_time] datetime2(7)  NULL,
  [update_user] bigint  NULL
)
GO

ALTER TABLE [dbo].[xn_code_gen_test] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
'MS_Description', N'主键',
'SCHEMA', N'dbo',
'TABLE', N'xn_code_gen_test',
'COLUMN', N'id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'姓名',
'SCHEMA', N'dbo',
'TABLE', N'xn_code_gen_test',
'COLUMN', N'name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'年龄',
'SCHEMA', N'dbo',
'TABLE', N'xn_code_gen_test',
'COLUMN', N'age'
GO

EXEC sp_addextendedproperty
'MS_Description', N'兴趣',
'SCHEMA', N'dbo',
'TABLE', N'xn_code_gen_test',
'COLUMN', N'interest'
GO

EXEC sp_addextendedproperty
'MS_Description', N'开关',
'SCHEMA', N'dbo',
'TABLE', N'xn_code_gen_test',
'COLUMN', N'switchTest'
GO

EXEC sp_addextendedproperty
'MS_Description', N'日期',
'SCHEMA', N'dbo',
'TABLE', N'xn_code_gen_test',
'COLUMN', N'birthday'
GO

EXEC sp_addextendedproperty
'MS_Description', N'是否已婚',
'SCHEMA', N'dbo',
'TABLE', N'xn_code_gen_test',
'COLUMN', N'whether'
GO

EXEC sp_addextendedproperty
'MS_Description', N'简介',
'SCHEMA', N'dbo',
'TABLE', N'xn_code_gen_test',
'COLUMN', N'explain_test'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建时间',
'SCHEMA', N'dbo',
'TABLE', N'xn_code_gen_test',
'COLUMN', N'create_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'测试',
'SCHEMA', N'dbo',
'TABLE', N'xn_code_gen_test'
GO


-- ----------------------------
-- Primary Key structure for table sys_app
-- ----------------------------
ALTER TABLE [dbo].[sys_app] ADD CONSTRAINT [PK__sys_app__3213E83FA469832F] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
ON [PRIMARY]
GO


-- ----------------------------
-- Primary Key structure for table sys_code_generate
-- ----------------------------
ALTER TABLE [dbo].[sys_code_generate] ADD CONSTRAINT [PK__sys_code__3213E83FC1D83C12] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
ON [PRIMARY]
GO


-- ----------------------------
-- Primary Key structure for table sys_code_generate_config
-- ----------------------------
ALTER TABLE [dbo].[sys_code_generate_config] ADD CONSTRAINT [PK__sys_code__3213E83F63AA024F] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
ON [PRIMARY]
GO


-- ----------------------------
-- Primary Key structure for table sys_config
-- ----------------------------
ALTER TABLE [dbo].[sys_config] ADD CONSTRAINT [PK__sys_conf__3213E83F52DD4C0E] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
ON [PRIMARY]
GO


-- ----------------------------
-- Primary Key structure for table sys_dict_data
-- ----------------------------
ALTER TABLE [dbo].[sys_dict_data] ADD CONSTRAINT [PK__sys_dict__3213E83F3D0F362E] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
ON [PRIMARY]
GO


-- ----------------------------
-- Primary Key structure for table sys_dict_type
-- ----------------------------
ALTER TABLE [dbo].[sys_dict_type] ADD CONSTRAINT [PK__sys_dict__3213E83F7E963628] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
ON [PRIMARY]
GO


-- ----------------------------
-- Primary Key structure for table sys_emp
-- ----------------------------
ALTER TABLE [dbo].[sys_emp] ADD CONSTRAINT [PK__sys_emp__3213E83FBEF4A799] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
ON [PRIMARY]
GO


-- ----------------------------
-- Primary Key structure for table sys_emp_ext_org_pos
-- ----------------------------
ALTER TABLE [dbo].[sys_emp_ext_org_pos] ADD CONSTRAINT [PK__sys_emp___3213E83F63AFD0D8] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
ON [PRIMARY]
GO


-- ----------------------------
-- Primary Key structure for table sys_emp_pos
-- ----------------------------
ALTER TABLE [dbo].[sys_emp_pos] ADD CONSTRAINT [PK__sys_emp___3213E83F82F1365A] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
ON [PRIMARY]
GO


-- ----------------------------
-- Primary Key structure for table sys_file_info
-- ----------------------------
ALTER TABLE [dbo].[sys_file_info] ADD CONSTRAINT [PK__sys_file__3213E83FEDFB4C82] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
ON [PRIMARY]
GO


-- ----------------------------
-- Primary Key structure for table sys_menu
-- ----------------------------
ALTER TABLE [dbo].[sys_menu] ADD CONSTRAINT [PK__sys_menu__3213E83F63327F79] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
ON [PRIMARY]
GO


-- ----------------------------
-- Primary Key structure for table sys_notice
-- ----------------------------
ALTER TABLE [dbo].[sys_notice] ADD CONSTRAINT [PK__sys_noti__3213E83F18F245D7] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
ON [PRIMARY]
GO


-- ----------------------------
-- Primary Key structure for table sys_notice_user
-- ----------------------------
ALTER TABLE [dbo].[sys_notice_user] ADD CONSTRAINT [PK__sys_noti__3213E83F5EAB8726] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
ON [PRIMARY]
GO


-- ----------------------------
-- Primary Key structure for table sys_oauth_user
-- ----------------------------
ALTER TABLE [dbo].[sys_oauth_user] ADD CONSTRAINT [PK__sys_oaut__3213E83FD166933A] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
ON [PRIMARY]
GO


-- ----------------------------
-- Primary Key structure for table sys_op_log
-- ----------------------------
ALTER TABLE [dbo].[sys_op_log] ADD CONSTRAINT [PK__sys_op_l__3213E83FE8902E3E] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
ON [PRIMARY]
GO


-- ----------------------------
-- Primary Key structure for table sys_org
-- ----------------------------
ALTER TABLE [dbo].[sys_org] ADD CONSTRAINT [PK__sys_org__3213E83FFF42A0D9] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
ON [PRIMARY]
GO


-- ----------------------------
-- Indexes structure for table sys_pos
-- ----------------------------
CREATE NONCLUSTERED INDEX [CODE_UNI]
ON [dbo].[sys_pos] (
  [code] ASC
)
GO


-- ----------------------------
-- Primary Key structure for table sys_pos
-- ----------------------------
ALTER TABLE [dbo].[sys_pos] ADD CONSTRAINT [PK__sys_pos__3213E83F9D91266C] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
ON [PRIMARY]
GO


-- ----------------------------
-- Primary Key structure for table sys_role
-- ----------------------------
ALTER TABLE [dbo].[sys_role] ADD CONSTRAINT [PK__sys_role__3213E83FBBF912B1] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
ON [PRIMARY]
GO


-- ----------------------------
-- Primary Key structure for table sys_role_data_scope
-- ----------------------------
ALTER TABLE [dbo].[sys_role_data_scope] ADD CONSTRAINT [PK__sys_role__3213E83F5B610FCD] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
ON [PRIMARY]
GO


-- ----------------------------
-- Primary Key structure for table sys_role_menu
-- ----------------------------
ALTER TABLE [dbo].[sys_role_menu] ADD CONSTRAINT [PK__sys_role__3213E83F42711968] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
ON [PRIMARY]
GO


-- ----------------------------
-- Primary Key structure for table sys_sms
-- ----------------------------
ALTER TABLE [dbo].[sys_sms] ADD CONSTRAINT [PK__sys_sms__3213E83F6AF506FE] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
ON [PRIMARY]
GO


-- ----------------------------
-- Primary Key structure for table sys_timers
-- ----------------------------
ALTER TABLE [dbo].[sys_timers] ADD CONSTRAINT [PK__sys_time__3213E83FA2F107D4] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
ON [PRIMARY]
GO


-- ----------------------------
-- Primary Key structure for table sys_user
-- ----------------------------
ALTER TABLE [dbo].[sys_user] ADD CONSTRAINT [PK__sys_user__3213E83FAC2D3929] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
ON [PRIMARY]
GO


-- ----------------------------
-- Primary Key structure for table sys_user_data_scope
-- ----------------------------
ALTER TABLE [dbo].[sys_user_data_scope] ADD CONSTRAINT [PK__sys_user__3213E83FDC5B50A2] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
ON [PRIMARY]
GO


-- ----------------------------
-- Primary Key structure for table sys_user_role
-- ----------------------------
ALTER TABLE [dbo].[sys_user_role] ADD CONSTRAINT [PK__sys_user__3213E83FDA1B5D8E] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
ON [PRIMARY]
GO


-- ----------------------------
-- Primary Key structure for table sys_vis_log
-- ----------------------------
ALTER TABLE [dbo].[sys_vis_log] ADD CONSTRAINT [PK__sys_vis___3213E83F6697A488] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
ON [PRIMARY]
GO


-- ----------------------------
-- Primary Key structure for table xn_code_gen_test
-- ----------------------------
ALTER TABLE [dbo].[xn_code_gen_test] ADD CONSTRAINT [PK__xn_code___3213E83FB5F32D00] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
ON [PRIMARY]
GO

