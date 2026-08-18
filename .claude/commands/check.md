---
description: 代码规范检查（后端 + 前端，三级清单）
---

# /check —— 代码规范检查

按 Snowy 规范对代码做三级检查（严重/警告/建议）。**只检查不修改**（修复由用户决定或另开任务）。

## 范围确定

- 参数 `$ARGUMENTS` 指定了文件/目录 → 只查该范围
- 未指定 → 默认查业务范围：`snowy-plugin/snowy-plugin-biz/` + `snowy-admin-web/src/api/biz/` + `snowy-admin-web/src/views/biz/`
- 全量（含框架模块）仅在用户明说"全量"时（框架代码预期全绿，作为基准）

## 检查清单

### 后端（严重级——任一命中报"不通过"）

| # | 检查项 | Grep pattern | 判定 |
|---|---|---|---|
| S1 | 包名 | `^package (com\|org\|net)\.` | 0 命中 |
| S2 | 注入 | `@Autowired` | 0 命中（用 @Resource） |
| S3 | 异常 | `new (RuntimeException\|ServiceException\|IllegalArgumentException)` | 0 命中（用 CommonException） |
| S4 | 类级路由 | `@RequestMapping`（controller 下） | 0 命中 |
| S5 | 表名小写 | `@TableName\((value = )?"[a-z]` | 0 命中 |
| S6 | MapstructUtils | `MapstructUtils\|@AutoMapper` | 0 命中（用 BeanUtil） |
| S7 | 版权头 | 抽查/全查 .java 前 12 行含 `Copyright [2022] [https://www.xiaonuo.vip]` | 新文件 100% 有 |
| S8 | 权限码 | `@SaCheckPermission\("[^/]` | 0 命中（值必须以 / 开头且=URL） |

### 后端（警告级）

| # | 检查项 | 要点 |
|---|---|---|
| W1 | ServiceImpl 形态 | `extends ServiceImpl<` 全命中；只 implements 报警 |
| W2 | 事务 | 写方法（add/edit/delete/update）有 `@Transactional(rollbackFor` |
| W3 | 操作日志 | 写接口有 `@CommonLog(` |
| W4 | checkSqlInjection | `new QueryWrapper` 后接 `.checkSqlInjection()` |
| W5 | Lombok | entity/param 无 `@Data` |
| W6 | Javadoc | 类头含 `@author` 与 `@date`、中文注释 |
| W7 | 主键 | `@TableId` 字段为 String |
| W8 | PageParam | 含 current/size/sortField/sortOrder/searchKey |
| W9 | 命名前缀 | biz 下类名 Biz 前缀 |

### 前端（biz 范围）

| # | 检查项 | 要点 |
|---|---|---|
| F1 | TS 残留 | `: string`、`interface `、`as [A-Z]` 0 命中 |
| F2 | Element Plus | `<el-` 0 命中 |
| F3 | api js 结构 | default export + baseRequest；URL 前缀 /biz/ |
| F4 | s-table 模式 | index.vue 有 `:data="loadData"` 函数模式 |
| F5 | defineExpose | form.vue 有 `defineExpose({ onOpen })` |
| F6 | 权限码 | hasPerm 值为驼峰式 |

## 执行方式

对每一项运行 Grep（路径 = 审查范围），记录命中文件与行；版权头/Javadoc/主键类需要 Read 抽查（每类抽 3 个文件，全部为新增文件时全查）。

## 输出格式

```markdown
# 规范检查报告

**范围**：...　**结论**：✅ 通过 / ❌ N 项严重问题

## ❌ 严重（必须修复）
| 级别 | 文件:行 | 问题 | 修复 |
|---|---|---|---|
| S2 | BizXxxController.java | @Autowired | 改 @Resource |

## ⚠️ 警告（建议修复）
（同上）

## 💡 建议
- ...

## 快速修复指引
（每个严重项给一行修复命令或修改要点；详细规范见 .claude/skills/code-patterns/）
```

## 收尾

- 严重问题 > 0 → 建议修复后重跑 `/check`
- 建议用户在提交前跑 code-reviewer 子代理做深度审查
