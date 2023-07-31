<template>
	<drag-modal v-if="isModal" :visible="visible" v-bind="$attrs">
		<template v-for="slotKey in slotKeys" #[slotKey]>
			<slot :name="slotKey" />
		</template>
	</drag-modal>
	<a-drawer v-else :visible="visible" v-bind="$attrs" :footer-style="{ textAlign: 'right' }">
		<template v-for="slotKey in slotKeys" #[slotKey]>
			<slot :name="slotKey" />
		</template>
	</a-drawer>
</template>

<script>
	import { mapState } from 'pinia'
	import { globalStore } from '@/store'
	import DragModal from "@/components/DragModal/index.vue";

	const FormContainerTypeEnum = {
		DRAWER: 'drawer',
		MODAL: 'modal'
	}
	export default {
		name: 'XnFormContainer',
		components: { DragModal },
		inheritAttrs: false,
		props: {
			visible: {
				type: Boolean,
				default: false,
				required: true
			}
		},
		computed: {
			...mapState(globalStore, ['formStyle']),
			slotKeys() {
				return Object.keys(this.$slots)
			},
			isModal() {
				return FormContainerTypeEnum.MODAL === this.formStyle
			}
		}
	}
</script>
