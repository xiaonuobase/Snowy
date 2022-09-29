<template>
	<div :class="[prefixCls]">
		<slot name="subtitle">
			<div :class="[`${prefixCls}-subtitle`]">{{ typeof subTitle === 'string' ? subTitle : subTitle() }}</div>
		</slot>
		<div class="number-info-value">
			<span>{{ total }}</span>
			<span class="sub-total">
				{{ subTotal }}
				<span v-if="`${status}` === 'up'"><caret-up-outlined /></span>
				<span v-else><caret-down-outlined /></span>
			</span>
		</div>
	</div>
</template>

<script>
	import { defineComponent } from 'vue'
	import { CaretDownOutlined, CaretUpOutlined } from '@ant-design/icons-vue'
	export default defineComponent({
		name: 'NumberInfo',
		components: {
			CaretUpOutlined,
			CaretDownOutlined
		},
		props: {
			prefixCls: {
				type: String,
				default: 'ant-pro-number-info'
			},
			total: {
				type: Number,
				required: true
			},
			subTotal: {
				type: Number,
				required: true
			},
			subTitle: {
				type: [String, Function],
				default: ''
			},
			status: {
				type: String,
				default: 'up'
			}
		},
		data() {
			return {}
		}
	})
</script>

<style lang="less" scoped>
	@import './index.less';
</style>
