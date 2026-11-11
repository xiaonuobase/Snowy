#!/usr/bin/env node
/**
 * PreToolUse Hook - 危险命令拦截与文件守护 (Snowy 版，Claude Code、Codex、Qwen Code 共用)
 * 拦截: Bash 危险命令、Windows `> nul` 误创建文件
 * 守护: 平台模块新建 Java 文件缺版权头警告（业务代码不检查）、框架模块修改警告、敏感配置写入提醒、技能副本误改提醒
 * 原则: 任何解析失败都放行，永不阻断正常工作流
 *
 * 三个工具的工具名不同:
 *   Claude Code: Bash（command）、Write（file_path + content）、Edit/MultiEdit（file_path）
 *   Codex:       Bash（command）、apply_patch（tool_input.command 为补丁文本，含 *** Add File / Update File / Delete File）
 *   Qwen Code:   run_shell_command、write_file、edit（参数名与 Claude Code 的 Bash/Write/Edit 一致，映射后复用同一套检查）
 */

const fs = require('fs');

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

// Qwen Code 工具名映射到 Claude Code 的同类工具
const QWEN_TOOLS = { run_shell_command: 'Bash', write_file: 'Write', edit: 'Edit' };
const isQwen = Object.prototype.hasOwnProperty.call(QWEN_TOOLS, input.tool_name);
const toolName = QWEN_TOOLS[input.tool_name] || input.tool_name || '';
const toolInput = input.tool_input || {};

// 放行：不输出任何内容
function allow() {
  process.exit(0);
}

// 阻断：原因会反馈给模型
function block(reason) {
  process.stdout.write(JSON.stringify({
    hookSpecificOutput: {
      hookEventName: 'PreToolUse',
      permissionDecision: 'deny',
      permissionDecisionReason: reason
    }
  }));
  process.exit(0);
}

// 警告并放行：systemMessage 给使用者看，additionalContext 给模型看
// Qwen Code 要求必须给出 permissionDecision，其 allow 仍走正常权限确认；Claude Code 的 allow 会跳过权限确认，所以只对 Qwen Code 输出
function warn(messages) {
  const message = messages.join('\n');
  const hookSpecificOutput = {
    hookEventName: 'PreToolUse',
    additionalContext: message
  };
  if (isQwen) {
    hookSpecificOutput.permissionDecision = 'allow';
    hookSpecificOutput.permissionDecisionReason = message;
  }
  process.stdout.write(JSON.stringify({
    systemMessage: message,
    hookSpecificOutput
  }));
  process.exit(0);
}

// ========== Bash 检查 ==========
if (toolName === 'Bash') {
  const command = toolInput.command || '';

  // Windows 下 `> nul` 会创建名为 nul 的文件，应使用 /dev/null
  if (/>\s*nul(\s|$)/i.test(command)) {
    block('检测到 `> nul`：Windows 下会创建名为 nul 的文件。Git Bash 请使用 `> /dev/null`，PowerShell 请使用 `> $null`');
  }

  // 危险命令模式（阻断）
  const dangerPatterns = [
    { pattern: /rm\s+-rf\s+\/(\s|$)/, reason: '禁止递归删除根目录' },
    { pattern: /drop\s+(database|schema)/i, reason: '禁止删除数据库' },
    { pattern: /truncate\s+table/i, reason: '禁止清空表数据' },
    { pattern: /git\s+push\s+.*--force.*\s+(master|main)\b/, reason: '禁止 force push 主分支' },
    { pattern: /git\s+push\s+-f\s+(origin\s+)?(master|main)\b/, reason: '禁止 force push 主分支' },
    { pattern: /git\s+reset\s+--hard\s+HEAD~\d+/, reason: '禁止硬回退多个提交（会丢失提交历史）' },
    { pattern: /mkfs(\.|\s)/i, reason: '禁止格式化磁盘' },
    { pattern: /:\(\)\s*\{\s*:\|:\s*&\s*\}\s*;:/, reason: '禁止 fork 炸弹' },
    { pattern: /dd\s+.*of=\/dev\/(sd|hd|nvme)/i, reason: '禁止直接写磁盘设备' }
  ];

  for (const item of dangerPatterns) {
    if (item.pattern.test(command)) {
      block(item.reason);
    }
  }

  // 警告命令模式（放行但提醒）
  const warnPatterns = [
    { pattern: /git\s+push\s+.*--force/, message: '注意：正在 force push，请确认分支正确' },
    { pattern: /npm\s+publish/, message: '注意：正在发布 npm 包，请确认' },
    { pattern: /git\s+clean\s+-fd/, message: '注意：git clean 会删除未跟踪文件（含可能的未提交新代码）' }
  ];

  const warnings = warnPatterns.filter(item => item.pattern.test(command)).map(item => item.message);
  if (warnings.length > 0) {
    warn(warnings);
  }

  allow();
}

