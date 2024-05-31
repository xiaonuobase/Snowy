<template>
	<a-list :grid="grid" :data-source="props.dataSource" :pagination="pagination" :loading="loading">
		<template #renderItem="{ item, index }">
			<a-list-item :key="index" class="xn-card-list-item">
				<a-badge-ribbon :text="item.badge.text" :color="item.badge.color ? item.badge.color : ''">
					<a-card class="xn-card">
						<template #title>
							<a-tag color="orange">{{ item.title }}</a-tag>
						</template>
						<template #actions>
							<template v-for="{ key, label, button, icon, color } in props.actions" :key="key">
								<a-button v-if="button" type="link" size="small" @click.stop="doAction(key, item)">
									<template #icon>
										<component :is="icon" :style="{ color: color }" />
									</template>
									<span :style="{ color: color }">{{ label }}</span>
								</a-button>
								<a-tooltip v-else :title="label">
									<component :is="icon" @click.stop="doAction(key, item)" :style="{ color: color }" />
								</a-tooltip>
							</template>
							<slot name="custom-action-component" :record="item.record" />
						</template>
						<a-card-meta class="xn-card-meta">
							<template #avatar>
								<a-avatar shape="square" :size="60" :src="item.img" />
							</template>
							<template #title>
								<span class="xn-card-meta-title">{{ item.subTitle }}</span>
							</template>
							<template #description>
								<div v-if="item.contents">
									<div v-for="content in item.contents" :key="content.value">
										<span>{{ content.label }}：{{ content.value }}</span>
									</div>
								</div>
							</template>
						</a-card-meta>
					</a-card>
				</a-badge-ribbon>
			</a-list-item>
		</template>
	</a-list>
</template>

<script setup name="xnCardList">
	import { message } from 'ant-design-vue'

	const props = defineProps({
		// grid布局
		grid: {
			type: Object,
			default: () => {
				return { gutter: 20, xs: 1, sm: 1, md: 2, lg: 2, xl: 3, xxl: 4, xxxl: 4 }
			}
		},
		// 数据源
		dataSource: {
			type: Array,
			required: true
		},
		// 分页
		page: {
			type: Object,
			required: true
		},
		// 动作
		actions: {
			type: Array,
			default: () => []
		},
		loading: {
			type: Boolean,
			default: false
		}
	})
	const emit = defineEmits(['action', 'page-change'])

	// 分页参数
	const { current, size, total } = toRefs(props.page)
	const pagination = reactive({
		onChange: (current) => {
			emit('page-change', current)
		},
		current: current,
		pageSize: size,
		total: total
	})

	// 触发 action
	const doAction = (key, item) => {
		if (!item.record) {
			message.error('记录参数[record]错误')
			return
		}
		emit('action', { key, record: item.record })
	}
</script>

<style lang="less" scoped>
	.xn-card {
		background: linear-gradient(141.6deg, var(--primary-1) 0%, rgba(255, 255, 255, 0) 70%);
		box-shadow: 0 0 10px 0 rgba(0, 0, 0, 0.1);
		border-radius: 10px;

		.xn-card-meta {
			display: flex;
			align-items: center;

			.xn-card-meta-title {
				font-size: 14px;
			}
		}
	}
</style>
<style lang="less">
	.xn-card-list-item {
		padding: initial !important;
	}
</style>
