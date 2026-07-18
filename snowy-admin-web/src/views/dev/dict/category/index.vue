<template>
	<div class="dict-container">
		<!-- 搜索区域 -->
		<a-form ref="searchFormRef" :model="searchFormState" layout="inline" class="search-form">
			<a-form-item>
				<a-radio-group v-model:value="categoryType" button-style="solid" @change="typeChange">
					<a-radio-button value="FRM">系统字典</a-radio-button>
					<a-radio-button value="BIZ">业务字典</a-radio-button>
				</a-radio-group>
			</a-form-item>
			<a-form-item label="关键词" name="searchKey">
				<a-input
					v-model:value="searchFormState.searchKey"
					placeholder="请输入字典类型名称"
					allow-clear
					@press-enter="tableRef.refresh(true)"
				/>
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

		<!-- 字典类型表格，单击行展开显示该类型下的字典值 -->
		<s-table
			ref="tableRef"
			:columns="columns"
			:data="loadData"
			:expand-row-by-click="true"
			:expanded-row-keys="expandedRowKeys"
			bordered
			:tool-config="toolConfig"
			:row-key="(record) => record.id"
			:alert="options.alert.show"
			:row-selection="options.rowSelection"
			:scroll="{ x: 'max-content' }"
			@on-expand="onExpandChange"
		>
			<template #operator>
				<a-space>
					<a-button type="primary" @click="addDictType">
						<template #icon><PlusOutlined /></template>
						新增字典类型
					</a-button>
					<xn-batch-button
						buttonName="批量删除"
						icon="DeleteOutlined"
						buttonDanger
						:selectedRowKeys="selectedRowKeys"
						@batchCallBack="deleteBatchDict"
					/>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'dictLabel'">
					<span class="dict-type-label">{{ record.dictLabel }}</span>
				</template>
				<template v-if="column.dataIndex === 'dictValue'">
					<a-typography-text code>{{ record.dictValue }}</a-typography-text>
				</template>
				<template v-if="column.dataIndex === 'childCount'">
					<a-tag :color="record.childCount ? 'processing' : 'default'">{{ record.childCount || 0 }} 项</a-tag>
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-space @click.stop>
						<a @click="addDictValue(record)">添加值</a>
						<a-divider type="vertical" />
						<a @click="formRef.onOpen(record, categoryType)">编辑</a>
						<a-divider type="vertical" />
						<a-popconfirm title="确定要删除此字典类型及其下的字典值吗？" @confirm="remove(record)" placement="topRight">
							<a-button type="link" danger size="small" style="padding: 0">删除</a-button>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
			<template #expandedRowRender="{ record }">
				<div class="dict-value-panel">
					<div class="dict-value-table-wrap">
						<a-table
							size="small"
							:columns="valueColumns"
							:data-source="getDictValues(record.id)"
							:pagination="false"
							:row-key="(value) => value.id"
							:scroll="getDictValues(record.id).length > 10 ? { y: 360 } : undefined"
						>
							<template #bodyCell="{ column, record: valueRecord, index }">
								<template v-if="column.dataIndex === 'index'">
									<span class="dict-value-index">{{ index + 1 }}</span>
								</template>
								<template v-if="column.dataIndex === 'dictValue'">
									<a-typography-text code>{{ valueRecord.dictValue }}</a-typography-text>
								</template>
								<template v-if="column.dataIndex === 'dictColor'">
									<a-tag v-if="valueRecord.dictColor" :color="valueRecord.dictColor">{{ valueRecord.dictLabel }}</a-tag>
								</template>
								<template v-if="column.dataIndex === 'action'">
									<a-space>
										<a @click="formRef.onOpen(valueRecord, categoryType, record)">编辑</a>
										<a-divider type="vertical" />
										<a-popconfirm title="确定要删除此字典值吗？" @confirm="remove(valueRecord)" placement="topRight">
											<a-button type="link" danger size="small" style="padding: 0">删除</a-button>
										</a-popconfirm>
									</a-space>
								</template>
							</template>
						</a-table>
						<transition name="fade">
							<div v-show="showScrollHint" class="scroll-hint">
								<DownOutlined />
							</div>
						</transition>
					</div>
					<a-button type="dashed" block class="dict-value-add" @click="addDictValue(record)">
						<template #icon><PlusOutlined /></template>
						添加字典值
					</a-button>
				</div>
			</template>
		</s-table>
	</div>
	<Form ref="formRef" @successful="formSuccessful()" />
</template>

