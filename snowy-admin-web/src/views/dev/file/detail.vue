<template>
	<xn-form-container title="详情" :width="700" :visible="visible" :destroy-on-close="true" @close="onClose">
		<a-descriptions :column="1" size="middle" bordered class="mb-2">
			<a-descriptions-item label="名称">{{ formData.name }}</a-descriptions-item>
			<a-descriptions-item label="文件引擎">{{
				$TOOL.dictTypeData('FILE_ENGINE', formData.engine)
			}}</a-descriptions-item>
			<a-descriptions-item label="储存桶">{{ formData.bucket }}</a-descriptions-item>
			<a-descriptions-item label="上传时间">{{ formData.createTime }}</a-descriptions-item>
			<a-descriptions-item label="大小">{{ formData.sizeInfo }}</a-descriptions-item>
		</a-descriptions>
		<a-form ref="formRef" :model="formData" layout="vertical">
			<a-form-item label="存储路径：" name="storagePath">
				<span>{{ formData.storagePath }}</span>
			</a-form-item>
			<a-form-item label="下载链接：" name="downloadPath">
				<span>{{ formData.downloadPath }}</span>
			</a-form-item>
		</a-form>
	</xn-form-container>
</template>

<script setup name="fileDetail">
	import fileApi from '@/api/dev/fileApi'

	// 默认是关闭状态
	const visible = ref(false)
	const formRef = ref()
	// 表单数据
	const formData = ref({})
	// 打开抽屉
	const onOpen = (record) => {
		visible.value = true
		getFileDetail(record)
	}
	// 获取站内信列表
	const getFileDetail = (record) => {
		const param = {
			id: record.id
		}
		fileApi.fileDetail(param).then((data) => {
			Object.assign(record, data)
			formData.value = record
		})
	}
	// 关闭抽屉
	const onClose = () => {
		formData.value = {}
		visible.value = false
	}
	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>
