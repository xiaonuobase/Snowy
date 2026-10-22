---
name: crypto-sm
description: Snowy 国密体系使用规范：登录密码 SM2 加密传输、口令 SM3 摘要存储、字段级 SM4-CBC 落库加密（CommonSm4CbcTypeHandler）、CommonCryptogramUtil API、前端 smCrypto.js 联动。触发场景：1) 新增敏感字段需要加密存储 2) 处理登录/改密逻辑 3) 前后端加解密联调。触发词：国密、SM2、SM3、SM4、加密、解密、密码、敏感字段、手机号加密、密文、脱敏、CommonCryptogramUtil、TypeHandler、等保。
---

# Snowy 国密体系规范

## 三层国密架构

| 层 | 算法 | 用途 | 实现位置 |
|---|---|---|---|
| 传输 | **SM2**（非对称） | 登录密码前端加密 → 后端解密（防抓包明文） | 前端 `snowy-admin-web/src/utils/smCrypto.js`；后端 `CommonCryptogramUtil` |
| 摘要 | **SM3**（哈希） | 口令落库摘要（不可逆） | `SysPasswordUtil`（sys 插件）+ CommonCryptogramUtil |
| 字段 | **SM4-CBC**（对称） | 手机号/证件号等敏感字段落库加密 | `CommonSm4CbcTypeHandler`（snowy-common/handler） |

底座：sm-crypto 0.3.2 + BouncyCastle 1.70。软件层面满足等保测评要求。

## CommonCryptogramUtil API（snowy-common/util）

```java
// SM2 —— 登录密码传输
String plain = "示例口令";
String cipher = CommonCryptogramUtil.doSm2Encrypt(plain);
String decrypted = CommonCryptogramUtil.doSm2Decrypt(cipher);

// SM3 —— 摘要
String digest = CommonCryptogramUtil.doHashValue(plain);

// SM4 —— 通用对称加解密（字段加密底层也是它）
String enc = CommonCryptogramUtil.doSm4CbcEncrypt(plain);
String dec = CommonCryptogramUtil.doSm4CbcDecrypt(enc);
```

当前实现的密钥常量在 `CommonCryptogramUtil` 中，前端公钥在 `smCrypto.js` 中；没有 `snowy.cryptogram.*` 配置绑定。修改密钥需同步前后端，并为已有 SM4 密文制定迁移方案。

## 字段级 SM4 加密（新增敏感字段的标准做法）

```java
@TableName(value = "BIZ_PATIENT", autoResultMap = true)     // ① 必须 autoResultMap = true
public class BizPatient extends CommonEntity {

    @TableId
    private String id;

    /** 手机号（SM4 加密落库） */
    @TableField(typeHandler = CommonSm4CbcTypeHandler.class)  // ② 加 TypeHandler
    @Schema(description = "手机号")
    private String phone;
}
```

效果：入库自动密文（`insert/update` 生效），查询结果自动解密透明返回。参考实战：`BizUser.phone / idCardNumber / emergencyPhone`（`biz/modular/user/entity/BizUser.java`）。

### ⚠️ SM4 字段的查询限制（必读）

当前 SM4-CBC 使用固定 key/IV，同一明文对应相同密文。不要直接用明文对密文字段做 like 查询。处理方案：
- 精确查询：`queryWrapper.lambda().eq(BizPatient::getPhone, 手机号明文)` —— TypeHandler 会让 MP 用密文比对？**不会自动**，需要走 eq 前手动 `CommonCryptogramUtil.doSm4CbcEncrypt(phone)` 加密后比对（以 SysUserServiceImpl 现有手机号查询写法为准，模仿之）
- 模糊查询：需要另行设计检索方案；整值 SM3 摘要辅助列只能用于精确匹配/查重，不能支持子串查询
- 列表展示：自动解密无需处理；需要脱敏展示（138****5678）在前端或 Result 层处理

## 登录/改密流程（不要自造）

```
登录：前端 smCrypto.js 用 SM2 公钥加密密码 → /auth/b/doLogin 密文上送
     → AuthServiceImpl doSm2Decrypt → 与库中 SM3 摘要比对
改密/新建用户：SysPasswordUtil 加密（SM3）后存储
```

新业务涉及口令（如二级密码）：同样 SM2 传输 + SM3 存储，直接复用 CommonCryptogramUtil / SysPasswordUtil。

## 前端联动

```js
// snowy-admin-web/src/utils/smCrypto.js
import smCrypto from '@/utils/smCrypto'
const cipher = smCrypto.doSm2Encrypt('示例口令')
```

登录接口的密码是十六进制密文，长度随明文长度变化。

## 禁止事项

- ❌ 明文存储/传输任何口令、身份证、手机号
- ❌ 自选 MD5/SHA-1/DES/AES（项目标准是国密）
- ❌ 把示例密钥当作部署隔离机制；调整当前常量实现时需同时考虑前后端兼容与历史数据迁移
- ❌ 日志输出明文敏感字段（log 里脱敏或干脆不打）
- ❌ 对 SM4 字段写 like 查询（永远查不到）

## 检查清单

- [ ] 新敏感字段：@TableName(autoResultMap=true) + @TableField(typeHandler=CommonSm4CbcTypeHandler.class)
- [ ] 该字段有精确/模糊查询需求时已评估方案（密文 eq / HASH 辅助列）
- [ ] 口令类只存 SM3 摘要，传输走 SM2
- [ ] 已核对当前密钥实现；如调整密钥，已同步前后端并规划历史数据迁移
- [ ] 日志无明文敏感信息

## 参考实现

| 文件 | 说明 |
|---|---|
| `snowy-common/src/main/java/vip/xiaonuo/common/util/CommonCryptogramUtil.java` | 国密工具 API |
| `snowy-common/src/main/java/vip/xiaonuo/common/handler/CommonSm4CbcTypeHandler.java` | 字段加密 TypeHandler |
| `snowy-plugin/snowy-plugin-biz/src/main/java/vip/xiaonuo/biz/modular/user/entity/BizUser.java` | SM4 字段实战 |
| `snowy-plugin/snowy-plugin-auth/src/main/java/vip/xiaonuo/auth/modular/login/service/impl/AuthServiceImpl.java` | SM2 解密 + SM3 校验现场 |
| `snowy-admin-web/src/utils/smCrypto.js` | 前端国密 |
| `snowy-common/src/main/java/vip/xiaonuo/common/util/CommonCryptogramUtil.java` | 当前密钥常量与 SM4 IV |
