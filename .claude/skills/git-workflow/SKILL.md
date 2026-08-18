---
name: git-workflow
description: 本项目 Git 规范：提交四原则、conventional commit 中文格式、仓库状态检测与降级、不提交清单。触发场景：1) 提交代码 2) 用户要求 push/分支操作 3) 检查仓库状态。触发词：Git、提交、commit、push、分支、merge、回退、暂存、仓库。
---

# Git 工作流规范

## 仓库现状

- 本仓库由下载的源码 zip 初始化（`git init`，master 分支），**无远程仓库**
- `.gitignore` 已覆盖 target/、node_modules/、.idea/、logs/、dist/ 等

## 提交四原则（硬性）

1. **只提交本次任务相关的文件**——`git status` 逐个确认，禁止 `git add -A` 一把梭（可能带进无关改动）
2. **不提交敏感与本地配置改动**——application.properties 的本地口令、.env 类文件（已忽略的除外也要留意）
3. **默认只做本地 commit**——push 必须用户明确要求；当前无远程，配置远程后也遵守此条
4. **提交前过 /check 或 code-patterns 检查清单**——规范违规不提交

## Commit Message 规范

```
<type>: <中文描述>

type ∈ feat | fix | refactor | docs | style | test | chore | perf
```

示例：
```
feat: 新增供应商管理六件套与前端三件
fix: 修复供应商分页排序字段未转下划线导致SQL报错
refactor: 供应商查询改批量接口消除N+1
docs: 补充国密与安全指南
chore: 初始化 Snowy v3.0.0 原始代码基线
```

## 常用操作

```bash
git status                          # 提交前必看
git add <具体文件路径>               # 精确暂存
git commit -m "feat: xxx"
git log --oneline -10               # 查看最近提交
git diff --stat                     # 改动概览

# 大改动前打 tag 备份
git tag v0.1-重构前
```

## 禁止操作

- ❌ `git push --force` 到主分支
- ❌ `git reset --hard` 丢弃未确认的改动（先问用户）
- ❌ `git clean -fd`（会删未跟踪的新文件）
- ❌ 提交 node_modules / target / 日志 / 本地口令配置

## 与项目管理命令的联动

`/sync` `/update-status` `/next` 依赖 git 历史分析提交记录；若命令执行中 git 不可用（`git rev-parse --is-inside-work-tree` 失败），自动降级为代码扫描 + 文档时间线模式。

## 检查清单

- [ ] git status 干净无关文件
- [ ] message 符合 type: 中文
- [ ] 未 push（除非用户明确要求）
- [ ] 提交的代码过了规范检查
