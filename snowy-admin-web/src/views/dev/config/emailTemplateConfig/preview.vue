<template>
	<a-modal title="预览" :width="700" :open="open" :destroy-on-close="true" :footer="null" @cancel="onClose">
		<div v-html="previewHtml"></div>
	</a-modal>
</template>

<script setup name="emailTemplatePreview">
	// 默认是关闭状态
	const open = ref(false)
	const previewHtml = ref('')
	const formRef = ref()
	// 打开抽屉
	const onOpen = (html) => {
		open.value = true
		previewHtml.value = html
	}
	// 关闭抽屉
	const onClose = () => {
		previewHtml.value = ''
		open.value = false
	}
	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>
