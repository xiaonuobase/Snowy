<template>
	<div class="xn-tree-skeleton" :style="cssVars">
		<!-- 脉冲节点 -->
		<template v-if="type === 'pulse'">
			<div
				v-for="(node, i) in treeNodes"
				:key="i"
				class="sk-node pulse-node"
				:style="{ paddingLeft: node.level * 20 + 'px', animationDelay: i * 0.08 + 's' }"
			>
				<div class="pulse-dot" :style="{ animationDelay: i * 0.15 + 's' }" />
				<div class="pulse-line" :style="{ width: node.width }" />
			</div>
		</template>

		<!-- 树形骨架屏 -->
		<template v-else-if="type === 'skeleton'">
			<div
				v-for="(node, i) in treeNodes"
				:key="i"
				class="sk-node skeleton-node"
				:style="{ paddingLeft: node.level * 20 + 'px', animationDelay: i * 0.06 + 's' }"
			>
				<div class="skeleton-icon" />
				<div class="skeleton-bar" :style="{ width: node.width }" />
			</div>
		</template>

		<!-- 弹性节点 -->
		<template v-else-if="type === 'bounce'">
			<div
				v-for="(node, i) in treeNodes"
				:key="i"
				class="sk-node bounce-node"
				:style="{ paddingLeft: node.level * 20 + 'px', animationDelay: i * 0.08 + 's' }"
			>
				<div class="bounce-circle"><div class="bounce-inner" /></div>
				<div class="bounce-label" :style="{ width: node.width }" />
			</div>
		</template>

		<!-- 呼吸光效 -->
		<template v-else-if="type === 'glow'">
			<div
				v-for="(node, i) in treeNodes"
				:key="i"
				class="sk-node glow-node"
				:style="{ paddingLeft: node.level * 20 + 'px', animationDelay: i * 0.05 + 's' }"
			>
				<div class="glow-icon" :style="{ animationDelay: i * 0.15 + 's' }" />
				<div class="glow-bar" :style="{ width: node.width }" />
			</div>
		</template>

		<!-- 波纹扩散 -->
		<template v-else-if="type === 'ripple'">
			<div class="ripple-container">
				<div class="ripple-icon">
					<svg
						class="ripple-svg"
						width="20"
						height="20"
						viewBox="0 0 24 24"
						fill="none"
						stroke="currentColor"
						stroke-width="2"
					>
						<path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
						<circle cx="9" cy="7" r="4" />
						<path d="M23 21v-2a4 4 0 0 0-3-3.87" />
						<path d="M16 3.13a4 4 0 0 1 0 7.75" />
					</svg>
					<div class="ripple-ring" />
					<div class="ripple-ring ripple-ring-2" />
					<div class="ripple-ring ripple-ring-3" />
				</div>
				<div class="ripple-text">加载机构数据中...</div>
			</div>
		</template>

		<!-- 文件夹骨架 -->
		<template v-else-if="type === 'folder'">
			<div
				v-for="(node, i) in treeNodes"
				:key="i"
				class="sk-node folder-node"
				:style="{ paddingLeft: node.level * 20 + 'px', animationDelay: i * 0.07 + 's' }"
			>
				<div v-if="node.hasChild" class="folder-tri" />
				<div v-else style="width: 6px; margin-right: 8px" />
				<div class="folder-icon" />
				<div class="folder-text" :style="{ width: node.width }" />
			</div>
		</template>
	</div>
</template>

