---
name: plugin-architecture
description: Snowy 插件化架构与跨插件调用规范：Maven 模块拓扑、插件内 core/modular 两层结构、plugin-api 解耦三步法、新建插件 checklist。触发场景：1) 需要调用其他插件的功能（如 biz 要用 sys 的用户）2) 划分新业务该放哪个模块 3) 新建插件或新的 *-api 接口。触发词：插件、plugin、模块划分、跨模块、跨插件、api 模块、provider、解耦、依赖、新建插件。注意：单个业务域内的 CRUD 分层见 crud-development；本技能讲"模块之间怎么组织与通信"。
---

# Snowy 插件化架构规范

## Maven 模块拓扑

```
依赖方向（箭头 = 依赖）：

snowy-web-app（启动模块，聚合一切）
    ├── snowy-plugin-{sys|auth|dev|gen|client|mobile|biz}
    │       └── snowy-common（公共基础，人人依赖）
    └── snowy-plugin-api/{x}-api（接口层，被调用方与调用方共同依赖）

关键规则：
- 插件实现模块（snowy-plugin/*）之间【禁止】互相依赖
- 插件要调用其他插件 → 双方都只依赖对方的 *-api 模块
- 公共代码（工具、注解、基类）下沉到 snowy-common
```

## 真实调用地图（提取自各插件 pom）

A→B = A 依赖 B 的 `*-api`（运行时 B 的 provider 实现注入 A）：

| 调用方 | 依赖的 api | 典型用途 |
|---|---|---|
| **biz**（二开主战场） | sys-api、auth-api、dev-api | 用户/登录态（SaBaseLoginUser）/字典配置文件消息 |
| auth | sys-api、dev-api、client-api | 登录验用户、发验证码、C 端用户 |
| sys | auth-api、dev-api、mobile-api | 登录用户 POJO/在线 token、字典翻译、移动按钮码 |
| dev | sys-api、auth-api | 日志关联用户、监听器类型 |
| mobile | auth-api、dev-api、sys-api | 移动资源引用用户/字典 |
| client | auth-api、dev-api | C 端登录态与工具能力 |
| gen | sys-api、mobile-api | 生成时读模块/菜单、写移动资源 |

- **sys↔dev、auth↔sys "互相调用"但实现层零循环**——互依的只是 api 接口模块；编译期只见接口，运行期 Spring 把 provider 的 @Service 注入调用方
- 反向通知（被调方 → 调用方）不走依赖，走 `CommonDataChangeEventCenter` 事件（见 platform-extension 技能）
- gen-api 是空壳；ClientUserApi 在 `vip.xiaonuo.client` 包根（不在 api 子包）

| 模块 | 职责 | 二次开发可否修改 |
|---|---|---|
| `snowy-common` | CommonResult/CommonException/CommonEntity/工具类/国密/缓存操作器 | 原则上不动 |
| `snowy-plugin-api/*-api` | 跨插件调用接口定义（纯接口 + 少量 POJO） | 可新增接口 |
| `snowy-plugin-sys` | B 端系统：用户/组织/角色/资源/数据范围 | 原则上不动 |
| `snowy-plugin-auth` | 登录鉴权/SSO/三方登录 | 原则上不动 |
| `snowy-plugin-dev` | 开发工具：字典/配置/文件/定时任务/日志/短信/邮件/推送 | 原则上不动 |
| `snowy-plugin-gen` | 代码生成器 | 原则上不动 |
| `snowy-plugin-client` | C 端用户体系 | 原则上不动 |
| `snowy-plugin-mobile` | 移动端资源管理 | 原则上不动 |
| **`snowy-plugin-biz`** | **业务插件——二次开发主战场** | ★ 随便写 |
| `snowy-web-app` | 启动 + 全局配置（白名单/异常处理） | 只改配置类 |

## 插件内两层结构

```
vip.xiaonuo.biz
├── core/                          插件级基础设施（每插件固定有）
│   ├── config/                    插件配置类
│   ├── enums/                     插件级枚举
│   ├── listener/                  数据变更监听（如 BizDataChangeListener）
│   ├── timer/                     定时任务入口
│   └── util/                      插件级工具
└── modular/                       业务域（一个域一个目录）
    └── {域名}/
        ├── controller/  entity/  enums/  mapper/(+mapping/)
        ├── param/  result/  service/(+impl/)  provider/
```

新业务域默认放 `snowy-plugin-biz` 的 `modular/{域名}` 下（参考 notice 域）；只有业务规模大到需要独立插件时才新建插件。

## 跨插件调用三步法（硬性规范）

**场景**：biz 插件的"订单"功能需要根据 userId 拿用户名称。

**第 1 步**：接口定义放被调方的 api 模块（sys 已有大量现成接口，先查再用）：

```java
// snowy-plugin-api/snowy-plugin-sys-api/src/main/java/vip/xiaonuo/sys/api/SysUserApi.java
public interface SysUserApi {

    /**
     * 根据用户id获取用户对象，没有则返回null
     *
     * @author xuyuxiang
     * @date 2022/6/20 18:19
     **/
    JSONObject getUserByIdWithoutException(String userId);

    List<JSONObject> getUserListByIdListWithoutException(List<String> userIdList);
    JSONObject getUserByIdWithException(String userId);       // 没有则抛异常版本
    List<JSONObject> getUserListByIdWithException(List<String> userIdList);
}
```

