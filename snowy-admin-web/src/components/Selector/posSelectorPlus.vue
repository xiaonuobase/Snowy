<template>
	<a-modal
		v-model:visible="visible"
		title="职位选择"
		:width="1000"
		:mask-closable="false"
		:destroy-on-close="true"
		@ok="handleOk"
		@cancel="handleClose"
	>
		<a-row :gutter="10">
			<a-col :span="7">
				<a-card size="small" :loading="cardLoading" class="selectorTreeDiv">
					<a-tree
						v-if="treeData"
						v-model:expandedKeys="defaultExpandedKeys"
						:tree-data="treeData"
						:field-names="treeFieldNames"
						@select="treeSelect"
					>
					</a-tree>
				</a-card>
			</a-col>
			<a-col :span="11">
				<div class="table-operator" style="margin-bottom: 10px">
					<a-form ref="searchFormRef" name="advanced_search" class="ant-advanced-search-form" :model="searchFormState">
						<a-row :gutter="24">
							<a-col :span="12">
								<a-form-item name="searchKey">
									<a-input v-model:value="searchFormState.searchKey" placeholder="请输入职位名"></a-input>
								</a-form-item>
							</a-col>
							<a-col :span="12">
								<a-button type="primary" class="primarySele" @click="loadData(searchFormState)"> 查询 </a-button>
								<a-button class="snowy-buttom-left" @click="() => searchFormRef.resetFields()"> 重置 </a-button>
							</a-col>
						</a-row>
					</a-form>
				</div>
				<div class="pos-table">
					<a-table
						ref="table"
						size="small"
						:columns="commons"
						:data-source="tableData"
						:expand-row-by-click="true"
						bordered
					>
						<template #title>
							<span>待选择列表 {{ tableRecordNum }} 条</span>
							<div v-if="!radioModel" style="float: right">
								<a-button type="dashed" size="small" @click="addAllPageRecord">添加当前数据</a-button>
							</div>
						</template>
						<template #bodyCell="{ column, record }">
							<template v-if="column.dataIndex === 'action'">
								<a-button type="dashed" size="small" @click="addRecord(record)">添加</a-button>
							</template>
							<template v-if="column.dataIndex === 'category'">
								{{ $TOOL.dictTypeData('POSITION_CATEGORY', record.category) }}
							</template>
						</template>
					</a-table>
				</div>
			</a-col>
			<a-col :span="6">
				<div class="pos-table">
					<a-table
						ref="selectedTable"
						size="small"
						:columns="selectedCommons"
						:data-source="selectedData"
						:expand-row-by-click="true"
						bordered
					>
						<template #title>
							<span>已选择: {{ selectedData.length }}</span>
							<div v-if="!radioModel" style="float: right">
								<a-button type="dashed" danger size="small" @click="delAllRecord">全部移除</a-button>
							</div>
						</template>
						<template #bodyCell="{ column, record }">
							<template v-if="column.dataIndex === 'action'">
								<a-button type="dashed" danger size="small" @click="delRecord(record)">移除</a-button>
							</template>
						</template>
					</a-table>
				</div>
			</a-col>
		</a-row>
	</a-modal>
</template>

