<template>
	<a-modal
		v-if="isModal"
		:open="visible"
		@cancel="cancel"
		v-bind="$attrs"
		:footer="slotKeys.includes('footer') ? undefined : null"
	>
		<template v-for="slotKey in slotKeys" #[slotKey]>
			<slot :name="slotKey" />
		</template>
	</a-modal>
	<a-drawer
		v-else
		:open="visible"
		v-bind="$attrs"
		@close="cancel"
		:footer-style="{ textAlign: 'right' }"
		:width="drawerWidth"
	>
		<template v-for="slotKey in slotKeys" #[slotKey]>
			<slot :name="slotKey" />
		</template>
	</a-drawer>
</template>

<script setup>
	import { useSlots, computed, useAttrs, onMounted, onUnmounted } from 'vue'
	import { globalStore } from '@/store'
	const slots = useSlots()
	const attrs = useAttrs()
	const store = globalStore()
	const props = defineProps({
		visible: {
			type: Boolean,
			default: false,
			required: false
		}
	})
	const FormContainerTypeEnum = {
		DRAWER: 'drawer',
		MODAL: 'modal'
	}
	const formStyle = computed(() => {
		return store.formStyle
	})
	const slotKeys = computed(() => {
		return Object.keys(slots)
	})
	const isModal = computed(() => {
		return FormContainerTypeEnum.MODAL === formStyle.value
	})

	// 响应式抽屉宽度
	const isSmallScreen = ref(window.innerWidth <= 768)
	const drawerWidth = computed(() => {
		return isSmallScreen.value ? '100%' : attrs.width // 小屏幕100%宽度，其他情况使用默认值
	})

	const emit = defineEmits(['close'])
	const cancel = () => {
		emit('close')
	}

	// 监听窗口大小变化
	const handleResize = () => {
		isSmallScreen.value = window.innerWidth <= 768
	}

	onMounted(() => {
		window.addEventListener('resize', handleResize)
	})

	onUnmounted(() => {
		window.removeEventListener('resize', handleResize)
	})
</script>
<script>
	// 声明额外的选项
	export default {
		inheritAttrs: false
	}
</script>

<style scoped>
	/* 确保小屏幕下抽屉不会有额外的边距或滚动条 */
	@media (max-width: 576px) {
		:deep(.ant-drawer-content-wrapper) {
			width: 100% !important;
			max-width: 100% !important;
		}
	}
</style>
