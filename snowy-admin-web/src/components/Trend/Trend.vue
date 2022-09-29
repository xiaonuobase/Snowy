<template>
	<div :class="[prefixCls, reverseColor && 'reverse-color']">
		<span>
			<slot name="term"></slot>
			<span class="item-text">
				<slot></slot>
			</span>
		</span>
		<span v-if="`${flag}` === 'up'" :class="[flag]"><caret-up-outlined /></span>
		<span v-else :class="[flag]"><caret-down-outlined /></span>
	</div>
</template>

<script>
	import { defineComponent } from 'vue'
	import { CaretDownOutlined, CaretUpOutlined } from '@ant-design/icons-vue'
	export default defineComponent({
		name: 'Trend',
		components: {
			CaretUpOutlined,
			CaretDownOutlined
		},
		props: {
			prefixCls: {
				type: String,
				default: 'ant-pro-trend'
			},
			/**
			 * 上升下降标识：up|down
			 */
			flag: {
				type: String,
				required: true
			},
			/**
			 * 颜色反转
			 */
			reverseColor: {
				type: Boolean,
				default: false
			}
		}
	})
</script>

<style lang="less" scoped>
	@import './index.less';
</style>
