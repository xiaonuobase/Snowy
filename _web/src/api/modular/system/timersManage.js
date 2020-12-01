import { axios } from '@/utils/request'

/**
 * 分页查询定时任务
 *
 * @author yubaoshan
 * @date 2020/7/3 03:13
 */
export function sysTimersPage (parameter) {
  return axios({
    url: '/sysTimers/page',
    method: 'get',
    params: parameter
  })
}

/**
 * 获取全部定时任务
 *
 * @author yubaoshan
 * @date 2020/7/3 03:23
 */
export function sysTimersList (parameter) {
  return axios({
    url: '/sysTimers/list',
    method: 'get',
    params: parameter
  })
}

/**
 * 查看详情定时任务
 *
 * @author yubaoshan
 * @date 2020/7/3 03:23
 */
export function sysTimersDetail (parameter) {
  return axios({
    url: '/sysTimers/detail',
    method: 'get',
    params: parameter
  })
}

/**
 * 添加定时任务
 *
 * @author yubaoshan
 * @date 2020/7/3 03:23
 */
export function sysTimersAdd (parameter) {
  return axios({
    url: '/sysTimers/add',
    method: 'post',
    data: parameter
  })
}

/**
 * 删除定时任务
 *
 * @author yubaoshan
 * @date 2020/7/3 03:23
 */
export function sysTimersDelete (parameter) {
  return axios({
    url: '/sysTimers/delete',
    method: 'post',
    data: parameter
  })
}

/**
 * 编辑定时任务
 *
 * @author yubaoshan
 * @date 2020/7/3 03:23
 */
export function sysTimersEdit (parameter) {
  return axios({
    url: '/sysTimers/edit',
    method: 'post',
    data: parameter
  })
}

/**
 * 获取系统的所有任务列表
 *
 * @author yubaoshan
 * @date 2020/7/3 03:23
 */
export function sysTimersGetActionClasses (parameter) {
  return axios({
    url: '/sysTimers/getActionClasses',
    method: 'post',
    data: parameter
  })
}

/**
 * 启动定时任务
 *
 * @author yubaoshan
 * @date 2020/7/3 03:23
 */
export function sysTimersStart (parameter) {
  return axios({
    url: '/sysTimers/start',
    method: 'post',
    data: parameter
  })
}

/**
 * 停止定时任务
 *
 * @author yubaoshan
 * @date 2020/7/3 03:23
 */
export function sysTimersStop (parameter) {
  return axios({
    url: '/sysTimers/stop',
    method: 'post',
    data: parameter
  })
}