**第 2 步**：被调插件 provider 实现（@Service）：

```java
// snowy-plugin-sys/.../modular/user/provider/SysUserApiProvider.java
@Service
public class SysUserApiProvider implements SysUserApi {

    @Resource
    private SysUserService sysUserService;

    @Override
    public JSONObject getUserByIdWithoutException(String userId) {
        SysUser sysUser = sysUserService.getById(userId);
        return JSONUtil.parseObj(sysUser);      // Entity → JSONObject 返回
    }
    // ...
}
```

**第 3 步**：调用方只依赖 *-api 模块，注入接口用：

```java
// snowy-plugin-biz 的 pom.xml 加依赖：
// <dependency>
//     <groupId>vip.xiaonuo</groupId>
//     <artifactId>snowy-plugin-sys-api</artifactId>
// </dependency>

@Service
public class BizOrderServiceImpl ... {

    @Resource
    private SysUserApi sysUserApi;              // 注入接口，不注入 sys 的实现

    public void xxx(String userId) {
        JSONObject user = sysUserApi.getUserByIdWithoutException(userId);
        String name = user.getStr("name");
    }
}
```

### 为什么返回 JSONObject 而不是实体类

被调方的 Entity 类在实现模块里，调用方不能依赖它（否则插件耦合）。JSONObject（hutool）是双方都能见的 neutral 类型。取字段用 `jsonObject.getStr("name")` / `getInt("age")`。

## 现成 API 速查（先查再用，不要重复造）

| api 模块 | 内容 |
|---|---|
| `snowy-plugin-api/snowy-plugin-sys-api` | SysUserApi/SysRoleApi/SysOrgApi/SysMenuApi/SysButtonApi/SysModuleApi/SysApi...（用户/角色/组织/资源） |
| `snowy-plugin-api/snowy-plugin-biz-api` | BizUserApi/BizOrgApi 等 B 端业务用户（biz 插件提供） |
| `snowy-plugin-api/snowy-plugin-dev-api` | DevConfigApi/DevDictApi/DevFileApi/DevSmsApi/DevEmailApi/DevMessageApi...（配置/字典/文件/短信/邮件/消息） |
| `snowy-plugin-api/snowy-plugin-auth-api` | SaBaseLoginUser 登录用户 POJO、AuthApi 等 |
| `snowy-plugin-api/snowy-plugin-client-api` | ClientUserApi 等 C 端用户 |

命名惯例：`getXxxByIdWithoutException`（查不到返 null）/ `getXxxByIdWithException`（查不到抛异常）/ `getXxxListByIdList...`（批量）。

## 新建插件 checklist（仅当业务真的需要独立插件）

1. `snowy-plugin/snowy-plugin-{name}/`：pom（parent = snowy，依赖 snowy-common）+ 包 `vip.xiaonuo.{name}`
2. `snowy-plugin-api/snowy-plugin-{name}-api/`：供他插件调用的接口模块
3. 根 `pom.xml` 的 modules 加两个新模块；`snowy-web-app/pom.xml` 加实现模块依赖
4. 包结构照 `core/ + modular/` 两层组织
5. Knife4j 分组：`snowy-web-app` 的 Knife4jConfigure 加新插件包扫描（可选）
6. 前端对应加 `src/api/{name}/` 与 `src/views/{name}/` 目录

⚠️ 大多数情况**不需要**新建插件——在 `snowy-plugin-biz/modular/` 下加业务域即可。

## 禁止事项

- ❌ 插件 pom 互相依赖实现模块（如 biz 依赖 snowy-plugin-sys）
- ❌ 绕过 *-api 直接 import 其他插件 modular 下的类
- ❌ 跨插件返回 Entity/自定义 DTO（会引入类依赖），一律 JSONObject
- ❌ 把业务代码写进 sys/auth/dev 等平台插件（升级会冲突）

## 检查清单

- [ ] 跨插件调用走了 *-api 接口 + provider 实现
- [ ] 返回值 JSONObject，字段名与 Entity 属性名一致
- [ ] 调用方 pom 只依赖 *-api 模块
- [ ] 新业务域放对了位置（默认 snowy-plugin-biz/modular）
- [ ] 先查过现成 api（sys-api / dev-api）再决定新写

## 参考实现

| 文件 | 说明 |
|---|---|
| `snowy-plugin-api/snowy-plugin-sys-api/src/main/java/vip/xiaonuo/sys/api/SysUserApi.java` | 接口定义范本 |
| `snowy-plugin/snowy-plugin-sys/src/main/java/vip/xiaonuo/sys/modular/user/provider/SysUserApiProvider.java` | provider 实现范本（含权限码组装） |
| `snowy-plugin-api/snowy-plugin-dev-api/src/main/java/vip/xiaonuo/dev/api/` | dev-api 全家（Config/Dict/File/Sms/Email/Message） |
| `snowy-plugin/snowy-plugin-biz/src/main/java/vip/xiaonuo/biz/core/` | 插件 core 层组织范本 |
| 根 `pom.xml` + `snowy-web-app/pom.xml` | 模块聚合与依赖关系 |
