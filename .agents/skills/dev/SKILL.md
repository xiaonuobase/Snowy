---
name: dev
description: 开发新功能（建表 + 后端六件套 + 前端三件 + 菜单SQL，双模式）。仅在用户显式调用时执行：Claude Code、Qwen Code 输入 /dev，Codex 输入 $dev。
disable-model-invocation: true
---

# dev —— 开发新业务功能

按以下流程开发一个新业务功能。**全程遵循以下技能规范**：先读取以下技能的 SKILL.md（Claude Code 可用 Skill 工具激活；其他工具直接读取 `.agents/skills/{技能名}/SKILL.md`）——crud-development / database-ops / code-generator / frontend-pc / code-patterns（自建插件或自定义包名时加读 plugin-architecture）。

## 第 1 步：需求确认

向用户确认（一次问全，不要挤牙膏）：
1. 功能名称与业务用途（一句话）
2. 核心字段与查询条件（给一个建议清单让用户增删）
3. 页面形态：普通表格 / 树形 / 左树右表 / 主子表
4. 代码位置与包名：放 snowy-plugin-biz 还是自建业务插件、用什么包名（未说明则默认 snowy-plugin-biz + `vip.xiaonuo.biz`）；自建插件按 plugin-architecture 新建插件清单注册，包名不以 vip.xiaonuo 开头时按其"使用自定义包名"补扫描配置
5. 开发模式：
   - **模式 A（AI 直写）**：AI 直接生成全部代码（推荐：可控、规范一致）
   - **模式 B（平台生成器）**：AI 建表并给出 GenBasic 配置清单，用户走平台"系统 → 在线开发 → 代码生成"生成，AI 负责生成后的核对修补（适合与官方模板保持极致一致）

## 第 2 步：功能重复检查（强制，不可跳过）

```bash
# Grep 检查是否已有同名/同义业务域（覆盖 biz 与自建插件）
Grep pattern: "(?i)(功能名英文|表名)"  path: snowy-plugin/
ls snowy-plugin/snowy-plugin-biz/src/main/java/vip/xiaonuo/biz/modular/   # 目标为自建插件时换成其 modular 目录
```
发现重复 → 停止，向用户报告冲突并询问是扩展已有模块还是确认新建。

## 第 3 步：设计表

按 database-ops 技能规范：
- 表名 `{插件前缀}_{域名}` 全大写（biz 插件用 `BIZ_`，自建插件用插件名，如 snowy-plugin-order → `ORDER_`）；`ID varchar(20)` 主键；审计字段组齐全；字段中文 COMMENT
- 输出建表 SQL 给用户确认（此时尚未执行）

## 第 4 步：执行建表（降级链）

数据库连接从 `snowy-web-app/src/main/resources/application.properties` 的 dynamic master 段动态解析（**禁止硬编码口令**）：
1. mysql CLI 可用 → 直接执行建表
2. 不可用 → SQL 写入 `docs/sql-pending/YYYY-MM-DD-{功能}.sql`，明确提示用户手动执行后再继续第 6 步

## 第 5 步：生成方案确认（仅一次）

列出将生成的文件清单（目标插件与包名 + 后端六件套 + 空 Mapper XML 共 10-12 个文件 + 前端三件 + 菜单 SQL；自定义包名时连同 snowy-web-app 的扫描配置改动一并列出），用户确认后进入执行。

## 第 6 步：生成代码

### 模式 A（AI 直写）
1. **先 Read 范本**：`snowy-plugin/snowy-plugin-biz/src/main/java/vip/xiaonuo/biz/modular/notice/` 下的 Controller/Entity/Service/ServiceImpl/Param（强制，逐文件对照）
2. 按顺序生成到目标插件的 `modular/{域名}/`（中文注释、@author/@date；不加小诺版权头，用户有版权头规范按用户的来；下列以 biz 为例，自建插件把 Biz 前缀换成插件缩写）：
   - `entity/Biz{Xxx}.java` → `mapper/Biz{Xxx}Mapper.java` + `mapper/mapping/Biz{Xxx}Mapper.xml`（空 `<mapper namespace="Mapper 全限定名">` 占位，与生成器一致，有自定义 SQL 时写在里面）→ `service/Biz{Xxx}Service.java` → `service/impl/Biz{Xxx}ServiceImpl.java` → `param/` 四类 → `controller/Biz{Xxx}Controller.java`（→ 需要时 `result/`、`enums/`）
   - 包名不以 vip.xiaonuo 开头：同步修改 snowy-web-app 的 3 处扫描配置（见 plugin-architecture"使用自定义包名"）
