---
name: api-development
description: Snowy 接口设计规范：URL 命名、参数与返回、CommonResult、CommonException 异常体系、Knife4j 文档、白名单与权限。触发场景：1) 设计新接口或修改接口 2) 处理统一返回/异常/错误码 3) 接口需要免登录或加权限。触发词：API、接口、RESTful、URL、Controller、CommonResult、CommonException、异常、错误码、doc.html、Knife4j、Swagger、白名单。注意：CRUD 全套代码模板见 crud-development；Sa-Token 体系详解见 security-auth。
---

# Snowy API 开发规范

## URL 命名规范

格式：`/{插件}/{业务域}/{动作}`，**动词式全路径直接写在方法注解上**，类上不写 @RequestMapping。

| 动作 | HTTP | 路径示例 | 参数形式 |
|---|---|---|---|
| 分页 | GET | `/biz/xxx/page` | Param 对象（无 @RequestBody） |
| 列表(不分页) | GET | `/biz/xxx/list` | Param 对象 |
| 详情 | GET | `/biz/xxx/detail` | `@Valid XxxIdParam` |
| 添加 | POST | `/biz/xxx/add` | `@RequestBody @Valid XxxAddParam` |
| 编辑 | POST | `/biz/xxx/edit` | `@RequestBody @Valid XxxEditParam` |
| 删除 | POST | `/biz/xxx/delete` | `@RequestBody @Valid @NotEmpty List<XxxIdParam>` |
| 业务动作 | POST | `/biz/xxx/disableStatus` | `@RequestBody @Valid XxxIdParam` |
| 上传 | POST | `/biz/xxx/upload` | `@RequestPart("file") MultipartFile` |
| 按 ID 批量查询 | POST | `/biz/xxx/getAncestorNodes` | `@RequestBody List<String>` 或 `@RequestBody @Valid XxxIdListParam` |
| 导出选中 | POST | `/biz/xxx/exportData` | `@RequestBody List<XxxIdParam>` + HttpServletResponse（生成器模板） |
| 导出 | GET | `/biz/xxx/export` | void + HttpServletResponse |
| 下载 | GET | `/biz/xxx/download` | void + HttpServletResponse |

规则：
- **查询 GET + Param 对象**（Spring 自动绑定 query string），**写入 POST + @RequestBody @Valid**（例外见表中上传、按 ID 批量查询、导出选中）
- 禁止 RESTful 路径变量（`/user/{id}`）、PUT/DELETE 方法
- 插件前缀：sys / biz / dev / gen / mobile，自建业务插件用自己的缩写（如 `/order/**`）；C 端接口 auth 插件用 `/auth/c/**`、client 插件用 `/client/c/**`

## 统一返回 CommonResult

```java
// snowy-common/src/main/java/vip/xiaonuo/common/pojo/CommonResult.java
CommonResult.data(obj)          // 成功带数据：{code:200, msg:"操作成功", data:obj, traceId:...}
CommonResult.ok()               // 成功无数据（写操作）
CommonResult.error("消息")       // 失败（一般不直接用，业务失败应抛 CommonException）
CommonResult.get(code, msg, data)  // 底层构建
```

- Controller 方法返回类型一律 `CommonResult<T>`；例外：文件流/导出/下载为 `void` + `HttpServletResponse` 参数（参考 SysUserController.exportUser）；SSO 协议回调（AuthSsoController.logoutCall/pushClient）按 Sa-Token SSO 协议返回 Object，属于平台协议接口，业务代码不要模仿
- 禁止返回 Map<String,Object>、裸 Entity 列表不带包装、R/AjaxResult/Result 等其他包装类

## 异常体系

```java
// 业务校验失败 —— 抛 CommonException（支持 {} 占位符格式化，消息中文）
throw new CommonException("存在重复的账号，账号为：{}", account);
throw new CommonException("XXX不存在，id值为：{}", id);

// 固定提示文案：用插件级异常枚举取文案（参考 auth 插件 AuthExceptionEnum）
throw new CommonException(BizXxxExceptionEnum.XXX.getValue());
// 需要指定错误码时用三参构造器（CommonException 只有无参、(msg, args)、(code, msg, args) 三个构造器，不能直接传枚举）
throw new CommonException(CommonExceptionEnum.ERROR403.getCode(), "无权操作该数据");
```

- 禁止 `RuntimeException`、`IllegalArgumentException`、`ServiceException` 直接抛出
- 全局异常处理在 `snowy-web-app/src/main/java/vip/xiaonuo/core/handler/GlobalExceptionHandler.java`（@ControllerAdvice → GlobalExceptionUtil.getCommonResult(e)），业务代码不要自己 try-catch 后返回错误 CommonResult
- @Valid 校验 Param 对象失败时（MethodArgumentNotValidException / BindException），由全局处理器返回 415 + 注解里的中文 message，不需要手写校验返回
- 方法参数上的约束（如删除接口的 `@NotEmpty List<…>`）和列表元素的级联校验（`@Valid List<XxxIdParam>` 里每个 id 的 `@NotBlank`）抛出 jakarta.validation.ConstraintViolationException，同样由全局处理器返回 415 + 中文 message；前提是 `@Valid` 必须 import `jakarta.validation.Valid`，javax 版不会触发级联校验

## 文档注解（Knife4j / OpenAPI3）

