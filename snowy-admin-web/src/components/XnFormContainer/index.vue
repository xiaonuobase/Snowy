<template>
	<Modal v-if="isModal" :visible="visible" @cancel="cancel" v-bind="$attrs">
		<template v-for="slotKey in slotKeys" #[slotKey]>
			<slot :name="slotKey" />
		</template>
	</Modal>
	<Drawer v-else :visible="visible" v-bind="$attrs">
		<template v-for="slotKey in slotKeys" #[slotKey]>
			<slot :name="slotKey" />
		</template>
	</Drawer>
</template>

<script>
	import { Drawer, Modal } from 'ant-design-vue'
	import { modalProps } from 'ant-design-vue/es/modal/Modal'
	import { drawerProps } from 'ant-design-vue/es/drawer'

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
			type: {
				type: String,
				default: FormContainerTypeEnum.MODAL
			},
			visible: {
				type: Boolean,
				default: false,
				required: true
			},
			...modalProps,
			...drawerProps
		},
		computed: {
			slotKeys() {
				return Object.keys(this.$slots)
			},
			isModal() {
				return this.type === FormContainerTypeEnum.MODAL
			}
		},
		methods: {
			cancel() {
				this.$emit('close')
			}
		}
	}
</script>
