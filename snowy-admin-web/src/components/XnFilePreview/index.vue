<template>
	<a-space class="go-back-button">
		<a-button type="primary" :href="props.src" target="_blank">
			<template #icon><download-outlined /></template>
		</a-button>
		<a-button type="primary" @click="emit('goBack')">
			<template #icon><rollback-outlined /></template>
			返回
		</a-button>
	</a-space>
	<a-card :bordered="false" :body-style="{ padding: '0px' }">
		<vue-office-docx
			v-if="props.fileType.toLowerCase() === 'doc' || props.fileType.toLowerCase() === 'docx'"
			:src="props.src"
			style="height: 82vh"
			@rendered="renderedHandler"
		/>
		<vue-office-excel
			v-else-if="props.fileType.toLowerCase() === 'xls' || props.fileType.toLowerCase() === 'xlsx'"
			:src="props.src"
			style="height: 82vh"
			@rendered="renderedHandler"
			@error="errorHandler"
		/>
		<vue-office-pdf
			v-else-if="props.fileType.toLowerCase() === 'pdf'"
			:src="props.src"
			@rendered="renderedHandler"
			@error="errorHandler"
		/>
		<img
			v-else-if="
				props.fileType.toLowerCase() === 'png' ||
				props.fileType.toLowerCase() === 'jpg' ||
				props.fileType.toLowerCase() === 'gif' ||
				props.fileType.toLowerCase() === 'bmp' ||
				props.fileType.toLowerCase() === 'jpeg' ||
				props.fileType.toLowerCase() === 'ico' ||
				props.fileType.toLowerCase() === 'svg'
			"
			:src="props.src"
			style="max-width: 100%"
		/>
		<a-result v-else status="warning" title="不支持预览的文件类型" />
	</a-card>
</template>

<script setup>
	import { message } from 'ant-design-vue'
	//引入VueOfficeDocx组件
	import VueOfficeDocx from '@vue-office/docx'
	//引入相关样式
	import '@vue-office/docx/lib/index.css'
	//引入VueOfficeExcel组件
	import VueOfficeExcel from '@vue-office/excel'
	//引入相关样式
	import '@vue-office/excel/lib/index.css'
	//引入VueOfficePdf组件
	import VueOfficePdf from '@vue-office/pdf'

	const emit = defineEmits({ goBack: null })
	const props = defineProps({
		src: {
			type: String,
			default: '',
			required: true
		},
		// 文件类型
		fileType: {
			type: String,
			default: 'defaults',
			required: false
		}
	})
	// 渲染完成
	const renderedHandler = () => {}
	// 渲染失败
	const errorHandler = () => {
		message.warning('渲染失败，请尝试重新打开！')
	}
</script>

<style lang="less" scoped>
	.go-back-button {
		position: absolute;
		float: right;
		right: 10px;
		z-index: 999;
	}
</style>
