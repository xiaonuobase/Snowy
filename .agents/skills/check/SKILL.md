---
name: check
description: 代码规范检查（后端 + 前端，三级清单）。仅在用户显式调用时执行：Claude Code、Qwen Code 输入 /check，Codex 输入 $check。
disable-model-invocation: true
---

# check —— 代码规范检查

按 Snowy 规范对代码做三级检查（严重/警告/建议）。**只检查不修改**（修复由用户决定或另开任务）。

## 范围确定

- 用户调用时附带的描述指定了文件/目录 → 只查该范围
- 未指定 → 默认查业务范围：`snowy-plugin/snowy-plugin-biz/` + 用户自建的业务插件（sys/auth/dev/gen/client/mobile 以外的插件）+ 对应前端 `snowy-admin-web/src/api/{插件}/`、`snowy-admin-web/src/views/{插件}/`
- 全量（含框架模块）仅在用户明说"全量"时执行。框架代码**不是全绿**，以下平台例外在报告中单列为"平台存量"，不算违规：
  - S2：AuthConfigure.rewriteSaStrategy、AuthSsoConfigure.configSsoClient 的 @Autowired 方法注入
  - S3：CommonOtpUtil 与 `auth/core/protocol/saml/**` 中把受检异常包装成的 RuntimeException / IllegalArgumentException
  - S4：AuthSsoController 的 SSO 回调（logoutCall/pushClient）、GlobalErrorViewController 的方法级 @RequestMapping
  - S7：AuthConfigure Javadoc 中的 `@SaCheckPermission("user:add")` 示例（注释，不是真实注解）
  - W1：没有实体的聚合服务只 implements（见 W1）
  - W6：EnvironmentDetector、JacksonConfigure、DevLogIdParam、`auth/core/protocol/saml/core/**`（移植的第三方协议代码）、`package-info.java` 缺 @author 或 @date
  - W8：消息、会话、文件、生成器配置等专用 PageParam 字段不全

## 检查清单

### 后端（严重级——任一命中报"不通过"）

| # | 检查项 | Grep pattern | 判定 |
|---|---|---|---|
| S1 | 自定义包名扫描 | `^package `，筛出不以 `vip.xiaonuo` 开头的包 | 全是 vip.xiaonuo 即通过；有自定义包名时，启动模块 Application 的 `scanBasePackages`、GlobalConfigure 的 `@MapperScan`、application.properties 的 `mybatis-plus.mapper-locations` 必须都覆盖该包，漏任一项即严重（接口 404、Bean 注入失败、Invalid bound statement；清单见 plugin-architecture） |
| S2 | 注入 | `@Autowired` | 0 命中（用 @Resource）；全量检查时 AuthConfigure.rewriteSaStrategy、AuthSsoConfigure 的 @Autowired 方法注入是平台例外 |
| S3 | 异常 | `new (RuntimeException\|ServiceException\|IllegalArgumentException)` | 0 命中（业务代码用 CommonException）；底层工具/协议代码把受检异常包装成运行时异常属于平台例外（CommonOtpUtil、`auth/core/protocol/saml/**`） |
| S4 | 类级路由与通用映射 | `@RequestMapping`（controller 下） | 0 命中（禁止类级路由，方法上只用 @GetMapping/@PostMapping；全量检查时 AuthSsoController 的 SSO 回调与 GlobalErrorViewController 是平台例外） |
| S5 | 表名小写 | `@TableName\((value = )?"[a-z]` | 0 命中 |
| S6 | MapstructUtils | `MapstructUtils\|@AutoMapper` | 0 命中（用 BeanUtil） |
| S7 | 权限码 | `^\s*@SaCheckPermission\("[^/]`（行首匹配，排除注释行） | 0 命中；另外人工核对注解值与同一方法的 @GetMapping/@PostMapping 路径逐字一致 |

### 后端（警告级）

