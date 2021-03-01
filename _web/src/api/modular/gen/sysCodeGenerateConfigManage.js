import { axios } from '@/utils/request'

/**
 * 代码生成详细配置列表
 *
 * @author yubaoshan
 * @date 2021-02-06 20:19:49
 */
export function sysCodeGenerateConfigList (parameter) {
  return axios({
    url: '/sysCodeGenerateConfig/list',
    method: 'get',
    params: parameter
  })
}

/**
 * 编辑代码生成详细配置
 *
 * @author yubaoshan
 * @date 2021-02-06 20:19:49
 */
export function sysCodeGenerateConfigEdit (parameter) {
  return axios({
    url: '/sysCodeGenerateConfig/edit',
    method: 'post',
    data: parameter
  })
}
