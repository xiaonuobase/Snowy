<template>
	<div class="dict-container">
		<XnResizablePanel
			:initialSize="300"
			:minSize="250"
			:maxSize="600"
			:bottomGap="10"
			:leftPadding="12"
			:rightPadding="24"
		>
			<template #left>
				<!-- 左侧面板 -->
				<div class="dict-left-panel">
					<a-spin :spinning="cardLoading">
						<div class="dict-left-header">
							<a-radio-group
								v-model:value="categoryType"
								button-style="solid"
								class="dict-type-radio"
								@change="typeChange"
							>
								<a-radio-button value="FRM">系统字典</a-radio-button>
								<a-radio-button value="BIZ">业务字典</a-radio-button>
							</a-radio-group>
							<a-input-search
								v-model:value="treeSearchKey"
								placeholder="搜索字典"
								class="dict-tree-search"
								allow-clear
								@search="onTreeSearch"
							/>
						</div>
						<div class="dict-tree-wrapper">
							<a-tree
								v-if="treeData.length > 0"
								v-model:expandedKeys="defaultExpandedKeys"
								:tree-data="treeData"
								:field-names="treeFieldNames"
								block-node
								:auto-expand-parent="autoExpandParent"
								@select="treeSelect"
							>
								<template #title="{ dictLabel }">
									<span v-if="dictLabel.indexOf(treeSearchKey) > -1">
										{{ dictLabel.substr(0, dictLabel.indexOf(treeSearchKey)) }}
										<span style="color: #f50">{{ treeSearchKey }}</span>
										{{ dictLabel.substr(dictLabel.indexOf(treeSearchKey) + treeSearchKey.length) }}
									</span>
									<span v-else>{{ dictLabel }}</span>
								</template>
							</a-tree>
							<a-empty v-else :image="Empty.PRESENTED_IMAGE_SIMPLE" description="暂无数据" />
						</div>
					</a-spin>
				</div>
			</template>
			<template #right>
				<!-- 右侧面板 -->
				<div class="dict-right-panel">
					<!-- 搜索区域 -->
					<a-form ref="searchFormRef" :model="searchFormState" layout="inline" class="search-form">
						<a-form-item label="关键词" name="searchKey">
							<a-input v-model:value="searchFormState.searchKey" placeholder="请输入字典名称关键词" allow-clear />
						</a-form-item>
						<a-form-item>
							<a-space>
								<a-button type="primary" @click="tableRef.refresh(true)">
									<template #icon><SearchOutlined /></template>
									查询
								</a-button>
								<a-button @click="reset">
									<template #icon><RedoOutlined /></template>
									重置
								</a-button>
							</a-space>
						</a-form-item>
					</a-form>

					<!-- 表格区域 -->
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
						<template #operator>
							<a-space>
								<a-button type="primary" @click="formRef.onOpen(undefined, categoryType, searchFormState.parentId)">
									<template #icon><PlusOutlined /></template>
									新增字典
								</a-button>
							</a-space>
						</template>
						<template #bodyCell="{ column, record }">
							<template v-if="column.dataIndex === 'level'">
								<a-tag color="processing" v-if="record.level">{{ record.level }}</a-tag>
								<a-tag color="success" v-else>子级</a-tag>
							</template>
							<template v-if="column.dataIndex === 'action'">
								<a-space>
									<a @click="formRef.onOpen(record, categoryType)">编辑</a>
									<a-divider type="vertical" />
									<a-popconfirm title="确定要删除此字典及其下级字典吗？" @confirm="remove(record)" placement="topRight">
										<a-button type="link" danger size="small" style="padding: 0">删除</a-button>
									</a-popconfirm>
								</a-space>
							</template>
						</template>
					</s-table>
				</div>
			</template>
		</XnResizablePanel>
	</div>
	<Form ref="formRef" @successful="formSuccessful()" />
</template>

