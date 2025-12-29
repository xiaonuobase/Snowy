<template>
	<xn-form-container title="批量复制组织" :width="550" :visible="visible" :destroy-on-close="true" @close="onClose">
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-form-item label="目标上级组织：" name="targetParentId">
				<a-tree-select
					v-model:value="formData.targetParentId"
					class="xn-wd"
					:dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
					placeholder="请选择目标上级组织"
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
				/>
				<a-alert
					class="mt-3"
					message="温馨提示：批量复制将自动跳过同名组织；复制内容包含排序号、组织名称、分类；部门主管需重新指定。"
					type="warning"
					show-icon
				/>
			</a-form-item>
		</a-form>
		<template #footer>
			<a-button class="xn-mr8" @click="onClose">关闭</a-button>
			<a-button type="primary" :loading="submitLoading" @click="onSubmit">保存</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="orgCopyForm">
	import { required } from '@/utils/formRules'
	import orgApi from '@/api/sys/orgApi'
	import { message } from 'ant-design-vue'

	// 定义emit事件
	const emit = defineEmits({ successful: null })
	// 默认是关闭状态
	const visible = ref(false)
	const formRef = ref()
	// 表单数据
	const formData = ref({})
	// 定义机构元素
	const treeData = ref([])
	const submitLoading = ref(false)
	// 选中的ID列表
	const ids = ref([])

	// 打开抽屉
	const onOpen = (idParam) => {
		visible.value = true
		formData.value = {}
		if (idParam) {
			ids.value = idParam.map((item) => item.id)
		}
		// 获取机构树并加入顶级
		orgApi.orgOrgTreeSelector().then((res) => {
			treeData.value = [
				{
					id: '0',
					parentId: '-1',
					name: '顶级',
					children: res
				}
			]
		})
	}
	// 关闭抽屉
	const onClose = () => {
		visible.value = false
	}
	// 默认要校验的
	const formRules = {
		targetParentId: [required('请选择目标上级组织')]
	}

	// 验证并提交数据
	const onSubmit = () => {
		formRef.value
			.validate()
			.then(() => {
				submitLoading.value = true
				const param = {
					ids: ids.value,
					targetParentId: formData.value.targetParentId
				}
				orgApi
					.orgCopy(param)
					.then(() => {
						visible.value = false
						message.success('复制成功')
						emit('successful')
					})
					.finally(() => {
						submitLoading.value = false
					})
			})
			.catch(() => {})
	}
	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>
