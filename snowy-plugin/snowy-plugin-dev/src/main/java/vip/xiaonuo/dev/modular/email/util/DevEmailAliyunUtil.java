/*
 * Copyright [2022] [https://www.xiaonuo.vip]
 *
 * Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *
 * 1.请不要删除和修改根目录下的LICENSE文件。
 * 2.请不要删除和修改Snowy源码头部的版权声明。
 * 3.本项目代码可免费商业使用，商业使用请保留源码和相关描述文件的项目出处，作者声明等。
 * 4.分发源码时候，请注明软件出处 https://www.xiaonuo.vip
 * 5.不可二次分发开源参与同类竞品，如有想法可联系团队xiaonuobase@qq.com商议合作。
 * 6.若您的项目无法满足以上几点，需要更多功能代码，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package vip.xiaonuo.dev.modular.email.util;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.aliyun.dm20151123.Client;
import com.aliyun.dm20151123.models.BatchSendMailRequest;
import com.aliyun.dm20151123.models.SingleSendMailRequest;
import com.aliyun.teaopenapi.models.Config;
import lombok.extern.slf4j.Slf4j;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.dev.api.DevConfigApi;

/**
 * 阿里云邮件工具类
 * 参考文档：https://help.aliyun.com/document_detail/29459.html
 *
 * @author xuyuxiang
 * @date 2022/6/17 10:17
 **/
@Slf4j
public class DevEmailAliyunUtil {

    private static Client client;

    private static final String SNOWY_EMAIL_ALIYUN_ACCESS_KEY_ID_KEY = "SNOWY_EMAIL_ALIYUN_ACCESS_KEY_ID";
    private static final String SNOWY_EMAIL_ALIYUN_ACCESS_KEY_SECRET_KEY = "SNOWY_EMAIL_ALIYUN_ACCESS_KEY_SECRET";
    private static final String SNOWY_EMAIL_ALIYUN_REGION_ID_KEY = "SNOWY_EMAIL_ALIYUN_REGION_ID";

