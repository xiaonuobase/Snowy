---
name: code-patterns
description: Snowy 全量编码禁令速查表（含"来自同类快速开发框架的惯性错误"专栏）、命名规范、Javadoc 格式、JSON/日期约定。触发场景：1) 写任何 Java 代码前的规范自查 2) 审查代码风格问题 3) 不确定命名或注释格式。触发词：编码规范、禁令、命名、代码风格、惯性错误、Javadoc、JSON、日期。注意：本技能是速查表；分层结构见 crud-development，模块组织见 plugin-architecture，前端规范见 frontend-pc，提交规范见 git-workflow。
---

# Snowy 编码禁令速查表

## 来自同类快速开发框架的惯性错误（最高优先级）

AI 对主流 Java 脚手架先验极强，**以下每条都是反向的**，写代码前先扫一遍：

| # | 常见脚手架惯性写法（错误） | Snowy 正确写法 |
|---|---|---|
| 1 | `implements IXxxService`（不继承） | `extends ServiceImpl<XxxMapper, Xxx> implements XxxService`（有实体的服务；首页统计等无实体服务可以只 implements，如 BizIndexServiceImpl） |
| 2 | `@RequiredArgsConstructor` + `private final` 构造器注入 | `@Resource` 字段注入（jakarta.annotation.Resource） |
| 3 | `MapstructUtils.convert(src, Target.class)` | `BeanUtil.toBean(param, Target.class)` / `BeanUtil.copyProperties(src, target)` |
| 4 | `@Data` | `@Getter @Setter` |
| 5 | `R<T>` / `AjaxResult` / `TableDataInfo<T>` | `CommonResult<T>`（data/ok/error 工厂方法） |
| 6 | `ServiceException` | `CommonException("中文消息{}", arg)` |
| 7 | 类级 `@RequestMapping("/biz/xxx")` + RESTful 动作 | 无类级注解，方法上全路径 `@PostMapping("/biz/xxx/add")` |
| 8 | `@PutMapping` / `@DeleteMapping` / `@GetMapping("/{id}")` | 全部 `@PostMapping` / `@GetMapping`，无路径变量 |
| 9 | 权限码 `biz:xxx:list` 冒号式 | `@SaCheckPermission("/biz/xxx/page")` URL 式 |
| 10 | 单个 XxxBo + AddGroup/EditGroup 校验组 | XxxPageParam / AddParam / EditParam / IdParam 每操作一类 |
| 11 | Long 雪花主键（JS 精度问题靠序列化器解决） | `@TableId private String id` |
| 12 | 小写表名 `biz_xxx` | 全大写 `BIZ_XXX` |
| 13 | 手写实体里自己声明 `del_flag` / `@TableLogic` | 手写实体继承 CommonEntity（字段 DELETE_FLAG）；代码生成器产出的实体不继承 CommonEntity、逐字段声明 `@TableLogic`，是模板写法，两种不要混用 |
| 14 | `@Log(title=..., businessType=...)` | `@CommonLog("中文标题")` |
| 15 | `buildQueryWrapper()` 抽方法、字符串列名 `eq("name", …)` | 分页/动态排序：`new QueryWrapper<Xxx>().checkSqlInjection()` + `queryWrapper.lambda()` 内联；固定条件查询、唯一性校验：`new LambdaQueryWrapper<Xxx>()`（生成器模板和平台代码都这样写，`CommonSqlUtil.scopeIn` 也要求传 LambdaQueryWrapper） |
| 16 | 前端 Element Plus 组件 | Ant Design Vue 4 组件（a-button 等） |
| 17 | 前端 TypeScript | JavaScript（仅 `views/front/index.vue` 一处历史 `lang="ts"` 页面，不要模仿） |

## 后端禁令速查表