3. 前端三件：`snowy-admin-web/src/api/biz/biz{Xxx}Api.js` + `views/biz/{域名}/index.vue` + `form.vue`（自建插件把 biz 换成插件名）；字段涉及上传、选人/选机构、树选择、大表下拉、富文本、批量操作时，**先查 frontend-pc 技能「底座组件选型」表用现成组件**（参数与坑见 `.agents/skills/frontend-pc/components.md`），不要手写
4. 菜单 SQL：SYS_RESOURCE 的 MENU 行 + BUTTON 行（驼峰按钮码），按 code-generator 技能里 sqlend 模板格式。**挂载点直接用出厂真实 ID**（见 database-ops 技能"菜单挂载点速查"：MODULE 列=业务模块 `1548901111999773976`，PARENT_ID=`'0'` 或公司架构目录 `1548901111999773977`），不留占位符

### 模式 B（平台生成器引导）
1. 确认表已建（第 4 步）
2. 输出 GenBasic 配置清单：pluginName=`snowy-plugin-biz`（默认值，填 biz 会导致项目内生成失败）、packageName=`vip.xiaonuo`、moduleName=`biz`（自建插件/自定义包名时分别填插件目录名、包名前缀、插件缩写，如 `snowy-plugin-order` / `com.company` / `order`，见 code-generator）、tablePrefix=`Y`（是否移除表前缀的 Y/N 开关，默认 Y，只影响自动带出的 busName，如 BIZ_SUPPLIER → supplier）、busName、className（原样用于类名和文件名，生成器**不会**自动加前缀；导入表时按完整表名带出 BIZ_SUPPLIER → BizSupplier，biz 插件保持 BizXxx，自建插件改为插件缩写开头）、module/menuPid（让用户在界面选所属模块与上级菜单）、genType=形态、authorName
3. 指导用户：系统 → 在线开发 → 代码生成 → 导入表 → 填配置 → 字段配置 → 生成
4. 用户生成后，AI 执行"生成后必做"：核对落位/URL/权限码（版权头按 code-generator 说明处理，自定义包名补扫描配置）；**ZIP 方式**（execGenZip）需执行 sqlend 菜单 SQL，再给角色"授权资源"（菜单/按钮）和"授权权限"（接口 URL，超管也要）；**项目内生成**（execGenPro）已自动写入菜单按钮并给超管授权菜单、按钮和生成的接口 URL，不要再执行菜单 SQL（会产生重复菜单），只需给超管以外的目标角色授权

## 第 7 步：收尾清单

- [ ] 菜单/按钮 SQL 已执行（或已落盘 sql-pending 并告知；模式 B 项目内生成已自动写入，跳过）
- [ ] 提醒用户：重启后端 → 角色管理里给角色"授权资源"（菜单/按钮）并"授权权限"（勾选新接口 URL，**超管角色也要勾**，否则超管也 403）→ 前端刷新
- [ ] **接口自测**（后端已启动时，见 api-verify 技能）：先确认超管已被授权新接口 URL（模式 A 手写必须到 角色管理 → 授权权限 勾选）→ SM2 加密密码登录 superAdmin（口令见 env-setup 出厂账号，旧库可能为 123456）拿 token → curl 调新接口五连（page/add/校验拦截/edit/delete）→ 输出自测报告
- [ ] 建议调用 check 流程技能（Claude Code、Qwen Code `/check`，Codex `$check`）审查本次产出
- [ ] 询问是否记入任务跟踪（更新 docs/待办清单.md 或项目状态.md）

## AI 强制规则

1. 版权头：修改平台代码时不删除原有 12 行版权头；新写的业务代码不加小诺版权头（用户有自己的版权头规范时按用户的来）
2. 代码风格逐 token 对照 notice 范本，禁止同类框架惯性写法（17 条反向清单见 code-patterns）
3. @SaCheckPermission 值 = URL；按钮码 = 驼峰
4. 表/字段全大写；String 主键
5. 新写的查询必须 `new QueryWrapper<>().checkSqlInjection()`（存量例外 BizPositionServiceImpl.positionSelector，不要模仿），只对可选查询参数判空；新写的写方法加 `@Transactional(rollbackFor = Exception.class)`（存量 BizDictServiceImpl.edit 缺事务，不要当范本）
6. 前端 JS 非 TS，AntdV 组件，s-table loadData 模式
7. 生成文件前先 Read 对应范本文件
8. 不修改 sys/auth/dev/gen 等 6 个平台插件和 snowy-common 的代码（业务代码放 biz 或自建业务插件）
9. 建表 SQL 执行前必须经用户确认
10. 完成后主动给出验证步骤（启动→用出厂账号 superAdmin 登录→角色授权（授权资源 + 授权权限）→页面操作→Knife4j 测试）
