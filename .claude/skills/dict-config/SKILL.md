---
name: dict-config
description: Snowy 字典/配置/枚举三件套规范：DEV_DICT 系统字典与 BIZ 业务字典双轨、前端 tool.dictList / DictSelect 字典下拉、@Trans 字典翻译、业务枚举类规范、DEV_CONFIG 系统配置。触发场景：1) 字段需要下拉选项（状态/类型）2) 需要系统参数配置 3) 定义业务枚举。触发词：字典、dict、DEV_DICT、业务字典、DictSelect、dictList、下拉、枚举、enum、系统配置、DEV_CONFIG、参数配置。
---

# Snowy 字典 / 配置 / 枚举规范

## 三种"可选项"怎么选

| 场景 | 用什么 | 存哪 |
|---|---|---|
| 选项需要最终用户在界面维护 | **字典**（DEV_DICT / BIZ 字典） | 数据库 |
| 选项与代码逻辑强绑定（不同值走不同分支） | **枚举类**（modular/{域}/enums/） | Java 代码 |
| 运行时可调的参数（开关/阈值） | **系统配置** DEV_CONFIG | 数据库（界面改） |

## 字典双轨

两轨共用 DEV_DICT 一张表，靠 CATEGORY 列区分（FRM=系统，BIZ=业务），**没有 BIZ_DICT 表**。

| 轨道 | 模块 | 用途 | 管理界面 |
|---|---|---|---|
| 系统字典 | `dev/modular/dict/`（DEV_DICT 表，category=FRM） | 平台级通用字典（性别 GENDER、通知类型等）；`/dev/dict/tree` 免登录（前端登录页也要用） | 系统 → 系统运维 → 数据字典（「系统字典」分类） |
| 业务字典 | `biz/modular/dict/`（和系统字典同表 DEV_DICT，category=BIZ） | 业务自定义字典 | 新建和删除在 系统 → 系统运维 → 数据字典（切到「业务字典」）；最终用户在 业务 → 业务字典 只能改标签文字、颜色和排序，不能增删项，也不能改值 |

**新业务字典优先建在 BIZ 分类**（在数据字典页切到「业务字典」再新建，不污染 FRM 平台字典）；性别这类平台级通用字典才建在 FRM。

## 字典使用全链路

```
1. 系统 → 系统运维 → 数据字典 → 切到「业务字典」新建字典（编码如 SUPPLIER_TYPE，子项填 值+标签+排序）
2. Entity 字段存字典"值"（String）：
     @Schema(description = "供应商类型")
     private String type;
3. 列表返回需要显示文本时，Entity 加 @Trans 字典翻译：
     @Trans(type = TransType.DICTIONARY, key = "SUPPLIER_TYPE")
     private String type;            // 不用声明 typeName，easy-trans 平铺模式（easy-trans.is-enable-tile=true）会自动输出 typeName
   只有 SIMPLE 关联翻译才需要 @TableField(exist = false) 的承接字段（如 BizUser.orgId 上 ref = "orgName"）
4. 前端下拉从字典缓存取 options（平台和生成器的主流写法）：
     const typeOptions = tool.dictList('SUPPLIER_TYPE')
     <a-select v-model:value="formData.type" :options="typeOptions" />
   也可以用等价封装 DictSelect（挂载时从登录时加载好的统一字典缓存读一次选项；组件本身不调接口，也不刷新缓存）：
     <dict-select v-model="formData.type" dict-type="SUPPLIER_TYPE" />
5. 查询条件：PageParam 的 type 字段 eq 精确匹配
```

## 业务枚举规范（modular/{域}/enums/）