<script setup name="posSelectorPlus">
	import posSelectorPlusApi from '@/api/components/Selector/posSelectorPlusApi'
	import { message } from 'ant-design-vue'
	import { remove } from 'lodash-es'
	// 弹窗是否打开
	let visible = $ref(false)
	// 主表格common
	const commons = [
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: 80
		},
		{
			title: '职位名',
			dataIndex: 'name',
			ellipsis: true
		},
		{
			title: '分类',
			dataIndex: 'category'
		}
	]
	// 选中表格的表格common
	const selectedCommons = [
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: 80
		},
		{
			title: '职位名',
			dataIndex: 'name',
			ellipsis: true
		}
	]
	// 主表格的ref 名称
	const table = ref()
	// 选中表格的ref 名称
	const selectedTable = ref()
	const tableRecordNum = ref()
	let searchFormState = reactive({})
	const searchFormRef = ref()
	const cardLoading = ref(true)
	// 替换treeNode 中 title,key,children
	const treeFieldNames = { children: 'children', title: 'name', key: 'id' }
	// 获取机构树数据
	const treeData = ref()
	//  默认展开二级树的节点id
	let defaultExpandedKeys = ref([])
	const emit = defineEmits({ onBack: null })
	const tableData = ref([])
	const selectedData = ref([])
	const recordIds = ref()
	const props = defineProps(['pageUrl', 'orgUrl', 'radioModel', 'dataIsConverterFlw'])
	// 是否是单选
	const radioModel = props.radioModel || false
	// 数据是否转换成工作流格式
	const dataIsConverterFlw = props.dataIsConverterFlw || false

	// 打开弹框
	const showPosPlusModal = (ids) => {
		visible = true
		if (dataIsConverterFlw) {
			ids = goDataConverter(ids)
		}
		recordIds.value = ids
		// 获取机构树
		posSelectorPlusApi.treeSelector(props.orgUrl).then((res) => {
			cardLoading.value = false
			if (res !== null) {
				treeData.value = res
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
		})
		loadData()
	}
	// 查询主表格数据
	const loadData = () => {
		posSelectorPlusApi.posSelector(props.pageUrl, searchFormState).then((res) => {
			// 总共多少条
			tableRecordNum.value = res.length
			tableData.value = res
			// 如果无查询条件，查询到已加载的
			if (JSON.stringify(searchFormState) === '{}') {
				loadCheckedKey()
			}
		})
	}
	// 加载已选中的
	const loadCheckedKey = () => {
		selectedData.value = []
		if (recordIds.value.length > 0) {
			recordIds.value.forEach((item) => {
				tableData.value.forEach((table) => {
					if (item === table.id) {
						selectedData.value.push(table)
					}
				})
			})
		}
	}
	const judge = () => {
		if (radioModel && selectedData.value.length > 0) {
			return false
		}
		return true
	}
	// 添加记录
	const addRecord = (record) => {
		if (!judge()) {
			message.warning('只可选择一条')
			return
		}
		const selectedRecord = selectedData.value.filter((item) => item.id === record.id)
		if (selectedRecord.length === 0) {
			selectedData.value.push(record)
		} else {
			message.warning('该记录已存在')
		}
	}
	// 添加全部
	const addAllPageRecord = () => {
		let newArray = selectedData.value.concat(tableData.value)
		let list = []
		for (let item1 of newArray) {
			let flag = true
			for (let item2 of list) {
				if (item1.id === item2.id) {
					flag = false
				}
			}
			if (flag) {
				list.push(item1)
			}
		}
		selectedData.value = list
	}
	// 删减记录
	const delRecord = (record) => {
		remove(selectedData.value, (item) => item.id === record.id)
	}
	// 删减记录
	const delAllRecord = () => {
		selectedData.value = []
	}
	// 点击树查询
	const treeSelect = (selectedKeys) => {
		if (selectedKeys.length > 0) {
			searchFormState.orgId = selectedKeys.toString()
		} else {
			delete searchFormState.orgId
		}
		loadData()
	}
	// 确定
	const handleOk = () => {
		const value = []
		selectedData.value.forEach((item) => {
			const obj = {
				id: item.id,
				name: item.name,
				account: item.account
			}
			value.push(obj)
		})
		// 判断是否做数据的转换为工作流需要的
		if (dataIsConverterFlw) {
			emit('onBack', outDataConverter(value))
		} else {
			emit('onBack', value)
		}
		handleClose()
	}
	const handleClose = () => {
		searchFormState = reactive({})
		visible = false
	}

	// 数据进入后转换
	const goDataConverter = (data) => {
		const resultData = []
		if (data.length > 0) {
			const values = data[0].value.split(',')
			if (JSON.stringify(values) !== '[""]') {
				for (let i = 0; i < values.length; i++) {
					resultData.push(values[i])
				}
			}
		}
		return resultData
	}
	// 数据出口转换器
	const outDataConverter = (data) => {
		const obj = {}
		let label = ''
		let value = ''
		for (let i = 0; i < data.length; i++) {
			if (data.length === i + 1) {
				label = label + data[i].name
				value = value + data[i].id
			} else {
				label = label + data[i].name + ','
				value = value + data[i].id + ','
			}
		}
		obj.key = 'POSITION'
		obj.label = label
		obj.value = value
		obj.extJson = ''
		return obj
	}
	defineExpose({
		showPosPlusModal
	})
</script>

<style lang="less" scoped>
	.selectorTreeDiv {
		max-height: 500px;
		overflow: auto;
	}
	.cardTag {
		margin-left: 10px;
	}
	.primarySele {
		margin-right: 10px;
	}
	.ant-form-item {
		margin-bottom: 0 !important;
	}
	.pos-table {
		overflow: auto;
		max-height: 450px;
	}
</style>