    /**
     * 初始化操作的客户端
     *
     * @author xuyuxiang
     * @date 2022/1/5 23:24
     */
    private static void initClient() {

        DevConfigApi devConfigApi = SpringUtil.getBean(DevConfigApi.class);

        /* accessKeyId */
        String accessKeyId = devConfigApi.getValueByKey(SNOWY_EMAIL_ALIYUN_ACCESS_KEY_ID_KEY);

        if(ObjectUtil.isEmpty(accessKeyId)) {
            throw new CommonException("阿里云邮件操作客户端未正确配置：accessKeyId为空");
        }

        /* accessKeySecret */
        String accessKeySecret = devConfigApi.getValueByKey(SNOWY_EMAIL_ALIYUN_ACCESS_KEY_SECRET_KEY);

        if(ObjectUtil.isEmpty(accessKeySecret)) {
            throw new CommonException("阿里云邮件操作客户端未正确配置：accessKeySecret为空");
        }

        /* regionId */
        String regionId = devConfigApi.getValueByKey(SNOWY_EMAIL_ALIYUN_REGION_ID_KEY);

        if(ObjectUtil.isEmpty(regionId)) {
            throw new CommonException("阿里云邮件操作客户端未正确配置：regionId为空");
        }

        try {
            client = new Client(new Config().setRegionId(regionId).setEndpoint("dm.aliyuncs.com").setAccessKeyId(accessKeyId).setAccessKeySecret(accessKeySecret));
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 发送纯文本邮件（不使用模板，频率限制100 QPS）
     *
     * @param from 管理控制台中配置的发信地址，必传且必须正确
     * @param user 发信人昵称，长度小于15个字符，可不传
     * @param tos 目标地址，多个 email 地址可以用逗号分隔，最多100个地址，必传且必须正确
     * @param subject 邮件主题，必传
     * @param content 邮件 txt 正文，限制28K，必传
     * @return 发送成功的回执id
     * @author xuyuxiang
     * @date 2022/2/23 14:24
     **/
    public static String sendTextEmail(String from, String user, String tos, String subject, String content) {
        try {
            initClient();
            SingleSendMailRequest singleSendMailRequest = createSingleSendRequest(from, user, tos, subject, content, false);
            return client.singleSendMail(singleSendMailRequest).getBody().getEnvId();
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 发送HTML邮件（不使用模板，频率限制100 QPS）
     *
     * @param from 管理控制台中配置的发信地址，必传且必须正确
     * @param user 发信人昵称，长度小于15个字符，可不传
     * @param tos 目标地址，多个 email 地址可以用逗号分隔，最多100个地址，必传且必须正确
     * @param subject 邮件主题，必传
     * @param content 邮件 html 正文，限制28K，必传
     * @return 发送成功的回执id
     * @author xuyuxiang
     * @date 2022/2/23 14:24
     **/
    public static String sendHtmlEmail(String from, String user, String tos, String subject, String content) {
        try {
            initClient();
            SingleSendMailRequest singleSendMailRequest = createSingleSendRequest(from, user, tos, subject, content, true);
            return client.singleSendMail(singleSendMailRequest).getBody().getEnvId();
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 使用模板发送邮件，国内频率限制是20/min；海外频率限制是10/min。
     *
     * @param from 管理控制台中配置的发信地址，必传且必须正确
     * @param tagName 控制台创建的邮件标签，可不传
     * @param toName 预先创建且上传了收件人的收件人列表名称，必传且必须正确
     * @param templateName 预先创建且通过审核的模板名称，必传且必须正确
     * @return 发送成功的回执id
     * @author xuyuxiang
     * @date 2022/2/23 14:24
     **/
    public static String sendEmailWithTemplate(String from, String tagName, String toName, String templateName) {
        try {
            initClient();
            BatchSendMailRequest batchSendMailRequest = createBatchSendRequest(from, tagName, toName, templateName);
            return client.batchSendMail(batchSendMailRequest).getBody().getEnvId();
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }


    /**
     * 创建发送一个邮件的请求
     *
     * @author xuyuxiang
     * @date 2022/2/23 13:33
     **/
    private static SingleSendMailRequest createSingleSendRequest(String from, String user, String tos, String subject, String content, boolean isHtml) {
        SingleSendMailRequest request = new SingleSendMailRequest();

        // 控制台创建的发信地址
        request.setAccountName(from);

        // 发信人昵称
        request.setFromAlias(user);

        // 地址类型：0-为随机账号，1-为发信地址
        request.setAddressType(1);

        // 使用管理台配置的回信地址
        request.setReplyToAddress(true);

        // 目标地址
        request.setToAddress(tos);

        // 邮件主题
        request.setSubject(subject);

        //如果采用byte[].toString的方式的话请确保最终转换成utf-8的格式再放入htmlbody和textbody，若编码不一致则会被当成垃圾邮件。
        if(isHtml) {
            request.setHtmlBody(content);
        } else  {
            request.setTextBody(content);
        }

        //是否开启追踪功能，开启需要备案，0关闭，1开启
        request.setClickTrace("0");

        return request;
    }

    /**
     * 创建发送批量邮件的请求
     *
     * @author fengshuonan
     * @date 2020/10/30 22:39
     */
    private static BatchSendMailRequest createBatchSendRequest(String from, String tagName, String toName, String templateName) {
        BatchSendMailRequest request = new BatchSendMailRequest();

        // 控制台创建的发信地址
        request.setAccountName(from);

        // 预先创建且上传了收件人的收件人列表名称
        request.setReceiversName(toName);

        // 邮件模板，在控制台创建，相当于邮件的内容
        request.setTemplateName(templateName);

        // 地址类型：0-为随机账号，1-为发信地址
        request.setAddressType(1);

        // 控制台创建的标签
        request.setTagName(tagName);

        //是否开启追踪功能，开启需要备案，0关闭，1开启
        request.setClickTrace("0");

        return request;
    }
}
