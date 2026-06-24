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
package vip.xiaonuo.auth.core.protocol.oidc.impl.welink;

import cn.dev33.satoken.oauth2.consts.SaOAuth2Consts;
import com.alibaba.fastjson.JSONObject;
import com.xkcoding.http.support.HttpHeader;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.enums.AuthUserGender;
import me.zhyd.oauth.exception.AuthException;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthDefaultRequest;
import me.zhyd.oauth.utils.HttpUtils;
import me.zhyd.oauth.utils.UrlBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * OIDC认证源WeLink请求
 *
 * @author xuyuxiang
 * @date 2025/1/24 15:09
 **/
public class AuthOidcWeLinkRequest extends AuthDefaultRequest {

    public AuthOidcWeLinkRequest(AuthConfig config) {
        super(config, new AuthOidcWeLinkSource());
    }

    @Override
    public AuthToken getAccessToken(AuthCallback authCallback) {
        String body = super.doPostAuthorizationCode(authCallback.getCode());
        JSONObject object = JSONObject.parseObject(body);
        this.checkResponse(object);
        return AuthToken.builder()
                .accessToken(object.getString("access_token"))
                .expireIn(object.getIntValue("expires_in"))
                .build();
    }

    @Override
    public AuthUser getUserInfo(AuthToken authToken) {
        String userIdBody = new HttpUtils(config.getHttpConfig()).get(userInfoUrl(authToken), null,
                new HttpHeader().add("x-wlk-Authorization", authToken.getAccessToken()), false).getBody();
        JSONObject userIdJSONObject= JSONObject.parseObject(userIdBody);
        this.checkResponse(userIdJSONObject);
        String userId = userIdJSONObject.getString("userId");
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", userId);
        String jsonString = JSONObject.toJSONString(paramMap);
        String userBody = new HttpUtils(config.getHttpConfig()).post(UrlBuilder
                .fromBaseUrl("https://open.welink.huaweicloud.com/api/contact/v2/user/detail")
                .build(), jsonString, new HttpHeader().add("x-wlk-Authorization",
                authToken.getAccessToken())).getBody();
        JSONObject userBodyJSONObject = JSONObject.parseObject(userBody);
        this.checkResponse(userBodyJSONObject);
        return AuthUser.builder()
                .uuid(userBodyJSONObject.getString("userId"))
                .username(userBodyJSONObject.getString("userNameCn"))
                .nickname(userBodyJSONObject.getString("userNameCn"))
                .avatar(userBodyJSONObject.getString("avatar"))
                .gender(AuthUserGender.UNKNOWN)
                .token(authToken)
                .source(source.toString())
                .build();
    }

    private void checkResponse(JSONObject object) {
        if (object.containsKey("errorCode")) {
            throw new AuthException(object.getString("errorMessage"));
        }
        if (object.containsKey("code") && !object.getString("code").equals("0") &&
                !object.getString("code").equals("60001")) {
            throw new AuthException(object.getString("message"));
        }
    }

    /**
     * 返回带{@code state}参数的授权url，授权回调时会带上这个{@code state}
     *
     * @param state state 验证授权流程的参数，可以防止csrf
     * @return 返回授权地址
     * @since 1.11.0
     */
    @Override
    public String authorize(String state) {
        return UrlBuilder.fromBaseUrl(super.authorize(state))
                .queryParam(SaOAuth2Consts.Param.scope, "backendlogin")
                .build();
    }
}
