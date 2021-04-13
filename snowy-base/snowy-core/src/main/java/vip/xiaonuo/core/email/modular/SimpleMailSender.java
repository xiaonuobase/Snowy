/*
Copyright [2020] [https://www.xiaonuo.vip]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：

1.请不要删除和修改根目录下的LICENSE文件。
2.请不要删除和修改Snowy源码头部的版权声明。
3.请保留源码和相关描述文件的项目出处，作者声明等。
4.分发源码时候，请注明软件出处 https://gitee.com/xiaonuobase/snowy
5.在修改包名，模块名称，项目代码等时，请注明软件出处 https://gitee.com/xiaonuobase/snowy
6.若您的项目无法满足以上几点，可申请商业授权，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package vip.xiaonuo.core.email.modular;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import vip.xiaonuo.core.email.MailSender;
import vip.xiaonuo.core.email.modular.exception.MailSendException;
import vip.xiaonuo.core.email.modular.model.SendMailParam;

/**
 * 邮件发送器
 *
 * @author xuyuxiang
 * @date 2020/6/9 22:54
 */
public class SimpleMailSender implements MailSender {

    /**
     * 邮件配置
     */
    private final MailAccount mailAccount;

    public SimpleMailSender(MailAccount mailAccount) {
        this.mailAccount = mailAccount;
    }

    @Override
    public void sendMail(SendMailParam sendMailParam) {

        //校验发送邮件的参数
        assertSendMailParams(sendMailParam);

        //spring发送邮件
        MailUtil.send(mailAccount, CollUtil.newArrayList(sendMailParam.getTo()), sendMailParam.getTitle(), sendMailParam.getContent(), false);
    }

    @Override
    public void sendMailHtml(SendMailParam sendMailParam) {

        //校验发送邮件的参数
        assertSendMailParams(sendMailParam);

        //spring发送邮件
        MailUtil.send(mailAccount, CollUtil.newArrayList(sendMailParam.getTo()), sendMailParam.getTitle(), sendMailParam.getContent(), true);
    }

    /**
     * 校验发送邮件的请求参数
     *
     * @author xuyuxiang
     * @date 2018/7/8 下午6:41
     */
    private void assertSendMailParams(SendMailParam sendMailParam) {
        if (sendMailParam == null) {
            throw new MailSendException(400, "请求参数为空");
        }

        if (ObjectUtil.isEmpty(sendMailParam.getTo())) {
            throw new MailSendException(400, "收件人邮箱为空");
        }

        if (ObjectUtil.isEmpty(sendMailParam.getTitle())) {
            throw new MailSendException(400, "邮件标题为空");
        }

        if (ObjectUtil.isEmpty(sendMailParam.getContent())) {
            throw new MailSendException(400, "邮件内容为空");
        }
    }

}
