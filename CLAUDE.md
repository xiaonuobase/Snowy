# CLAUDE.md

本文件是 Snowy 二次开发项目的工程宪法。所有 AI 辅助开发必须遵守此处规范；详细规范见 `.claude/skills/` 与 `.claude/docs/`。

## 这是什么项目

**Snowy v3.0.0** —— 小诺开源的国密前后端分离快速开发平台，本项目以它为底座做二次开发。

- 后端：Java 17 + Spring Boot 3.5.9 + MyBatis-Plus 3.5.5 + Sa-Token 1.44.0 + Redisson + Hutool 5.8.25（**插件化 Maven 多模块架构**）
- 前端：`snowy-admin-web/`（Vue 3.5 + Vite 6 + **Ant Design Vue 4.2.6** + Pinia + vue-i18n，**JavaScript，不是 TS**，包管理用 npm）
- 数据库：MySQL（脚本 `_sql/snowy_mysql.sql`），表名**全大写下划线**
- 国密：登录密码 SM2 加密传输、口令 SM3 摘要存储、敏感字段 SM4-CBC 落库加密
- 本项目定位：**二次开发底座**——业务代码写在 `snowy-plugin-biz`，平台插件（sys/auth/dev/gen/client/mobile）原则上不动

## 目录结构

```
snowy-master/
├── snowy-common/            公共基础模块（包 vip.xiaonuo.common）：CommonResult/CommonException/
│                            CommonEntity/CommonPageRequest/CommonCacheOperator/国密工具等
├── snowy-plugin/            7 个插件实现（包 vip.xiaonuo.{插件名}）
│   ├── snowy-plugin-sys     B端系统：用户/组织/职位/角色/资源菜单/关系/数据范围
│   ├── snowy-plugin-auth    登录鉴权：B/C端登录、SSO、OAuth2/OIDC/CAS/SAML、三方登录
│   ├── snowy-plugin-dev     开发工具：配置/字典/邮件/文件/定时任务/日志/站内信/监控/短信/推送
│   ├── snowy-plugin-gen     代码生成器（Beetl 模板）
│   ├── snowy-plugin-client  C端功能（CLIENT_USER 独立用户体系）
│   ├── snowy-plugin-mobile  移动端资源管理
│   └── snowy-plugin-biz     ★ 业务插件——二次开发代码都放这里（biz/modular/{业务域}）
├── snowy-plugin-api/        7 个对应 *-api 模块：跨插件调用的接口定义（解耦用）
├── snowy-web-app/           唯一启动模块：Application.java、application.properties、_sql/
├── snowy-admin-web/         前端工程（独立 npm 项目）
└── .claude/                 本工程化配置（见下文）
```

插件内两层结构（以 biz 为例）：
- `vip.xiaonuo.biz.core.{config,enums,listener,timer,util}` —— 插件级基础设施
- `vip.xiaonuo.biz.modular.{业务域}.{controller,entity,enums,mapper,mapper/mapping,param,result,service,service/impl,provider}` —— 业务域标准结构

## 常用命令

```bash
# 后端（JDK 17，首次需导入数据库：mysql 执行 snowy-web-app/src/main/resources/_sql/snowy_mysql.sql，并启动本地 Redis）
mvn clean install -DskipTests          # 根目录构建
# 运行：IDE 启动 vip.xiaonuo.Application（snowy-web-app 模块），端口 82
# 接口文档：http://localhost:82/doc.html（Knife4j，basic 认证 admin/123456）
# 出厂登录：superAdmin / Snowy@2026!（⚠️不是 123456；权威来源 DEV_CONFIG 的 SNOWY_SYS_DEFAULT_PASSWORD_FOR_B）

# 前端（snowy-admin-web/ 目录下）
npm install
npm run dev                             # 端口 81，代理 /api → localhost:82
```

⚠️ 本机构建环境备忘（2026-08-18 实测）：
- 命令行 mvn 需用 IDEA 内置：`"/c/Program Files/JetBrains/IntelliJ IDEA 2026.2/plugins/maven-plugin/lib/maven3/bin/mvn"`，JAVA_HOME 指向同目录 jbr
- Maven Central 直连失败（DNS），需加阿里云镜像（临时 settings.xml 已生成在 /tmp/m2/settings.xml，mirrorOf=central → maven.aliyun.com/repository/public）
- IDEA 内置 JBR 是 Java 25，与项目 Lombok 1.18.30 不兼容（@Slf4j 失效）——**命令行完整编译需自装 JDK 17**；日常在 IDEA 里配 Project SDK 17 构建即可

## 后端架构

### 分层与 CRUD 标准结构（六件套 + 两可选件）

Controller → Service → Mapper 三层。一个业务域 = 一个 `modular/{域名}` 目录：

