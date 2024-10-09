import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/gen/basic/` + url, ...arg)

export default {
	// 获取代码生成基础分页
	basicPage(data) {
		return request('page', data, 'get')
	},
	// 提交表单 edit为true时为编辑，默认为新增
	submitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除代码生成基础
	basicDelete(data) {
		return request('delete', data)
	},
	// 获取代码生成基础详情
	basicDetail(data) {
		return request('detail', data, 'get')
	},
	// 获取所有表信息
	basicTables(data) {
		return request('tables', data, 'get')
	},
	// 获取表内所有字段信息
	basicTableColumns(data) {
		return request('tableColumns', data, 'get')
	},
	// 执行代码生成 压缩包
	basicExecGenBiz(data) {
		const options = {
			responseType: 'blob'
		}
		return request('execGenZip', data, 'get', options)
	},
	// 执行代码生成 项目内
	basicExecGenPro(data) {
		return request('execGenPro', data)
	},
	// 预览代码生成
	basicPreviewGen(data) {
		return request('previewGen', data, 'get')
	},
	// 获取所有移动端模块
	basicMobileModuleSelector(data) {
		return request('mobileModuleSelector', data, 'get')
	},
	// 获取所有模块
	basicModuleSelector(data) {
		return request('moduleSelector', data, 'get')
	},
	// 获取所有菜单树包括未授权的
	basicMenuTreeSelector(data) {
		return request('menuTreeSelector', data, 'get')
	}
}
