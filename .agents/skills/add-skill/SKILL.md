---
name: add-skill
description: 元技能——教 AI 如何为本项目新增一个符合规范的技能（SKILL.md 格式、注册到 AGENTS.md 技能索引、同步、验证流程）。触发场景：1) 用户要求新增/修改技能 2) 发现重复出现的知识值得沉淀为技能。触发词：新增技能、创建技能、添加skill、技能规范、SKILL.md。
---

# 如何新增一个技能

## 流程（6 步）

1. **定边界**：新技能解决什么问题？与现有技能（26 个知识技能 + 10 个流程技能）是否重叠？（重叠就合并进去，不新建）
2. **核实事实**：技能里的每条规范、每个代码片段、每个参考路径，**必须先 Read 真实代码核实**，禁止凭模型先验写（本项目与同类快速开发框架大量反向，先验会写反）
3. **写文件**：`.agents/skills/{name}/SKILL.md`（技能唯一来源），格式见下
4. **注册**：在根目录 `AGENTS.md` 的技能索引区（`<!-- skill-index:start -->` 与 `<!-- skill-index:end -->` 之间）对应分类下加一行；**不需要改钩子脚本**（各工具自动加载 AGENTS.md，索引随之进入上下文）
5. **同步**：运行 `node .agents/scripts/sync-skills.js`，把技能复制到 `.claude/skills/`（Claude Code 只认该目录；AI 会话结束时钩子也会自动同步）
6. **验证**：参考路径 100% 存在；`.claude/skills/{name}/SKILL.md` 已生成；触发词演练（3 条该命中 + 2 条不该命中）

## SKILL.md 格式规范

```markdown
---
name: kebab-case-name
description: 一句话定位。触发场景：1) ... 2) ... 3) ...。触发词：词1、词2、词3、...。注意：与 X 技能的边界说明。
---

# 技能标题

## 架构特征表 / 核心规则
（表格优先，必须/禁止措辞加粗）

## 正误代码对照
（错误/正确两列，代码与项目真实代码逐 token 一致）

## 检查清单
（- [ ] 复选框）

## 参考实现
（指向真实存在的文件路径，禁止行号——行号会随代码漂移）
```

硬性要求：
- name 用 kebab-case，与目录名一致
- description 必须含：一句话定位 + 至少 3 条触发场景 + 至少 5 个触发词 +（可选）与相邻技能的边界
- 正文 ≤300 行；表格优先于长文
- 代码示例先从项目里找到真实样本再抄写改写
- 全中文
- 不使用 emoji 和装饰图标（如警告三角、红叉、绿勾、星号、彩色圆点、圈数字）：正误对照用「错误 / 正确」，警示用「注意：」，禁令用「禁止」，序号用 1. 2. 3.；普通箭头 → 可以用
## 现有技能清单（26 知识技能 + 10 流程技能，防范围冲突）

| 类别 | 技能 |
|---|---|
| 后端主线 | crud-development、api-development、plugin-architecture、code-generator、database-ops、backend-annotations、code-patterns |
| 平台能力 | common-toolkit、cache-redis、file-oss-management、sms-mail、message-push、scheduled-jobs、dict-config |
| 安全前端 | security-auth、crypto-sm、frontend-pc |
| C 端与质量 | client-mobile、bug-detective、performance-doctor、project-navigator、git-workflow |
| Snowy 特色自研 | env-setup（环境与首启）、api-verify（接口自测闭环）、platform-extension（平台扩展点地图） |
| 元技能 | add-skill |
| 流程技能（用户显式调用：Claude Code、Qwen Code `/名称`，Codex `$名称`） | start、dev、crud、check、sync、update-status、progress、next、init-docs、add-todo |

新增技能先对照此表：内容若属于某现有技能的一个章节，**合并**而不是新建。

## 注册位置（两处）

1. 根目录 `AGENTS.md` 的技能索引区（`<!-- skill-index:start -->` 与 `<!-- skill-index:end -->` 之间）——知识技能加在"知识技能"分类下，流程技能加在"流程技能"分类下，格式 `- {name}: {一句话触发词摘要}`。标记之间只放索引本身，不要放其他说明
2. 本技能上面的清单表（保持文档与实际一致）

## 常见陷阱

1. **写完忘注册 AGENTS.md 索引 / 忘记同步** → 技能不被评估激活；或 Claude Code 里找不到该技能（`.claude/skills/` 未生成副本）
2. **直接改 `.claude/skills/` 副本** → 会被下次同步覆盖或产生冲突；修改技能只改 `.agents/skills/`
3. **触发词太宽泛**（如"开发"、"代码"）→ 到处误触发；触发词要具体
4. **代码虚构**——凭同类框架/通用 Spring 先验写代码模板，与本项目规范相反
5. **参考实现带行号**——代码一改就失效
6. **与现有技能范围重叠**——知识分散且互相矛盾

## 验证脚本（新增技能后跑）

```bash
# 校验 SKILL.md 的参考路径存在性：提取"参考实现"表格里的项目相对路径逐个检查
# 简易版：肉眼核对，或用 Grep 提取后 ls 验证
```
