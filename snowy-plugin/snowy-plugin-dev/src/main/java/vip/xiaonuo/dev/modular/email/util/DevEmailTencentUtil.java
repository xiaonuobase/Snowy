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

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.ses.v20201002.SesClient;
import com.tencentcloudapi.ses.v20201002.models.*;
import lombok.extern.slf4j.Slf4j;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.dev.api.DevConfigApi;

import java.util.List;

/**
 * 腾讯云邮件工具类
 * 参考文档：https://cloud.tencent.com/document/api/1288/51034
 *
 * @author xuyuxiang
 * @date 2022/6/17 11:26
 **/
@Slf4j
public class DevEmailTencentUtil {

    private static SesClient client;

    private static final String SNOWY_EMAIL_TENCENT_SECRET_ID_KEY = "SNOWY_EMAIL_TENCENT_SECRET_ID";
    private static final String SNOWY_EMAIL_TENCENT_SECRET_KEY_KEY = "SNOWY_EMAIL_TENCENT_SECRET_KEY";
    private static final String SNOWY_EMAIL_TENCENT_REGION_ID_KEY = "SNOWY_EMAIL_TENCENT_REGION_ID";

    /**
     * 初始化操作的客户端
     *
     * @author xuyuxiang
     * @date 2022/1/5 23:24
     */
    private static void initClient() {

        DevConfigApi devConfigApi = SpringUtil.getBean(DevConfigApi.class);

        /* secretId */
        String secretId = devConfigApi.getValueByKey(SNOWY_EMAIL_TENCENT_SECRET_ID_KEY);

        if(ObjectUtil.isEmpty(secretId)) {
            throw new CommonException("腾讯云邮件操作客户端未正确配置：secretId为空");
        }

        /* secretKey */
        String secretKey = devConfigApi.getValueByKey(SNOWY_EMAIL_TENCENT_SECRET_KEY_KEY);

        if(ObjectUtil.isEmpty(secretKey)) {
            throw new CommonException("腾讯云邮件操作客户端未正确配置：secretKey为空");
        }

        /* regionId */
        String regionId = devConfigApi.getValueByKey(SNOWY_EMAIL_TENCENT_REGION_ID_KEY);

        if(ObjectUtil.isEmpty(regionId)) {
            throw new CommonException("腾讯云邮件操作客户端未正确配置：regionId为空");
        }

        client = new SesClient(new Credential(secretId, secretKey), regionId);
    }

