<template>
	<div style="position: relative">
		<a-space class="go-back-button">
			<a-button :href="props.src" size="small" target="_blank">
				<template #icon><download-outlined /></template>
			</a-button>
			<a-button type="primary" size="small" @click="emit('goBack')">
				<template #icon><rollback-outlined /></template>
				返回
			</a-button>
		</a-space>
		<a-card :bordered="false" :body-style="{ padding: '0px' }">
			<a-spin :spinning="loading">
				<vue-office-docx
					v-if="fileType === 'doc' || fileType === 'docx'"
					:src="props.src"
					class="xn-ht82"
					@rendered="renderedHandler"
				/>
				<vue-office-excel
					v-else-if="fileType === 'xls' || fileType === 'xlsx'"
					:src="props.src"
					class="xn-ht82"
					@rendered="renderedHandler"
					@error="errorHandler"
				/>
				<vue-office-pdf
					v-else-if="fileType === 'pdf'"
					:src="props.src"
					@rendered="renderedHandler"
					@error="errorHandler"
				/>
				<img
					v-else-if="
						fileType === 'png' ||
						fileType === 'jpg' ||
						fileType === 'gif' ||
						fileType === 'bmp' ||
						fileType === 'jpeg' ||
						fileType === 'ico' ||
						fileType === 'svg'
					"
					:src="props.src"
					class="xn-mwh"
				/>
				<a-result v-else status="warning" title="不支持预览的文件类型" />
			</a-spin>
		</a-card>
	</div>
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

	const loading = ref(false)
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
	const fileType = ref()
	watch(
		() => props.src,
		(newVal) => {
			if (newVal) {
				fileType.value = props.fileType.toLowerCase()
			}
		},
		{ immediate: true, deep: true }
	)
	watch(
		() => props.src,
		(newVal) => {
			if (newVal) {
				if (
					fileType.value === 'doc' ||
					fileType.value === 'docx' ||
					fileType.value === 'xls' ||
					fileType.value === 'xlsx' ||
					fileType.value === 'pdf'
				) {
					loading.value = true
				}
			}
		},
		{ immediate: true, deep: true }
	)
	// 渲染完成
	const renderedHandler = () => {
		loading.value = false
	}
	// 渲染失败
	const errorHandler = () => {
		message.warning('渲染失败，请尝试重新打开！')
	}
</script>

<style lang="less" scoped>
	.xn-mwh {
		max-width: 100%;
	}
	.xn-ht82 {
		height: 82vh;
	}
	.go-back-button {
		position: absolute;
		float: right;
		right: 0;
		z-index: 999;
	}
</style>
