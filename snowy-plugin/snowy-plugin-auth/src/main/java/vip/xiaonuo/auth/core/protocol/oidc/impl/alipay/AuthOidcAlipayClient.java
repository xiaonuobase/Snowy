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
package vip.xiaonuo.auth.core.protocol.oidc.impl.alipay;

import com.xkcoding.http.HttpUtil;
import com.xkcoding.http.support.hutool.HutoolImpl;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.request.AuthAlipayRequest;
import me.zhyd.oauth.request.AuthRequest;
import vip.xiaonuo.auth.core.protocol.oidc.AuthOidcBaseJson;

/**
 * OIDC认证源支付宝客户端
 *
 * @author xuyuxiang
 * @date 2024/11/8 15:41
 **/
@Slf4j
public record AuthOidcAlipayClient(AuthOidcBaseJson authOidcBaseJson) {

    public AuthRequest getAuthRequest() {
        return getAuthRequest(false);
    }

    public AuthRequest getAuthRequest(boolean ignoreCheckState) {
        String clientId = authOidcBaseJson.getClientId();
        String clientSecret = authOidcBaseJson.getClientSecret();
        String callbackUrl = authOidcBaseJson.getCallbackUrl();
        String publicKey = authOidcBaseJson.getPublicKey();
        HttpUtil.setHttp(new HutoolImpl());
        return new AuthAlipayRequest(AuthConfig.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .redirectUri(callbackUrl)
                .ignoreCheckState(ignoreCheckState)
                .build(), publicKey);
    }
}
