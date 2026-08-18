# Snowy 二次开发文档中心

> 阅读顺序：先读根目录 `CLAUDE.md`（项目宪法），再按需查本目录指南；按需知识（技能）在 `.claude/skills/`，由 hook 自动激活。

## 文档导航

| 文档 | 内容 | 什么时候看 |
|---|---|---|
| [框架说明](框架说明.md) | Maven 模块拓扑、插件机制、启动链路、B/C 双端模型、29 项平台功能清单 | 想了解"平台已经有什么、不用我开发什么" |
| [后端开发指南](后端开发指南.md) | 六件套逐文件详解（以 BizNotice 为线）、跨插件调用 | 写后端代码前 |
| [前端开发指南](前端开发指南.md) | snowy-admin-web：api js / index.vue / form.vue / 组件 / i18n | 写前端代码前 |
| [数据库设计规范](数据库设计规范.md) | 大写表名、审计字段、建表模板、33 表清单、菜单 SQL | 建表/写 SQL 前 |
| [工具类使用指南](工具类使用指南.md) | snowy-common 16 工具类 + Hutool 优先原则 | 找工具时 |
| [新功能开发流程规范](新功能开发流程规范.md) | /dev 双模式全流程、验证方式 | 开工一个新功能时 |
| [国密与安全指南](国密与安全指南.md) | SM2/SM3/SM4、双端鉴权、白名单、数据范围 | 涉及密码/敏感字段/权限时 |

## 按开发阶段查阅

- **刚接手**：CLAUDE.md → 框架说明 → `/start`
- **要开发**：新功能开发流程规范 → 后端/前端开发指南 → 数据库设计规范
- **遇到问题**：`.claude/skills/bug-detective` → 国密与安全指南（涉密时）
- **要提交**：`.claude/skills/code-patterns` 的 Git 规范 → `/check`

## 核心规范速查（背下来）

| 项 | 规范 |
|---|---|
| 包名 | `vip.xiaonuo.*`（业务在 biz 插件） |
| 注入 | `@Resource`（禁 @Autowired / 构造器注入） |
| ServiceImpl | `extends ServiceImpl<M, T> implements XxxService` |
| 转换 | Hutool `BeanUtil`（禁 MapstructUtils） |
| Lombok | `@Getter @Setter`（禁 @Data） |
| 返回/异常 | `CommonResult<T>` / `CommonException`（中文消息） |
| URL | `/{插件}/{域}/{动作}` 动词式，方法注解上，权限码=URL |
| 表/主键 | 全大写 `BIZ_XXX` / String 雪花 |
| 版权头 | 每个 .java 头部 12 行 Apache 2.0 声明，不可删 |
| 注释 | 全中文，Javadoc 带 @author @date |

## 禁止事项（Top 10）

1. 删版权头
2. RuoYi 风格代码（@Autowired/MapstructUtils/@Data/RESTful URL/冒号权限码/Long 主键/小写表名）
3. 业务代码写进 sys/auth/dev/gen 等 6 个平台插件
4. 跨插件直接 import 对方实体
5. 明文存储口令/敏感字段（必须国密）
6. 写操作不加事务/操作日志
7. 查询不带 checkSqlInjection
8. 前端写 TS / Element Plus
9. 手写 delete_flag 条件（逻辑删除自动）
10. 硬编码数据库口令
