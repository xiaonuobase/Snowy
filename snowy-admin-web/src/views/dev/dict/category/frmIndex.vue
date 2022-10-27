<template>
	<a-row>
		<a-col :span="5">
			<a-tree
				v-if="treeData.length > 0"
				v-model:expandedKeys="defaultExpandedKeys"
				:tree-data="treeData"
				:field-names="treeFieldNames"
				@select="treeSelect"
			>
			</a-tree>
			<a-empty v-else :image="Empty.PRESENTED_IMAGE_SIMPLE" />
		</a-col>
		<a-col :span="19">
			<s-table
				ref="table"
				:columns="columns"
				:data="loadData"
				:expand-row-by-click="true"
				bordered
				:row-key="(record) => record.id"
			>
				<template #operator class="table-operator">
					<a-form ref="searchFormRef" name="advanced_search" class="ant-advanced-search-form" :model="searchFormState">
						<a-row :gutter="24">
							<a-col :span="8">
								<a-form-item name="searchKey" label="字典名称">
									<a-input v-model:value="searchFormState.searchKey" placeholder="请输入字典名称"></a-input>
								</a-form-item>
							</a-col>
							<a-col :span="8">
								<a-button type="primary" @click="$refs.table.refresh(true)">
									<template #icon><SearchOutlined /></template>
									查询
								</a-button>
								<a-button class="snowy-buttom-left" @click="() => searchFormRef.resetFields()">
									<template #icon><redo-outlined /></template>
									重置
								</a-button>
							</a-col>
							<a-col :span="8">
								<a-button type="primary" @click="form.onOpen(undefined, 'FRM')">
									<template #icon><plus-outlined /></template>
									新增
								</a-button>
							</a-col>
						</a-row>
					</a-form>
				</template>
				<template #bodyCell="{ column, record }">
					<template v-if="column.dataIndex === 'category'">
						{{ $TOOL.dictTypeData('DICT_CATEGORY', record.category) }}
					</template>
					<template v-if="column.dataIndex === 'action'">
						<a @click="form.onOpen(record, 'FRM')">编辑</a>
					</template>
				</template>
			</s-table>
		</a-col>
	</a-row>
	<Form ref="form" @successful="formSuccessful()" />
</template>

<script setup>
	import { Empty } from 'ant-design-vue'
	import dictApi from '@/api/dev/dictApi'
	import Form from './form.vue'
	const { proxy } = getCurrentInstance()
	const columns = [
		{
			title: '字典名称',
			dataIndex: 'dictLabel'
		},
		{
			title: '字典值',
			dataIndex: 'dictValue'
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
	// 定义tableDOM
	const table = ref(null)
	const form = ref()
	const searchFormRef = ref()
	let searchFormState = reactive({})
	// 默认展开的节点
	let defaultExpandedKeys = ref([])
	const treeData = ref([])
	// 替换treeNode 中 title,key,children
	const treeFieldNames = { children: 'children', title: 'dictLabel', key: 'id' }

	// 表格查询 返回 Promise 对象
	const loadData = (parameter) => {
		loadTreeData()
		parameter.category = 'FRM'
		return dictApi.dictPage(Object.assign(parameter, searchFormState)).then((res) => {
			return res
		})
	}
	// 加载左侧的树
	const loadTreeData = () => {
		const param = {
			category: 'FRM'
		}
		dictApi.dictTree(param).then((res) => {
			if (res !== null) {
				treeData.value = res
			}
		})
	}
	// 点击树查询
	const treeSelect = (selectedKeys) => {
		if (selectedKeys.length > 0) {
			searchFormState.parentId = selectedKeys.toString()
		} else {
			delete searchFormState.parentId
		}
		table.value.refresh(true)
	}
	// 表单界面回调
	const formSuccessful = () => {
		table.value.refresh()
		refreshStoreDict()
	}
	// 刷新store中的字典
	const refreshStoreDict = () => {
		dictApi.dictTree().then((res) => {
			proxy.$TOOL.data.set('DICT_TYPE_TREE_DATA', res)
		})
	}
</script>

<style scoped>
	.cardImp {
		margin-right: 10px;
	}
	.ant-form-item {
		margin-bottom: 0 !important;
	}
	.snowy-buttom-left {
		margin-left: 8px;
	}
</style>