<script setup name="xnTreeSkeleton">
	import { computed } from 'vue'

	const props = defineProps({
		// 动画类型：pulse | skeleton | bounce | glow | ripple | folder
		type: {
			type: String,
			default: 'pulse',
			validator: (v) => ['pulse', 'skeleton', 'bounce', 'glow', 'ripple', 'folder'].includes(v)
		},
		// 主色调
		color: {
			type: String,
			default: '#1890ff'
		}
	})

	// 解析颜色为 RGB，用于 rgba() 透明度变体
	const cssVars = computed(() => {
		const hex = props.color.replace('#', '')
		const r = parseInt(hex.substring(0, 2), 16)
		const g = parseInt(hex.substring(2, 4), 16)
		const b = parseInt(hex.substring(4, 6), 16)
		return {
			'--sk-color': props.color,
			'--sk-rgb': `${r}, ${g}, ${b}`
		}
	})

	// 模拟树形层级结构
	const treeNodes = [
		{ level: 0, width: '70%', hasChild: true },
		{ level: 1, width: '60%', hasChild: true },
		{ level: 2, width: '50%', hasChild: false },
		{ level: 2, width: '55%', hasChild: false },
		{ level: 1, width: '65%', hasChild: true },
		{ level: 2, width: '45%', hasChild: false },
		{ level: 2, width: '58%', hasChild: false },
		{ level: 2, width: '40%', hasChild: false },
		{ level: 1, width: '62%', hasChild: false },
		{ level: 0, width: '68%', hasChild: true },
		{ level: 1, width: '52%', hasChild: false },
		{ level: 1, width: '48%', hasChild: false },
		{ level: 0, width: '72%', hasChild: false }
	]
</script>

