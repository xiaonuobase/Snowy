---
name: performance-doctor
description: Snowy 性能优化规范：慢查询与索引、N+1、分页规范、缓存策略、@Trans 批量翻译、列表页大数据。触发场景：1) 接口慢/列表加载久 2) 优化 SQL 与索引 3) 缓存策略选型 4) 大数据量处理。触发词：性能、慢、优化、慢查询、索引、N+1、卡顿、内存、大数据量、分页、批量、缓存策略。
---

# Snowy 性能优化指南

## 诊断流程

```
接口慢
├─ 后端慢？ Knife4j 单测接口计时（排除前端）
│   ├─ SQL 慢 → Druid 监控（/druid，账号见配置）看慢 SQL / 执行计划 EXPLAIN
│   ├─ 循环查库（N+1）→ 看代码 for 里有没有 getById/listBy
│   └─ 翻译慢 → @Trans 相关表数据量
└─ 前端慢？ 浏览器 Network 看接口耗时 vs 渲染耗时
```

## 查询优化规范

1. **必须分页**：列表接口一律 page（CommonPageRequest 上限 100），禁止全量 list 返给前端
2. **条件判空**：QueryWrapper 每个条件 ObjectUtil.isNotEmpty 包裹（避免无谓的全表 like）
3. **索引**：高频查询字段建索引（查询字段、外键、时间范围字段）；EXPLAIN 确认命中
4. **排序**：sortField 走 `StrUtil.toUnderlineCase` 后是列名直排——确保排序列有索引
5. **count 优化**：深分页大表按业务限制时间范围

## N+1 与批量化

```java
// ❌ N+1：循环里查库
for(Order order : orders) {
    JSONObject user = sysUserApi.getUserByIdWithoutException(order.getUserId());   // N 次调用
}

// ✅ 批量：一次查完再内存关联
List<String> userIds = CollStreamUtil.toList(orders, Order::getUserId);
List<JSONObject> users = sysUserApi.getUserListByIdListWithoutException(userIds);
Map<String, JSONObject> userMap = users.stream()
        .collect(Collectors.toMap(j -> j.getStr("id"), j -> j, (a, b) -> a));
orders.forEach(o -> o.setUserName(userMap.get(o.getUserId()).getStr("name")));
```

（api 已提供 `getXxxListByIdList` 批量版本，优先用。）

## @Trans 翻译性能

- DICTIONARY/SIMPLE 翻译有 Redis 缓存，通常无忧
- target 表数据量大且变化频繁时缓存刷新成本高——高频大表关联翻译改手写批量查询（见上）
- 列表页 Entity 上 @Trans 字段多时，确认每个都有缓存支撑

## 缓存策略（详见 cache-redis 技能）

| 数据特征 | 策略 |
|---|---|
| 读多写少的字典/配置 | 缓存 + 数据变更事件刷新 |
| 热点统计（首页数字） | CommonCacheOperator 定时过期（如 60s）+ 定时任务预热 |
| 计数/防重 | Redisson RAtomicLong |
| 用户维度临时数据 | 带 TTL 的 key（含 userId） |

## 大数据量处理

- 导入导出：用 EasyExcel/EasyPoi 流式（参考 SysUserController.exportUser + DEV 文件模块），禁止一次性加载全部到 List
- 定时批处理：分批（几百/批）+ 幂等（见 scheduled-jobs 技能）
- 大文本：EXT_JSON longtext 别当查询字段

## 前端性能

- 长列表用 s-table 自带分页（不要一次拉全量）
- 字典/选项走 DictSelect 缓存；不重复拉
- 大表单字段多时按 Tab 分组（a-tabs）

## 常见错误正误对照

| ❌ | ✅ |
|---|---|
| 循环单查关联数据 | 批量接口 + 内存 Map 关联 |
| 列表接口 list() 全量返回 | page 分页 |
| 无索引字段当查询条件 | 建索引或改查询设计 |
| 每次请求实时算统计 | 缓存/定时任务预热 |
| selectList(null) 全表 | 带条件 + 分页 |

## 检查清单

- [ ] 列表接口分页且条件判空
- [ ] 无 N+1（循环内无查库）
- [ ] 高频查询字段有索引
- [ ] 热点读走缓存且刷新链路完整
- [ ] 导入导出流式处理

## 参考实现

| 文件 | 说明 |
|---|---|
| `snowy-plugin/snowy-plugin-dev/src/main/java/vip/xiaonuo/dev/modular/monitor/` | 服务器监控（内存/线程排查入口） |
| `snowy-plugin/snowy-plugin-sys/src/main/java/vip/xiaonuo/sys/modular/user/service/impl/SysUserServiceImpl.java` | 批量查询实战 |
| `snowy-common/src/main/java/vip/xiaonuo/common/cache/CommonCacheOperator.java` | 缓存操作器 |
| `snowy-web-app/src/main/resources/application.properties` | Druid 监控配置 |
