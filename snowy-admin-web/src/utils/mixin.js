/**
 *  Copyright [2022] [https://www.xiaonuo.vip]
 *	Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *	1.请不要删除和修改根目录下的LICENSE文件。
 *	2.请不要删除和修改Snowy源码头部的版权声明。
 *	3.本项目代码可免费商业使用，商业使用请保留源码和相关描述文件的项目出处，作者声明等。
 *	4.分发源码时候，请注明软件出处 https://www.xiaonuo.vip
 *	5.不可二次分发开源参与同类竞品，如有想法可联系团队xiaonuobase@qq.com商议合作。
 *	6.若您的项目无法满足以上几点，需要更多功能代码，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
import { mapState } from 'vuex'
import { DEVICE_TYPE, deviceEnquire } from '@/utils/device'

// const mixinsComputed = Vue.config.optionMergeStrategies.computed
// const mixinsMethods = Vue.config.optionMergeStrategies.methods

const mixin = {
	computed: {
		...mapState({
			layoutMode: (state) => state.app.layout,
			navTheme: (state) => state.app.theme,
			primaryColor: (state) => state.app.color,
			colorWeak: (state) => state.app.weak,
			fixedHeader: (state) => state.app.fixedHeader,
			fixSiderbar: (state) => state.app.fixSiderbar,
			fixSidebar: (state) => state.app.fixSiderbar,
			contentWidth: (state) => state.app.contentWidth,
			autoHideHeader: (state) => state.app.autoHideHeader,
			sidebarOpened: (state) => state.app.sidebar,
			multiTab: (state) => state.app.multiTab
		})
	},
	methods: {
		isTopMenu() {
			return this.layoutMode === 'topmenu'
		},
		isSideMenu() {
			return !this.isTopMenu()
		}
	}
}

const mixinDevice = {
	computed: {
		...mapState({
			device: (state) => state.app.device
		})
	},
	methods: {
		isMobile() {
			return this.device === DEVICE_TYPE.MOBILE
		},
		isDesktop() {
			return this.device === DEVICE_TYPE.DESKTOP
		},
		isTablet() {
			return this.device === DEVICE_TYPE.TABLET
		}
	}
}

const AppDeviceEnquire = {
	mounted() {
		const { $store } = this
		deviceEnquire((deviceType) => {
			switch (deviceType) {
				case DEVICE_TYPE.DESKTOP:
					$store.commit('TOGGLE_DEVICE', 'desktop')
					$store.dispatch('setSidebar', true)
					break
				case DEVICE_TYPE.TABLET:
					$store.commit('TOGGLE_DEVICE', 'tablet')
					$store.dispatch('setSidebar', false)
					break
				case DEVICE_TYPE.MOBILE:
				default:
					$store.commit('TOGGLE_DEVICE', 'mobile')
					$store.dispatch('setSidebar', true)
					break
			}
		})
	}
}

export { mixin, AppDeviceEnquire, mixinDevice }
