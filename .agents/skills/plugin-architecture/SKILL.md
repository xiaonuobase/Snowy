---
name: plugin-architecture
description: Snowy 插件化架构与跨插件调用规范：Maven 模块拓扑、插件内 core/modular 两层结构、plugin-api 解耦三步法、新建业务插件 checklist、自定义包名扫描配置。触发场景：1) 需要调用其他插件的功能（如 biz 要用 sys 的用户）2) 划分新业务该放哪个模块 3) 新建业务插件或新的 *-api 接口 4) 业务代码使用非 vip.xiaonuo 的自定义包名。触发词：插件、plugin、模块划分、跨模块、跨插件、api 模块、provider、解耦、依赖、新建插件、包名、自定义包名、scanBasePackages。注意：单个业务域内的 CRUD 分层见 crud-development；本技能讲"模块之间怎么组织与通信"。
---

# Snowy 插件化架构规范

## Maven 模块拓扑

```
依赖方向（箭头 = 依赖）：

snowy-web-app（启动模块，聚合一切）
    ├── snowy-plugin-{sys|auth|dev|gen|client|mobile|biz}
    │       └── 自己的 *-api → snowy-common（传递依赖；公共基础，人人依赖）
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
| **`snowy-plugin-biz`** | **业务插件——二次开发主战场** | 随便写 |
| **`snowy-plugin-{name}`**（自建） | 用户自己的业务插件（如 snowy-plugin-order） | 随便写（按下方新建插件清单注册） |
| `snowy-web-app` | 启动 + 全局配置（白名单/异常处理/包扫描） | 只改配置类 |

## 插件内两层结构

```
vip.xiaonuo.biz
├── core/                          插件级基础设施（子目录按需建；出厂 biz 只有 config/enums/listener）
│   ├── config/                    插件配置类
│   ├── enums/                     插件级枚举
│   ├── listener/                  数据变更监听（如 BizDataChangeListener）
│   ├── timer/                     定时任务入口（需要时再建）
│   └── util/                      插件级工具（需要时再建）
└── modular/                       业务域（一个域一个目录）
    └── {域名}/
        ├── controller/  entity/  enums/  mapper/(+mapping/)
        ├── param/  result/  service/(+impl/)  provider/
```

新业务域默认放 `snowy-plugin-biz` 的 `modular/{域名}` 下（参考 notice 域）；也可以新建自己的业务插件（按下方清单注册），插件内结构相同，包根换成该插件的包名（如 `com.company.order`）。

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

## 新建业务插件 checklist

业务可以新建自己的插件（如 snowy-plugin-order），步骤：

1. `snowy-plugin/snowy-plugin-{name}/`：pom 的 parent = `snowy-plugin`（groupId 继承 `vip.xiaonuo`，与 Java 包名无关），依赖自己的 `snowy-plugin-{name}-api`（snowy-common 经它传递引入）和要调用的其他 *-api；包名默认 `vip.xiaonuo.{name}`，也可自定义（如 `com.company.order`，必须按下节补扫描配置）
2. `snowy-plugin-api/snowy-plugin-{name}-api/`：供他插件调用的接口模块，pom 的 parent = `snowy-plugin-api`，依赖 snowy-common
3. 在 `snowy-plugin/pom.xml` 和 `snowy-plugin-api/pom.xml` 的 modules 里分别登记（根 `pom.xml` 的 modules 只有 snowy-common/snowy-plugin/snowy-plugin-api/snowy-web-app 四个，不用改）；根 `pom.xml` 的 dependencyManagement 增加两个模块的版本声明（`${snowy.version}`）；`snowy-web-app/pom.xml` 加实现模块依赖
4. 包结构照 `core/ + modular/` 两层组织；类名前缀用插件缩写（如 Order），URL 第一段用插件名（如 `/order/xxx/page`），表前缀用插件名大写（如 `ORDER_`）
5. Knife4j 分组（可选）：在 `snowy-web-app/src/main/resources/application.properties` 的 `# knife4j doc groups` 段追加 `springdoc.group-configs[n].group=SNOWY-PLUGIN-{NAME}`、`display-name`、`packages-to-scan={插件包名}`（Knife4jConfigure 里没有包扫描配置）
6. 前端对应加 `src/api/{name}/` 与 `src/views/{name}/` 目录

放 biz 还是自建插件由业务决定：功能少、与出厂演示域共存无妨时放 `snowy-plugin-biz/modular/` 最省事；需要独立演进、按团队/产品线拆分时新建插件。平台插件（sys/auth/dev/gen/client/mobile）和 snowy-common 仍不建议改。

