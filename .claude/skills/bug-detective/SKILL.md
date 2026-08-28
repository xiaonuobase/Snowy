---
name: bug-detective
description: Snowy 问题排查方法论与高发故障库：启动失败、401/403、接口报错、数据查不到、前后端联调问题的诊断决策树。触发场景：1) 任何报错/不工作/异常排查 2) 启动失败或页面白屏 3) 权限/数据问题定位。触发词：Bug、报错、异常、错误、不工作、失败、排查、调试、401、403、500、白屏、启动失败、查不到、排查思路。
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
2. MySQL 连不上/库没导 → 导入 `_sql/snowy_mysql.sql`，核对 application.properties 的 master 段口令
3. Redis 连不上 → 本地 6379 是否启动（database 1）
4. JDK 版本 → 至少 17（17 及以上，如 17/21/23 均可）
5. 依赖没编译 → 根目录 `mvn clean install -DskipTests`
6. Knife4j 401 → /doc.html 的 basic 认证是 admin/123456

## 树 B：接口报错

| 现象 | 根因 | 处理 |
|---|---|---|
| 401 未登录 | token 没带/过期；或接口不在白名单也确实需要登录 | 检查请求头 `token`；新公开接口要加 GlobalConfigure.NO_LOGIN_PATH_ARR 并**重启** |
| 403 / 无权限 | 该用户角色的资源授权里没有此接口 URL | 角色管理重新授权（接口权限 = 资源授权生成的 apiUrl 列表） |
| 500 + CommonException 消息 | 业务异常（中文消息就是线索） | 按消息定位 Service 抛出点 |
| 500 + NPE | 常见：跨插件取到 null 未判空（JSONObject.getStr）；登录用户取不到 | ObjectUtil.isEmpty 判空 |
| 参数校验失败 | @Valid 注解的消息 | 看 Param 类校验消息 |
| delete 后台删不掉 | 逻辑删除字段值异常 | 检查 DELETE_FLAG 值（NOT_DELETE/DEDED） |
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
| 分页总数不对 | PageParam 的 current/size 没传到（GET 参数绑定失败，检查字段名拼写） |
| ID 前端精度丢失 | 前端把 String id 当 Number 处理了（保持字符串） |

## 树 D：前端异常

| 现象 | 根因 |
|---|---|
| 白屏 | 控制台看报错；常见组件名拼错 / api js 路径 404 / 代理未启动（npm run dev 的 /api 代理 → 82） |
| 菜单不显示 | SYS_RESOURCE 菜单 SQL 未执行；或未给角色授权 |
| 按钮不显示 | hasPerm 码与 SYS_RESOURCE BUTTON 行 code 不一致（驼峰） |
| 表单打不开 | form.vue 忘了 defineExpose({ onOpen }) |
| 登录密码报错 | 前端 SM2 公钥与后端配置不一致（smCrypto.js / snowy.cryptogram 配置段） |
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

## 本项目特有问题库（高发 Top 9）

1. **登录一直报密码错误** → 本仓库出厂密码是 **Snowy@2026!**（不是网上说的 123456——那是官方演示站的）；权威来源 DEV_CONFIG 的 SNOWY_SYS_DEFAULT_PASSWORD_FOR_B。接口直调登录还需 SM2 加密密码（见 api-verify 技能）
2. **Sa-Token 白名单改了没重启** → GlobalConfigure 是启动时构建的
3. **授权了还是 403** → 授权的是按钮码，但接口权限要的是"资源授权勾选到对应菜单/按钮"生成的 apiUrl 集合；重新授权并让用户**重新登录**（权限码缓存在 TokenSession）
4. **SM4 字段 like 查不到** → 设计期就要定查询方案
5. **雪花 id 丢失精度** → 后端 String，前端任何 Number 转换都会坏（parseInt 等）
6. **跨插件注入失败** → 只依赖了 *-api 却想注入实现类；或忘加依赖
7. **@Trans 不生效** → autoResultMap 没开 / 字典缓存旧数据（发数据变更事件）
8. **新增菜单 404** → 菜单 component 路径与前端 views 目录不匹配（如 biz/supplier/index ↔ src/views/biz/supplier/index.vue）
9. **生成的代码包名错** → 代码生成器 packageName/pluginName 填错，落位后要手工核

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
