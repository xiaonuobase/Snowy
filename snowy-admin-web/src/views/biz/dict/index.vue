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
				<a-empty v-else :image="Empty.PRESENTED_IMAGE_SIMPLE" />
			</a-card>
		</a-col>
		<a-col :xs="24" :sm="24" :md="24" :lg="20" :xl="20">
			<a-card :bordered="false" class="xn-mb10">
				<a-form ref="searchFormRef" :model="searchFormState">
					<a-row :gutter="10">
						<a-col :xs="24" :sm="8" :md="8" :lg="0" :xl="0">
							<a-form-item label="请选择上级字典：" name="parentId">
								<a-tree-select
									v-model:value="searchFormState.parentId"
									class="xn-wd"
									:dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
									placeholder="请选择上级字典"
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
							<a-form-item name="searchKey" label="字典名称关键词">
								<a-input v-model:value="searchFormState.searchKey" placeholder="请输入字典名称关键词" />
							</a-form-item>
						</a-col>
						<a-col :xs="24" :sm="8" :md="8" :lg="8" :xl="8">
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
			<a-card :bordered="false" class="xn-mb10">
				<s-table
					ref="tableRef"
					:columns="columns"
					:data="loadData"
					:expand-row-by-click="true"
					bordered
					:tool-config="toolConfig"
					:row-key="(record) => record.id"
					:scroll="{ x: 'max-content' }"
				>
					<template #bodyCell="{ column, record }">
						<template v-if="column.dataIndex === 'level'">
							<a-tag color="blue" v-if="record.level">{{ record.level }}</a-tag>
							<a-tag color="green" v-else>子级</a-tag>
						</template>
						<template v-if="column.dataIndex === 'action'">
							<a @click="formRef.onOpen(record)" v-if="hasPerm('bizDictEdit')">编辑</a>
						</template>
					</template>
				</s-table>
			</a-card>
		</a-col>
	</a-row>
	<Form ref="formRef" @successful="formSuccessful()" />
</template>

<script setup>
	import { Empty } from 'ant-design-vue'
	import bizDictApi from '@/api/biz/bizDictApi'
	import Form from './form.vue'
	import tool from '@/utils/tool'
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
		}
	]
	if (hasPerm('bizDictEdit')) {
		columns.push({
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			fixed: 'right'
		})
	}
	// 定义tableDOM
	const tableRef = ref(null)
	const formRef = ref()
	const cardLoading = ref(true)
	const searchFormRef = ref()
	const searchFormState = ref({})
	// 默认展开的节点
	const defaultExpandedKeys = ref([])
	const treeData = ref([])
	// 替换treeNode 中 title,key,children
	const treeFieldNames = { children: 'children', title: 'dictLabel', key: 'id' }
	const toolConfig = { refresh: true, height: true, columnSetting: true, striped: false }

	// 表格查询 返回 Promise 对象
	const loadData = (parameter) => {
		loadTreeData()
		return bizDictApi.dictPage(Object.assign(parameter, searchFormState.value)).then((data) => {
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
		bizDictApi
			.dictTree()
			.then((res) => {
				if (res) {
					treeData.value = res
				}
			})
			.finally(() => {
				cardLoading.value = false
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
	// 表单界面回调
	const formSuccessful = () => {
		tableRef.value.refresh()
		refreshStoreDict()
	}
	// 刷新store中的字典
	const refreshStoreDict = () => {
		nextTick(() => {
			bizDictApi.dictTreeAll().then((res) => {
				tool.data.set('DICT_TYPE_TREE_DATA', res)
			})
		})
	}
</script>

<style lang="less" scoped>

</style>
