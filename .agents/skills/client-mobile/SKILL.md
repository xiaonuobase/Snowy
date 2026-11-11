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
| 登录 | `/auth/b/doLogin` | `/auth/c/doLogin`（见 AuthClientController） |
| 工具类 | StpUtil / StpLoginUserUtil | StpClientUtil / StpClientLoginUserUtil |
| 前端请求 | request.js `baseRequest` | clientRequest.js `clientBaseRequest` |
| 前端工程 | snowy-admin-web | snowy-admin-web 内置 C 端登录、注册、找回密码和前台页（views/auth/client、views/front）；完整的移动端在外部 uni-app 工程 |

C 端接口路径规范：`/client/c/{业务域}/{动作}`（被 CLIENT_USER_PERMISSION_PATH_ARR 路由规则覆盖，走 C 端鉴权）。出厂 `/client/userCenter/*` 是历史路径，不在这条规则覆盖范围内，实际走 B 端登录校验，新接口**不要模仿**。

## C 端模块结构（snowy-plugin-client）

```
client/modular/
├── user/        C 端用户（ClientUser，CLIENT_USER 表：账号/头像/昵称/状态等）
└── relation/    C 端通用关系表（对象-目标-分类-扩展 JSON，仿 SYS_RELATION；出厂只有 USER_TEST 测试分类，
                 没有对外接口，好友、关注等需要自行扩展分类）
```

新增 C 端业务：在 client 插件 modular 下建域，Controller URL 用 `/client/c/{域}/{动作}`，注入用 StpClientUtil 取 C 端登录用户。结构规范与 biz 完全相同（六件套），只是类前缀 Client。

## C 端登录链路（auth 插件已实现）

```
POST /auth/c/doLogin（账号密码）、/auth/c/doLoginByPhone、/auth/c/doLoginByEmail、/auth/c/doLoginByOtp
   （账号密码 / 手机验证码 / 邮箱验证码 / OTP 动态口令；第三方 Token 交换登录只有 B 端提供）
   → 校验 CLIENT_USER
   → StpClientUtil.login(id)（独立 StpLogic，与 B 端 token 隔离）
   → 返回 C 端 token
```

参考：`snowy-plugin-auth/src/main/java/vip/xiaonuo/auth/modular/login/`（AuthClientController + AuthServiceImpl 的 client 分支）。

## 前端 C 端请求（如需在 admin-web 调 C 端接口）

```js
import { clientBaseRequest } from '@/utils/clientRequest'
const request = (url, ...arg) => clientBaseRequest('/client/c/xxx/' + url, ...arg)
// 用法与 baseRequest 相同，token 头与重定向逻辑按 C 端处理（读取/失效时清理 CLIENT_TOKEN）
```

（出厂 clientLoginApi.js、clientUserCenterApi.js 仍 import baseRequest，clientBaseRequest 暂时没有调用方）

实际移动端页面在独立 uni-app 工程（生成器 mobile 模板就是面向它的：请求用该工程的 `@/utils/request`，形如 `request({ url, method, data })`；token 头名 `token`，后端 82 端口）。

## 移动端（snowy-plugin-mobile）

```
mobile/modular/
├── mobile/     移动端模块管理
└── resource/   移动端资源（菜单/按钮，MOBILE_RESOURCE 表）
```

- 作用：管理 uni-app 端的菜单与按钮权限（与 B 端 SYS_RESOURCE 平行的一套）
- 内置移动端配套图片（文件类型图标、轮播图）：`snowy-plugin/snowy-plugin-mobile/src/main/resources/static/mobile/`，不含移动端打包产物
- 代码生成器可产出 mobile 代码：GenBasic 配置 mobileModule 后，sqlend 模板会生成 MOBILE_RESOURCE 的 INSERT（按钮码 `mobile{ClassName}Add` 形态）
- 完整 uni-app 前端工程不在本仓库（本仓库只有生成器的 mobile 模板），工程地址向团队确认

## 常见错误正误对照

| 错误 | 正确 |
|---|---|
| C 端接口用 StpUtil 取用户 | StpClientUtil / StpClientLoginUserUtil（取错端会抛 Sa-Token 未登录/Token 无效异常，接口报 401 或错误，而不是拿到 null） |
| C 端接口路径写 /client/b/... | `/client/c/...`（走 C 端路由规则） |
| CLIENT_USER 与 SYS_USER 混用 | 两套独立体系，外键别串 |
| 移动端按钮码用 B 端驼峰码 | mobile 前缀码（MOBILE_RESOURCE） |
| 在 B 端管理页调 clientBaseRequest | B 端一律 baseRequest |

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
