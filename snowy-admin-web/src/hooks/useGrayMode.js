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
import { globalStore } from '@/store'

// 灰色模式类名，对应样式见 src/style/index.less 的 html.gray-mode
const GRAY_MODE_CLASS = 'gray-mode'

/**
 * 灰色模式，跟随 store.grayModeOpen 在 html 元素上增删类名
 *
 * 滤镜必须加在 html 上：一是 Modal、Drawer 等浮层挂在 body 下，加在业务容器上盖不住；
 * 二是 filter 会创建新的包含块，加在中间层会让内部的 position: fixed 失效
 */
export const useGrayMode = () => {
	const store = globalStore()

	watch(
		() => store.grayModeOpen,
		(isEnabled) => {
			document.documentElement.classList.toggle(GRAY_MODE_CLASS, isEnabled)
		},
		{ immediate: true }
	)

	onUnmounted(() => {
		document.documentElement.classList.remove(GRAY_MODE_CLASS)
	})
}
