---
name: scheduled-jobs
description: Snowy 定时任务规范：CommonTimerTaskRunner 接口实现 + DEV_JOB 表配置 + Cron 表达式。触发场景：1) 业务需要定时执行（对账/清理/汇总）2) 添加或排查定时任务 3) 写 Cron 表达式。触发词：定时任务、定时、Cron、调度、DEV_JOB、TimerTaskRunner、轮询、周期执行、任务日志。
---

# Snowy 定时任务规范

## 机制（Snowy 自研 timer 体系，不是 SnailJob/xxl-job/quartz）

```
后端：写一个 CommonTimerTaskRunner 实现类（@Component）
界面：系统 → 系统运维 → 任务调度 → 新增任务（DEV_JOB 表）
      填：任务类名（下拉选你的 Runner 类）、Cron；列表里用启停开关启用
执行：Hutool CronUtil 按 Cron 触发，反射拿到 Runner Bean 调 action(extJson)；平台不记录任务执行历史
```

## 写一个定时任务（标准模板）

```java
package vip.xiaonuo.biz.core.timer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vip.xiaonuo.common.timer.CommonTimerTaskRunner;

/**
 * 供应商月度对账任务
 *
 * @author 你的名字
 * @date  2026/08/18 10:00
 **/
@Slf4j
@Component
public class BizSupplierMonthlyTimerTaskRunner implements CommonTimerTaskRunner {

    @Resource
    private BizSupplierService bizSupplierService;

    @Override
    public void action(String extJson) {
        // extJson = 界面配置的扩展 JSON 参数（可传开关/日期偏移等）
        log.info("开始执行供应商月度对账");
        bizSupplierService.doMonthlyReconcile(extJson);
    }
}
```

放置位置：业务任务放所在业务插件的 `core/timer/`（biz 插件即 `biz/core/timer/`，和 sys、client 插件的密码到期提醒任务一致）；dev 插件的 `DevJobTimerTaskRunner` 在 `modular/job/task/`，只当接口示例看，不作为目录参考。自定义包名的 Runner 要被启动模块扫描到才会出现在任务下拉里（见 plugin-architecture）。

写完代码后：**重启后端 → 任务调度界面 → 新增 → 选择该 Runner → 填 Cron → 启用**。

## Cron 速查

| 表达式 | 含义 |
|---|---|
| `0 0 2 * * ?` | 每天凌晨 2 点 |
| `0 */5 * * * ?` | 每 5 分钟 |
| `0 0 0 1 * ?` | 每月 1 号零点 |
| `0 0 9-18 * * MON-FRI` | 工作日 9-18 点整点 |

前端有 Cron 组件可视化生成（`snowy-admin-web/src/components/Cron/`）。

## 任务规范

注：平台自带的 Runner（sys/client 的密码到期提醒、dev 的示例任务）都没有做分批和幂等标记，新任务不要拿它们当范本，按本节要求实现。

- **幂等**：任务可能重复触发/重叠执行，action 内先判"本次是否已处理"
- **大数据量切片**：分批查处理（每批几百条），别一次 load 全表
- **异常自吞**：单条失败 log.error 继续，不要让整批中断；整体失败要有日志/告警
- **参数走 extJson 或 DEV_CONFIG**：只属于这个任务的可变参数（天数偏移、开关）放任务的扩展参数 extJson；多个功能共用的业务策略放 DEV_CONFIG（如平台的密码到期天数）。不要写死
- 日志：关键节点 log.info，失败 log.error（只写进应用日志文件，平台没有任务执行日志界面；要看执行历史得业务自建记录表）

## 常见错误正误对照

| 错误 | 正确 |
|---|---|
| `@Scheduled(cron=...)` 注解（Spring 原生） | CommonTimerTaskRunner + DEV_JOB 界面配置（可管理可控） |
| 引入 quartz/xxl-job 依赖 | Snowy 自研 timer 已覆盖 |
| 任务里把状态写死 | 可变参数走 extJson 或 DEV_CONFIG |
| 忘记在界面注册任务 | 代码只是 Bean，必须界面新增并启用才调度 |
| 一次性全表加载处理 | 分批 + 幂等 |

## 检查清单

- [ ] 业务 Runner 在所在插件的 core/timer/ 下，@Component，实现 CommonTimerTaskRunner
- [ ] 可变参数来自 extJson 或 DEV_CONFIG，处理了幂等和分批
- [ ] 界面已配置任务（Cron + 启用）
- [ ] 关键节点和失败都有 log 输出（应用日志）；需要可视化执行历史时，已自建记录

## 参考实现

| 文件 | 说明 |
|---|---|
| `snowy-common/src/main/java/vip/xiaonuo/common/timer/CommonTimerTaskRunner.java` | 任务接口定义 |
| `snowy-plugin/snowy-plugin-dev/src/main/java/vip/xiaonuo/dev/modular/job/task/DevJobTimerTaskRunner.java` | 官方示例任务 |
| `snowy-plugin/snowy-plugin-dev/src/main/java/vip/xiaonuo/dev/modular/job/` | 任务管理模块（DEV_JOB） |
| `snowy-admin-web/src/components/Cron/` | Cron 可视化组件 |
