import { axios } from '@/utils/request'

/**
 * 获取菜单列表
 *
 * @author yubaoshan
 * @param parameter
 * @returns {*}
 */
export function getMenuList (parameter) {
  return axios({
    url: '/sysMenu/list',
    method: 'get',
    params: parameter
  })
}

/**
 * 获取系统菜单树，用于新增，编辑时选择上级节点
 *
 * @author yubaoshan
 * @date 2020/4/23 12:22
 */
export function getMenuTree (parameter) {
  return axios({
    url: '/sysMenu/tree',
    method: 'get',
    params: parameter
  })
}

/**
 * 增加菜单
 *
 * @author yubaoshan
 * @date 2020/4/24 23:23
 */
export function sysMenuAdd (parameter) {
  return axios({
    url: '/sysMenu/add',
    method: 'post',
    data: parameter
  })
}

/**
 * 增加菜单
 *
 * @author yubaoshan
 * @date 2020/4/24 23:23
 */
export function sysMenuDelete (parameter) {
  return axios({
    url: '/sysMenu/delete',
    method: 'post',
    data: parameter
  })
}

/**
 * 查看菜单详情
 *
 * @author yubaoshan
 * @date 2020/4/25 01:11
 */
export function sysMenuDetail (parameter) {
  return axios({
    url: '/sysMenu/detail',
    method: 'post',
    data: parameter
  })
}

/**
 * 编辑系统菜单
 *
 * @author yubaoshan
 * @date 2020/4/25 01:11
 */
export function sysMenuEdit (parameter) {
  return axios({
    url: '/sysMenu/edit',
    method: 'post',
    data: parameter
  })
}

/**
 * 获取系统菜单树，用于给角色授权时选择
 *
 * @author yubaoshan
 * @date 2020/6/2 17:30
 */
export function SysMenuTreeForGrant (parameter) {
  return axios({
    url: '/sysMenu/treeForGrant',
    method: 'get',
    params: parameter
  })
}

/**
 * 根据系统切换菜单
 *
 * @author yubaoshan
 * @date 2020/6/28 15:25
 */
export function sysMenuChange (parameter) {
  return axios({
    url: '/sysMenu/change',
    method: 'post',
    data: parameter
  })
}
