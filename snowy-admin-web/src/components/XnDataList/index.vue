<template>
	<a-list item-layout="vertical" :data-source="props.dataSource" :pagination="pagination" :loading="loading">
		<template #renderItem="{ item, index }">
			<a-list-item :key="index" class="xn-data-list-item">
				<a-list-item-meta class="xn-data-list-item-meta">
					<template #avatar>
						<a-avatar shape="square" :size="50">{{ index + 1 }}</a-avatar>
					</template>
					<template #title>
						<div class="xn-data-list-item-meta-title">
							<div class="xn-data-list-item-meta-title-prefix">
								<slot name="title-prefix" :record="item.record"></slot>
							</div>
							<a @click="clickTitle(item)">{{ item.title }}</a>
							<div class="xn-data-list-item-meta-title-suffix">
								<slot name="title-suffix" :record="item.record"></slot>
							</div>
						</div>
					</template>
					<template #description>
						<a-tooltip placement="top" v-for="description in item.descriptions">
							<template #title>
								<span>{{ description.title }}</span>
							</template>
							<a-tag>{{ description.content }}</a-tag>
						</a-tooltip>
					</template>
				</a-list-item-meta>
				<div class="xn-content" v-if="item.contents">
					<div class="xn-statistics" v-for="content in item.contents">
						<div class="xn-statistic-title">
							{{ content.title }}
						</div>
						<div class="xn-statistic-content">
							<span>{{ content.content }}</span>
						</div>
					</div>
				</div>
				<template #extra v-if="item.extra">
					<div class="xn-extra">
						<div class="xn-statistics">
							<div class="xn-statistic-title">{{ item.extra.title }}</div>
							<div class="xn-statistic-content">
								<span>{{ item.extra.content }}</span>
							</div>
						</div>
					</div>
				</template>
				<template #actions>
					<a-button type="link" @click="doAction(key, item)" v-for="{ key, label, icon, color } in props.actions">
						<template #icon>
							<component :is="icon" :style="{ color: color }" />
						</template>
						{{ label }}
					</a-button>
				</template>
			</a-list-item>
		</template>
	</a-list>
</template>

<script setup name="xnDataList">
	import { message } from 'ant-design-vue'

	const props = defineProps({
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
	const emit = defineEmits(['title', 'action', 'page-change'])

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

	// 出发 点击标题
	const clickTitle = (item) => {
		if (!item.record) {
			message.error('记录参数[record]错误')
			return
		}
		emit('title', { record: item.record })
	}
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
	.xn-data-list-item {
		box-shadow: 0 0 10px 0 rgba(0, 0, 0, 0.1);
		padding: 12px 15px;
		margin-bottom: 10px;

		.xn-data-list-item-meta {
			align-items: center;
			padding-bottom: 15px;
			border-bottom: 1px solid #f0f0f0;

			.xn-data-list-item-meta-title {
				display: flex;
				align-items: center;

				.xn-data-list-item-meta-title-prefix {
					padding-left: 5px;
				}

				a {
					color: var(--text-color);
					transition: all 0.3s;
					padding: 0 5px;
				}

				.xn-data-list-item-meta-title-suffix {
					display: flex;
					flex: 1;
					justify-content: flex-end;
				}
			}
		}

		.xn-content {
			//box-shadow: 0 0 4px 0 rgba(0, 0, 0, 0.1);
		}

		.xn-extra {
			display: flex;
			align-items: center;
			height: 100%;
		}
	}

	.xn-statistics {
		display: inline-block;
		box-sizing: border-box;
		margin: 5px 20px;
		padding: 10px;
		color: var(--text-color);
		font-size: 14px;
		font-variant: tabular-nums;
		line-height: 1.5715;
		list-style: none;
		font-feature-settings: 'tnum';

		&:hover {
			box-shadow: var(--card-shadow);
		}

		.xn-statistic-title {
			margin-bottom: 4px;
			color: var(--text-color-secondary);
			font-size: 14px;
		}

		.xn-statistic-content {
			color: var(--heading-color);
			font-size: 15px;
		}
	}
</style>
