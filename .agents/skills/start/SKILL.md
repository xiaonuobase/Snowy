---
name: start
description: 新会话快速了解项目现状。仅在用户显式调用时执行：Claude Code、Qwen Code 输入 /start，Codex 输入 $start。
disable-model-invocation: true
---

# start —— 项目概览

新会话开场：快速给出项目现状报告，让自己（AI）和用户对齐上下文。

## 执行步骤

### 1. 读配置
Read `AGENTS.md`（工程宪法）与 `.claude/framework-config.json`（框架/业务划分口径）。

### 2. 扫描业务域
```bash
find snowy-plugin/*/src/main/java -type d -name modular 2>/dev/null | grep -vE 'snowy-plugin-(sys|auth|dev|gen|client|mobile)/'   # biz + 自建业务插件
ls {上面输出的每个 modular 目录}/
ls snowy-admin-web/src/views/
```
snowy-plugin-biz 对照 excludeModules（index/dict/group/notice/org/position/user 为出厂演示）排除，自建插件里的域全部计入，得出**自建业务域列表**（口径见 framework-config 的 business.backend）。

### 3. 检查 git（带降级）
```bash
git rev-parse --is-inside-work-tree 2>/dev/null && git log --oneline -10 || echo "NO_GIT"
```
git 可用 → 取最近 10 条提交；不可用 → 报告"无提交历史可分析"并跳过。

### 4. 检查文档状态
检查 `docs/` 三文档（项目状态/需求文档/待办清单）是否存在；不存在 → 提示可调用 init-docs 流程技能（Claude Code、Qwen Code `/init-docs`，Codex `$init-docs`）初始化。

### 5. 按档位输出报告

**出厂状态档**（自建业务域 = 0）：
```markdown
# Snowy 二次开发项目 · 现状

**平台**：Snowy 3.x（Spring Boot 3.5.16 + Java 17 插件化后端 + Vue3/AntdV 前端），国密体系
**业务进度**：0%（尚无自建业务域——出厂 7 个演示域不计入）

## 环境就绪检查
- MySQL：库 snowy 需导入 snowy-web-app/src/main/resources/_sql/snowy_mysql.sql
- Redis：127.0.0.1:6379
- 后端启动：snowy-web-app 的 Application（82 端口）；前端：snowy-admin-web npm run dev（81）

## 开始开发
1. 想好第一个业务功能 → 调用 dev 流程技能（Claude Code、Qwen Code `/dev`，Codex `$dev`）并附带功能描述
2. 或先调用 init-docs 流程技能（Claude Code、Qwen Code `/init-docs`，Codex `$init-docs`）建立项目管理文档
3. 开发规范速查：AGENTS.md + .agents/skills/ 下的全部技能
```

> 报告中的版本号输出前先核实：Spring Boot 以根 pom.xml 的 `spring-boot.version` 为准；pom/package.json 中的 version 字段固定为 3.0.0，不代表发布版本，Snowy 发布版本以 `git describe --tags` 的结果为准（如 v3.7.0）。

**开发中档**（1-5 个自建域）：在上述基础上加"业务模块进度表"（每域六件套完整度 + 前端三件完整度，口径见 framework-config），最近提交摘要，待办摘要。

**成熟档**（>5 个域）：进度表 + 风险与建议（TODO/FIXME 扫描业务插件范围）。

## 注意

- 只读扫描，不改任何文件
- 报告控制在 40 行内，细节让用户调用 progress / next 流程技能（Claude Code、Qwen Code `/progress` `/next`，Codex `$progress` `$next`）
