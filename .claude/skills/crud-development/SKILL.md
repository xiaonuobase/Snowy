---
name: crud-development
description: Snowy CRUD 全套开发规范（六件套 + 可选两件 + 前端三件）。触发场景：1) 开发新的业务模块/增删改查功能 2) 编写 Entity/Mapper/Service/Controller/Param/Result 任何一件 3) 仿照现有模块新建业务域。触发词：CRUD、增删改查、业务模块、新建模块、Entity、Service、Controller、Param、分页、六件套。注意：只讲"怎么写代码"；建表/菜单 SQL 见 database-ops；接口设计细节见 api-development；用自带生成器见 code-generator。
---

# Snowy CRUD 开发规范

## 架构特征表（先背下来再写代码）

| 特征 | Snowy 约定 | 与 RuoYi 系相反点 |
|---|---|---|
| 包名 | `vip.xiaonuo.biz.modular.{域名}.{层}` | 不是 org.dromara / com.ruoyi |
| 依赖注入 | `@Resource`（jakarta.annotation）字段注入 | ❌@Autowired ❌构造器注入 |
| ServiceImpl | `extends ServiceImpl<XxxMapper, Xxx> implements XxxService` | ❌只 implements 不继承 |
| 对象转换 | Hutool `BeanUtil.toBean / copyProperties` | ❌MapstructUtils |
| Lombok | 只用 `@Getter @Setter` | ❌@Data |
| 主键 | `@TableId private String id`（字符串雪花） | ❌Long |
| URL | 动词式写方法上 `/biz/xxx/page` | ❌RESTful 路径 ❌类级 @RequestMapping |
| 分页 | `CommonPageRequest.defaultPage()` + MP `Page<T>` | — |
| 版权头 | 每个 .java 头部 12 行 Apache 2.0 声明 | AI 生成最容易漏 |

## 版权头模板（每个新 .java 必须有）

```java
/*
 * Copyright [2022] [https://www.xiaonuo.vip]
 *
 * Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *
 * 1.请不要删除和修改根目录下的LICENSE文件。
 * 2.请不要删除和修改Snowy源码头部的版权声明。
 * 3.本项目代码可免费商业使用，商业使用请保留源码和相关描述文件的项目出处，作者声明等。
 * 4.分发源码时候，请注明软件出处 https://www.xiaonuo.vip
 * 5.不可二次分发开源参与同类竞品，如有想法可联系团队xiaonuobase@qq.com商议合作。
 * 6.若您的项目无法满足以上几点，需要更多功能代码，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
```

## 目录结构（一个业务域 = modular 下一个目录）

```
snowy-plugin/snowy-plugin-biz/src/main/java/vip/xiaonuo/biz/modular/{域名}/
├── controller/XxxController.java
├── entity/Xxx.java
├── enums/XxxStatusEnum.java          （可选，有状态字段时）
├── mapper/XxxMapper.java
│   └── mapping/XxxMapper.xml         （可选，仅自定义 SQL）
├── param/XxxPageParam.java
│       XxxAddParam.java
│       XxxEditParam.java
│       XxxIdParam.java
├── result/XxxResult.java             （可选，仅投影返回）
├── service/XxxService.java
│   └── impl/XxxServiceImpl.java
└── provider/XxxApiProvider.java      （可选，被跨插件调用时）
```

类名前缀 = 插件缩写（业务插件是 **Biz**）：`BizNotice`、`BizNoticeService`。

## 第 1 件：Entity

```java
@Getter
@Setter
@TableName("BIZ_XXX")                      // 表名全大写；有 SM4 加密字段时 @TableName(value = "BIZ_XXX", autoResultMap = true)
public class BizXxx extends CommonEntity {

    /** 主键 */
    @TableId
    @Schema(description = "主键")
    private String id;

    /** 名称 */
    @Schema(description = "名称")
    private String name;

    /** 排序 */
    @Schema(description = "排序")
    private Integer sortCode;

    /** 扩展信息 */
    @Schema(description = "扩展信息")
    private String extJson;
}
```

要点：
- 继承 `CommonEntity`（自动获得 deleteFlag/createUser/createTime/updateUser/updateTime 审计字段，逻辑删除自动过滤）
- 敏感字段（手机号等）加 `@TableField(typeHandler = CommonSm4CbcTypeHandler.class)`，见 crypto-sm 技能
- 字典翻译字段加 `@Trans(type = TransType.DICTIONARY, key = "XXX")`，关联表翻译加 `@Trans(type = TransType.SIMPLE, target = Yyy.class, fields = "name", alias = "yyy", ref = "yyyName")`
- 每个字段有 `/** 中文说明 */` + `@Schema(description = "中文")`

## 第 2 件：Mapper（通常空接口）

```java
public interface BizXxxMapper extends BaseMapper<BizXxx> {
}
```

自定义 SQL 才建 `mapper/mapping/BizXxxMapper.xml`（namespace = Mapper 全限定名，与 Java 同包）。

## 第 3 件：Service 接口