## 使用自定义包名

平台代码包名是 `vip.xiaonuo.*`；用户自己的业务（biz 内新业务域或自建插件）可以用自定义包名（如 `com.company.order`）。未指定时沿用目标插件现有包名（snowy-plugin-biz 为 `vip.xiaonuo.biz`）。

启动模块默认只扫描 `vip.xiaonuo`，包名不以 `vip.xiaonuo` 开头时**必须**在 snowy-web-app 补齐以下 3 处，否则 Bean/Mapper 不生效（以 `com.company` 为例）：

| 文件 | 原配置 | 改法 |
|---|---|---|
| `snowy-web-app/src/main/java/vip/xiaonuo/Application.java` | `@SpringBootApplication(exclude = {Knife4jAutoConfiguration.class})` | 加 `scanBasePackages = {"vip.xiaonuo", "com.company"}`，保留原有 exclude，`vip.xiaonuo` 不能丢 |
| `snowy-web-app/src/main/java/vip/xiaonuo/core/config/GlobalConfigure.java` | `@MapperScan(basePackages = {"vip.xiaonuo.**.mapper"})` | 追加 `"com.company.**.mapper"` |
| `snowy-web-app/src/main/resources/application.properties` | `mybatis-plus.mapper-locations=classpath*:vip/xiaonuo/**/mapping/*.xml,...` | 末尾追加 `,classpath*:com/company/**/mapping/*.xml` |

建议项：
- application.properties 的 `# knife4j doc groups` 段增加一组 `springdoc.group-configs[n].group / display-name / packages-to-scan=com.company`，否则 doc.html 看不到新接口
- `snowy-web-app/src/main/resources/logback-spring.xml` 只给 `vip.xiaonuo` 配了 logger，需要业务日志同样输出时按需给 `com.company` 加一条

不受包名影响、无需配置：@SaCheckPermission 接口权限收集（ResourceCollectListener 遍历全部接口，不限包）、全局异常处理（@ControllerAdvice 未限定包）、Sa-Token 路由白名单（按 URL）、`src/main/java` 下 Mapper XML 打包（根 pom 已配置 `**/*.xml`）。

自定义包名只换包路径，其余规范（类名前缀、@Resource、CommonResult、URL 动词式等）不变。

## 禁止事项

- 禁止插件 pom 互相依赖实现模块（如 biz 依赖 snowy-plugin-sys）
- 禁止绕过 *-api 直接 import 其他插件 modular 下的类
- 禁止跨插件返回**实现模块里的** Entity/Param/Result/自定义 DTO（会引入类依赖）：对象统一用 JSONObject（列表 `List<JSONObject>`、分页 `Page<JSONObject>`），简单值可直接用 String/Long/boolean/List\<String\>；唯一例外是定义在 *-api 模块内的共享 POJO（如 auth-api 的 SaBaseLoginUser）
- 禁止把业务代码写进 sys/auth/dev 等平台插件（升级会冲突）

## 检查清单

- [ ] 跨插件调用走了 *-api 接口 + provider 实现
- [ ] 对象返回值用 JSONObject（简单值可直接用 String/Long/boolean 等），字段名与 Entity 属性名一致
- [ ] 调用方 pom 只依赖 *-api 模块
- [ ] 新业务域放对了位置（snowy-plugin-biz/modular 或自建业务插件，不进平台插件）
- [ ] 包名不以 vip.xiaonuo 开头时，snowy-web-app 的 3 处扫描配置已补齐
- [ ] 先查过现成 api（sys-api / dev-api）再决定新写

## 参考实现

| 文件 | 说明 |
|---|---|
| `snowy-plugin-api/snowy-plugin-sys-api/src/main/java/vip/xiaonuo/sys/api/SysUserApi.java` | 接口定义范本 |
| `snowy-plugin/snowy-plugin-sys/src/main/java/vip/xiaonuo/sys/modular/user/provider/SysUserApiProvider.java` | provider 实现范本（含权限码组装） |
| `snowy-plugin-api/snowy-plugin-dev-api/src/main/java/vip/xiaonuo/dev/api/` | dev-api 全家（Config/Dict/File/Sms/Email/Message） |
| `snowy-plugin/snowy-plugin-biz/src/main/java/vip/xiaonuo/biz/core/` | 插件 core 层组织范本 |
| 根 `pom.xml`（dependencyManagement）+ `snowy-plugin/pom.xml`、`snowy-plugin-api/pom.xml`（modules 登记）+ `snowy-web-app/pom.xml` | 模块聚合与依赖关系 |
