<template>
	<Modal v-if="isModal" :visible="visible" @cancel="cancel" v-bind="$attrs">
		<template v-for="slotKey in slotKeys" #[slotKey]>
			<slot :name="slotKey" />
		</template>
	</Modal>
	<Drawer v-else :visible="visible" v-bind="$attrs" :footer-style="{ textAlign: 'right' }">
		<template v-for="slotKey in slotKeys" #[slotKey]>
			<slot :name="slotKey" />
		</template>
	</Drawer>
</template>

<script>
	import { Drawer, Modal } from 'ant-design-vue'
	import { modalProps } from 'ant-design-vue/es/modal/Modal'
	import { drawerProps } from 'ant-design-vue/es/drawer'
	import { mapState } from 'pinia'
	import { globalStore } from '@/store'

	const FormContainerTypeEnum = {
		DRAWER: 'drawer',
		MODAL: 'modal'
	}
	export default {
		name: 'XnFormContainer',
		components: {
			Drawer,
			Modal
		},
		inheritAttrs: false,
		props: {
			visible: {
				type: Boolean,
				default: false,
				required: true
			},
			...modalProps,
			...drawerProps
		},
		computed: {
			...mapState(globalStore, ['formStyle']),
			slotKeys() {
				return Object.keys(this.$slots)
			},
			isModal() {
				return FormContainerTypeEnum.MODAL === this.formStyle
			}
		},
		methods: {
			cancel() {
				this.$emit('close')
			}
		}
	}
</script>
