<template>
	<div class="msg panel-item" :hidden="msgList.length == 0" @click="showMsg">
		<a-badge :count="msgList.length" class="badge">
			<comment-outlined />
		</a-badge>
		<a-drawer v-model:visible="msgVisible" title="新消息" placement="right">
			<a-list :data-source="msgList" size="small" class="mb-3">
				<template #renderItem="{ item }">
					<a-list-item>
						<a-list-item-meta :description="item.createTime">
							<template #title>
								<a @click="messageDetail(item)">{{ item.subject }}</a>
							</template>
						</a-list-item-meta>
					</a-list-item>
				</template>
			</a-list>
			<a-space>
				<a-button type="primary">消息中心</a-button>
				<a-button @click="markRead">全部设为已读</a-button>
			</a-space>
		</a-drawer>
	</div>
</template>

<script setup name="devUserMessage">
	const msgVisible = ref(false)
	const msgList = ref([])

	msgList.value = [
		{
			subject: '凌晨发来一份电报',
			createTime: '2022-09-05 22:29:02'
		},
		{
			subject: '听说2.0要发布了，是真的吗？',
			createTime: '2022-09-05 22:29:02'
		}
	]
	// 显示站内信
	const showMsg = () => {
		msgVisible.value = true
	}
	// 标记已读
	const markRead = () => {
		msgList.value = []
	}
</script>

<style scoped></style>
