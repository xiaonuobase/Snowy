package com.cn.xiaonuo.core.email;


import com.cn.xiaonuo.core.email.modular.model.SendMailParam;

/**
 * 邮件收发统一接口
 *
 * @author xuyuxiang
 * @date 2018-07-08-下午3:26
 */
public interface MailSender {

    /**
     * 发送普通邮件
     *
     * @author xuyuxiang
     * @date 2018/7/8 下午3:34
     */
    void sendMail(SendMailParam sendMailParam);

    /**
     * 发送html的邮件
     *
     * @author xuyuxiang
     * @date 2020/6/9 22:58
     */
    void sendMailHtml(SendMailParam sendMailParam);

}
