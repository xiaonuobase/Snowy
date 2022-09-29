<template>
	<a-drawer
		:title="formData.id ? '编辑岗位' : '增加岗位'"
		:width="500"
		:visible="visible"
		:destroy-on-close="true"
		:body-style="{ paddingBottom: '80px' }"
		:footer-style="{ textAlign: 'right' }"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-form-item label="所属组织：" name="orgId">
				<a-tree-select
					v-model:value="formData.orgId"
					style="width: 100%"
					:dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
					placeholder="请选择组织"
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
			<a-form-item label="岗位名称：" name="name">
				<a-input v-model:value="formData.name" placeholder="请输入岗位名称" allow-clear />
			</a-form-item>
			<a-form-item label="岗位分类：" name="category">
				<a-select
					v-model:value="formData.category"
					:options="positionCategoryOptions"
					style="width: 100%"
					placeholder="请选择岗位分类"
				>
				</a-select>
			</a-form-item>
			<a-form-item label="排序：" name="sortCode">
				<a-slider v-model:value="formData.sortCode" :max="100" />
			</a-form-item>
		</a-form>
		<template #footer>
			<a-button style="margin-right: 8px" @click="onClose">关闭</a-button>
			<a-button type="primary" :loading="submitLoading" @click="onSubmit">保存</a-button>
		</template>
	</a-drawer>
</template>

<script setup name="bizPositionForm">
	import { required } from '@/utils/formRules'
	import { getCurrentInstance } from 'vue'
	import { message } from 'ant-design-vue'
	import bizOrgApi from '@/api/biz/bizOrgApi'
	import bizPositionApi from '@/api/biz/bizPositionApi'

	// 定义emit事件
	const emit = defineEmits({ successful: null })
	const { proxy } = getCurrentInstance()
	// 默认是关闭状态
	let visible = $ref(false)
	const formRef = ref()
	// 表单数据，也就是默认给一些数据
	const formData = ref({})
	// 定义机构元素
	const treeData = ref([])
	const submitLoading = ref(false)

	// 打开抽屉
	const onOpen = (record) => {
		visible = true
		formData.value = {
			sortCode: 99
		}
		if (record) {
			formData.value = Object.assign({}, record)
		}
		// 获取机构树并加入顶级
		bizOrgApi.orgTree().then((res) => {
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
		orgId: [required('请选择所属组织')],
		name: [required('请输入岗位名称')],
		category: [required('请选择岗位分类')],
		sortCode: [required('请选择排序')]
	}
	let positionCategoryOptions = proxy.$TOOL.dictTypeList('POSITION_CATEGORY').map((item) => {
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
				submitLoading.value = true
				bizPositionApi.submitForm(formData.value, !formData.value.id).then(() => {
					visible = false
					submitLoading.value = false
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

<style scoped></style>
