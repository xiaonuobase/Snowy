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
package vip.xiaonuo.auth.modular.third.request;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.enums.AuthUserGender;
import me.zhyd.oauth.enums.scope.AuthGiteeScope;
import me.zhyd.oauth.exception.AuthException;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthDefaultRequest;
import me.zhyd.oauth.utils.AuthScopeUtils;
import me.zhyd.oauth.utils.UrlBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.Map;

/**
 * 山信通认证源通用请求
 *
 * @author xuyuxiang
 * @date 2025/1/24 15:09
 **/
@Getter
public class AuthThirdIamRequest extends AuthDefaultRequest {

    private final Map<String, String> authSourceOidcBaseJson;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public AuthThirdIamRequest(AuthConfig config, Map<String, String> authSourceOidcBaseJson) {
        super(config, new AuthThirdIamCommonSource(authSourceOidcBaseJson.get("authorizeUrl"),
                authSourceOidcBaseJson.get("accessTokenUrl"),
                authSourceOidcBaseJson.get("userInfoUrl")));
        this.authSourceOidcBaseJson = authSourceOidcBaseJson;
    }

    @Override
    public AuthToken getAccessToken(AuthCallback authCallback) {
        String response = this.doPostAuthorizationCode(authCallback.getCode());
        com.alibaba.fastjson.JSONObject accessTokenObject = com.alibaba.fastjson.JSONObject.parseObject(response);
        this.checkResponse(accessTokenObject);
        return AuthToken.builder()
                .accessToken(accessTokenObject.getString("access_token"))
                .refreshToken(accessTokenObject.getString("refresh_token"))
                .scope(accessTokenObject.getString("scope"))
                .tokenType(accessTokenObject.getString("token_type"))
                .expireIn(accessTokenObject.getIntValue("expires_in"))
                .build();
    }

    @Override
    public AuthUser getUserInfo(AuthToken authToken) {
        String userInfo = this.doGetUserInfo(authToken);
        com.alibaba.fastjson.JSONObject userInfoObject = com.alibaba.fastjson.JSONObject.parseObject(userInfo);
        this.checkResponse(userInfoObject);
        return AuthUser.builder().rawUserInfo(userInfoObject)
                .uuid(userInfoObject.getString("sub"))
                .nickname(userInfoObject.getString("name"))
                .username(userInfoObject.getString("account"))
                .avatar(userInfoObject.getString("picture"))
                .email(userInfoObject.getString("email"))
                .gender(AuthUserGender.UNKNOWN)
                .token(authToken)
                .source(this.source.toString()).build();
    }

    private void checkResponse(JSONObject object) {
        if (object.getIntValue("code") != 200) {
            throw new AuthException(object.getString("msg"));
        }
    }

    @Override
    public String authorize(String state) {
        return UrlBuilder.fromBaseUrl(super.authorize(state)).queryParam("scope", this.getScopes(" ", true, AuthScopeUtils.getDefaultScopes(AuthGiteeScope.values()))).build();
    }
}
