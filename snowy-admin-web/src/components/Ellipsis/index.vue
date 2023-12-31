<template>
	<ellipsis />
</template>
<script setup>
	import { h } from 'vue'
	import Tooltip from 'ant-design-vue/es/tooltip'
	import { cutStrByFullLength, getStrFullLength } from './util'
	import { useSlots } from 'vue'
	const slots = useSlots()

	const props = defineProps({
		prefixCls: {
			type: String,
			default: 'ant-pro-ellipsis'
		},
		tooltip: {
			type: Boolean
		},
		length: {
			type: Number,
			required: true
		},
		lines: {
			type: Number,
			default: 1
		},
		fullWidthRecognition: {
			type: Boolean,
			default: false
		}
	})

	const str = slots
		.default()
		.map((vNode) => vNode.children)
		.join('')

	const fullLength = getStrFullLength(str)
	const showStr = cutStrByFullLength(str, props.length) + (fullLength > props.length ? '...' : '')

	// 使用h函数注册渲染一个组件
	const ellipsis = () => {
		return props.tooltip && fullLength > props.length
			? h(Tooltip, { title: str }, { default: () => showStr })
			: // 引用组件时，需要设置默认值 default: () => xxx
			  h('span', showStr)
	}
</script>
