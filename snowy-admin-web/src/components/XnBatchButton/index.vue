<template>
	<div v-if="isPopconFirm">
		<a-popconfirm title="批量处理此信息？" :open="batchVisible" @openChange="batchVisibleChange" @confirm="deleteBatch">
			<a-button :type="props.buttonType" :danger="props.buttonDanger" :size="props.size" :loading="loading">
				<template #icon v-if="props.icon">
					<component :is="props.icon" :style="{ color: props.color }" />
				</template>
				{{ props.buttonName }}
			</a-button>
		</a-popconfirm>
	</div>
	<div v-else>
		<a-button
			:type="props.buttonType"
			:danger="props.buttonDanger"
			:size="props.size"
			:loading="loading"
			@click="deleteBatch"
		>
			<template #icon v-if="props.icon">
				<component :is="props.icon" :style="{ color: props.color }" />
			</template>
			{{ props.buttonName }}
		</a-button>
	</div>
</template>

<script setup name="commonBatchButton">
	import { message } from 'ant-design-vue'
	const batchVisible = ref(false)
	const emit = defineEmits({ batchCallBack: null })
	const props = defineProps({
		idKey: {
			type: String,
			default: () => 'id'
		},
		buttonName: {
			type: String,
			default: () => '批量操作'
		},
		buttonDanger: {
			type: Boolean,
			default: () => false
		},
		buttonType: {
			type: String,
			default: () => undefined
		},
		icon: {
			type: String,
			default: () => undefined
		},
		size: {
			type: String,
			default: () => 'middle'
		},
		selectedRowKeys: {
			type: Array,
			default: () => []
		},
		color: {
			type: String,
			default: () => ''
		},
		// 是否确认提示
		isPopconFirm: {
			type: Boolean,
			default: () => true
		},
		loading: {
			type: Boolean,
			default: () => false
		}
	})
	// 参数校验
	const batchVisibleChange = () => {
		if (batchVisible.value) {
			batchVisible.value = false
			return false
		} else {
			if (props.selectedRowKeys.length < 1) {
				message.warning('请选择一条或多条数据')
				batchVisible.value = false
				return false
			} else {
				batchVisible.value = true
				return true
			}
		}
	}
	// 批量操作
	const deleteBatch = () => {
		if (!props.isPopconFirm) {
			if (props.selectedRowKeys.length < 1) {
				message.warning('请选择一条或多条数据')
				batchVisible.value = false
				return false
			}
		}
		const params = props.selectedRowKeys.map((m) => {
			return {
				[props.idKey]: m
			}
		})
		// 发起方法调用，谁的谁来实现
		emit('batchCallBack', params)
	}
</script>
