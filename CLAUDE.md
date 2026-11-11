# CLAUDE.md
@AGENTS.md

## Claude Code 专属补充

上面引入的 `AGENTS.md` 是全部 AI 工具共用的工程宪法，以下仅补充 Claude Code 的差异：

- **技能目录**：Claude Code 只认 `.claude/skills/`，它是 `.agents/skills/` 的自动副本（`node .agents/scripts/sync-skills.js` 生成，会话结束时钩子也会自动同步）。**不要直接改副本**，修改技能只改 `.agents/skills/`
- **激活技能**：用 Skill 工具激活 `AGENTS.md` 技能索引中匹配的知识技能
- **流程技能**：用 `/名称` 调用，如 `/dev`、`/crud`、`/check`、`/start`
- **子代理**：`.claude/agents/` —— code-reviewer（只读审查）、project-manager（项目管理文档）
- **钩子**：注册在 `.claude/settings.json`，脚本在 `.agents/hooks/`（与 Codex 共用）
