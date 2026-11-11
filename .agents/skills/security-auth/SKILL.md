---
name: security-auth
description: Snowy 鉴权与安全规范：Sa-Token B/C 双端模型、路由白名单数组、登录方式、获取当前用户、接口权限与按钮权限、数据范围、三方登录、SSO。触发场景：1) 接口需要登录/免登录/权限校验 2) 获取当前登录用户信息 3) 数据范围（按机构过滤）4) 三方登录或 SSO 集成。触发词：鉴权、权限、登录、Sa-Token、StpUtil、token、白名单、免登录、401、403、数据范围、DataScope、角色、三方登录、SSO、OAuth2、越权。
---

# Snowy 鉴权与安全规范

## 双端模型（B 端 / C 端）

| 端 | 用户体系 | 登录工具类 | 接口前缀 |
|---|---|---|---|
| B 端（管理后台） | SYS_USER（sys 插件） | `StpUtil` / `StpLoginUserUtil.getLoginUser()` | `/sys/*` `/biz/*` `/dev/*` `/gen/*` |
| C 端（终端用户） | CLIENT_USER（client 插件） | `StpClientUtil` / `StpClientLoginUserUtil` | `/auth/c/**` `/client/c/**` `/client/userCenter/**` |

出厂 `ClientUserCenterController`（`/client/userCenter/*`）是历史命名，已登记在 `CLIENT_USER_PERMISSION_PATH_ARR`，走 C 端登录校验（其中找回密码相关接口在免登录名单里）；新 C 端接口仍一律用 `/client/c/{域}/{动作}`，不要再起类似命名。`/client/user/*` 是 B 端管理 C 端用户的接口。

两套独立 StpLogic（`AuthConfigure` 里注册 `stpLogic` 与 `stpClientLogic`），token 互不通用。**业务后台代码一律用 B 端 API**。

## 路由级白名单（GlobalConfigure，改后必须重启）

`snowy-web-app/src/main/java/vip/xiaonuo/core/config/GlobalConfigure.java`：

| 数组 | 作用 |
|---|---|
| `NO_LOGIN_PATH_ARR` | 免登录路径（验证码、登录接口、C 端退出、公开页）；移动端只放行静态图片 `/mobile/swiper/**`、`/mobile/fileImg/**`，禁止再用 `/mobile/**` 这类整段通配 |
| `CLIENT_USER_PERMISSION_PATH_ARR` | C 端鉴权路径（`/auth/c/**`、`/client/c/**`、`/client/userCenter/**`），校验通过后自动续期 C 端 token |
| `SUPER_PERMISSION_PATH_ARR` | 仅超管角色可访问（路由级 `StpUtil.checkRole("superAdmin")`，与 `@SaCheckPermission` 无关） |
| `NO_LOCK_CHECK_PATH_ARR` | 锁屏期间仍放行的 B 端接口（解锁、退出登录、登录态查询），其余接口锁屏时一律拒绝；新增锁屏期间也要能调的接口时加在这里 |

新增公开接口 → 加 NO_LOGIN_PATH_ARR → 重启。漏加 = 401。

## 获取当前用户（业务代码高频）

```java
import vip.xiaonuo.auth.core.util.StpLoginUserUtil;

String userId = StpLoginUserUtil.getLoginUser().getId();          // 当前用户 id
SaBaseLoginUser user = StpLoginUserUtil.getLoginUser();      // 完整登录用户（name/orgId/...）
String orgId = StpLoginUserUtil.getLoginUser().getOrgId();
// SaBaseLoginUser 在 snowy-plugin-api/snowy-plugin-auth-api 的 core/pojo 下
```

自动填充的 createUser/updateUser 由 `GlobalConfigure.CustomMetaObjectHandler` 写入：先取 `StpUtil.getLoginIdAsString()`（B 端用户 id），B 端未登录时回退 `StpClientUtil.getLoginIdAsString()`（C 端用户 id），都取不到写 `-1`。

## 接口权限 vs 按钮权限（两层，别混淆）

