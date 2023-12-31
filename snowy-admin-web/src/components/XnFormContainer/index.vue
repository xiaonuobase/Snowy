<template>
	<a-modal v-if="isModal" :visible="visible" @cancel="cancel" v-bind="$attrs">
		<template v-for="slotKey in slotKeys" #[slotKey]>
			<slot :name="slotKey" />
		</template>
	</a-modal>
	<a-drawer v-else :visible="visible" v-bind="$attrs" @close="cancel" :footer-style="{ textAlign: 'right' }">
		<template v-for="slotKey in slotKeys" #[slotKey]>
			<slot :name="slotKey" />
		</template>
	</a-drawer>
</template>

<script setup>
	import { useSlots } from 'vue'
	const slots = useSlots()

	import { globalStore } from '@/store'
	const store = globalStore()
	const props = defineProps({
		visible: {
			type: Boolean,
			default: false,
			required: true
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
	const emit = defineEmits(['close'])
	const cancel = () => {
		emit('close')
	}
</script>

<script>
	// 声明额外的选项
	export default {
		inheritAttrs: false
	}
</script>
