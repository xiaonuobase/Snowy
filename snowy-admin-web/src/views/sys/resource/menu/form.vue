<template>
	<a-drawer
		:title="formData.id ? '编辑菜单' : '增加菜单'"
		:width="500"
		:visible="visible"
		:destroy-on-close="true"
		:body-style="{ paddingBottom: '80px' }"
		:footer-style="{ textAlign: 'right' }"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-form-item label="显示名称：" name="title">
				<a-input v-model:value="formData.title" placeholder="请输入显示名称" allow-clear />
			</a-form-item>
			<a-form-item label="菜单类型：" name="menuType">
				<a-radio-group
					v-model:value="formData.menuType"
					button-style="solid"
					:options="categoryOptions"
					option-type="button"
				>
				</a-radio-group>
			</a-form-item>
			<a-form-item label="上级菜单：" name="parentId">
				<a-tree-select
					v-model:value="formData.parentId"
					v-model:treeExpandedKeys="defaultExpandedKeys"
					style="width: 100%"
					:dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
					placeholder="请选择上级菜单"
					allow-clear
					tree-default-expand-all
					:tree-data="treeData"
					:field-names="{
						children: 'children',
						label: 'title',
						value: 'id'
					}"
					selectable="false"
					tree-line
					@change="parentChange(formData.parentId)"
				></a-tree-select>
			</a-form-item>
			<a-form-item v-if="formData.menuType !== 'CATALOG'" name="path">
				<template #label>
					<a-tooltip>
						<template #title>
							类型为内外链条时，输入https开头的链接即可（例：https://xiaonuo.vip）,正常路由前面必须有反斜杠！
						</template>
						<question-circle-outlined />
					</a-tooltip>
					&nbsp {{ formData.menuType === 'MENU' || formData.menuType === 'CATALOG' ? '路由地址' : 'https链接地址' }}：
				</template>
				<a-input v-model:value="formData.path" placeholder="请输入路由地址" allow-clear />
			</a-form-item>
			<a-form-item v-if="formData.menuType === 'MENU'" name="component">
				<template #label>
					<a-tooltip>
						<template #title> 按规范可设置为代码组件文件夹名称,注：首字母无反斜杠哦！ </template>
						<question-circle-outlined />
					</a-tooltip>
					&nbsp 组件地址：
				</template>
				<a-input
					v-model:value="formData.component"
					addon-before="src/views/"
					placeholder="请输入组件地址"
					allow-clear
				/>
			</a-form-item>
			<a-form-item v-if="formData.menuType === 'MENU'" name="name">
				<template #label>
					<a-tooltip>
						<template #title> 按规范可设置为代码组件文件夹名称,注：首字母无反斜杠哦！ </template>
						<question-circle-outlined />
					</a-tooltip>
					&nbsp 别名：
				</template>
				<a-input
					v-model:value="formData.name"
					addon-before="setup name="
					placeholder="请输入组件组件中name属性"
					allow-clear
				/>
			</a-form-item>
			<a-form-item label="图标：" name="icon">
				<a-input v-model:value="formData.icon" style="width: calc(100% - 70px)" placeholder="请选择图标" allow-clear />
				<a-button type="primary" @click="iconSelector.showIconModal(formData.icon)">选择</a-button>
			</a-form-item>
			<a-form-item label="排序:" name="sortCode">
				<a-slider v-model:value="formData.sortCode" :max="100" />
			</a-form-item>
		</a-form>
		<template #footer>
			<a-button style="margin-right: 8px" @click="onClose">关闭</a-button>
			<a-button type="primary" :loading="submitLoading" @click="onSubmit">保存</a-button>
		</template>
		<Icon-selector ref="iconSelector" @iconCallBack="iconCallBack" />
	</a-drawer>
</template>

<script setup>
	import { required, rules } from '@/utils/formRules'
	import SnowflakeId from 'snowflake-id'
	import tool from '@/utils/tool'
	import menuApi from '@/api/sys/resource/menuApi'
	import IconSelector from '@/components/Selector/iconSelector.vue'
	import { getCurrentInstance } from 'vue'
	// 默认是关闭状态
	let visible = $ref(false)
	const emit = defineEmits({ successful: null })
	const formRef = ref()
	const treeData = ref([])
	let iconSelector = ref()
	// 表单数据，也就是默认给一些数据
	const formData = ref({})
	// 默认展开的节点(顶级)
	const defaultExpandedKeys = ref([0])
	const submitLoading = ref(false)
	// 模块ID
	const moduleId = ref('')
	// 打开抽屉
	const onOpen = (record, module) => {
		moduleId.value = module
		visible = true
		formData.value = {
			menuType: 'MENU',
			sortCode: 99
		}
		if (record) {
			formData.value = Object.assign({}, record)
		}
		// 获取菜单树并加入顶级
		const treeParam = {
			module: module
		}
		menuApi.menuTreeSelector(treeParam).then((res) => {
			treeData.value = [
				{
					id: 0,
					parentId: '-1',
					title: '顶级',
					children: res
				}
			]
		})
	}
	// 关闭抽屉
	const onClose = () => {
		formRef.value.resetFields()
		visible = false
	}
	// 选择上级加载模块的选择框
	const parentChange = (value) => {
		if (value > 0) {
			// 执行接口去查询选择的上级是哪个模块，吧对应的也置为一样的
			const param = {
				id: value
			}
			menuApi.menuDetail(param).then((res) => {
				formData.value.module = res.module
			})
		} else {
			formData.value.module = null
		}
	}
	// 图标选择器回调
	const iconCallBack = (value) => {
		formData.value.icon = value
	}

	// 默认要校验的
	const formRules = {
		title: [required('请输入菜单名称')],
		parentId: [required('请选择上级菜单')],
		menuType: [required('请选择菜单类型')],
		path: [required('请输入路由地址')],
		name: [required('请输入组件中name属性')],
		module: [required('请选择模块')],
		component: [required('请输入组件地址')]
	}

	const { proxy } = getCurrentInstance()
	let categoryOptions = proxy.$TOOL.dictTypeList('MENU_TYPE').map((item) => {
		return {
			value: item['dictValue'],
			label: item['name']
		}
	})

	// 验证并提交数据
	const onSubmit = () => {
		formRef.value
			.validate()
			.then(() => {
				const param = parameterChanges(formData.value)
				submitLoading.value = true
				menuApi.submitForm(param, !param.id).then(() => {
					onClose()
					emit('successful')
				})
			}).finally(() => {
				submitLoading.value = false
			})
	}
	const parameterChanges = (data) => {
		// 每个都先增加一个模块ID
		data.module = moduleId.value
		// 如果是目录级菜单，他的path跟name我们在前端生成，无需使用着填写
		if (data.menuType !== 'MENU') {
			const snowflake = new SnowflakeId()
			const uuid = snowflake.generate()
			if (!data.path) {
				data.path = '/' + uuid
			}
			if (!data.name) {
				data.name = uuid
			}
		}
		if (!data.component) {
			return data
		}
		// 如果用户输入的组件path路径
		if (data.component.slice(0, 1) === '/') {
			data.component = data.component.slice(1)
		}
		return data
	}
	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>
