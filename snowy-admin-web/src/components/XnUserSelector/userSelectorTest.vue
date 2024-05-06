<template>
	<a-card>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			表单的值：{{ formData }}
			<a-form-item name="userIdList">
				<xn-user-selector
					ref="userSelectorPlusProRef"
					:org-tree-api="selectorApiFunction.orgTreeApi"
					:user-page-api="selectorApiFunction.userPageApi"
					:user-list-by-id-list-api="selectorApiFunction.userListByIdListApi"
					v-model:value="formData.userIdList"
					@onBack="userSelectorOnBack"
				/>
			</a-form-item>
		</a-form>
		<a-button type="primary" @click="onSubmit">保存</a-button>
	</a-card>
</template>

<script setup name="userTest">
	import bizOrgApi from '@/api/biz/bizOrgApi'
	import userCenterApi from '@/api/sys/userCenterApi'
	import { required } from '@/utils/formRules'
	const formRef = ref()
	const formData = ref({
		userIdList: '1543837863788879871,1543837863788879873'
		// userIdList: ['1543837863788879871', '1543837863788879873']
	})
	const formRules = {
		userIdList: [required('请选择用户')]
	}
	const onSubmit = () => {
		formRef.value
			.validate()
			.then((result) => {
				console.log('最终表单数据：' + JSON.stringify(result))
			})
			.catch(() => {})
	}
	// 传递设计器需要的API
	const selectorApiFunction = {
		orgTreeApi: (param) => {
			return bizOrgApi.orgTreeSelector(param).then((data) => {
				return Promise.resolve(data)
			})
		},
		userPageApi: (param) => {
			return bizOrgApi.orgUserSelector(param).then((data) => {
				return Promise.resolve(data)
			})
		},
		userListByIdListApi: (param) => {
			return userCenterApi.userCenterGetUserListByIdList(param).then((data) => {
				return Promise.resolve(data)
			})
		}
	}
	// 如果用 v-model:value 来关联，这个可以不需要
	const userSelectorOnBack = (data) => {
		console.log('返回的：' + JSON.stringify(data))
	}
</script>
