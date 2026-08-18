---
name: security-auth
description: Snowy 鉴权与安全规范：Sa-Token B/C 双端模型、路由白名单三段、登录方式、获取当前用户、接口权限与按钮权限、数据范围、三方登录、SSO。触发场景：1) 接口需要登录/免登录/权限校验 2) 获取当前登录用户信息 3) 数据范围（按机构过滤）4) 三方登录或 SSO 集成。触发词：鉴权、权限、登录、Sa-Token、StpUtil、token、白名单、免登录、401、403、数据范围、DataScope、角色、三方登录、SSO、OAuth2、越权。
---

# Snowy 鉴权与安全规范

## 双端模型（B 端 / C 端）

| 端 | 用户体系 | 登录工具类 | 接口前缀 |
|---|---|---|---|
| B 端（管理后台） | SYS_USER（sys 插件） | `StpUtil` / `StpLoginUserUtil.getLoginUser()` | `/sys/*` `/biz/*` `/dev/*` `/gen/*` |
| C 端（终端用户） | CLIENT_USER（client 插件） | `StpClientUtil` / `StpClientLoginUserUtil` | `/auth/c/**` `/client/c/**` |

两套独立 StpLogic（`AuthConfigure` 里注册 `stpLogic` 与 `stpClientLogic`），token 互不通用。**业务后台代码一律用 B 端 API**。

## 路由级白名单（GlobalConfigure，改后必须重启）

`snowy-web-app/src/main/java/vip/xiaonuo/core/config/GlobalConfigure.java`：

| 数组 | 作用 |
|---|---|
| `NO_LOGIN_PATH_ARR` | 免登录路径（验证码、登录接口、公开页） |
| `CLIENT_USER_PERMISSION_PATH_ARR` | C 端鉴权路径 |
| `SUPER_PERMISSION_PATH_ARR` | 仅超管角色可访问 |

新增公开接口 → 加 NO_LOGIN_PATH_ARR → 重启。漏加 = 401。

## 获取当前用户（业务代码高频）

```java
import vip.xiaonuo.auth.core.util.StpLoginUserUtil;

String userId = StpLoginUserUtil.getLoginUserId();          // 当前用户 id
SaBaseLoginUser user = StpLoginUserUtil.getLoginUser();      // 完整登录用户（name/orgId/...）
String orgId = StpLoginUserUtil.getLoginUser().getOrgId();
// SaBaseLoginUser 在 snowy-plugin-api/snowy-plugin-auth-api 的 core/pojo 下
```

自动填充的 createUser/updateUser 就是取自这里（MetaObjectHandler）。

## 接口权限 vs 按钮权限（两层，别混淆）

| 层 | 载体 | 形态 | 用法 |
|---|---|---|---|
| 接口权限 | 角色授权生成的数据范围 apiUrl | **URL 式** | 后端 `@SaCheckPermission("/biz/xxx/page")` |
| 按钮权限 | SYS_RESOURCE 的 BUTTON 行 code | **驼峰式** | 前端 `hasPerm('bizXxxAdd')` |

登录时（`SysLoginUserApiProvider`）组装：`permissionCodeList`（来自 dataScope 的 apiUrl）+ `buttonCodeList`（驼峰码下发前端）。所以：
- 新接口上线 → 角色管理授权对应资源，否则非超管 403
- 新按钮 → SYS_RESOURCE 加 BUTTON 行 + 授权，否则前端不显示

## 数据范围（DataScope）

- 用户可被授权"某模块/某菜单下、某机构的全部/自定义/仅本部门/仅本人"数据范围（SYS_USER_DATA_SCOPE 表，角色管理→数据授权界面配置）
- 后端查询受数据范围过滤的实现走 sys 插件的 ApiUrl 清单机制：数据范围授权时选择"接口地址集合"
- **业务表要支持数据范围**：确保查询走标准 QueryWrapper 链路；机构维度字段命名 orgId（varchar，SYS_ORG 外键）
- 手动忽略数据权限的场景参考 DataPermissionHelper 类似机制（以 sys 插件实现为准）

## 登录方式矩阵（auth 插件已实现，直接用）

账密（密码 SM2 加密传输 + SM3 摘要校验）、手机验证码、邮箱验证码、OTP 动态口令、三方 token 换绑。入口：`snowy-plugin-auth/modular/login/controller/AuthController.java`（B 端 `/auth/b/*`）与 `AuthClientController`（C 端 `/auth/c/*`）。

## 三方登录与 SSO

- 三方登录：JustAuth（`auth/modular/third/`），支持微信/钉钉/企微/飞书/QQ/微博/Gitee 等；配置在 DEV_CONFIG
- SSO/OAuth2/OIDC/CAS/SAML：`auth/core/protocol/`（服务端与客户端协议栈）；配置 `AuthSsoConfigure`
- 业务一般只做"绑定/解绑三方账号"，协议栈不用动

## 安全检查清单

- [ ] 新公开接口已加 NO_LOGIN_PATH_ARR 并重启
- [ ] @SaCheckPermission 值 = URL；新资源已授权给角色
- [ ] 涉及用户数据的接口校验归属（防水平越权：编辑/删除前确认记录属于当前用户机构）
- [ ] 密码类字段不落日志、不明文存储（见 crypto-sm）
- [ ] 敏感查询参数防注入（checkSqlInjection）

## 常见错误正误对照

| ❌ | ✅ |
|---|---|
| `@SaCheckPermission("biz:xxx:list")` | `@SaCheckPermission("/biz/xxx/page")` |
| Controller 里手写 `StpUtil.getLoginId()` 判空跳转 | 白名单/注解体系（GlobalConfigure + @SaCheckLogin 系） |
| B 端接口用 StpClientUtil | B 端 StpUtil / StpLoginUserUtil |
| 新菜单配好但用户 403 | 角色管理未授权（接口权限来自资源授权） |
| 自己写 token 解析 | Sa-Token 体系（token 名 `token`，请求头） |

## 参考实现

| 文件 | 说明 |
|---|---|
| `snowy-web-app/src/main/java/vip/xiaonuo/core/config/GlobalConfigure.java` | 路由白名单 + SaServletFilter |
| `snowy-plugin/snowy-plugin-auth/src/main/java/vip/xiaonuo/auth/core/config/AuthConfigure.java` | 双端 StpLogic + StpInterfaceImpl |
| `snowy-plugin/snowy-plugin-sys/src/main/java/vip/xiaonuo/sys/modular/user/provider/SysLoginUserApiProvider.java` | 权限码/按钮码/数据范围组装（refreshOnlineUserPermission） |
| `snowy-plugin/snowy-plugin-auth/src/main/java/vip/xiaonuo/auth/modular/login/` | 登录全流程 |
| `snowy-plugin-api/snowy-plugin-auth-api/src/main/java/vip/xiaonuo/auth/core/pojo/SaBaseLoginUser.java` | 登录用户 POJO |
| `snowy-plugin/snowy-plugin-auth/src/main/java/vip/xiaonuo/auth/modular/third/` | 三方登录 |
| `snowy-plugin/snowy-plugin-auth/src/main/java/vip/xiaonuo/auth/core/protocol/` | SSO 协议栈 |
