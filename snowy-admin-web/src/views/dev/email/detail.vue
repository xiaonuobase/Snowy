<template>
	<xn-form-container title="详情" :width="700" :visible="visible" :destroy-on-close="true" @close="onClose">
		<a-descriptions :column="1" size="middle" bordered class="mb-2">
			<a-descriptions-item label="邮件主题">{{ formData.subject }}</a-descriptions-item>
			<a-descriptions-item label="邮件引擎">{{
				$TOOL.dictTypeData('FILE_ENGINE', formData.engine)
			}}</a-descriptions-item>
			<a-descriptions-item label="发送账户">{{ formData.sendAccount }}</a-descriptions-item>
			<a-descriptions-item label="发送时间">{{ formData.createTime }}</a-descriptions-item>
			<a-descriptions-item label="发送人" v-if="formData.sendUser">{{ formData.sendUser }}</a-descriptions-item>
			<a-descriptions-item label="接收账户">{{ formData.receiveAccounts }}</a-descriptions-item>
		</a-descriptions>

		<a-form ref="formRef" :model="formData" layout="vertical">
			<a-form-item label="正文：" name="content">
				<span>{{ formData.content }}</span>
			</a-form-item>
			<a-row :gutter="10" v-if="formData.templateName || formData.templateParam">
				<a-col :span="12">
					<a-form-item label="模板名称：" name="templateName">
						<span>{{ formData.templateName }}</span>
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="模板参数：" name="templateParam">
						<span>{{ formData.templateParam }}</span>
					</a-form-item>
				</a-col>
			</a-row>
			<a-form-item label="回执信息：" name="receiptInfo">
				<span>{{ formData.receiptInfo }}</span>
			</a-form-item>
		</a-form>
	</xn-form-container>
</template>

<script setup name="emailDetail">
	import emailApi from '@/api/dev/emailApi'

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
		emailApi.emailDetail(param).then((data) => {
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
