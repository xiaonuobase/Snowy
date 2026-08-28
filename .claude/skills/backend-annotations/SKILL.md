---
name: backend-annotations
description: Snowy 常用注解速查与正误用法：@CommonLog 操作日志、@CommonNoRepeat 防重、@CommonWrapper 返回包装、@SaCheckPermission、@Trans 字段翻译、@Validated/@Valid、MyBatis-Plus 注解。触发场景：1) 给接口加日志/防重/权限注解 2) 字段需要字典或关联表翻译 3) 不确定该用哪个注解或注解参数。触发词：注解、@CommonLog、@CommonNoRepeat、@SaCheckPermission、@Trans、Easy-Trans、翻译、字典翻译、@Validated、@TableName、@TableId、@TableLogic。注意：注解底层机制见对应技能（日志见 message-push 相关、权限见 security-auth、字典见 dict-config）。
---

# Snowy 常用注解速查

## 注解总览

| 注解 | 来源 | 用在哪 | 作用 |
|---|---|---|---|
| `@CommonLog("中文标题")` | snowy-common | Controller 写方法 | 操作日志落 DEV_LOG 表（AOP：DevLogAop） |
| `@CommonNoRepeat(interval=5000)` | snowy-common | Controller 写方法 | 防重复提交（默认 5 秒内相同请求拦截） |
| `@SaCheckPermission("/url")` | Sa-Token | Controller 方法 | 接口权限校验，**值 = 接口 URL** |
| `@SaCheckRole("xxx")` | Sa-Token | Controller 方法 | 角色校验（少用） |
| `@Trans(type=...)` | Easy-Trans | Entity 字段 | 字典/关联表字段自动翻译 |
| `@Validated` | Spring | Controller 类 | 开启方法级校验 |
| `@Valid` | jakarta | 参数前 | 触发 Param 校验注解 |
| `@TableName("BIZ_XXX")` | MyBatis-Plus | Entity 类 | 表映射（大写） |
| `@TableId` | MyBatis-Plus | Entity id 字段 | 字符串雪花主键 |
| `@TableLogic` | MyBatis-Plus | CommonEntity 的 deleteFlag | 逻辑删除（继承即有，别重复加） |
| `@TableField(exist = false)` | MyBatis-Plus | Entity 字段 | 非表字段（翻译冗余名等） |
| `@TableField(typeHandler = CommonSm4CbcTypeHandler.class)` | MyBatis-Plus | 敏感字段 | SM4 加密落库（@TableName 需 autoResultMap = true） |
| `@Tag / @Operation / @Schema` | OpenAPI3 | 类/方法/字段 | 接口文档（中文） |
| `@Transactional(rollbackFor = Exception.class)` | Spring | Service 写方法 | 事务 |

## @CommonLog —— 操作日志

```java
@Operation(summary = "添加供应商")
@CommonLog("添加供应商")                // value = 中文动作名，默认"未命名"（别用默认）
@SaCheckPermission("/biz/supplier/add")
@PostMapping("/biz/supplier/add")
public CommonResult<String> add(...) {...}
```

- **所有写操作接口必须加**（add/edit/delete/业务动作）；查询接口不加
- 日志由 `snowy-plugin-dev` 的 DevLogAop 切面落 DEV_LOG 表，可在 开发工具→日志 查看
- ❌ `@Log(title=..., businessType=...)` 是同类快速开发框架的写法，本项目没有

## @CommonNoRepeat —— 防重复提交

```java
@CommonNoRepeat                        // 默认 5000ms 内视为重复
@PostMapping("/biz/order/create")
public CommonResult<String> create(...) {...}

@CommonNoRepeat(interval = 10000)      // 自定义间隔
```

- 用于不可重复的关键写操作（下单、支付、审批提交）
- 实现：snowy-web-app 的 GlobalConfigure 内嵌 CommonNoRepeatAop（IP+URL+参数 指纹）

## @SaCheckPermission —— 接口权限

