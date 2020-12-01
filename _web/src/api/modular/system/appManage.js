/**
 * 系统应用
 *
 * @author yubaoshan
 * @date 2020年4月23日12:10:57
 */
import { axios } from '@/utils/request'

/**
 * 系统应用列表
 *
 * @author yubaoshan
 * @date 2020年7月9日15:05:01
 */
export function getAppPage (parameter) {
  return axios({
    url: '/sysApp/page',
    method: 'get',
    params: parameter
  })
}

/**
 * 系统应用列表
 *
 * @author yubaoshan
 * @date 2020年7月9日15:05:01
 */
export function getAppList (parameter) {
  return axios({
    url: '/sysApp/list',
    method: 'get',
    params: parameter
  })
}

/**
 * 新增系统应用
 *
 * @author yubaoshan
 * @date 2020年7月9日15:05:01
 */
export function sysAppAdd (parameter) {
  return axios({
    url: '/sysApp/add',
    method: 'post',
    data: parameter
  })
}

/**
 * 编辑系统应用
 *
 * @author yubaoshan
 * @param parameter
 * @returns {*}
 */
export function sysAppEdit (parameter) {
  return axios({
    url: '/sysApp/edit',
    method: 'post',
    data: parameter
  })
}

/**
 * 删除系统应用
 *
 * @author yubaoshan
 * @date 2020年7月9日15:05:01
 */
export function sysAppDelete (parameter) {
  return axios({
    url: '/sysApp/delete',
    method: 'post',
    data: parameter
  })
}

/**
 * 设为默认应用
 *
 * @author yubaoshan
 * @date 2020年7月9日15:05:01
 */
export function sysAppSetAsDefault (parameter) {
  return axios({
    url: '/sysApp/setAsDefault',
    method: 'post',
    data: parameter
  })
}
