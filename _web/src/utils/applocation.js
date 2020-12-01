import store from '@/store'

/**
 * 缓存中的已选中应用
 *
 * @author yubaoshan
 * @date 2020/06/27 02:34
 */
export function sysApplication () {
  return store.getters.applocation
}
