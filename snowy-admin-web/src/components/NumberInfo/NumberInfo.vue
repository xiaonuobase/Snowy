<template>
	<div :class="[props.prefixCls]">
		<slot name="subtitle">
			<div :class="[`${props.prefixCls}-subtitle`]">
				{{ typeof props.subTitle === 'string' ? props.subTitle : props.subTitle() }}
			</div>
		</slot>
		<div class="number-info-value">
			<span>{{ props.total }}</span>
			<span class="sub-total">
				{{ props.subTotal }}
				<span v-if="`${props.status}` === 'up'"><caret-up-outlined /></span>
				<span v-else><caret-down-outlined /></span>
			</span>
		</div>
	</div>
</template>

<script setup name="NumberInfo">
	import { CaretDownOutlined, CaretUpOutlined } from '@ant-design/icons-vue'

	const props = defineProps({
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
	})
</script>

<style lang="less" scoped>
	.ant-pro-number-info-subtitle {
		color: @text-color-secondary;
		font-size: @font-size-base;
		height: 22px;
		line-height: 22px;
		overflow: hidden;
		text-overflow: ellipsis;
		word-break: break-all;
		white-space: nowrap;
	}
	.number-info-value {
		margin-top: 4px;
		font-size: 0;
		overflow: hidden;
		text-overflow: ellipsis;
		word-break: break-all;
		white-space: nowrap;

		& > span {
			color: @heading-color;
			display: inline-block;
			line-height: 32px;
			height: 32px;
			font-size: 24px;
			margin-right: 32px;
		}

		.sub-total {
			color: @text-color-secondary;
			font-size: @font-size-lg;
			vertical-align: top;
			margin-right: 0;
			i {
				font-size: 12px;
				transform: scale(0.82);
				margin-left: 4px;
			}
		}
	}
</style>
