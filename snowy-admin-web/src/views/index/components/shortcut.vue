<template>
	<a-card :bordered="false">
		<template #title> 快捷方式 </template>
		<a-row :gutter="10">
			<a-col :span="6" :key="shortcut.id" v-for="shortcut in shortcutList">
				<shortcutCard
					:icon="shortcut.icon ? shortcut.icon : 'menu-outlined'"
					:label="shortcut.title"
					@click="leaveFor(shortcut.path)"
				/>
			</a-col>
		</a-row>
	</a-card>
</template>

<script setup name="shortcut">
	import router from '@/router'
	import userCenterApi from '@/api/sys/userCenterApi'
	import shortcutCard from '@/components/ShortcutCard/index.vue'
	import { onMounted } from 'vue'
	const shortcutList = ref([])

	onMounted(() => {
		// 进来后执行查询
		getUserLoginWorkbench()
	})

	const getUserLoginWorkbench = () => {
		userCenterApi.userLoginWorkbench().then((data) => {
			shortcutList.value = JSON.parse(data).shortcut
		})
	}

	const leaveFor = (url = '/') => {
		router.replace({
			path: url
		})
	}
</script>

<style scoped>
	.ant-list-item {
		padding: 8px 0px !important;
	}
</style>
