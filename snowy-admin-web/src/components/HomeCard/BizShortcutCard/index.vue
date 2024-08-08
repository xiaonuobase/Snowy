<template>
	<a-card :title="title" :bordered="false" :loading="apiLoading">
		<div class="card-div">
			<a-row :gutter="10">
				<a-col :span="6" :key="shortcut.id" v-for="shortcut in shortcutList" :xs="12" :sm="8" :md="6" :lg="8" :xl="6">
					<shortcutCard
						:icon="shortcut.icon ? shortcut.icon : 'menu-outlined'"
						:label="shortcut.title"
						@click="leaveFor(shortcut.path)"
					/>
				</a-col>
			</a-row>
		</div>
	</a-card>
</template>

<script setup name="shortcut">
	import router from '@/router'
	import userCenterApi from '@/api/sys/userCenterApi'
	import ShortcutCard from '@/components/ShortcutCard/index.vue'
	import { onMounted } from 'vue'
	const shortcutList = ref([])
	const title = ref('快捷方式')
	const apiLoading = ref(false)
	onMounted(() => {
		// 进来后执行查询
		getUserLoginWorkbench()
	})
	const getUserLoginWorkbench = () => {
		apiLoading.value = true
		userCenterApi
			.userLoginWorkbench()
			.then((data) => {
				if (data) {
					shortcutList.value = JSON.parse(data).shortcut
				}
			})
			.catch(() => {})
			.finally(() => {
				apiLoading.value = false
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
	.card-div {
		overflow: scroll;
		max-height: 260px;
	}
	:deep(.ant-card-body) {
		padding-top: 0 !important;
	}
</style>
