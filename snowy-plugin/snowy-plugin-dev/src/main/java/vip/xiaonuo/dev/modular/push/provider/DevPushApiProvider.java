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
package vip.xiaonuo.dev.modular.push.provider;

import cn.hutool.core.util.ObjectUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import vip.xiaonuo.dev.api.DevPushApi;
import vip.xiaonuo.dev.modular.push.enums.DevPushNoticeTypeEnum;
import vip.xiaonuo.dev.modular.push.param.*;
import vip.xiaonuo.dev.modular.push.service.DevPushService;

/**
 * 消息推送API接口提供者
 *
 * @author xuyuxiang
 * @date 2022/6/22 15:32
 **/
@Service
public class DevPushApiProvider implements DevPushApi {

    @Resource
    private DevPushService devPushService;

    @Override
    public void pushDynamicText(String content, boolean noticeAll) {
        DevPushDynamicTextParam dynamicTextParam = new DevPushDynamicTextParam();
        dynamicTextParam.setContent(content);
        dynamicTextParam.setNoticeAll(noticeAll);
        devPushService.pushDynamicText(dynamicTextParam);
    }

    @Override
    public void pushFeiShuText(String content, boolean noticeAll) {
        DevPushFeiShuTextParam pushFeiShuTextParam = new DevPushFeiShuTextParam();
        pushFeiShuTextParam.setContent(content);
        if(noticeAll){
            pushFeiShuTextParam.setNoticeType(DevPushNoticeTypeEnum.ALL.getValue());
        } else {
            pushFeiShuTextParam.setNoticeType(DevPushNoticeTypeEnum.NONE.getValue());
        }
        devPushService.pushFeiShuText(pushFeiShuTextParam);
    }

    @Override
    public void pushDingTalkText(String content, boolean noticeAll, String phones) {
        DevPushDingTalkTextParam pushDingTalkTextParam = new DevPushDingTalkTextParam();
        pushDingTalkTextParam.setContent(content);
        if(noticeAll){
            pushDingTalkTextParam.setNoticeType(DevPushNoticeTypeEnum.ALL.getValue());
        } else {
            if(ObjectUtil.isNotEmpty(phones)){
                pushDingTalkTextParam.setNoticeType(DevPushNoticeTypeEnum.PHONE.getValue());
            } else {
                pushDingTalkTextParam.setNoticeType(DevPushNoticeTypeEnum.NONE.getValue());
            }
        }
        devPushService.pushDingTalkText(pushDingTalkTextParam);
    }

    @Override
    public void pushDingTalkMarkdown(String title, String content, boolean noticeAll) {
        DevPushDingTalkMarkdownParam devPushDingTalkMarkdownParam = new DevPushDingTalkMarkdownParam();
        devPushDingTalkMarkdownParam.setTitle(title);
        devPushDingTalkMarkdownParam.setContent(content);
        if(noticeAll){
            devPushDingTalkMarkdownParam.setNoticeType(DevPushNoticeTypeEnum.ALL.getValue());
        } else {
            devPushDingTalkMarkdownParam.setNoticeType(DevPushNoticeTypeEnum.NONE.getValue());
        }
        devPushService.pushDingTalkMarkdown(devPushDingTalkMarkdownParam);
    }

    @Override
    public void pushDingTalkLink(String title, String content, String picUrl, String messageUrl) {
        DevPushDingTalkLinkParam devPushDingTalkLinkParam = new DevPushDingTalkLinkParam();
        devPushDingTalkLinkParam.setTitle(title);
        devPushDingTalkLinkParam.setContent(content);
        devPushDingTalkLinkParam.setPicUrl(picUrl);
        devPushDingTalkLinkParam.setMessageUrl(messageUrl);
        devPushService.pushDingTalkLink(devPushDingTalkLinkParam);
    }

    @Override
    public void pushWorkWechatText(String content, boolean noticeAll, String phones) {
        DevPushWorkWechatTextParam devPushWorkWechatTextParam = new DevPushWorkWechatTextParam();
        devPushWorkWechatTextParam.setContent(content);
        if(noticeAll){
            devPushWorkWechatTextParam.setNoticeType(DevPushNoticeTypeEnum.ALL.getValue());
        } else {
            if(ObjectUtil.isNotEmpty(phones)){
                devPushWorkWechatTextParam.setNoticeType(DevPushNoticeTypeEnum.PHONE.getValue());
            } else {
                devPushWorkWechatTextParam.setNoticeType(DevPushNoticeTypeEnum.NONE.getValue());
            }
        }
        devPushService.pushWorkWechatText(devPushWorkWechatTextParam);
    }

    @Override
    public void pushWorkWechatMarkdown(String title, String content) {
        DevPushWorkWechatMarkdownParam devPushWorkWechatMarkdownParam = new DevPushWorkWechatMarkdownParam();
        devPushWorkWechatMarkdownParam.setTitle(title);
        devPushWorkWechatMarkdownParam.setContent(content);
        devPushService.pushWorkWechatMarkdown(devPushWorkWechatMarkdownParam);
    }

    @Override
    public void pushWorkWechatNews(String title, String content, String picUrl, String messageUrl) {
        DevPushWorkWechatNewsParam devPushWorkWechatNewsParam = new DevPushWorkWechatNewsParam();
        devPushWorkWechatNewsParam.setTitle(title);
        devPushWorkWechatNewsParam.setContent(content);
        devPushWorkWechatNewsParam.setPicUrl(picUrl);
        devPushWorkWechatNewsParam.setMessageUrl(messageUrl);
        devPushService.pushWorkWechatNews(devPushWorkWechatNewsParam);
    }
}
