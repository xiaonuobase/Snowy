# AGENTS.md

本文件是 Snowy 二次开发项目的工程宪法，适用于所有 AI 编程工具（Claude Code、Codex、Qwen Code、Cursor、Copilot、Gemini CLI 等）。所有 AI 辅助开发必须遵守此处规范；详细规范见 `.agents/skills/`（技能）与 `.claude/docs/`（开发指南）。

## 这是什么项目

**Snowy 3.x**（发布版本以 git tag 为准）—— 小诺开源的国密前后端分离快速开发平台，本项目以它为底座做二次开发。

- 后端：Java 17（21 可用）+ Spring Boot 3.5.16 + MyBatis-Plus 3.5.9 + Sa-Token 1.44.0 + Redisson + Hutool 5.8.25（版本以根 pom.xml 为准；**插件化 Maven 多模块架构**）
- 前端：`snowy-admin-web/`（Vue 3.5 + Vite 6 + **Ant Design Vue 4.2.6** + Pinia + vue-i18n，**JavaScript，不是 TS**，包管理用 npm）
- 数据库：MySQL（脚本 `snowy-web-app/src/main/resources/_sql/snowy_mysql.sql`），表名**全大写下划线**
- 国密：登录密码 SM2 加密传输、口令 SM3 摘要存储、敏感字段 SM4-CBC 落库加密
- 本项目定位：**二次开发底座**——业务代码写在 `snowy-plugin-biz` 或自建业务插件，平台插件（sys/auth/dev/gen/client/mobile）原则上不动

## 技能使用规则（动手前必读）

`.agents/skills/` 下每个技能是一份详细规范（正误代码对照 + 检查清单）。每次接到开发任务，按以下步骤执行：

1. **评估**：先对照下方技能索引列出匹配的技能，格式 `技能名: 理由`；无匹配写"无匹配技能"；最多选 4 个最相关的（与代码编写直接相关的优先于方法论类的）。评估结果必须在回复中展示
2. **激活**：Claude Code、Qwen Code 用 Skill 工具激活；其他工具直接读取 `.agents/skills/{技能名}/SKILL.md` 全文；本会话已激活过的不重复激活
3. **实现**：读完技能再写代码，并遵守技能中的正误对照与检查清单

只列确实相关的技能，宁缺毋滥。Claude Code、Codex、Qwen Code 有钩子，会话首轮注入激活流程、后续每轮注入精简提醒（索引本身就是下方内容，不重复注入）；其他工具无钩子，须自觉遵守本文件。

<!-- skill-index:start -->
> 注意：snowy-admin-web/ 目录存在，CRUD/dev 类任务应同时生成前端三文件（api js + index.vue + form.vue）

知识技能（按任务自动匹配）：
- crud-development: CRUD/业务模块/Entity/Service/Controller/Param 开发
- api-development: API设计/接口规范/URL/CommonResult/异常处理
- plugin-architecture: 插件/模块划分/跨插件调用/provider/新建插件
- code-generator: 代码生成/Beetl/gen/生成器/菜单SQL
- database-ops: 数据库/SQL/建表/大写表名/菜单/字典数据
- backend-annotations: 注解/@CommonLog/@SaCheckPermission/@Trans/@Validated
- code-patterns: 编码规范/禁令/命名/同类框架惯性错误
- common-toolkit: 工具类/CommonCacheOperator/CommonCryptogramUtil/Hutool
- cache-redis: 缓存/Redis/Redisson/CommonCacheOperator
- file-oss-management: 文件上传/OSS/云存储/MinIO/XnUpload
- sms-mail: 短信/邮件/验证码/sms4j/SMTP
- message-push: 站内信/消息推送/钉钉/企微/飞书/WebSocket
- scheduled-jobs: 定时任务/Cron/DEV_JOB/TimerTaskRunner
- dict-config: 字典/DEV_DICT/BIZ_DICT/DictSelect/枚举/系统配置
- security-auth: 鉴权/Sa-Token/登录/白名单/B端C端/数据范围/权限
- crypto-sm: 国密/SM2/SM3/SM4/加密/密码/敏感字段
- frontend-pc: 前端/Vue/AntdV/api js/index.vue/form.vue/底座组件选型(上传/选人/树选择/分页下拉)
- client-mobile: C端/客户端/移动端/uni-app/mobile插件
- bug-detective: Bug/报错/异常/不工作/排查
- performance-doctor: 性能/慢查询/优化/N+1/缓存
- project-navigator: 找文件/在哪/目录结构/导航
- git-workflow: Git/提交/commit/分支
- env-setup: 环境搭建/首次启动/默认密码/出厂账号/导入数据库
- api-verify: 接口测试/自测/登录拿token/curl验证/冒烟
- platform-extension: 扩展/定制平台/监听/联动/复用平台能力/不改框架
- add-skill: 新增技能/创建skill

