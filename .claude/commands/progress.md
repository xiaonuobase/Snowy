---
description: 项目进度梳理（业务模块完整度报告）
---

# /progress —— 进度梳理

按 framework-config.json 的口径统计业务进度并输出报告。**只统计用户自建业务**，框架与出厂演示域不计入。

## 执行步骤

### 1. 枚举业务域
```bash
ls snowy-plugin/snowy-plugin-biz/src/main/java/vip/xiaonuo/biz/modular/
```
过滤掉 excludeModules：index、dict、group、notice、org、position、user。剩余为自建域（空则报告"0 个自建业务域，进度 0%"）。

### 2. 逐域统计六件套（分母 6）
对每个自建域检查：
```bash
ls snowy-plugin/snowy-plugin-biz/src/main/java/vip/xiaonuo/biz/modular/{域}/entity/
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
```

### 4. 质量扫描（biz 范围）
```bash
Grep pattern: "TODO:|FIXME:|XXX:|HACK:"  path: snowy-plugin/snowy-plugin-biz/
Grep pattern: "@Autowired|MapstructUtils|@Data\b"  path: snowy-plugin/snowy-plugin-biz/   # 规范违规粗查
```

### 5. 输出报告

```markdown
# 业务进度报告（YYYY-MM-DD）

## 总览
自建业务域 N 个 | 后端平均完整度 X% | 前端平均完整度 Y% | 总体 Z%

## 模块明细
| 业务域 | Entity | Mapper | Service | Impl | Controller | Param | 完整度 | 前端(api/index/form) | 前端完整度 |
|---|---|---|---|---|---|---|---|---|---|
| supplier | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | 100% | ✓/✓/✓ | 100% |
| order | ✓ | ✓ | ✓ | ✗ | ✗ | ✓ | 67% | ✓/✗/✗ | 33% |

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
（@Autowired / MapstructUtils / @Data 命中情况，建议 /check 深查）

## 建议下一步
- 完成度最低的模块：补 X 件 → 建议 /dev 续写或手补
- /next 看优先级建议
```

## 注意

- 不落盘（用户要求时才更新 docs/项目状态.md）
- 统计只看文件存在性，不审内容质量（那是 /check 的事）