// ========== 文件写入检查 ==========

// 解析 Codex 补丁文本，提取涉及的文件；新增文件同时提取内容
function parsePatch(patchText) {
  const files = [];
  let current = null;
  for (const line of patchText.split(/\r?\n/)) {
    const header = line.match(/^\*\*\* (Add|Update|Delete) File: (.+)$/);
    const moveTo = line.match(/^\*\*\* Move to: (.+)$/);
    if (header) {
      current = { filePath: header[2].trim(), isNew: header[1] === 'Add', content: '' };
      files.push(current);
    } else if (moveTo) {
      current = { filePath: moveTo[1].trim(), isNew: false, content: '' };
      files.push(current);
    } else if (line.startsWith('***')) {
      current = null;
    } else if (current && current.isNew && line.startsWith('+')) {
      current.content += line.substring(1) + '\n';
    }
  }
  return files;
}

// 统一成 { filePath, isNew, content } 列表；isNew 表示提供了完整文件内容（可检查版权头）
let targets = [];
if (toolName === 'Write') {
  targets = [{ filePath: toolInput.file_path || '', isNew: true, content: toolInput.content || '' }];
} else if (toolName === 'Edit' || toolName === 'MultiEdit') {
  targets = [{ filePath: toolInput.file_path || '', isNew: false, content: '' }];
} else if (toolName === 'apply_patch') {
  targets = parsePatch(toolInput.command || toolInput.input || toolInput.patch || '');
}

const warnings = [];

for (const target of targets) {
  const filePath = target.filePath.replace(/\\/g, '/');
  const fileName = filePath.split('/').pop();

  // 敏感配置文件写入提醒（不阻止）
  const sensitiveFiles = [
    'application.properties',
    'application-docker.yml',
    'docker-compose.yml',
    'logback-spring.xml'
  ];
  if (sensitiveFiles.some(f => filePath.endsWith(f))) {
    warnings.push('注意：正在写入配置文件 ' + fileName + '：注意不要泄露数据库口令/密钥，不要提交生产环境配置');
  }

  // 框架模块：平台底座代码，业务代码放 snowy-plugin-biz 或用户自建的业务插件
  const frameworkPaths = [
    '/snowy-common/',
    '/snowy-plugin/snowy-plugin-sys/',
    '/snowy-plugin/snowy-plugin-auth/',
    '/snowy-plugin/snowy-plugin-dev/',
    '/snowy-plugin/snowy-plugin-gen/',
    '/snowy-plugin/snowy-plugin-client/',
    '/snowy-plugin/snowy-plugin-mobile/',
    '/snowy-plugin-api/',
    '/snowy-web-app/'
  ];
  const isFramework = frameworkPaths.some(p => ('/' + filePath).includes(p));

  // Java 版权头检查：只要求平台模块保持 Apache 2.0 版权声明，用户业务代码不强制（仅能检查提供完整内容的新文件）
  if (isFramework && target.isNew && filePath.endsWith('.java')) {
    const hasLicense = target.content.includes('Copyright [2022] [https://www.xiaonuo.vip]');
    if (!hasLicense) {
      warnings.push('注意：' + fileName + ' 位于 Snowy 平台模块，头部应保持 12 行 Apache 2.0 版权声明（含 "Copyright [2022] [https://www.xiaonuo.vip]"），可参考同模块现有 Java 文件补齐');
    }
  }

  // 框架模块保护：修改平台底座时提醒
  if (isFramework) {
    warnings.push('注意：正在修改 Snowy 平台框架模块（' + filePath.split('/').slice(-2).join('/') + '）：业务代码应放在 snowy-plugin-biz 或自建业务插件，修改框架会影响升级兼容性');
  }

  // 技能副本保护：.claude/skills 由同步脚本生成，直接改会被覆盖或产生冲突
  if (filePath.includes('.claude/skills/')) {
    warnings.push('注意：.claude/skills/ 是 .agents/skills/ 的自动副本，请改 .agents/skills/ 下的对应文件，改完运行 node .agents/scripts/sync-skills.js');
  }
}

if (warnings.length > 0) {
  warn(warnings);
}

// 其他工具放行
allow();
