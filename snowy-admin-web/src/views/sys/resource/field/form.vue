<template>
	<a-modal
		v-model:open="visible"
		:title="formData.id ? '编辑字段' : '增加字段'"
		:width="550"
		:mask-closable="false"
		:destroy-on-close="true"
		@ok="onSubmit"
		@cancel="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-form-item label="显示名称：" name="title">
				<a-input v-model:value="formData.title" placeholder="请输入显示名称" allow-clear />
			</a-form-item>
			<a-form-item label="编码：" name="code">
				<a-input v-model:value="formData.code" placeholder="请输入字段驼峰编码" allow-clear />
			</a-form-item>
			<a-form-item label="排序:" name="sortCode">
				<a-input-number class="xn-wd" v-model:value="formData.sortCode" :max="100" :min="0" />
			</a-form-item>
		</a-form>
	</a-modal>
</template>

<script setup name="sysResourceFieldForm">
	import { required } from '@/utils/formRules'
	import fieldApi from '@/api/sys/resource/fieldApi'
	// 默认是关闭状态
	const visible = ref(false)
	const emit = defineEmits({ successful: null })
	const formRef = ref()
	const treeData = ref([])
	const tableRef = ref()
	// 表单数据，也就是默认给一些数据
	const formData = ref({})
	const recordData = ref()
	// 打开弹框
	const onOpen = (record, fieldData) => {
		visible.value = true
		recordData.value = record
		formData.value = {
			sortCode: 99
		}
		if (fieldData) {
			formData.value = Object.assign({}, fieldData)
		}
	}
	// 关闭弹框
	const onClose = () => {
		formRef.value.resetFields()
		visible.value = false
	}
	// 默认要校验的
	const formRules = {
		title: [required('请输入字段名称')],
		code: [required('请输入字段驼峰编码')]
	}
	// 加载按钮数据
	const loadData = (parameter) => {
		return fieldApi.fieldTree(Object.assign(parameter, searchFormState.value)).then((res) => {
			return res
		})
	}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value
			.validate()
			.then(() => {
				const defParam = {
					category: 'FIELD',
					parentId: recordData.value.id
				}
				const param = Object.assign(defParam, formData.value)
				fieldApi.submitForm(param, formData.value.id).then((res) => {
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
