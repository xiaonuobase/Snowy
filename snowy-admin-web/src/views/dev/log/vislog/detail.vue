<template>
	<xn-form-container title="详情" :width="700" :visible="visible" :destroy-on-close="true" @close="onClose">
		<a-descriptions :column="1" size="middle" bordered class="mb-2">
			<a-descriptions-item label="名称">{{ formData.name }}</a-descriptions-item>
			<a-descriptions-item label="IP地址">{{ formData.opIp }}</a-descriptions-item>
			<a-descriptions-item label="地址">{{ formData.opAddress }}</a-descriptions-item>
			<a-descriptions-item label="浏览器">{{ formData.opBrowser }}</a-descriptions-item>
			<a-descriptions-item label="设备">{{ formData.opOs }}</a-descriptions-item>
			<a-descriptions-item label="时间">{{ formData.opTime }}</a-descriptions-item>
			<a-descriptions-item label="用户">{{ formData.opUser }}</a-descriptions-item>
		</a-descriptions>
	</xn-form-container>
</template>

<script setup name="vislogDetail">
	// 默认是关闭状态
	const visible = ref(false)
	// 表单数据
	const formData = ref({})
	const tableRef = ref()
	// 打开抽屉
	const onOpen = (record) => {
		visible.value = true
		formData.value = record
	}
	// 关闭抽屉
	const onClose = () => {
		visible.value = false
	}
	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>
