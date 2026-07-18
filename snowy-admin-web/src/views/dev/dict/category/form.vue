<template>
	<xn-form-container :title="formTitle" :width="550" :visible="visible" :destroy-on-close="true" @close="onClose">
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-form-item v-if="mode === 'value'" label="所属字典类型：">
				<a-input :value="parentInfo.dictLabel" disabled />
			</a-form-item>
			<a-form-item :label="mode === 'type' ? '类型名称：' : '字典文字：'" name="dictLabel">
				<a-input
					v-model:value="formData.dictLabel"
					:placeholder="mode === 'type' ? '请输入字典类型名称' : '请输入字典文字'"
					allow-clear
				/>
			</a-form-item>
			<a-form-item
				:label="mode === 'type' ? '类型编码：' : '字典值：'"
				name="dictValue"
				:extra="mode === 'type' && formData.id ? '类型编码被系统引用，不可修改' : ''"
			>
				<a-input
					v-model:value="formData.dictValue"
					:placeholder="mode === 'type' ? '请输入字典类型编码' : '请输入字典值'"
					allow-clear
					:disabled="mode === 'type' && !!formData.id"
				/>
			</a-form-item>
			<a-form-item label="字典颜色：" name="dictColor">
				<a-select v-model:value="formData.dictColor" placeholder="请选择字典颜色" allow-clear>
					<a-select-option v-for="item in dictColorList" :key="item" :value="item">
						<a-tag :color="item">{{ item }}</a-tag>
					</a-select-option>
				</a-select>
			</a-form-item>
			<a-form-item label="排序：" name="sortCode" :extra="mode === 'value' ? '同一字典类型下的字典值按此序号升序排列' : ''">
				<a-input-number class="xn-wd" v-model:value="formData.sortCode" :min="1" :max="1000" />
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
	// 表单形态：type为字典类型，value为字典值
	const mode = ref('type')
	// 字典值所属的字典类型信息
	const parentInfo = ref({})
	// 定义字典颜色
	const dictColorList = [
		'default',
		'pink',
		'red',
		'orange',
		'green',
		'cyan',
		'blue',
		'purple',
		'gold',
		'geekblue',
		'volcano',
		'magenta',
		'processing',
		'success',
		'error',
		'warning'
	]

	const formTitle = computed(() => {
		const action = formData.value.id ? '编辑' : '增加'
		return mode.value === 'type' ? `${action}字典类型` : `${action}字典值`
	})

	// 打开抽屉，parent不为空时表示操作的是该类型下的字典值，defaultSortCode为新增时的默认排序号
	const onOpen = (record, type, parent, defaultSortCode) => {
		visible.value = true
		parentInfo.value = parent || {}
		mode.value = parent || (record && record.parentId !== '0') ? 'value' : 'type'
		formData.value = {
			sortCode: defaultSortCode || 99,
			category: type,
			parentId: parent ? parent.id : '0'
		}
		if (record) {
			formData.value = Object.assign({}, record)
			formData.value.category = type
		}
	}
	// 关闭抽屉
	const onClose = () => {
		visible.value = false
	}
	// 默认要校验的
	const formRules = {
		dictLabel: [required('请输入名称')],
		dictValue: [required('请输入字典值')],
		dictColor: [required('请选择字典颜色')],
		sortCode: [required('请选择排序')]
	}
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
