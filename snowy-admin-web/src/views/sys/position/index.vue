<template>
	<XnResizablePanel direction="row" :initial-size="300" :min-size="200" :max-size="500" :md="0">
		<template #left>
			<div ref="treeContainerRef" style="height: 100%; display: flex; flex-direction: column">
				<a-input-search
					v-model:value="treeSearchKey"
					placeholder="搜索组织"
					allow-clear
					size="small"
					style="margin-bottom: 8px; flex-shrink: 0"
					@search="onTreeSearch"
				/>
				<div style="flex: 1; overflow: hidden">
					<xn-tree-skeleton v-if="treeLoading && treeData.length === 0" />
					<a-spin v-else-if="treeData.length > 0" :spinning="treeLoading">
						<a-tree
							v-model:expandedKeys="defaultExpandedKeys"
							v-model:loadedKeys="treeLoadedKeys"
							:show-line="{ showLeafIcon: false }"
							:tree-data="treeData"
							:field-names="treeFieldNames"
							:load-data="searchMode ? undefined : onLoadData"
							:height="treeHeight"
							@select="treeSelect"
						/>
					</a-spin>
					<a-empty v-else :image="Empty.PRESENTED_IMAGE_SIMPLE" />
				</div>
			</div>
		</template>
		<template #right>
			<a-form ref="searchFormRef" :model="searchFormState">
				<a-row :gutter="10">
					<a-col :xs="24" :sm="8" :md="8" :lg="0" :xl="0">
						<a-form-item label="组织：" name="orgId">
							<a-tree-select
								v-model:value="searchFormState.orgId"
								class="xn-wd"
								:dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
								placeholder="请选择组织"
								allow-clear
								:tree-data="treeData"
								:field-names="treeSelectFieldNames"
								tree-line
								:load-data="onLoadData"
							/>
						</a-form-item>
					</a-col>
					<a-col :xs="24" :sm="8" :md="8" :lg="8" :xl="8">
						<a-form-item name="searchKey" label="关键词">
							<a-input v-model:value="searchFormState.searchKey" placeholder="请输入职位名称关键词" />
						</a-form-item>
					</a-col>
					<a-col :xs="24" :sm="16" :md="16" :lg="16" :xl="16">
						<a-form-item>
							<a-space>
								<a-button type="primary" @click="tableRef.refresh(true)">
									<template #icon>
										<SearchOutlined />
									</template>
									查询
								</a-button>
								<a-button @click="reset">
									<template #icon>
										<redo-outlined />
									</template>
									重置
								</a-button>
							</a-space>
						</a-form-item>
					</a-col>
				</a-row>
			</a-form>
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
				<template #operator>
					<a-space>
						<a-button type="primary" @click="formRef.onOpen(undefined, searchFormState.orgId)">
							<template #icon>
								<plus-outlined />
							</template>
							新增
						</a-button>
						<xn-batch-button
							buttonName="批量删除"
							icon="DeleteOutlined"
							buttonDanger
							:selectedRowKeys="selectedRowKeys"
							@batchCallBack="deleteBatchPosition"
						/>
					</a-space>
				</template>
				<template #bodyCell="{ column, record }">
					<template v-if="column.dataIndex === 'category'">
						{{ $TOOL.dictTypeData('POSITION_CATEGORY', record.category) }}
					</template>
					<template v-if="column.dataIndex === 'action'">
						<a @click="formRef.onOpen(record)">编辑</a>
						<a-divider type="vertical" />
						<a-popconfirm title="确定删除此职位？" @confirm="removeOrg(record)">
							<a-button type="link" danger size="small">删除</a-button>
						</a-popconfirm>
					</template>
				</template>
			</s-table>
		</template>
	</XnResizablePanel>
	<Form ref="formRef" @successful="tableRef.refresh(true)" />
</template>

