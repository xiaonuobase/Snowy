<template>
	<a-card :bordered="false">
		<s-table
			ref="tableRef"
			:columns="columns"
			:data="loadDate"
			:expand-row-by-click="true"
			:showPagination="false"
			bordered
		>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'fieldRemark'">
					<a-input v-model:value="record.fieldRemark" />
				</template>
				<template v-if="column.dataIndex === 'fieldJavaType'">
					<a-select
						class="xn-wd"
						v-model:value="record.fieldJavaType"
						:options="fieldJavaTypeOptions"
						placeholder="请选择"
						:disabled="toCommonFieldEstimate(record)"
						@change="fieldJavaTypeChange(record)"
					/>
				</template>
				<template v-if="column.dataIndex === 'effectType'">
					<a-select
						class="xn-wd"
						v-model:value="record.effectType"
						:options="effectTypeOptions"
						placeholder="请选择"
						:disabled="toCommonFieldEstimate(record) || toFieldSelectEstimate(record)"
						@change="effectTypeChange(record)"
					/>
				</template>
				<template v-if="column.dataIndex === 'dictTypeCode'">
					<a-select
						v-if="record.effectType === 'radio' || record.effectType === 'select' || record.effectType === 'checkbox'"
						class="xn-wd"
						v-model:value="record.dictTypeCode"
						:options="dictTypeCodeOptions"
						placeholder="请选择字典"
					/>
					<span v-else>无</span>
				</template>
				<template v-if="column.dataIndex === 'whetherTable'">
					<a-checkbox v-model:checked="record.whetherTable" @change="whetherTableChange(record)" />
				</template>
				<template v-if="column.dataIndex === 'whetherRetract'">
					<a-checkbox v-model:checked="record.whetherRetract" :disabled="!record.whetherTable" />
				</template>
				<template v-if="column.dataIndex === 'whetherAddUpdate'">
					<a-checkbox v-model:checked="record.whetherAddUpdate" :disabled="toFieldEstimate(record)" />
				</template>
				<template v-if="column.dataIndex === 'whetherRequired'">
					<a-checkbox
						v-model:checked="record.whetherRequired"
						:disabled="toFieldEstimate(record) || !record.whetherAddUpdate"
					/>
				</template>
				<template v-if="column.dataIndex === 'queryWhether'">
					<a-switch v-model:checked="record.queryWhether" :disabled="toQueryWhetherDisabled(record)" />
				</template>
				<template v-if="column.dataIndex === 'queryType'">
					<a-select
						v-if="record.queryWhether === true && record.effectType !== 'datepicker'"
						class="xn-wd"
						v-model:value="record.queryType"
						:options="queryTypeOptions"
						placeholder="请选择"
					/>
					<span v-else-if="record.effectType === 'datepicker' && record.queryWhether === true">时间段</span>
					<span v-else>无</span>
				</template>
			</template>
		</s-table>
	</a-card>
</template>