| 件 | 位置 | 要点 |
|---|---|---|
| Entity | `entity/Xxx.java` | `extends CommonEntity`；`@TableName("BIZ_XXX")` 大写；`@TableId private String id`（字符串雪花）；敏感字段加 `typeHandler = CommonSm4CbcTypeHandler.class`（需 `autoResultMap = true`）；字段用 `@Schema(description="中文")` |
| Mapper | `mapper/XxxMapper.java` | `extends BaseMapper<Xxx>`，通常空接口 |
| Mapper XML（可选） | `mapper/mapping/XxxMapper.xml` | 仅自定义 SQL 需要；与 Mapper 同包 |
| Service | `service/XxxService.java` | `extends IService<Xxx>`，方法带中文 Javadoc（@author/@date） |
| ServiceImpl | `service/impl/XxxServiceImpl.java` | `@Service`，`extends ServiceImpl<XxxMapper, Xxx> implements XxxService`；查询 `QueryWrapper<Xxx>().checkSqlInjection()` + lambda 条件；分页 `this.page(CommonPageRequest.defaultPage(), queryWrapper)`；写方法 `@Transactional(rollbackFor = Exception.class)`；业务校验 `throw new CommonException("中文消息{}", 参数)` |
| Controller | `controller/XxxController.java` | `@Tag + @RestController + @Validated`；**URL 直接写方法上**：`@GetMapping("/biz/xxx/page")`；查询 GET + Param 对象、写入 POST + `@RequestBody @Valid`；写操作加 `@CommonLog("中文标题")`；返回 `CommonResult.data(...)` / `CommonResult.ok()` |
| Param 组 | `param/Xxx{Page,Add,Edit,Id}Param.java` | **每个操作一个 Param 类**（不是单个 Bo）；PageParam 含 current/size/sortField/sortOrder/searchKey |
| Result（可选） | `result/XxxResult.java` | 仅投影返回需要，可直接返回 Entity |

URL 规范：`/{插件}/{业务域}/{动作}`，动作动词式（page/add/edit/delete/detail + 业务动作）；查询 GET、写入 POST；delete 收 `List<XxxIdParam>`。

### 跨插件调用（硬性规范）

插件之间**禁止**直接依赖对方的 modular 实体类。标准三步法：
1. 接口定义放 `snowy-plugin-api/{x}-api` 模块（如 `SysUserApi`）
2. 被调插件在 `provider/XxxApiProvider.java`（@Service）实现该接口
3. 调用方只依赖 `*-api` 模块注入接口；跨插件返回值用 hutool `JSONObject`，不返回实体类

### 权限模型（两层，易混淆）

