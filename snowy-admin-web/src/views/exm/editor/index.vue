<template>
	<a-card>
		<xn-editor v-model:value="html" placeholder="请输入" :height="400"></xn-editor>
	</a-card>
</template>

<script setup name="exmEditor">
	const html = ref('')
</script>
