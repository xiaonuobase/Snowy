<template>
	<div class="setting-drawer-index-content">
		<div class="scrollbar">
			<h3>整体风格设置</h3>
			<div class="snowy-setting-checkbox">
				<a-tooltip v-for="(a, i) in sideStyleList" :key="i" placement="top">
					<template #title>
						<span>{{ a.tips }}</span>
					</template>
					<div :class="['snowy-setting-checkbox-item', a.style]" @click="setSideStyle(a.value)">
						<check-outlined v-if="theme === a.value" class="snowy-setting-checkbox-item-select-icon" />
					</div>
				</a-tooltip>
			</div>
			<h3>整体界面布局</h3>
			<div class="snowy-setting-checkbox">
				<a-tooltip v-for="(a, i) in layoutList" :key="i" placement="top">
					<template #title>
						<span>{{ a.tips }}</span>
					</template>

					<div :class="['snowy-setting-checkbox-item', a.style]" @click="layoutStyle(a.value)">
						<div class="snowy-setting-layout-menu-doublerow-inner" />
						<check-outlined v-if="layout === a.value" class="snowy-setting-checkbox-item-select-icon" />
					</div>
				</a-tooltip>
			</div>
			<a-divider />
			<div class="mb-4">
				<h3>主题色</h3>
				<div class="h-[50px]">
					<a-tooltip v-for="(item, index) in colorList" :key="index" class="snowy-setting-theme-color-colorBlock">
						<template #title>
							<span>{{ item.key }}</span>
						</template>
						<a-tag :color="item.color" @click="tagColor(item.color)">
							<check-outlined v-if="themeColor === item.color" />
						</a-tag>
					</a-tooltip>
				</div>
			</div>
			<div class="mb-4 layout-slide">
				<h4 class="">顶栏应用主题色：</h4>
				<a-switch :checked="topHanderThemeColorOpen" @change="changeTopHanderThemeColorOpen" />
			</div>
			<div class="mb-4 layout-slide">
				<h4>顶栏主题色通栏：</h4>
				<a-switch
					style="float: right"
					:checked="topHanderThemeColorSpread"
					:disabled="!topHanderThemeColorOpen"
					@change="changeTopHanderThemeColorSpread"
				/>
			</div>
			<a-divider />
			<a-form ref="form" class="text-right">
				<a-form-item label="模块坞">
					<a-switch :checked="moduleUnfoldOpen" @change="toggleState('moduleUnfoldOpen')" />
				</a-form-item>
				<a-form-item label="面包屑">
					<a-switch :checked="breadcrumbOpen" @change="toggleState('breadcrumbOpen')" />
				</a-form-item>
				<a-form-item label="多标签">
					<a-switch :checked="layoutTagsOpen" @change="toggleState('layoutTagsOpen')" />
				</a-form-item>
				<a-form-item label="折叠菜单">
					<a-switch :checked="menuIsCollapse" @change="toggleState('menuIsCollapse')" />
				</a-form-item>
				<a-form-item label="菜单排他展开">
					<a-switch :checked="sideUniqueOpen" @change="toggleState('sideUniqueOpen')" />
				</a-form-item>
				<a-form-item label="表单风格">
					<a-select
						:value="formStyle"
						class="!w-[80px]"
						size="small"
						:options="xnFormStyleOptions"
						@change="formStyleChange"
					/>
				</a-form-item>
			</a-form>
			<a-alert
				message="以上配置可实时预览，开发者可在 config/index.js 中配置默认值，不建议在生产环境下开放布局设置"
				type="warning"
			/>
		</div>
	</div>
</template>

<script>
	import { colorList } from '@/config/settingConfig'
	import { ThemeModeEnum } from '@/utils/enum'
	import { globalStore } from '@/store'
	import { mapState, mapStores } from 'pinia'
	import tool from '@/utils/tool'

	const toolDataNameMap = {
		menuIsCollapse: 'MENU_COLLAPSE',
		sideUniqueOpen: 'SIDE_UNIQUE_OPEN',
		layoutTagsOpen: 'LAYOUT_TAGS_OPEN',
		breadcrumbOpen: 'BREADCRUMD_OPEN',
		topHanderThemeColorOpen: 'TOP_HANDER_THEME_COLOR_OPEN',
		topHanderThemeColorSpread: 'TOP_HANDER_THEME_COLOR_SPREAD',
		moduleUnfoldOpen: 'MODULE_UNFOLD_OPEN'
	}
	export default defineComponent({
		data() {
			return {
				sideStyleList: [
					{
						tips: '暗色主题风格',
						value: ThemeModeEnum.DARK,
						style: 'snowy-setting-checkbox-item-dark'
					},
					{
						tips: '亮色主题风格',
						value: ThemeModeEnum.LIGHT,
						style: 'snowy-setting-checkbox-item-light'
					},
					{
						tips: '暗黑模式',
						value: ThemeModeEnum.REAL_DARK,
						style: 'snowy-setting-checkbox-item-realdark'
					}
				],
				layoutList: [
					{
						tips: '经典',
						value: 'classical',
						style: 'snowy-setting-layout-menu-classical'
					},
					{
						tips: '双排菜单',
						value: 'doublerow',
						style: 'snowy-setting-layout-menu-doublerow'
					}
				],
				xnFormStyleOptions: [
					{
						label: '抽屉',
						value: 'drawer'
					},
					{
						label: '对话框',
						value: 'modal'
					}
				],
				colorList
			}
		},
		computed: {
			...mapStores(globalStore),
			...mapState(globalStore, [
				'theme',
				'themeColor',
				'layout',
				'menuIsCollapse',
				'sideUniqueOpen',
				'layoutTagsOpen',
				'breadcrumbOpen',
				'moduleUnfoldOpen',
				'topHanderThemeColorOpen',
				'topHanderThemeColorSpread',
				'formStyle'
			])
		},
		mounted() {},
		methods: {
			changeTopHanderThemeColorOpen() {
				this.toggleState('topHanderThemeColorOpen')
				if (!this.topHanderThemeColorOpen) {
					this.globalStore.topHanderThemeColorSpread = false
					tool.data.set('SNOWY_TOP_HANDER_THEME_COLOR_SPREAD', false)
				}
			},
			changeTopHanderThemeColorSpread() {
				this.toggleState('topHanderThemeColorSpread')
			},
			toggleState(stateName) {
				this.globalStore.toggleConfig(stateName)
				const toolDataName = toolDataNameMap[stateName]
				tool.data.set(`SNOWY_${toolDataName}`, this.globalStore[stateName])
			},
			// 设置整体风格主题
			setSideStyle(value) {
				this.globalStore.setTheme(value)
				tool.data.set('SNOWY_THEME', value)
			},
			// 设置整体界面布局
			layoutStyle(value) {
				this.globalStore.setLayout(value)
				tool.data.set('SNOWY_LAYOUT', value)
			},
			// 切换颜色
			tagColor(value) {
				tool.data.set('SNOWY_THEME_COLOR', value)
				this.globalStore.setThemeColor(value)
			},
			// 切换表单风格
			formStyleChange(value) {
				tool.data.set('SNOWY_FORM_STYLE', value)
				this.globalStore.setFormStyle(value)
			}
		}
	})
