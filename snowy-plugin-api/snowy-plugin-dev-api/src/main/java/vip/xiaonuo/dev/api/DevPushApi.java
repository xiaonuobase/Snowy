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
package vip.xiaonuo.dev.api;

/**
 * 邮件API接口
 *
 * @author xuyuxiang
 * @date 2022/6/22 15:21
 **/
public interface DevPushApi {

    /**
     * 动态推送消息（使用系统配置的默认消息推送引擎）
     *
     * @param content 内容
     * @param noticeAll 是否通知所有人
     * @author xuyuxiang
     * @date 2022/2/23 14:24
     */
    void pushDynamicText(String content, boolean noticeAll);

    /**
     * 推送消息——飞书TXT
     *
     * @param content 内容
     * @param noticeAll 是否通知所有人
     * @author xuyuxiang
     * @date 2022/2/23 14:24
     */
    void pushFeiShuText(String content, boolean noticeAll);

    /**
     * 推送消息——钉钉TXT
     *
     * @param content 内容
     * @param noticeAll 是否通知所有人
     * @param phones 通知的用户手机号，英文逗号分割
     * @author xuyuxiang
     * @date 2022/2/23 14:24
     */
    void pushDingTalkText(String content, boolean noticeAll, String phones);

    /**
     * 推送消息——钉钉MARKDOWN
     *
     * @param title 标题
     * @param content 内容
     * @param noticeAll 是否通知所有人
     * @author xuyuxiang
     * @date 2022/2/23 14:24
     */
    void pushDingTalkMarkdown(String title, String content, boolean noticeAll);

    /**
     * 推送消息——钉钉LINK
     *
     * @param title 标题
     * @param content 内容
     * @param picUrl 封面图片地址
     * @param messageUrl 消息链接地址
     * @author xuyuxiang
     * @date 2022/2/23 14:24
     */
    void pushDingTalkLink(String title, String content, String picUrl,String messageUrl);

    /**
     * 推送消息——企业微信TXT
     *
     * @param content 内容
     * @param noticeAll 是否通知所有人
     * @param phones 通知的用户手机号，英文逗号分割
     * @author xuyuxiang
     * @date 2022/2/23 14:24
     */
    void pushWorkWechatText(String content, boolean noticeAll, String phones);

    /**
     * 推送消息——企业微信MARKDOWN
     *
     * @param title 标题
     * @param content 内容
     * @author xuyuxiang
     * @date 2022/2/23 14:24
     */
    void pushWorkWechatMarkdown(String title, String content);

    /**
     * 推送消息——企业微信NEWS
     *
     * @param title 标题
     * @param content 内容
     * @param picUrl 封面图片地址
     * @param messageUrl 消息链接地址
     * @author xuyuxiang
     * @date 2022/2/23 14:24
     */
    void pushWorkWechatNews(String title, String content, String picUrl,String messageUrl);
}
