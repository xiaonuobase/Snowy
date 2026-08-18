---
name: client-mobile
description: Snowy C 端（client 插件）与移动端（mobile 插件）开发规范：C 端用户体系、C 端登录链路、clientRequest 前端封装、移动端资源管理、代码生成器 mobile 模板。触发场景：1) 开发面向终端用户（C 端）的功能 2) 小程序/APP/H5 接口对接 3) 移动端菜单资源管理。触发词：C端、客户端、client、移动端、mobile、uni-app、小程序、APP、CLIENT_USER、移动端菜单、C端用户。
---

# Snowy C 端与移动端规范

## B 端 vs C 端（先分清）

| | B 端（管理后台） | C 端（终端用户） |
|---|---|---|
| 用户表 | SYS_USER | CLIENT_USER |
| 插件 | sys / biz | client |
| 登录 | `/auth/b/login` | `/auth/c/login`（见 AuthClientController） |
| 工具类 | StpUtil / StpLoginUserUtil | StpClientUtil / StpClientLoginUserUtil |
| 前端请求 | request.js baseRequest | clientRequest.js |
| 前端工程 | snowy-admin-web | 外部 H5/小程序/APP（uni-app） |

C 端接口路径规范：`/client/c/{业务域}/{动作}`（被 CLIENT_USER_PERMISSION_PATH_ARR 路由规则覆盖，走 C 端鉴权）。

## C 端模块结构（snowy-plugin-client）

```
client/modular/
├── user/        C 端用户（ClientUser，CLIENT_USER 表：账号/头像/昵称/状态等）
└── relation/    C 端关系（好友/关注等社交关系）
```

新增 C 端业务：在 client 插件 modular 下建域，Controller URL 用 `/client/c/{域}/{动作}`，注入用 StpClientUtil 取 C 端登录用户。结构规范与 biz 完全相同（六件套），只是类前缀 Client。

## C 端登录链路（auth 插件已实现）

```
POST /auth/c/login（账号密码 / 手机验证码 / 三方 token）
   → 校验 CLIENT_USER
   → StpClientUtil.login(id)（独立 StpLogic，与 B 端 token 隔离）
   → 返回 C 端 token
```

参考：`snowy-plugin-auth/src/main/java/vip/xiaonuo/auth/modular/login/`（AuthClientController + AuthServiceImpl 的 client 分支）。

## 前端 C 端请求（如需在 admin-web 调 C 端接口）

```js
import { clientRequest } from '@/utils/clientRequest'
// 用法与 baseRequest 相同，token 头与重定向逻辑按 C 端处理
```

实际 C 端页面通常在独立 uni-app 工程里（用 axios/fly 自行封装，token 头名 `token`， baseURL 指向后端 82 端口）。

## 移动端（snowy-plugin-mobile）

```
mobile/modular/
├── mobile/     移动端模块管理
└── resource/   移动端资源（菜单/按钮，MOBILE_RESOURCE 表）
```

- 作用：管理 uni-app 端的菜单与按钮权限（与 B 端 SYS_RESOURCE 平行的一套）
- 内置打包好的移动端静态资源：`snowy-plugin/snowy-plugin-mobile/src/main/resources/static/mobile/`
- 代码生成器可产出 mobile 代码：GenBasic 配置 mobileModule 后，sqlend 模板会生成 MOBILE_RESOURCE 的 INSERT（按钮码 `mobile{ClassName}Add` 形态）
- 完整 uni-app 前端工程在独立仓库（snowy 官方 mobile 工程），本仓库不含源码

## 常见错误正误对照

| ❌ | ✅ |
|---|---|
| C 端接口用 StpUtil 取用户 | StpClientUtil / StpClientLoginUserUtil（取错端会拿到 null） |
| C 端接口路径写 /client/b/... | `/client/c/...`（走 C 端路由规则） |
| CLIENT_USER 与 SYS_USER 混用 | 两套独立体系，外键别串 |
| 移动端按钮码用 B 端驼峰码 | mobile 前缀码（MOBILE_RESOURCE） |
| 在 B 端管理页调 clientRequest | B 端一律 baseRequest |

## 检查清单

- [ ] C 端接口前缀 /client/c/，取用户用 StpClientUtil 系
- [ ] C 端表用 CLIENT_ 前缀，类前缀 Client
- [ ] 移动端资源走 MOBILE_RESOURCE（生成器 mobileModule 配置）
- [ ] 两端 token 不混用

## 参考实现

| 文件 | 说明 |
|---|---|
| `snowy-plugin/snowy-plugin-client/src/main/java/vip/xiaonuo/client/modular/user/` | C 端用户六件套 |
| `snowy-plugin/snowy-plugin-auth/src/main/java/vip/xiaonuo/auth/modular/login/controller/AuthClientController.java` | C 端登录接口 |
| `snowy-plugin/snowy-plugin-mobile/src/main/java/vip/xiaonuo/mobile/modular/` | 移动端资源管理 |
| `snowy-admin-web/src/utils/clientRequest.js` | C 端请求封装 |
| `snowy-plugin/snowy-plugin-gen/src/main/resources/mobile/` | 移动端代码模板 |
| `snowy-plugin/snowy-plugin-gen/src/main/resources/sqlend/Mysql.sql.btl` | MOBILE_RESOURCE SQL 模板 |
