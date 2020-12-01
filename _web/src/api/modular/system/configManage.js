import { axios } from '@/utils/request'

/**
 * 分页查询配置列表
 *
 * @author yubaoshan
 * @date 2020/5/25 01:57
 */
export function sysConfigPage (parameter) {
  return axios({
    url: '/sysConfig/page',
    method: 'get',
    params: parameter
  })
}

/**
 * 添加系统参数配置
 *
 * @author yubaoshan
 * @date 2020/5/25 01:57
 */
export function sysConfigAdd (parameter) {
  return axios({
    url: '/sysConfig/add',
    method: 'post',
    data: parameter
  })
}

/**
 * 编辑系统参数配置
 *
 * @author yubaoshan
 * @date 2020/5/25 01:57
 */
export function sysConfigEdit (parameter) {
  return axios({
    url: '/sysConfig/edit',
    method: 'post',
    data: parameter
  })
}

/**
 * 删除系统参数配置
 *
 * @author yubaoshan
 * @date 2020/5/25 01:57
 */
export function sysConfigDelete (parameter) {
  return axios({
    url: '/sysConfig/delete',
    method: 'post',
    data: parameter
  })
}

/**
 * 获取字典类型下所有字典，举例，返回格式为：[{code:"M",value:"男"},{code:"F",value:"女"}]
 *
 * @author yubaoshan
 * @date 2020/5/25 02:06
 */
export function sysDictTypeDropDown (parameter) {
  return axios({
    url: '/sysDictType/dropDown',
    method: 'get',
    params: parameter
  })
}

/**
 * 获取系统的所有任务列表
 *
 * @author yubaoshan
 * @date 2020/7/8 20:46
 */
export function sysTimersGetActionClasses (parameter) {
  return axios({
    url: '/sysTimers/getActionClasses',
    method: 'get',
    params: parameter
  })
}
