---
name: bug-detective
description: Snowy 问题排查方法论与常见故障库：启动失败、401/403、接口报错、数据查不到、前后端联调问题的诊断决策树。触发场景：1) 任何报错/不工作/异常排查 2) 启动失败或页面白屏 3) 权限/数据问题定位。触发词：Bug、报错、异常、错误、不工作、失败、排查、调试、401、403、500、白屏、启动失败、查不到、排查思路。
---

# Snowy 问题排查指南

## 诊断决策树（先分类再深入）

```
问题发生在哪一层？
├─ 后端启动失败 ──→ 树 A
├─ 接口报错（4xx/5xx/业务码） ──→ 树 B
├─ 数据不对（查不到/查错/删不掉） ──→ 树 C
└─ 前端异常（白屏/按钮不见/数据不刷） ──→ 树 D
```

## 树 A：后端启动失败

1. 端口 82 被占？（`netstat -ano | findstr :82`）
2. MySQL 连不上/库没导 → 导入 `snowy-web-app/src/main/resources/_sql/snowy_mysql.sql`，核对 application.properties 的 master 段口令
3. Redis 连不上 → 本地 6379 是否启动（database 1）
4. JDK 版本 → 推荐 17（与编译目标一致），21 可用
5. 依赖没编译 → 根目录 `mvn clean install -DskipTests`
6. Knife4j 401 → /doc.html 的 basic 认证见 application.properties 的 `knife4j.basic.username/password`（当前为 snowyAdmin / Snowy@2026!）

## 树 B：接口报错

| 现象 | 根因 | 处理 |
|---|---|---|
| 401 未登录 | token 没带/过期；或接口不在白名单也确实需要登录 | 检查请求头 `token`；新公开接口要加 GlobalConfigure.NO_LOGIN_PATH_ARR 并**重启** |
| 403 / 无权限 | 用户直接授权和所属角色授权的**接口权限**里都没有此接口 URL | 角色管理（或用户管理）→ **授权权限**（grantPermission：勾选接口 URL + 数据范围）补上该 URL；"授权资源"只管菜单/按钮码，不产生接口权限。超管（superAdmin）也不会自动放行手写的新接口 |
| 404 接口不存在 | URL 与前端调用不一致；或自定义包名的 Controller 没被扫描 | 核对 @GetMapping/@PostMapping 路径；自定义包名见下方问题库第 10 条 |
| 500 + CommonException 消息 | 业务异常（中文消息就是线索） | 按消息定位 Service 抛出点 |
| 500 + NPE | 常见：跨插件取到 null 未判空（JSONObject.getStr）；登录用户取不到 | ObjectUtil.isEmpty 判空 |
| 参数校验失败 | @Valid 注解的消息 | 看 Param 类校验消息 |
| delete 后台删不掉 | 逻辑删除字段值异常 | 检查 DELETE_FLAG 值（未删除 `NOT_DELETE`／已删除 `DELETED`，取值见 application.properties 的 logic-delete-value / logic-not-delete-value） |
| SQL 报错 Unknown column | Entity 字段与表列不一致 / 排序字段没转下划线 | 核对 @TableName 大小写与列名；sortField 要 StrUtil.toUnderlineCase |

## 树 C：数据问题

| 现象 | 根因 |
|---|---|
| 列表查不到已插数据 | DELETE_FLAG 不是 NOT_DELETE（逻辑删除过滤掉了） |
| like 手机号查不到 | 该字段 SM4 加密落库，密文 like 永远不命中（见 crypto-sm） |
| 翻译字段（xxxName）为空 | @Trans 配置错：字典编码不存在 / target 表无数据 / 缺 @TableField(exist=false) 冗余字段 |
| 字典下拉为空 | 字典编码大小写不一致；前端字典缓存未刷新 |
| 新增后列表不刷新 | 前端没 emit('successful') / 没 tableRef.refresh |
| 时间范围查不到 | 前端没拆 startCreateTime/endCreateTime；或 value-format 缺失 |
| 分页页码/条数不对 | 请求参数名必须是 `current`、`size`（CommonPageRequest.defaultPage() 直接读 HttpServletRequest，与 PageParam 绑定无关；size>100 按 100 处理） |
| 分页 total 不对 | 自定义 XML 联表产生了重复行；或 count 条件与查询条件不一致 |
| ID 前端精度丢失 | 前端把 String id 当 Number 处理了（保持字符串） |

## 树 D：前端异常

| 现象 | 根因 |
|---|---|
| 白屏 | 控制台看报错；常见组件名拼错 / api js 路径 404 / 代理未启动（npm run dev 的 /api 代理 → 82） |
| 菜单不显示 | SYS_RESOURCE 菜单 SQL 未执行；或未给角色授权 |
| 按钮不显示 | hasPerm 码与 SYS_RESOURCE BUTTON 行 code 不一致（驼峰） |
| 表单打不开 | form.vue 忘了 defineExpose({ onOpen }) |
| 登录密码报错 | 前端 SM2 公钥与后端常量不一致（smCrypto.js / CommonCryptogramUtil） |
| 修改代码不生效 | Vite 热更新失效 → 重启 npm run dev；后端改动 → 重启 Java |

