<template>
	<a-card :bordered="false">
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-row :gutter="16">
				<a-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
					<a-form-item label="生成类型：">
						<a-tag :color="genTypeMap[formData.genType]?.color || 'blue'">{{ genTypeMap[formData.genType]?.label || formData.genType }}</a-tag>
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
					<a-form-item label="选择主表或表名称搜索：" name="dbTable">
						<a-select
							v-model:value="formData.dbTable"
							:options="tableList"
							showSearch
							class="xn-wd"
							placeholder="请选择主表"
							@select="selectTableColumnsData(formData.dbTable, false)"
						/>
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
					<a-form-item label="选择主键：" name="dbTableKey">
						<a-select
							v-model:value="formData.dbTableKey"
							:options="tableColumns"
							class="xn-wd"
							placeholder="选择主键"
						/>
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
					<a-form-item label="表前缀移除：" name="tablePrefix">
						<a-radio-group
							v-model:value="formData.tablePrefix"
							:options="tablePrefixOptions"
							@change="tablePrefixChange"
						/>
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
					<a-form-item name="generateType">
						<template #label>
							<a-tooltip>
								<template #title>注：移动端代码生成目前只支持【压缩包】方式。</template>
								<question-circle-outlined />
								生成方式：
							</a-tooltip>
						</template>
						<a-radio-group v-model:value="formData.generateType" :options="generateTypeOptions" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
					<a-form-item label="所属模块：" name="module">
						<a-select
							v-model:value="formData.module"
							:options="moduleOptions"
							class="xn-wd"
							placeholder="请选择所属模块"
							@change="moduleChange(formData.module, false)"
						/>
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
					<a-form-item label="上级目录：" name="menuPid">
						<a-tree-select
							v-model:value="formData.menuPid"
							class="xn-wd"
							:dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
							placeholder="请选择上级目录"
							allow-clear
							tree-default-expand-all
							:tree-data="menuTreeData"
							:field-names="treeFieldNames"
							selectable="false"
							tree-line
						/>
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
					<a-form-item label="移动端所属模块：" name="mobileModule">
						<a-select
							v-model:value="formData.mobileModule"
							:options="mobileModuleList"
							class="xn-wd"
							placeholder="请选择移动端所属模块"
							allow-clear
						/>
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
					<a-form-item name="pluginName">
						<template #label>
							<a-tooltip>
								<template #title>
									不想把代码生成到本框架指定插件名称下，可以新建一个plugin模块，并在这里改为新的名字。
								</template>
								<question-circle-outlined />
							</a-tooltip>
							&nbsp; 插件名：
						</template>
						<a-input v-model:value="formData.pluginName" placeholder="请输入插件名" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
					<a-form-item label="包名：" name="packageName">
						<a-input v-model:value="formData.packageName" placeholder="请输入包名" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
					<a-form-item name="moduleName">
						<template #label>
							<a-tooltip>
								<template #title> 代码模块名就是包名后面的代码包，例如：vip.xiaonuo.*，*代表此模块名。 </template>
								<question-circle-outlined />
							</a-tooltip>
							&nbsp; 模块名：
						</template>
						<a-input v-model:value="formData.moduleName" placeholder="请输入模块名" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
					<a-form-item label="功能名：" name="functionName">
						<a-input v-model:value="formData.functionName" placeholder="请输入功能名" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
					<a-form-item name="busName">
						<template #label>
							<a-tooltip>
								<template #title> 业务名是代码生成后，存放controller、service等代码的文件夹名称。 </template>
								<question-circle-outlined />
							</a-tooltip>
							&nbsp; 业务名：
						</template>
						<a-input v-model:value="formData.busName" placeholder="请输入业务名" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
					<a-form-item label="类名：" name="className">
						<a-input v-model:value="formData.className" placeholder="请输入类名" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
					<a-form-item label="作者：" name="authorName">
						<a-input v-model:value="formData.authorName" placeholder="请输入作者名" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
					<a-form-item name="formLayout">
						<template #label>
							<a-tooltip>
								<template #title> 垂直选项生成出来的前端表单代码为名称跟输入框是上下两行，反之水平则是一行。 </template>
								<question-circle-outlined />
							</a-tooltip>
							&nbsp; 表单布局：
						</template>
						<a-radio-group v-model:value="formData.formLayout" :options="formLayoutOptions" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
					<a-form-item name="gridWhether">
						<template #label>
							<a-tooltip>
								<template #title> 如果使用了栅格配置，即生成出来的前端代码，表单是一排两列，并非一排一列。 </template>
								<question-circle-outlined />
							</a-tooltip>
							&nbsp; 使用栅格：
						</template>
						<a-radio-group v-model:value="formData.gridWhether" :options="gridWhetherOptions" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
					<a-form-item label="排序：" name="sortCode">
						<a-input-number class="xn-wd" v-model:value="formData.sortCode" :max="100" />
					</a-form-item>
				</a-col>
			</a-row>
			<!-- 树表类型：树父级字段、树显示字段 -->
			<template v-if="formData.genType === 'TREE' || formData.genType === 'LEFT_TREE_TABLE'">
				<a-divider>{{ formData.genType === 'LEFT_TREE_TABLE' ? '左树配置' : '树配置' }}</a-divider>
				<a-row :gutter="16">
					<a-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
						<a-form-item label="树父级字段" name="treeParentField" :rules="[{ required: true, message: '请选择树父级字段' }]">
							<a-select v-model:value="formData.treeParentField" placeholder="请选择树父级字段"
								:options="tableColumnList.map(c => ({label: c.columnName + '(' + c.columnRemark + ')', value: c.columnName}))" />
						</a-form-item>
					</a-col>
					<a-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
						<a-form-item label="树显示字段" name="treeNameField" :rules="[{ required: true, message: '请选择树显示字段' }]">
							<a-select v-model:value="formData.treeNameField" placeholder="请选择树显示名称字段"
								:options="tableColumnList.map(c => ({label: c.columnName + '(' + c.columnRemark + ')', value: c.columnName}))" />
						</a-form-item>
					</a-col>
				</a-row>
			</template>
			<!-- 双表类型：子表配置 -->
			<template v-if="formData.genType === 'LEFT_TREE_TABLE' || formData.genType === 'MASTER_DETAIL'">
				<a-divider>{{ formData.genType === 'LEFT_TREE_TABLE' ? '右表配置' : '子表配置' }}</a-divider>
				<a-row :gutter="16">
					<a-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
						<a-form-item label="子表" name="subDbTable" :rules="[{ required: true, message: '请选择子表' }]">
							<a-select v-model:value="formData.subDbTable" placeholder="请选择子表" show-search option-filter-prop="label" @change="subTableChange"
								:options="tableList.map(t => ({label: t.label, value: t.value}))" />
						</a-form-item>
					</a-col>
					<a-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
						<a-form-item label="子表主键" name="subDbTableKey" :rules="[{ required: true, message: '请选择子表主键' }]">
							<a-select v-model:value="formData.subDbTableKey" placeholder="请选择子表主键"
								:options="subTableColumnList.map(c => ({label: c.columnName + '(' + c.columnRemark + ')', value: c.columnName}))" />
						</a-form-item>
					</a-col>
					<a-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
						<a-form-item label="关联外键" name="subForeignKey" :rules="[{ required: true, message: '请选择关联外键' }]">
							<a-select v-model:value="formData.subForeignKey" placeholder="请选择子表中关联主表的外键字段"
								:options="subTableColumnList.map(c => ({label: c.columnName + '(' + c.columnRemark + ')', value: c.columnName}))" />
						</a-form-item>
					</a-col>
					<a-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
						<a-form-item label="子表功能名" name="subFunctionName" :rules="[{ required: true, message: '请输入子表功能名' }]">
							<a-input v-model:value="formData.subFunctionName" placeholder="请输入子表功能名" />
						</a-form-item>
					</a-col>
					<a-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
						<a-form-item label="子表业务名" name="subBusName" :rules="[{ required: true, message: '请输入子表业务名' }]">
							<a-input v-model:value="formData.subBusName" placeholder="请输入子表业务名（英文小写）" />
						</a-form-item>
					</a-col>
					<a-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
						<a-form-item label="子表类名" name="subClassName" :rules="[{ required: true, message: '请输入子表类名' }]">
							<a-input v-model:value="formData.subClassName" placeholder="请输入子表类名（大驼峰）" />
						</a-form-item>
					</a-col>
				</a-row>
			</template>
		</a-form>
	</a-card>
