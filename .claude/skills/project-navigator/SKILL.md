---
name: project-navigator
description: Snowy 项目导航地图：目录结构速查、"X 功能在哪个文件"速查表、常用查找命令。触发场景：1) 找某功能/配置/类在哪个文件 2) 不熟悉项目结构想快速定位 3) 新会话了解项目。触发词：在哪、哪个文件、目录结构、导航、找、定位、项目结构、入口、启动类。
---

# Snowy 项目导航地图

## 顶层结构（30 秒版）

```
snowy-master/
├── snowy-common/               公共基础（CommonResult/异常/实体基类/缓存/国密/工具）
├── snowy-plugin/               7 个插件：sys(系统) auth(鉴权) dev(工具) gen(生成器) client(C端) mobile(移动) biz(★业务)
├── snowy-plugin-api/           对应 7 个 *-api 跨插件接口模块
├── snowy-web-app/              启动模块（Application + 全局配置 + SQL 脚本）
├── snowy-admin-web/            前端（Vue3 + AntdV，JS）
└── .claude/                    本工程化配置
```

插件内：`{plugin}.core.{config,enums,listener,timer,util}` + `{plugin}.modular.{业务域}.{controller,entity,enums,mapper(+mapping),param,result,service(+impl),provider}`

## "我想找 X 在哪" 速查表

| 想找什么 | 位置 |
|---|---|
| 启动类 | `snowy-web-app/src/main/java/vip/xiaonuo/Application.java`（端口 82） |
| 全部环境配置（数据库/Redis/密钥） | `snowy-web-app/src/main/resources/application.properties` |
| 建库脚本（33 表） | `snowy-web-app/src/main/resources/_sql/snowy_mysql.sql` |
| 路由白名单（免登录/C端/超管） | `snowy-web-app/.../core/config/GlobalConfigure.java` |
| 全局异常处理 | `snowy-web-app/.../core/handler/GlobalExceptionHandler.java` |
| 统一返回/异常 | `snowy-common/.../pojo/CommonResult.java`、`exception/CommonException.java` |
| 实体基类（审计字段/逻辑删除） | `snowy-common/.../pojo/CommonEntity.java` |
| 国密工具/字段加密 | `snowy-common/.../util/CommonCryptogramUtil.java`、`handler/CommonSm4CbcTypeHandler.java` |
| 缓存操作器 | `snowy-common/.../cache/CommonCacheOperator.java` |
| 定时任务接口 | `snowy-common/.../timer/CommonTimerTaskRunner.java` |
| B 端登录流程 | `snowy-plugin-auth/.../modular/login/` |
| 权限码组装（登录时） | `snowy-plugin-sys/.../user/provider/SysLoginUserApiProvider.java` |
| 用户/角色/组织/菜单管理 | `snowy-plugin-sys/.../modular/{user,role,org,resource}/` |
| 字典（系统/业务） | `snowy-plugin-dev/.../dict/`、`snowy-plugin-biz/.../dict/` |
| 文件上传 | `snowy-plugin-dev/.../file/` |
| 短信/邮件/站内信/推送 | `snowy-plugin-dev/.../{sms,email,message,push}/` |
| 定时任务管理 | `snowy-plugin-dev/.../job/` |
| 操作日志切面 | `snowy-plugin-dev/.../core/aop/DevLogAop.java` |
| 代码生成器逻辑/模板 | `snowy-plugin-gen/.../basic/`、`snowy-plugin-gen/src/main/resources/` |
| 跨插件接口定义 | `snowy-plugin-api/*-api/.../api/*.java` |
| ★ 业务代码（二开主战场） | `snowy-plugin-biz/.../modular/{域}/` |
| 前端 API 封装 | `snowy-admin-web/src/api/{插件}/xxxApi.js` |
| 前端页面 | `snowy-admin-web/src/views/{插件}/{域}/index.vue + form.vue` |
| 前端请求封装/工具 | `snowy-admin-web/src/utils/request.js`、`tool.js` |
| 前端组件（37 个 Xn*） | `snowy-admin-web/src/components/` |
| 前端路由/菜单 | `snowy-admin-web/src/router/`（动态部分来自 SYS_RESOURCE） |
| 前端状态（Pinia） | `snowy-admin-web/src/store/` |
| 前端国际化 | `snowy-admin-web/src/locales/` |
| 前端国密 | `snowy-admin-web/src/utils/smCrypto.js` |

## 常用查找命令

```bash
# 按类名找文件
Glob pattern: **/BizNotice*.java

# 按 URL 找接口
Grep pattern: "/biz/notice/page"  glob: **/*.java

# 按注解找（如所有写接口）
Grep pattern: "@CommonLog"  glob: **/controller/*.java

# 按表名找实体
Grep pattern: "@TableName.*BIZ_NOTICE"  glob: **/*.java

# 前端找按钮权限码
Grep pattern: "hasPerm"  path: snowy-admin-web/src/views/biz

# 找某功能的菜单 SQL
Grep pattern: "INSERT INTO .SYS_RESOURCE"  path: snowy-web-app/src/main/resources/_sql
```

## 典型链路（一个请求的旅程）

```
前端 xxxApi.js（baseRequest '/biz/xxx/page'）
  → Vite 代理 /api → localhost:82
  → GlobalConfigure 的 SaServletFilter（白名单校验）
  → SaInterceptor（@SaCheckPermission URL 校验）
  → XxxController.page（@Validated）
  → XxxServiceImpl.page（QueryWrapper.checkSqlInjection + CommonPageRequest）
  → XxxMapper（MP BaseMapper）
  → MySQL（BIZ_XXX，逻辑删除自动过滤）
  → CommonResult.data(page) 返回
  → request.js 统一处理（code!==200 报错）
```

## 检查清单（导航技能没有代码改动，用于定位）

- [ ] 定位到目标文件后再动手，不要凭记忆猜路径
- [ ] 改动前 Read 真实文件（文档可能滞后于代码）