| 禁止 | 替代 | 原因 |
|---|---|---|
| 业务用自定义包名（如 `com.company.order`）却没补启动模块扫描配置 | 补齐 Application 的 `scanBasePackages`、GlobalConfigure 的 `@MapperScan`、`mybatis-plus.mapper-locations`（见 plugin-architecture）；未指定包名时沿用目标插件现有包名（biz 为 `vip.xiaonuo.biz`） | 漏配会导致接口 404、Bean 注入失败、Invalid bound statement |
| 删除平台自带文件（common/平台插件/*-api/web-app/biz 出厂演示代码）的 12 行版权头 | 原样保留；用户新写的业务代码不强制，默认不加小诺版权声明（用户有自己的版权头规范按用户的来） | Apache 2.0 协议要求保留原声明 |
| `@Autowired` | `@Resource` | 项目统一风格（存量仅 auth 插件 2 处方法级 @Autowired，用于 Sa-Token 配置初始化） |
| 裸 `new RuntimeException(...)` | `throw new CommonException(...)` | 统一异常处理链路 |
| `Map<String,Object>` 作业务返回 | 类型化 Result/Entity + CommonResult | 类型安全 |
| 手写 SQL 拼接 | `checkSqlInjection()` + lambda | 防注入 |
| 写操作无 `@Transactional(rollbackFor = Exception.class)` | 加上 | rollbackFor 必须显式（默认不回滚受检异常） |
| 手动赋值 create_user/create_time | 继承 CommonEntity 自动填充 | MetaObjectHandler 统一填 |
| 查询手写 delete_flag 条件 | 逻辑删除自动过滤 | @TableLogic 已处理 |
| 跨插件 import 对方 modular 类 | 走 *-api + provider | 模块解耦 |
| 英文注释/异常消息 | 全中文 | 项目统一 |
| System.out.println | hutool/Spring 日志 | 生产可控 |

## 命名规范表

| 对象 | 规则 | 示例 |
|---|---|---|
| Java 类前缀 | = 所在插件缩写（biz 用 Biz，自建插件用自己的缩写） | BizSupplier、SysUser、DevConfig、GenBasic、AuthThirdUser、ClientUser、MobileMenu；自建 order 插件如 OrderPayRecord |
| Service 接口/实现 | `XxxService` / `XxxServiceImpl` | BizSupplierService / BizSupplierServiceImpl |
| Mapper / XML | `XxxMapper` / `mapping/XxxMapper.xml` 同包 | BizSupplierMapper |
| Param 后缀 | PageParam / AddParam / EditParam / IdParam / SelectorXxxParam / ExportParam | BizSupplierPageParam |
| Result 后缀 | `XxxResult` | BizSupplierResult |
| Controller URL | `/biz/supplier/page` 小写驼峰域名 | — |
| 数据库 | 全大写 | BIZ_SUPPLIER |
| 枚举 | `XxxEnum`，含 getValue()，值用枚举不用魔法串 | BizNoticeStatusEnum |
| Javadoc | 类与方法都带，含 `@author 名字` `@date yyyy/MM/dd HH:mm` | 格式见下节 |

## Javadoc 格式规范（求整齐，只写一行）

**固定四行，不多不少**：中文摘要一行 → 空行 → `@author` → `@date`。

```java
/**
 * 解除订阅
 *
 * @author yubaoshan
 * @date 2026/08/27 10:00
 */
