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
package vip.xiaonuo.dev.modular.push.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.dev.modular.push.entity.DevPush;
import vip.xiaonuo.dev.modular.push.param.*;

import java.util.List;

/**
 * 消息推送Service接口
 *
 * @author xuyuxiang
 * @date 2022/2/23 18:27
 **/
public interface DevPushService extends IService<DevPush> {

    /**
     * 动态推送消息
     *
     * @param engine 推送引擎
     * @param content 文本内容
     * @param noticeAll 是否@所有人
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    void pushDynamicText(String engine, String content, boolean noticeAll);

    /**
     * 动态推送消息
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    void pushDynamicText(DevPushDynamicTextParam devPushDynamicTextParam);

    /**
     * 推送消息——飞书TXT
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    void pushFeiShuText(DevPushFeiShuTextParam devPushFeiShuTextParam);

    /**
     * 推送消息——钉钉TXT
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    void pushDingTalkText(DevPushDingTalkTextParam devPushDingTalkTextParam);

    /**
     * 推送消息——钉钉MARKDOWN
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    void pushDingTalkMarkdown(DevPushDingTalkMarkdownParam devPushDingTalkMarkdownParam);

    /**
     * 推送消息——钉钉LINK
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    void pushDingTalkLink(DevPushDingTalkLinkParam devPushDingTalkLinkParam);

    /**
     * 推送消息——企业微信TXT
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    void pushWorkWechatText(DevPushWorkWechatTextParam devPushWorkWechatTextParam);

    /**
     * 推送消息——企业微信MARKDOWN
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    void pushWorkWechatMarkdown(DevPushWorkWechatMarkdownParam devPushWorkWechatMarkdownParam);

    /**
     * 推送消息——企业微信NEWS
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    void pushWorkWechatNews(DevPushWorkWechatNewsParam devPushWorkWechatNewsParam);

    /**
     * 获取推送消息分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    Page<DevPush> page(DevPushPageParam devPushPageParam);

    /**
     * 删除推送消息
     *
     * @author xuyuxiang
     * @date 2022/8/4 10:36
     **/
    void delete(List<DevPushIdParam> devPushIdParamList);

    /**
     * 获取推送消息详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    DevPush detail(DevPushIdParam devPushIdParam);

    /**
     * 获取推送消息详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    DevPush queryEntity(String id);
}
