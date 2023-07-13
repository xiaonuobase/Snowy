<template>
	<a-spin size="small" :spinning="spinning">
		<a-select
			v-model:value="modelValue"
			:options="options"
			:field-names="{ label: 'name', value: 'id' }"
			style="width: 100%"
			:placeholder="props.placeholder"
			:allow-clear="props.allowClear"
			:disabled="props.disabled"
			@change="handleChange"
			@popupScroll="handlePopupScroll"
		/>
	</a-spin>
</template>

<script setup name="xnPageSelector">
	import { cloneDeep } from 'lodash-es'
	import { watch } from 'vue'

	const current = ref(1) // 当前页数
	const total = ref(0) // 数据总数
	const initParams = ref({})
	const options = ref([])
	const spinning = ref(false)
	const emit = defineEmits({ change: null, 'update:value': null })
	const props = defineProps({
		value: {
			type: String,
			default: () => undefined
		},
		placeholder: {
			type: String,
			default: () => '请选择'
		},
		pageFunction: {
			type: Function
		},
		echoFunction: {
			type: Function
		},
		pageSize: {
			type: Number,
			default: () => 10
		},
		allowClear: {
			type: Boolean,
			default: () => false
		},
		disabled: {
			type: Boolean,
			default: () => false
		}
	})
	const modelValue = ref(props.value)
	watch(props, (newValue) => {
		modelValue.value = newValue.value
	})

	// 请求数据
	const onPage = (param = {}) => {
		if (props.pageFunction) {
			initParams.value = param
			initParams.value.size = props.pageSize
			// 加载API
			spinning.value = true
			props
				.pageFunction(initParams.value)
				.then((data) => {
					initParams.value.current = data.current
					// 加载完后设置总数
					total.value = data.total
					options.value = data.records
					queryEcho()
				})
				.finally(() => {
					spinning.value = false
				})
		}
	}
	const queryEcho = () => {
		// 如果带有查询的方法，那么我们去给查询回显
		if (props.echoFunction) {
			nextTick(() => {
				if (modelValue.value) {
					const entity = options.value.find((f) => f.id === modelValue.value)
					if (!entity) {
						const param = {
							idList: [modelValue.value]
						}
						props.echoFunction(param).then((data) => {
							if (data[0]){
								options.value.push(data[0])
							}
						})
					}
				}
			})
		}
	}
	// change
	const handleChange = (value, array) => {
		modelValue.value = value
		// 触发change事件
		emit('change', value, array)
		// 更新数据
		emit('update:value', value)
	}
	// 滚动触发
	const handlePopupScroll = (e) => {
		const { target } = e
		const { scrollTop, scrollHeight, clientHeight } = target
		if (scrollTop + 2 + clientHeight >= scrollHeight) {
			// 已经到达底部，触发分页逻辑
			handlePagination()
		}
	}
	// 分页加载数据
	const handlePagination = () => {
		// 判断已有数量是否小于总量
		if (options.value.length < total.value) {
			const param = cloneDeep(initParams.value)
			param.current = initParams.value.current + 1
			spinning.value = true
			props
				.pageFunction(param)
				.then((data) => {
					if (data.records.length > 0) {
						options.value = [...cloneDeep(options.value), ...data.records].filter((item, index, self) => {
							return (
								self.findIndex((f) => {
									return f.id === item.id
								}) === index
							)
						})
					}
				})
				.finally(() => {
					spinning.value = false
				})
		}
	}
	defineExpose({
		onPage
	})
</script>
