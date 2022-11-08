<template>
	<a-card :bordered="false">
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-row :gutter="16">
				<a-col :span="8">
					<a-form-item label="选择主表：" name="dbTable">
						<a-select
							v-model:value="formData.dbTable"
							:options="tableList"
							style="width: 100%"
							placeholder="请选择主表"
							@select="selectTableColumnsData(formData.dbTable, false)"
						>
						</a-select>
					</a-form-item>
				</a-col>
				<a-col :span="8">
					<a-form-item label="选择主键：" name="dbTableKey">
						<a-select
							v-model:value="formData.dbTableKey"
							:options="tableColumns"
							style="width: 100%"
							placeholder="选择主键"
						>
						</a-select>
					</a-form-item>
				</a-col>
				<a-col :span="8">
					<a-form-item label="表前缀移除：" name="tablePrefix">
						<a-radio-group v-model:value="formData.tablePrefix" :options="tablePrefixOptions" @change="tablePrefixChange"> </a-radio-group>
					</a-form-item>
				</a-col>
			</a-row>
			<a-row :gutter="16">
				<a-col :span="8">
					<a-form-item label="生成方式：" name="generateType">
						<a-radio-group v-model:value="formData.generateType" :options="generateTypeOptions"> </a-radio-group>
					</a-form-item>
				</a-col>
				<a-col :span="8">
					<a-form-item label="所属模块：" name="module">
						<a-select
							v-model:value="formData.module"
							:options="moduleOptions"
							style="width: 100%"
							placeholder="请选择所属模块"
							@change="moduleChange(formData.module, false)"
						>
						</a-select>
					</a-form-item>
				</a-col>
				<a-col :span="8">
					<a-form-item label="上级目录：" name="menuPid">
						<a-tree-select
							v-model:value="formData.menuPid"
							style="width: 100%"
							:dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
							placeholder="请选择上级目录"
							allow-clear
							tree-default-expand-all
							:tree-data="menuTreeData"
							:field-names="{
								children: 'children',
								label: 'title',
								value: 'id'
							}"
							selectable="false"
							tree-line
						></a-tree-select>
					</a-form-item>
				</a-col>
			</a-row>
			<a-row :gutter="16">
				<a-col :span="8">
					<a-form-item label="功能名：" name="functionName">
						<a-input v-model:value="formData.functionName" placeholder="请输入功能名" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="8">
					<a-form-item label="业务名：" name="busName">
						<a-input v-model:value="formData.busName" placeholder="请输入业务名" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="8">
					<a-form-item label="类名：" name="className">
						<a-input v-model:value="formData.className" placeholder="请输入类名" allow-clear />
					</a-form-item>
				</a-col>
			</a-row>
			<a-row :gutter="16">
				<a-col :span="8">
					<a-form-item label="表单布局：" name="formLayout">
						<a-radio-group v-model:value="formData.formLayout" :options="formLayoutOptions" />
					</a-form-item>
				</a-col>
				<a-col :span="8">
					<a-form-item label="使用栅格：" name="gridWhether">
						<a-radio-group v-model:value="formData.gridWhether" :options="gridWhetherOptions" />
					</a-form-item>
				</a-col>
				<a-col :span="8">
					<a-form-item label="排序：" name="sortCode">
						<a-slider v-model:value="formData.sortCode" :max="100" style="width: 100%"/>
					</a-form-item>
				</a-col>
			</a-row>
			<a-row :gutter="16">
				<a-col :span="8">
					<a-form-item label="作者：" name="authorName">
						<a-input v-model:value="formData.authorName" placeholder="请输入作者名" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="8" v-if="formData.generateType === 'BIZ'">
					<a-form-item label="包名：" name="packageName">
						<a-input v-model:value="formData.packageName" placeholder="请输入包名" allow-clear />
					</a-form-item>
				</a-col>
			</a-row>
		</a-form>
	</a-card>
</template>

<script setup name="genBasic">
	import { required } from '@/utils/formRules'
	import tool from '@/utils/tool'
	import genBasicApi from '@/api/gen/genBasicApi'
	const formRef = ref()
	// 表单数据
	const formData = ref({})
	// 定义
	const tableList = ref([])
	const tableColumns = ref([])
	const menuTreeData = ref([])
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
		},
	])
	// 打开抽屉
	const onOpen = (record) => {
		// 加载默认的模块
		moduleOptions.value = tool.data.get('MENU').map((item) => {
			return {
				label: item.name,
				value: item.id
			}
		})
		// 获取数据库中的所有表
		genBasicApi.basicTables().then((data) => {
			tableList.value = data.map((item) => {
				return {
					value: item['tableName'],
					label: `${item['tableRemark']}-${item['tableName']}`,
					tableRemark: item['tableRemark'] || item['tableName'],
					tableColumns: []
				}
			})
		})
		if (record) {
			const params = {
				id: record.id
			}
			submitLoading.value = true
			genBasicApi.basicDetail(params).then((data) => {
				formData.value = data
				// 让主键选中
				selectTableColumnsData(data.dbTable, true)
				// 让模块旁边的上级菜单选中
				moduleChange(data.module, true)
			}).finally(() => {
				submitLoading.value = false
			})
		} else {
			formData.value = {
				sortCode: 99,
				tablePrefix: 'Y',
				generateType: 'ZIP',
				packageName: 'vip.xiaonuo',
				formLayout: 'vertical',
				gridWhether: 'N'
			}
		}
	}
	// 默认要校验的
	const formRules = {
		tablePrefix: [required('请选择是否移除表前缀')],
		dbTable: [required('请选择主表')],
		dbTableKey: [required('请选择主表主键')],
		generateType: [required('请选择生成方式')],
		module: [required('请选择所属模块')],
		menuPid: [required('请选择上级目录')],
		functionName: [required('请输入功能名')],
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
		const menuTree = tool.data.get('MENU').find((item) => {
			if (item.id === value) {
				return item
			}
		})
		menuTreeData.value = [
			{
				id: '0',
				title: '顶级',
				menuType: 'CATALOG',
				children: traverseChildren(menuTree.children)
			}
		]
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
		}
		formFieldAssign(tableName)
		// 通过这个 tableName 查到这个表下的字段
		const param = {
			tableName: tableName
		}
		genBasicApi.basicTableColumns(param).then((data) => {
			tableColumns.value = data.map((item) => {
				return {
					value: item['columnName'],
					label: item['columnRemark'] || item['columnName']
				}
			})
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
		return new Promise((resolve,reject) => {
			formRef.value
				.validate()
				.then(() => {
					submitLoading.value = true
					genBasicApi.submitForm(formData.value, !formData.value.id).then((data) => {
						resolve(data)
					}).finally(() => {
						submitLoading.value = false
					})
				}).catch((err) => {
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
	.childAddButton {
		margin-bottom: 10px;
	}
	.form-row {
		background-color: var(--item-hover-bg);
		margin-left: 0px !important;
	}
	.form-row-con {
		padding-bottom: 5px;
		padding-top: 5px;
		padding-left: 15px;
	}
	.form-div {
		padding-top: 10px;
	}
</style>
