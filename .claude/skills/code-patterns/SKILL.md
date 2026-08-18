---
name: code-patterns
description: Snowy 全量编码禁令速查表（含"来自 RuoYi 的惯性错误"专栏）、命名规范、JSON/日期约定、Git 提交规范。触发场景：1) 写任何 Java/前端代码前的规范自查 2) 审查代码风格问题 3) 提交代码。触发词：编码规范、禁令、命名、代码风格、惯性错误、RuoYi、JSON、日期、Git 提交、commit。注意：本技能是速查表；分层结构见 crud-development，模块组织见 plugin-architecture。
---

# Snowy 编码禁令速查表

## ⚠️ 来自 RuoYi / RuoYi-Vue-Plus 的惯性错误（最高优先级）

AI 对 RuoYi 系框架先验极强，**以下每条都是反向的**，写代码前先扫一遍：

| # | ❌ RuoYi 惯性写法 | ✅ Snowy 正确写法 |
|---|---|---|
| 1 | `implements IXxxService`（不继承） | `extends ServiceImpl<XxxMapper, Xxx> implements XxxService` |
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
| 13 | `del_flag` / `@TableLogic` 自己声明 | 继承 CommonEntity（字段 DELETE_FLAG） |
| 14 | `@Log(title=..., businessType=...)` | `@CommonLog("中文标题")` |
| 15 | `LambdaQueryWrapper` + `buildQueryWrapper()` 方法 | `QueryWrapper<Xxx>().checkSqlInjection()` + `queryWrapper.lambda()` 内联 |
| 16 | 前端 Element Plus / plus-ui 组件 | Ant Design Vue 4 组件（a-button 等） |
| 17 | 前端 TypeScript | JavaScript（本项目前端无 TS） |

## 后端禁令速查表

| 禁止 | 替代 | 原因 |
|---|---|---|
| 包名 `com.*` / `org.*` | `vip.xiaonuo.*` | 包结构即模块边界 |
| 删除/省略 12 行版权头 | 每个 .java 必带 | Apache 2.0 协议要求 |
| `@Autowired` | `@Resource` | 项目统一风格（240+ 处 vs 2 处） |
| `@Data` | `@Getter @Setter` | 避免相等性/构造器副作用 |
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
| Java 类前缀 | = 插件缩写 | BizSupplier、SysUser、DevConfig、GenBasic、AuthThird、ClientUser、MobileResource |
| Service 接口/实现 | `XxxService` / `XxxServiceImpl` | BizSupplierService / BizSupplierServiceImpl |
| Mapper / XML | `XxxMapper` / `mapping/XxxMapper.xml` 同包 | BizSupplierMapper |
| Param 后缀 | PageParam / AddParam / EditParam / IdParam / SelectorXxxParam / ExportParam | BizSupplierPageParam |
| Result 后缀 | `XxxResult` | BizSupplierResult |
| Controller URL | `/biz/supplier/page` 小写驼峰域名 | — |
| 数据库 | 全大写 | BIZ_SUPPLIER |
| 枚举 | `XxxEnum`，含 getValue()，值用枚举不用魔法串 | BizNoticeStatusEnum |
| Javadoc | 类与方法都带，含 `@author 名字` `@date yyyy/MM/dd HH:mm` | — |

## JSON / 日期约定

- JSON：hutool `JSONUtil`（parseObj/toJsonStr）优先；跨插件传值一律 `JSONObject`
- 日期：Entity 用 `Date` 或 String（现有代码以 String 居多，如 startCreateTime/endCreateTime 查询参数）；格式统一 `yyyy-MM-dd HH:mm:ss`
- 对象复制：新增 `BeanUtil.toBean(param, Entity.class)`；编辑 `queryEntity(id)` 后 `BeanUtil.copyProperties(param, entity)`
- 集合操作：hutool `CollStreamUtil.toList(list, Xxx::getId)`、`ObjectUtil.isNotEmpty/isAllNotEmpty` 判空

## 前端禁令速查

| 禁止 | 替代 |
|---|---|
| TypeScript 语法（interface、type、as） | 纯 JavaScript |
| Element Plus 组件 | Ant Design Vue（a-xxx） |
| 自造请求封装 | `src/utils/request.js` 的 baseRequest / clientRequest |
| api 文件乱放 | `src/api/{插件}/{xxx}Api.js` |
| 页面文案硬编码中文 | `$t('xxx')` + locales 补词条（遵循现有 i18n） |
| 按钮权限乱写 | `hasPerm('bizXxxAdd')` 驼峰码，数组与 'and'/'or' 组合 |

## Git 提交规范

```
<type>: <subject>            # subject 中文，一行，不加句号

type ∈ feat|fix|refactor|docs|style|test|chore|perf
示例：
feat: 新增供应商管理六件套
fix: 修复供应商分页排序字段未下划线转换的问题
docs: 补充后端开发指南的跨插件调用章节
```

- 只提交本次任务相关文件；**不提交** application.properties 的本地口令改动、node_modules、target
- 默认只做本地 commit，明确要求才 push（当前仓库无远程）
- 分支：master 单分支（个人项目）；大重构前打 tag

## 避免过度工程

- 标准六件套够用就别加层（不要 DAO/Manager 层、不要 DTO 转换链）
- 没有复用诉求不抽公共方法；两处以内重复可接受
- 不引入新依赖前先查 hutool / snowy-common 是否已有（见 common-toolkit 技能）

## 检查清单（提交前）

- [ ] 17 条 RuoYi 惯性错误全部规避
- [ ] 版权头齐全、注释中文、Javadoc 带 @author @date
- [ ] 命名符合前缀与后缀规范
- [ ] 前端无 TS / Element Plus 残留
- [ ] commit message 符合 type: 中文 描述

## 参考实现

| 文件 | 说明 |
|---|---|
| `snowy-plugin/snowy-plugin-biz/src/main/java/vip/xiaonuo/biz/modular/notice/` | 全部规范的活样本 |
| `snowy-plugin/snowy-plugin-biz/src/main/java/vip/xiaonuo/biz/modular/user/entity/BizUser.java` | 复杂 Entity（@Trans/SM4） |
| `snowy-plugin/snowy-plugin-sys/src/main/java/vip/xiaonuo/sys/modular/user/service/impl/SysUserServiceImpl.java` | 复杂业务写法 |
| `CLAUDE.md` | 项目宪法（易错点警告节） |
