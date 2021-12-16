/**
 * 系统应用
 *
 * @author yubaoshan
 * @date 2020/5/26 19:06
 */
import { axios } from '@/utils/request'

/**
 * 登录
 *
 * @author yubaoshan
 * @date 2020/5/26 19:06
 */
export function login (parameter) {
  // 密码采用sm2加密传输密码
  const sm2 = require('sm-crypto').sm2
  const publicKey = '04298364ec840088475eae92a591e01284d1abefcda348b47eb324bb521bb03b0b2a5bc393f6b71dabb8f15c99a0050818b56b23f31743b93df9cf8948f15ddb54'
  const encryptData = sm2.doEncrypt(parameter.password, publicKey, 1)
  parameter.password = encryptData

  return axios({
    url: '/login',
    method: 'post',
    data: parameter
  })
}

/**
 * 登出
 *
 * @author yubaoshan
 * @date 2020/5/26 19:07
 */
export function logout (parameter) {
  return axios({
    url: '/logout',
    method: 'get',
    params: parameter
  })
}

/**
 * 获取登录用户信息
 *
 * @author yubaoshan
 * @date 2020/5/26 19:08
 */
export function getLoginUser (parameter) {
  return axios({
    url: '/getLoginUser',
    method: 'get',
    params: parameter
  })
}

/**
 * 获取租户开关
 *
 * @author yubaoshan
 * @date 2020/9/5 1:24
 */
export function getTenantOpen (parameter) {
  return axios({
    url: '/getTenantOpen',
    method: 'get',
    params: parameter
  })
}

/**
 * 获取短信验证码
 *
 * @author yubaoshan
 * @date 2020/5/26 19:29
 */
export function getSmsCaptcha (parameter) {
  return axios({
    url: '/getSmsCaptcha',
    method: 'get',
    params: parameter
  })
}

/**
 * 获取验证码开关
 *
 * @author Jax
 * @date 2021/1/22 00:00
 */
export function getCaptchaOpen (parameter) {
  return axios({
    url: '/getCaptchaOpen',
    method: 'get',
    params: parameter
  })
}

/**
 * 获取验证图片 以及token
 *
 * @author Jax
 * @date 2021/1/22 00:00
 */
export function reqGet(data) {
  return axios({
    url: '/captcha/get',
    method: 'post',
    data
  })
}

/**
 * 滑动或者点选验证
 *
 * @author Jax
 * @date 2021/1/22 00:00
 */
export function reqCheck(data) {
  return axios({
    url: '/captcha/check',
    method: 'post',
    data
  })
}
