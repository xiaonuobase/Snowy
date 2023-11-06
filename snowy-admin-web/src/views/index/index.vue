<template>
	<a-row :gutter="10">
		<a-col :span="16" :xs="24" :sm="24" :md="24" :lg="16" :xl="16">
			<userInfo class="mb-2" />
			<shortcut class="mb-2" />
			<a-row :gutter="10">
				<a-col :span="12" :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
					<visLog class="mb-2" />
				</a-col>
				<a-col :span="12" :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
					<opLog class="mb-2" />
				</a-col>
			</a-row>
		</a-col>
		<a-col :span="8" :xs="24" :sm="24" :md="24" :lg="8" :xl="8">
			<carousel class="mb-2" />
			<schedule class="mb-2" />
			<miniMessage class="mb-2" />
		</a-col>
	</a-row>
</template>

<script setup name="indexHome">
	import UserInfo from './components/userInfo.vue'
	import Shortcut from './components/shortcut.vue'
	import Schedule from './components/schedule.vue'
	import MiniMessage from './components/miniMessage.vue'
	import Carousel from './components/carousel.vue'
	import VisLog from './components/visLog.vue'
	import OpLog from './components/opLog.vue'
</script>
