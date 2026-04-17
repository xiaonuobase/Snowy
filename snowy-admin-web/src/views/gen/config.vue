<template>
	<a-card :bordered="false">
		<!-- 双表类型：使用tabs展示主表和子表 -->
		<template v-if="isDualTable">
			<a-tabs v-model:activeKey="activeTabKey">
				<a-tab-pane key="MAIN" :tab="mainTabLabel">
					<a-alert
						v-if="currentGenType === 'LEFT_TREE_TABLE'"
						message="左树仅树名称字段支持配置列显示和查询，其余字段的列显示和查询已自动禁用"
						type="info"
						show-icon
						style="margin-bottom: 16px"
					/>
					<s-table
						ref="tableRef"
						:columns="columns"
						:data="loadDate"
						:expand-row-by-click="true"
						:showPagination="false"
						:scroll="{ x: 'max-content' }"
						bordered
					>
						<template #headerCell="{ title, column }">
							<template v-if="column.dataIndex === 'whetherRequired'">
								<a-tooltip>
									<template #title> 非增改字段不可选择必填 </template>
									<question-circle-outlined />&nbsp; {{ title }}
								</a-tooltip>
							</template>
							<template v-if="column.dataIndex === 'whetherUnique'">
								<a-tooltip>
									<template #title> 非必填字段不可选择唯一 </template>
									<question-circle-outlined />&nbsp; {{ title }}
								</a-tooltip>
							</template>
						</template>
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
								<span v-if="isTreeParentField(record)">树选择</span>
								<a-select
									v-else
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
								<a-checkbox v-model:checked="record.whetherTable" @change="whetherTableChange(record)" :disabled="isTreeParentField(record) || isLeftTreeNonNameField(record)" />
							</template>
							<template v-if="column.dataIndex === 'whetherRetract'">
								<a-checkbox v-model:checked="record.whetherRetract" :disabled="!record.whetherTable || isTreeParentField(record) || isLeftTreeNonNameField(record)" />
							</template>
							<template v-if="column.dataIndex === 'whetherAddUpdate'">
								<a-checkbox
									v-model:checked="record.whetherAddUpdate"
									@change="whetherAddUpdateChange(record)"
									:disabled="toFieldEstimate(record) || isTreeParentField(record)"
								/>
							</template>
							<template v-if="column.dataIndex === 'whetherRequired'">
								<a-checkbox
									v-model:checked="record.whetherRequired"
									@change="whetherRequiredChange(record)"
									:disabled="toFieldEstimate(record) || !record.whetherAddUpdate || isTreeParentField(record)"
								/>
							</template>
							<template v-if="column.dataIndex === 'whetherUnique'">
								<a-checkbox
									v-model:checked="record.whetherUnique"
									:disabled="toFieldEstimate(record) || !record.whetherAddUpdate || !record.whetherRequired || isTreeParentField(record)"
								/>
							</template>
							<template v-if="column.dataIndex === 'queryWhether'">
								<a-switch v-model:checked="record.queryWhether" :disabled="toQueryWhetherDisabled(record) || isTreeParentField(record) || isLeftTreeNonNameField(record)" />
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
				</a-tab-pane>
				<a-tab-pane key="SUB" :tab="subTabLabel">
					<a-alert
						v-if="currentGenType === 'LEFT_TREE_TABLE'"
						message="右表外键字段默认渲染为树选择器，关联左树数据，用户可通过下拉树直接选择所属节点，无需手动输入ID"
						type="info"
						show-icon
						style="margin-bottom: 16px"
					/>
					<a-alert
						v-if="currentGenType === 'MASTER_DETAIL'"
						message="子表外键字段由系统自动填充（取当前主表记录ID），无需在列表展示或表单中手动输入，相关配置项已锁定"
						type="info"
						show-icon
						style="margin-bottom: 16px"
					/>
					<s-table
						ref="subTableRef"
						:columns="columns"
						:data="loadSubDate"
						:expand-row-by-click="true"
						:showPagination="false"
						:scroll="{ x: 'max-content' }"
						bordered
					>
						<template #headerCell="{ title, column }">
							<template v-if="column.dataIndex === 'whetherRequired'">
								<a-tooltip>
									<template #title> 非增改字段不可选择必填 </template>
									<question-circle-outlined />&nbsp; {{ title }}
								</a-tooltip>
							</template>
							<template v-if="column.dataIndex === 'whetherUnique'">
								<a-tooltip>
									<template #title> 非必填字段不可选择唯一 </template>
									<question-circle-outlined />&nbsp; {{ title }}
								</a-tooltip>
							</template>
						</template>
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
								<span v-if="isTreeParentField(record)">树选择</span>
								<span v-else-if="isSubForeignKeyField(record) && currentGenType === 'LEFT_TREE_TABLE'">树选择</span>
								<span v-else-if="isSubForeignKeyField(record) && currentGenType === 'MASTER_DETAIL'">自动填充</span>
								<a-select
									v-else
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
								<a-checkbox v-model:checked="record.whetherTable" @change="whetherTableChange(record)" :disabled="isTreeParentField(record) || isSubForeignKeyField(record)" />
							</template>
							<template v-if="column.dataIndex === 'whetherRetract'">
								<a-checkbox v-model:checked="record.whetherRetract" :disabled="!record.whetherTable || isTreeParentField(record) || isSubForeignKeyField(record)" />
							</template>
							<template v-if="column.dataIndex === 'whetherAddUpdate'">
								<a-checkbox
									v-model:checked="record.whetherAddUpdate"
									@change="whetherAddUpdateChange(record)"
									:disabled="toFieldEstimate(record) || isTreeParentField(record) || isSubForeignKeyField(record)"
								/>
							</template>
							<template v-if="column.dataIndex === 'whetherRequired'">
								<a-checkbox
									v-model:checked="record.whetherRequired"
									@change="whetherRequiredChange(record)"
									:disabled="toFieldEstimate(record) || !record.whetherAddUpdate || isTreeParentField(record) || isSubForeignKeyField(record)"
								/>
							</template>
							<template v-if="column.dataIndex === 'whetherUnique'">
								<a-checkbox
									v-model:checked="record.whetherUnique"
									:disabled="toFieldEstimate(record) || !record.whetherAddUpdate || !record.whetherRequired || isTreeParentField(record) || isSubForeignKeyField(record)"
								/>
							</template>
							<template v-if="column.dataIndex === 'queryWhether'">
								<a-switch v-model:checked="record.queryWhether" :disabled="toQueryWhetherDisabled(record) || isTreeParentField(record) || isSubForeignKeyField(record)" />
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
				</a-tab-pane>
			</a-tabs>
		</template>
		<!-- 单表类型：直接展示 -->
		<template v-else>
			<s-table
				ref="tableRef"
				:columns="columns"
				:data="loadDate"
				:expand-row-by-click="true"
				:showPagination="false"
				:scroll="{ x: 'max-content' }"
				bordered
			>
				<template #headerCell="{ title, column }">
					<template v-if="column.dataIndex === 'whetherRequired'">
						<a-tooltip>
							<template #title> 非增改字段不可选择必填 </template>
							<question-circle-outlined />&nbsp; {{ title }}
						</a-tooltip>
					</template>
					<template v-if="column.dataIndex === 'whetherUnique'">
						<a-tooltip>
							<template #title> 非必填字段不可选择唯一 </template>
							<question-circle-outlined />&nbsp; {{ title }}
						</a-tooltip>
					</template>
				</template>
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
						<span v-if="isTreeParentField(record)">树选择</span>
						<a-select
							v-else
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
						<a-checkbox v-model:checked="record.whetherTable" @change="whetherTableChange(record)" :disabled="isTreeParentField(record)" />
					</template>
					<template v-if="column.dataIndex === 'whetherRetract'">
						<a-checkbox v-model:checked="record.whetherRetract" :disabled="!record.whetherTable || isTreeParentField(record)" />
					</template>
					<template v-if="column.dataIndex === 'whetherAddUpdate'">
						<a-checkbox
							v-model:checked="record.whetherAddUpdate"
							@change="whetherAddUpdateChange(record)"
							:disabled="toFieldEstimate(record) || isTreeParentField(record)"
						/>
					</template>
					<template v-if="column.dataIndex === 'whetherRequired'">
						<a-checkbox
							v-model:checked="record.whetherRequired"
							@change="whetherRequiredChange(record)"
							:disabled="toFieldEstimate(record) || !record.whetherAddUpdate || isTreeParentField(record)"
						/>
					</template>
					<template v-if="column.dataIndex === 'whetherUnique'">
						<a-checkbox
							v-model:checked="record.whetherUnique"
							:disabled="toFieldEstimate(record) || !record.whetherAddUpdate || !record.whetherRequired || isTreeParentField(record)"
						/>
					</template>
					<template v-if="column.dataIndex === 'queryWhether'">
						<a-switch v-model:checked="record.queryWhether" :disabled="toQueryWhetherDisabled(record) || isTreeParentField(record)" />
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
		</template>
	</a-card>
