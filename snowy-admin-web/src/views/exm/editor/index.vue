<template>
	<a-card>
		<xn-editor v-model="html" placeholder="请输入" :height="400"></xn-editor>
	</a-card>
</template>

<script setup name="exmEditor">
	import XnEditor from '@/components/Editor/index.vue'
	const html = ref('')
</script>
