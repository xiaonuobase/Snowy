import store from '@/store'

export function actionToObject (json) {
  try {
    return JSON.parse(json)
  } catch (e) {
    console.log('err', e.message)
  }
  return []
}

/**
 * 控制按钮是否显示
 *
 * @author yubaoshan
 * @date 2020/06/27 02:34
 */
export function hasBtnPermission (permission) {
  const myBtns = store.getters.buttons
  const admintype = store.getters.admintype
  // eslint-disable-next-line eqeqeq
  if (admintype == '1') {
     return true
  }
  return myBtns.indexOf(permission) > -1
}
