import { axios } from '@/utils/request'

/**
 * 发送邮件
 *
 * @author yubaoshan
 * @date 2020/7/3 23:22
 */
export function emailSendEmail (parameter) {
  return axios({
    url: '/email/sendEmail',
    method: 'post',
    data: parameter
  })
}

/**
 * 发送html邮件
 *
 * @author yubaoshan
 * @date 2020/7/3 23:23
 */
export function emailSendEmailHtml (parameter) {
  return axios({
    url: '/email/sendEmailHtml',
    method: 'post',
    data: parameter
  })
}
