<template>
	<div>
		<button @click="showDrawer">Show Drawer</button>
		<button @click="showModal">Show Modal</button>
		<xn-form-container
			v-model:visible="drawerVisible"
			:type="drawerType"
			title="Drawer Title"
			placement="right"
			width="300px"
			:destroyOnClose="false"
			@ok="onOkDrawer"
			@close="onCloseDrawer"
		>
			<p>Drawer Content</p>
			<template #footer>
				<div>Drawer footer</div>
			</template>
			<template #title>
				<div>Drawer title</div>
			</template>
		</xn-form-container>
		<xn-form-container
			v-model:visible="modalVisible"
			:type="modalType"
			title="Modal Title"
			:width="800"
			:destroyOnClose="false"
			@ok="onOkModal"
			@close="onCloseModal"
		>
			<p>Modal Content</p>
			<template #footer>
				<div>Modal footer</div>
			</template>
			<template #title>
				<div>Modal title</div>
			</template>
		</xn-form-container>
	</div>
</template>

<script>
	import XnFormContainer from './XnFormContainer'

	export default {
		name: 'Example',
		components: {
			XnFormContainer
		},
		data() {
			return {
				drawerVisible: false,
				drawerType: 'drawer',
				modalVisible: false,
				modalType: 'modal'
			}
		},
		methods: {
			showDrawer() {
				this.drawerVisible = true
			},
			showModal() {
				this.modalVisible = true
			},
			onOkDrawer() {
				this.$message.success('onOkDrawer')
				this.drawerVisible = false
			},
			onCloseModal() {
				this.$message.success('onCloseModal')
				this.modalVisible = false
			},
			onCloseDrawer() {
				this.$message.success('onCloseDrawer')
				this.drawerVisible = false
			},
			onOkModal() {
				this.$message.success('onOkModal')
				this.modalVisible = false
			}
		}
	}
</script>
