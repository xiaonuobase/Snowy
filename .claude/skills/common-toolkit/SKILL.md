---
name: common-toolkit
description: snowy-common 工具类地图与 Hutool 优先原则：CommonCacheOperator、CommonCryptogramUtil、CommonEmailUtil、CommonDownloadUtil 等 16 个工具类的用途索引与选择决策树。触发场景：1) 需要 utility（加密/邮箱校验/下载/IP/验证码/头像等）时先查这里 2) 不确定用哪个工具类 3) 想自写工具前检查是否已有。触发词：工具类、util、CommonCryptogramUtil、CommonEmailUtil、CommonDownloadUtil、Hutool、工具选择、复用。注意：缓存的完整用法见 cache-redis，国密详见 crypto-sm。
---

# Snowy 工具类地图

## 选择决策树（自上而下）

```
需要某能力
 ├─ snowy-common 里有 Common* 工具？ ──→ 用它（项目标准，含业务约定如缓存前缀/国密算法）
 ├─ 没有，hutool 里有？ ──→ 用 hutool（cn.hutool.*，项目已全局依赖 5.8.25）
 ├─ 也没有，Spring/Sa-Token/MP 官方 API？ ──→ 用官方
 └─ 都没有 ──→ 才自写；放对应插件 core/util/，通用才下沉 snowy-common
```

禁止：重复造轮子、引入 commons-lang3/guava 等冗余依赖（hutool 已覆盖）。

## snowy-common/util/ 全部 16 个工具类

| 工具类 | 用途 | 常用方法/说明 |
|---|---|---|
| `CommonCryptogramUtil` | 国密三件套 | doSm2Encrypt/doSm2Decrypt（登录密码传输）、doHashValue（口令摘要）、doSm4CbcEncrypt/doSm4CbcDecrypt、后端解密登录密码用（详见 crypto-sm 技能） |
| `CommonEmailUtil` | 邮箱格式校验 | isNotEmail / validEmail；**不负责发送**，发邮件走 dev-api 的 `DevEmailApi`（详见 sms-mail 技能） |
| `CommonDownloadUtil` | 文件下载 | 通过 HttpServletResponse 写流下载（Controller 导出场景用它，不要手写 IO） |
| `CommonAvatarUtil` | 随机头像 | 生成默认头像（新用户无头像时） |
| `CommonOtpUtil` | OTP 动态口令 | 生成/校验一次性验证码（登录 MFA 用） |
| `CommonSqlUtil` | SQL 兼容工具 | safeIn：IN 超过 999 项时自动拆分为多个 IN 用 OR 连接，兼容 Oracle；scopeIn：数据范围过滤改用预计算表（SYS_USER_DATA_SCOPE）子查询。大集合 IN 查询优先用它（形参要求 LambdaQueryWrapper） |
| `CommonResponseUtil` | 响应写出 | 向 response 直接写 JSON（过滤器/拦截器场景，Controller 不要用） |
| `CommonServletUtil` | Servlet 工具 | 请求参数/Request/Response 获取 |
| `CommonKeyUtil` | 国密密钥保护 | 密钥用 XOR 混淆存储，运行时用 reconstruct 还原；generateSm2KeyPair / generateSm4Key 用于开发期生成密钥并输出混淆代码（CommonCryptogramUtil 的密钥由它还原）；业务代码一般不直接调用。缓存 key 请用常量（框架级见 CacheConstant，命名见 cache-redis） |
| `CommonIpAddressUtil` | IP 归属地 | ip2region 离线库解析（登录日志属地显示） |
| `CommonUaUtil` | User-Agent | 解析浏览器/操作系统（登录日志设备显示） |
| `CommonTraceIdUtil` | 链路追踪 | traceId 生成（响应体里的 traceId） |
| `CommonTimeFormatUtil` | 时间友好化显示 | formatWxPastTime（仿微信"x 分钟前"）、formatSeconds（秒数转天时分秒）；**不提供** yyyy-MM-dd 格式化 |
| `CommonNetWorkInfoUtil` | 网速测量 | getNetworkUpRate（采样 2 秒，返回上下行速率，监控场景用）；不提供内外网判断 |
| `CommonJoinPointUtil` | 切面工具 | AOP 场景取参数/方法信息（日志切面用） |
| `CommonFilterExceptionUtil` | 过滤器异常 | 过滤器链里的统一错误输出 |

