<template>
	<div class="xn-panel" :class="{ 'xn-panel-bordered': bordered }" :style="wrapperStyle">
		<!-- 头部：支持标题与右侧扩展区 -->
		<div v-if="hasHeader" class="xn-panel-header" :style="headerStyle">
			<div class="xn-panel-title">
				<slot name="title">{{ title }}</slot>
			</div>
			<div class="xn-panel-extra">
				<slot name="extra"></slot>
			</div>
		</div>
		<!-- 主体：自动占满剩余空间，超出时内部滚动且隐藏滚动条 -->
		<div class="xn-panel-body" :style="bodyStyle">
			<slot></slot>
		</div>
		<!-- 底部：常用于操作区或信息展示 -->
		<div v-if="hasFooter" class="xn-panel-footer" :style="footerStyle">
			<slot name="footer"></slot>
		</div>
	</div>
</template>

<script setup name="xnPanel">
	import { computed, useSlots } from 'vue'
	import { globalStore } from '@/store'

	// XnPanel 面板容器组件
	const props = defineProps({
		// 标题文本（也可用 title 插槽替代）
		title: { type: String, default: '' },
		// 是否显示外边框
		bordered: { type: Boolean, default: false },
		// 主体内边距（Number 自动转 px，String 直接使用）
		padding: { type: [Number, String], default: 24 },
		// 头部内边距
		headerPadding: { type: [Number, String], default: 0 },
		// 底部内边距
		footerPadding: { type: [Number, String], default: 10 },
		// 是否显示阴影（默认关闭）
		shadow: { type: Boolean, default: true },
		// 圆角（支持 Number/px）
		radius: { type: [Number, String], default: 'system' },
		// 头部分割线开关
		headerDivider: { type: Boolean, default: true },
		// 底部分割线开关
		footerDivider: { type: Boolean, default: true },
		// 容器外部底部留白（避免贴底）
		bottomGap: { type: [Number, String], default: 10 }
	})

	const slots = useSlots()
	const store = globalStore()
	const systemRadius = computed(() => (store.roundedCornerStyleOpen ? 8 : 2))
	// 是否渲染头部：有 title 文本或存在 title/extra 插槽
	const hasHeader = computed(() => !!props.title || !!slots?.title || !!slots?.extra)
	// 是否渲染底部：存在 footer 插槽
	const hasFooter = computed(() => !!slots?.footer)

	// 将数字统一转为 px 字符串
	const toPx = (val) => (typeof val === 'number' ? `${val}px` : val)

	const headerStyle = computed(() => ({
		padding: toPx(props.headerPadding),
		borderBottom: props.headerDivider ? '1px solid var(--background-border)' : 'none'
	}))

	const footerStyle = computed(() => ({
		padding: toPx(props.footerPadding),
		borderTop: props.footerDivider ? '1px solid var(--border-color-split)' : 'none',
		background: 'var(--snowy-background-color)'
	}))

	const bodyStyle = computed(() => ({
		// 主体区域 padding 统一受控
		padding: toPx(props.padding)
	}))

	// 容器样式：统一应用圆角、阴影，并在外部保留 bottomGap 间距
	const wrapperStyle = computed(() => {
		const gap = toPx(props.bottomGap)
		return {
			borderRadius: toPx(props.radius === 'system' ? systemRadius.value : props.radius),
			boxShadow: props.shadow ? 'var(--card-shadow-soft, 0 1px 6px rgba(0, 0, 0, 0.06))' : 'none',
			// 在容器外部留白，避免卡片贴到页面底部
			marginBottom: gap,
			// 同步减少容器高度，确保不超出父容器
			height: `calc(100% - ${gap})`
		}
	})
</script>

<style scoped>
	.xn-panel {
		display: flex;
		flex-direction: column;
		width: 100%;
		height: 100%;
		/* 背景颜色跟随系统暗黑主题变量 */
		background: var(--snowy-background-color);
		overflow: hidden;
	}

	.xn-panel-bordered {
		/* 外边框颜色跟随系统变量 */
		border: 1px solid var(--border-color-split);
	}

	.xn-panel-header {
		display: flex;
		align-items: center;
		justify-content: space-between;
		min-height: 48px;
		padding: 0;
		/* 分割线受 headerDivider 控制，默认展示 */
		border-bottom: 1px solid var(--background-border);
	}

	.xn-panel-title {
		padding: 0 24px;
		color: var(--heading-color);
		font-size: 16px;
		font-weight: 500;
	}

	.xn-panel-extra {
		padding: 0 24px;
		display: flex;
		align-items: center;
		gap: 8px;
	}

	.xn-panel-body {
		flex: 1;
		overflow: auto;
		/* 主体背景：更浅的背景层 */
		background: var(--snowy-background-color);
		scrollbar-width: none;
		-ms-overflow-style: none;
	}

	.xn-panel-body::-webkit-scrollbar {
		/* 隐藏滚动条但保留滚动行为 */
		display: none;
	}

	.xn-panel-footer {
		/* 分割线受 footerDivider 控制，默认展示 */
		border-top: 1px solid var(--border-color-split);
		display: flex;
		justify-content: flex-end;
		align-items: center;
		background: var(--snowy-background-color);
	}
</style>
