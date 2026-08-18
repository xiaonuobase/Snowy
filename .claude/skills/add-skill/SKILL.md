---
name: add-skill
description: 元技能——教 AI 如何为本项目新增一个符合规范的技能（SKILL.md 格式、注册到 hook、验证流程）。触发场景：1) 用户要求新增/修改技能 2) 发现重复出现的知识值得沉淀为技能。触发词：新增技能、创建技能、添加skill、技能规范、SKILL.md。
---

# 如何新增一个技能

## 流程（5 步）

1. **定边界**：新技能解决什么问题？与现有 26 个技能是否重叠？（重叠就合并进去，不新建）
2. **核实事实**：技能里的每条规范、每个代码片段、每个参考路径，**必须先 Read 真实代码核实**，禁止凭模型先验写（本项目与 RuoYi 系大量反向，先验会写反）
3. **写文件**：`.claude/skills/{name}/SKILL.md`，格式见下
4. **注册**：更新 `.claude/hooks/skill-forced-eval.js` 里的技能清单（加一行）
5. **验证**：参考路径 100% 存在；触发词演练（3 条该命中 + 2 条不该命中）

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
（❌/✅ 两列，代码与项目真实代码逐 token 一致）

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

## 现有 26 技能清单（防范围冲突）

| 类别 | 技能 |
|---|---|
| 后端主线 | crud-development、api-development、plugin-architecture、code-generator、database-ops、backend-annotations、code-patterns |
| 平台能力 | common-toolkit、cache-redis、file-oss-management、sms-mail、message-push、scheduled-jobs、dict-config |
| 安全前端 | security-auth、crypto-sm、frontend-pc |
| C 端与质量 | client-mobile、bug-detective、performance-doctor、project-navigator、git-workflow |
| Snowy 特色自研 | env-setup（环境与首启）、api-verify（接口自测闭环）、platform-extension（平台扩展点地图） |
| 元技能 | add-skill |

新增技能先对照此表：内容若属于某现有技能的一个章节，**合并**而不是新建。

## 注册位置（两处）

1. `.claude/hooks/skill-forced-eval.js` 的 instructions 字符串里的技能清单——加一行 `- {name}: {一句话触发词摘要}`
2. 本技能上面的清单表（保持文档与实际一致）

## 常见陷阱

1. **写完忘注册 hook** → 技能永远不被激活
2. **触发词太宽泛**（如"开发"、"代码"）→ 到处误触发；触发词要具体
3. **代码虚构**——凭 RuoYi/通用 Spring 先验写代码模板，与本项目规范相反
4. **参考实现带行号**——代码一改就失效
5. **与现有技能范围重叠**——知识分散且互相矛盾

## 验证脚本（新增技能后跑）

```bash
# 校验 SKILL.md 的参考路径存在性：提取"参考实现"表格里的项目相对路径逐个检查
# 简易版：肉眼核对，或用 Grep 提取后 ls 验证
```
