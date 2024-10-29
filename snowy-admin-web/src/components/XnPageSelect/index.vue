<template>
	<a-spin size="small" :spinning="spinning">
		<!-- 重写两边是因为可能是个antdv的bug影响到我们的控制台输出错误 -->
		<a-select
			v-if="props.showSearch"
			v-model:value="modelValue"
			:options="options"
			:field-names="{ label: 'name', value: 'id' }"
			class="xn-wd"
			:placeholder="props.placeholder"
			:allow-clear="props.allowClear"
			:disabled="props.disabled"
			:filterOption="!props.showSearch"
			show-search
			@change="handleChange"
			@search="handleSearch"
			@popupScroll="handlePopupScroll"
		/>
		<a-select
			v-else
			v-model:value="modelValue"
			:options="options"
			:field-names="{ label: 'name', value: 'id' }"
			class="xn-wd"
			:placeholder="props.placeholder"
			:allow-clear="props.allowClear"
			:disabled="props.disabled"
			:filterOption="!props.showSearch"
			@change="handleChange"
			@onSearch="handleSearch"
			@popupScroll="handlePopupScroll"
		/>
	</a-spin>
</template>

<script setup name="xnPageSelector">
	import { watch } from 'vue'

	const total = ref(0) // 数据总数
	const initParams = ref({})
	const options = ref([])
	const spinning = ref(false)
	const emit = defineEmits({ change: null, 'update:value': null, search: null })
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
		showSearch: {
			type: Boolean,
			default: () => false
		},
		searchKeyName: {
			type: String,
			default: () => ''
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
			initParams.value = { ...initParams.value, ...param, size: props.pageSize }
			// 加载API
			spinning.value = true
			// 重置当前页
			initParams.value.current = 1
			props
				.pageFunction(initParams.value)
				.then((data) => {
					// 更新当前页码
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
							if (data[0]) {
								options.value.unshift(data[0])
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
		if (value == null && props.showSearch) {
			//被清空，重置查询条件
			initParams.value[props.searchKeyName] = value
		}
		// 更新数据
		emit('update:value', value)
		// 触发change事件
		emit('change', value, array)
	}
	// search
	const handleSearch = (searchValue) => {
		let _params = { current: 1 }
		if (props.searchKeyName && props.searchKeyName !== '') {
			_params[props.searchKeyName] = searchValue
			onPage({ ...initParams.value, ..._params })
		}
		// 触发search事件
		emit('search', searchValue)
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
			const param = { ...initParams.value, current: initParams.value.current + 1 }
			spinning.value = true
			props
				.pageFunction(param)
				.then((data) => {
					if (data.records.length > 0) {
						// 更新当前页码
						initParams.value.current = data.current
						// 合并新旧数据
						const newOptions = [...options.value, ...data.records]
						// 使用 id 去重
						const uniqueOptions = newOptions.reduce((acc, cur) => {
							acc[cur.id] = cur
							return acc
						}, {})
						options.value = Object.values(uniqueOptions)
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
