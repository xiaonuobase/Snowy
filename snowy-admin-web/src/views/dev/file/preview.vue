<template>
	<xn-file-preview :data="data" :file-type="fileType" :file-name="fileName" @goBack="onBack" />
</template>

<script setup name="filePreview">
	// 已下载的文件内容
	const data = shallowRef(null)
	const fileType = ref('')
	const fileName = ref('')
	const emit = defineEmits({ goBack: null })
	// 打开预览，传入列表记录与已下载的文件内容
	const onOpen = (record, blob) => {
		data.value = blob
		fileType.value = record.suffix
		fileName.value = record.name
	}
	// 返回时清空文件内容，释放内存
	const onBack = () => {
		data.value = null
		emit('goBack')
	}
	defineExpose({
		onOpen
	})
</script>