```java
public interface BizXxxService extends IService<BizXxx> {

    /**
     * 获取XXX分页
     *
     * @author 你的名字
     * @date  2026/08/18 10:00
     */
    Page<BizXxx> page(BizXxxPageParam bizXxxPageParam);

    void add(BizXxxAddParam bizXxxAddParam);          // 每个方法都要中文 Javadoc + @author + @date
    void edit(BizXxxEditParam bizXxxEditParam);
    void delete(List<BizXxxIdParam> bizXxxIdParamList);
    BizXxx detail(BizXxxIdParam bizXxxIdParam);
    BizXxx queryEntity(String id);                    // 内部帮助方法：查不到抛异常
}
```

## 第 4 件：ServiceImpl（核心模板）

```java
@Service
public class BizXxxServiceImpl extends ServiceImpl<BizXxxMapper, BizXxx> implements BizXxxService {

    @Override
    public Page<BizXxx> page(BizXxxPageParam bizXxxPageParam) {
        QueryWrapper<BizXxx> queryWrapper = new QueryWrapper<BizXxx>().checkSqlInjection();
        // 每个查询条件都要判空
        if(ObjectUtil.isNotEmpty(bizXxxPageParam.getName())) {
            queryWrapper.lambda().like(BizXxx::getName, bizXxxPageParam.getName());   // 模糊
        }
        if(ObjectUtil.isNotEmpty(bizXxxPageParam.getType())) {
            queryWrapper.lambda().eq(BizXxx::getType, bizXxxPageParam.getType());     // 精确
        }
        if(ObjectUtil.isAllNotEmpty(bizXxxPageParam.getStartCreateTime(), bizXxxPageParam.getEndCreateTime())) {
            queryWrapper.lambda().between(BizXxx::getCreateTime,
                    bizXxxPageParam.getStartCreateTime(), bizXxxPageParam.getEndCreateTime());
        }
        // 排序：前端传了 sortField/sortOrder 用之，否则默认按 sortCode 升序
        if(ObjectUtil.isAllNotEmpty(bizXxxPageParam.getSortField(), bizXxxPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(bizXxxPageParam.getSortOrder());
            queryWrapper.orderBy(true, bizXxxPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(bizXxxPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(BizXxx::getSortCode);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)      // 写操作必须带
    @Override
    public void add(BizXxxAddParam bizXxxAddParam) {
        BizXxx bizXxx = BeanUtil.toBean(bizXxxAddParam, BizXxx.class);   // 新增：Param → Entity
        this.save(bizXxx);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(BizXxxEditParam bizXxxEditParam) {
        BizXxx bizXxx = this.queryEntity(bizXxxEditParam.getId());       // 编辑：先查再拷贝
        BeanUtil.copyProperties(bizXxxEditParam, bizXxx);
        this.updateById(bizXxx);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<BizXxxIdParam> bizXxxIdParamList) {
        this.removeByIds(CollStreamUtil.toList(bizXxxIdParamList, BizXxxIdParam::getId));
    }

    @Override
    public BizXxx detail(BizXxxIdParam bizXxxIdParam) {
        return this.queryEntity(bizXxxIdParam.getId());
    }

    @Override
    public BizXxx queryEntity(String id) {
        BizXxx bizXxx = this.getById(id);
        if(ObjectUtil.isEmpty(bizXxx)) {
            throw new CommonException("XXX不存在，id值为：{}", id);        // 中文消息 + {} 占位
        }
        return bizXxx;
    }
}
```

## 第 5 件：Controller（五接口模板）

```java
@Tag(name = "XXX控制器")
@RestController
@Validated
public class BizXxxController {

    @Resource
    private BizXxxService bizXxxService;

    @Operation(summary = "获取XXX分页")
    @SaCheckPermission("/biz/xxx/page")                      // 权限码 = 接口 URL
    @GetMapping("/biz/xxx/page")
    public CommonResult<Page<BizXxx>> page(BizXxxPageParam bizXxxPageParam) {
        return CommonResult.data(bizXxxService.page(bizXxxPageParam));
    }

    @Operation(summary = "添加XXX")
    @CommonLog("添加XXX")                                    // 写操作必须加操作日志
    @SaCheckPermission("/biz/xxx/add")
    @PostMapping("/biz/xxx/add")
    public CommonResult<String> add(@RequestBody @Valid BizXxxAddParam bizXxxAddParam) {
        bizXxxService.add(bizXxxAddParam);
        return CommonResult.ok();
    }

    @Operation(summary = "编辑XXX")
    @CommonLog("编辑XXX")
    @SaCheckPermission("/biz/xxx/edit")
    @PostMapping("/biz/xxx/edit")
    public CommonResult<String> edit(@RequestBody @Valid BizXxxEditParam bizXxxEditParam) {
        bizXxxService.edit(bizXxxEditParam);
        return CommonResult.ok();
    }

    @Operation(summary = "删除XXX")
    @CommonLog("删除XXX")
    @SaCheckPermission("/biz/xxx/delete")
    @PostMapping("/biz/xxx/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<BizXxxIdParam> bizXxxIdParamList) {
        bizXxxService.delete(bizXxxIdParamList);
        return CommonResult.ok();
    }

    @Operation(summary = "获取XXX详情")
    @SaCheckPermission("/biz/xxx/detail")
    @GetMapping("/biz/xxx/detail")
    public CommonResult<BizXxx> detail(@Valid BizXxxIdParam bizXxxIdParam) {
        return CommonResult.data(bizXxxService.detail(bizXxxIdParam));
    }
}
```

