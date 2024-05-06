<template>
	<div class="setting-drawer-index-content">
		<div class="scrollbar">
			<h3 class="setting-item-title">整体风格设置</h3>
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
			<h3 class="setting-item-title">整体界面布局</h3>
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
				<h3 class="setting-item-title">主题色</h3>
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
			<div class="mb-4 layout-slide" v-if="!topHeaderThemeColorOpenDisabled" style="padding-top: 10px">
				<h4 class="setting-item-title">顶栏应用主题色：</h4>
				<a-switch
					:checked="topHeaderThemeColorOpen"
					@change="changeTopHeaderThemeColorOpen"
					:disabled="topHeaderThemeColorOpenDisabled"
				/>
			</div>
			<div class="mb-4 layout-slide" v-if="!topHeaderThemeColorSpreadDisabled">
				<h4 class="setting-item-title">顶栏主题色通栏：</h4>
				<a-switch
					class="xn-fdr"
					:checked="topHeaderThemeColorSpread"
					:disabled="!topHeaderThemeColorOpen || topHeaderThemeColorSpreadDisabled"
					@change="changeTopHeaderThemeColorSpread"
				/>
			</div>

			<a-divider />
			<a-form ref="formRef" class="text-right">
				<a-form-item label="模块坞" v-if="!moduleUnfoldDisabled">
					<a-switch
						:checked="moduleUnfoldOpen"
						@change="toggleState('moduleUnfoldOpen')"
						:disabled="moduleUnfoldDisabled"
					/>
				</a-form-item>
				<a-form-item label="固定宽度" v-if="layout == layoutEnum.TOP">
					<a-switch :checked="fixedWidth" @change="toggleState('fixedWidth')" />
				</a-form-item>
				<a-form-item label="面包屑">
					<a-switch :checked="breadcrumbOpen" @change="toggleState('breadcrumbOpen')" />
				</a-form-item>
				<a-form-item label="多标签">
					<a-switch :checked="layoutTagsOpen" @change="toggleState('layoutTagsOpen')" />
				</a-form-item>
				<a-form-item label="折叠菜单" v-if="!menuIsCollapseDisabled">
					<a-switch
						:checked="menuIsCollapse"
						@change="toggleState('menuIsCollapse')"
						:disabled="menuIsCollapseDisabled"
					/>
				</a-form-item>
				<a-form-item label="菜单排他展开" v-if="!sideUniqueOpenDisabled">
					<a-switch
						:checked="sideUniqueOpen"
						@change="toggleState('sideUniqueOpen')"
						:disabled="sideUniqueOpenDisabled"
					/>
				</a-form-item>
				<a-form-item label="登录用户水印">
					<a-switch :checked="loginUserWatermarkOpen" @change="toggleState('loginUserWatermarkOpen')" />
				</a-form-item>
				<a-form-item label="页脚版权信息">
					<a-switch :checked="footerCopyrightOpen" @change="toggleState('footerCopyrightOpen')" />
				</a-form-item>
				<a-form-item label="圆角风格">
					<a-switch :checked="roundedCornerStyleOpen" @change="toggleState('roundedCornerStyleOpen')" />
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
<script setup>
	import { colorList } from '@/config/settingConfig'
	import { themeEnum } from '@/layout/enum/themeEnum'
	import { layoutEnum } from '@/layout/enum/layoutEnum'
	import { globalStore } from '@/store'
	import tool from '@/utils/tool'
	const store = globalStore()
	const topHeaderThemeColorOpenDisabled = ref(false)
	const topHeaderThemeColorSpreadDisabled = ref(false)
	const moduleUnfoldDisabled = ref(false)
	const menuIsCollapseDisabled = ref(false)
	const sideUniqueOpenDisabled = ref(false)
	const toolDataNameMap = {
		menuIsCollapse: 'MENU_COLLAPSE',
		sideUniqueOpen: 'SIDE_UNIQUE_OPEN',
		layoutTagsOpen: 'LAYOUT_TAGS_OPEN',
		breadcrumbOpen: 'BREADCRUMD_OPEN',
		fixedWidth: 'FIXEDWIDTH_OPEN',
		topHeaderThemeColorOpen: 'TOP_HEADER_THEME_COLOR_OPEN',
		topHeaderThemeColorSpread: 'TOP_HEADER_THEME_COLOR_SPREAD',
		loginUserWatermarkOpen: 'LOGIN_USER_WATERMARK_OPEN',
		footerCopyrightOpen: 'FOOTER_COPYRIGHT_OPEN',
		roundedCornerStyleOpen: 'ROUNDED_CORNER_STYLE_OPEN',
		moduleUnfoldOpen: 'MODULE_UNFOLD_OPEN'
	}
	const sideStyleList = ref([
		{
			tips: '暗色主题风格',
			value: themeEnum.DARK,
			style: 'snowy-setting-checkbox-item-dark'
		},
		{
			tips: '亮色主题风格',
			value: themeEnum.LIGHT,
			style: 'snowy-setting-checkbox-item-light'
		},
		{
			tips: '暗黑模式',
			value: themeEnum.REAL_DARK,
			style: 'snowy-setting-checkbox-item-realdark'
		}
	])
	const layoutList = ref([
		{
			tips: '经典',
			value: layoutEnum.CLASSICAL,
			style: 'snowy-setting-layout-menu-classical'
		},
		{
			tips: '双排菜单',
			value: layoutEnum.DOUBLEROW,
			style: 'snowy-setting-layout-menu-doublerow'
		},
		{
			tips: '顶部菜单',
			value: layoutEnum.TOP,
			style: 'snowy-setting-layout-menu-top'
		}
	])
	const xnFormStyleOptions = ref([
		{
			label: '抽屉',
			value: 'drawer'
		},
		{
			label: '对话框',
			value: 'modal'
		}
	])
	const theme = computed(() => {
		return store.theme
	})
	const themeColor = computed(() => {
		return store.themeColor
	})
	const layout = computed(() => {
		return store.layout
	})
	const menuIsCollapse = computed(() => {
		return store.menuIsCollapse
	})
	const sideUniqueOpen = computed(() => {
		return store.sideUniqueOpen
	})
	const loginUserWatermarkOpen = computed(() => {
		return store.loginUserWatermarkOpen
	})
	const footerCopyrightOpen = computed(() => {
		return store.footerCopyrightOpen
	})
	const roundedCornerStyleOpen = computed(() => {
		return store.roundedCornerStyleOpen
	})
	const layoutTagsOpen = computed(() => {
		return store.layoutTagsOpen
	})
	const breadcrumbOpen = computed(() => {
		return store.breadcrumbOpen
	})
	const fixedWidth = computed(() => {
		return store.fixedWidth
	})
	const moduleUnfoldOpen = computed(() => {
		return store.moduleUnfoldOpen
	})
	const topHeaderThemeColorOpen = computed(() => {
		return store.topHeaderThemeColorOpen
	})
	const topHeaderThemeColorSpread = computed(() => {
		return store.topHeaderThemeColorSpread
	})
	const formStyle = computed(() => {
		return store.formStyle
	})
	const changeTopHeaderThemeColorOpen = () => {
		toggleState('topHeaderThemeColorOpen')
		if (!topHeaderThemeColorOpen.value) {
			store.topHeaderThemeColorSpread = false
			tool.data.set('SNOWY_TOP_HEADER_THEME_COLOR_SPREAD', false)
		}
	}
	const changeTopHeaderThemeColorSpread = () => {
		toggleState('topHeaderThemeColorSpread')
	}
	const toggleState = (stateName) => {
		store.toggleConfig(stateName)
		const toolDataName = toolDataNameMap[stateName]
		tool.data.set(`SNOWY_${toolDataName}`, store[stateName])
	}
	// 设置整体风格主题
	const setSideStyle = (value) => {
		store.setTheme(value)
		tool.data.set('SNOWY_THEME', value)
		layoutChange(layout.value)
	}
	// 设置整体界面布局
	const layoutStyle = (value) => {
		store.setLayout(value)
		tool.data.set('SNOWY_LAYOUT', value)
		layoutChange(value)
	}
	// 切换颜色
	const tagColor = (value) => {
		tool.data.set('SNOWY_THEME_COLOR', value)
		store.setThemeColor(value)
	}
	// 切换表单风格
	const formStyleChange = (value) => {
		tool.data.set('SNOWY_FORM_STYLE', value)
		store.setFormStyle(value)
	}
	// 更改布局后设置
	const layoutChange = (layout) => {
		// 暗黑色情况下，将其禁用顶栏颜色跟通栏功能
		if (theme.value === themeEnum.REAL_DARK) {
			topHeaderThemeColorOpenDisabled.value = true
			topHeaderThemeColorSpreadDisabled.value = true
			// 在切换主题风格的时候判断顶栏相关的，暗黑模式下是不允许开启顶栏变色的
			if (topHeaderThemeColorOpen.value) {
				toggleState('topHeaderThemeColorOpen')
				tool.data.set('SNOWY_TOP_HEADER_THEME_COLOR_SPREAD', false)
			}
		} else {
			if (layout !== layoutEnum.TOP) {
				topHeaderThemeColorSpreadDisabled.value = false
				topHeaderThemeColorOpenDisabled.value = false
			} else {
				topHeaderThemeColorOpenDisabled.value = false
			}
		}
		// 如果是顶栏布局，限制一些配置
		if (layout === layoutEnum.TOP) {
			// 将其模块坞功能禁用
			moduleUnfoldDisabled.value = true
			// 禁用折叠菜单
			menuIsCollapseDisabled.value = true
			// 菜单排他展开
			if (sideUniqueOpen.value) {
				toggleState('sideUniqueOpen')
			}
			sideUniqueOpenDisabled.value = true
			topHeaderThemeColorSpreadDisabled.value = true
		} else {
			moduleUnfoldDisabled.value = false
			menuIsCollapseDisabled.value = false
			sideUniqueOpenDisabled.value = false
		}
	}
	onMounted(() => {
		const layout = tool.data.get('SNOWY_LAYOUT')
		// 这里主要为了dom销毁后重新打开设置界面，界面上禁用相关
		layoutChange(layout)
	})
</script>

<style lang="less" scoped>
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
		color: #1677FF;
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
	.snowy-setting-layout-menu-top {
		z-index: 1;
		background-color: #ebeef1;
		content: '';
	}
	.snowy-setting-layout-menu-top::before {
		z-index: 1;
		background-color: #ebeef1;
		content: '';
	}
	.snowy-setting-layout-menu-top::after {
		z-index: 2;
		position: absolute;
		top: 0;
		left: 0;
		width: 100%;
		height: 25%;
		background-color: #001529;
		content: '';
	}
	.scrollbar {
		margin: 0 auto;
	}
	.setting-item-title {
		color: var(--font-color);
	}
	:deep(.ant-form-item) {
		margin-bottom: 12px !important;
	}
</style>
