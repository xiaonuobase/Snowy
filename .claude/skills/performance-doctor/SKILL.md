---
name: performance-doctor
description: Snowy 性能优化规范：慢查询与索引、N+1、分页规范、缓存策略、@Trans 批量翻译、列表页大数据。触发场景：1) 接口慢/列表加载久 2) 优化 SQL 与索引 3) 缓存策略选型 4) 大数据量处理。触发词：性能、慢、优化、慢查询、索引、N+1、卡顿、内存、大数据量、分页、批量、缓存策略。
---

# Snowy 性能优化指南

## 诊断流程

```
接口慢
├─ 后端慢？ Knife4j 单测接口计时（排除前端）
│   ├─ SQL 慢 → Druid 监控（/druid；出厂默认关闭，需把 application.properties 的 `spring.datasource.druid.stat-view-servlet.enabled` 改为 true，账号见同段配置）看慢 SQL / 执行计划 EXPLAIN
│   ├─ 循环查库（N+1）→ 看代码 for 里有没有 getById/listBy
│   └─ 翻译慢 → @Trans 相关表数据量
└─ 前端慢？ 浏览器 Network 看接口耗时 vs 渲染耗时
```

## 查询优化规范

1. **必须分页**：会随业务增长的表格列表接口一律 page（CommonPageRequest 上限 100）；树、选择器、关联回显、字典等有界数据可以返回 List（如 /biz/org/tree、/biz/dict/tree）；没有条数上限的明细**禁止**用 list 全量返回
2. **条件判空**：可选的外部查询参数用 ObjectUtil.isNotEmpty 包裹（避免空值产生无效条件、无谓的全表 like）；固定条件（如 category=BIZ）和已用 @NotBlank 校验的必填参数不用包裹
3. **索引**：高频查询字段建索引（查询字段、外键、时间范围字段）；EXPLAIN 确认命中
   - 注：出厂表除 SYS_USER_DATA_SCOPE / SYS_USER_DATA_SCOPE_MAP 外基本只有主键，不能当索引范本；本条针对新建 BIZ_ 表和已确认慢的查询，检查时不对出厂表报"缺索引"
4. **排序**：sortField 走 `StrUtil.toUnderlineCase` 后是列名直排——确保排序列有索引
5. **count 优化**：深分页大表按业务限制时间范围（以 EXPLAIN/慢 SQL 为依据）

## N+1 与批量化

```java
// 错误：N+1：循环里查库
for(Order order : orders) {
    JSONObject user = sysUserApi.getUserByIdWithoutException(order.getUserId());   // N 次调用
}

// 正确：批量：一次查完再内存关联
List<String> userIds = CollStreamUtil.toList(orders, Order::getUserId);
List<JSONObject> users = sysUserApi.getUserListByIdListWithoutException(userIds);
Map<String, JSONObject> userMap = users.stream()
        .collect(Collectors.toMap(j -> j.getStr("id"), j -> j, (a, b) -> a));
orders.forEach(o -> o.setUserName(userMap.get(o.getUserId()).getStr("name")));
```

（api 已提供 `getXxxListByIdList` 批量版本，优先用。）

## @Trans 翻译性能

- DICTIONARY 翻译走 Redis 缓存（字典增改删时由字典服务自动刷新）；SIMPLE 翻译默认**没有跨请求缓存**，每次响应都按 id 批量查 target 表（同一请求内去重）
- target 表大、列表页 SIMPLE 字段多时，给 target 实体加 `@TransDefaultSett(isUseCache = true, cacheSeconds = N)`，或改为手写批量查询（见上）

## 缓存策略（详见 cache-redis 技能）

| 数据特征 | 策略 |
|---|---|
| 读多写少的字典/配置 | 缓存 + 写方法内直接刷新或失效（参考 DevConfigServiceImpl.edit 的 commonCacheOperator.remove、DevDictServiceImpl 的 refreshTransCache）；需要其他插件感知变更时，再发 CommonDataChangeEventCenter 数据变更事件（平台在用户/机构/职位/角色/资源上这样用） |
| 热点统计（首页数字） | 小表直接 count() 即可（平台 SysIndexServiceImpl.getBizDataCount 就是这样）；大表或聚合较重的统计再用 CommonCacheOperator 定时过期（如 60s）+ 定时任务预热 |
| 计数/防重 | 注入 RedissonClient（RedissonConfig 装配）用 RAtomicLong / RLock |
| 用户维度临时数据 | 带 TTL 的 key（含 userId） |

## 大数据量处理

- 导入导出：数据量大时用 EasyExcel 分批处理（导出用 ExcelWriter + 分页查询逐页 write；导入用 PageReadListener 每批入库），**不要**一次性加载全部到 List。注意：平台 SysUserServiceImpl 的 exportUser/importUser 是整批 list()/doReadSync() 实现，只适合小数据量，不能当流式范本
- 定时批处理：分批（几百/批）+ 幂等（见 scheduled-jobs 技能）
- 大文本：EXT_JSON longtext 别当查询字段（平台 DevMessageServiceImpl.unreadCount 对 EXT_JSON 做 like 是存量写法，不要模仿；需要查询的属性拆成独立列）

## 前端性能

- 长列表用 s-table 自带分页（不要一次拉全量）
- 字典/选项用 `tool.dictList('字典编码')`（读取登录后缓存的 DICT_TYPE_TREE_DATA，不发请求），也可以用 `<dict-select>` 组件（src/components 自动注册，内部同样读 tool.dictList）；不要为了下拉框重复调用字典接口
- 大表单字段多时按 Tab 分组（a-tabs）

## 常见错误正误对照

| 错误 | 正确 |
|---|---|
| 循环单查关联数据 | 批量接口 + 内存 Map 关联 |
| 可增长的明细列表用 list() 全量返回 | page 分页 |
| 无索引字段当查询条件 | 建索引或改查询设计 |
| 大表聚合统计每次请求实时算 | 缓存/定时任务预热 |
| selectList(null) 全表 | 带条件 + 分页 |

## 检查清单

- [ ] 列表接口分页，可选查询参数判空
- [ ] 无 N+1（循环内不调用 getById/list/count/跨插件单查；平台 getChildListById(allList, …) 这类基于已加载 List 的重载不查库，不算 N+1）；存量反例 SysOrgServiceImpl.copy 不要模仿
- [ ] 高频查询字段有索引
- [ ] 热点读走缓存且刷新链路完整
- [ ] 大数据量导入导出已分批

## 参考实现

| 文件 | 说明 |
|---|---|
| `snowy-plugin/snowy-plugin-dev/src/main/java/vip/xiaonuo/dev/modular/monitor/` | 服务器监控（内存/线程排查入口） |
| `snowy-plugin/snowy-plugin-sys/src/main/java/vip/xiaonuo/sys/modular/user/service/impl/SysUserServiceImpl.java` | 批量查询实战 |
| `snowy-common/src/main/java/vip/xiaonuo/common/cache/CommonCacheOperator.java` | 缓存操作器 |
| `snowy-web-app/src/main/resources/application.properties` | Druid 监控配置 |