</template>

<script setup name="genBasic">
	import { required, rules } from '@/utils/formRules'
	import genBasicApi from '@/api/gen/genBasicApi'
	// 声明组件自定义事件
	const emit = defineEmits(['successful'])
	const formRef = ref()
	// 表单数据
	const formData = ref({})
	const genTypeMap = {
		TABLE: { label: '单表格', color: 'blue' },
		TREE: { label: '单树表', color: 'green' },
		LEFT_TREE_TABLE: { label: '左树右表', color: 'orange' },
		MASTER_DETAIL: { label: '主子表', color: 'purple' }
	}
	// 定义
	const mobileModuleList = ref([])
	const tableList = ref([])
	const tableColumns = ref([])
	const tableColumnList = ref([])
	const subTableColumnList = ref([])
	const menuTreeData = ref([])
	const treeFieldNames = { children: 'children', label: 'title', value: 'id' }
	const submitLoading = ref(false)
	const moduleOptions = ref()
	const generateTypeOptions = ref([
		{
			label: '压缩包',
			value: 'ZIP'
		},
		{
			label: '项目内',
			value: 'PRO'
		}
	])
	const tablePrefixOptions = ref([
		{
			label: '移除',
			value: 'Y'
		},
		{
			label: '不移除',
			value: 'N'
		}
	])
	const formLayoutOptions = ref([
		{
			label: '垂直',
			value: 'vertical'
		},
		{
			label: '水平',
			value: 'horizontal'
		}
	])
	const gridWhetherOptions = ref([
		{
			label: '栅格布局',
			value: 'Y'
		},
		{
			label: '不使用',
			value: 'N'
		}
	])
	// 打开抽屉
	const onOpen = (record, genType) => {
		// 加载默认的模块
		genBasicApi.basicModuleSelector().then((data) => {
			if (data) {
				moduleOptions.value = data.map((item) => {
					return {
						label: item.name,
						value: item.id
					}
				})
			}
		})
		// 获取数据库中的所有表
		genBasicApi.basicTables().then((data) => {
			tableList.value = data.map((item) => {
				return {
					value: item['tableName'],
					label: `${item['tableRemark']}-${item['tableName']}`,
					tableRemark: item['tableRemark'] || item['tableName'],
					tableName: item['tableName'],
					tableColumns: []
				}
			})
			if (record) {
				const params = {
					id: record.id
				}
				submitLoading.value = true
				genBasicApi
					.basicDetail(params)
					.then((data) => {
						formData.value = data
						formData.value.genType = data.genType || genType || 'TABLE'
						// 让主键选中
						selectTableColumnsData(data.dbTable, true)
						// 让模块旁边的上级菜单选中
						moduleChange(data.module, true)
						// 如果有子表，加载子表字段
						if (data.subDbTable) {
							genBasicApi.basicTableColumns({ tableName: data.subDbTable }).then((res) => {
								subTableColumnList.value = res
							})
						}
					})
					.finally(() => {
						submitLoading.value = false
					})
			} else {
				formData.value = {
					genType: genType || 'TABLE',
					pluginName: 'snowy-plugin-biz',
					packageName: 'vip.xiaonuo',
					moduleName: 'biz',
					sortCode: 99,
					tablePrefix: 'Y',
					generateType: 'ZIP',
					formLayout: 'vertical',
					gridWhether: 'N'
				}
			}
		})

		// 获取移动端模块
		submitLoading.value = true
		genBasicApi
			.basicMobileModuleSelector()
			.then((data) => {
				mobileModuleList.value = data.map((item) => {
					return {
						value: item['id'],
						label: item['name']
					}
				})
			})
			.finally(() => {
				submitLoading.value = false
			})
	}
	// 默认要校验的
	const formRules = {
		pluginName: [required('请输入插件名')],
		moduleName: [required('请输入模块名')],
		tablePrefix: [required('请选择是否移除表前缀')],
		dbTable: [required('请选择主表')],
		dbTableKey: [required('请选择主表主键')],
		generateType: [required('请选择生成方式')],
		module: [required('请选择所属模块')],
		menuPid: [required('请选择上级目录')],
		functionName: [required('请输入功能名'), rules.horizontalChart],
		busName: [required('请输入业务名')],
		className: [required('请输入类名')],
		packageName: [required('请输入包名')],
		sortCode: [required('请选择排序')],
		formLayout: [required('请选择表单布局')],
		gridWhether: [required('请选择是否使用栅格')],
		authorName: [required('请输入作者名')]
	}
	// 选择模板的回调
	const moduleChange = (value, assign) => {
		if (!assign) {
			// 先去掉值
			formData.value.menuPid = undefined
		}
		// 加载默认的模块
		menuTreeData.value = [
			{
				id: '0',
				title: '顶级',
				menuType: 'CATALOG',
				children: []
			}
		]
		const param = {
			module: value
		}
		genBasicApi.basicMenuTreeSelector(param).then((data) => {
			if (data) {
				menuTreeData.value[0].children = traverseChildren(data)
			}
		})
	}
	// 遍历增加属性
	const traverseChildren = (data = []) => {
		// 递归遍历控件树
		const traverse = (array) => {
			array.forEach((element) => {
				if (element.menuType === 'CATALOG') {
					if (element.children) {
						traverse(element.children)
					}
				} else {
					// 设置不可用
					element.disabled = true
					element.selectable = false
				}
			})
		}
		traverse(data)
		return data
	}
	// 获取表字段
	const selectTableColumnsData = (tableName, assign) => {
		if (!assign) {
			formData.value.dbTableKey = undefined
			formFieldAssign(tableName)
		}
		// 通过这个 tableName 查到这个表下的字段
		const param = {
			tableName: tableName
		}
		genBasicApi.basicTableColumns(param).then((data) => {
			tableColumnList.value = data
			tableColumns.value = data.map((item) => {
				return {
					value: item['columnName'],
					label: item['columnRemark'] || item['columnName']
				}
			})
		})
	}

	// 子表选择变化
	const subTableChange = (tableName) => {
		formData.value.subDbTableKey = undefined
		formData.value.subForeignKey = undefined
		// 自动计算子表功能名、业务名、类名
		const subData = tableList.value.find((item) => item.value === tableName)
		if (subData) {
			formData.value.subFunctionName = formData.value.subFunctionName || subData.tableRemark
			const subTableNameHump = getTableNameToHump(subData.value)
			formData.value.subBusName = formData.value.subBusName || subTableNameHump.toLowerCase()
			formData.value.subClassName = formData.value.subClassName || getClassName(subData.value)
		}
		genBasicApi.basicTableColumns({ tableName }).then((res) => {
			subTableColumnList.value = res
		})
	}

	// 点击选择是否移除前缀
	const tablePrefixChange = () => {
		const tableName = formData.value.dbTable
		if (tableName) {
			const tableNameHump = getTableNameToHump(tableName)
			formData.value.busName = tableNameHump.toLowerCase()
		}
	}
	// 表单内设置默认的值
	const formFieldAssign = (value) => {
		const data = tableList.value.find((item) => item.value === value)
		formData.value.functionName = data.tableRemark
		const tableNameHump = getTableNameToHump(data.value)
		formData.value.busName = tableNameHump.toLowerCase()
		formData.value.className = getClassName(data.value)
	}
	// 获取数据库表的驼峰命名
	const getTableNameToHump = (tableName) => {
		if (tableName) {
			const arr = tableName.toLowerCase().split('_')
			if (formData.value.tablePrefix === 'Y') {
				arr.splice(0, 1)
			}
			for (let i = 0; i < arr.length; i++) {
				// charAt()方法得到第一个字母，slice()得到第二个字母以后的字符串
				arr[i] = arr[i].charAt(0).toUpperCase() + arr[i].slice(1)
			}
			return arr.join('')
		}
		return ''
	}
	// 获取数据库表的驼峰命名
	const getClassName = (tableName) => {
		if (tableName) {
			const arr = tableName.toLowerCase().split('_')
			for (let i = 0; i < arr.length; i++) {
				// charAt()方法得到第一个字母，slice()得到第二个字母以后的字符串
				arr[i] = arr[i].charAt(0).toUpperCase() + arr[i].slice(1)
			}
			return arr.join('')
		}
		return ''
	}
	// 验证并提交数据
	const onSubmit = () => {
		return new Promise((resolve, reject) => {
			formRef.value
				.validate()
				.then(() => {
					submitLoading.value = true
					genBasicApi
						.submitForm(formData.value, formData.value.id)
						.then((data) => {
							// 提交成功后触发成功事件，供父组件监听
							emit('successful', data)
							resolve(data)
						})
						.finally(() => {
							submitLoading.value = false
						})
				})
				.catch((err) => {
					reject(err)
				})
		})
	}
	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen,
		onSubmit
	})
</script>
<style scoped>
	.form-row {
		background-color: var(--item-hover-bg);
		margin-left: 0px !important;
	}
	.form-row-con {
		padding-bottom: 5px;
		padding-top: 5px;
		padding-left: 15px;
	}
</style>
