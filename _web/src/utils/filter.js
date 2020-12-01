import Vue from 'vue'
import { DICT_TYPE_TREE_DATA } from '@/store/mutation-types'
import moment from 'moment'
import 'moment/locale/zh-cn'
moment.locale('zh-cn')

Vue.filter('NumberFormat', function (value) {
  if (!value) {
    return '0'
  }
  const intPartFormat = value.toString().replace(/(\d)(?=(?:\d{3})+$)/g, '$1,') // 将整数部分逢三一断
  return intPartFormat
})

Vue.filter('dayjs', function (dataStr, pattern = 'YYYY-MM-DD HH:mm:ss') {
  return moment(dataStr).format(pattern)
})

Vue.filter('moment', function (dataStr, pattern = 'YYYY-MM-DD HH:mm:ss') {
  return moment(dataStr).format(pattern)
})

/**
 * 金额格式化 ，使用方法：{{ val | Fmoney }}
 *
 * @author yubaoshan
 * @date 2020-9-15 15:02:20
 */
Vue.filter('Fmoney', function (val) {
  // eslint-disable-next-line no-useless-escape
  val = val.toString().replace(/\$|\,/g, '')
  if (isNaN(val)) {
    val = '0'
  }
  // eslint-disable-next-line eqeqeq
  const sign = (val == (val = Math.abs(val)))
  val = Math.floor(val * 100 + 0.50000000001)
  let cents = val % 100
  val = Math.floor(val / 100).toString()
  if (cents < 10) {
    cents = '0' + cents
  }
  // eslint-disable-next-line no-undef
  for (let i = 0; i < Math.floor((val.length - (1 + i)) / 3); I++) {
    val = val.substring(0, val.length - (4 * i + 3)) + ',' + val.substring(val.length - (4 * i + 3))
  }
  return (((sign) ? '' : '-') + val + '.' + cents)
})

/**
 * 翻译使用方法，直接返回翻译后的name {{ code | dictType(value) }}
 *
 * @author yubaoshan
 * @date 2020-9-15 15:02:20
 */
Vue.filter('dictType', function (code, value) {
  const dictTypeTree = Vue.ls.get(DICT_TYPE_TREE_DATA)
  if (dictTypeTree === undefined) {
    return '需重新登录'
  }
  // eslint-disable-next-line eqeqeq
  const tree = dictTypeTree.filter(item => item.code == code)[0].children
  if (tree === undefined || tree.length === 0) {
    return '无此字典'
  }
  // eslint-disable-next-line eqeqeq
  const values = tree.filter(item => item.code == value)
  if (values.length === undefined || values.length === 0) {
    return '无此字典'
  }
  return values[0].name
})

/**
 * 获取某个code下字典的列表，多用于字典下拉框，使用方法：{{ code | dictData }}
 *
 * @author yubaoshan
 * @date 2020-9-19 22:40:22
 */
Vue.filter('dictData', function (code) {
  const dictTypeTree = Vue.ls.get(DICT_TYPE_TREE_DATA)
  if (dictTypeTree === undefined) {
    return []
  }
  // eslint-disable-next-line eqeqeq
  const tree = dictTypeTree.filter(item => item.code == code)[0].children
  if (tree === undefined) {
    return []
  }
  return tree
})