## 排查工具箱

```bash
# 看后端日志（IDE 控制台为主；文件日志看配置）
# 数据库直查（从 application.properties 取连接）
mysql -h127.0.0.1 -uroot -p****** snowy -e "SELECT ID,DELETE_FLAG FROM BIZ_XXX LIMIT 5;"

# 验证接口（Knife4j：http://localhost:82/doc.html，可带 token 调试）

# 全局搜代码
Grep pattern: "方法名/类名/错误消息片段"  path: snowy-plugin
```

**接口调试优先用 Knife4j（/doc.html）**：先登录拿 token（B 端鉴权里全局设置），再单测接口，把"前端问题还是后端问题"先切开。

## 本项目特有问题库（10 条，按排查经验整理，不按频率排序）

1. **登录一直报密码错误** → 出厂密码是 **Snowy@2026!**（权威来源 DEV_CONFIG 的 SNOWY_SYS_DEFAULT_PASSWORD_FOR_B）；2026-06-26 之前导入的旧库为 `123456`，可查 SYS_USER.PASSWORD 区分：`207cf410…` 对应 123456，`b7eb53ce…` 对应 Snowy@2026!。接口直调登录还需 SM2 加密密码（见 api-verify 技能）
2. **Sa-Token 白名单改了没重启** → GlobalConfigure 是启动时构建的
3. **授权了还是 403** → 通常只做了"授权资源"（菜单/按钮，SYS_ROLE_HAS_RESOURCE），没做"授权权限"（接口 URL，SYS_ROLE_HAS_PERMISSION）。到 角色管理 → 授权权限 勾选该 URL（超管也一样，手写的新接口不会自动授权）。保存授权后系统会自动刷新该角色在线用户的 TokenSession 权限（refreshOnlineUserPermission），**不用重新登录**；若仍 403，确认用户确实挂了该角色，且授权的 URL 与 `@SaCheckPermission` 的值逐字一致
4. **SM4 字段 like 查不到** → 设计期就要定查询方案
5. **雪花 id 丢失精度** → 后端 String，前端任何 Number 转换都会坏（parseInt 等）
6. **跨插件注入失败** → 只依赖了 *-api 却想注入实现类；或忘加依赖
7. **@Trans 不生效** → 1) 接口返回的是否就是带 @Trans 的实体/VO（easy-trans 全局模式在 Controller 返回时翻译）；2) DICTIONARY：字典编码是否存在，是否绕过字典管理直接改了库（直接改库不会触发 refreshTransCache，需要重启或在字典管理里保存一次）；3) SIMPLE：target/ref 是否写对，冗余字段是否标了 `@TableField(exist = false)`。autoResultMap 只与 TypeHandler（如 SM4）有关，与 @Trans 无关
8. **新增菜单 404** → 菜单 component 路径与前端 views 目录不匹配（如 biz/supplier/index ↔ src/views/biz/supplier/index.vue）
9. **生成的代码包名或位置错** → 代码生成器 packageName/pluginName/moduleName 填错（生成到 biz 为 `packageName=vip.xiaonuo`、`pluginName=snowy-plugin-biz`、`moduleName=biz`；自建插件填它自己的目录名和缩写，pluginName 只填缩写会导致项目内生成失败，见 code-generator），落位后要手工核
10. **自定义包名后接口 404、Bean 找不到、Mapper 报 Invalid bound statement** → 业务包不以 vip.xiaonuo 开头时，启动模块默认扫不到：检查 Application 的 `scanBasePackages`、GlobalConfigure 的 `@MapperScan`、application.properties 的 `mybatis-plus.mapper-locations` 三处是否覆盖该包（见 plugin-architecture）

## 检查清单（修复后）

- [ ] 根因明确（能说出为什么），不是碰巧好使
- [ ] 修复未引入新的规范违规（过一遍 code-patterns）
- [ ] 同类隐患点已排查（同类接口/同类字段）

## 参考实现（排查时看这些）

| 文件 | 说明 |
|---|---|
| `snowy-web-app/src/main/java/vip/xiaonuo/core/handler/GlobalExceptionHandler.java` | 异常→错误码映射 |
| `snowy-web-app/src/main/java/vip/xiaonuo/core/config/GlobalConfigure.java` | 白名单/拦截规则 |
| `snowy-web-app/src/main/resources/application.properties` | 全部环境配置 |
| `snowy-plugin/snowy-plugin-dev/src/main/java/vip/xiaonuo/dev/modular/log/` | 操作/异常日志模块 |
| `snowy-plugin/snowy-plugin-dev/src/main/java/vip/xiaonuo/dev/modular/monitor/` | 服务器监控 |
