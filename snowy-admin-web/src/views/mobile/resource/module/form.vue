<template>
	<xn-form-container
		:title="formData.id ? '编辑菜单模块' : '增加菜单模块'"
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
				<a-input v-model:value="formData.icon" class="xn-wdcalc-70" placeholder="请选择图标" allow-clear disabled />
				<a-button type="primary" @click="iconSelector.showIconModal(formData.icon)">选择</a-button>
			</a-form-item>
			<a-form-item label="颜色：" name="color">
				<color-picker v-model:value="formData.color" />
			</a-form-item>
			<a-form-item label="排序:" name="sortCode">
				<a-input-number class="xn-wd" v-model:value="formData.sortCode" :max="100" />
			</a-form-item>
		</a-form>
		<template #footer>
			<a-button class="xn-mr8" @click="onClose">关闭</a-button>
			<a-button type="primary" @click="onSubmit">保存</a-button>
		</template>
		<icon-mobile-selector ref="iconSelector" @iconCallBack="iconCallBack" />
	</xn-form-container>
</template>

<script setup>
	import ColorPicker from '@/components/ColorPicker/index.vue'
	import { required } from '@/utils/formRules'
	import moduleApi from '@/api/mobile/resource/moduleApi'
	import IconMobileSelector from '@/components/Selector/iconMobileSelector.vue'
	// 默认是关闭状态
	const visible = ref(false)
	const emit = defineEmits({ successful: null })
	const formRef = ref()
	const treeData = ref([])
	let iconSelector = ref()
	// 表单数据
	const formData = ref({})
	// 打开抽屉
	const onOpen = (record) => {
		visible.value = true
		formData.value = {
			sortCode: 99,
			color: '#1677FF'
		}
		if (record) {
			formData.value = Object.assign({}, record)
		}
	}
	// 关闭抽屉
	const onClose = () => {
		formRef.value.resetFields()
		visible.value = false
	}
	// 图标选择器回调
	const iconCallBack = (value) => {
		if (value) {
			formRef.value.clearValidate('icon')
		}
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
		formRef.value
			.validate()
			.then(() => {
				moduleApi.submitForm(formData.value, formData.value.id).then(() => {
					onClose()
					emit('successful')
				})
			})
			.catch(() => {})
	}

	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>
