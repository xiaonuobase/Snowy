---
name: dict-config
description: Snowy 字典/配置/枚举三件套规范：DEV_DICT 系统字典与 BIZ 业务字典双轨、前端 DictSelect 组件、@Trans 字典翻译、业务枚举类规范、DEV_CONFIG 系统配置。触发场景：1) 字段需要下拉选项（状态/类型）2) 需要系统参数配置 3) 定义业务枚举。触发词：字典、dict、DEV_DICT、BIZ_DICT、DictSelect、下拉、枚举、enum、系统配置、DEV_CONFIG、参数配置。
---

# Snowy 字典 / 配置 / 枚举规范

## 三种"可选项"怎么选

| 场景 | 用什么 | 存哪 |
|---|---|---|
| 选项需要最终用户在界面维护 | **字典**（DEV_DICT / BIZ 字典） | 数据库 |
| 选项与代码逻辑强绑定（不同值走不同分支） | **枚举类**（modular/{域}/enums/） | Java 代码 |
| 运行时可调的参数（开关/阈值） | **系统配置** DEV_CONFIG | 数据库（界面改） |

## 字典双轨

| 轨道 | 模块 | 用途 | 管理界面 |
|---|---|---|---|
| 系统字典 | `dev/modular/dict/`（DEV_DICT 表） | 平台级通用字典（性别 GENDER、通知类型等）；`/dev/dict/tree` 免登录（前端登录页也要用） | 开发工具 → 字典管理 |
| 业务字典 | `biz/modular/dict/`（BIZ 字典服务） | 业务自定义字典（面向最终用户可维护） | 业务功能 → 业务字典 |

**新业务字典优先建业务字典**（不污染平台字典）；平台级通用（性别这类）才进 DEV_DICT。

## 字典使用全链路

```
1. 界面建字典（编码如 SUPPLIER_TYPE，子项 值+标签+排序）
2. Entity 字段存字典"值"（String）：
     @Schema(description = "供应商类型")
     private String type;
3. 列表返回需要显示文本时，Entity 加 @Trans 翻译（向后兼容多一个冗名字段）：
     @Trans(type = TransType.DICTIONARY, key = "SUPPLIER_TYPE")
     private String type;
     @TableField(exist = false)
     private String typeName;        // 注意：@Trans DICTIONARY 自动生成翻译；SIMPLE 才用 ref
4. 前端下拉用 DictSelect 组件（自动拉字典并缓存）：
     <dict-select v-model:value="formData.type" dict-type-code="SUPPLIER_TYPE" />
5. 查询条件：PageParam 的 type 字段 eq 精确匹配
```

## 业务枚举规范（modular/{域}/enums/）

```java
/*
 * ... 版权头
 */
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
- 命名 `Xxx{含义}Enum`，值用 String 大写（与字典值风格一致）
- 代码里取值 `BizSupplierStatusEnum.ENABLE.getValue()`，**禁止魔法字符串**散落
- 参考现有：`BizNoticeStatusEnum`（`biz/modular/notice/enums/`）

## 系统配置 DEV_CONFIG

- 界面：开发工具 → 系统配置（键值对，分组）
- 读取：

```java
@Resource
private DevConfigApi devConfigApi;

String value = devConfigApi.getConfigValueByKey("BIZ_XXX_SWITCH");
```

- ❌ 不要把可调参数写死在代码或 application.properties（改起来要重启）

## 常见错误正误对照

| ❌ | ✅ |
|---|---|
| 状态字段用 Integer 0/1 魔法值 | String + 枚举/字典（与平台风格一致） |
| 前端手写 options 数组存字典项 | DictSelect 组件（统一缓存） |
| 可调开关写死代码 | DEV_CONFIG + DevConfigApi |
| 平台字典里建业务选项 | 业务字典（biz） |
| 字典改了页面不刷新 | 前端字典有缓存，刷新页面/重新拉取；后端翻译缓存走数据变更事件 |

## 检查清单

- [ ] 可维护选项进字典（业务字典优先），逻辑绑定选项进枚举
- [ ] Entity 翻译字段用 @Trans，冗余字段 @TableField(exist = false)
- [ ] 前端 DictSelect 的 dict-type-code 与字典编码一致
- [ ] 枚举实现 CommonEnum，无魔法字符串
- [ ] 运行时参数走 DEV_CONFIG

## 参考实现

| 文件 | 说明 |
|---|---|
| `snowy-plugin/snowy-plugin-dev/src/main/java/vip/xiaonuo/dev/modular/dict/` | 系统字典模块 |
| `snowy-plugin/snowy-plugin-biz/src/main/java/vip/xiaonuo/biz/modular/dict/` | 业务字典模块 |
| `snowy-plugin/snowy-plugin-biz/src/main/java/vip/xiaonuo/biz/modular/notice/enums/BizNoticeStatusEnum.java` | 枚举范本 |
| `snowy-plugin-api/snowy-plugin-dev-api/src/main/java/vip/xiaonuo/dev/api/DevConfigApi.java` | 配置读取接口 |
| `snowy-admin-web/src/components/DictSelect/` | 字典下拉组件（含 README） |
| `snowy-plugin/snowy-plugin-biz/src/main/java/vip/xiaonuo/biz/modular/user/entity/BizUser.java` | @Trans DICTIONARY 实战 |
