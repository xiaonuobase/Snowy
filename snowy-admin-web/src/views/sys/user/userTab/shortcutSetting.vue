<template>
	<a-form
		ref="formRef"
		:model="formData"
		v-bind="layout"
		:label-col="{ ...layout.labelCol, offset: 0 }"
		:wrapper-col="{ ...layout.wrapperCol, offset: 0 }"
	>
		<a-form-item label="选择菜单：">
			<menuTreeSelect ref="menuTreeSelectRef" :resultData="true" />
		</a-form-item>
		<a-form-item :wrapper-col="{ ...layout.wrapperCol, offset: 4 }">
			<a-button type="primary" :loading="submitLoading" @click="onSubmit">保存</a-button>
		</a-form-item>
	</a-form>
</template>

<script setup name="shortcutSetting">
	import userCenterApi from '@/api/sys/userCenterApi'
	import { onMounted } from 'vue'
	import MenuTreeSelect from '@/components/TreeSelect/menuTreeSelect.vue'

	const formRef = ref()
	let formData = ref({})
	const menuTreeSelectRef = ref()
	const submitLoading = ref(false)

	onMounted(() => {
		// 进来后执行查询
		getUserLoginWorkbench()
	})
	const getUserLoginWorkbench = () => {
		userCenterApi.userLoginWorkbench().then((data) => {
			if (data) {
				// 设置组件回显
				menuTreeSelectRef.value.setSelectData(JSON.parse(data).shortcut)
			}
		})
	}

	// 验证并提交数据
	const onSubmit = () => {
		const selectMenu = menuTreeSelectRef.value.getSelectData()
		const workbenchData = {
			shortcut: selectMenu
		}
		formData.value.workbenchData = JSON.stringify(workbenchData)
		submitLoading.value = true
		userCenterApi
			.userUpdateUserWorkbench(formData.value)
			.then(() => {})
			.finally(() => {
				submitLoading.value = false
			})
	}
	const layout = {
		labelCol: {
			span: 4
		},
		wrapperCol: {
			span: 12
		}
	}
</script>