<script setup name="genConfig">
	import tool from '@/utils/tool'
	import genConfigApi from '@/api/gen/genConfigApi'
	import { cloneDeep } from 'lodash-es'

	const tableRef = ref()
	const recordData = ref()
	const tableData = ref()

	const columns = [
		{
			title: '字段',
			align: 'center',
			dataIndex: 'fieldName',
			ellipsis: true
		},
		{
			title: '注释',
			align: 'center',
			dataIndex: 'fieldRemark',
			ellipsis: true
		},
		{
			title: '类型',
			align: 'center',
			dataIndex: 'fieldType',
			ellipsis: true
		},
		{
			title: '实体类型',
			align: 'center',
			dataIndex: 'fieldJavaType',
			ellipsis: true
		},
		{
			title: '作用类型',
			align: 'center',
			dataIndex: 'effectType',
			ellipsis: true
		},
		{
			title: '字典',
			align: 'center',
			dataIndex: 'dictTypeCode',
			width: 140
		},
		{
			title: '列表显示',
			align: 'center',
			dataIndex: 'whetherTable',
			width: 80
		},
		{
			title: '列省略',
			align: 'center',
			dataIndex: 'whetherRetract',
			width: 80
		},
		{
			title: '增改',
			align: 'center',
			dataIndex: 'whetherAddUpdate',
			width: 80
		},
		{
			title: '必填',
			align: 'center',
			dataIndex: 'whetherRequired',
			width: 80
		},
		{
			title: '查询',
			align: 'center',
			dataIndex: 'queryWhether',
			width: 80
		},
		{
			title: '查询方式',
			align: 'center',
			dataIndex: 'queryType'
		}
	]
	const onOpen = (record) => {
		recordData.value = record
		nextTick(() => {
			tableRef.value.refresh()
		})
	}
	// 表格查询 返回 Promise 对象
	const loadDate = (parameter) => {
		if (recordData.value) {
			parameter.basicId = recordData.value.id
			return genConfigApi.configList(parameter).then((data) => {
				tableData.value = JSON.parse(JSON.stringify(data))
				let deleteIndex = []
				tableData.value.forEach((item, index) => {
					for (const key in item) {
						if (item[key] === 'Y') {
							item[key] = true
						}
						if (item[key] === 'N') {
							item[key] = false
						}
					}
					// 如果是主键，我们不提供主键的配置，也用不到
					if (item.isTableKey) {
						deleteIndex.push(index)
					}
					// 去掉删除标识
					if (item.fieldName.toLowerCase().indexOf('delete_flag') > -1) {
						deleteIndex.push(index)
					}
					// 让默认的变成设置的
					fieldJavaTypeChange(item)
				})
				if (deleteIndex) {
					deleteIndex.forEach((item, index) => {
						if (index > 0) {
							item = item - 1
						}
						delete tableData.value.splice(item, 1)
					})
				}
				return tableData.value
			})
		} else {
			return new Promise((resolve, reject) => {
				resolve([])
			})
		}
	}
	// 实体类型
	const fieldJavaTypeOptions = ref([
		{
			label: 'Integer',
			value: 'Integer'
		},
		{
			label: 'Long',
			value: 'Long'
		},
		{
			label: 'String',
			value: 'String'
		},
		{
			label: 'Boolean',
			value: 'Boolean'
		},
		{
			label: 'Float',
			value: 'Float'
		},
		{
			label: 'Double',
			value: 'Double'
		},
		{
			label: 'Date',
			value: 'Date'
		},
		{
			label: 'BigDecimal',
			value: 'BigDecimal'
		}
	])
	// 类型
	const effectTypeOptions = ref([
		{
			label: '输入框',
			value: 'input'
		},
		{
			label: '文本框',
			value: 'textarea'
		},
		{
			label: '下拉框',
			value: 'select'
		},
		{
			label: '单选框',
			value: 'radio'
		},
		{
			label: '复选框',
			value: 'checkbox'
		},
		{
			label: '日期选择器',
			value: 'datepicker'
		},
		{
			label: '时间选择器',
			value: 'timepicker'
		},
		{
			label: '数字输入框',
			value: 'inputNumber'
		},
		{
			label: '滑动数字条',
			value: 'slider'
		},
		{
			label: '图片上传',
			value: 'imageUpload'
		},
		{
			label: '文件上传',
			value: 'fileUpload'
		},
		{
			label: '富文本',
			value: 'editor'
		}
	])
	// 字典数据
	const dictTypeCodeOptions = tool.dictDataAll().map((item) => {
		return {
			label: item.name,
			value: item.dictValue
		}
	})
	// 查询方式
	const queryTypeOptions = ref([
		{
			label: '模糊包含',
			value: 'like'
		},
		{
			label: '模糊不包含',
			value: 'notLike'
		},
		{
			label: '等于',
			value: 'eq'
		},
		{
			label: '不等于',
			value: 'ne'
		},
		{
			label: '大于',
			value: 'gt'
		},
		{
			label: '大于等于',
			value: 'ge'
		},
		{
			label: '小于',
			value: 'lt'
		},
		{
			label: '小于等于',
			value: 'le'
		}
	])
	const emit = defineEmits({ successful: null }, { close: null })
	const toFieldEstimate = (data) => {
		if (
			data.fieldName.toLowerCase().indexOf('create_user') > -1 ||
			data.fieldName.toLowerCase().indexOf('create_time') > -1 ||
			data.fieldName.toLowerCase().indexOf('update_user') > -1 ||
			data.fieldName.toLowerCase().indexOf('update_time') > -1 ||
			data.fieldName.toLowerCase().indexOf('delete_flag') > -1 ||
			data.isTableKey === true
		) {
			return true
		}
		return false
	}
	// 通用字段是否可选
	const toCommonFieldEstimate = (record) => {
		if (
			record.fieldName.toLowerCase().indexOf('create_user') > -1 ||
			record.fieldName.toLowerCase().indexOf('update_user') > -1
		) {
			return true
		}
		return false
	}
	// 设置该下拉框是否能选
	const toFieldSelectEstimate = (record) => {
		if (record.fieldJavaType === 'Date' && record.effectType === 'datepicker') {
			return true
		}
		return false
	}
	// 选择作用类型触发-这里只负责字段类型的赋值，至于禁用是每一个字段自己的
	const effectTypeChange = (record) => {
		// 图片跟文件不可设置查询跟查询方式
		if (record.effectType === 'imageUpload' || record.effectType === 'fileUpload') {
			record.queryWhether = 'N'
			record.queryType = null
		}
		// 富文本只能模糊包含跟模糊不包含
		if (record.effectType === 'editor') {
			record.whetherTable = false
			record.whetherRetract = false
			record.queryWhether = 'N'
			record.queryType = null
		}
	}
	// 点击列表显示，处理
	const whetherTableChange = (record) => {
		// 如果去除了勾选，清理部分数据
		if (!record.whetherTable) {
			record.queryWhether = 'N'
			record.queryType = null
		}
	}
	// 查询条件是否可用
	const toQueryWhetherDisabled = (record) => {
		// 去掉了列表显示、图片上传、文件上传是不让生成搜索的
		return !record.whetherTable || record.effectType === 'imageUpload' || record.effectType === 'fileUpload'
	}
	// 实体类型选择触发
	const fieldJavaTypeChange = (record) => {
		if (record.fieldJavaType === 'Date') {
			record.effectType = 'datepicker'
		}
	}
	// 提交
	const onSubmit = (recordData) => {
		let submitParam = cloneDeep(tableData.value)
		let errStatus = 100
		submitParam.forEach((item) => {
			// 必填那一项转换
			for (const key in item) {
				if (item[key] === true) {
					item[key] = 'Y'
				}
				if (item[key] === false) {
					item[key] = 'N'
				}
			}
			if (item.queryWhether === 'Y' && !item.queryType) {
				// 排除掉时间选择
				if (item.fieldJavaType !== 'Date' && item.effectType !== 'checkbox') {
					errStatus++
				}
			}
			if (
				(item.effectType === 'select' || item.effectType === 'radio' || item.effectType === 'checkbox') &&
				!item.dictTypeCode
			) {
				errStatus++
			}
		})
		return new Promise((resolve, reject) => {
			if (errStatus > 100) {
				reject('校验失败，请选择对应的下拉框选项')
				return
			}
			genConfigApi
				.configEditBatch(submitParam)
				.then((data) => {
					resolve(data)
				})
				.catch((err) => {
					reject(err)
				})
		})
	}
	// 抛出钩子
	defineExpose({
		onOpen,
		onSubmit
	})
</script>
<style scoped>
	.table-wrapper {
		margin-top: -16px !important;
	}
</style>
