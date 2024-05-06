<template>
	<xn-form-container title="详情" :width="700" :visible="visible" :destroy-on-close="true" @close="onClose">
		<a-descriptions :column="1" size="middle" bordered class="mb-2">
			<a-descriptions-item label="主题">{{ formData.subject }}</a-descriptions-item>
			<a-descriptions-item label="发送时间">{{ formData.createTime }}</a-descriptions-item>
		</a-descriptions>
		<a-form ref="formRef" :model="formData" layout="vertical">
			<a-form-item label="内容：" name="content">
				<span>{{ formData.content }}</span>
			</a-form-item>
			<a-form-item label="查收情况：" name="receiveInfoList">
				<s-table
					ref="tableRef"
					:columns="columns"
					:data="loadData"
					:alert="false"
					:showPagination="false"
					bordered
					:row-key="(record) => record.id"
				>
					<template #bodyCell="{ column, record }">
						<template v-if="column.dataIndex === 'read'">
							<span v-if="record.read" class="xn-color-d9d9d9">已读</span>
							<span v-else class="xn-color-ff4d4f">未读</span>
						</template>
					</template>
				</s-table>
			</a-form-item>
		</a-form>
	</xn-form-container>
</template>

<script setup name="messageDetail">
	import userCenterApi from '@/api/sys/userCenterApi'
	const emits = defineEmits(['refresh'])
	const receiveInfoList = ref([])

	// 默认是关闭状态
	const visible = ref(false)
	const formRef = ref()
	// 表单数据
	const formData = ref({})
	const tableRef = ref()
	const columns = [
		{
			title: '姓名',
			dataIndex: 'receiveUserName'
		},
		{
			title: '是否已读',
			dataIndex: 'read',
			width: 120
		}
	]
	// 打开抽屉
	const onOpen = (record) => {
		visible.value = true
		getMessageList(record)
	}
	// 获取站内信列表
	const getMessageList = (record) => {
		const param = {
			id: record.id
		}
		userCenterApi.userLoginUnreadMessageDetail(param).then((data) => {
			Object.assign(record, data)
			formData.value = record
			receiveInfoList.value = data.receiveInfoList
			tableRef.value.refresh(true)
		})
	}
	const loadData = () => {
		return new Promise((resolve) => {
			resolve(receiveInfoList.value)
		})
	}
	// 关闭抽屉
	const onClose = () => {
		receiveInfoList.value = []
		visible.value = false
		emits('refresh')
	}
	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>
