<template>
	<div class="dict-container">
		<!-- 搜索区域 -->
		<a-form ref="searchFormRef" :model="searchFormState" layout="inline" class="search-form">
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
			:scroll="{ x: 'max-content' }"
			@on-expand="onExpandChange"
		>
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
						<a @click="formRef.onOpen(record)" v-if="hasPerm('bizDictEdit')">编辑</a>
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
									<span>{{ index + 1 }}</span>
								</template>
								<template v-if="column.dataIndex === 'dictValue'">
									<a-typography-text code>{{ valueRecord.dictValue }}</a-typography-text>
								</template>
								<template v-if="column.dataIndex === 'action'">
									<a @click="formRef.onOpen(valueRecord, record)" v-if="hasPerm('bizDictEdit')">编辑</a>
								</template>
							</template>
						</a-table>
						<transition name="fade">
							<div v-show="showScrollHint" class="scroll-hint">
								<DownOutlined />
							</div>
						</transition>
					</div>
				</div>
			</template>
		</s-table>
	</div>
	<Form ref="formRef" @successful="formSuccessful()" />
</template>

<script setup>
	import bizDictApi from '@/api/biz/bizDictApi'
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
		}
	]
	if (hasPerm('bizDictEdit')) {
		columns.push({
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: 100,
			fixed: 'right'
		})
	}

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
			title: '排序',
			dataIndex: 'sortCode',
			align: 'center',
			width: 80
		}
	]
	if (hasPerm('bizDictEdit')) {
		valueColumns.push({
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: 100
		})
	}

	// 定义tableDOM
	const tableRef = ref(null)
	const formRef = ref()
	const searchFormRef = ref()
	const searchFormState = ref({})
	// 各字典类型下的字典值集合，key为类型id
	const dictValueMap = ref({})
	// 当前展开的行，手风琴模式同时只展开一个
	const expandedRowKeys = ref([])
	// 子表格滚动提示是否显示（滚动到底后隐藏）
	const showScrollHint = ref(false)
	const toolConfig = { refresh: true, height: true, columnSetting: true, striped: false }

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

	// 表格查询 返回 Promise 对象，只查字典类型（parentId为0的根级）
	const loadData = (parameter) => {
		loadDictValueData()
		parameter.parentId = '0'
		return bizDictApi.dictPage(Object.assign(parameter, searchFormState.value))
	}

	// 通过字典树一次性加载各类型下的字典值
	const loadDictValueData = () => {
		bizDictApi.dictTree().then((res) => {
			const map = {}
			;(res || []).forEach((node) => {
				map[node.id] = (node.children || []).slice().sort((a, b) => (a.sortCode || 0) - (b.sortCode || 0))
			})
			dictValueMap.value = map
		})
	}

	const getDictValues = (typeId) => {
		return dictValueMap.value[typeId] || []
	}

	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}

	// 表单界面回调
	const formSuccessful = () => {
		tableRef.value.refresh()
		loadDictValueData()
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
