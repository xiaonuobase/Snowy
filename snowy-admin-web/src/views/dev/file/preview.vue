<template>
	<xn-file-preview :src="src" :file-type="fileType" @goBack="emit('goBack')" />
</template>

<script setup name="filePreview">
	const src = ref()
	const fileType = ref()
	const emit = defineEmits({ goBack: null })
	const onOpen = (record) => {
		src.value = record.downloadPath
		fileType.value = record.suffix
	}
	defineExpose({
		onOpen
	})
</script>
