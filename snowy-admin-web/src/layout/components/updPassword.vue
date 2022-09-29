<template>
	<a-modal
		v-model:visible="visible"
		title="修改密码"
		:mask-closable="false"
		:width="800"
		:destroy-on-close="true"
		@ok="handleOk"
		@cancel="handleCancel"
	>
	</a-modal>
</template>

<script>
	export default {
		data() {
			return {
				visible: false
			}
		},
		methods: {
			// 打开
			showUpdPwdModal(value) {
				this.visible = true
			},
			// 切换icon风格
			radioGroupChange(e) {
				this.iconItemDefault = e.target.value
			},
			// 选择图标后关闭并返回
			handleOk() {
				this.visible = false
				this.$emit('updPwdCallBack')
			},
			handleCancel() {
				this.visible = false
				this.$emit('updPwdCallBack')
			}
		}
	}
</script>
