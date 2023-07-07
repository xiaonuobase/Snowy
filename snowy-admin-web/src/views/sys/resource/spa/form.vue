<template>
	<xn-form-container
		:title="formData.id ? '编辑单页' : '增加单页'"
		:width="700"
		:visible="visible"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-alert style="margin-bottom: 10px" message="温馨提示：排序第一条为首页页面！" type="warning" closable />
			<a-row :gutter="16">
				<a-col :span="12">
					<a-form-item label="单页名称：" name="title">
						<a-input v-model:value="formData.title" placeholder="请输入单页名称" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="单页类型：" name="menuType">
						<a-radio-group
							v-model:value="formData.menuType"
							button-style="solid"
							:options="categoryOptions"
							option-type="button"
						/>
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item name="path">
						<template #label>
							<a-tooltip>
								<template #title>
									类型为内外链条时，输入https开头的链接即可（例：https://www.xiaonuo.vip）,正常路由前面必须有反斜杠！
								</template>
								<question-circle-outlined />
							</a-tooltip>
							&nbsp {{ formData.menuType === 'MENU' ? '路由地址' : 'https链接地址' }}：
						</template>
						<a-input v-model:value="formData.path" placeholder="请输入路由地址" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12" v-if="formData.menuType === 'MENU'">
					<a-form-item name="component">
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
				</a-col>
				<a-col :span="12" v-if="formData.menuType === 'MENU'">
					<a-form-item name="name">
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
				</a-col>
				<a-col :span="12">
					<a-form-item label="图标：" name="icon">
						<a-input
							v-model:value="formData.icon"
							style="width: calc(100% - 70px)"
							placeholder="请选择图标"
							allow-clear
						/>
						<a-button type="primary" @click="iconSelector.showIconModal(formData.icon)">选择</a-button>
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="排序:" name="sortCode">
						<a-input-number style="width: 100%" v-model:value="formData.sortCode" :max="100" />
					</a-form-item>
				</a-col>
			</a-row>
		</a-form>
		<template #footer>
			<a-button style="margin-right: 8px" @click="onClose">关闭</a-button>
			<a-button type="primary" :loading="submitLoading" @click="onSubmit">保存</a-button>
		</template>
		<Icon-selector ref="iconSelector" @iconCallBack="iconCallBack" />
	</xn-form-container>
</template>

<script setup name="spaForm">
	import { required } from '@/utils/formRules'
	import IconSelector from '@/components/Selector/iconSelector.vue'
	import spaApi from '@/api/sys/resource/spaApi'
	import tool from '@/utils/tool'
	// 默认是关闭状态
	let visible = $ref(false)
	const emit = defineEmits({ successful: null })
	const formRef = ref()
	const formData = ref({})
	const submitLoading = ref(false)
	const iconSelector = ref()
	// 打开抽屉
	const onOpen = (record) => {
		visible = true
		formData.value = {
			menuType: 'MENU',
			sortCode: 99
		}
		if (record) {
			formData.value = Object.assign({}, record)
		}
	}
	// 关闭抽屉
	const onClose = () => {
		formRef.value.resetFields()
		visible = false
	}

	// 默认要校验的
	const formRules = {
		title: [required('请输入菜单名称')],
		menuType: [required('请选择菜单类型')],
		path: [required('请输入路由地址')],
		name: [required('请输入组件中name属性')],
		module: [required('请选择模块')],
		component: [required('请输入组件地址')]
	}

	// 图标选择器回调
	const iconCallBack = (value) => {
		formData.value.icon = value
	}
	let categoryOptions = tool
		.dictList('MENU_TYPE')
		.filter((item) => {
			// 排除
			if (item.value !== 'CATALOG') {
				return item
			}
		})
		.map((item) => {
			return {
				value: item['value'],
				label: item['label'] + '页'
			}
		})

	// 验证并提交数据
	const onSubmit = () => {
		formRef.value.validate().then(() => {
			const param = parameterChanges(formData.value)
			submitLoading.value = true
			spaApi
				.submitForm(param, param.id)
				.then(() => {
					visible = false
					emit('successful')
				})
				.finally(() => {
					submitLoading.value = false
				})
		})
	}
	const parameterChanges = (data) => {
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