<style scoped>
	.xn-tree-skeleton {
		width: 100%;
		padding: 12px 8px;
	}
	.sk-node {
		display: flex;
		align-items: center;
		margin-bottom: 10px;
		opacity: 0;
	}

	/* ===== pulse 脉冲节点 ===== */
	.pulse-node {
		animation: skFadeIn 0.5s ease forwards;
	}
	.pulse-dot {
		width: 8px;
		height: 8px;
		border-radius: 50%;
		background: var(--sk-color);
		margin-right: 10px;
		flex-shrink: 0;
		animation: skPulse 1.2s ease-in-out infinite;
	}
	.pulse-line {
		height: 10px;
		border-radius: 5px;
		background: rgba(var(--sk-rgb), 0.15);
	}

	/* ===== skeleton 树形骨架屏 ===== */
	.skeleton-node {
		animation: skFadeIn 0.4s ease forwards;
	}
	.skeleton-icon {
		width: 14px;
		height: 14px;
		border-radius: 3px;
		background: linear-gradient(90deg, #f0f0f0 25%, #e8e8e8 50%, #f0f0f0 75%);
		background-size: 200% 100%;
		animation: skShimmer 1.5s infinite;
		margin-right: 8px;
		flex-shrink: 0;
	}
	.skeleton-bar {
		height: 12px;
		border-radius: 6px;
		background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
		background-size: 200% 100%;
		animation: skShimmer 1.5s infinite;
	}

	/* ===== bounce 弹性节点 ===== */
	.bounce-node {
		transform: scale(0.8);
		animation: skBounceIn 0.5s cubic-bezier(0.68, -0.55, 0.265, 1.55) forwards;
	}
	.bounce-circle {
		width: 22px;
		height: 22px;
		border-radius: 50%;
		border: 2px solid var(--sk-color);
		margin-right: 10px;
		flex-shrink: 0;
		display: flex;
		align-items: center;
		justify-content: center;
	}
	.bounce-inner {
		width: 8px;
		height: 8px;
		border-radius: 50%;
		background: var(--sk-color);
		animation: skPulse 1.5s ease-in-out infinite;
	}
	.bounce-label {
		height: 11px;
		border-radius: 5px;
		background: rgba(var(--sk-rgb), 0.08);
	}

	/* ===== glow 呼吸光效 ===== */
	.glow-node {
		animation: skFadeIn 0.3s ease forwards;
	}
	.glow-icon {
		width: 16px;
		height: 16px;
		border-radius: 4px;
		background: rgba(var(--sk-rgb), 0.12);
		margin-right: 8px;
		flex-shrink: 0;
		animation: skGlow 2s ease-in-out infinite;
	}
	.glow-bar {
		height: 12px;
		border-radius: 6px;
		background: #f5f5f5;
		position: relative;
		overflow: hidden;
	}
	.glow-bar::after {
		content: '';
		position: absolute;
		top: 0;
		left: -100%;
		width: 100%;
		height: 100%;
		background: linear-gradient(90deg, transparent, rgba(var(--sk-rgb), 0.15), transparent);
		animation: skGlowSweep 2s ease-in-out infinite;
	}

	/* ===== ripple 波纹扩散 ===== */
	.ripple-container {
		width: 100%;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		min-height: 230px;
	}
	.ripple-icon {
		width: 40px;
		height: 40px;
		position: relative;
		margin-bottom: 16px;
	}
	.ripple-svg {
		position: absolute;
		top: 50%;
		left: 50%;
		transform: translate(-50%, -50%);
		z-index: 2;
		color: var(--sk-color);
	}
	.ripple-ring {
		position: absolute;
		top: 50%;
		left: 50%;
		transform: translate(-50%, -50%);
		border: 2px solid var(--sk-color);
		border-radius: 50%;
		animation: skRipple 1.5s ease-out infinite;
	}
	.ripple-ring-2 {
		animation-delay: 0.5s;
	}
	.ripple-ring-3 {
		animation-delay: 1s;
	}
	.ripple-text {
		color: var(--sk-color);
		font-size: 13px;
		animation: skBreathe 1.5s ease-in-out infinite;
	}

	/* ===== folder 文件夹骨架 ===== */
	.folder-node {
		transform: translateX(-10px);
		animation: skSlideIn 0.4s ease forwards;
	}
	.folder-tri {
		width: 0;
		height: 0;
		border-left: 6px solid #bfbfbf;
		border-top: 5px solid transparent;
		border-bottom: 5px solid transparent;
		margin-right: 8px;
		flex-shrink: 0;
	}
	.folder-icon {
		width: 16px;
		height: 13px;
		background: #faad14;
		border-radius: 2px;
		margin-right: 8px;
		flex-shrink: 0;
		position: relative;
	}
	.folder-icon::before {
		content: '';
		position: absolute;
		top: -4px;
		left: 0;
		width: 8px;
		height: 4px;
		background: #faad14;
		border-radius: 2px 2px 0 0;
	}
	.folder-text {
		height: 11px;
		border-radius: 5px;
		background: linear-gradient(
			90deg,
			rgba(var(--sk-rgb), 0.12) 0%,
			rgba(var(--sk-rgb), 0.25) 50%,
			rgba(var(--sk-rgb), 0.12) 100%
		);
		background-size: 200% 100%;
		animation: skShimmer 2s infinite;
	}

	/* ===== Keyframes ===== */
	@keyframes skFadeIn {
		to {
			opacity: 1;
		}
	}
	@keyframes skPulse {
		0%,
		100% {
			transform: scale(1);
			opacity: 0.6;
		}
		50% {
			transform: scale(1.4);
			opacity: 1;
		}
	}
	@keyframes skShimmer {
		0% {
			background-position: 200% 0;
		}
		100% {
			background-position: -200% 0;
		}
	}
	@keyframes skBounceIn {
		to {
			opacity: 1;
			transform: scale(1);
		}
	}
	@keyframes skGlow {
		0%,
		100% {
			background: rgba(var(--sk-rgb), 0.12);
			box-shadow: 0 0 0 rgba(var(--sk-rgb), 0);
		}
		50% {
			background: rgba(var(--sk-rgb), 0.25);
			box-shadow: 0 0 8px rgba(var(--sk-rgb), 0.3);
		}
	}
	@keyframes skGlowSweep {
		0% {
			left: -100%;
		}
		50% {
			left: 100%;
		}
		100% {
			left: 100%;
		}
	}
	@keyframes skRipple {
		0% {
			width: 10px;
			height: 10px;
			opacity: 1;
		}
		100% {
			width: 60px;
			height: 60px;
			opacity: 0;
		}
	}
	@keyframes skBreathe {
		0%,
		100% {
			opacity: 0.5;
		}
		50% {
			opacity: 1;
		}
	}
	@keyframes skSlideIn {
		to {
			opacity: 1;
			transform: translateX(0);
		}
	}
</style>