| 层 | 载体 | 形态 | 用法 |
|---|---|---|---|
| 接口权限 | 用户直接授权 + 所属角色授权的**接口权限**（角色管理 / 用户管理 → **授权权限**，存 SYS_RELATION 的 SYS_USER_HAS_PERMISSION / SYS_ROLE_HAS_PERMISSION） | **URL 式** | 后端 `@SaCheckPermission("/biz/xxx/page")`，值 = 接口 URL |
| 按钮权限 | SYS_RESOURCE 的 BUTTON 行 code（角色管理 / 用户管理 → **授权资源**，存 SYS_ROLE_HAS_RESOURCE / SYS_USER_HAS_RESOURCE） | **驼峰式** | 前端 `hasPerm('bizXxxAdd')` |

"授权资源"只控制菜单和按钮，**不产生接口权限**。登录时由 `AuthServiceImpl.execLoginB` 调用 `SysLoginUserApiProvider` 取数并组装：`permissionCodeList`（来自 dataScope 的 apiUrl）+ `buttonCodeList`（驼峰码下发前端）；授权变更后由 `SysLoginUserApiProvider.refreshOnlineUserPermission` 刷新在线用户。所以：
- 新接口上线 → 角色管理 → **授权权限** 勾选该接口 URL，否则 403
- 超管（角色码 `superAdmin`）**不会**自动通过 `@SaCheckPermission`：出厂 SQL 只给超管显式授权了已有 biz 接口；代码生成器以"生成到项目内"方式生成时会自动给超管授权生成的 URL（下载压缩包方式不会）；**手写的新接口必须到 角色管理 → 授权权限 给超管勾选，否则超管也 403**。平台插件（sys/dev 等）不用 `@SaCheckPermission`，靠 GlobalConfigure 的路由级超管校验
- 新按钮 → SYS_RESOURCE 加 BUTTON 行 + 授权资源，否则前端不显示

## 数据范围（DataScope）

- 用户可按接口被授权数据范围（全部 / 仅自己 / 仅所属组织 / 所属组织及以下 / 自定义组织，对应 SysRoleDataScopeCategoryEnum 的 SCOPE_ALL/SELF/ORG/ORG_CHILD/ORG_DEFINE）：入口为角色管理 → 授权 → **授权权限**（或用户管理 → 授权权限）；原始授权存 SYS_RELATION（category = SYS_ROLE_HAS_PERMISSION / SYS_USER_HAS_PERMISSION，范围放 EXT_JSON）；SYS_USER_DATA_SCOPE / SYS_USER_DATA_SCOPE_MAP 是系统计算出的预计算结果表，**业务代码不要直接写**
- 后端查询受数据范围过滤的实现走 sys 插件的 ApiUrl 清单机制：数据范围授权时选择"接口地址集合"
- **业务表要支持数据范围**：平台没有自动拦截器，只用 QueryWrapper **不会过滤**。必须在 Service 里显式调用 `List<String> scope = StpLoginUserUtil.getLoginUserDataScope();`：`null` = 全部数据，不加条件；空集合 = 无授权机构，返回空页（参考 BizPositionServiceImpl）或仅本人（参考 BizUserServiceImpl）；非空 = `CommonSqlUtil.safeIn(queryWrapper.lambda(), BizXxx::getOrgId, scope)`（也可用 `CommonSqlUtil.scopeIn` 走预计算表子查询，参考 BizOrgServiceImpl）。新增、编辑、删除前同样要校验 orgId 在 scope 内。该方法按当前请求路径匹配已授权的 apiUrl，只能在被授权接口的调用链里使用
- 机构维度字段命名 orgId（ORG_ID varchar(20)，逻辑关联 SYS_ORG.ID，不建数据库外键）
- 数据范围是显式过滤（见上条），没有需要"忽略"的自动机制；某个查询不需要数据范围时，不调用 `getLoginUserDataScope()` 即可

## 登录方式矩阵（auth 插件已实现，直接用）

账密（SM2 传输 + SM3 校验）、手机验证码、邮箱验证码、OTP 动态口令（B/C 两端都有）；第三方 Token 交换登录 `/auth/b/doLoginByThirdToken`（iframe 嵌入免登，只有 B 端）。三方账号绑定见下节 `/auth/third/*`。入口：`snowy-plugin-auth/modular/login/controller/AuthController.java`（B 端 `/auth/b/*`）与 `AuthClientController`（C 端 `/auth/c/*`）。

