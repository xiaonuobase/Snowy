import { axios } from '@/utils/request'

/**
 * 查询系统字典值
 *
 * @author yubaoshan
 * @date 2020/5/17 02:24
 */
export function sysDictDataPage (parameter) {
  return axios({
    url: '/sysDictData/page',
    method: 'get',
    params: parameter
  })
}

/**
 * 添加系统字典值
 *
 * @author yubaoshan
 * @date 2020/5/17 02:24
 */
export function sysDictDataAdd (parameter) {
  return axios({
    url: '/sysDictData/add',
    method: 'post',
    data: parameter
  })
}

/**
 * 编辑系统字典值
 *
 * @author yubaoshan
 * @date 2020/5/17 02:25
 */
export function sysDictDataEdit (parameter) {
  return axios({
    url: '/sysDictData/edit',
    method: 'post',
    data: parameter
  })
}

/**
 * 删除系统字典值
 *
 * @author yubaoshan
 * @date 2020/5/17 02:25
 */
export function sysDictDataDelete (parameter) {
  return axios({
    url: '/sysDictData/delete',
    method: 'post',
    data: parameter
  })
}
