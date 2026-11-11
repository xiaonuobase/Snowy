#!/usr/bin/env node
/**
 * 技能同步脚本 (Snowy 版)
 * 功能: 把技能唯一来源 .agents/skills/ 单向复制到 .claude/skills/（Claude Code 只认后者）
 * 原因: Windows 下 git 默认把符号链接检出成普通文本文件，所以用复制代替链接
 *
 * 用法:
 *   node .agents/scripts/sync-skills.js           安全同步（副本被手改过的文件不覆盖，报告冲突）
 *   node .agents/scripts/sync-skills.js --check   只检查是否一致，不写文件；不一致时退出码为 1
 *   node .agents/scripts/sync-skills.js --force   强制镜像（以 .agents/skills 为准，覆盖冲突、删除多余文件）
 *
 * 防误删机制: 每次同步后在 .claude/skills-sync-manifest.json 记录副本各文件的指纹（本地文件，不提交）。
 * 副本文件指纹与记录不符、且在 git 中有未提交的改动 = 有人直接改了副本，安全模式下不覆盖、不删除，只报告。
 * 副本文件与 git 已提交版本一致时（如新克隆、别人提交了来源但没同步副本），覆盖后仍可从 git 找回，直接同步。
 */

const fs = require('fs');
const path = require('path');
const crypto = require('crypto');
const { execFileSync } = require('child_process');

const ROOT = path.resolve(__dirname, '..', '..');
const SRC = path.join(ROOT, '.agents', 'skills');
const DST = path.join(ROOT, '.claude', 'skills');
const MANIFEST = path.join(ROOT, '.claude', 'skills-sync-manifest.json');
const IGNORE_NAMES = new Set(['.DS_Store', 'Thumbs.db']);

// 递归列出目录下所有文件的相对路径（统一用 / 分隔）
function listFiles(dir, base = dir) {
  if (!fs.existsSync(dir)) {
    return [];
  }
  let result = [];
  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    if (IGNORE_NAMES.has(entry.name)) {
      continue;
    }
    const fullPath = path.join(dir, entry.name);
    if (entry.isDirectory()) {
      result = result.concat(listFiles(fullPath, base));
    } else if (entry.isFile()) {
      result.push(path.relative(base, fullPath).split(path.sep).join('/'));
    }
  }
  return result;
}

// 计算文件指纹：先把 CRLF 统一成 LF，避免 Windows 换行自动转换造成误判
function hashFile(filePath) {
  const content = fs.readFileSync(filePath).toString('utf8').replace(/\r\n/g, '\n');
  return crypto.createHash('sha1').update(content).digest('hex');
}

function readManifest() {
  try {
    return JSON.parse(fs.readFileSync(MANIFEST, 'utf8')).files || {};
  } catch {
    return {};
  }
}

// 副本文件是否与 git 已提交版本一致（无未提交改动）；git 不可用时按"有改动"处理，宁可报告冲突也不覆盖
function isCommittedInGit(filePath) {
  try {
    const status = execFileSync('git', ['status', '--porcelain', '--', filePath], { cwd: ROOT, stdio: ['ignore', 'pipe', 'ignore'] });
    return status.toString().trim() === '';
  } catch {
    return false;
  }
}

// 副本文件可以安全覆盖或删除：与上次同步记录一致，或与 git 已提交版本一致
function isSafeToReplace(manifest, rel, dstPath, dstHash) {
  return manifest[rel] === dstHash || isCommittedInGit(dstPath);
}

function removeEmptyDirs(dir) {
  if (!fs.existsSync(dir)) {
    return;
  }
  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    if (entry.isDirectory()) {
      removeEmptyDirs(path.join(dir, entry.name));
    }
  }
  if (dir !== DST && fs.readdirSync(dir).length === 0) {
    fs.rmdirSync(dir);
  }
}

/**
 * 执行同步
 * @param {{check?: boolean, force?: boolean}} options
 * @returns {{copied: string[], removed: string[], conflicts: string[], pending: string[]}}
 */
function syncSkills(options = {}) {
  const { check = false, force = false } = options;
  const result = { copied: [], removed: [], conflicts: [], pending: [] };
  if (!fs.existsSync(SRC)) {
    return result;
  }
  const manifest = readManifest();
  const newManifest = {};
  const srcFiles = listFiles(SRC);
  const srcSet = new Set(srcFiles);

  for (const rel of srcFiles) {
    const srcPath = path.join(SRC, rel);
    const dstPath = path.join(DST, rel);
    const srcHash = hashFile(srcPath);
    if (fs.existsSync(dstPath)) {
      const dstHash = hashFile(dstPath);
      if (dstHash === srcHash) {
        newManifest[rel] = srcHash;
        continue;
      }
      // 副本与记录不符且有未提交改动：说明副本被直接修改过
      if (!force && !isSafeToReplace(manifest, rel, dstPath, dstHash)) {
        result.conflicts.push(rel);
        if (manifest[rel]) {
          newManifest[rel] = manifest[rel];
        }
        continue;
      }
    }
    if (check) {
      result.pending.push(rel);
      continue;
    }
    fs.mkdirSync(path.dirname(dstPath), { recursive: true });
    fs.copyFileSync(srcPath, dstPath);
    newManifest[rel] = srcHash;
    result.copied.push(rel);
  }

  // 副本中多出来的文件：来源已删除则跟着删；副本里手工新建或改过且未提交的不删，报告冲突
  for (const rel of listFiles(DST)) {
    if (srcSet.has(rel)) {
      continue;
    }
    const dstPath = path.join(DST, rel);
    if (!force && !isSafeToReplace(manifest, rel, dstPath, hashFile(dstPath))) {
      result.conflicts.push(rel);
      continue;
    }
    if (check) {
      result.pending.push(rel);
      continue;
    }
    fs.unlinkSync(dstPath);
    result.removed.push(rel);
  }

  if (!check) {
    removeEmptyDirs(DST);
    fs.mkdirSync(path.dirname(MANIFEST), { recursive: true });
    fs.writeFileSync(MANIFEST, JSON.stringify({
      note: '技能同步指纹记录，由 .agents/scripts/sync-skills.js 自动生成，请勿手改、勿提交',
      files: newManifest
    }, null, 2));
  }
  return result;
}

// 生成给人看的冲突提示
function conflictMessage(conflicts) {
  return '⚠️ 发现 ' + conflicts.length + ' 个技能副本文件被直接修改过（' + conflicts.slice(0, 3).join('、')
    + (conflicts.length > 3 ? ' 等' : '') + '），为防丢失未覆盖。技能请只改 .agents/skills/：'
    + '把需要保留的改动挪到 .agents/skills/ 后，运行 node .agents/scripts/sync-skills.js --force';
}

module.exports = { syncSkills, conflictMessage };

if (require.main === module) {
  const args = process.argv.slice(2);
  const check = args.includes('--check');
  const force = args.includes('--force');
  const result = syncSkills({ check, force });
  if (check) {
    const dirty = result.pending.length + result.conflicts.length;
    console.log(dirty === 0 ? '技能已同步，.claude/skills 与 .agents/skills 一致' : '待同步 ' + result.pending.length + ' 个文件');
    if (result.conflicts.length > 0) {
      console.log(conflictMessage(result.conflicts));
    }
    process.exit(dirty === 0 ? 0 : 1);
  }
  console.log('技能同步完成：复制 ' + result.copied.length + ' 个，删除 ' + result.removed.length + ' 个');
  if (result.conflicts.length > 0) {
    console.log(conflictMessage(result.conflicts));
    process.exit(1);
  }
}