<script setup name="sysPosition">
	import { Empty } from 'ant-design-vue'
	import { triggerRef, onMounted, onActivated, onUnmounted } from 'vue'
	import positionApi from '@/api/sys/positionApi'
	import orgApi from '@/api/sys/orgApi'
	import Form from './form.vue'

	const columns = [
		{
			title: '职位名称',
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
	const treeFieldNames = { children: 'children', title: 'name', key: 'id' }
	const treeSelectFieldNames = { children: 'children', label: 'name', value: 'id' }
	// 树容器高度自适应
	const treeContainerRef = ref(null)
	const treeHeight = ref(0)
	let resizeObserver = null
	const calcTreeHeight = () => {
		if (treeContainerRef.value) {
			treeHeight.value = treeContainerRef.value.clientHeight - 40
		}
	}
	onMounted(() => {
		calcTreeHeight()
		if (treeContainerRef.value) {
			resizeObserver = new ResizeObserver(calcTreeHeight)
			resizeObserver.observe(treeContainerRef.value)
		}
	})
	onActivated(calcTreeHeight)
	onUnmounted(() => {
		if (resizeObserver) {
			resizeObserver.disconnect()
		}
	})

	// 表格查询 返回 Promise 对象
	const loadData = (parameter) => {
		return positionApi.positionPage(Object.assign(parameter, searchFormState.value)).then((res) => {
			return res
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	const treeLoading = ref(true)
	const treeSearchKey = ref('')
	const searchMode = ref(false)
	const treeLoadedKeys = ref([])
	const collectTreeKeys = (nodes) => {
		const keys = []
		const traverse = (list) => {
			if (!list) return
			list.forEach((node) => {
				keys.push(node.id)
				if (node.children) traverse(node.children)
			})
		}
		traverse(nodes)
		return keys
	}
	const onTreeSearch = (value) => {
		if (!value || !value.trim()) {
			// 先清空树数据和展开状态，再切换模式，避免懒加载风暴导致卡死
			treeData.value = []
			defaultExpandedKeys.value = []
			treeLoadedKeys.value = []
			searchMode.value = false
			loadTreeData()
			return
		}
		treeLoading.value = true
		searchMode.value = true
		orgApi
			.orgTree({ searchKey: value.trim() })
			.then((res) => {
				if (res !== null) {
					treeData.value = res
					defaultExpandedKeys.value = collectTreeKeys(res)
				} else {
					treeData.value = []
				}
			})
			.finally(() => {
				treeLoading.value = false
			})
	}
	// 加载左侧的树
	const loadTreeData = () => {
		treeLoading.value = true
		orgApi
			.orgTree()
			.then((res) => {
				if (res !== null) {
					treeLoadedKeys.value = []
					defaultExpandedKeys.value = []
					treeData.value = res.map((item) => {
						return {
							...item,
							isLeaf: item.isLeaf === undefined ? false : item.isLeaf
						}
					})
					// 只有一个根节点时才自动展开
					if (treeData.value.length === 1) {
						defaultExpandedKeys.value = [treeData.value[0].id]
					}
				}
			})
			.finally(() => {
				treeLoading.value = false
			})
	}
	loadTreeData()
	// 懒加载子节点
	const onLoadData = (treeNode) => {
		return new Promise((resolve) => {
			if (treeNode.dataRef.children || treeNode.dataRef.isLeaf) {
				resolve()
				return
			}
			orgApi
				.orgTree({
					parentId: treeNode.dataRef.id
				})
				.then((res) => {
					treeNode.dataRef.children = res.map((item) => {
						return {
							...item,
							isLeaf: item.isLeaf === undefined ? false : item.isLeaf
						}
					})
					triggerRef(treeData)
					resolve()
				})
		})
	}
	// 点击树查询
	const treeSelect = (selectedKeys) => {
		if (selectedKeys.length > 0) {
			searchFormState.value.orgId = selectedKeys.toString()
		} else {
			delete searchFormState.value.orgId
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
		positionApi.positionDelete(params).then(() => {
			tableRef.value.refresh(true)
		})
	}
	// 批量删除
	const deleteBatchPosition = (params) => {
		positionApi.positionDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
</script>
