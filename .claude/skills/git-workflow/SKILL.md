---
name: git-workflow
description: 本项目 Git 规范：提交四原则、【类别】中文描述提交格式、仓库状态检测与降级、不提交清单。触发场景：1) 提交代码 2) 用户要求 push/分支操作 3) 检查仓库状态。触发词：Git、提交、commit、push、分支、merge、回退、暂存、仓库。
---

# Git 工作流规范

## 仓库现状

- 本仓库克隆自 gitee（origin = https://gitee.com/xiaonuobase/snowy.git），主分支 master，日常开发在 dev 分支，发版打 tag（如 v3.7.0）；具体状态以 `git remote -v`、`git branch -a` 的实际结果为准
- `.gitignore` 已覆盖 target/、node_modules/、.idea/、logs/、dist/ 等

## 提交四原则（硬性）

1. **只提交本次任务相关的文件**——`git status` 逐个确认，禁止 `git add -A` 一把梭（可能带进无关改动）
2. **不提交敏感与本地配置改动**——application.properties 的本地口令、.env 类文件（已忽略的除外也要留意）
3. **默认只做本地 commit**——已配置 origin，push 会直接推到 gitee 远程，必须用户明确要求
4. **提交前过 check 流程技能（Claude Code、Qwen Code `/check`，Codex `$check`）或 code-patterns 检查清单**——规范违规不提交

## Commit Message 规范

```
【类别】中文描述

常用类别：底座 | 新增 | 修复 | 优化 | 升级 | 更新（与仓库历史提交一致）
```

示例：
```
【新增】供应商管理六件套与前端三件
【修复】供应商分页排序字段未转下划线导致SQL报错
【优化】供应商查询改批量接口消除N+1
【更新】补充国密与安全指南
【底座】升级前后端依赖
```

## 常用操作

```bash
git status                          # 提交前必看
git add <具体文件路径>               # 精确暂存
git commit -m "【新增】xxx"
git log --oneline -10               # 查看最近提交
git diff --stat                     # 改动概览

# 大改动前打 tag 备份
git tag v0.1-重构前
```

## 禁止操作

- 禁止 `git push --force` 到主分支
- 禁止 `git reset --hard` 丢弃未确认的改动（先问用户）
- 禁止 `git clean -fd`（会删未跟踪的新文件）
- 禁止提交 node_modules / target / 日志 / 本地口令配置

## 与项目管理流程技能的联动

sync、update-status、next 流程技能依赖 git 历史分析提交记录；若执行中 git 不可用（`git rev-parse --is-inside-work-tree` 失败），自动降级为代码扫描 + 文档时间线模式。

## 检查清单

- [ ] git status 干净无关文件
- [ ] message 为【类别】中文描述
- [ ] 未 push（除非用户明确要求）
- [ ] 提交的代码过了规范检查
