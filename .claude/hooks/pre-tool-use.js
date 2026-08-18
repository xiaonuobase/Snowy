#!/usr/bin/env node
/**
 * PreToolUse Hook - 危险命令拦截与文件守护 (Snowy 版)
 * 拦截: Bash 危险命令、Windows `> nul` 误创建文件
 * 守护: Snowy 版权头缺失警告、框架模块修改警告、敏感配置写入提醒
 * 原则: 任何解析失败都放行，永不阻断正常工作流
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

const toolName = input.tool_name || '';
const toolInput = input.tool_input || {};

function allow() {
  process.stdout.write(JSON.stringify({ continue: true }));
  process.exit(0);
}

function block(reason) {
  process.stdout.write(JSON.stringify({
    decision: 'block',
    reason: reason
  }));
  process.exit(0);
}

function warn(message) {
  process.stdout.write(JSON.stringify({
    continue: true,
    systemMessage: message
  }));
  process.exit(0);
}

// ========== Bash 检查 ==========
if (toolName === 'Bash') {
  const command = toolInput.command || '';

  // Windows 下 `> nul` 会创建名为 nul 的文件，应使用 /dev/null
  if (/>\s*nul(\s|$)/i.test(command)) {
    block('检测到 `> nul`：Windows 下会创建名为 nul 的文件。请使用 `> /dev/null`（本环境 shell 为 Git Bash）');
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
    { pattern: /git\s+push\s+.*--force/, message: '⚠️ 正在 force push，请确认分支正确' },
    { pattern: /npm\s+publish/, message: '⚠️ 正在发布 npm 包，请确认' },
    { pattern: /git\s+clean\s+-fd/, message: '⚠️ git clean 会删除未跟踪文件（含可能的未提交新代码）' }
  ];

  for (const item of warnPatterns) {
    if (item.pattern.test(command)) {
      warn(item.message);
    }
  }

  allow();
}

// ========== Write 检查 ==========
if (toolName === 'Write') {
  const filePath = (toolInput.file_path || '').replace(/\\/g, '/');
  const content = toolInput.content || '';

  // 敏感配置文件写入提醒（不阻止）
  const sensitiveFiles = [
    'application.properties',
    'application-docker.yml',
    'docker-compose.yml',
    'logback-spring.xml'
  ];
  if (sensitiveFiles.some(f => filePath.endsWith(f))) {
    warn('⚠️ 正在写入配置文件 ' + filePath.split('/').pop() + '：注意不要泄露数据库口令/密钥，不要提交生产环境配置');
  }

  // Java 版权头检查：Snowy 要求每个 .java 头部有 Apache 2.0 版权声明
  if (filePath.endsWith('.java')) {
    const hasLicense = content.includes('Copyright [2022] [https://www.xiaonuo.vip]');
    if (!hasLicense) {
      warn('⚠️ Snowy 规范：每个 .java 文件头部必须有 12 行 Apache 2.0 版权声明（含 "Copyright [2022] [https://www.xiaonuo.vip]"）。请参考任意现有 Java 文件头部补齐');
    }
  }

  // 框架模块保护：修改平台底座时提醒
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
  if (frameworkPaths.some(p => filePath.includes(p))) {
    warn('⚠️ 正在修改 Snowy 平台框架模块（' + filePath.split('/').slice(-2).join('/') + '）：业务二次开发应优先放在 snowy-plugin-biz，修改框架会影响升级兼容性');
  }

  allow();
}

// 其他工具放行
allow();
