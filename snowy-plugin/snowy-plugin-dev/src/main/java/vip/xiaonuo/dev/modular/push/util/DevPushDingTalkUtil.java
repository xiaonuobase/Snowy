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
package vip.xiaonuo.dev.modular.push.util;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.dromara.oa.api.OaSender;
import org.dromara.oa.comm.entity.Request;
import org.dromara.oa.comm.entity.Response;
import org.dromara.oa.comm.enums.MessageType;
import org.dromara.oa.core.dingTalk.config.DingTalkConfig;
import org.dromara.oa.core.dingTalk.config.DingTalkFactory;
import org.dromara.oa.core.provider.factory.OaFactory;
import org.dromara.oa.core.provider.factory.ProviderFactoryHolder;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.dev.api.DevConfigApi;

/**
 * 钉钉消息推送工具类
 *
 * @author xuyuxiang
 * @date 2022/1/2 17:05
 */
@Slf4j
public class DevPushDingTalkUtil {

    private static OaSender oaSender;

    private static final String SNOWY_PUSH_DINGTALK_SIGN_KEY = "SNOWY_PUSH_DINGTALK_SIGN";
    private static final String SNOWY_PUSH_DINGTALK_TOKEN_ID_KEY = "SNOWY_PUSH_DINGTALK_TOKEN_ID";

    static {
        ProviderFactoryHolder.registerFactory(DingTalkFactory.instance());
    }

    /**
     * 初始化操作的客户端
     *
     * @author xuyuxiang
     * @date 2022/1/5 23:24
     */
    private static void initClient() {

        DevConfigApi devConfigApi = SpringUtil.getBean(DevConfigApi.class);

        /* sign */
        String sign = devConfigApi.getValueByKey(SNOWY_PUSH_DINGTALK_SIGN_KEY);

        if(ObjectUtil.isEmpty(sign)) {
            throw new CommonException("钉钉推送操作客户端未正确配置：sign为空");
        }

        /* tokenId */
        String tokenId = devConfigApi.getValueByKey(SNOWY_PUSH_DINGTALK_TOKEN_ID_KEY);

        if(ObjectUtil.isEmpty(tokenId)) {
            throw new CommonException("钉钉推送操作客户端未正确配置：tokenId为空");
        }

        DingTalkConfig dingTalkConfig = new DingTalkConfig();
        dingTalkConfig.setConfigId(tokenId);
        dingTalkConfig.setSign(sign);
        dingTalkConfig.setTokenId(tokenId);
        OaFactory.createAndRegisterOaSender(dingTalkConfig);
        oaSender = OaFactory.getSmsOaBlend(tokenId);
    }

    /**
     * 发送文本消息
     *
     * @param content 内容
     * @param noticeAll 是否通知所有人
     * @param phones 通知的用户手机号，英文逗号分割
     * @return 发送成功的回执信息
     * @author xuyuxiang
     * @date 2022/2/23 14:24
     */
    public static String pushDingTalkText(String content, boolean noticeAll, String phones) {
        try {
            initClient();
            Request request = new Request();
            request.setContent(content);
            if(noticeAll) {
                request.setIsNoticeAll(true);
            } else {
                if(ObjectUtil.isNotEmpty(phones)) {
                    request.setPhoneList((StrUtil.split(phones, StrUtil.COMMA)));
                }
            }
            Response response = oaSender.sender(request, MessageType.DING_TALK_TEXT);
            if(!response.isSuccess()) {
                throw new CommonException("消息推送错误，返回内容：{}", JSONUtil.toJsonStr(response.getData()));
            }
            return JSONUtil.toJsonStr(response.getData());
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 发送MarkDown消息
     *
     * @param title 标题
     * @param content 内容
     * @param noticeAll 是否通知所有人
     * @return 发送成功的回执信息
     * @author xuyuxiang
     * @date 2022/2/23 14:24
     */
    public static String pushDingTalkMarkdown(String title, String content, boolean noticeAll) {
        try {
            initClient();
            Request request = new Request();
            request.setTitle(title);
            request.setContent(content);
            if(noticeAll) {
                request.setIsNoticeAll(true);
            }
            Response response = oaSender.sender(request, MessageType.DING_TALK_MARKDOWN);
            if(!response.isSuccess()) {
                throw new CommonException("消息推送错误，返回内容：{}", JSONUtil.toJsonStr(response.getData()));
            }
            return JSONUtil.toJsonStr(response.getData());
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 发送链接消息
     *
     * @param title 标题
     * @param content 内容
     * @param picUrl 封面图片地址
     * @param messageUrl 消息链接地址
     * @return 发送成功的回执信息
     * @author xuyuxiang
     * @date 2022/2/23 14:24
     */
    public static String pushDingTalkLink(String title, String content, String picUrl,String messageUrl) {
        try {
            initClient();
            Request request = new Request();
            request.setTitle(title);
            request.setContent(content);
            request.setPicUrl(picUrl);
            request.setMessageUrl(messageUrl);
            Response response = oaSender.sender(request, MessageType.DING_TALK_LINK);
            if(!response.isSuccess()) {
                throw new CommonException("消息推送错误，返回内容：{}", JSONUtil.toJsonStr(response.getData()));
            }
            return JSONUtil.toJsonStr(response.getData());
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }
}
