#!/usr/bin/env node
/**
 * UserPromptSubmit Hook - 强制技能评估 (Snowy 版)
 * 功能: 开发场景下，将 Skills 激活率从约 25% 提升到 90% 以上
 *
 * 适配项目: Snowy v3.0.0 (前后端分离，插件化架构)
 * 后端包名: vip.xiaonuo.*
 * 前端工程: snowy-admin-web/ (Vue3 + Ant Design Vue，JS 非 TS)
 */

const fs = require('fs');

// 从 stdin 读取用户输入
let inputData = '';
try {
  inputData = fs.readFileSync(0, 'utf8');
} catch {
  process.exit(0);
}

let input;
try {
  input = JSON.parse(inputData);
} catch {
  process.exit(0);
}

const prompt = (input.prompt || '').trim();

// 检测是否是恢复会话（防止上下文溢出死循环）
const skipPatterns = [
  'continued from a previous conversation',
  'ran out of context',
  'No code restore',
  'Conversation compacted',
  'commands restored',
  'context window',
  'session is being continued'
];

const isRecoverySession = skipPatterns.some(pattern =>
  prompt.toLowerCase().includes(pattern.toLowerCase())
);

if (isRecoverySession) {
  // 恢复会话，跳过技能评估以防止死循环
  process.exit(0);
}

// 检测是否是斜杠命令
// 规则：以 / 开头，且后面不包含第二个 /（排除 /sys/user 这样的路径）
const isSlashCommand = /^\/[^\/\s]+$/.test(prompt.split(/\s/)[0]);

if (isSlashCommand) {
  // 斜杠命令，跳过技能评估
  process.exit(0);
}

const instructions = `## 强制技能激活流程（必须执行）

### 步骤 1 - 评估（必须在响应中明确展示）

针对用户问题，列出匹配的技能：\`技能名: 理由\`，无匹配则写"无匹配技能"

可用技能（前后端同仓库项目）：
> 注意：snowy-admin-web/ 目录存在，CRUD/dev 类任务应同时生成前端三文件（api js + index.vue + form.vue）
- crud-development: CRUD/业务模块/Entity/Service/Controller/Param 开发
- api-development: API设计/接口规范/URL/CommonResult/异常处理
- plugin-architecture: 插件/模块划分/跨插件调用/provider/新建插件
- code-generator: 代码生成/Beetl/gen/生成器/菜单SQL
- database-ops: 数据库/SQL/建表/大写表名/菜单/字典数据
- backend-annotations: 注解/@CommonLog/@SaCheckPermission/@Trans/@Validated
- code-patterns: 编码规范/禁令/命名/来自RuoYi的惯性错误
- common-toolkit: 工具类/CommonCacheOperator/CommonCryptogramUtil/Hutool
- cache-redis: 缓存/Redis/Redisson/CommonCacheOperator
- file-oss-management: 文件上传/OSS/云存储/MinIO/XnUpload
- sms-mail: 短信/邮件/验证码/sms4j/SMTP
- message-push: 站内信/消息推送/钉钉/企微/飞书/WebSocket
- scheduled-jobs: 定时任务/Cron/DEV_JOB/TimerTaskRunner
- dict-config: 字典/DEV_DICT/BIZ_DICT/DictSelect/枚举/系统配置
- security-auth: 鉴权/Sa-Token/登录/白名单/B端C端/数据范围/权限
- crypto-sm: 国密/SM2/SM3/SM4/加密/密码/敏感字段
- frontend-pc: 前端/Vue/AntdV/api js/index.vue/form.vue/Xn组件
- client-mobile: C端/客户端/移动端/uni-app/mobile插件
- bug-detective: Bug/报错/异常/不工作/排查
- performance-doctor: 性能/慢查询/优化/N+1/缓存
- project-navigator: 找文件/在哪/目录结构/导航
- git-workflow: Git/提交/commit/分支
- env-setup: 环境搭建/首次启动/默认密码/出厂账号/导入数据库
- api-verify: 接口测试/自测/登录拿token/curl验证/冒烟
- platform-extension: 扩展/定制平台/监听/联动/复用平台能力/不改框架
- add-skill: 新增技能/创建skill

### 步骤 2 - 激活

对步骤 1 列出的每个匹配技能，必须逐个串行调用 Skill 工具激活（禁止并行调用、禁止只激活部分）。
如果列表为 4 个以上，只激活最相关的 4 个（优先级：与代码编写直接相关的 > 方法论类的）。

### 步骤 3 - 实现

全部激活完成后，再开始实现用户的请求。

### 关键规则

1. 只有确实相关的技能才列入步骤 1，宁缺毋滥
2. 技能激活后必须遵循技能内的规范（正误对照、检查清单）
3. 涉及写 Java 代码时，牢记本项目规范与 RuoYi 系框架方向相反（详见 code-patterns 技能）
4. Snowy 核心规范速记（写码前扫一眼）：包名 vip.xiaonuo.*｜@Resource 注入｜ServiceImpl 必须继承｜BeanUtil 转换｜@Getter @Setter｜CommonResult/CommonException｜URL 动词式写方法上且 @SaCheckPermission 值=URL｜String 主键｜表名全大写｜每个 .java 带 12 行版权头`;

process.stdout.write(instructions);
process.exit(0);