## 第 6 件：Param 组（每操作一个类）

- **XxxPageParam**：current/size/sortField/sortOrder/searchKey 五个固定字段 + 业务查询字段（无校验注解）
- **XxxAddParam**：业务字段 + `@NotBlank(message = "xxx不能为空")` 等校验（jakarta.validation）
- **XxxEditParam**：同 AddParam + `@NotBlank private String id`
- **XxxIdParam**：只有 id

```java
@Getter
@Setter
public class BizXxxIdParam {

    /** 主键 */
    @Schema(description = "主键")
    @NotBlank(message = "id不能为空")
    private String id;
}
```

## 可选两件

- **Result**：仅需要投影/跨表组装返回时建 `result/XxxResult.java`（@Getter @Setter + @Schema）；直接返回 Entity 是允许的
- **Mapper XML**：仅自定义 SQL；空 XML 不要建

## 前端三件（见 frontend-pc 技能详解）

1. `snowy-admin-web/src/api/biz/bizXxxApi.js` —— API 封装
2. `snowy-admin-web/src/views/biz/xxx/index.vue` —— 列表页（s-table）
3. `snowy-admin-web/src/views/biz/xxx/form.vue` —— 弹窗表单

## 常见错误正误对照

| ❌ 错误（RuoYi 惯性） | ✅ 正确（Snowy） |
|---|---|
| `@Autowired private XxxService x;` | `@Resource private XxxService x;` |
| `public class XxxServiceImpl implements IXxxService` | `extends ServiceImpl<XxxMapper, Xxx> implements XxxService` |
| `MapstructUtils.convert(bo, Xxx.class)` | `BeanUtil.toBean(param, Xxx.class)` |
| `@Data public class Xxx` | `@Getter @Setter public class Xxx` |
| `@RequestMapping("/biz/xxx")` 类级 + `@PostMapping("/list")` | 无类级注解，方法上直接 `@PostMapping("/biz/xxx/add")` |
| `@SaCheckPermission("biz:xxx:list")` | `@SaCheckPermission("/biz/xxx/page")` |
| `private Long id` / `@TableId(type = IdType.ASSIGN_ID)` Long | `@TableId private String id` |
| `throw new ServiceException("xxx")` | `throw new CommonException("xxx不存在，id值为：{}", id)` |
| 单个 XxxBo + AddGroup/EditGroup | XxxPageParam / XxxAddParam / XxxEditParam / XxxIdParam 四类 |
| `R.ok(data)` / `AjaxResult` | `CommonResult.data(data)` / `CommonResult.ok()` |
| `queryWrapper.eq("name", ...)` 字符串列名 | `queryWrapper.lambda().eq(Xxx::getName, ...)` |

## 检查清单（写完自查）

- [ ] 每个 .java 有 12 行版权头
- [ ] 包名 `vip.xiaonuo.biz.modular.xxx.*`，类名 Biz 前缀
- [ ] Entity 继承 CommonEntity，@TableName 大写，@TableId String
- [ ] ServiceImpl extends ServiceImpl 且写方法有 @Transactional(rollbackFor = Exception.class)
- [ ] 查询 QueryWrapper 带 checkSqlInjection()，条件全部判空
- [ ] Controller：@Tag/@RestController/@Validated，URL 动词式，@SaCheckPermission 值 = URL，写操作 @CommonLog
- [ ] Param 四类齐全，校验消息中文
- [ ] 所有注释/Javadoc/异常消息中文，Javadoc 有 @author @date
- [ ] 前端三件已同步生成

## 参考实现（真实代码，照这个写）

| 文件 | 说明 |
|---|---|
| `snowy-plugin/snowy-plugin-biz/src/main/java/vip/xiaonuo/biz/modular/notice/` | 最干净完整的六件套范本（entity/mapper/service/impl/controller/param 全有） |
| `snowy-plugin/snowy-plugin-sys/src/main/java/vip/xiaonuo/sys/modular/user/SysUserController.java` | 复杂版 Controller（含导出/禁用启用） |
| `snowy-plugin/snowy-plugin-sys/src/main/java/vip/xiaonuo/sys/modular/user/service/impl/SysUserServiceImpl.java` | 复杂版 ServiceImpl（含 SM4 字段/事务/重复校验） |
| `snowy-common/src/main/java/vip/xiaonuo/common/page/CommonPageRequest.java` | 分页请求构造 |
| `snowy-common/src/main/java/vip/xiaonuo/common/enums/CommonSortOrderEnum.java` | 排序枚举 |
| `snowy-common/src/main/java/vip/xiaonuo/common/pojo/CommonEntity.java` | 实体基类（审计字段） |
