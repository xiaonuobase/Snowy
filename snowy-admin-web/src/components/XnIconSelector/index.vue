<template>
	<div class="icon-selector">
		<a-popover
			v-model:open="visible"
			trigger="click"
			placement="bottomLeft"
			:overlayStyle="{ width: '500px' }"
			@openChange="handleOpenChange"
		>
			<template #content>
				<div class="icon-selector-content">
					<a-tabs v-model:activeKey="activeKey" tab-position="left" size="small" @change="handleTabChange">
						<a-tab-pane v-for="group in iconData" :key="group.key" :tab="group.name">
							<div v-if="group.iconItem.length > 1" class="icon-category">
								<a-form-item-rest>
									<a-radio-group v-model:value="currentCategory" @change="handleCategoryChange" size="small">
										<a-radio-button v-for="item in group.iconItem" :key="item.key" :value="item.key">
											{{ item.name }}
										</a-radio-button>
									</a-radio-group>
								</a-form-item-rest>
							</div>
							<div class="icon-grid">
								<template v-for="iconGroup in group.iconItem" :key="iconGroup.key">
									<template v-if="iconGroup.key === currentCategory">
										<div
											v-for="icon in iconGroup.item"
											:key="icon"
											class="icon-item"
											:class="{ active: icon === selectedIcon }"
											@click="handleIconSelect(icon)"
										>
											<component :is="icon" class="icon-preview" />
										</div>
									</template>
								</template>
							</div>
						</a-tab-pane>
					</a-tabs>
				</div>
			</template>
			<a-input
				:value="showIconName ? selectedIcon : ''"
				:size="size"
				:disabled="disabled"
				:placeholder="placeholder"
				:style="{ width: '100%' }"
				readonly
			>
				<template #prefix>
					<component v-if="selectedIcon" :is="selectedIcon" />
					<SearchOutlined v-else />
				</template>
				<template #suffix>
					<close-circle-outlined v-if="selectedIcon && !disabled" class="clear-icon" @click.stop="handleClear" />
				</template>
			</a-input>
		</a-popover>
	</div>
</template>

<script setup>
	import { ref } from 'vue'
	import config from '@/config/iconSelect'
	import { SearchOutlined, CloseCircleOutlined } from '@ant-design/icons-vue'

	const props = defineProps({
		value: {
			type: String,
			default: ''
		},
		size: {
			type: String,
			default: 'middle'
		},
		disabled: {
			type: Boolean,
			default: false
		},
		placeholder: {
			type: String,
			default: '请选择图标'
		},
		showIconName: {
			type: Boolean,
			default: true
		}
	})
	const formRef = defineModel('formRef')
	const emit = defineEmits(['update:value', 'change'])

	const selectedIcon = ref()
	const iconData = ref(config.icons)
	const visible = ref(false)
	const activeKey = ref(iconData.value[0]?.key || '')
	const currentCategory = ref('default')

	watch(
		() => props.value,
		(newVal) => {
			selectedIcon.value = newVal
		}
	)

	const handleOpenChange = (isOpen) => {
		if (!props.disabled) {
			visible.value = isOpen
		}
	}

	const handleCategoryChange = (e) => {
		currentCategory.value = e.target.value
	}

	const handleIconSelect = (icon) => {
		emit('update:value', icon)
		formRef.value?.validateFields('icon')
		visible.value = false
	}

	const handleClear = () => {
		emit('update:value', '')
		selectedIcon.value = ''
		formRef.value?.validateFields('icon')
	}

	const handleTabChange = (key) => {
		activeKey.value = key
		// 重置当前分类为default
		currentCategory.value = 'default'
	}
</script>

<style lang="less" scoped>
	.icon-selector {
		width: 100%;

		:deep(.ant-input) {
			cursor: pointer;
		}

		:deep(.clear-icon) {
			cursor: pointer;
			color: rgba(0, 0, 0, 0.25);
			transition: color 0.3s;
			font-size: 12px;

			&:hover {
				color: rgba(0, 0, 0, 0.45);
			}
		}
	}

	.icon-category {
		margin-bottom: 10px;
	}

	.icon-selector-content {
		max-height: 350px;
		overflow: hidden;

		:deep(.ant-tabs-left) {
			.ant-tabs-nav {
				width: 120px;
				background-color: #fafafa;
				border-right: 1px solid #f0f0f0;

				.ant-tabs-tab {
					padding: 12px 16px;
					margin: 0;
					font-size: 14px;
					color: #666;
					transition: all 0.3s;

					&:hover {
						color: var(--primary-color);
						background-color: #f0f5ff;
					}

					&.ant-tabs-tab-active {
						color: var(--primary-color);
						background-color: #f0f5ff;
						font-weight: 500;

						.ant-tabs-tab-btn {
							color: var(--primary-color);
						}
					}
				}
			}

			.ant-tabs-content-holder {
				border-left: none;
				padding-left: 16px;
			}
		}
	}

	.icon-grid {
		display: grid;
		grid-template-columns: repeat(auto-fill, minmax(48px, 1fr));
		gap: 2px;
		max-height: 280px;
		overflow-y: auto;

		&::-webkit-scrollbar {
			width: 4px;
		}

		&::-webkit-scrollbar-thumb {
			background-color: rgba(0, 0, 0, 0.2);
			border-radius: 4px;
		}

		&::-webkit-scrollbar-track {
			background-color: transparent;
		}
	}

	.icon-item {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 8px;
		border: 1px solid #e8e8e8;
		border-radius: 2px;
		cursor: pointer;
		transition: all 0.3s;

		&:hover {
			border-color: var(--primary-color);
			background-color: #f0f5ff;
		}

		&.active {
			border-color: var(--primary-color);
			background-color: #f0f5ff;
		}

		.icon-preview {
			font-size: 14px;
		}
	}

	:deep(.ant-tabs-tab) {
		padding: 8px !important;
	}

	:deep(.ant-tabs-nav) {
		width: 60px !important;
	}

	:deep(.ant-tabs-tabpane) {
		padding-left: 0 !important;
	}
</style>