<script setup name="dictCategoryIndex">
	import dictApi from '@/api/dev/dictApi'
	import Form from './form.vue'
	import tool from '@/utils/tool'

	const columns = [
		{
			title: '字典类型名称',
			dataIndex: 'dictLabel'
		},
		{
			title: '类型编码',
			dataIndex: 'dictValue',
			ellipsis: true
		},
		{
			title: '字典值数',
			dataIndex: 'childCount',
			align: 'center',
			width: 100
		},
		{
			title: '排序',
			dataIndex: 'sortCode',
			align: 'center',
			width: 80
		},
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: 200,
			fixed: 'right'
		}
	]

	// 展开后的字典值表格列
	const valueColumns = [
		{
			title: '序号',
			dataIndex: 'index',
			align: 'center',
			width: 60
		},
		{
			title: '字典文字',
			dataIndex: 'dictLabel'
		},
		{
			title: '字典值',
			dataIndex: 'dictValue',
			ellipsis: true
		},
		{
			title: '标签预览',
			dataIndex: 'dictColor',
			align: 'center',
			width: 120
		},
		{
			title: '排序',
			dataIndex: 'sortCode',
			align: 'center',
			width: 80
		},
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: 130
		}
	]

	const categoryType = ref('FRM')

	// 定义tableDOM
	const tableRef = ref(null)
	const formRef = ref()
	const searchFormRef = ref()
	const searchFormState = ref({})
	// 各字典类型下的字典值集合，key为类型id
	const dictValueMap = ref({})
	// 当前分类下字典类型的最大排序号
	const typeMaxSortCode = ref(0)
	// 当前展开的行，手风琴模式同时只展开一个
	const expandedRowKeys = ref([])
	// 子表格滚动提示是否显示（滚动到底后隐藏）
	const showScrollHint = ref(false)

	// 展开/收起某一行，保证同时只展开一个字典类型
	const onExpandChange = (expanded, record) => {
		expandedRowKeys.value = expanded ? [record.id] : []
		showScrollHint.value = false
		if (expanded && getDictValues(record.id).length > 10) {
			nextTick(bindScrollHint)
		}
	}

	// 绑定子表格滚动监听，未到底时显示提示，滚动到底后隐藏
	const bindScrollHint = () => {
		const body = document.querySelector('.dict-value-panel .ant-table-body')
		if (!body) return
		const update = () => {
			showScrollHint.value = body.scrollHeight - body.scrollTop - body.clientHeight > 8
		}
		update()
		body.onscroll = update
	}

	const toolConfig = { refresh: true, height: true, columnSetting: true, striped: false }

	// 选择配置
	let selectedRowKeys = ref([])
	const options = {
		alert: {
			show: false,
			clear: () => {
				selectedRowKeys = ref([])
			}
		},
		rowSelection: {
			onChange: (selectedRowKey, selectedRows) => {
				selectedRowKeys.value = selectedRowKey
			}
		}
	}

	// 表格查询 返回 Promise 对象，只查字典类型（parentId为0的根级）
	const loadData = (parameter) => {
		loadDictValueData()
		parameter.category = categoryType.value
		parameter.parentId = '0'
		return dictApi.dictPage(Object.assign(parameter, searchFormState.value))
	}

	// 通过字典树一次性加载各类型下的字典值
	const loadDictValueData = () => {
		dictApi.dictTree({ category: categoryType.value }).then((res) => {
			const map = {}
			let maxSortCode = 0
			;(res || []).forEach((node) => {
				maxSortCode = Math.max(maxSortCode, node.sortCode || 0)
				map[node.id] = (node.children || []).slice().sort((a, b) => (a.sortCode || 0) - (b.sortCode || 0))
			})
			dictValueMap.value = map
			typeMaxSortCode.value = maxSortCode
		})
	}

	const getDictValues = (typeId) => {
		return dictValueMap.value[typeId] || []
	}

	// 新增字典类型，排序号默认为当前最大排序号加1
	const addDictType = () => {
		formRef.value.onOpen(undefined, categoryType.value, undefined, typeMaxSortCode.value + 1)
	}

	// 给某个字典类型添加字典值，排序号默认为该类型下最大排序号加1
	const addDictValue = (record) => {
		const values = getDictValues(record.id)
		const maxSortCode = values.reduce((max, item) => Math.max(max, item.sortCode || 0), 0)
		formRef.value.onOpen(undefined, categoryType.value, record, maxSortCode + 1)
	}

	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}

	// 切换分类
	const typeChange = () => {
		expandedRowKeys.value = []
		showScrollHint.value = false
		tableRef.value.clearSelected && tableRef.value.clearSelected()
		tableRef.value.refresh(true)
	}

	// 删除（字典类型或字典值通用）
	const remove = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		dictApi.dictDelete(params).then(() => {
			tableRef.value.refresh()
			loadDictValueData()
			refreshStoreDict()
		})
	}

	// 批量删除
	const deleteBatchDict = (params) => {
		dictApi.dictDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
			loadDictValueData()
			refreshStoreDict()
		})
	}

	// 表单界面回调
	const formSuccessful = () => {
		tableRef.value.refresh()
		loadDictValueData()
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
	.dict-container {
		height: 100%;

		.search-form {
			margin-bottom: 16px;
		}

		.dict-type-label {
			font-weight: 500;
		}

		.dict-value-panel {
			padding: 0;
			animation: dictExpandIn 0.25s ease;

			.dict-value-table-wrap {
				position: relative;
			}

			.scroll-hint {
				position: absolute;
				left: 0;
				right: 0;
				bottom: 0;
				height: 28px;
				display: flex;
				align-items: flex-end;
				justify-content: center;
				padding-bottom: 2px;
				pointer-events: none;
				z-index: 2;
				color: var(--primary-color);
				font-size: 14px;
				background: linear-gradient(to bottom, transparent, var(--component-background));

				:deep(.anticon) {
					animation: hintBounce 1.2s ease-in-out infinite;
				}
			}

			.dict-value-add {
				margin-top: 8px;
			}
		}

		:deep(.ant-table-expanded-row > .ant-table-cell) {
			padding: 6px 12px;
			box-shadow:
				inset 0 5px 5px -5px rgba(0, 0, 0, 0.22),
				inset 0 6px 4px -6px rgba(255, 255, 255, 0.08),
				inset 0 -4px 4px -4px rgba(0, 0, 0, 0.1);
		}
	}

	.fade-enter-active,
	.fade-leave-active {
		transition: opacity 0.3s ease;
	}
	.fade-enter-from,
	.fade-leave-to {
		opacity: 0;
	}

	@keyframes hintBounce {
		0%,
		100% {
			transform: translateY(0);
		}
		50% {
			transform: translateY(3px);
		}
	}

	@keyframes dictExpandIn {
		from {
			opacity: 0;
			transform: translateY(-4px);
		}
		to {
			opacity: 1;
			transform: translateY(0);
		}
	}
</style>
