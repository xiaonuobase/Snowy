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
import configApi from '@/api/dev/configApi'
import { message } from 'ant-design-vue'

const formData = ref({
	SNOWY_SYS_LOGO: '',
	SNOWY_SYS_BACK_IMAGE: '',
	SNOWY_SYS_NAME: '',
	SNOWY_SYS_VERSION: '',
	SNOWY_SYS_COPYRIGHT: '',
	SNOWY_SYS_COPYRIGHT_URL: '',
	SNOWY_SYS_DEFAULT_FILE_ENGINE: 'LOCAL',
	SNOWY_SYS_DEFAULT_CAPTCHA_OPEN: false,
	SNOWY_SYS_DEFAULT_PASSWORD: ''
})

const param = {
	category: 'SYS_BASE'
}

const getSysBaseConfig = () => {
	configApi.configList(param).then((data) => {
		if (data) {
			data.forEach((item) => {
				formData.value[item.configKey] = item.configValue ? '' : item.configValue
			})
		} else {
			message.warning('表单项不存在，请初始化数据库')
		}
	})
}
