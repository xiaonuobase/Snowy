---
description: 全量同步代码状态并生成综合报告
---

# /sync —— 全量同步与综合报告

全量扫描代码 + git 历史，生成综合状态报告落盘 `docs/sync-report-YYYY-MM-DD.md`。

命令流水线：`/start（了解）→ /dev|/crud（开发）→ /check（检查）→ /sync（本命令）→ /progress（进度）→ /next（建议）`

## 执行步骤

### 1. 代码全量扫描
- 业务域枚举与完整度统计（同 /progress 第 1-3 步逻辑）
- TODO/FIXME 扫描（biz 范围 + 前端 views/biz）
- 规范粗查（@Autowired / @Data / MapstructUtils / TS 残留）

### 2. git 分析（带降级）
```bash
git rev-parse --is-inside-work-tree 2>/dev/null || echo NO_GIT
```
- 可用：`git log --oneline -50` 分析最近提交（区分业务提交/工程配置提交）；`git log --format="%ad %s" --date=short -20` 得时间线
- 不可用：降级为"文档时间线"——读 docs/项目状态.md 的最近更新记录 + `ls -lt` 业务域目录 mtime 推断活跃度

### 3. 读取现有文档
docs/ 三文档（存在则读取整合；不存在标注"未初始化，建议 /init-docs"）。

### 4. 生成报告并落盘

```markdown
# 同步报告 YYYY-MM-DD

## 一、代码状态
（业务域完整度表——同 /progress 格式）

## 二、提交历史 / 变更时间线
（git 提交列表或降级时间线；标注业务/配置分类）

## 三、质量问题
（规范粗查命中 + TODO/FIXME 清单）

## 四、文档状态
（三文档存在性与最后更新时间；缺失项提醒）

## 五、风险与建议
（结合上述输出的 3-5 条具体建议）
```

写入 `docs/sync-report-YYYY-MM-DD.md`（docs/ 不存在则创建；已有同日报告则覆盖前提示）。

### 5. 汇报

向用户输出报告摘要（10 行内）+ 报告文件路径。

## 注意

- 报告用中文；表格优先
- 只写 docs/ 下的报告文件，不碰业务代码
- git 降级不报错，报告中注明"基于文档时间线（无 git 历史）"
