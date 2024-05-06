<template>
	<xn-form-container title="更改模块" :width="550" :visible="visible" :destroy-on-close="true" @close="onClose">
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-form-item label="显示名称：" name="title">
				<span>{{ formData.title }}</span>
			</a-form-item>
			<a-form-item label="所属目录：" name="module" v-if="formData.parentId === '0'">
				<a-radio-group v-model:value="formData.module" button-style="solid">
					<a-radio-button v-for="module in moduleTypeList" :key="module.id" :value="module.id">
						<component :is="module.icon" />
						{{ module.title }}</a-radio-button
					>
				</a-radio-group>
			</a-form-item>
		</a-form>
		<template #footer>
			<a-button class="xn-mr8" @click="onClose">关闭</a-button>
			<a-button type="primary" :loading="submitLoading" @click="onSubmit">保存</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="sysResourceMenuChangeModuleForm">
	import { required } from '@/utils/formRules'
	import menuApi from '@/api/sys/resource/menuApi'
	// 默认是关闭状态
	const visible = ref(false)
	const emit = defineEmits({ successful: null })
	const formRef = ref()
	// 表单数据，也就是默认给一些数据
	const formData = ref({})
	const submitLoading = ref(false)
	const moduleTypeList = ref([])
	// 打开抽屉
	const onOpen = (record) => {
		visible.value = true
		if (record) {
			formData.value = Object.assign({}, record)
		}
		// 获取模块列表
		menuApi.menuModuleSelector().then((data) => {
			moduleTypeList.value = data
		})
	}
	// 关闭抽屉
	const onClose = () => {
		formRef.value.resetFields()
		visible.value = false
	}

	// 默认要校验的
	const formRules = {
		module: [required('请选择所属目录')]
	}

	// 验证并提交数据
	const onSubmit = () => {
		formRef.value
			.validate()
			.then(() => {
				const param = {
					id: formData.value.id,
					module: formData.value.module
				}
				submitLoading.value = true
				menuApi
					.menuChangeModule(param)
					.then(() => {
						submitLoading.value = false
						emit('successful')
					})
					.finally(() => {
						visible.value = false
					})
			})
			.catch(() => {})
	}
	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>