</script>

<style type="less" scoped>
	.snowy-setting-checkbox {
		display: flex;
		margin-bottom: 20px;
	}
	.snowy-setting-checkbox-item {
		position: relative;
		width: 44px;
		height: 36px;
		margin-right: 16px;
		overflow: hidden;
		background-color: #ebeef1;
		border-radius: 2px;
		box-shadow: 0 1px 2.5px 0 rgb(0 0 0 / 18%);
		cursor: pointer;
	}
	.snowy-setting-checkbox-item::before {
		position: absolute;
		top: 0;
		left: 0;
		width: 33%;
		height: 100%;
		background-color: #fff;
		content: '';
	}
	.snowy-setting-checkbox-item::after {
		position: absolute;
		top: 0;
		left: 0;
		width: 100%;
		height: 25%;
		background-color: #fff;
		content: '';
	}
	.snowy-setting-checkbox-item-light {
		background-color: #ebeef1;
		content: '';
	}
	.snowy-setting-checkbox-item-light::before {
		background-color: #fff;
		content: '';
	}
	.snowy-setting-checkbox-item-light::after {
		background-color: #fff;
	}
	.snowy-setting-checkbox-item-dark {
		z-index: 1;
		background-color: #ebeef1;
		content: '';
	}
	.snowy-setting-checkbox-item-dark::before {
		z-index: 1;
		background-color: #001529;
		content: '';
	}
	.snowy-setting-checkbox-item-dark::after {
		position: absolute;
		top: 0;
		left: 0;
		width: 100%;
		height: 25%;
		background-color: #fff;
		content: '';
	}
	.snowy-setting-checkbox-item-realdark {
		background-color: rgba(0, 21, 41, 0.85);
	}
	.snowy-setting-checkbox-item-realdark::before {
		z-index: 1;
		background-color: rgba(0, 21, 41, 0.65);
		content: '';
	}
	.snowy-setting-checkbox-item-realdark::after {
		background-color: rgba(0, 21, 41, 0.85);
	}
	.snowy-setting-checkbox-item-select-icon {
		position: absolute;
		right: 8px;
		bottom: 8px;
		color: #1890ff;
		font-weight: 700;
		font-size: 14px;
		pointer-events: none;
	}

	.snowy-setting-theme-color-colorBlock {
		margin-top: 8px;
		width: 20px;
		height: 20px;
		border-radius: 2px;
		float: left;
		cursor: pointer;
		margin-right: 8px;
		padding-left: 0px;
		padding-right: 0px;
		text-align: center;
		color: #fff;
		font-weight: 700;
	}

	.snowy-setting-layout-menu-doublerow {
		z-index: 1;
		background-color: #ebeef1;
		content: '';
	}
	.snowy-setting-layout-menu-doublerow::before {
		z-index: 1;
		width: 16%;
		background-color: #001529;
		content: '';
	}
	.snowy-setting-layout-menu-doublerow-inner {
		position: absolute;
		top: 0;
		left: 0;
		display: block;
		width: 33%;
		height: 100%;
		background-color: #fff;
		content: '';
	}
	.snowy-setting-layout-menu-doublerow::after {
		position: absolute;
		top: 0;
		left: 0;
		width: 100%;
		height: 25%;
		background-color: #fff;
		content: '';
	}
	.snowy-setting-layout-menu-classical {
		z-index: 1;
		background-color: #ebeef1;
		content: '';
	}
	.snowy-setting-layout-menu-classical::before {
		z-index: 1;
		background-color: #001529;
		content: '';
	}
	.snowy-setting-layout-menu-classical::after {
		position: absolute;
		top: 0;
		left: 0;
		width: 100%;
		height: 25%;
		background-color: #fff;
		content: '';
	}

	.scrollbar {
		margin: 0 auto;
	}
</style>
