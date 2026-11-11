---
name: cache-redis
description: Snowy 缓存使用规范：CommonCacheOperator 统一操作器、Redisson 客户端、缓存 key 常量、数据变更事件联动刷新、alone-redis 独立配置。触发场景：1) 业务需要读写缓存 2) 改完数据要刷新缓存 3) 需要分布式锁或 Redis 原生结构。触发词：缓存、Redis、Redisson、CommonCacheOperator、CacheConstant、缓存刷新、缓存key、分布式锁、缓存过期。
---

# Snowy 缓存使用规范

## 基础设施

| 组件 | 位置 | 说明 |
|---|---|---|
| `CommonCacheOperator` | `snowy-common/src/main/java/vip/xiaonuo/common/cache/CommonCacheOperator.java` | **统一缓存操作器**（注入 RedissonClient，用 RBucket 读写，key 自动加 `Cache:` 前缀；2026-06-30 起由 RedisTemplate 改为 Redisson） |
| `RedissonConfig` | `snowy-web-app/src/main/java/vip/xiaonuo/core/config/RedissonConfig.java` | Redisson 客户端装配：`redissonClient`（@Primary，业务缓存，库号取 `spring.data.redis.database`，出厂 1，编解码 JsonJacksonCodec）+ `aloneRedissonClient`（读 `sa-token.alone-redis.*`，供 Sa-Token 的 SaTokenDaoForRedisson 使用）；另保留一个 RedisTemplate Bean 用于兼容 |
| `CacheConstant` | `snowy-common/src/main/java/vip/xiaonuo/common/consts/CacheConstant.java` | 框架级缓存 key 常量 |
| 数据变更事件 | `snowy-common/src/main/java/vip/xiaonuo/common/listener/CommonDataChangeEventCenter.java` | 增删改后在**本进程内同步**通知已注册的 CommonDataChangeListener，各插件在监听器里刷新自己的缓存 |

连接：`application.properties` 的 `spring.data.redis` 段（127.0.0.1:6379，database 1）；Sa-Token 通过 `sa-token.alone-redis.*` 独立配置（默认引用 spring.data.redis 的 host/port，与业务缓存同一个 Redis 实例；库号以 `sa-token.alone-redis.database` 配置为准）。

## CommonCacheOperator API（业务缓存首选）

```java
@Resource
private CommonCacheOperator commonCacheOperator;

commonCacheOperator.put(key, value);                       // 永久（逻辑过期由业务控制）
commonCacheOperator.put(key, value, 60);                   // 60 秒过期
Object v = commonCacheOperator.get(key);                   // 取（无则 null）
commonCacheOperator.remove(key1, key2);                    // 删一个或多个
commonCacheOperator.removeBatch("prefix:*");               // 按模式批量删
commonCacheOperator.getAllKeys();                          // 全部 key（调试用）
```

- key 命名沿用项目现有风格 `{插件}-{对象}:{标识}`（如 `biz-org:all-list`、`biz-supplier:count`），用短横线 + 冒号分层，在本插件的 ServiceImpl 里定义成常量；CommonCacheOperator 会自动加 `Cache:` 前缀，不要自己再拼；框架已占用的前缀见 CacheConstant（`permission-resource`、`auth-b-permission-list:` 等，**不要冲突**）
- 值可以直接存对象（JsonJacksonCodec 编解码），不需要先 JSON 序列化；实体/集合取回后用 `JSONUtil.toBean / toList` 转回目标类型（平台写法，参考 BizOrgServiceImpl.getAllOrgList），简单值（String/Integer）可直接强转（如 AuthServiceImpl 的登录失败计数）

## 需要原生 Redis 结构/锁时：RedissonClient

```java
@Resource
private RedissonClient redissonClient;                     // 注入的是 @Primary 的业务 redissonClient；直接用它的 key 不带 Cache: 前缀

// 分布式锁
RLock lock = redissonClient.getLock("biz-order:lock:" + orderId);
try {
    if(lock.tryLock(3, 10, TimeUnit.SECONDS)) {           // 等3秒，持10秒
        // 临界区
    }
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
} finally {
    if(lock.isHeldByCurrentThread()) {
        lock.unlock();                                     // 必须 finally 且校验持有者
    }
}

// 其他结构
RMap<String, Object> map = redissonClient.getMap("biz-xxx:map");
RAtomicLong counter = redissonClient.getAtomicLong("biz-xxx:counter");
```

