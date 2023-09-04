<template>
	<div :class="[props.prefixCls, props.reverseColor && 'reverse-color']">
		<span>
			<slot name="term"></slot>
			<span class="item-text">
				<slot></slot>
			</span>
		</span>
		<span v-if="`${props.flag}` === 'up'" :class="[props.flag]"><caret-up-outlined /></span>
		<span v-else :class="[props.flag]"><caret-down-outlined /></span>
	</div>
</template>

<script setup name="Trend">
	import { CaretDownOutlined, CaretUpOutlined } from '@ant-design/icons-vue'
	const props = defineProps({
		prefixCls: {
			type: String,
			default: 'ant-pro-trend'
		},
		// 上升下降标识：up|down
		flag: {
			type: String,
			required: true
		},
		// 颜色反转
		reverseColor: {
			type: Boolean,
			default: false
		}
	})
</script>

<style lang="less" scoped>
	@import './index.less';
</style>