```java
package vip.xiaonuo.biz.modular.supplier.enums;

import lombok.Getter;
import vip.xiaonuo.common.exception.CommonException;

/**
 * 供应商状态枚举
 *
 * @author 你的名字
 * @date  2026/08/18 10:00
 */
@Getter
public enum BizSupplierStatusEnum {

    /** 启用 */
    ENABLE("ENABLE"),

    /** 禁用 */
    DISABLE("DISABLE");

    private final String value;

    BizSupplierStatusEnum(String value) {
        this.value = value;
    }

    /** 校验值合法性（入参来自前端时校验用） */
    public static void validate(String value) {
        boolean flag = ENABLE.getValue().equals(value) || DISABLE.getValue().equals(value);
        if(!flag) {
            throw new CommonException("不支持的状态：{}", value);
        }
    }
}
```

- 纯枚举 + `@Getter` + `private final String value` + 构造器（**无公共基类**）；需要校验时加静态 validate
- 命名 `Xxx{含义}Enum`；业务状态、类型类枚举的值用 String 大写（与字典值风格一致）。异常提示枚举、内置编码枚举、前端协议值（如排序 ascend/descend）不受此限
- 代码里取值 `BizSupplierStatusEnum.ENABLE.getValue()`，**禁止魔法字符串**散落（指 Java 代码。平台里少量遗留字面量，如 SysUserServiceImpl 的 `"FALSE"`/`"NO"`、GenBasicServiceImpl 的 `"SUB"`/`"MAIN"`，不要模仿；前端直接比较字典值是正常写法）
- 参考现有：`BizNoticeStatusEnum`（`biz/modular/notice/enums/`）

## 系统配置 DEV_CONFIG

- 界面：系统 → 系统运维 → 系统配置（键值对，分组）
- 读取：

```java
@Resource
private DevConfigApi devConfigApi;

String value = devConfigApi.getValueByKey("BIZ_XXX_SWITCH");
```

- 不要把可调参数写死在代码或 application.properties（改起来要重启）

## 常见错误正误对照

| 错误 | 正确 |
|---|---|
| 状态字段用 Integer 0/1 魔法值 | String + 枚举/字典（与平台风格一致） |
| 前端手写 options 数组存字典项 | `tool.dictList('编码')` 生成 options（或用 DictSelect 封装），两者都读统一字典缓存 |
| 可调开关写死代码 | DEV_CONFIG + DevConfigApi |
| 平台字典里建业务选项 | 业务字典（biz） |
| 字典改了页面不刷新 | 前端字典有缓存，刷新页面/重新拉取；后端 @Trans 翻译缓存由字典服务在增删改后直接调 `refreshTransCache()` 刷新，业务不用处理 |

## 检查清单

- [ ] 可维护选项进字典（业务字典优先），逻辑绑定选项进枚举
- [ ] 字典翻译用 @Trans(DICTIONARY)，不加冗余字段；SIMPLE 翻译的承接字段加 @TableField(exist = false)
- [ ] 前端 tool.dictList / DictSelect 用的字典编码与后端一致，没有硬编码的选项数组
- [ ] 枚举是普通 enum + @Getter + private final String value（不继承基类、不实现接口），Java 代码里没有魔法字符串
- [ ] 运行时参数走 DEV_CONFIG

## 参考实现

| 文件 | 说明 |
|---|---|
| `snowy-plugin/snowy-plugin-dev/src/main/java/vip/xiaonuo/dev/modular/dict/` | 系统字典模块 |
| `snowy-plugin/snowy-plugin-biz/src/main/java/vip/xiaonuo/biz/modular/dict/` | 业务字典模块 |
| `snowy-plugin/snowy-plugin-biz/src/main/java/vip/xiaonuo/biz/modular/notice/enums/BizNoticeStatusEnum.java` | 枚举范本 |
| `snowy-plugin-api/snowy-plugin-dev-api/src/main/java/vip/xiaonuo/dev/api/DevConfigApi.java` | 配置读取接口 |
| `snowy-admin-web/src/utils/tool.js` | `tool.dictList('编码')` 从登录时加载的字典缓存取下拉选项 |
| `snowy-admin-web/src/components/DictSelect/` | 字典下拉组件（含 README） |
| `snowy-plugin/snowy-plugin-biz/src/main/java/vip/xiaonuo/biz/modular/user/entity/BizUser.java` | @Trans DICTIONARY 实战 |