- **后端接口权限**：`@SaCheckPermission("/biz/xxx/page")`，值 = **接口 URL**（不是冒号式权限码）；用户拥有的 URL 权限来自角色-资源授权生成的数据范围 apiUrl 列表
- **前端按钮权限**：`hasPerm('bizNoticeAdd')`，驼峰式按钮码，存于 SYS_RESOURCE 表 category=BUTTON 行（code 列），登录时放入 buttonCodeList 下发前端
- 路由级白名单（免登录/C端/超管）集中在 `snowy-web-app` 的 `GlobalConfigure`，新增免登录接口必须改那里
- B 端用 `StpUtil` / `StpLoginUserUtil`；C 端用 `StpClientUtil` / `StpClientLoginUserUtil`（接口前缀 /auth/c/**、/client/c/**）

## 前端架构（snowy-admin-web）

- API 层：`src/api/{插件}/{xxx}Api.js` —— 模板 `const request = (url, ...arg) => baseRequest('/biz/xxx/' + url, ...arg)`，导出对象方法，第三参为 method（范本 `src/api/biz/bizNoticeApi.js`）
- 页面三件：`src/views/{插件}/{域}/index.vue`（列表 + s-table）+ `form.vue`（弹窗表单，父组件 `formRef.onOpen(record)` 打开）+ 可选 `detail.vue`
- 组件：37 个 `Xn` 前缀业务组件（XnUpload/XnUserSelector/XnOrgSelector/DictSelect/s-table 等，见 `src/components/`）
- 请求：`src/utils/request.js`（token 头名 `token`、code!==200 统一报错）；C 端用 `clientRequest.js`；国密加密 `src/utils/smCrypto.js`
- 文案走 vue-i18n：`$t('xxx.yyy')`，语言文件在 `src/locales/`

## 后端必须遵守的规范

| 必须 | 禁止 |
|---|---|
| 包名根 `vip.xiaonuo.*` | `com.*` / `org.*` 等其他包名 |
| 每个 .java 头部 12 行 Apache 2.0 版权声明（含 `Copyright [2022] [https://www.xiaonuo.vip]`） | 删除/省略版权头（AI 生成代码最容易漏） |
| 依赖注入用 `@Resource` | `@Autowired`、构造器注入 |
| `extends ServiceImpl<Mapper, Entity> implements XxxService` | 只 implements 不继承 ServiceImpl |
| 对象转换用 Hutool `BeanUtil.toBean / copyProperties` | MapstructUtils / MapStruct 体系 |
| Lombok 只用 `@Getter @Setter` | `@Data` |
| 返回 `CommonResult<T>`、异常 `CommonException` | `R<T>`、`ServiceException`、`RuntimeException` |
| URL 动词式写方法上 + `@SaCheckPermission` 用 URL | 类级 @RequestMapping、RESTful 路径变量、冒号式权限码 |
| 主键 `String` 雪花；表名/字段全大写 | Long 主键、小写表名 |
| 注释/Javadoc/异常消息/Swagger 描述**全中文** | 英文注释 |
| 类名前缀 = 插件缩写（Biz/Sys/Dev/Gen/Auth/Client/Mobile） | 无前缀类名 |
| 查询构造带 `checkSqlInjection()`，写操作带 `@Transactional(rollbackFor = Exception.class)` | 裸 QueryWrapper、无事务写操作 |

## ⚠️ 易错点警告（先读再写代码）

**来自 RuoYi的惯性错误**——本项目的约定与它们**方向相反**：
1. ❌ `implements IXxxService` 不继承 → ✅ 必须 `extends ServiceImpl<XxxMapper, Xxx>`
2. ❌ `@RequiredArgsConstructor` 构造器注入 → ✅ `@Resource` 字段注入
3. ❌ `MapstructUtils.convert()` → ✅ `BeanUtil.toBean()`
4. ❌ `@Data` → ✅ `@Getter @Setter`
5. ❌ RESTful `/list`、`/{id}`、PUT/DELETE → ✅ `/page`、`/detail`、全 POST
6. ❌ 权限码 `system:user:list` → ✅ 权限码 = 接口 URL `/sys/user/page`
7. ❌ 单个 Bo + AddGroup/EditGroup → ✅ 每操作一个 Param 类
8. ❌ Long 雪花主键 → ✅ String 主键；❌ 小写表名 → ✅ 全大写

其他易错点：
- **版权头**：AI 生成的新 Java 文件必须补 12 行版权声明（pre-tool-use hook 会警告）
- **逻辑删除**：`DELETE_FLAG` 继承自 CommonEntity，查询自动过滤，不要手写 delete_flag 条件
- **对象转换到 Entity 时**：`BeanUtil.toBean(param, Xxx.class)` 用于新增；编辑先 `queryEntity(id)` 查出再 `BeanUtil.copyProperties(param, entity)`
- **新业务表**用 `BIZ_` 前缀（出厂 biz 域复用了 SYS_/DEV_ 表是历史设计，新表不要模仿）
- 前端是 **Ant Design Vue + JS**：不要写 Element Plus 组件、不要写 TypeScript

## 新增业务功能时的流程

1. 设计表（BIZ_ 前缀大写、String 雪花主键、审计字段组齐全，参考 `.claude/skills/database-ops`）
2. 开发：`/dev` 命令（模式 A：AI 直写六件套 + 前端三件；模式 B：用平台自带代码生成器）或 `/crud`（表已存在）
3. 菜单 SQL：SYS_RESOURCE 表 INSERT（MENU 行 + BUTTON 行，按钮码驼峰式，参考 gen 插件 sqlend 模板）
4. 重启后端 + 刷新前端，在角色管理里给角色授权新资源
5. `/check` 检查规范；完整度口径见 `.claude/framework-config.json`（六件套 /6）

## .claude 目录说明

| 目录 | 内容 |
|---|---|
| `skills/` | 26 个技能（crud-development、code-patterns、security-auth、env-setup、api-verify、platform-extension 等），hook 会强制评估激活 |
| `commands/` | 10 个斜杠命令：/start /dev /crud /check /sync /update-status /progress /next /init-docs /add-todo |
| `agents/` | 2 个子代理：code-reviewer（只读审查）、project-manager（项目管理文档） |
| `docs/` | 8 篇开发指南（框架说明/后端/前端/数据库/工具类/国密安全等） |
| `templates/` | 3 个管理文档模板（需求/项目状态/待办清单） |
| `hooks/` | 3 个 hook：强制技能评估、危险命令拦截、nul 清理 |
| `framework-config.json` | 框架 vs 业务划分与进度统计规则（机器可读） |

进度统计口径：出厂 biz 7 域（index/dict/group/notice/org/position/user）为演示代码**不计进度**，新建业务域才计入（完整度 = 六件套存在数 / 6）。

## 外部依赖说明

- 无 `.mcp.json`（未配置数据库 MCP）；需要查库时用 mysql CLI，不可用时 SQL 落盘 `docs/sql-pending/` 提示手动执行
- 数据库连接从 `snowy-web-app/src/main/resources/application.properties` 的 dynamic master 段动态解析，**禁止在任何文档/命令里硬编码连接串**
- 无 `.codegraph` 索引；定位代码优先用 Glob/Grep 或 project-navigator 技能的速查表
- git 仓库已初始化（master 分支）；提交规范见 git-workflow 技能
