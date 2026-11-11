#!/usr/bin/env node
/**
 * UserPromptSubmit Hook - 强制技能评估 (Snowy 版，Claude Code、Codex、Qwen Code 共用)
 * 功能: 开发场景下把技能激活要求注入模型上下文，提升技能激活率
 *
 * 注入策略（节省 token）:
 *   - 会话首轮: 注入完整版（激活流程 + 核心规范速记）
 *   - 后续轮次: 只注入精简提醒
 *   - 拿不到会话 ID 或标记文件读写失败时退回完整版，保证规则不丢
 *   会话标记存放在系统临时目录 snowy-skill-eval/ 下，超过 7 天自动清理
 *
 * 技能索引不再由本脚本注入: 三个工具都会自动加载 AGENTS.md（Claude Code 经 CLAUDE.md 的 @AGENTS.md 引入），
 * 索引已在上下文中，重复注入只会浪费 token。新增技能只需登记到 AGENTS.md 索引区，本脚本无需修改
 *
 * 注册位置: .claude/settings.json（Claude Code）、.codex/hooks.json（Codex）、.qwen/settings.json（Qwen Code）
 * 输出格式: hookSpecificOutput.additionalContext（三个工具均支持）
 */

const fs = require('fs');
const os = require('os');
const path = require('path');

const STATE_DIR = path.join(os.tmpdir(), 'snowy-skill-eval');
const STATE_TTL_MS = 7 * 24 * 60 * 60 * 1000;

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

// Qwen Code 在工具结果回传等续写场景也会触发本事件，只在用户真正提交输入（带 submitted_prompt）时注入
// Qwen Code 会给钩子进程设置 QWEN_PROJECT_DIR 环境变量，Claude Code 与 Codex 不设置
const isQwen = Boolean(process.env.QWEN_PROJECT_DIR);
const prompt = ((isQwen ? input.submitted_prompt : input.prompt) || '').trim();

if (isQwen && !prompt) {
  process.exit(0);
}

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

// 后台任务完成通知（Claude Code 子代理/后台命令结束时回调）不是用户输入，跳过
if (prompt.includes('<task-notification>')) {
  process.exit(0);
}

// 检测是否是显式调用技能/命令：Claude Code 用 /名称，Codex 用 $名称
// 规则：首个词为 / 或 $ 加技能名（只含字母、数字、-、_、:），排除 /sys/user 路径和 /CLAUDE.md 这样的文件名
const isExplicitInvocation = /^[\/$][A-Za-z0-9_:-]+$/.test(prompt.split(/\s/)[0]);

if (isExplicitInvocation) {
  // 用户已明确指定技能，跳过技能评估
  process.exit(0);
}

// 清理过期的会话标记，失败忽略
function cleanExpiredMarkers() {
  const now = Date.now();
  for (const name of fs.readdirSync(STATE_DIR)) {
    try {
      const file = path.join(STATE_DIR, name);
      if (now - fs.statSync(file).mtimeMs > STATE_TTL_MS) {
        fs.unlinkSync(file);
      }
    } catch {
      // 单个文件清理失败不影响注入
    }
  }
}

// 判断本会话是否已注入过完整版：首次调用写入标记并返回 false，之后返回 true
// 拿不到会话 ID 或读写失败时返回 false（退回完整版）
function hasInjectedFull(sessionId) {
  const safeId = String(sessionId || '').replace(/[^A-Za-z0-9_-]/g, '');
  if (!safeId) {
    return false;
  }
  try {
    fs.mkdirSync(STATE_DIR, { recursive: true });
    const marker = path.join(STATE_DIR, safeId);
    if (fs.existsSync(marker)) {
      return true;
    }
    fs.writeFileSync(marker, '');
    cleanExpiredMarkers();
    return false;
  } catch {
    return false;
  }
}

const CORE_RULES = 'Snowy 核心规范速记（写码前扫一眼）：@Resource 注入｜ServiceImpl 必须继承｜BeanUtil 转换｜@Getter @Setter｜CommonResult/CommonException｜URL 动词式写方法上且 @SaCheckPermission 值=URL｜String 主键｜表名全大写｜业务可放 biz 或自建插件、可自定义包名（非 vip.xiaonuo 开头须补扫描配置，见 plugin-architecture）｜业务代码不强制版权头';

function buildFullInstructions() {
  return `## 强制技能激活流程（必须执行）

### 步骤 1 - 评估（必须在响应中明确展示）

针对用户问题，对照 AGENTS.md 技能索引区列出匹配的技能：\`技能名: 理由\`，无匹配则写"无匹配技能"（当前上下文中没有 AGENTS.md 时先读取该文件）

### 步骤 2 - 激活

对步骤 1 列出的每个匹配技能逐个激活，禁止只激活部分：
- Claude Code、Qwen Code：逐个串行调用 Skill 工具（禁止并行调用）
- Codex 及其他工具：逐个读取 \`.agents/skills/{技能名}/SKILL.md\` 全文
如果列表为 4 个以上，只激活最相关的 4 个（优先级：与代码编写直接相关的 > 方法论类的）。
本会话已激活过的技能内容仍在上下文中，不要重复激活；仅当上下文被压缩、技能细节已不在上下文时才重新激活。
流程技能只在用户显式调用时执行，评估时不要主动激活。

### 步骤 3 - 实现

全部激活完成后，再开始实现用户的请求。

### 关键规则

1. 只有确实相关的技能才列入步骤 1，宁缺毋滥
2. 技能激活后必须遵循技能内的规范（正误对照、检查清单）
3. 涉及写 Java 代码时，牢记本项目规范与同类快速开发框架方向相反（详见 code-patterns 技能）
4. ${CORE_RULES}`;
}

function buildShortInstructions() {
  return `## 技能评估提醒（必须执行）

1. 评估：在回复中列出匹配技能（\`技能名: 理由\`，无匹配写"无匹配技能"，最多 4 个，宁缺毋滥）；技能索引见 AGENTS.md 技能索引区（当前上下文中没有时先读取该文件）
2. 激活：只激活本会话尚未激活过的技能（Claude Code、Qwen Code 逐个串行调用 Skill 工具；Codex 及其他工具读取 \`.agents/skills/{技能名}/SKILL.md\`）；已激活过的不要重复激活，除非上下文被压缩后技能细节已丢失
3. 流程技能只在用户显式调用时执行；激活完成后再实现
4. ${CORE_RULES}`;
}

const instructions = hasInjectedFull(input.session_id || input.sessionId)
  ? buildShortInstructions()
  : buildFullInstructions();

process.stdout.write(JSON.stringify({
  hookSpecificOutput: {
    hookEventName: 'UserPromptSubmit',
    additionalContext: instructions
  }
}));
process.exit(0);
