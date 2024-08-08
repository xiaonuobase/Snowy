<template>
	<!-- 本组件这兄弟写的很好 请参照：https://blog.csdn.net/weixin_41897680/article/details/124925222-->
	<div class="hljs-container" :codetype="props.language">
		<a-button v-if="props.copy" size="small" type="primary" class="hljs-copy" @click="codeCopy">
			<CopyOutlined />
			拷贝
		</a-button>
		<highlightjs :language="props.language" :autodetect="!props.language" :code="props.code" />
	</div>
</template>

<script setup name="XnHighlightjs">
	import { message } from 'ant-design-vue'
	const props = defineProps({
		language: {
			type: String,
			default: () => undefined
		},
		code: {
			type: String,
			default: () => '无'
		},
		copy: {
			type: Boolean,
			default: () => false
		}
	})
	const codeCopy = () => {
		copyTextToClipboard(props.code).then(() => {
			message.success('拷贝成功')
		})
	}
	const copyTextToClipboard = async (text) => {
		try {
			await navigator.clipboard.writeText(text)
		} catch (err) {
			message.warning('拷贝失败')
		}
	}
</script>

<style scoped lang="less">
	.hljs-container {
		position: relative;
	}
	/** 滚动条 */
	:deep(.hljs, .hljs-container) {
		max-height: 300px !important;
		overflow-x: auto;
	}

	:deep(.hljs::-webkit-scrollbar) {
		width: 12px !important;
		height: 12px !important;
	}

	:deep(.hljs::-webkit-scrollbar-thumb) {
		height: 30px !important;
		background: #d1d8e6;
		background-clip: content-box;
		border: 2px solid transparent;
		border-radius: 19px;
		opacity: 0.8;
	}

	:deep(.hljs::-webkit-scrollbar-thumb:hover) {
		background: #a5b3cf;
		background-clip: content-box;
		border: 2px solid transparent;
	}

	:deep(.hljs::-webkit-scrollbar-track-piece) {
		width: 30px;
		height: 30px;
		background: #333;
	}

	::-webkit-scrollbar-button {
		display: none;
	}
	/** 复制样式 */
	.hljs-copy {
		float: right;
		top: 6px;
		right: 6px;
		position: absolute;
		z-index: 9;
	}
</style>
