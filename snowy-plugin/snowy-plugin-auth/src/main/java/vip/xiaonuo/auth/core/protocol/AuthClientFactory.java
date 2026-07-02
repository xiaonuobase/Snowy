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
package vip.xiaonuo.auth.core.protocol;


import cn.hutool.json.JSONObject;
import vip.xiaonuo.auth.core.enums.AuthPlatformEnum;
import vip.xiaonuo.auth.core.protocol.base.AuthBaseClient;
import vip.xiaonuo.auth.core.protocol.cas.AuthCasClient;
import vip.xiaonuo.auth.core.protocol.jwt.AuthJwtClient;
import vip.xiaonuo.auth.core.protocol.oauth.AuthOauthClient;
import vip.xiaonuo.auth.core.protocol.oidc.AuthOidcClient;
import vip.xiaonuo.auth.core.protocol.saml.AuthSamlClient;

/**
 * 认证源客户端工厂类
 *
 * @author xuyuxiang
 * @date 2024/11/19 15:00
 **/
public class AuthClientFactory {

    /**
     * 创建认证源客户端
     *
     * @author xuyuxiang
     * @date 2024/11/19 15:02
     **/
    public static AuthBaseClient<?> createClient(String authPlatform, JSONObject authSource) {
        AuthPlatformEnum authPlatformEnum = AuthPlatformEnum.valueOf(authPlatform);
        return switch (authPlatformEnum) {
            case OAUTH -> new AuthOauthClient(authSource, authPlatform);
            case JWT -> new AuthJwtClient(authSource, authPlatform);
            case CAS -> new AuthCasClient(authSource, authPlatform);
            case SAML -> new AuthSamlClient(authSource, authPlatform);
            case OIDC, IAM, DINGTALK, WORKWECHAT, FEISHU, WELINK, YUNZHIJIA, QQ, WECHAT, WECHAT_MINI, WEIBO, DOUYIN, ALIPAY -> new AuthOidcClient(authSource, authPlatform);
        };
    }
}
