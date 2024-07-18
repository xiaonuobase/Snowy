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
import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/dev/file/` + url, ...arg)
/**
 * 文件
 *
 * @author yubaoshan
 * @date 2022-09-22 22:33:20
 */
export default {
	// 动态上传文件返回id
	fileUploadDynamicReturnId(data) {
		return request('uploadDynamicReturnId', data)
	},
	// 动态上传文件返回url
	fileUploadDynamicReturnUrl(data) {
		return request('uploadDynamicReturnUrl', data)
	},
	// 本地文件上传，返回文件id
	fileUploadReturnId(data) {
		return request('uploadLocalReturnId', data)
	},
	// 阿里云文件上传，返回文件id
	fileUploadAliyunReturnId(data) {
		return request('uploadAliyunReturnId', data)
	},
	// 腾讯云文件上传，返回文件id
	fileUploadTencentReturnId(data) {
		return request('uploadTencentReturnId', data)
	},
	// MINIO文件上传，返回文件id
	fileUploadMinioReturnId(data) {
		return request('uploadMinioReturnId', data)
	},
	// 本地文件上传，返回文件Url
	fileUploadLocalReturnUrl(data) {
		return request('uploadLocalReturnUrl', data)
	},
	// 阿里云文件上传，返回文件Url
	fileUploadAliyunReturnUrl(data) {
		return request('uploadAliyunReturnUrl', data)
	},
	// 腾讯云文件上传，返回文件Url
	fileUploadTencentReturnUrl(data) {
		return request('uploadTencentReturnUrl', data)
	},
	// MINIO文件上传，返回文件Url
	fileUploadMinioReturnUrl(data) {
		return request('uploadMinioReturnUrl', data)
	},
	// 获取文件分页列表
	filePage(data) {
		return request('page', data, 'get')
	},
	// 获取文件列表
	fileList(data) {
		return request('list', data, 'get')
	},
	// 下载文件，这里要带上blob类型
	fileDownload(data) {
		return request('download', data, 'get', {
			responseType: 'blob'
		})
	},
	// 获取文件详情
	fileDetail(data) {
		return request('detail', data, 'get')
	},
	// 根据文件url集合获取文件集合
	fileGetFileListByUrlList(data) {
		return request('getFileListByUrlList', data)
	},
	// 删除文件
	fileDelete(data) {
		return request('delete', data)
	}
}
