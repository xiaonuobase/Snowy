---
name: api-verify
description: AI 接口自测闭环：SM2 加密登录拿 token → curl 直调新接口 → 断言 CommonResult → 出报告。让 AI 不依赖人工点页面就能验证自己生成的接口。触发场景：1) 刚写完 Controller/Service 要验证接口通不通 2) 用户报"接口不对"需要复现请求 3) 验证权限/参数校验行为 4) 需要 token 调 /doc.html 之外的接口。触发词：接口测试、自测、curl、token、登录拿token、调接口、验证接口、冒烟、smoke、401复现、请求复现。注意：环境没起来先看 env-setup；报错排查的完整决策树见 bug-detective。
---

# AI 接口自测闭环

**核心价值**：后端在跑（82 端口）时，AI 可以自己完成"登录 → 拿 token → 调接口 → 断言结果"的完整验证，不必让用户手点页面。

## 第 1 步：确认后端活着

```bash
curl -s http://localhost:82/          # 应输出 WELCOME；无响应 → 先走 env-setup
```

## 第 2 步：生成 SM2 登录密文（后端强制 SM2，明文会 PWD_DECRYPT_ERROR）

登录口令必须先用**出厂公钥**做 SM2 加密（公钥硬编码在 `CommonCryptogramUtil`，前后端同一把）。用一条 node 命令产出：

```bash
# 密文是随机的，每次生成都不同——正常
node -e "const s=require('snowy-admin-web/node_modules/sm-crypto');console.log(s.sm2.doEncrypt('Snowy@2026!','04298364ec840088475eae92a591e01284d1abefcda348b47eb324bb521bb03b0b2a5bc393f6b71dabb8f15c99a0050818b56b23f31743b93df9cf8948f15ddb54',1))"
```

依赖：`snowy-admin-web/node_modules/sm-crypto`（前端执行过 npm install 即有；没有则先装，或临时 `npm i sm-crypto --registry=https://registry.npmmirror.com` 到任意目录）。cipherMode=1（C1C3C2），与前端 smCrypto.js 一致。

## 第 3 步：登录拿 token

```bash
# 密码参数名 password，账号 password 均为上一步密文/明文账号
RESP=$(curl -s -X POST http://localhost:82/auth/b/doLogin \
  -H "Content-Type: application/json" \
  -d '{"account":"superAdmin","password":"<上一步的SM2密文>","device":0}')
echo "$RESP"
# 成功：{"code":200,"data":{"token":"..."},...}
# 账密错：code!=200 且 msg 提示账号或密码错误（核对 Snowy@2026! 与密文重生成）
```

取 token：`TOKEN=$(echo "$RESP" | node -e "let d='';process.stdin.on('data',c=>d+=c).on('end',()=>{const j=JSON.parse(d);console.log(j.data?.token||'')})")`

> 若系统开启了图形验证码（DEV_CONFIG 可配），doLogin 还需 validCode/validCodeReqNo——此时改用 /doc.html 手动调试或让用户从浏览器复制 token（请求头名就是 `token`）。

## 第 4 步：调目标接口并断言

```bash
curl -s "http://localhost:82/biz/xxx/page?current=1&size=10" -H "token: $TOKEN"
# 或 POST：
curl -s -X POST http://localhost:82/biz/xxx/add -H "token: $TOKEN" -H "Content-Type: application/json" -d '{...}'
```

**断言表**：

| 期望 | 判定 |
|---|---|
| 业务正常 | `code === 200`，`data` 结构正确（分页是 `data.records/total`） |
| 未登录 | 401/`code!==200` 且提示登录 → token 头没带或过期（头名是 `token`，不是 Authorization） |
| 无权限 | 403 类提示 → 角色未授权该接口 URL（superAdmin 全通过，可先用它排除权限因素） |
| 参数校验失败 | msg 为 Param 里写的中文校验消息 |
| 业务异常 | msg 为 CommonException 抛出的中文消息——**这就是定位线索** |

## 第 5 步：输出验证报告

```markdown
## 接口自测报告
| 接口 | 结果 | 说明 |
|---|---|---|
| POST /auth/b/doLogin | ✅ 200 | token 获取成功 |
| GET /biz/xxx/page | ✅ 200 | 返回 3 条记录，含逻辑删除过滤 |
| POST /biz/xxx/add | ✅ 200 | 落库 ID=xxx（已查库核对） |
| POST /biz/xxx/add（缺name） | ✅ 按预期拦截 | "name不能为空" |
```

有条件时用 mysql 复核落库（连接串从 application.properties 解析）：
```bash
mysql -uroot -p*** snowy -e "SELECT ID,NAME,DELETE_FLAG FROM BIZ_XXX ORDER BY CREATE_TIME DESC LIMIT 3;"
```

## 使用原则

- 生成接口代码后**主动自测**再交付（本技能 = /dev 收尾步骤的自动化版）
- 测试数据用"测试-"前缀，验证完清理（或逻辑删除）
- 密文/口令不写入任何文件，只在命令行内联使用
- 自测发现问题 → 修复 → 重测，闭环后再报告

## 参考实现

| 文件 | 说明 |
|---|---|
| `snowy-plugin/snowy-plugin-auth/src/main/java/vip/xiaonuo/auth/modular/login/controller/AuthController.java` | /auth/b/doLogin 定义 |
| `snowy-plugin/snowy-plugin-auth/src/main/java/vip/xiaonuo/auth/modular/login/service/impl/AuthServiceImpl.java` | SM2 解密 + SM3 比对逻辑 |
| `snowy-common/src/main/java/vip/xiaonuo/common/util/CommonCryptogramUtil.java` | SM2 公钥与加解密 |
| `snowy-admin-web/src/utils/smCrypto.js` | 前端同款加密（cipherMode=1） |
| `snowy-web-app/src/main/resources/application.properties` | sa-token.token-name=token |