流程技能（只在用户显式调用时执行：Claude Code、Qwen Code 输入 `/名称`，Codex 输入 `$名称`）：
- start: 新会话快速了解项目现状
- dev: 开发新功能（建表 + 后端六件套 + 前端三件 + 菜单SQL，双模式）
- crud: 基于已有数据库表快速生成 CRUD 代码
- check: 代码规范检查（后端 + 前端，三级清单）
- sync: 全量同步代码状态并生成综合报告
- update-status: 增量更新项目状态文档（日常高频）
- progress: 项目进度梳理（业务模块完整度报告）
- next: 下一步开发建议（优先级排序）
- init-docs: 初始化 docs/ 三大管理文档
- add-todo: 快速添加待办事项
<!-- skill-index:end -->

## 目录结构

- `snowy-common/`：公共基础（包 `vip.xiaonuo.common`）：CommonResult/CommonException/CommonEntity/CommonPageRequest/CommonCacheOperator/国密工具
- `snowy-plugin/`：7 个插件——sys（用户/组织/角色/资源/数据范围）、auth（B/C 端登录、SSO、三方登录）、dev（配置/字典/文件/定时任务/日志/消息/短信）、gen（代码生成器）、client（C 端用户）、mobile（移动端资源）、**biz（业务代码默认放这里，也可自建 snowy-plugin-xxx）**
- `snowy-plugin-api/`：7 个 `*-api` 模块，跨插件调用的接口定义
- `snowy-web-app/`：唯一启动模块（Application.java、application.properties、`_sql/`、GlobalConfigure）
- `snowy-admin-web/`：前端工程（独立 npm 项目）
- 插件内两层：`{插件包}.core.{config,enums,listener,timer,util}` 基础设施；`{插件包}.modular.{业务域}.{controller,entity,enums,mapper,mapper/mapping,param,result,service,service/impl,provider}` 业务域。详细导航见 project-navigator 技能

## 常用命令

```bash
# 后端（JDK 17；首次需导入 snowy-web-app/src/main/resources/_sql/snowy_mysql.sql 并启动本地 Redis）
mvn clean install -DskipTests          # 根目录构建；只构建单模块必须带 -am（welink-sdk 为 system scope）
# 运行：IDE 启动 vip.xiaonuo.Application（snowy-web-app 模块），端口 82
# 接口文档：http://localhost:82/doc.html（basic 认证 snowyAdmin / Snowy@2026!，以 knife4j.basic.* 配置为准）
# 出厂登录：superAdmin / Snowy@2026!（权威来源 DEV_CONFIG 的 SNOWY_SYS_DEFAULT_PASSWORD_FOR_B；2026-06-26 前导入的旧库为 123456）
# 已在用的库密码可能被改过：登录失败不要反复重试，连续输错会锁定账号

# 前端（snowy-admin-web/ 目录下）
npm install
npm run dev                             # 端口 81，代理 /api → localhost:82
```

镜像、JDK 版本等构建环境问题见 env-setup 技能。

## 后端架构

### 分层与 CRUD 标准结构（六件套 + 两可选件）

Controller → Service → Mapper 三层。一个业务域 = 一个 `modular/{域名}` 目录：

