---
name: platform-extension
description: Snowy 平台扩展点地图：不改框架代码实现定制的所有正规途径——数据变更监听、跨插件 api 复用、provider 暴露、定时任务、系统配置、CommonWrapper、前端复用。触发场景：1) 用户说"我想改/增强平台某行为"（如用户变更后同步业务缓存、给某接口加默认逻辑）2) 判断一个定制需求该新建业务域还是扩展平台 3) 想复用平台能力（用户/字典/文件/消息）不知道从哪接。触发词：扩展、增强、定制、改平台、改框架、钩子、监听、联动、同步缓存、复用平台、不动源码、覆盖、事件。注意：新建业务功能走 crud-development/dev；本技能讲"平台行为定制"的正规入口。
---

# Snowy 平台扩展点地图

## 核心原则

> **平台插件（sys/auth/dev/gen/client/mobile）原则上不改**——改了升级即冲突。几乎所有"改平台行为"的需求都有正规扩展点。先查本表，再决定动不动框架。

## "我想做 X" → 扩展点速查表

| 需求 | 正确姿势 | 位置 |
|---|---|---|
| 用户/组织/角色变更后刷新**我方缓存或联动业务** | 实现 `CommonDataChangeListener` + @Component，按 dataType 分发 | 我的插件 `core/listener/` |
| 在业务代码里用平台能力（查用户/发消息/传文件/读配置） | 注入现成 `*-api` 接口（SysUserApi/DevMessageApi/DevFileApi/DevConfigApi...） | 见 plugin-architecture 的现成 API 速查表 |
| 把我方能力暴露给其他插件 | 定义接口进 `snowy-plugin-api/{我方}-api` + 我方 `provider/` 实现 | provider/ |
| 周期性任务（对账/清理/汇总） | 实现 `CommonTimerTaskRunner` + @Component + 界面配置 | 我的插件 `core/timer/` |
| 可调的业务参数（开关/阈值/默认密码） | DEV_CONFIG（系统配置界面维护），代码里 DevConfigApi 读取 | 界面：开发工具→系统配置 |
| 下拉选项数据 | 业务字典（BIZ）/枚举（见 dict-config） | biz/modular/dict 或 enums/ |
| 返回给前端的对象统一加工/包装 | `@CommonWrapper(包装类.class)`（返回值包装 AOP） | snowy-common/annotation |
| 接口防重复提交 | `@CommonNoRepeat` | Controller 方法上 |
| 操作留痕 | `@CommonLog("中文")` | Controller 写方法上 |
| 字段展示为字典文本/关联名 | Entity 加 `@Trans`（DICTIONARY/SIMPLE） | entity/ |
| 新页面/新菜单 | views/biz/{域} + SYS_RESOURCE 资源 SQL | 前端 + database-ops 挂载点速查 |
| 记录业务操作日志/异常排查 | @CommonLog 已落 DEV_LOG，直接查 | 开发工具→日志 |

## 扩展点 1：数据变更监听（最高频）

平台（sys 等）改了共享数据（用户/组织/角色）会广播事件；我的插件想要联动（清缓存/同步数据），实现监听器即可，**零侵入**：

```java
/*
 * ... 版权头
 */
package vip.xiaonuo.biz.core.listener;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import org.springframework.stereotype.Component;
import vip.xiaonuo.common.listener.CommonDataChangeListener;

import java.util.List;

/**
 * XXX数据变化侦听器：监听平台对共享数据的变更，联动本插件逻辑
 *
 * @author 你的名字
 * @date  2026/08/18
 **/
@Component
public class BizXxxDataChangeListener implements CommonDataChangeListener {

    @Override
    public void doUpdateWithDataId(String dataType, String dataId) {
        // dataType 判别来源（自定义枚举），命中才处理
    }

    @Override
    public void doDeleteWithDataIdList(String dataType, List<String> dataIdList) {
    }

    // 接口共 10 个回调，按需覆写：Add/Update/Delete × WithDataId/WithDataIdList/WithData/WithDataList
    // （其中 Add/Update 有 WithData/WithDataList，Delete 只有 WithDataId/WithDataIdList）
}
```

- @Component 即自动注册（CommonDataChangeEventCenter 收集 Spring 容器中所有实现）
- 平台侧发事件的写法（我的插件改了共享数据也要广播）：`CommonDataChangeEventCenter.doUpdateWithData(Xxx.class)` 等——写在 Service 的增删改里
- dataType：跟随事件来源（BizDataTypeEnum 这类插件级枚举定义自己的类型）

## 扩展点 2：复用平台能力（注入 *-api）

```java
@Resource
private SysUserApi sysUserApi;          // 用户
@Resource
private DevConfigApi devConfigApi;      // 系统配置（业务参数唯一正解）
@Resource
private DevFileApi devFileApi;          // 文件
@Resource
private DevMessageApi devMessageApi;    // 站内信（自动实时推送）
```

完整清单见 plugin-architecture 技能"现成 API 速查"表。**先查有没有现成 api，再考虑自己写。**

## 什么情况下才真的要改框架

满足全部三条才动：
1. 扩展点表里确实没有对应姿势
2. 平台行为本身要变（不是"加上我的业务"，而是"改掉平台默认"）
3. 用户明确知晓升级冲突风险并接受

改法约束：优先"同包同名 @Configuration 覆盖 Bean/条件装配"，其次最小 diff 修改；改动必须记入 docs 并在 /sync 报告中标注"框架改动点"。

## 常见错误正误对照

| ❌ | ✅ |
|---|---|
| 直接在 SysUserService 里加业务逻辑 | biz 插件建自己的域，跨插件走 SysUserApi |
| 业务开关写死在代码/properties | DEV_CONFIG + DevConfigApi 读取 |
| 改共享数据后不管缓存 | 发 CommonDataChangeEventCenter 事件 |
| 想收平台变更通知却去改 sys 源码 | 实现 CommonDataChangeListener |
| 每个新需求都想新建插件 | 默认进 snowy-plugin-biz/modular（见 plugin-architecture） |

## 检查清单

- [ ] 需求先过了扩展点速查表
- [ ] 监听器/provider 放对位置（core/listener、provider/）
- [ ] 改共享数据后有广播事件
- [ ] 未修改 6 个平台插件的任何文件（或已按"三条全满足"记录改动）

## 参考实现

| 文件 | 说明 |
|---|---|
| `snowy-plugin/snowy-plugin-biz/src/main/java/vip/xiaonuo/biz/core/listener/BizDataChangeListener.java` | 监听器官方范本（监听 sys 变更清 biz 缓存） |
| `snowy-common/src/main/java/vip/xiaonuo/common/listener/CommonDataChangeEventCenter.java` | 事件中心（注册/广播） |
| `snowy-common/src/main/java/vip/xiaonuo/common/listener/CommonDataChangeListener.java` | 监听接口（全部回调） |
| `snowy-common/src/main/java/vip/xiaonuo/common/annotation/CommonWrapper.java` | 返回包装注解 |
| `snowy-plugin/snowy-plugin-dev/src/main/java/vip/xiaonuo/dev/modular/job/task/DevJobTimerTaskRunner.java` | 定时任务扩展点范本 |
| `snowy-plugin-api/snowy-plugin-dev-api/src/main/java/vip/xiaonuo/dev/api/` | 可注入的平台能力全集 |