## common 下其他非 util 但常被当工具用的

| 类 | 位置 | 用途 |
|---|---|---|
| `CommonCacheOperator` | `common/cache/` | Redis 缓存统一操作（基于 RedissonClient，key 自动加 `Cache:` 前缀，详见 cache-redis 技能） |
| `CommonSm4CbcTypeHandler` | `common/handler/` | SM4-CBC 字段加密 TypeHandler |
| `CommonPageRequest` | `common/page/` | 分页请求 → MP Page |
| `CommonResult` / `CommonException` | `common/pojo/` / `exception/` | 统一返回/异常 |
| `CommonDataChangeEventCenter` | `common/listener/` | 数据变更事件广播（增删改后发事件刷新各方缓存） |
| `CacheConstant` | `common/consts/` | 缓存 key 常量（PERMISSION_RESOURCE_CACHE_KEY 等） |
| `CommonTimerTaskRunner` | `common/timer/` | 定时任务接口（详见 scheduled-jobs 技能） |
| `CommonDeleteAbsoluteMapper` | `common/mapper/` | 物理删除专用 Mapper（慎用） |

## Hutool 高频速查（本项目常用模块）

| 模块 | 常用类 | 场景 |
|---|---|---|
| `cn.hutool.core.util` | `StrUtil`（isNotBlank/...）、`ObjectUtil`（isEmpty/isNotEmpty/isAllNotEmpty）、`RandomUtil`、`IdUtil`、`ReflectUtil` | 字符串/对象判空、随机 |
| `cn.hutool.core.bean` | `BeanUtil`（toBean/copyProperties） | **对象转换唯一选择** |
| `cn.hutool.core.collection` | `CollStreamUtil`（toList）、`CollectionUtil`（newArrayList/unionAll） | 集合流/并集 |
| `cn.hutool.json` | `JSONUtil`（parseObj/toJsonStr）、`JSONObject` | JSON 与跨插件传值 |
| `cn.hutool.core.convert` | `Convert`（toList/toStr） | 类型转换 |
| `cn.hutool.core.io` | `IoUtil`、`FileUtil` | 文件流 |
| `cn.hutool.core.date` | `DateUtil` | 日期 |
| `cn.hutool.crypto` | `SmUtil` 等已被 CommonCryptogramUtil 封装 | 优先走 Common 层 |

## 常见错误正误对照

| 错误 | 正确 |
|---|---|
| `org.apache.commons.lang3.StringUtils` | `cn.hutool.core.util.StrUtil` |
| 手写 `MD5/SHA` 加密工具 | `CommonCryptogramUtil`（国密标准） |
| Controller 里 `response.getOutputStream()` 手写下载 | `CommonDownloadUtil` |
| 自己 new SimpleDateFormat 到处格式化 | 常规格式化用 hutool `DateUtil.format / formatDateTime`；`CommonTimeFormatUtil` 只用于友好化显示 |
| 业务里手拼 Redis key | key 定义成常量（框架级在 `CacheConstant`，业务 key 在本插件内定义，命名见 cache-redis）+ `CommonCacheOperator` |
| 重复实现"对象转 JSON 字符串" | `JSONUtil.toJsonStr` |

> 出厂代码 CommonAvatarUtil、GlobalExceptionUtil（以及 auth 插件 SAML 移植代码）里的 lang3 `StringUtils` 是历史遗留（传递依赖），新代码不要模仿。

## 检查清单

- [ ] 用工具前扫过本表（Common* 优先 → hutool → 官方 → 自写）
- [ ] 没有引入新工具类依赖（guava/commons-lang3 等）
- [ ] 自写工具放对了位置（插件 core/util/，通用的才进 snowy-common）

## 参考实现

| 文件 | 说明 |
|---|---|
| `snowy-common/src/main/java/vip/xiaonuo/common/util/` | 16 个工具类源码 |
| `snowy-common/src/main/java/vip/xiaonuo/common/consts/CacheConstant.java` | 缓存 key 常量 |
| `snowy-plugin/snowy-plugin-auth/src/main/java/vip/xiaonuo/auth/modular/login/service/impl/AuthServiceImpl.java` | 工具类组合使用现场（加密/IP/UA/缓存） |
