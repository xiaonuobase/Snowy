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
/**
 * 加解密的工具类
 * 使用：https://github.com/JuneAndGreen/sm-crypto
 *
 * @author yubaoshan
 */
import smCrypto from 'sm-crypto'

const sm2 = smCrypto.sm2
const cipherMode = 1 // 1 - C1C3C2，0 - C1C2C3，默认为1
const publicKey =
	'04298364ec840088475eae92a591e01284d1abefcda348b47eb324bb521bb03b0b2a5bc393f6b71dabb8f15c99a0050818b56b23f31743b93df9cf8948f15ddb54'

/**
 * 国密加解密工具类
 */
export default {
	// SM2加密
	doSm2Encrypt(msgString) {
		return sm2.doEncrypt(msgString, publicKey, cipherMode)
	},
	// SM2数组加密
	doSm2ArrayEncrypt(msgString) {
		return sm2.doEncrypt(msgString, publicKey, cipherMode)
	}
}
