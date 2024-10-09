<template>
	<a-row :gutter="10">
		<a-col :xs="24" :sm="24" :md="24" :lg="5" :xl="5">
			<div class="dict-tree-div">
				<a-tree
					v-if="treeData.length > 0"
					v-model:expandedKeys="defaultExpandedKeys"
					:tree-data="treeData"
					:field-names="treeFieldNames"
					@select="treeSelect"
				>
				</a-tree>
				<a-empty v-else :image="Empty.PRESENTED_IMAGE_SIMPLE" />
			</div>
		</a-col>
		<a-col :xs="24" :sm="24" :md="24" :lg="19" :xl="19">
			<a-form ref="searchFormRef" name="advanced_search" class="ant-advanced-search-form mb-3" :model="searchFormState">
				<a-row :gutter="24">
					<a-col :span="8">
						<a-form-item name="searchKey" label="字典名称">
							<a-input v-model:value="searchFormState.searchKey" placeholder="请输入字典名称" />
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
			<a-divider class="m-3 mx-0" />
			<s-table
				ref="tableRef"
				:columns="columns"
				:data="loadData"
				:expand-row-by-click="true"
				bordered
				:tool-config="toolConfig"
				:row-key="(record) => record.id"
			>
				<template #operator class="table-operator">
					<a-button type="primary" @click="formRef.onOpen(undefined, categoryType, searchFormState.parentId)">
						<template #icon><plus-outlined /></template>
						新增
					</a-button>
				</template>
				<template #bodyCell="{ column, record }">
					<template v-if="column.dataIndex === 'level'">
						<a-tag color="blue" v-if="record.level">{{ record.level }}</a-tag>
						<a-tag color="green" v-else>子级</a-tag>
					</template>
					<template v-if="column.dataIndex === 'action'">
						<a @click="formRef.onOpen(record, categoryType)">编辑</a>
						<a-divider type="vertical" />
						<a-popconfirm title="删除此字典与下级字典吗？" @confirm="remove(record)">
							<a-button type="link" danger size="small">删除</a-button>
						</a-popconfirm>
					</template>
				</template>
			</s-table>
		</a-col>
	</a-row>
	<Form ref="formRef" @successful="formSuccessful()" />
</template>

<script setup name="dictCategoryIndex">
	import { Empty } from 'ant-design-vue'
	import dictApi from '@/api/dev/dictApi'
	import Form from './form.vue'
	import tool from '@/utils/tool'
	const props = defineProps({
		type: {
			type: String,
			default: 'FRM'
		}
	})
	const columns = [
		{
			title: '字典名称',
			dataIndex: 'dictLabel',
			width: 350
		},
		{
			title: '字典值',
			dataIndex: 'dictValue',
			width: 350
		},
		{
			title: '排序',
			dataIndex: 'sortCode'
		},
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: '150px'
		}
	]
	const categoryType = computed(() => {
		return props.type
	})
	// 定义tableDOM
	const tableRef = ref(null)
	const formRef = ref()
	const searchFormRef = ref()
	const searchFormState = ref({})
	// 默认展开的节点
	let defaultExpandedKeys = ref([])
	const treeData = ref([])
	// 替换treeNode 中 title,key,children
	const treeFieldNames = { children: 'children', title: 'dictLabel', key: 'id' }
	const toolConfig = { refresh: true, height: true, columnSetting: true, striped: false }

	// 表格查询 返回 Promise 对象
	const loadData = (parameter) => {
		loadTreeData()
		parameter.category = categoryType.value
		return dictApi.dictPage(Object.assign(parameter, searchFormState.value)).then((data) => {
			if (data.records) {
				if (searchFormState.value.parentId) {
					let dataArray = []
					data.records.forEach((item) => {
						const obj = data.records.find((f) => f.id === item.parentId)
						if (!obj) {
							dataArray.push(item)
						}
					})
					if (dataArray.length === 1) {
						data.records.forEach((item) => {
							if (item.id === dataArray[0].id) {
								item.level = '上级'
							}
						})
					}
					dataArray = []
				}
			}
			return data
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	// 加载左侧的树
	const loadTreeData = () => {
		const param = {
			category: categoryType.value
		}
		dictApi.dictTree(param).then((res) => {
			if (res) {
				treeData.value = res
			}
		})
	}
	// 点击树查询
	const treeSelect = (selectedKeys) => {
		if (selectedKeys && selectedKeys.length > 0) {
			searchFormState.value.parentId = selectedKeys.toString()
			if (!columns.find((f) => f.title === '层级')) {
				columns.splice(2, 0, {
					title: '层级',
					dataIndex: 'level',
					width: 100
				})
			}
		} else {
			delete searchFormState.value.parentId
			columns.splice(2, 1)
		}
		tableRef.value.refresh(true)
	}
	// 删除
	const remove = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		dictApi.dictDelete(params).then(() => {
			tableRef.value.refresh()
			refreshStoreDict()
		})
	}
	// 表单界面回调
	const formSuccessful = () => {
		tableRef.value.refresh()
		refreshStoreDict()
	}
	// 刷新store中的字典
	const refreshStoreDict = () => {
		dictApi.dictTree().then((res) => {
			tool.data.set('DICT_TYPE_TREE_DATA', res)
		})
	}
</script>

<style scoped lang="less">
	.ant-form-item {
		margin-bottom: 0 !important;
	}
	.snowy-button-left {
		margin-left: 8px;
	}
	.dict-tree-div {
		height: 700px;
		overflow: auto;
	}
</style>