| 件 | 位置 | 要点 |
|---|---|---|
| Entity | `entity/Xxx.java` | 手写实体 `extends CommonEntity`（关系表可不继承，如 SysRelation；生成器实体逐字段声明，不要混用）；`@TableName("BIZ_XXX")` 大写；`@TableId private String id`；敏感字段加 `typeHandler = CommonSm4CbcTypeHandler.class`（需 `autoResultMap = true`）；字段 `@Schema(description="中文")` |
| Mapper | `mapper/XxxMapper.java` | `extends BaseMapper<Xxx>`，通常空接口 |
| Mapper XML | `mapper/mapping/XxxMapper.xml` | 空 `<mapper namespace="Mapper 全限定名">` 占位（与生成器一致），自定义 SQL 写在里面 |
| Service | `service/XxxService.java` | `extends IService<Xxx>`，方法带中文 Javadoc（@author/@date） |
| ServiceImpl | `service/impl/XxxServiceImpl.java` | `@Service`，`extends ServiceImpl<XxxMapper, Xxx> implements XxxService`；分页/动态排序 `new QueryWrapper<Xxx>().checkSqlInjection()` + lambda 条件，固定条件/唯一性校验可用 `LambdaQueryWrapper`；分页 `this.page(CommonPageRequest.defaultPage(), queryWrapper)`；写方法 `@Transactional(rollbackFor = Exception.class)`；业务校验 `throw new CommonException("中文消息{}", 参数)` |
| Controller | `controller/XxxController.java` | `@Tag + @RestController + @Validated`；**URL 写在方法上**：`@GetMapping("/biz/xxx/page")`；查询 GET + Param 对象、写入 POST + `@RequestBody @Valid`（上传、ID 列表查询等例外见 api-development）；写操作和导出/下载加 `@CommonLog("中文标题")`（登录/注册不加）；返回 `CommonResult.data(...)` / `CommonResult.ok()` |
| Param 组 | `param/Xxx{Page,Add,Edit,Id}Param.java` | **每个操作一个 Param 类**；数据库分页的 PageParam 含 current/size/sortField/sortOrder/searchKey（专用分页可按需裁剪） |
| Result（可选） | `result/XxxResult.java` | 仅投影返回需要，可直接返回 Entity |

URL 规范：`/{插件}/{业务域}/{动作}`，动作动词式（page/list/add/edit/delete/detail + 业务动作）；delete 收 `List<XxxIdParam>`。

### 跨插件调用（硬性规范）

插件之间**禁止**直接依赖对方的 modular 类。三步法：1) 接口定义放 `snowy-plugin-api/{x}-api`（如 `SysUserApi`）2) 被调插件在 `provider/XxxApiProvider.java`（@Service）实现 3) 调用方只依赖 `*-api` 注入接口；返回值**不用对方的 Entity/Param/Result**：对象用 hutool `JSONObject`（含 List/Page 形式），简单值用 String/boolean 等，登录用户用 auth-api 的 SaBaseLoginUser。

### 权限模型（两层，易混淆）

