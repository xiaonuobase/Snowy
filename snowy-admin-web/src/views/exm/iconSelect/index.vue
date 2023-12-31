<template>
	<a-card :bordered="false" title="图标选择">
		<a-input-group compact style="width: 30%">
			<a-input v-model:value="iconValue" style="width: calc(100% - 100px)" placeholder="请选择图标" />
			<a-button type="primary" @click="openIcon(iconValue)">选择</a-button>
		</a-input-group>

		<icon-selector ref="iconselectorRef" @iconCallBack="iconCallBack" />
	</a-card>

	<a-card :bordered="false" title="图标展示" class="mt-3">
		<a-tabs v-model:activeKey="activeKey" tab-position="left" size="small" @change="paneChange">
			<a-tab-pane v-for="item in iconData" :key="item.key" :tab="item.name">
				<div v-if="item.iconItem.length > 1" class="xn-icon-select-radio">
					<a-radio-group v-model:value="iconItemDefault" @change="radioGroupChange">
						<a-radio-button v-for="iconItem in item.iconItem" :value="iconItem.key">{{ iconItem.name }}</a-radio-button>
					</a-radio-group>
				</div>

				<div v-for="iconItemIns in item.iconItem">
					<div v-if="iconItemIns.key === iconItemDefault" class="xn-icon-select-list">
						<ul>
							<li
								v-for="icon in iconItemIns.item"
								:key="icon"
								:class="icon === modelValue ? 'active' : ''"
								@click="selectIcon(icon)"
							>
								<component :is="icon" class="xn-icons" />
							</li>
						</ul>
					</div>
				</div>
			</a-tab-pane>
		</a-tabs>
	</a-card>
</template>

<script setup name="iconSelect">
	import IconSelector from '@/components/Selector/iconSelector.vue'
	import config from '@/config/iconSelect'
	import { message } from 'ant-design-vue'

	const iconselectorRef = ref()
	const iconValue = ref('')

	// 打开icon选择器
	const openIcon = (iconValue) => {
		iconselectorRef.value.showIconModal(iconValue)
	}
	// 选择后回调
	const iconCallBack = (value) => {
		iconValue.value = value
	}

	// 下面是平铺的图标
	const activeKey = ref('default')
	const iconItemDefault = ref('default')
	const modelValue = ref('')
	const iconData = config.icons
	const paneChange = (e) => {
		if (e.indexOf('default') === -1) {
			iconItemDefault.value = 'default'
		}
	}
	const radioGroupChange = (e) => {
		iconItemDefault.value = e.target.value
	}
	// 选择图标后
	const selectIcon = (value) => {
		message.success('<' + value + ' />')
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
