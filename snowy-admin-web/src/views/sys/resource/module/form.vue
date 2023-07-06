<template>
	<xn-form-container
		:title="formData.id ? '编辑模块' : '增加模块'"
		:width="550"
		:visible="visible"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-form-item label="模块名称：" name="title">
				<a-input v-model:value="formData.title" placeholder="请输入模块名称" allow-clear />
			</a-form-item>
			<a-form-item label="图标：" name="icon">
				<a-input v-model:value="formData.icon" style="width: calc(100% - 70px)" placeholder="请选择图标" allow-clear />
				<a-button type="primary" @click="iconSelector.showIconModal(formData.icon)">选择</a-button>
			</a-form-item>
			<a-form-item label="颜色：" name="color">
				<color-picker v-model:value="formData.color" />
			</a-form-item>
			<a-form-item label="排序:" name="sortCode">
				<a-input-number style="width: 100%" v-model:value="formData.sortCode" :max="100" />
			</a-form-item>
		</a-form>
		<template #footer>
			<a-button style="margin-right: 8px" @click="onClose">关闭</a-button>
			<a-button type="primary" @click="onSubmit">保存</a-button>
		</template>
		<Icon-selector ref="iconSelector" @iconCallBack="iconCallBack" />
	</xn-form-container>
</template>

<script setup>
	import ColorPicker from '@/components/ColorPicker/index.vue'
	import { required } from '@/utils/formRules'
	import moduleApi from '@/api/sys/resource/moduleApi'
	import IconSelector from '@/components/Selector/iconSelector.vue'
	// 默认是关闭状态
	let visible = $ref(false)
	const emit = defineEmits({ successful: null })
	const formRef = ref()
	const treeData = ref([])
	let iconSelector = ref()
	// 表单数据
	const formData = ref({})

	// 打开抽屉
	const onOpen = (record) => {
		visible = true
		formData.value = {
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
	// 图标选择器回调
	const iconCallBack = (value) => {
		formData.value.icon = value
	}

	// 默认要校验的
	const formRules = {
		title: [required('请输入模块名称')],
		icon: [required('请选择图标')],
		color: [required('请选择颜色')]
	}

	// 验证并提交数据
	const onSubmit = () => {
		formRef.value.validate().then(() => {
			moduleApi.submitForm(formData.value, formData.value.id).then(() => {
				onClose()
				emit('successful')
			})
		})
	}

	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>
