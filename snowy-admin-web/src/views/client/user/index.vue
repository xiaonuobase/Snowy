<template>
	<div>
		<a-card :bordered="false" style="margin-bottom: 10px">
			<a-form ref="searchFormRef" name="advanced_search" class="ant-advanced-search-form" :model="searchFormState">
				<a-row :gutter="24">
					<a-col :span="8">
						<a-form-item name="searchKey" label="用户关键词">
							<a-input v-model:value="searchFormState.searchKey" placeholder="请输入用户关键词" />
						</a-form-item>
					</a-col>
					<a-col :span="8">
						<a-button type="primary" @click="tableRef.refresh(true)">
							<template #icon><SearchOutlined /></template>
							查询
						</a-button>
						<a-button class="snowy-button-left" @click="reset">
							<template #icon><redo-outlined /></template>
							重置
						</a-button>
					</a-col>
				</a-row>
			</a-form>
		</a-card>
		<a-card :bordered="false">
			<s-table
				ref="tableRef"
				:columns="columns"
				:data="loadData"
				:expand-row-by-click="true"
				bordered
				:alert="options.alert.show"
				:tool-config="toolConfig"
				:row-key="(record) => record.id"
				:row-selection="options.rowSelection"
			>
				<template #operator class="table-operator">
					<a-space>
						<a-button type="primary" @click="clientUserFormRef.onOpen()">
							<template #icon><plus-outlined /></template>
							<span>新增用户</span>
						</a-button>
						<xn-batch-button
							buttonName="批量删除"
							icon="DeleteOutlined"
							buttonDanger
							:selectedRowKeys="selectedRowKeys"
							@batchCallBack="deleteBatchUser"
						/>
					</a-space>
				</template>
				<template #bodyCell="{ column, record }">
					<template v-if="column.dataIndex === 'avatar'">
						<a-avatar :src="record.avatar" style="margin-bottom: -5px; margin-top: -5px" />
					</template>
					<template v-if="column.dataIndex === 'gender'">
						{{ $TOOL.dictTypeData('GENDER', record.gender) }}
					</template>
					<template v-if="column.dataIndex === 'userStatus'">
						{{ $TOOL.dictTypeData('COMMON_STATUS', record.userStatus) }}
					</template>
					<template v-if="column.dataIndex === 'action'">
						<a @click="clientUserFormRef.onOpen(record)">编辑</a>
						<a-divider type="vertical" />
						<a-popconfirm title="确定要删除此用户吗" @confirm="removeUser(record)">
							<a-button type="link" danger size="small"> 删除 </a-button>
						</a-popconfirm>
					</template>
				</template>
			</s-table>
		</a-card>
		<client-user-form ref="clientUserFormRef" @successful="tableRef.refresh()" />
	</div>
</template>
<script setup name="clientUser">
	import clientUserApi from '@/api/client/clientUserApi'
	import ClientUserForm from './form.vue'

	const columns = [
		{
			title: '头像',
			dataIndex: 'avatar',
			align: 'center',
			width: '80px'
		},
		{
			title: '账号',
			dataIndex: 'account',
			ellipsis: true
		},
		{
			title: '姓名',
			dataIndex: 'name'
		},
		{
			title: '性别',
			dataIndex: 'gender',
			width: 100
		},
		{
			title: '手机',
			dataIndex: 'phone',
			ellipsis: true
		},
		{
			title: '状态',
			dataIndex: 'userStatus',
			width: 100
		},
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: '220px'
		}
	]
	const toolConfig = { refresh: true, height: true, columnSetting: true }
	const searchFormRef = ref()
	const searchFormState = ref({})
	const tableRef = ref(null)
	const selectedRowKeys = ref([])
	const clientUserFormRef = ref(null)
	// 表格查询 返回 Promise 对象
	const loadData = (parameter) => {
		return clientUserApi.userPage(Object.assign(parameter, searchFormState.value)).then((res) => {
			return res
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	// 列表选择配置
	const options = {
		alert: {
			show: false,
			clear: () => {
				selectedRowKeys.value = ref([])
			}
		},
		rowSelection: {
			onChange: (selectedRowKey, selectedRows) => {
				selectedRowKeys.value = selectedRowKey
			}
		}
	}
	// 删除用户
	const removeUser = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		clientUserApi.userDelete(params).then(() => {
			tableRef.value.refresh()
		})
	}
	// 批量删除
	const deleteBatchUser = (params) => {
		clientUserApi.userDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
	// 重置用户密码
	const resetPassword = (record) => {
		clientUserApi.userResetPassword(record).then(() => {})
	}
</script>

<style lang="less" scoped>
	.ant-form-item {
		margin-bottom: 0 !important;
	}
	.snowy-table-avatar {
		margin-top: -10px;
		margin-bottom: -10px;
	}
	.snowy-button-left {
		margin-left: 8px;
	}
</style>