| # | 检查项 | 要点 |
|---|---|---|
| W1 | ServiceImpl 形态 | 有对应 Entity + Mapper 的 ServiceImpl 必须 `extends ServiceImpl<`；没有实体的聚合服务（如 BizIndexServiceImpl、SysIndexServiceImpl、AuthServiceImpl、DevMonitorServiceImpl）只 implements 不报警 |
| W2 | 事务 | 写方法（add/edit/delete/update）有 `@Transactional(rollbackFor`；存量例外 BizDictServiceImpl.edit、BizUserExtServiceImpl.updatePasswordLastTime（全量检查时平台 dev/client/mobile 等另有缺失）列为"存量"，不作为新代码违规 |
| W3 | 操作日志 | 写接口（按语义判断：新增/修改/删除/授权/状态变更等）有 `@CommonLog(`；用 POST 的查询接口（如 getAncestorNodes、getXxxListByIdList）不要求；登录类接口由登录日志单独记录 |
| W4 | checkSqlInjection | `new QueryWrapper` 后接 `.checkSqlInjection()`；存量例外 BizPositionServiceImpl.positionSelector 标为"存量"，新增代码 100% 要求 |
| W5 | Lombok | entity/param 无 `@Data` |
| W6 | Javadoc | 类头含 `@author` 与 `@date`、中文注释；**仅新增文件**再查四行式（一行中文摘要 + @author + @date），出现 `@param`/`@return`/`<p>`/多行说明段即报（存量不报） |
| W7 | 主键 | `@TableId` 字段为 String |
| W8 | PageParam | 通用业务 PageParam 必须含 current/size/sortField/sortOrder/searchKey 五个字段；专用分页（消息、会话、文件、生成器配置等）按接口需要，全量检查时不报 |
| W9 | 命名前缀 | 类名前缀 = 所在插件缩写（biz 用 Biz，自建插件用自己的缩写） |
| W10 | 版权头 | 只查平台自带文件（common、平台插件、*-api、web-app、biz 出厂演示代码）的 12 行版权头有没有被删；用户新写的业务代码不强制，没有不报 |

### 前端（业务范围）

| # | 检查项 | 要点 |
|---|---|---|
| F1 | TS 残留 | `: string`、`interface `、`as [A-Z]` 0 命中 |
| F2 | Element Plus | `<el-` 0 命中 |
| F3 | api js 结构 | default export + baseRequest；URL 前缀与后端插件一致（biz 为 /biz/） |
| F4 | s-table 模式 | 使用 s-table 的列表页 index.vue 有 `:data="loadData"` 函数模式；业务首页 views/biz/index/index.vue 等非列表页不检查 |
| F5 | defineExpose | form.vue 有 `defineExpose({ onOpen })` |
| F6 | 权限码 | hasPerm 值为驼峰式 |
| F7 | 注释一行制 | **仅新增文件**：无连续 2 行以上 `//` 段落、无 JSDoc `@param`/`@returns`、无注释掉的废代码（存量不报） |
| F8 | SFC 块顺序 | .vue 首个顶级标签必须是 `<template`，顺序 template → script → style；script 带 `name=`（存量例外 views/biz/dict/index.vue、views/biz/user/transfer.vue，新增文件必须带） |
| F9 | 底座组件复用 | **仅新增文件**：上传、选人/选机构、树选择、分页下拉、批量按钮未手写同类实现（如 `<a-upload`、a-modal 内嵌 a-table 选人），应使用 frontend-pc 选型表中的底座组件 |
| F10 | 选择器 API 来源 | xn-*-selector / xn-tree-select / xn-page-select 的 API 参数来自 `@/api/biz/*`，业务页不得 import `@/api/sys/*` 给选择器（超管专属接口，普通用户被拒） |

## 执行方式

对每一项运行 Grep（路径 = 审查范围），记录命中文件与行；S1 命中自定义包名时 Read 启动模块三处配置核对；版权头（仅平台自带文件，可结合 `git diff` 看是否被删）/Javadoc/主键类需要 Read 抽查（每类抽 3 个文件，全部为新增文件时全查）。

## 输出格式

```markdown
# 规范检查报告

**范围**：...　**结论**：通过 / N 项严重问题

## 严重（必须修复）
| 级别 | 文件:行 | 问题 | 修复 |
|---|---|---|---|
| S2 | BizXxxController.java | @Autowired | 改 @Resource |

## 警告（建议修复）
（同上）

## 建议
- ...

## 快速修复指引
（每个严重项给一行修复命令或修改要点；详细规范见 .agents/skills/code-patterns/）
```

## 收尾

- 严重问题 > 0 → 建议修复后重跑 check 流程技能（Claude Code、Qwen Code `/check`，Codex `$check`）
- 建议用户在提交前做深度审查：支持子代理的工具使用 code-reviewer 子代理（定义见 `.claude/agents/code-reviewer.md`）；不支持子代理的工具按该文件中的审查清单自行审查
