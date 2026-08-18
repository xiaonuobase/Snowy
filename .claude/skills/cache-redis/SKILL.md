---
name: cache-redis
description: Snowy 缓存使用规范：CommonCacheOperator 统一操作器、Redisson 客户端、缓存 key 常量、数据变更事件联动刷新、alone-redis 独立配置。触发场景：1) 业务需要读写缓存 2) 改完数据要刷新缓存 3) 需要分布式锁或 Redis 原生结构。触发词：缓存、Redis、Redisson、CommonCacheOperator、CacheConstant、缓存刷新、缓存key、分布式锁、缓存过期。
---

# Snowy 缓存使用规范

## 基础设施

| 组件 | 位置 | 说明 |
|---|---|---|
| `CommonCacheOperator` | `snowy-common/src/main/java/vip/xiaonuo/common/cache/CommonCacheOperator.java` | **统一缓存操作器**（封装 Redisson Bucket，key 自动加前缀） |
| `RedissonConfig` | `snowy-web-app/src/main/java/vip/xiaonuo/core/config/RedissonConfig.java` | Redisson 客户端装配（默认 database 1） |
| `CacheConstant` | `snowy-common/src/main/java/vip/xiaonuo/common/consts/CacheConstant.java` | 框架级缓存 key 常量 |
| 数据变更事件 | `snowy-common/src/main/java/vip/xiaonuo/common/listener/CommonDataChangeEventCenter.java` | 增删改后广播事件，各插件监听刷新自己的缓存 |

连接：`application.properties` 的 `spring.data.redis` 段（127.0.0.1:6379，database 1）；Sa-Token 用 alone-redis 独立配置（同实例）。

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

- key 命名：`{业务}:{对象}:{标识}` 小写冒号分层，如 `biz:supplier:count`；框架已占用的前缀见 CacheConstant（`permission-resource`、`auth-b-permission-list:` 等，**不要冲突**）
- 值直接存对象（Redisson 编解码），不需要先 JSON 序列化

## 需要原生 Redis 结构/锁时：RedissonClient

```java
@Resource
private RedissonClient redissonClient;

// 分布式锁
RLock lock = redissonClient.getLock("biz:order:lock:" + orderId);
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
RMap<String, Object> map = redissonClient.getMap("biz:xxx:map");
RAtomicLong counter = redissonClient.getAtomicLong("biz:xxx:counter");
```

## 数据变更事件（缓存联动的标准姿势）

改了被缓存的数据源后，广播事件让监听方刷新：

```java
// 写操作后发事件（在 Service 的 add/edit/delete 里）
CommonDataChangeEventCenter.doAddWithData(Xxx.class);            // 新增
CommonDataChangeEventCenter.doUpdateWithData(Xxx.class);         // 更新
CommonDataChangeEventCenter.doDeleteWithData(Xxx.class);         // 删除

// 各插件在 core/listener/ 实现监听（参考 BizDataChangeListener）
```

典型消费方：sys 的权限/资源缓存、easy-trans 字典缓存。**改字典/资源/用户相关数据后不发事件 = 别的节点/模块读到旧缓存**。

## 使用原则

| 场景 | 方案 |
|---|---|
| 普通 key-value 业务缓存 | CommonCacheOperator（+ 过期秒数） |
| 计数器/防重/限流 | RedissonClient 的 RAtomicLong / RLock |
| 改了共享基础数据（字典/配置/资源） | 发 CommonDataChangeEventCenter 事件 |
| 登录用户信息/权限 | 框架已管（Sa-Token TokenSession），业务不要碰 |

## 常见错误正误对照

| ❌ | ✅ |
|---|---|
| `stringRedisTemplate.opsForValue()...` | `commonCacheOperator.put/get`（项目无 StringRedisTemplate 装配） |
| 直接注入 `RedisTemplate` | Redisson 体系（CommonCacheOperator/RedissonClient） |
| 改字典后不广播事件 | `CommonDataChangeEventCenter.doUpdateWithData(...)` |
| 分布式锁 unlock 不判持有者 | `if(lock.isHeldByCurrentThread()) lock.unlock()` 在 finally |
| key 大写/无分层 | 小写冒号分层 `{业务}:{对象}:{id}` |
| 缓存业务对象手动 JSON.toJSONString | 直接存对象 |

## 检查清单

- [ ] 用的 CommonCacheOperator 而非自造 Redis 封装
- [ ] key 有业务前缀分层，不与 CacheConstant 冲突
- [ ] 有过期时间的缓存设置了秒数
- [ ] 数据变更后发了 DataChangeEvent（涉及共享数据时）
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