## 数据变更事件（缓存联动的标准姿势）

改了被缓存的数据源后，广播事件让监听方刷新：

```java
// 写操作后发事件（在 Service 的 add/edit/delete 里）
CommonDataChangeEventCenter.doAddWithData("XXX", JSONUtil.parseObj(entity));            // 新增
CommonDataChangeEventCenter.doUpdateWithData("XXX", JSONUtil.parseObj(entity));         // 更新
CommonDataChangeEventCenter.doDeleteWithDataId("XXX", entity.getId());         // 删除

// 各插件在 core/listener/ 实现监听（参考 BizDataChangeListener）
```

现有消费方：SysDataChangeListener / BizDataChangeListener 收到 ORG（机构）事件后清理机构缓存（sys 侧还会同步当前用户的数据范围缓存）；出厂只有 ORG 有实际消费逻辑，其余类型是预留扩展点。字典/配置缓存**不走事件**，由平台 Service 在写操作后直接刷新（DevDictServiceImpl / BizDictServiceImpl 调 refreshTransCache，DevConfigServiceImpl 直接 `commonCacheOperator.remove`）。**不发事件 = 其他插件的监听器无法清理相关缓存**（事件只在当前 JVM 内同步分发，多实例部署时其他节点收不到）。

## 使用原则

| 场景 | 方案 |
|---|---|
| 普通 key-value 业务缓存 | CommonCacheOperator（+ 过期秒数） |
| 计数器/防重/限流 | RedissonClient 的 RAtomicLong / RLock |
| 改了被其他插件缓存或依赖的数据（如机构） | 发 CommonDataChangeEventCenter 事件，并在对方 core/listener 中实现消费 |
| 登录用户信息/权限 | 框架已管（Sa-Token TokenSession），业务不要碰 |

## 常见错误正误对照

| 错误 | 正确 |
|---|---|
| `stringRedisTemplate.opsForValue()...` | `commonCacheOperator.put/get`（统一 `Cache:` 前缀和序列化方式，业务不要绕过操作器直连 Redis） |
| 直接注入 `RedisTemplate` | Redisson 体系（CommonCacheOperator/RedissonClient）；RedissonConfig 仍保留 RedisTemplate Bean 用于兼容，业务不要注入 |
| 自建缓存的数据改了不清缓存 | 写方法里直接 remove/刷新；需要其他插件感知时再发 CommonDataChangeEventCenter 事件 |
| 分布式锁 unlock 不判持有者 | `if(lock.isHeldByCurrentThread()) lock.unlock()` 在 finally |
| key 无插件前缀/无分层 | `{插件}-{对象}:{标识}` |
| 缓存业务对象手动 JSON.toJSONString | 直接存对象（出厂 BizOrgServiceImpl.getVisibleOrgIds 中手动 toJsonStr 的写法属历史遗留） |

## 检查清单

- [ ] 用的 CommonCacheOperator 而非自造 Redis 封装
- [ ] key 有插件前缀分层，不与 CacheConstant 冲突
- [ ] 有过期时间的缓存设置了秒数
- [ ] 自建缓存的数据变更后清了缓存；需要其他插件感知时发了 DataChangeEvent
- [ ] 分布式锁在 finally 释放且校验持有者
- [ ] 本地 Redis 已启动（127.0.0.1:6379 database 1）

## 参考实现

| 文件 | 说明 |
|---|---|
| `snowy-common/src/main/java/vip/xiaonuo/common/cache/CommonCacheOperator.java` | 操作器源码 |
| `snowy-common/src/main/java/vip/xiaonuo/common/consts/CacheConstant.java` | 框架 key 常量 |
| `snowy-common/src/main/java/vip/xiaonuo/common/listener/CommonDataChangeEventCenter.java` | 事件中心 |
| `snowy-plugin/snowy-plugin-biz/src/main/java/vip/xiaonuo/biz/core/listener/BizDataChangeListener.java` | 监听实现范本 |
| `snowy-web-app/src/main/java/vip/xiaonuo/core/config/RedissonConfig.java` | Redisson 装配 |