```

| 要求 | 说明 |
|---|---|
| 摘要**只有一行** | 一句话说清做什么，不换行、不加句号；写不下就说明方法职责太杂，该拆方法而不是加注释 |
| 只允许 `@author` `@date` 两个标签 | date 格式 `yyyy/MM/dd HH:mm`；注释块 `*/` 收尾（平台老代码的 `**/` 不再沿用） |
| 类头同样只写一行 | 类名已表达含义，`XXX控制器` / `XXX服务实现类` 即可 |

### 禁止出现在 Javadoc 里的东西

| 禁止 | 原因 |
|---|---|
| `@param` / `@return` / `@throws` | 参数名与 Param 类名已自解释，写了就是复述签名；改签名还容易漏改成为错误注释 |
| 摘要下方的补充说明段落 | 属于实现细节，写在方法体内的行注释里，别占 Javadoc |
| `<p>` `</p>` `<ul><li>` 等 HTML 标签 | 本项目 Javadoc 不追求 HTML 渲染，标签只会撑长注释 |
| `1. xxx 2. xxx` 多条列举 | 同上，说明逻辑分支请写在代码里 |

补充说明（如权限复用原因）移到方法体内或 `@SaCheckPermission` 旁的行注释。

> 存量说明：仓库现有数百处不符合四行式的 Javadoc（主要是 @param/@return 标签，少量多行说明段落和 `<p>` 标签，平台原生 CommonEntity、CommonResult、StpClientUtil 等都有），**不做批量回改**——本规范只约束新写与重构中顺手经过的代码，避免纯格式 diff 淹没 review。

## JSON / 日期约定

- JSON：hutool `JSONUtil`（parseObj/toJsonStr）优先；跨插件传对象用 `JSONObject`（不传对方的实体类），简单值可以直接用 String/`List<String>`/boolean 等
- 日期：Entity 的 datetime 字段用 `Date`（与 CommonEntity 审计字段、生成器 GenDbTypeUtil 一致）；只有按字符串存的日期列（varchar，如 BIRTHDAY、ENTRY_DATE）才用 String；分页查询的时间区间参数用 String（如 BizNoticePageParam 的 startCreateTime/endCreateTime）；全局 JSON 日期格式 `yyyy-MM-dd HH:mm:ss`（application.properties 的 spring.jackson.date-format）
- 对象复制：新增 `BeanUtil.toBean(param, Entity.class)`；编辑 `queryEntity(id)` 后 `BeanUtil.copyProperties(param, entity)`
- 集合操作：hutool `CollStreamUtil.toList(list, Xxx::getId)`、`ObjectUtil.isNotEmpty/isAllNotEmpty` 判空

## 前端禁令速查

TS / Element Plus 见上方惯性错误第 16、17 条；其余前端规范（请求封装、api 文件位置、i18n、注释、SFC 块顺序、按钮权限）统一维护在 frontend-pc 技能，写前端时激活该技能。

## Git 提交规范

格式 `【类别】中文描述`（类别：底座/新增/修复/优化/升级/更新），默认只本地 commit；完整规范见 git-workflow 技能。

## 避免过度工程

- 标准六件套够用就别加层（不要 DAO/Manager 层、不要 DTO 转换链）
- 没有复用诉求不抽公共方法；两处以内重复可接受
- 不引入新依赖前先查 hutool / snowy-common 是否已有（见 common-toolkit 技能）

## 检查清单（提交前）

- [ ] 17 条常见脚手架惯性错误全部规避
- [ ] 平台自带文件的版权头未被删（新业务代码不强制）、注释中文；自定义包名已补启动模块扫描配置；Javadoc 为「一行摘要 + @author + @date」四行式，无 @param/@return/`<p>`/补充段落
- [ ] 命名符合前缀与后缀规范
- [ ] 前端无 TS / Element Plus 残留
- [ ] commit message 为【类别】中文描述

## 参考实现

| 文件 | 说明 |
|---|---|
| `snowy-plugin/snowy-plugin-biz/src/main/java/vip/xiaonuo/biz/modular/notice/` | 全部规范的活样本 |
| `snowy-plugin/snowy-plugin-biz/src/main/java/vip/xiaonuo/biz/modular/user/entity/BizUser.java` | 复杂 Entity（@Trans/SM4） |
| `snowy-plugin/snowy-plugin-sys/src/main/java/vip/xiaonuo/sys/modular/user/service/impl/SysUserServiceImpl.java` | 复杂业务写法 |
| `AGENTS.md` | 项目宪法（必须遵守的规范与易错点节） |
