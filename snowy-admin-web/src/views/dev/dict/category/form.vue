<template>
	<xn-form-container
		:title="formData.id ? '编辑字典' : '增加字典'"
		:width="550"
		:visible="visible"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical" :label-col="labelCol">
			<a-form-item label="上级字典：" name="parentId">
				<a-tree-select
					v-model:value="formData.parentId"
					v-model:treeExpandedKeys="defaultExpandedKeys"
					class="xn-wd"
					:dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
					placeholder="请选择上级字典"
					allow-clear
					:tree-data="treeData"
					:field-names="{
						children: 'children',
						label: 'name',
						value: 'id'
					}"
					selectable="false"
					treeLine
				></a-tree-select>
			</a-form-item>
			<a-form-item label="字典名称：" name="dictLabel">
				<a-input v-model:value="formData.dictLabel" placeholder="请输入字典名称" allow-clear />
			</a-form-item>
			<a-form-item label="字典值：" name="dictValue">
				<a-input
					v-model:value="formData.dictValue"
					placeholder="请输入字典值"
					allow-clear
					:disabled="formData.parentId === '0'"
				/>
			</a-form-item>
			<a-form-item label="排序：" name="sortCode">
				<a-input-number class="xn-wd" v-model:value="formData.sortCode" :max="1000" />
			</a-form-item>
		</a-form>
		<template #footer>
			<a-button class="xn-mr8" @click="onClose">关闭</a-button>
			<a-button type="primary" @click="onSubmit">保存</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="dictForm">
	import { required } from '@/utils/formRules'
	import dictApi from '@/api/dev/dictApi'

	// 定义emit事件
	const emit = defineEmits({ successful: null })
	// 默认是关闭状态
	const visible = ref(false)
	const formRef = ref()
	// 表单数据
	let formData = ref({})
	// 定义树元素
	const treeData = ref([])
	// 默认展开的节点(顶级)
	const defaultExpandedKeys = ref([0])

	// 打开抽屉
	const onOpen = (record, type, parentId) => {
		visible.value = true
		formData.value = {
			sortCode: 99,
			category: type
		}
		if (parentId) {
			formData.value.parentId = parentId
		}
		if (record) {
			formData.value = Object.assign({}, record)
			formData.value.category = type
		}
		// 获取树并加入顶级
		const treeParam = {
			category: type
		}
		dictApi.dictTree(treeParam).then((res) => {
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
		visible.value = false
	}
	// 默认要校验的
	const formRules = {
		dictLabel: [required('请输入字典名称')],
		dictValue: [required('请选择字典值')],
		sortCode: [required('请选择排序')]
	}
	// 表单固定label实现
	const labelCol = ref({
		style: {
			width: '100px'
		}
	})
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value
			.validate()
			.then(() => {
				dictApi.submitForm(formData.value, formData.value.id).then(() => {
					visible.value = false
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
