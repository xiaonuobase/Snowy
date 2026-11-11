# Snowy 二次开发文档中心

> 阅读顺序：先读根目录 `AGENTS.md`（项目宪法），再按需查本目录指南；按需知识（技能）在 `.agents/skills/`，Claude Code、Codex、Qwen Code 由钩子自动提醒激活（其他工具无钩子，按 AGENTS.md 的技能使用规则自觉读取）。

## 文档导航

| 文档 | 内容 | 什么时候看 |
|---|---|---|
| [框架说明](框架说明.md) | Maven 模块拓扑、插件机制、启动链路、B/C 双端模型、平台功能清单 | 想了解"平台已经有什么、不用我开发什么" |
| [后端开发指南](后端开发指南.md) | 六件套逐文件详解（以 BizNotice 为线）、跨插件调用 | 写后端代码前 |
| [前端开发指南](前端开发指南.md) | snowy-admin-web：api js / index.vue / form.vue / 组件 / i18n | 写前端代码前 |
| [数据库设计规范](数据库设计规范.md) | 大写表名、审计字段、建表模板、33 表清单、菜单 SQL | 建表/写 SQL 前 |
| [工具类使用指南](工具类使用指南.md) | snowy-common 工具类 + Hutool 优先原则 | 找工具时 |
| [新功能开发流程规范](新功能开发流程规范.md) | dev 流程技能双模式全流程、验证方式 | 开工一个新功能时 |
| [国密与安全指南](国密与安全指南.md) | SM2/SM3/SM4、双端鉴权、白名单、数据范围 | 涉及密码/敏感字段/权限时 |

## 按开发阶段查阅

- **刚接手**：AGENTS.md → 框架说明 → start 流程技能（Claude Code、Qwen Code `/start`，Codex `$start`）
- **要开发**：新功能开发流程规范 → 后端/前端开发指南 → 数据库设计规范
- **遇到问题**：`.agents/skills/bug-detective` → 国密与安全指南（涉密时）
- **要提交**：`.agents/skills/code-patterns` 的 Git 规范 → check 流程技能（Claude Code、Qwen Code `/check`，Codex `$check`）

## 核心规范速查（背下来）

| 项 | 规范 |
|---|---|
| 包名 | 平台代码 `vip.xiaonuo.*`；自建业务可用自定义包名（未指定沿用目标插件现有包名，非 vip.xiaonuo 开头需补扫描配置，见 plugin-architecture 技能"使用自定义包名"） |
| 代码位置 | 业务写在 snowy-plugin-biz 或自建业务插件（snowy-plugin/ 下与 biz 平级），不改平台插件与 snowy-common |
| 注入 | `@Resource`（禁 @Autowired / 构造器注入） |
| ServiceImpl | 有实体的服务 `extends ServiceImpl<M, T> implements XxxService` |
| 转换 | Hutool `BeanUtil`（禁 MapstructUtils） |
| Lombok | `@Getter @Setter`（禁 @Data） |
| 返回/异常 | `CommonResult<T>` / `CommonException`（中文消息） |
| URL | `/{插件}/{域}/{动作}` 动词式，方法注解上，权限码=URL |
| 表/主键 | 全大写，前缀 = 所在插件（`BIZ_XXX` / 自建插件 `ORDER_XXX`） / String 雪花 |
| 版权头 | 平台自带代码保留 12 行 Apache 2.0 声明，修改时不删；自建业务代码不强制，默认不加小诺版权声明（用户有自己的规范按用户的） |
| 注释 | 全中文，Javadoc 带 @author @date |

## 禁止事项（Top 10）

1. 删平台自带代码的版权头
2. 同类框架风格代码（@Autowired/MapstructUtils/@Data/RESTful 路径变量与 PUT/DELETE/冒号权限码/Long 主键/小写表名；不分页列表 GET `/list` 是正常写法）
3. 业务代码写进 sys/auth/dev/gen/client/mobile 6 个平台插件或 snowy-common
4. 跨插件直接 import 对方实体
5. 明文存储口令/敏感字段（必须国密）
6. 写操作不加事务/操作日志
7. 分页/动态排序查询的 QueryWrapper 不带 checkSqlInjection（固定条件、唯一性校验用 LambdaQueryWrapper 是正常写法）
8. 前端写 TS / Element Plus
9. 手写 delete_flag 条件（逻辑删除自动）
10. 硬编码数据库口令
