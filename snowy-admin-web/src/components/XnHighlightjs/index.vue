<template>
	<!-- 本组件这兄弟写的很好 请参照：https://blog.csdn.net/weixin_41897680/article/details/124925222-->
	<div class="hljs-container" :codetype="props.language">
		<highlightjs :language="props.language" :autodetect="!props.language" :code="props.code"></highlightjs>
	</div>
</template>

<script setup name="XnHighlightjs">
	/*import 'highlight.js/styles/atom-one-dark.css'
	import 'highlight.js/lib/common'
	import hljsVuePlugin from '@highlightjs/vue-plugin'*/

	const props = defineProps({
		language: {
			type: String,
			default: () => undefined
		},
		code: {
			type: String,
			default: () => '无'
		}
	})
</script>

<style scoped lang="less">
	/* 语法高亮 */
	/*.hljs-container {
		position: relative;
		display: block;
		padding: 30px 5px 2px;
		overflow-x: hidden;
		line-height: 20px;
		text-align: left;
		background: #21252b;
		box-shadow: 0 10px 30px 0 rgb(0 0 0 / 40%);
	}*/
	/** 3个点 */
	/*.hljs-container::before {
		position: absolute;
		top: 10px;
		left: 15px;
		width: 12px;
		height: 12px;
		overflow: visible;
		font-weight: 700;
		font-size: 16px;
		line-height: 12px;
		white-space: nowrap;
		text-indent: 75px;
		background-color: #fc625d;
		border-radius: 16px;
		box-shadow: 20px 0 #fdbc40, 40px 0 #35cd4b;
		content: attr(codetype);
	}*/

	/** 滚动条 */
	:deep(.hljs,.hljs-container) {
		max-height: 300px!important;
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
</style>
