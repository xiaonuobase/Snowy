---
name: env-setup
description: Snowy 新环境搭建与首次启动 playbook：JDK/MySQL/Redis/前端依赖/建库导入/启动验证全清单，含出厂账号真实密码。触发场景：1) 新机器/新同事把项目跑起来 2) 环境重置后重新初始化 3) 启动前的就绪自检 4) 查出厂登录账号密码。触发词：环境搭建、首次启动、跑起来、初始化、安装依赖、导入数据库、建库、登录不了、默认密码、出厂账号、superAdmin、新环境、就绪检查。注意：启动后的故障排查见 bug-detective；本技能讲"从零到能登录"。
---

# Snowy 环境搭建与首启

## 就绪清单（按顺序）

| # | 依赖 | 要求                                                                                                    | 验证 |
|---|---|-------------------------------------------------------------------------------------------------------|---|
| 1 | JDK | **17/21/23**（⚠️ 本机只有 IDEA 内置 JBR 25 时命令行编译会因 Lombok 1.18.30 失败，IDEA 里配 Project SDK 17 构建即可）           | `java -version` |
| 2 | MySQL | 8.0/5.7，建库 `snowy`（utf8mb4）                                                                           | `mysql -e "SHOW DATABASES LIKE 'snowy'"` |
| 3 | 导入种子数据 | 执行 `snowy-web-app/src/main/resources/_sql/snowy_mysql.sql`（33 张表）                                     | `mysql snowy -e "SELECT COUNT(*) FROM SYS_USER"` 应 ≥2 |
| 4 | Redis | 本地 6379，无密码，用 database 1                                                                              | `redis-cli -n 1 ping` |
| 5 | 数据源核对 | `snowy-web-app/src/main/resources/application.properties` 的 dynamic master 段（默认 root/12345678，本机不同则改） | — |
| 6 | 后端构建启动 | IDEA 启动 `vip.xiaonuo.Application`（snowy-web-app），端口 **82**                                            | 浏览器开 `http://localhost:82` 应显示 WELCOME |
| 7 | 前端依赖 | `snowy-admin-web/` 下 `npm install`                                                                    | — |
| 8 | 前端启动 | `npm run dev`，端口 **81**，代理 /api → 82                                                                  | 打开 `http://localhost:81` |

## ★ 出厂登录账号（本仓库种子数据的真实值）

| 账号 | 密码 | 角色 |
|---|---|---|
| `superAdmin` | **`Snowy@2026!`** | 超级管理员（拥有全部权限） |
| `bizAdmin` | **`Snowy@2026!`** | 业务管理员（仅业务模块） |

> ⚠️ **不是 123456**——网上教程/官方演示站说的 123456 是 xiaonuo.vip 演示库的密码。本仓库种子密码的权威来源是 DEV_CONFIG 表 `SNOWY_SYS_DEFAULT_PASSWORD_FOR_B` 配置项（新建用户的默认密码也是它，可在 开发工具→系统配置 里改）。存储为 SM3 摘要：SM3("Snowy@2026!") = b7eb53ce42289cd168e21e37cc3b94333c3e2e691486548a06f5c5fae129f157。
> 登录失败次数过多会锁定（PWD_ERROR 处理逻辑），连不上先确认大小写与感叹号。

## 常用自检命令

```bash
# 端口占用（82 后端 / 81 前端）
netstat -ano | findstr ":82 :81"

# 后端活着吗（应输出 WELCOME）
curl -s http://localhost:82/

# 接口文档（basic 认证 admin/123456）
curl -s -u admin:123456 http://localhost:82/doc.html | head -5

# 表齐了吗
mysql -uroot -p snowy -e "SELECT COUNT(*) AS tables_cnt FROM information_schema.tables WHERE table_schema='snowy';"
```

## AI 首启引导流程（用户说"跑不起来/帮我搭环境"时）

1. 按就绪清单逐项检查，从失败的那项开始修
2. 常见首启故障（详见 bug-detective 树 A）：端口占用 / 库没导 / Redis 没起 / JDK 版本 / 口令不符
3. 全部就绪后：打开前端 → superAdmin / Snowy@2026! 登录 → 看到首页即完成
4. 登录成功后建议用户立刻改密码（系统管理 → 用户中心）

## 新增依赖说明

- Maven 中央仓库直连失败时用阿里云镜像（settings.xml mirrorOf=central → maven.aliyun.com/repository/public）
- npm 失败时用 npmmirror：`npm install --registry=https://registry.npmmirror.com`

## 检查清单

- [ ] 8 项就绪清单全过
- [ ] 登录成功（superAdmin / Snowy@2026!）
- [ ] 提醒用户改默认密码
- [ ] 本机偏离默认的配置（数据库口令等）已回写 application.properties 且未提交明文口令变更

## 参考实现

| 文件 | 说明 |
|---|---|
| `snowy-web-app/src/main/resources/application.properties` | 数据源/Redis/端口/文档认证全配置 |
| `snowy-web-app/src/main/resources/_sql/snowy_mysql.sql` | 种子数据（账号/菜单/DEV_CONFIG 默认密码） |
| `snowy-web-app/src/main/java/vip/xiaonuo/Application.java` | 启动类 |
| `snowy-admin-web/package.json` | 前端依赖与脚本 |
| `CLAUDE.md` 常用命令节 | 构建环境备忘（IDEA 内置 Maven/镜像/JDK17） |
