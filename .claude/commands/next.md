---
description: 下一步开发建议（优先级排序）
---

# /next —— 下一步建议

扫描项目现状，给出按优先级排序的具体下一步建议（带预计耗时与操作命令）。

## 建议来源（按此顺序扫描）

### 1. 待办清单（docs/待办清单.md）
存在 → 高/中/低优先级各区取头部条目。不存在 → 跳过并提示可 /init-docs。

### 2. 代码缺口（biz 范围）
- 六件套不齐的域（/progress 口径）→ "补齐 X 件"
- 前端三件缺失 → "补前端"
```bash
Grep pattern: "FIXME:|TODO:"  path: snowy-plugin/snowy-plugin-biz/
```

### 3. 质量问题
```bash
Grep pattern: "@Autowired|@Data\b|MapstructUtils"  path: snowy-plugin/snowy-plugin-biz/
```
命中 → "规范修复"。

### 4. git 近期提交（带降级）
```bash
git rev-parse --is-inside-work-tree 2>/dev/null && git log --oneline -15
```
可用 → 看是否有"进行到一半"的提交序列（同模块连续 feat）；不可用 → 跳过。

## 输出格式

```markdown
# 下一步建议

## 🔴 高优先级
1. [约 30 分钟] 补齐 order 域的 Controller 与 ServiceImpl（当前完整度 67%）
   → 操作：`/dev 继续 order 模块，补 controller 和 service`
2. [约 10 分钟] 修复 BizXxxServiceImpl 的 FIXME（分页排序未判空）
   → 操作：直接让我修，或描述问题让我处理

## 🟡 中优先级
3. [约 20 分钟] 前端 supplier 缺 form.vue
4. ...

## 🟢 低优先级 / 待办清单
5. ...

## 建议节奏
（结合近期完成情况给一句话，如："order 模块后端已齐，建议一鼓作气补前端"）
```

## 规则

- 每条建议：具体动作 + 预计耗时 + 可直接执行的命令/指令
- 最多 8 条，宁缺毋滥
- 不主动建议改框架模块（sys/auth/dev/gen）
