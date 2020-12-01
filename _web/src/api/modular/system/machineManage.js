import { axios } from '@/utils/request'

/**
 * 系统属性监控
 *
 * @author yubaoshan
 * @date 2020/6/8 19:47
 */
export function sysMachineQuery (parameter) {
  return axios({
    url: '/sysMachine/query',
    method: 'get',
    params: parameter
  })
}
