<template>
	<div class="snowy-color-picker" @click="forceResize">
		<color-picker v-bind="$attrs" format="hex" :pureColor="props.value" @update:pureColor="update" />
	</div>
</template>

<script setup>
	import { ColorPicker } from 'vue3-colorpicker'
	import 'vue3-colorpicker/style.css'

	const emit = defineEmits(['update:value'])

	const props = defineProps({
		value: {
			type: String,
			default: '#1890ff'
		}
	})

	const forceResize = () => {
		window.dispatchEvent(new Event('resize'))
	}

	const update = (val) => {
		const currentColor = document.querySelector('.current-color')
		if (currentColor) {
			currentColor.textContent = val
		}
		emit('update:value', val)
	}
</script>

<style lang="less">
	.snowy-color-picker {
		.vc-color-wrap {
			width: auto;
		}
		.current-color {
			color: #fff;
			padding: 0 10px;
		}
	}
</style>
