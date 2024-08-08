<template>
	<xn-form-container title="详情" :width="700" :visible="visible" :destroy-on-close="true" @close="onClose">
		<a-descriptions :column="1" size="middle" bordered class="mb-2">
			<a-descriptions-item label="名称">{{ formData.name }}</a-descriptions-item>
			<a-descriptions-item label="请求IP">{{ formData.opIp }}</a-descriptions-item>
			<a-descriptions-item label="IP来源">{{ formData.opAddress }}</a-descriptions-item>
			<a-descriptions-item label="请求地址">{{ formData.reqUrl }}</a-descriptions-item>
			<a-descriptions-item label="操作类">{{ formData.className }}</a-descriptions-item>
			<a-descriptions-item label="操作方法">{{ formData.methodName }}</a-descriptions-item>
			<a-descriptions-item label="操作时间">{{ formData.opTime }}</a-descriptions-item>
			<a-descriptions-item label="操作人">{{ formData.opUser }}</a-descriptions-item>
		</a-descriptions>

		<a-space direction="vertical" class="mb-2 xn-wd" >
			请求参数：
			<XnHighlightjs language="JSON" :code="paramJson"></XnHighlightjs>
		</a-space>
		<a-space direction="vertical" class="mb-2 xn-wd" >
			返回结果：
			<XnHighlightjs autodetect :code="resultJson"></XnHighlightjs>
		</a-space>
	</xn-form-container>
</template>

<script setup name="oplogDetail">
	// 默认是关闭状态
	const visible = ref(false)
	const formData = ref({})
	const tableRef = ref()
	const paramJson = ref()
	const resultJson = ref()
	// 打开抽屉
	const onOpen = (record) => {
		visible.value = true
		formData.value = record
		if (record.paramJson) {
			const code = JSON.parse(record.paramJson)
			paramJson.value = JSON.stringify(code, undefined, 2)
		} else {
			paramJson.value = '无'
		}
		if (record.category === 'EXCEPTION') {
			resultJson.value = record.exeMessage
		} else {
			if (record.resultJson) {
				const code = JSON.parse(record.resultJson)
				resultJson.value = JSON.stringify(code, undefined, 2)
			} else {
				resultJson.value = '无'
			}
		}
	}
	// 关闭抽屉
	const onClose = () => {
		paramJson.value = ''
		resultJson.value = ''
		visible.value = false
	}
	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>
