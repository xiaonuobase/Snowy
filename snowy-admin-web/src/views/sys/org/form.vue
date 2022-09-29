<template>
	<a-drawer
		:title="formData.id ? '编辑组织' : '增加组织'"
		:width="500"
		:visible="visible"
		:destroy-on-close="true"
		:body-style="{ paddingBottom: '80px' }"
		:footer-style="{ textAlign: 'right' }"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-form-item label="上级组织：" name="parentId">
				<a-tree-select
					v-model:value="formData.parentId"
					style="width: 100%"
					:dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
					placeholder="请选择上级组织"
					allow-clear
					tree-default-expand-all
					:tree-data="treeData"
					:field-names="{
						children: 'children',
						label: 'name',
						value: 'id'
					}"
					selectable="false"
					tree-line
				></a-tree-select>
			</a-form-item>
			<a-form-item label="组织名称：" name="name">
				<a-input v-model:value="formData.name" placeholder="请输入组织名称" allow-clear />
			</a-form-item>
			<a-form-item label="组织分类：" name="category">
				<a-select
					v-model:value="formData.category"
					:options="orgCategoryOptions"
					style="width: 100%"
					placeholder="请选择组织分类"
				>
				</a-select>
			</a-form-item>
			<a-form-item label="排序：" name="sortCode">
				<a-slider v-model:value="formData.sortCode" :max="100" />
			</a-form-item>
			<a-form-item label="指定主管：" name="directorId">
				<a-button type="link" style="padding-left: 0px" @click="openSelector(formData.directorId)">选择</a-button>
				<a-tag v-if="formData.directorId && extJson.length > 0" color="orange" closable @close="closeUserTag">{{
					extJson[0].name
				}}</a-tag>
				<a-input v-show="false" v-model:value="formData.directorId" />
			</a-form-item>
		</a-form>
		<template #footer>
			<a-button style="margin-right: 8px" @click="onClose">关闭</a-button>
			<a-button type="primary" :loading="submitLoading" @click="onSubmit">保存</a-button>
		</template>
		<user-selector-plus
			ref="UserSelectorPlus"
			page-url="/sys/org/userSelector"
			org-url="/sys/org/orgTreeSelector"
			:radio-model="true"
			@onBack="userBack"
		/>
	</a-drawer>
</template>

<script setup name="orgForm">
	import { required } from '@/utils/formRules'
	import { message } from 'ant-design-vue'
	import orgApi from '@/api/sys/orgApi'
	import { getCurrentInstance } from 'vue'
	import userSelectorPlus from '@/components/Selector/userSelectorPlus.vue'

	// 定义emit事件
	const emit = defineEmits({ successful: null })
	const { proxy } = getCurrentInstance()
	// 默认是关闭状态
	let visible = $ref(false)
	let UserSelectorPlus = ref()
	const formRef = ref()
	// 表单数据，也就是默认给一些数据
	const formData = ref({})
	// 定义机构元素
	const treeData = ref([])
	const extJson = ref([])
	const submitLoading = ref(false)

	// 打开抽屉
	const onOpen = (record) => {
		visible = true
		extJson.value = ref([])
		formData.value = {
			sortCode: 99
		}
		if (record) {
			const param = {
				id: record.id
			}
			orgApi.orgDetail(param).then((data) => {
				formData.value = Object.assign({}, data)
				extJson.value = JSON.parse(formData.value.extJson) || []
			})
		}
		// 获取机构树并加入顶级
		orgApi.orgTree().then((res) => {
			treeData.value = [
				{
					id: 0,
					parentId: '-1',
					name: '顶级',
					children: res
				}
			]
		})
	}
	// 关闭抽屉
	const onClose = () => {
		visible = false
	}
	// 默认要校验的
	const formRules = {
		name: [required('请输入组织名称')],
		category: [required('请选择组织分类')],
		sortCode: [required('请选择排序')]
	}
	// 机构分类字典
	let orgCategoryOptions = proxy.$TOOL.dictTypeList('ORG_CATEGORY').map((item) => {
		return {
			value: item['dictValue'],
			label: item['name']
		}
	})
	// 打开人员选择器，选择主管
	const openSelector = (id) => {
		let checkedUserIds = []
		checkedUserIds.push(id)
		UserSelectorPlus.value.showUserPlusModal(checkedUserIds)
	}
	// 人员选择器回调
	const userBack = (value) => {
		extJson.value = value
		if (value.length > 0) {
			formData.value.directorId = value[0].id
		} else {
			formData.value.directorId = ''
		}
	}
	// 通过小标签删除主管
	const closeUserTag = () => {
		extJson.value = []
		formData.value.directorId = ''
	}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value
			.validate()
			.then(() => {
				submitLoading.value = true
				formData.value.extJson = JSON.stringify(extJson.value)
				orgApi
					.submitForm(formData.value, !formData.value.id)
					.then(() => {
						visible = false
						emit('successful')
					})
					.finally(() => {
						submitLoading.value = false
					})
			})
	}
	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>
