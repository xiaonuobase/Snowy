#!/usr/bin/env node
/**
 * Stop Hook - 会话收尾 (Snowy 版，Claude Code、Codex、Qwen Code 共用)
 * 1. 清理 Windows 误创建的 nul 文件：在 cwd 下递归（深度 5，跳过 . 开头目录、node_modules、target）删除名为 nul 的文件，
 *    与 pre-tool-use.js 的 `> nul` 拦截形成双保险
 * 2. 技能自动同步：把 .agents/skills/ 安全同步到 .claude/skills/（副本被手改过的文件不覆盖，只提醒）
 */

const fs = require('fs');
const path = require('path');

const SKIP_DIRS = new Set(['node_modules', 'target', '.git', '.idea', '.claude']);
const MAX_DEPTH = 5;

function removeNulFiles(dir, depth) {
  if (depth > MAX_DEPTH) {
    return 0;
  }
  let removed = 0;
  let entries;
  try {
    entries = fs.readdirSync(dir, { withFileTypes: true });
  } catch {
    return 0;
  }
  for (const entry of entries) {
    const fullPath = path.join(dir, entry.name);
    if (entry.isFile() && entry.name.toLowerCase() === 'nul') {
      try {
        fs.unlinkSync(fullPath);
        removed++;
      } catch {
        // 无法删除（可能被占用），忽略
      }
    } else if (entry.isDirectory() && !entry.name.startsWith('.') && !SKIP_DIRS.has(entry.name)) {
      removed += removeNulFiles(fullPath, depth + 1);
    }
  }
  return removed;
}

const messages = [];

try {
  const removed = removeNulFiles(process.cwd(), 0);
  if (removed > 0) {
    messages.push('已清理 ' + removed + ' 个误创建的 nul 文件（来自 `> nul` 重定向）');
  }
} catch {
  // 清理失败不影响会话
}

try {
  const { syncSkills, conflictMessage } = require('../scripts/sync-skills.js');
  const result = syncSkills();
  if (result.copied.length + result.removed.length > 0) {
    messages.push('已同步技能到 .claude/skills/：复制 ' + result.copied.length + ' 个、删除 ' + result.removed.length + ' 个文件');
  }
  if (result.conflicts.length > 0) {
    messages.push(conflictMessage(result.conflicts));
  }
} catch {
  // 同步失败不影响会话
}

if (messages.length > 0) {
  process.stdout.write(JSON.stringify({ systemMessage: messages.join('\n') }));
}
process.exit(0);
