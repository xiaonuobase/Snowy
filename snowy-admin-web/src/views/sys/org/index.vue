<template>
	<a-row :gutter="10">
		<a-col :xs="0" :sm="0" :md="0" :lg="4" :xl="4">
			<a-card :bordered="false" :loading="cardLoading" class="left-tree-container">
				<a-tree
					v-if="treeData.length > 0"
					v-model:expandedKeys="defaultExpandedKeys"
					:tree-data="treeData"
					:field-names="treeFieldNames"
					@select="treeSelect"
				/>
				<a-empty v-else :image="Empty.PRESENTED_IMAGE_SIMPLE"/>
			</a-card>
		</a-col>
		<a-col :xs="24" :sm="24" :md="24" :lg="20" :xl="20">
			<a-card :bordered="false" class="xn-mb10">
				<a-form ref="searchFormRef" :model="searchFormState">
					<a-row :gutter="10">
						<a-col :xs="24" :sm="8" :md="8" :lg="0" :xl="0">
							<a-form-item label="上级组织：" name="parentId">
								<a-tree-select
									v-model:value="searchFormState.parentId"
									class="xn-wd"
									:dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
									placeholder="请选择上级组织"
									allow-clear
									:tree-data="treeData"
									:field-names="{
											children: 'children',
											label: 'name',
											value: 'id'
										}"
									selectable="false"
									tree-line
								/>
							</a-form-item>
						</a-col>
						<a-col :xs="24" :sm="8" :md="8" :lg="8" :xl="8">
							<a-form-item name="searchKey" label="名称">
								<a-input v-model:value="searchFormState.searchKey" placeholder="请输入名称关键词"/>
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
					:expand-row-by-click="true"
					:alert="options.alert.show"
					bordered
					:row-key="(record) => record.id"
					:row-selection="options.rowSelection"
					:scroll="{ x: 'max-content' }"
				>
					<template #operator class="table-operator">
						<a-space>
							<a-button type="primary" @click="formRef.onOpen(undefined, searchFormState.parentId)">
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
								@batchCallBack="deleteBatchOrg"
							/>
						</a-space>
					</template>
					<template #bodyCell="{ column, record }">
						<template v-if="column.dataIndex === 'category'">
							{{ $TOOL.dictTypeData('ORG_CATEGORY', record.category) }}
						</template>
						<template v-if="column.dataIndex === 'action'">
							<a @click="formRef.onOpen(record)">编辑</a>
							<a-divider type="vertical"/>
							<a-popconfirm title="删除此组织与下级组织吗？" @confirm="removeOrg(record)">
								<a-button type="link" danger size="small">删除</a-button>
							</a-popconfirm>
						</template>
					</template>
				</s-table>
			</a-card>
		</a-col>
	</a-row>
	<Form ref="formRef" @successful="tableRef.refresh()"/>
</template>

<script setup name="sysOrg">
import {Empty} from 'ant-design-vue'
import {isEmpty} from 'lodash-es'
import orgApi from '@/api/sys/orgApi'
import Form from './form.vue'

const columns = [
	{
		title: '组织名称',
		dataIndex: 'name'
	},
	{
		title: '分类',
		dataIndex: 'category'
	},
	{
		title: '排序',
		dataIndex: 'sortCode'
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
// 定义tableDOM
const tableRef = ref(null)
const formRef = ref()
const searchFormRef = ref()
const searchFormState = ref({})
// 默认展开的节点
const defaultExpandedKeys = ref([])
const treeData = ref([])
// 替换treeNode 中 title,key,children
const treeFieldNames = {children: 'children', title: 'name', key: 'id'}
const cardLoading = ref(true)

// 表格查询 返回 Promise 对象
const loadData = (parameter) => {
	loadTreeData()
	return orgApi.orgPage(Object.assign(parameter, searchFormState.value)).then((res) => {
		return res
	})
}
// 重置
const reset = () => {
	searchFormRef.value.resetFields()
	tableRef.value.refresh(true)
}
// 加载左侧的树
const loadTreeData = () => {
	orgApi.orgTree().then((res) => {
		cardLoading.value = false
		if (res !== null) {
			treeData.value = res
			if (isEmpty(defaultExpandedKeys.value)) {
				// 默认展开2级
				treeData.value.forEach((item) => {
					// 因为0的顶级
					if (item.parentId === '0') {
						defaultExpandedKeys.value.push(item.id)
						// 取到下级ID
						if (item.children) {
							item.children.forEach((items) => {
								defaultExpandedKeys.value.push(items.id)
							})
						}
					}
				})
			}
		}
	})
}
// 点击树查询
const treeSelect = (selectedKeys) => {
	if (selectedKeys.length > 0) {
		searchFormState.value.parentId = selectedKeys.toString()
	} else {
		delete searchFormState.value.parentId
	}
	tableRef.value.refresh(true)
}
// 删除
const removeOrg = (record) => {
	let params = [
		{
			id: record.id
		}
	]
	orgApi.orgDelete(params).then(() => {
		tableRef.value.refresh(true)
	})
}
// 批量删除
const deleteBatchOrg = (params) => {
	orgApi.orgDelete(params).then(() => {
		tableRef.value.clearRefreshSelected()
	})
}
</script>

<style scoped>

</style>