- **后端接口权限**：`@SaCheckPermission("/biz/xxx/page")`，值 = **接口 URL**。来源 = 用户直接授权 + 所属角色的**授权权限**（SYS_RELATION 的 SYS_USER_HAS_PERMISSION / SYS_ROLE_HAS_PERMISSION）；"授权资源"只控制菜单和按钮，不产生接口权限
- **超管与新接口**：超管（角色码 `superAdmin`）**不会**自动通过 `@SaCheckPermission`；**手写的新接口必须到 角色管理 → 授权权限 给超管勾选，否则超管也 403**（生成器"项目内生成"会自动授权）；平台插件（sys/dev 等）不用该注解，靠 GlobalConfigure 路由级超管校验
- **前端按钮权限**：`hasPerm('bizNoticeAdd')`，驼峰码，存于 SYS_RESOURCE 表 category=BUTTON 行的 code 列，登录时放入 buttonCodeList 下发前端
- 路由级白名单（免登录/C 端/超管）集中在 `snowy-web-app` 的 `GlobalConfigure`，新增免登录接口必须改那里
- B 端用 `StpUtil` / `StpLoginUserUtil`；C 端用 `StpClientUtil` / `StpClientLoginUserUtil`（接口前缀 /auth/c/**、/client/c/**）

## 前端架构（snowy-admin-web）

- API 层：`src/api/{插件}/{xxx}Api.js`，模板 `const request = (url, ...arg) => baseRequest('/biz/xxx/' + url, ...arg)`，导出对象方法，第三参为 method（范本 `src/api/biz/bizNoticeApi.js`）
- 页面三件：`src/views/{插件}/{域}/index.vue`（列表 + s-table）+ `form.vue`（弹窗表单，父组件 `formRef.onOpen(record)` 打开）+ 可选 `detail.vue`
- 组件：`src/components/` 下的 Xn 前缀业务组件及 DictSelect、STable、CropUpload 等，完整列表以 `ls snowy-admin-web/src/components` 为准
- 请求：`src/utils/request.js`（token 头名 `token`、code!==200 统一报错）；C 端用 `clientRequest.js`；国密 `src/utils/smCrypto.js`
- 文案：业务页面直接写中文，**不强制**国际化；vue-i18n 只用于 auth 登录/找回密码页和 sys/user 列表，改这些页面时延续 `$t('xxx.yyy')` 并补 `src/locales/` 词条

## 必须遵守的规范与易错点（先读再写代码）

本项目很多约定与同类快速开发框架**方向相反**，表中前 8 行是 AI 最容易写反的条目：

| 必须 | 禁止 |
|---|---|
| 有实体的 Service `extends ServiceImpl<XxxMapper, Xxx> implements XxxService`（首页统计等无实体服务只 implements） | `implements IXxxService` 不继承 |
| `@Resource` 字段注入 | `@Autowired`、任何构造器注入（含 `@RequiredArgsConstructor`） |
| Hutool `BeanUtil.toBean`（新增）/ 先 `queryEntity(id)` 再 `BeanUtil.copyProperties`（编辑） | MapstructUtils / MapStruct |
| Lombok 只用 `@Getter @Setter` | `@Data` |
| 返回 `CommonResult<T>`（导出/下载 void、SSO 协议回调除外）、异常 `CommonException` | `R<T>`、`ServiceException`、`RuntimeException` |
| URL 动词式写在方法上，查询 GET、写入 POST；业务接口加 `@SaCheckPermission`（值 = URL，选择器等辅助接口可不加） | 类级 @RequestMapping、`/{id}` 路径变量、`@PutMapping`/`@DeleteMapping`、冒号式权限码 `system:user:list` |
| 每操作一个 Param 类 | 单个 Bo + AddGroup/EditGroup |
| 主键 `String` 雪花；表名/字段全大写；新表前缀 = 所在插件（biz 用 `BIZ_`，snowy-plugin-order 用 `ORDER_`，勿复用 SYS_/DEV_） | Long 主键、小写表名 |
| 接收前端排序字段的 QueryWrapper 带 `checkSqlInjection()`；写操作带 `@Transactional(rollbackFor = Exception.class)` | 裸 QueryWrapper、字符串列名条件、无事务写操作 |
| 逻辑删除 `DELETE_FLAG` 继承自 CommonEntity，查询自动过滤 | 手写 delete_flag 条件 |
| 平台包名 `vip.xiaonuo.*`；业务可自定义包名，非 vip.xiaonuo 开头须补扫描配置（见 plugin-architecture） | 自定义包名不配扫描（404、找不到 Bean/Mapper） |
| 平台自带文件保留 12 行版权头；用户新写的业务代码默认不加 | 删除平台文件版权头、给业务代码擅自加小诺版权声明 |
| 注释/Javadoc/异常消息/Swagger 描述**全中文**；类名前缀 = 所在插件缩写（Biz/Sys/Dev，自建插件用自己的） | 英文注释、无前缀类名 |
| 前端 **Ant Design Vue + JavaScript** | Element Plus、TypeScript |

完整禁令（17 条惯性错误 + 命名 + Javadoc）见 code-patterns 技能。

## 新增业务功能时的流程

1. 设计表（前缀 = 所在插件、大写、String 雪花主键、审计字段组齐全，见 database-ops 技能）
2. 开发：先定代码位置（biz 或自建插件）和包名（默认 vip.xiaonuo.biz），再用 dev 流程技能（模式 A：AI 直写六件套 + 前端三件；模式 B：平台代码生成器）或 crud 流程技能（表已存在）
3. 菜单 SQL：SYS_RESOURCE 表 INSERT（MENU 行 + BUTTON 行，按钮码驼峰式，参考 gen 插件 sqlend 模板）
4. 重启后端 + 刷新前端，在角色管理给角色"授权资源"（菜单/按钮）和"授权权限"（新接口 URL，**超管也要勾**）
5. check 流程技能检查规范；进度口径：出厂 biz 7 域（index/dict/group/notice/org/position/user）为演示代码不计进度，新建业务域才计入（完整度 = 六件套数 / 6，见 `.claude/framework-config.json`）

## AI 工程化配置与外部依赖

- **修改技能只改 `.agents/skills/`**，改完运行 `node .agents/scripts/sync-skills.js` 同步到 `.claude/skills/`（会话结束钩子也会自动同步）；新增技能按 add-skill 技能登记到上方索引。配置文件一览与各工具用法见 `docs/guides/ai-multi-tool-guide.md`
- 数据库连接从 `snowy-web-app/src/main/resources/application.properties` 的 dynamic master 段动态解析，**禁止在任何文档/命令里硬编码连接串**；查库用 mysql CLI，不可用时 SQL 落盘 `docs/sql-pending/` 提示手动执行
- git 远程 gitee（origin），开发分支 dev、主分支 master；默认只本地 commit，明确要求才 push；提交规范见 git-workflow 技能
