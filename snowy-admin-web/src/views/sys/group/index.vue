<template>
	<a-card :bordered="false" class="xn-mb10">
		<a-form ref="searchFormRef" :model="searchFormState">
			<a-row :gutter="10">
				<a-col :xs="24" :sm="8" :md="8" :lg="8" :xl="8">
					<a-form-item label="关键词" name="name">
						<a-input v-model:value="searchFormState.name" placeholder="请输入名称关键词"/>
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="16" :md="16" :lg="16" :xl="16">
					<a-form-item>
						<a-space>
							<a-button type="primary" @click="tableRef.refresh(true)">
								<template #icon>
									<SearchOutlined/>
								</template>
								查询
							</a-button>
							<a-button @click="reset">
								<template #icon>
									<redo-outlined/>
								</template>
								重置
							</a-button>
						</a-space>
					</a-form-item>
				</a-col>
			</a-row>
		</a-form>
	</a-card>
	<a-card :bordered="false">
		<s-table
			ref="tableRef"
			:columns="columns"
			:data="loadData"
			:alert="options.alert.show"
			bordered
			:row-key="(record) => record.id"
			:tool-config="toolConfig"
			:row-selection="options.rowSelection"
			:scroll="{ x: 'max-content' }"
		>
			<template #operator class="table-operator">
				<a-space>
					<a-button type="primary" @click="formRef.onOpen()">
						<template #icon>
							<plus-outlined/>
						</template>
						新增
					</a-button>
					<xn-batch-button
						buttonName="批量删除"
						icon="DeleteOutlined"
						buttonDanger
						:selectedRowKeys="selectedRowKeys"
						@batchCallBack="deleteBatchSysGroup"
					/>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="formRef.onOpen(record)">编辑</a>
						<a-divider type="vertical"/>
						<a @click="openGroupUserSelector(record)">授权用户</a>
						<a-divider type="vertical"/>
						<a-popconfirm title="确定要删除吗？" @confirm="deleteSysGroup(record)">
							<a-button type="link" danger size="small">删除</a-button>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
		</s-table>
	</a-card>
	<Form ref="formRef" @successful="tableRef.refresh()"/>
	<xn-user-selector
		ref="userSelectorRef"
		:org-tree-api="selectorApiFunction.orgTreeApi"
		:user-page-api="selectorApiFunction.userPageApi"
		data-type="object"
		:user-show="false"
		@onBack="userCallBack"
	/>
</template>

<script setup name="sysGroupIndex">
import {cloneDeep} from 'lodash-es'
import Form from './form.vue'
import sysGroupApi from '@/api/sys/groupApi'

const searchFormState = ref({})
const searchFormRef = ref()
const tableRef = ref()
const formRef = ref()
const toolConfig = {refresh: true, height: true, columnSetting: true, striped: false}
const recordCacheData = ref()
const userSelectorRef = ref()
const columns = [
	{
		title: '名称',
		dataIndex: 'name'
	},
	{
		title: '备注',
		dataIndex: 'remark'
	},
	{
		title: '排序码',
		dataIndex: 'sortCode'
	},
	{
		title: '创建时间',
		dataIndex: 'createTime'
	},
	{
		title: '操作',
		dataIndex: 'action',
		align: 'center',
		fixed: 'right'
	}
]
const selectedRowKeys = ref([])
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
const loadData = (parameter) => {
	const searchFormParam = cloneDeep(searchFormState.value)
	return sysGroupApi.groupPage(Object.assign(parameter, searchFormParam)).then((data) => {
		return data
	})
}
// 重置
const reset = () => {
	searchFormRef.value.resetFields()
	tableRef.value.refresh(true)
}
// 删除
const deleteSysGroup = (record) => {
	let params = [
		{
			id: record.id
		}
	]
	sysGroupApi.groupDelete(params).then(() => {
		tableRef.value.refresh(true)
	})
}
// 批量删除
const deleteBatchSysGroup = (params) => {
	sysGroupApi.groupDelete(params).then(() => {
		tableRef.value.clearRefreshSelected()
	})
}
// 打开用户选择器
const openGroupUserSelector = (record) => {
	// 打开人员选择器的时候，缓存一个记录数据
	recordCacheData.value = record
	// 查询接口，查到这个角色是多少个用户都有它
	const param = {
		id: record.id
	}
	sysGroupApi.groupOwnUser(param).then((data) => {
		userSelectorRef.value.showUserPlusModal(data)
	})
}
// 人员选择器回调
const userCallBack = (value) => {
	const param = {
		id: recordCacheData.value.id,
		grantInfoList: value
	}
	sysGroupApi.groupGrantUser(param).then(() => {
	})
}
// 传递设计器需要的API
const selectorApiFunction = {
	orgTreeApi: (param) => {
		return sysGroupApi.groupOrgTreeSelector(param).then((data) => {
			return Promise.resolve(data)
		})
	},
	userPageApi: (param) => {
		return sysGroupApi.groupUserSelector(param).then((data) => {
			return Promise.resolve(data)
		})
	}
}
</script>
