---
name: progress
description: 项目进度梳理（业务模块完整度报告）。仅在用户显式调用时执行：Claude Code、Qwen Code 输入 /progress，Codex 输入 $progress。
disable-model-invocation: true
---

# progress —— 进度梳理

按 framework-config.json 的口径统计业务进度并输出报告。**只统计用户自建业务**，框架与出厂演示域不计入。

## 执行步骤

### 1. 枚举业务域
业务插件 = snowy-plugin-biz + 用户自建插件（snowy-plugin/ 下除 sys/auth/dev/gen/client/mobile 外的 snowy-plugin-* 目录）。自建插件包名可能自定义，用 find 定位 modular 目录：
```bash
find snowy-plugin/*/src/main/java -type d -name modular 2>/dev/null | grep -vE 'snowy-plugin-(sys|auth|dev|gen|client|mobile)/'
ls {上面输出的每个 modular 目录}/
```
只对 snowy-plugin-biz 过滤 excludeModules：index、dict、group、notice、org、position、user（自建插件里的域全部计入）。剩余为自建域，报告中标注所属插件（空则报告"0 个自建业务域，进度 0%"）。

### 2. 逐域统计六件套（分母 6）
对每个自建域检查：
```bash
ls {modular 目录}/{域}/entity/
ls .../{域}/mapper/            # Mapper 接口（mapping/ 下 XML 是加分项）
ls .../{域}/service/           # Service 接口
ls .../{域}/service/impl/      # ServiceImpl
ls .../{域}/controller/
ls .../{域}/param/             # 至少 Page/Add/Edit/Id 四类
```
记：必备 6 件各存在与否（Param 组 4 类齐全才算 1 件）；可选件（mapping XML、result/）单独标注。

### 3. 前端三件（分母 3）
```bash
ls snowy-admin-web/src/api/biz/        # biz{Xxx}Api.js
ls snowy-admin-web/src/views/biz/{域}/ # index.vue / form.vue
# 自建插件对应 src/api/{插件缩写}/、src/views/{插件缩写}/{域}/（缩写 = 目录名去掉 snowy-plugin-）
```

### 4. 质量扫描（业务插件范围）
```bash
Grep pattern: "TODO:|FIXME:|XXX:|HACK:"  path: {每个业务插件目录}   # snowy-plugin-biz + 自建插件，逐个执行
Grep pattern: "@Autowired|MapstructUtils|@Data\b"  path: {每个业务插件目录}   # 规范违规粗查
```

### 5. 输出报告

```markdown
# 业务进度报告（YYYY-MM-DD）

## 总览
自建业务域 N 个 | 后端平均完整度 X% | 前端平均完整度 Y% | 总体 Z%

## 模块明细
| 业务域 | Entity | Mapper | Service | Impl | Controller | Param | 完整度 | 前端(api/index/form) | 前端完整度 |
|---|---|---|---|---|---|---|---|---|---|
| supplier | 有 | 有 | 有 | 有 | 有 | 有 | 100% | 有/有/有 | 100% |
| order | 有 | 有 | 有 | 缺 | 缺 | 有 | 67% | 有/缺/缺 | 33% |

（+ 可选件列：XML/Result 有无）

## 完成度判定
- 100%：六件套+前端三件齐
- 71-99%：后端齐前端缺 / 或缺可选关键件
- 50-70%：核心链路可跑但有缺口
- 1-49%：开工未半
- 0%：仅规划

## 待办代码标记
TODO x N（列文件）、FIXME x N

## 规范粗查
（@Autowired / MapstructUtils / @Data 命中情况，建议调用 check 流程技能（Claude Code、Qwen Code `/check`，Codex `$check`）深查）

## 建议下一步
- 完成度最低的模块：补 X 件 → 建议调用 dev 流程技能（Claude Code、Qwen Code `/dev`，Codex `$dev`）续写或手补
- 调用 next 流程技能（Claude Code、Qwen Code `/next`，Codex `$next`）看优先级建议
```

## 注意

- 不落盘（用户要求时才更新 docs/项目状态.md）
- 统计只看文件存在性，不审内容质量（那是 check 技能的事）