```java
@SaCheckPermission("/biz/supplier/page")    // ✅ 值 = 本接口 URL
@SaCheckPermission("biz:supplier:page")     // ❌ 冒号式是同类框架的，校验必失败
```

- 权限来源：角色-资源授权生成的数据范围 apiUrl 列表（详见 security-auth 技能）
- C 端接口用 `@SaClientCheckLogin` / `@SaClientCheckPermission`（auth-api 提供）

## @Trans —— 字段翻译（Easy-Trans）

```java
// 字典翻译：GENDER 字典码 → 字典值文本
@Trans(type = TransType.DICTIONARY, key = "GENDER")
private String gender;

// 关联表翻译：主管 id → 主管姓名，翻译结果放进 ref 指定的冗余字段
@Trans(type = TransType.SIMPLE, target = BizUser.class, fields = "name", alias = "director", ref = "directorName")
private String directorId;

@TableField(exist = false)
private String directorName;      // 翻译结果落这里（非表字段）
```

- Entity 继承 CommonEntity（implements TransPojo）即支持
- 字典翻译要求字典编码存在（DEV_DICT / BIZ 字典，见 dict-config 技能）
- SIMPLE 翻译要求 target 表数据量可控（内部有缓存）；大量关联时考虑手写批量查询

## 校验注解（jakarta.validation）

```java
// Controller 类上 @Validated，方法参数：
public CommonResult<String> add(@RequestBody @Valid BizXxxAddParam param) {...}          // POST body
public CommonResult<BizXxx> detail(@Valid BizXxxIdParam param) {...}                      // GET query
public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                   List<BizXxxIdParam> paramList) {...}                   // 批量删除

// Param 字段上：
@NotBlank(message = "name不能为空")      // 字符串非空
@NotNull(message = "count不能为空")       // 对象/null
@NotEmpty(message = "ids不能为空")        // 集合
```

校验失败由全局异常处理器统一返回中文提示，不需要手写返回。消息格式惯例：`字段名不能为空`（字段用英文驼峰名）。

## MyBatis-Plus 注解

```java
@TableName("BIZ_SUPPLIER")                                   // 大写；有 TypeHandler 字段时 value=... , autoResultMap = true
public class BizSupplier extends CommonEntity {              // 继承即有 DELETE_FLAG 逻辑删除 + 审计字段
    @TableId
    private String id;                                       // 字符串雪花
}
```

❌ 常见误用：`@TableId(type = IdType.AUTO)`（本项目 ASSIGN_ID 全局配置，不写 type）、自己再声明 deleteFlag/createTime 字段（与基类冲突）。

## 检查清单

- [ ] 写接口有 @CommonLog（中文标题）+ 写 Service 方法有 @Transactional(rollbackFor = Exception.class)
- [ ] @SaCheckPermission 值 = URL 全路径
- [ ] 翻译字段 @Trans 的 ref 冗余字段有 @TableField(exist = false)
- [ ] 校验消息中文、格式统一
- [ ] 没有使用同类框架特有注解（@Log、@RateLimiter、@DataPermission、@ExcelProperty 换 EasyExcel 注解）

## 参考实现

| 文件 | 说明 |
|---|---|
| `snowy-common/src/main/java/vip/xiaonuo/common/annotation/CommonLog.java` | 日志注解定义 |
| `snowy-common/src/main/java/vip/xiaonuo/common/annotation/CommonNoRepeat.java` | 防重注解定义 |
| `snowy-plugin/snowy-plugin-biz/src/main/java/vip/xiaonuo/biz/modular/notice/controller/BizNoticeController.java` | 注解组合使用范本 |
| `snowy-plugin/snowy-plugin-biz/src/main/java/vip/xiaonuo/biz/modular/user/entity/BizUser.java` | @Trans 双类型 + SM4 TypeHandler 范本 |
| `snowy-plugin/snowy-plugin-dev/src/main/java/vip/xiaonuo/dev/core/aop/DevLogAop.java` | 日志切面实现 |
| `snowy-web-app/src/main/java/vip/xiaonuo/core/config/GlobalConfigure.java` | 防重/包装 AOP 注册 |
