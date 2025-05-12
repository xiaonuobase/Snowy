<template>
	<xn-form-container title="详情" :width="800" :visible="visible" :destroy-on-close="true" @close="onClose">
		<a-descriptions :column="1" size="middle" bordered class="mb-2">
			<a-descriptions-item label="消息标题">{{ formData.title }}</a-descriptions-item>
			<a-descriptions-item label="消息引擎">
				{{ $TOOL.dictTypeData('PUSH_ENGINE', formData.engine) }}
			</a-descriptions-item>
			<a-descriptions-item label="消息类别">{{ formData.type }}</a-descriptions-item>
			<a-descriptions-item label="发送人" v-if="formData.createUserName">
				{{ formData.createUserName }}
			</a-descriptions-item>
			<a-descriptions-item label="发送时间">{{ formData.createTime }}</a-descriptions-item>
		</a-descriptions>
		<a-space direction="vertical" class="mb-2 xn-wd">
			详细信息：
			<xn-md-preview ref="mdPreviewRef" id="mdPreviewRef" :text="formData.content" class="md-preview" />
		</a-space>
		<a-space direction="vertical" class="mb-2 xn-wd">
			回执信息：
			<XnHighlightjs language="JSON" :code="receiptInfo"></XnHighlightjs>
		</a-space>
	</xn-form-container>
</template>

<script setup name="smsDetail">
	import pushApi from '@/api/dev/pushApi'
	import { XnMdPreview } from '@/components/XnMdEditor/mdEditor'
	// 默认是关闭状态
	const visible = ref(false)
	const receiptInfo = ref()
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
		pushApi.pushDetail(param).then((data) => {
			Object.assign(record, data)
			formData.value = record
			if (record.receiptInfo) {
				const jsonStr = JSON.parse(record.receiptInfo)
				receiptInfo.value = JSON.stringify(jsonStr, undefined, 2)
			}
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
<style lang="less" scoped>
	:deep(.github-markdown-body) {
		padding: 16px 0 !important;
	}
</style>
