#!/usr/bin/env node
/**
 * Stop Hook - 清理 Windows 误创建的 nul 文件 (Snowy 版)
 * 在 cwd 下递归（深度 5，跳过 . 开头目录、node_modules、target）删除名为 nul 的文件
 * 与 pre-tool-use.js 的 `> nul` 拦截形成双保险
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

try {
  const cwd = process.cwd();
  const removed = removeNulFiles(cwd, 0);
  if (removed > 0) {
    process.stdout.write(JSON.stringify({
      continue: true,
      systemMessage: '已清理 ' + removed + ' 个误创建的 nul 文件（来自 `> nul` 重定向）'
    }));
  } else {
    process.stdout.write(JSON.stringify({ continue: true }));
  }
} catch {
  process.stdout.write(JSON.stringify({ continue: true }));
}
process.exit(0);
