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
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.dromara.oa.api.OaSender;
import org.dromara.oa.comm.entity.Request;
import org.dromara.oa.comm.entity.Response;
import org.dromara.oa.comm.enums.MessageType;
import org.dromara.oa.core.byteTalk.config.ByteTalkConfig;
import org.dromara.oa.core.byteTalk.config.ByteTalkFactory;
import org.dromara.oa.core.provider.factory.OaFactory;
import org.dromara.oa.core.provider.factory.ProviderFactoryHolder;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.dev.api.DevConfigApi;

/**
 * 飞书消息推送工具类
 *
 * @author xuyuxiang
 * @date 2022/1/2 17:05
 */
@Slf4j
public class DevPushFeiShuUtil {

    private static OaSender oaSender;

    private static final String SNOWY_PUSH_FEISHU_TOKEN_ID_KEY = "SNOWY_PUSH_FEISHU_TOKEN_ID";

    static {
        ProviderFactoryHolder.registerFactory(ByteTalkFactory.instance());
    }

    /**
     * 初始化操作的客户端
     *
     * @author xuyuxiang
     * @date 2022/1/5 23:24
     */
    private static void initClient() {

        DevConfigApi devConfigApi = SpringUtil.getBean(DevConfigApi.class);

        /* tokenId */
        String tokenId = devConfigApi.getValueByKey(SNOWY_PUSH_FEISHU_TOKEN_ID_KEY);

        if(ObjectUtil.isEmpty(tokenId)) {
            throw new CommonException("飞书推送操作客户端未正确配置：tokenId为空");
        }

        ByteTalkConfig byteTalkConfig = new ByteTalkConfig();
        byteTalkConfig.setConfigId(tokenId);
        byteTalkConfig.setTokenId(tokenId);
        OaFactory.createAndRegisterOaSender(byteTalkConfig);
        oaSender = OaFactory.getSmsOaBlend(tokenId);
    }

    /**
     * 发送文本消息
     *
     * @param content 内容
     * @param noticeAll 是否通知所有人
     * @return 发送成功的回执信息
     * @author xuyuxiang
     * @date 2022/2/23 14:24
     */
    public static String pushFeiShuText(String content, boolean noticeAll) {
        try {
            initClient();
            Request request = new Request();
            request.setContent(content);
            if(noticeAll) {
                request.setIsNoticeAll(true);
            }
            Response response = oaSender.sender(request, MessageType.BYTE_TALK_TEXT);
            if(!response.isSuccess()) {
                throw new CommonException("消息推送错误，返回内容：{}", JSONUtil.toJsonStr(response.getData()));
            }
            return JSONUtil.toJsonStr(response.getData());
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }
}
