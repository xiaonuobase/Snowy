<template>
	<a-card>
		<a-button type="primary" @click="jump"> 跳转自己并带参数 </a-button>
		当然也可以跳到指定的路由啦
	</a-card>
</template>

<script setup name="exmNestRoutor">
	import { useRouter, useRoute } from 'vue-router'

	const router = useRouter()
	const route = useRoute()

	const jump = () => {
		router.push({
			path: '/exm/nestRoutor',
			query: {
				id: '12312321'
			}
		})
	}
</script>
