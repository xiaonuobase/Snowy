<template>
	<a-result status="404" title="404" sub-title="对不起，您访问的页面不存在。">
		<template #extra>
			<a-button type="primary" @click="gohome">返回首页</a-button>
			<a-button type="dashed" @click="goback">返回上一页</a-button>
		</template>
	</a-result>
</template>
<script setup>
	import { useRouter } from 'vue-router'
	import { useMenuStore } from '@/store/menu'
	const router = useRouter()
	const gohome = () => {
		location.href = '/'
	}
	const goback = () => {
		router.go(-1)
		useMenuStore().changeRefreshFlag(true)
	}
</script>