    /**
     * 发送纯文本邮件（不使用模板，默认接口请求频率限制：20次/秒。）
     *
     * @param from 管理控制台中配置的发信地址，必传且必须正确
     * @param user 发信人昵称，可不传
     * @param tos 目标地址，多个 email 地址可以用逗号分隔，最多50个地址，必传且必须正确，非群发邮件请多次调用API发送
     * @param subject 邮件主题，必传
     * @param content 邮件 txt 正文，必传，注意：腾讯云api目前要求请求包大小不得超过8 MB。
     * @param attachmentList 需要发送附件时，填写附件相关参数，格式:[{"FileName": "xxxx", "Content": "xxx"}]
     *                       支持的格式与说明见：https://cloud.tencent.com/document/api/1288/51053#Attachment
     * @return 发送成功的回执id
     * @author xuyuxiang
     * @date 2022/2/23 14:24
     **/
    public static String sendTextEmail(String from, String user, String tos, String subject, String content, List<JSONObject> attachmentList) {
        try {
            initClient();
            SendEmailRequest singleSendMailRequest = createSingleSendRequest(from, user, tos, subject, content, false, attachmentList);
            return client.SendEmail(singleSendMailRequest).getMessageId();
        } catch (TencentCloudSDKException e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 发送HTML邮件（不使用模板，默认接口请求频率限制：20次/秒。）
     *
     * @param from 管理控制台中配置的发信地址，必传且必须正确
     * @param user 发信人昵称，可不传
     * @param tos 目标地址，多个 email 地址可以用逗号分隔，最多50个地址，必传且必须正确，非群发邮件请多次调用API发送
     * @param subject 邮件主题，必传
     * @param content 邮件 txt 正文，必传，注意：腾讯云api目前要求请求包大小不得超过8 MB。
     * @param attachmentList 需要发送附件时，填写附件相关参数，格式:[{"FileName": "xxxx", "Content": "xxx"}]
     *                       支持的格式与说明见：https://cloud.tencent.com/document/api/1288/51053#Attachment
     * @return 发送成功的回执id
     * @author xuyuxiang
     * @date 2022/2/23 14:24
     **/
    public static String sendHtmlEmail(String from, String user, String tos, String subject, String content, List<JSONObject> attachmentList) {
        try {
            initClient();
            SendEmailRequest singleSendMailRequest = createSingleSendRequest(from, user, tos, subject, content, true, attachmentList);
            return client.SendEmail(singleSendMailRequest).getMessageId();
        } catch (TencentCloudSDKException e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 使用模板发送邮件，默认接口请求频率限制：20次/秒。
     *
     * @param from 管理控制台中配置的发信地址，必传且必须正确
     * @param user 发信人昵称，可不传
     * @param toId 预先创建且上传了收件人的收件人列表id，必传且必须正确
     * @param templateId 预先创建且通过审核的模板Id，必传且必须正确
     * @param templateParam 预先创建且通过审核的模板的参数json，格式{"name":"张三"}，可不传
     * @param subject 邮件主题，必传
     * @param attachmentList 需要发送附件时，填写附件相关参数，格式:[{"FileName": "xxxx", "Content": "xxx"}]
     *                       支持的格式与说明见：https://cloud.tencent.com/document/api/1288/51053#Attachment
     * @return 发送成功的回执id
     * @author xuyuxiang
     * @date 2022/2/23 14:24
     **/
    public static String sendEmailWithTemplate(String from, String user, String toId, String templateId,
                                        String templateParam, String subject, List<JSONObject> attachmentList) {
        try {
            initClient();
            BatchSendEmailRequest batchSendEmailRequest = createBatchSendRequest(from, user, toId, templateId, templateParam, subject, attachmentList);
            return client.BatchSendEmail(batchSendEmailRequest).getTaskId().toString();
        } catch (TencentCloudSDKException e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 创建发送一个邮件的请求
     *
     * @author xuyuxiang
     * @date 2022/2/23 13:33
     **/
    private static SendEmailRequest createSingleSendRequest(String from, String user, String tos, String subject,
                                                     String content, boolean isHtml, List<JSONObject> attachmentList) {
        SendEmailRequest sendEmailRequest = new SendEmailRequest();
        sendEmailRequest.setFromEmailAddress(ObjectUtil.isNotEmpty(user)?user + " <" + from + ">" :from);
        sendEmailRequest.setDestination(StrUtil.splitToArray(tos, StrUtil.COMMA));
        sendEmailRequest.setSubject(subject);
        Simple simple = new Simple();
        if(isHtml) {
            simple.setHtml(Base64.encode(content));
        } else {
            simple.setText(Base64.encode(content));
        }
        sendEmailRequest.setSimple(simple);
        if(ObjectUtil.isNotEmpty(attachmentList)) {
            Attachment[] attachments = (Attachment[]) attachmentList.stream().map(jsonObject -> {
                Attachment attachment = new Attachment();
                BeanUtil.copyProperties(jsonObject, attachment);
                return attachment;
            }).toArray();
            sendEmailRequest.setAttachments(attachments);
        }
        return sendEmailRequest;
    }

    /**
     * 创建发送批量邮件的请求
     *
     * @author xuyuxiang
     * @date 2022/2/23 13:33
     **/
    private static BatchSendEmailRequest createBatchSendRequest(String from, String user, String toId, String templateId,
                                                         String templateParam, String subject, List<JSONObject> attachmentList) {
        BatchSendEmailRequest batchSendEmailRequest = new BatchSendEmailRequest();
        batchSendEmailRequest.setFromEmailAddress(ObjectUtil.isNotEmpty(user)?user + " <" + from + ">" :from);
        batchSendEmailRequest.setReceiverId(Convert.toLong(toId));
        Template template = new Template();
        template.setTemplateID(Convert.toLong(templateId));
        template.setTemplateData(templateParam);
        batchSendEmailRequest.setTemplate(template);
        batchSendEmailRequest.setSubject(subject);
        batchSendEmailRequest.setTaskType(1L);
        if(ObjectUtil.isNotEmpty(attachmentList)) {
            Attachment[] attachments = (Attachment[]) attachmentList.stream().map(jsonObject -> {
                Attachment attachment = new Attachment();
                BeanUtil.copyProperties(jsonObject, attachment);
                return attachment;
            }).toArray();
            batchSendEmailRequest.setAttachments(attachments);
        }
        return batchSendEmailRequest;
    }
}
