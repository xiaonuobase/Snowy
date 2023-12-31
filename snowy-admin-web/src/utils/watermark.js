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
import tool from '@/utils/tool'
export const watermark = {
	set: function (text1, text2) {
		const canvas = document.createElement('canvas')
		canvas.width = 150
		canvas.height = 120
		canvas.style.display = 'none'
		const shuiyin = canvas.getContext('2d')
		// 控制文字的旋转角度和上下位置
		shuiyin.rotate((-20 * Math.PI) / 180)
		shuiyin.translate(-50, 20)
		//文字颜色
		shuiyin.fillStyle = '#f5f5f5'
		//文字样式
		shuiyin.font = '100 16px Microsoft JhengHei '
		shuiyin.fillText(text1, canvas.width / 3, canvas.height / 2)
		shuiyin.fillText(text2, canvas.width / 3, canvas.height / 2 + 20)
		/* 新建一个用于填充canvas水印的标签，之所以没有直接在body上添加，
           是因为z-index对个别内容影响，才考虑的不用body */
		const watermark = document.createElement('div')
		const styleStr = `
            position:fixed;
            top:0;
            left:0;
            width:100vw;
            height:100vh;
            z-index:99999;
            pointer-events:none;
            background-repeat:repeat;
            mix-blend-mode: multiply;
            background-image:url('${canvas.toDataURL('image/png')}')`
		watermark.setAttribute('style', styleStr)
		watermark.classList.add('watermark')
		document.body.appendChild(watermark)

		//此方法是防止用户通过控制台修改样式去除水印效果
		/* MutationObserver 是一个可以监听DOM结构变化的接口。 */
		const observer = new MutationObserver(() => {
			// 此处根据用户登录状态，判断是否终止监听，避免用户退出后登录页面仍然有水印
			if (!tool.data.get('TOKEN')) {
				this.close()
				observer.disconnect()
			}
			const wmInstance = document.body.querySelector('.watermark')
			if (!wmInstance || wmInstance.getAttribute('style') !== styleStr) {
				//如果标签在，只修改了属性，重新赋值属性
				if (wmInstance) {
					// 避免一直触发
					// observer.disconnect();
					wmInstance.setAttribute('style', styleStr)
				} else {
					/* 此处根据用户登录状态，判断是否终止监听，避免用户退出后登录页面仍然有水印 */
					if (tool.data.get('TOKEN')) {
						//标签被移除，重新添加标签
						document.body.appendChild(watermark)
					} else {
						observer.disconnect()
					}
				}
			}
		})
		observer.observe(document.body, {
			attributes: true,
			subtree: true,
			childList: true
		})
	},
	close: function () {
		/* 关闭页面的水印，即要移除水印标签 */
		let watermark = document.body.querySelector('.watermark')
		if (watermark) {
			document.body.removeChild(watermark)
		}
	}
}
// 使用方法
// import { watermark } from '@/utils/watermark'
// 添加水印
// watermark.set('Snowy','xiaonuo.vip')
// 移除水印,传 null 移除水印
// watermark.close()
