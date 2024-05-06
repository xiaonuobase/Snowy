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
package vip.xiaonuo.dev.modular.email.provider;

import cn.hutool.json.JSONObject;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import vip.xiaonuo.dev.api.DevEmailApi;
import vip.xiaonuo.dev.modular.email.param.*;
import vip.xiaonuo.dev.modular.email.service.DevEmailService;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 邮件API接口提供者
 *
 * @author xuyuxiang
 * @date 2022/6/22 15:32
 **/
@Service
public class DevEmailApiProvider implements DevEmailApi {

    @Resource
    private DevEmailService devEmailService;

    @Override
    public void sendTextEmailLocal(String tos, String subject, String content, List<File> files) {
        DevEmailSendLocalTxtParam devEmailSendLocalTxtParam = new DevEmailSendLocalTxtParam();
        devEmailSendLocalTxtParam.setReceiveAccounts(tos);
        devEmailSendLocalTxtParam.setSubject(subject);
        devEmailSendLocalTxtParam.setContent(content);
        devEmailSendLocalTxtParam.setFiles(files);
        devEmailService.sendLocal(devEmailSendLocalTxtParam);
    }

    @Override
    public void sendHtmlEmailLocal(String tos, String subject, String content, Map<String, InputStream> imageMap, List<File> files) {
        DevEmailSendLocalHtmlParam devEmailSendLocalHtmlParam = new DevEmailSendLocalHtmlParam();
        devEmailSendLocalHtmlParam.setReceiveAccounts(tos);
        devEmailSendLocalHtmlParam.setSubject(subject);
        devEmailSendLocalHtmlParam.setContent(content);
        devEmailSendLocalHtmlParam.setImageMap(imageMap);
        devEmailSendLocalHtmlParam.setFiles(files);
        devEmailService.sendLocal(devEmailSendLocalHtmlParam);
    }

    @Override
    public void sendTextEmailAliyun(String from, String user, String tos, String subject, String content) {
        DevEmailSendAliyunTxtParam devEmailSendAliyunTxtParam = new DevEmailSendAliyunTxtParam();
        devEmailSendAliyunTxtParam.setSendAccount(from);
        devEmailSendAliyunTxtParam.setSendUser(user);
        devEmailSendAliyunTxtParam.setReceiveAccounts(tos);
        devEmailSendAliyunTxtParam.setSubject(subject);
        devEmailSendAliyunTxtParam.setContent(content);
        devEmailService.sendAliyun(devEmailSendAliyunTxtParam);
    }

    @Override
    public void sendHtmlEmailAliyun(String from, String user, String tos, String subject, String content) {
        DevEmailSendAliyunHtmlParam devEmailSendAliyunHtmlParam = new DevEmailSendAliyunHtmlParam();
        devEmailSendAliyunHtmlParam.setSendAccount(from);
        devEmailSendAliyunHtmlParam.setSendUser(user);
        devEmailSendAliyunHtmlParam.setReceiveAccounts(tos);
        devEmailSendAliyunHtmlParam.setSubject(subject);
        devEmailSendAliyunHtmlParam.setContent(content);
        devEmailService.sendAliyun(devEmailSendAliyunHtmlParam);
    }

    @Override
    public void sendEmailWithTemplateAliyun(String from, String tagName, String toName, String templateName) {
        DevEmailSendAliyunTmpParam devEmailSendAliyunTmpParam = new DevEmailSendAliyunTmpParam();
        devEmailSendAliyunTmpParam.setReceiveAccounts(toName);
        devEmailSendAliyunTmpParam.setTagName(tagName);
        devEmailSendAliyunTmpParam.setTemplateName(templateName);
        devEmailService.sendAliyun(devEmailSendAliyunTmpParam);
    }

    @Override
    public void sendTextEmailTencent(String from, String user, String tos, String subject, String content, List<JSONObject> attachmentList) {
        DevEmailSendTencentTxtParam devEmailSendTencentTxtParam = new DevEmailSendTencentTxtParam();
        devEmailSendTencentTxtParam.setSendAccount(from);
        devEmailSendTencentTxtParam.setSendUser(user);
        devEmailSendTencentTxtParam.setReceiveAccounts(tos);
        devEmailSendTencentTxtParam.setSubject(subject);
        devEmailSendTencentTxtParam.setContent(content);
        devEmailSendTencentTxtParam.setAttachmentList(attachmentList);
        devEmailService.sendTencent(devEmailSendTencentTxtParam);
    }

    @Override
    public void sendHtmlEmailTencent(String from, String user, String tos, String subject, String content, List<JSONObject> attachmentList) {
        DevEmailSendTencentHtmlParam devEmailSendTencentHtmlParam = new DevEmailSendTencentHtmlParam();
        devEmailSendTencentHtmlParam.setSendAccount(from);
        devEmailSendTencentHtmlParam.setSendUser(user);
        devEmailSendTencentHtmlParam.setReceiveAccounts(tos);
        devEmailSendTencentHtmlParam.setSubject(subject);
        devEmailSendTencentHtmlParam.setContent(content);
        devEmailSendTencentHtmlParam.setAttachmentList(attachmentList);
        devEmailService.sendTencent(devEmailSendTencentHtmlParam);
    }

    @Override
    public void sendEmailWithTemplateTencent(String from, String user, String toId, String templateId, String templateParam, String subject, List<JSONObject> attachmentList) {
        DevEmailSendTencentTmpParam devEmailSendTencentTmpParam = new DevEmailSendTencentTmpParam();
        devEmailSendTencentTmpParam.setSendAccount(from);
        devEmailSendTencentTmpParam.setSendUser(user);
        devEmailSendTencentTmpParam.setReceiveAccounts(toId);
        devEmailSendTencentTmpParam.setTemplateName(templateId);
        devEmailSendTencentTmpParam.setAttachmentList(attachmentList);
        devEmailService.sendTencent(devEmailSendTencentTmpParam);
    }
}