```java
@Tag(name = "XXX控制器")                    // 类级
@Operation(summary = "获取XXX分页")          // 方法级
@Schema(description = "标题")                // 字段级（Param/Entity/Result）
@Schema(description = "标题", requiredMode = Schema.RequiredMode.REQUIRED)  // 必填字段
```

接口文档地址：`http://localhost:82/doc.html`（basic 认证 snowyAdmin / Snowy@2026!，以 application.properties 的 `knife4j.basic.*` 为准，2026-08-20 之前为 admin/123456；按插件分 7 组）。所有 description 用中文。

## 权限注解

```java
@SaCheckPermission("/biz/xxx/page")     // 值 = 接口 URL（不是冒号式权限码！）
```

- 用户能通过校验的条件：用户直接授权 + 所属角色授权的**接口权限**（角色管理 / 用户管理 → **授权权限**，存 SYS_RELATION 的 SYS_USER_HAS_PERMISSION / SYS_ROLE_HAS_PERMISSION）里包含该 URL；"授权资源"只控制菜单和按钮（SYS_ROLE_HAS_RESOURCE），不产生接口权限，不能替代
- 超管（角色码 `superAdmin`）**不会**自动通过 `@SaCheckPermission`：出厂 SQL 只给超管显式授权了已有 biz 接口；代码生成器"项目内生成"时会自动给超管授权生成的 URL；**手写的新接口必须到 角色管理 → 授权权限 给超管勾选，否则超管也 403**
- `SUPER_PERMISSION_PATH_ARR` 是路由级"仅超管可访问"名单（`StpUtil.checkRole("superAdmin")`），与 @SaCheckPermission 无关；平台插件（sys/dev 等）不用 `@SaCheckPermission`，靠它做路由级超管校验

## 免登录 / 白名单

路由级放行集中在 `snowy-web-app/src/main/java/vip/xiaonuo/core/config/GlobalConfigure.java`：

| 数组 | 含义 | 修改时机 |
|---|---|---|
| `NO_LOGIN_PATH_ARR` | 免登录路径 | 新增公开接口（如验证码、健康检查）时加这里 |
| `CLIENT_USER_PERMISSION_PATH_ARR` | C 端鉴权路径（/auth/c/**、/client/c/**） | 新增 C 端接口模块时 |
| `SUPER_PERMISSION_PATH_ARR` | 仅超管可访问路径 | 新增敏感管理接口时 |

改完白名单必须重启后端生效。

## 防重复提交

```java
@CommonNoRepeat                          // snowy-common 注解，写操作防重复提交
@PostMapping("/biz/xxx/add")
public CommonResult<String> add(...) { ... }
```

关键写操作（下单、支付类）建议加；普通 CRUD 可不加（Controller 已有 @Validated + 前端按钮防抖）。

## 常见错误正误对照

| 错误 | 正确 |
|---|---|
| `@RequestMapping("/biz/xxx")` + `@GetMapping("/{id}")` | 方法上直接 `@GetMapping("/biz/xxx/detail")` |
| `@PutMapping` / `@DeleteMapping` | 全部用 `@PostMapping` |
| `public Map<String, Object> detail(...)` | `public CommonResult<BizXxx> detail(...)` |
| `try { ... } catch (Exception e) { return CommonResult.error(e.getMessage()); }` | 直接抛 CommonException，交给全局处理器 |
| `throw new RuntimeException("xxx不存在")` | `throw new CommonException("xxx不存在，id值为：{}", id)` |
| 白名单写死在 Controller 里判断 | 统一改 GlobalConfigure 的 NO_LOGIN_PATH_ARR |

## 检查清单

- [ ] URL = /{插件}/{域}/{动作}，动词式，写方法注解上
- [ ] 查询 GET+Param / 写入 POST+@RequestBody @Valid（上传、按 ID 批量查询、导出选中按 URL 表的例外写法）
- [ ] 返回 CommonResult<T>（文件流 void 除外）
- [ ] 新接口已在 角色管理 → 授权权限 给需要的角色（含超管）勾选
- [ ] @SaCheckPermission 值与 URL 完全一致
- [ ] @Tag/@Operation/@Schema 中文齐全
- [ ] 业务异常用 CommonException，无裸 RuntimeException
- [ ] 新公开接口已加 GlobalConfigure 白名单并说明需重启

## 参考实现

| 文件 | 说明 |
|---|---|
| `snowy-plugin/snowy-plugin-biz/src/main/java/vip/xiaonuo/biz/modular/notice/controller/BizNoticeController.java` | 标准 Controller 范本 |
| `snowy-plugin/snowy-plugin-sys/src/main/java/vip/xiaonuo/sys/modular/user/controller/SysUserController.java` | 含导出/下载 void 场景 |
| `snowy-common/src/main/java/vip/xiaonuo/common/pojo/CommonResult.java` | 统一返回体 |
| `snowy-common/src/main/java/vip/xiaonuo/common/exception/CommonException.java` | 业务异常 |
| `snowy-common/src/main/java/vip/xiaonuo/common/enums/CommonExceptionEnum.java` | 错误码枚举 |
| `snowy-web-app/src/main/java/vip/xiaonuo/core/handler/GlobalExceptionHandler.java` | 全局异常处理 |
| `snowy-web-app/src/main/java/vip/xiaonuo/core/config/GlobalConfigure.java` | 路由白名单 |
| `snowy-web-app/src/main/java/vip/xiaonuo/core/config/Knife4jConfigure.java` | 接口文档分组 |
