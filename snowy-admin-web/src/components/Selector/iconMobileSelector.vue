<template>
	<a-modal
		v-model:open="visible"
		title="移动端图标选择"
		:mask-closable="false"
		:width="800"
		:destroy-on-close="true"
		:footer="null"
		@cancel="onCancel"
	>
		<a-tabs v-model:activeKey="activeKey" tab-position="left" size="small" @change="paneChange">
			<a-tab-pane v-for="item in iconData" :key="item.key" :tab="item.name">
				<div v-if="item.iconItem.length > 1" class="xn-icon-select-radio">
					<a-radio-group v-model:value="iconItemDefault" @change="radioGroupChange">
						<a-radio-button v-for="iconItem in item.iconItem" :key="iconItem.key" :value="iconItem.key">{{
							iconItem.name
						}}</a-radio-button>
					</a-radio-group>
				</div>

				<div :key="iconItemIns" v-for="iconItemIns in item.iconItem">
					<div v-if="iconItemIns.key === iconItemDefault" class="xn-icon-select-list">
						<ul>
							<li
								v-for="icon in iconItemIns.item"
								:key="icon"
								:class="icon === modelValue ? 'active' : ''"
								@click="selectIcon(icon.font_class)"
							>
								<span class="snowy xn-icons" :class="icon.font_class"></span>
							</li>
						</ul>
					</div>
				</div>
			</a-tab-pane>
		</a-tabs>
	</a-modal>
</template>
<script setup>
	import config from '@/assets/icons/mobile'
	const visible = ref(false)
	const iconData = ref([])
	const modelValue = ref('')
	const activeKey = ref('default')
	const iconItemDefault = ref('default')

	onMounted(() => {
		iconData.value.push(...config.icons)
	})

	// 打开
	const showIconModal = (value) => {
		visible.value = true
		defaultSetting(value)
	}

	// 暴露子组件的方法
	defineExpose({
		showIconModal
	})
	// 默认配置
	const defaultSetting = (value) => {
		if (value) {
			modelValue.value = value
			// 判断展开哪个
			if (value.indexOf('-outlined') > -1 || value.indexOf('-filled') > -1 || value.indexOf('-two-tone') > -1) {
				activeKey.value = 'default'
				if (value.indexOf('-two-tone') > -1) {
					iconItemDefault.value = 'twotone'
				} else if (value.indexOf('-filled') > -1) {
					iconItemDefault.value = 'filled'
				}
			} else if (value.indexOf('-extend') > -1) {
				// 扩展列表
				activeKey.value = 'extend'
				// 如扩展其他顶部单选的情况，默认选中在这里配置,同时这里需要做判断
				// this.iconItemDefault = '您的json中配置的'
			}
		}
	}

	// 切换标签页，如果是切换到了没用额外的标签页的地方，我们将其置为默认
	const paneChange = (e) => {
		iconData.value
			.find((tabItem) => tabItem.key === e)
			?.iconItem.some((groupItem) => groupItem.key === iconItemDefault.value) || (iconItemDefault.value = 'default')
	}

	// 切换icon风格
	const radioGroupChange = (e) => {
		iconItemDefault.value = e.target.value
	}
	const emit = defineEmits(['iconCallBack'])

	// 选择图标后关闭并返回
	const selectIcon = (value) => {
		visible.value = false
		// eslint-disable-next-line vue/require-explicit-emits
		emit('iconCallBack', value)
	}

	const onCancel = () => {
		visible.value = false
	}
</script>

<style lang="less" scoped>
	.xn-icon-select-radio {
		padding-left: 5px;
		padding-bottom: 10px;
	}
	.xn-icons {
		font-size: 26px;
		width: 100%;
		height: 100%;
		display: flex;
		justify-content: center;
		align-items: center;
	}
	.xn-icon-select-list {
		height: 360px;
		overflow: auto;
	}
	.xn-icon-select-list ul {
		li {
			display: inline-block;
			width: 60px;
			height: 60px;
			padding: 18px;
			margin: 5px;
			border-radius: 2px;
			vertical-align: top;
			box-shadow: 0 0 0 1px var(--border-color-split);
			transition: all 0.1s;
			position: relative;

			&:hover,
			&.active {
				cursor: pointer;
				color: #ffffff;
				background-color: var(--primary-color);
			}
		}
	}
</style>