## 三方登录与 SSO

- 三方登录：`auth/modular/third/` + `auth/core/protocol/`（自研认证源客户端，由 `AuthClientFactory` 创建，复用 JustAuth 的 AuthCallback/AuthUser 模型），支持 OAuth2/OIDC/JWT/CAS/SAML 标准协议认证源，以及 IAM、微信、微信小程序、钉钉、企业微信、飞书、WeLink、云之家、QQ、微博、抖音、支付宝；配置在 DEV_CONFIG（`SNOWY_THIRD_*` 键）
- SSO：`auth/modular/sso/`（Sa-Token SSO 客户端，配置 `AuthSsoConfigure`，接口 `/auth/sso/b/*`）；`auth/core/protocol/` 是三方登录的认证源客户端，**不是** SSO 协议栈，平台不提供认证服务端
- 业务一般只用 `/auth/third/*`（render / callback / bindAccount / page）做三方账号登录与绑定，认证源客户端不用动

## 安全检查清单

- [ ] 新公开接口已加 NO_LOGIN_PATH_ARR 并重启
- [ ] @SaCheckPermission 值 = URL；新接口已在 角色管理 → 授权权限 授权给角色（超管也要勾），新按钮已授权资源
- [ ] 涉及用户数据的接口校验归属（防水平越权：编辑/删除前确认记录属于当前用户机构）
- [ ] 密码和敏感字段不落日志、不明文存储：注意 `@CommonLog` 会把整个入参原样写入 DEV_LOG.PARAM_JSON，入参含口令、证件号、手机号的写接口，加 @CommonLog 前要确认这些字段已经是密文（如 SM2 密文口令），或已从 Param 中剔除（见 crypto-sm）
- [ ] 敏感查询参数防注入（checkSqlInjection）

## 常见错误正误对照

| 错误 | 正确 |
|---|---|
| `@SaCheckPermission("biz:xxx:list")` | `@SaCheckPermission("/biz/xxx/page")` |
| Controller 里手写 `StpUtil.getLoginId()` 判空跳转 | 白名单/注解体系（GlobalConfigure + @SaCheckLogin 系） |
| B 端接口用 StpClientUtil | B 端 StpUtil / StpLoginUserUtil |
| 新菜单配好但用户 403 | 角色管理 → 授权权限 未勾选该接口 URL（接口权限来自"授权权限"，"授权资源"只管菜单和按钮；超管同样需要） |
| 自己写 token 解析 | Sa-Token 体系（token 名 `token`，请求头） |

## 参考实现

| 文件 | 说明 |
|---|---|
| `snowy-web-app/src/main/java/vip/xiaonuo/core/config/GlobalConfigure.java` | 路由白名单 + SaServletFilter |
| `snowy-plugin/snowy-plugin-auth/src/main/java/vip/xiaonuo/auth/core/config/AuthConfigure.java` | 双端 StpLogic + StpInterfaceImpl |
| `snowy-plugin/snowy-plugin-sys/src/main/java/vip/xiaonuo/sys/modular/user/provider/SysLoginUserApiProvider.java` | 权限码/按钮码/数据范围取数，在线用户权限刷新（refreshOnlineUserPermission） |
| `snowy-plugin/snowy-plugin-auth/src/main/java/vip/xiaonuo/auth/modular/login/` | 登录全流程（`AuthServiceImpl.execLoginB` 组装登录用户） |
| `snowy-plugin-api/snowy-plugin-auth-api/src/main/java/vip/xiaonuo/auth/core/util/StpLoginUserUtil.java` | 当前用户、数据范围（getLoginUserDataScope） |
| `snowy-plugin-api/snowy-plugin-auth-api/src/main/java/vip/xiaonuo/auth/core/pojo/SaBaseLoginUser.java` | 登录用户 POJO |
| `snowy-plugin/snowy-plugin-auth/src/main/java/vip/xiaonuo/auth/modular/third/` | 三方登录 |
| `snowy-plugin/snowy-plugin-auth/src/main/java/vip/xiaonuo/auth/core/protocol/` | 三方/标准协议认证源客户端 |
| `snowy-plugin/snowy-plugin-auth/src/main/java/vip/xiaonuo/auth/modular/sso/` | SSO 客户端 |
