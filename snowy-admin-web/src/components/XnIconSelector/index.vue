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
										<!-- PC端图标：Vue组件方式 -->
										<template v-if="!isMobileIcon">
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
										<!-- 移动端图标：iconfont字体方式 -->
										<template v-else>
											<div
												v-for="icon in iconGroup.item"
												:key="icon.font_class"
												class="icon-item"
												:class="{ active: icon.font_class === selectedIcon }"
												@click="handleIconSelect(icon)"
											>
												<span class="snowy xn-icons" :class="icon.font_class"></span>
											</div>
										</template>
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
					<!-- PC端图标预览 -->
					<component v-if="selectedIcon && !isMobileIcon" :is="selectedIcon" />
					<!-- 移动端图标预览 -->
					<span v-else-if="selectedIcon && isMobileIcon" class="snowy xn-icons" :class="selectedIcon"></span>
					<!-- 默认搜索图标 -->
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
	import { ref, watch, defineModel, computed } from 'vue'
	import pcConfig from '@/config/iconSelect'
	import mobileConfig from '@/assets/icons/mobile'
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
		},
		// 图标类型：pc 使用PC端图标，mobile 使用移动端图标
		iconType: {
			type: String,
			default: 'pc',
			validator: (value) => ['pc', 'mobile'].includes(value)
		}
	})
	const formRef = defineModel('formRef')
	const emit = defineEmits(['update:value', 'change'])

	const selectedIcon = ref()
	// 根据 iconType 选择对应的配置
	const iconData = computed(() => {
		return props.iconType === 'mobile' ? mobileConfig.icons : pcConfig.icons
	})
	// 判断是否是移动端图标（iconfont字体图标）
	const isMobileIcon = computed(() => props.iconType === 'mobile')

	const visible = ref(false)
	const activeKey = ref(iconData.value[0]?.key || '')
	const currentCategory = ref('default')

	// 初始化时同步一次，且后续保持与外部 v-model 一致
	selectedIcon.value = props.value
	watch(
		() => props.value,
		(newVal) => {
			selectedIcon.value = newVal
		},
		{ immediate: true }
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
		// 移动端图标需要取 font_class 属性
		const iconValue = isMobileIcon.value ? icon.font_class : icon
		selectedIcon.value = iconValue
		emit('update:value', iconValue)
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

		// 移动端iconfont图标样式
		.xn-icons {
			font-size: 20px;
			width: 100%;
			height: 100%;
			display: flex;
			justify-content: center;
			align-items: center;
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
