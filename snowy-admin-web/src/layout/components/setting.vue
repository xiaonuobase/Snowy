<template>
	<div class="setting-drawer-index-content">
		<div class="scrollbar">
			<h3>整体风格设置</h3>
			<div class="snowy-setting-checkbox">
				<a-tooltip v-for="(a, i) in sideStyleList" :key="i" placement="top">
					<template #title
						><span>{{ a.tips }}</span></template
					>
					<div :class="['snowy-setting-checkbox-item', a.style]" @click="setSideStyle(a.value)">
						<check-outlined v-if="sideStyle === a.value" class="snowy-setting-checkbox-item-select-icon" />
					</div>
				</a-tooltip>
			</div>
			<h3>整体界面布局</h3>
			<div class="snowy-setting-checkbox">
				<a-tooltip v-for="(a, i) in layoutList" :key="i" placement="top">
					<template #title
						><span>{{ a.tips }}</span></template
					>
					<div :class="['snowy-setting-checkbox-item', a.style]" @click="layoutStyle(a.value)">
						<div class="snowy-setting-layout-menu-doublerow-inner" />
						<check-outlined v-if="layout === a.value" class="snowy-setting-checkbox-item-select-icon" />
					</div>
				</a-tooltip>
			</div>
			<a-divider />
			<div :style="{ marginBottom: '24px' }">
				<h3>主题色</h3>
				<div style="height: 50px">
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
			<div :style="{ marginBottom: '24px' }">
				<span
					><h4>顶栏应用主题色：<a-switch style="float: right" v-model:checked="topHanderThemeColorOpen" /></h4
				></span>
			</div>
			<div :style="{ marginBottom: '24px' }">
				<span
					><h4>
						顶栏主题色通栏：<a-switch
							style="float: right"
							v-model:checked="topHanderThemeColorSpread"
							:disabled="!topHanderThemeColorOpen"
						/></h4
				></span>
			</div>
			<a-divider />
			<a-form ref="form" style="text-align: right">
				<a-form-item label="模块坞">
					<a-switch v-model:checked="moduleUnfoldOpen" />
				</a-form-item>
				<a-form-item label="面包屑">
					<a-switch v-model:checked="breadcrumbOpen" />
				</a-form-item>
				<a-form-item label="多标签">
					<a-switch v-model:checked="layoutTagsOpen" />
				</a-form-item>
				<a-form-item label="折叠菜单">
					<a-switch v-model:checked="menuIsCollapse" />
				</a-form-item>
				<a-form-item label="菜单排他展开">
					<a-switch v-model:checked="sideUniqueOpen" />
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
	import { colorList } from '../../config/settingConfig'
	import { ThemeModeEnum } from '../../utils/enum'

	export default defineComponent({
		data() {
			return {
				// 整体风格
				sideStyle: this.$TOOL.data.get('SNOWY_THEME') || this.$store.state.global.theme,
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
				layout: this.$TOOL.data.get('SNOWY_LAYOUT') || this.$store.state.global.layout,
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
				topHanderThemeColorOpen:
					this.$TOOL.data.get('SNOWY_TOP_HANDER_THEME_COLOR_OPEN') || this.$store.state.global.topHanderThemeColorOpen,
				topHanderThemeColorSpread:
					this.$TOOL.data.get('SNOWY_TOP_HANDER_THEME_COLOR_SPREAD') ||
					this.$store.state.global.topHanderThemeColorSpread,
				menuIsCollapse: this.$TOOL.data.get('SNOWY_MENU_COLLAPSE') || this.$store.state.global.menuIsCollapse,
				sideUniqueOpen: this.$TOOL.data.get('SNOWY_SIDE_UNIQUE_OPEN') || this.$store.state.global.sideUniqueOpen,
				layoutTagsOpen: this.$TOOL.data.get('SNOWY_LAYOUT_TAGS_OPEN') || this.$store.state.global.layoutTagsOpen,
				breadcrumbOpen: this.$TOOL.data.get('SNOWY_BREADCRUMD_OPEN') || this.$store.state.global.breadcrumbOpen,
				moduleUnfoldOpen: this.$TOOL.data.get('SNOWY_MODULE_UNFOLD_OPEN') || this.$store.state.global.moduleUnfoldOpen,
				theme: this.$TOOL.data.get('APP_THEME') || this.$store.state.global.theme,
				themeColor: this.$TOOL.data.get('SNOWY_THEME_COLOR') || this.$store.state.global.themeColor,
				colorList
			}
		},
		watch: {
			menuIsCollapse() {
				this.$store.commit('TOGGLE_menuIsCollapse')
				if (this.$store.state.global.menuIsCollapse) {
					this.$TOOL.data.set('SNOWY_MENU_COLLAPSE', true)
				} else {
					this.$TOOL.data.set('SNOWY_MENU_COLLAPSE', false)
				}
			},
			sideUniqueOpen() {
				this.$store.commit('TOGGLE_sideUniqueOpen')
				if (this.$store.state.global.sideUniqueOpen) {
					this.$TOOL.data.set('SNOWY_SIDE_UNIQUE_OPEN', true)
				} else {
					this.$TOOL.data.set('SNOWY_SIDE_UNIQUE_OPEN', false)
				}
			},
			layoutTagsOpen() {
				this.$store.commit('TOGGLE_layoutTagsOpen')
				if (this.$store.state.global.layoutTagsOpen) {
					this.$TOOL.data.set('SNOWY_LAYOUT_TAGS_OPEN', true)
				} else {
					this.$TOOL.data.set('SNOWY_LAYOUT_TAGS_OPEN', false)
				}
			},
			breadcrumbOpen() {
				this.$store.commit('TOGGLE_breadcrumbOpen')
				if (this.$store.state.global.breadcrumbOpen) {
					this.$TOOL.data.set('SNOWY_BREADCRUMD_OPEN', true)
				} else {
					this.$TOOL.data.set('SNOWY_BREADCRUMD_OPEN', false)
				}
			},
			topHanderThemeColorOpen() {
				this.$store.commit('TOGGLE_topHanderThemeColorOpen')
				if (this.$store.state.global.topHanderThemeColorOpen) {
					this.$TOOL.data.set('SNOWY_TOP_HANDER_THEME_COLOR_OPEN', true)
				} else {
					// 关闭顶栏主题色
					this.$TOOL.data.set('SNOWY_TOP_HANDER_THEME_COLOR_OPEN', false)
					// 这个时候我们吧通栏的设置也给搞为false
					this.topHanderThemeColorSpread = false
				}
			},
			topHanderThemeColorSpread() {
				this.$store.commit('TOGGLE_topHanderThemeColorSpread')
				if (this.$store.state.global.topHanderThemeColorSpread) {
					this.$TOOL.data.set('SNOWY_TOP_HANDER_THEME_COLOR_SPREAD', true)
				} else {
					this.$TOOL.data.set('SNOWY_TOP_HANDER_THEME_COLOR_SPREAD', false)
				}
			},
			moduleUnfoldOpen() {
				this.$store.commit('TOGGLE_moduleUnfoldOpen')
				if (this.$store.state.global.moduleUnfoldOpen) {
					this.$TOOL.data.set('SNOWY_MODULE_UNFOLD_OPEN', true)
				} else {
					this.$TOOL.data.set('SNOWY_MODULE_UNFOLD_OPEN', false)
				}
			},
		},
		methods: {
			// 设置整体风格主题
			setSideStyle(value) {
				this.$store.commit('SET_theme', value)
				this.sideStyle = value
				this.$TOOL.data.set('SNOWY_THEME', value)
			},
			// 设置整体界面布局
			layoutStyle(value) {
				this.$store.commit('SET_layout', value)
				this.$TOOL.data.set('SNOWY_LAYOUT', value)
				this.layout = value
			},
			// 切换颜色
			tagColor(value) {
				this.themeColor = value
				this.$TOOL.data.set('SNOWY_THEME_COLOR', value)
				this.$store.commit('SET_themeColor', value)
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