<script setup name="dictCategoryIndex">
	import { Empty } from 'ant-design-vue'
	import dictApi from '@/api/dev/dictApi'
	import Form from './form.vue'
	import tool from '@/utils/tool'
	import XnResizablePanel from '@/components/XnResizablePanel/index.vue'

	const props = defineProps({
		type: {
			type: String,
			default: 'FRM'
		}
	})

	const columns = ref([
		{
			title: '字典名称',
			dataIndex: 'dictLabel',
			width: 200
		},
		{
			title: '字典值',
			dataIndex: 'dictValue',
			ellipsis: true
		},
		{
			title: '排序',
			dataIndex: 'sortCode',
			width: 100,
			align: 'center'
		},
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			fixed: 'right',
			width: 150
		}
	])

	const categoryType = ref('FRM')
	const treeSearchKey = ref('')
	const autoExpandParent = ref(true)

	// 定义tableDOM
	const tableRef = ref(null)
	const formRef = ref()
	const cardLoading = ref(true)
	const searchFormRef = ref()
	const searchFormState = ref({})
	// 默认展开的节点
	let defaultExpandedKeys = ref([])
	const treeData = ref([])
	// 备份完整树数据用于搜索
	const treeDataOrigin = ref([])

	// 替换treeNode 中 title,key,children
	const treeFieldNames = { children: 'children', title: 'dictLabel', key: 'id' }
	const toolConfig = { refresh: true, height: true, columnSetting: true, striped: false }

	// 表格查询 返回 Promise 对象
	const loadData = (parameter) => {
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

	// 切换类型
	const typeChange = () => {
		cardLoading.value = true
		treeSearchKey.value = ''
		loadTreeData()
		// 切换类型时清空选中状态并刷新列表
		searchFormState.value.parentId = undefined
		const index = columns.value.findIndex((f) => f.title === '层级')
		if (index !== -1) {
			columns.value.splice(index, 1)
		}
		tableRef.value.refresh(true)
	}

	// 加载左侧的树
	const loadTreeData = () => {
		const param = {
			category: categoryType.value
		}
		dictApi
			.dictTree(param)
			.then((res) => {
				if (res) {
					treeData.value = res
					treeDataOrigin.value = res
				} else {
					treeData.value = []
					treeDataOrigin.value = []
				}
			})
			.finally(() => {
				cardLoading.value = false
			})
	}
	// 初始化加载树
	loadTreeData()

	// 树搜索
	const onTreeSearch = () => {
		if (!treeSearchKey.value) {
			treeData.value = treeDataOrigin.value
			return
		}
		// 简单的前端搜索过滤，如果数据量大应该走后端
		// 这里暂不实现复杂的前端递归过滤，AntDV的Tree通常配合后端搜索或扁平化数据处理
		// 既然是字典树，数据量通常不大，我们只高亮匹配项，或者让用户通过视觉查找
		// 这里为了体验，我们保持树结构不变，仅高亮，且展开所有
		autoExpandParent.value = true
		// 递归获取所有包含key的节点的父级key，用于展开
		const expanded = []
		const getParentKeys = (data, key) => {
			data.forEach((item) => {
				if (item.children) {
					if (JSON.stringify(item.children).indexOf(key) > -1) {
						expanded.push(item.id)
					}
					getParentKeys(item.children, key)
				}
			})
		}
		getParentKeys(treeDataOrigin.value, treeSearchKey.value)
		defaultExpandedKeys.value = [...new Set(expanded)]
	}
	// 监听搜索词变化
	watch(treeSearchKey, () => {
		onTreeSearch()
	})

	// 点击树查询
	const treeSelect = (selectedKeys) => {
		if (selectedKeys && selectedKeys.length > 0) {
			searchFormState.value.parentId = selectedKeys.toString()
			if (!columns.value.find((f) => f.title === '层级')) {
				columns.value.splice(2, 0, {
					title: '层级',
					dataIndex: 'level',
					width: 100,
					align: 'center'
				})
			}
		} else {
			delete searchFormState.value.parentId
			const index = columns.value.findIndex((f) => f.title === '层级')
			if (index !== -1) {
				columns.value.splice(index, 1)
			}
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
			loadTreeData() // 删除后刷新树
		})
	}

	// 表单界面回调
	const formSuccessful = () => {
		tableRef.value.refresh()
		refreshStoreDict()
		loadTreeData() // 新增/编辑后刷新树
	}

	// 刷新store中的字典
	const refreshStoreDict = () => {
		dictApi.dictTree().then((res) => {
			tool.data.set('DICT_TYPE_TREE_DATA', res)
		})
	}
</script>

<style scoped lang="less">
	.dict-container {
		height: 100%; /* 适配常见的顶部导航高度，保证铺满但不溢出 */
		.dict-left-panel {
			height: 100%;
			background-color: #fff;
			display: flex;
			flex-direction: column;
			overflow: hidden; /* 防止外层滚动 */
			:deep(.ant-spin-nested-loading) {
				height: 100%;
				display: flex;
				flex-direction: column;
				.ant-spin-container {
					height: 100%;
					display: flex;
					flex-direction: column;
				}
			}

			.dict-left-header {
				flex-shrink: 0;
				margin-bottom: 12px;
				text-align: center;

				.dict-type-radio {
					width: 100%;
					margin-bottom: 12px;
					display: flex;

					:deep(.ant-radio-button-wrapper) {
						flex: 1;
						text-align: center;
					}
				}

				.dict-tree-search {
					width: 100%;
				}
			}

			.dict-tree-wrapper {
				flex: 1;
				overflow-y: auto;
				/* 隐藏滚动条但保留滚动功能 */
				&::-webkit-scrollbar {
					display: none;
				}
				scrollbar-width: none; /* Firefox */
				-ms-overflow-style: none; /* IE and Edge */
			}
		}

		.dict-right-panel {
			overflow: hidden;
			display: flex;
			flex-direction: column;

			.search-form {
				margin-bottom: 16px;
				flex-shrink: 0;
			}
		}
	}
</style>