</template>

<script setup name="genConfig">
	import { computed } from 'vue'
	import tool from '@/utils/tool'
	import genConfigApi from '@/api/gen/genConfigApi'
	import { cloneDeep } from 'lodash-es'

	const tableRef = ref()
	const subTableRef = ref()
	const recordData = ref()
	const tableData = ref()
	const subTableData = ref()
	const currentGenType = ref('TABLE')
	const activeTabKey = ref('MAIN')

	const isDualTable = computed(() => {
		return currentGenType.value === 'LEFT_TREE_TABLE' || currentGenType.value === 'MASTER_DETAIL'
	})

	const mainTabLabel = computed(() => {
		return currentGenType.value === 'LEFT_TREE_TABLE' ? '左树字段配置' : '主表字段配置'
	})
	const subTabLabel = computed(() => {
		return currentGenType.value === 'LEFT_TREE_TABLE' ? '右表字段配置' : '子表字段配置'
	})

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
			dataIndex: 'dictTypeCode'
		},
		{
			title: '列表显示',
			align: 'center',
			dataIndex: 'whetherTable'
		},
		{
			title: '列省略',
			align: 'center',
			dataIndex: 'whetherRetract'
		},
		{
			title: '增改',
			align: 'center',
			dataIndex: 'whetherAddUpdate'
		},
		{
			title: '必填',
			align: 'center',
			dataIndex: 'whetherRequired'
		},
		{
			title: '唯一',
			align: 'center',
			dataIndex: 'whetherUnique'
		},
		{
			title: '查询',
			align: 'center',
			dataIndex: 'queryWhether'
		},
		{
			title: '查询方式',
			align: 'center',
			dataIndex: 'queryType'
		}
	]
	const onOpen = (record, genType) => {
		recordData.value = record
		currentGenType.value = genType || (record && record.genType) || 'TABLE'
		activeTabKey.value = 'MAIN'
		nextTick(() => {
			tableRef.value.refresh()
			if (isDualTable.value && subTableRef.value) {
				subTableRef.value.refresh()
			}
		})
	}
	// 处理配置列表数据的通用方法
	const processConfigData = (data, tableType) => {
		let processedData = JSON.parse(JSON.stringify(data))
		let deleteIndex = []
		processedData.forEach((item, index) => {
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
			// 树父级字段强制开启增改、必填，关闭列表显示和查询（通过点击树过滤）
			if (isTreeParentField(item)) {
				item.whetherAddUpdate = true
				item.whetherRequired = true
				item.queryWhether = false
				item.queryType = null
				item.whetherTable = false
				item.whetherRetract = false
				item.whetherUnique = false
			}
			// 左树右表类型下，左树中非树名称字段禁用列显示和查询
			if (tableType === 'MAIN' && isLeftTreeNonNameField(item) && !isTreeParentField(item)) {
				item.whetherTable = false
				item.whetherRetract = false
				item.queryWhether = false
				item.queryType = null
			}
			// 左树右表类型下，右表的关联外键字段与树父级字段行为一致
			if (tableType === 'SUB' && isSubForeignKeyField(item)) {
				if (currentGenType.value === 'LEFT_TREE_TABLE') {
					item.whetherAddUpdate = true
					item.whetherRequired = true
					item.queryWhether = false
					item.queryType = null
					item.whetherTable = false
					item.whetherRetract = false
					item.whetherUnique = false
				} else if (currentGenType.value === 'MASTER_DETAIL') {
					item.whetherAddUpdate = false
					item.whetherRequired = false
					item.queryWhether = false
					item.queryType = null
					item.whetherTable = false
					item.whetherRetract = false
					item.whetherUnique = false
				}
			}
		})
		if (deleteIndex.length > 0) {
			// 倒序删除，避免索引偏移问题
			for (let i = deleteIndex.length - 1; i >= 0; i--) {
				processedData.splice(deleteIndex[i], 1)
			}
		}
		return processedData
	}
	// 表格查询 返回 Promise 对象（主表）
	const loadDate = (parameter) => {
		if (recordData.value) {
			parameter.basicId = recordData.value.id
			if (isDualTable.value) {
				parameter.tableType = 'MAIN'
			}
			return genConfigApi.configList(parameter).then((data) => {
				tableData.value = processConfigData(data, 'MAIN')
				return tableData.value
			})
		} else {
			return new Promise((resolve, reject) => {
				resolve([])
			})
		}
	}
	// 子表数据加载
	const loadSubDate = (parameter) => {
		if (recordData.value) {
			parameter.basicId = recordData.value.id
			parameter.tableType = 'SUB'
			return genConfigApi.configList(parameter).then((data) => {
				subTableData.value = processConfigData(data, 'SUB')
				return subTableData.value
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
		},
		{
			label: '人员选择器(单选)',
			value: 'userSelector'
		},
		{
			label: '人员选择器(多选)',
			value: 'userSelectorMulti'
		},
		{
			label: '机构选择器(单选)',
			value: 'orgSelector'
		},
		{
			label: '机构选择器(多选)',
			value: 'orgSelectorMulti'
		},
		{
			label: '职位选择器(单选)',
			value: 'positionSelector'
		},
		{
			label: '职位选择器(多选)',
			value: 'positionSelectorMulti'
		},
		{
			label: '用户组选择器(单选)',
			value: 'groupSelector'
		},
		{
			label: '用户组选择器(多选)',
			value: 'groupSelectorMulti'
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
	const emit = defineEmits({ successful: null, close: null })
	// 判断是否为树父级字段（树表类型下该字段的增改、必填、查询强制开启且不可编辑）
	const isTreeParentField = (record) => {
		if (currentGenType.value !== 'TREE' && currentGenType.value !== 'LEFT_TREE_TABLE') {
			return false
		}
		return recordData.value && record.fieldName === recordData.value.treeParentField
	}
	// 判断是否为左树中非树名称字段（LEFT_TREE_TABLE类型下，左树仅树名称字段允许配置列显示和查询，其余字段禁用）
	const isLeftTreeNonNameField = (record) => {
		if (currentGenType.value !== 'LEFT_TREE_TABLE') {
			return false
		}
		return recordData.value && record.fieldName !== recordData.value.treeNameField
	}
	// 判断是否为右表关联外键字段（左树右表类型下，右表的外键字段与树父级字段行为一致）
	const isSubForeignKeyField = (record) => {
		if (currentGenType.value !== 'LEFT_TREE_TABLE' && currentGenType.value !== 'MASTER_DETAIL') {
			return false
		}
		return recordData.value && record.fieldName === recordData.value.subForeignKey
	}
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
	// 判断是否为选择器类型
	const isSelectorType = (effectType) => {
		return ['userSelector', 'userSelectorMulti', 'orgSelector', 'orgSelectorMulti',
			'positionSelector', 'positionSelectorMulti', 'groupSelector', 'groupSelectorMulti'].includes(effectType)
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
			record.queryWhether = false
			record.queryType = null
		}
		// 富文本只能模糊包含跟模糊不包含
		if (record.effectType === 'editor') {
			record.whetherTable = false
			record.whetherRetract = false
			record.queryWhether = false
			record.queryType = null
		}
		// 选择器类型不可设置查询
		if (isSelectorType(record.effectType)) {
			record.queryWhether = false
			record.queryType = null
			record.dictTypeCode = null
		}
	}
	// 点击列表显示，处理
	const whetherTableChange = (record) => {
		// 如果去除了勾选，清理部分数据
		if (!record.whetherTable) {
			record.queryWhether = false
			record.queryType = null
		}
	}
	// 是否增改选择触发
	const whetherAddUpdateChange = (element) => {
		if (!element.whetherAddUpdate) {
			element.whetherRequired = false
			element.whetherUnique = false
		}
	}
	// 是否必填选择触发
	const whetherRequiredChange = (element) => {
		if (!element.whetherRequired) {
			element.whetherUnique = false
		}
	}
	// 查询条件是否可用
	const toQueryWhetherDisabled = (record) => {
		// 去掉了列表显示、图片上传、文件上传、选择器类型是不让生成搜索的
		return !record.whetherTable || record.effectType === 'imageUpload' || record.effectType === 'fileUpload' || isSelectorType(record.effectType)
	}
	// 实体类型选择触发
	const fieldJavaTypeChange = (record) => {
		if (record.fieldJavaType === 'Date') {
			record.effectType = 'datepicker'
		}
	}
	// 转换提交数据的通用方法
	const convertSubmitData = (data) => {
		let submitParam = cloneDeep(data)
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
		return { submitParam, errStatus }
	}
	// 提交
	const onSubmit = (recordData) => {
		// 主表数据
		const mainResult = convertSubmitData(tableData.value || [])
		// 子表数据（双表类型才有）
		let subResult = { submitParam: [], errStatus: 100 }
		if (isDualTable.value && subTableData.value) {
			subResult = convertSubmitData(subTableData.value)
		}

		return new Promise((resolve, reject) => {
			if (mainResult.errStatus > 100 || subResult.errStatus > 100) {
				reject('校验失败，请选择对应的下拉框选项')
				return
			}
			// 合并主表和子表数据一起提交
			const allData = [...mainResult.submitParam, ...subResult.submitParam]
			genConfigApi
				.configEditBatch(allData)
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
