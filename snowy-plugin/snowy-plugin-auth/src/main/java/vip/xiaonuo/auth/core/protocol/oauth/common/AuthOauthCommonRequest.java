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
package vip.xiaonuo.auth.core.protocol.oauth.common;

import cn.dev33.satoken.oauth2.consts.GrantType;
import cn.dev33.satoken.oauth2.consts.SaOAuth2Consts;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xkcoding.http.config.HttpConfig;
import com.xkcoding.http.support.HttpHeader;
import lombok.Getter;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.enums.AuthResponseStatus;
import me.zhyd.oauth.exception.AuthException;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthDefaultRequest;
import me.zhyd.oauth.utils.HttpUtils;
import me.zhyd.oauth.utils.UrlBuilder;
import vip.xiaonuo.auth.core.protocol.oauth.AuthOauthBaseJson;

import java.util.List;

/**
 * Oauth认证源通用请求
 *
 * @author xuyuxiang
 * @date 2025/1/24 15:09
 **/
@Getter
public class AuthOauthCommonRequest extends AuthDefaultRequest {

    private final AuthOauthBaseJson authOauthBaseJson;

    public AuthOauthCommonRequest(AuthConfig config, AuthOauthBaseJson authOauthBaseJson) {
        super(config, new AuthOauthCommonSource(authOauthBaseJson.getAuthorizeUrl(),
                authOauthBaseJson.getAccessTokenUrl(),
                authOauthBaseJson.getUserInfoUrl()));
        this.authOauthBaseJson = authOauthBaseJson;
    }

    @Override
    public AuthToken getAccessToken(AuthCallback authCallback) {
        JSONObject jsonObject = JSONUtil.createObj();
        jsonObject.set( SaOAuth2Consts.Param.client_id, this.config.getClientId());
        jsonObject.set( SaOAuth2Consts.Param.client_secret, this.config.getClientSecret());
        jsonObject.set( SaOAuth2Consts.Param.grant_type, GrantType.authorization_code);
        jsonObject.set( SaOAuth2Consts.Param.redirect_uri, this.config.getRedirectUri());
        jsonObject.set( SaOAuth2Consts.Param.code, authCallback.getCode());
        String body = HttpUtil.post(this.source.accessToken(), jsonObject, 5000);
        JSONObject bodyJsonObject = JSONUtil.parseObj(body);
        String accessTokenKey = SaOAuth2Consts.Param.access_token;
        String accessTokenCamelKey = StrUtil.toCamelCase(accessTokenKey);

        if(!bodyJsonObject.containsKey(accessTokenKey) &&
                !bodyJsonObject.containsKey(accessTokenCamelKey) && bodyJsonObject.containsKey("data")) {
            Object data = bodyJsonObject.get("data");
            if(ObjectUtil.isEmpty(data)) {
                throw new AuthException(AuthResponseStatus.FAILURE);
            }
            bodyJsonObject = JSONUtil.parseObj(data);
        }

        String accessToken = bodyJsonObject.getStr(accessTokenKey);
        if(ObjectUtil.isEmpty(accessToken)) {
            accessToken = bodyJsonObject.getStr(accessTokenCamelKey);
        }
        
        if(ObjectUtil.isEmpty(accessToken)) {
            throw new AuthException(AuthResponseStatus.FAILURE);
        }
        return AuthToken.builder()
                .accessToken(accessToken)
                .refreshToken(bodyJsonObject.getStr(SaOAuth2Consts.Param.refresh_token))
                .idToken(bodyJsonObject.getStr(SaOAuth2Consts.ExtraField.id_token))
                .tokenType(bodyJsonObject.getStr("token_type"))
                .scope(bodyJsonObject.getStr(SaOAuth2Consts.Param.scope))
                .build();
    }

    @Override
    public AuthUser getUserInfo(AuthToken authToken) {
        HttpConfig httpConfig = HttpConfig.builder().timeout(5000).build();
        String tokenType = authToken.getTokenType();
        String userInfo;
        if(ObjectUtil.isNotEmpty(tokenType) && tokenType.equals(SaOAuth2Consts.TokenType.Bearer)) {
            HttpHeader header = (new HttpHeader()).add(SaOAuth2Consts.Param.Authorization,
                    SaOAuth2Consts.TokenType.Bearer + " " + authToken.getAccessToken());
            userInfo = (new HttpUtils(httpConfig))
                    .get(this.source.userInfo(), null, header, false).getBody();
        } else {
            userInfo = (new HttpUtils(httpConfig)).get(this.userInfoUrl(authToken)).getBody();
        }
        JSONObject bodyJsonObject = JSONUtil.parseObj(userInfo);
        if(!bodyJsonObject.containsKey(authOauthBaseJson.getSourceProperty()) && bodyJsonObject.containsKey("data")) {
            Object data = bodyJsonObject.get("data");
            if(ObjectUtil.isEmpty(data)) {
                throw new AuthException(AuthResponseStatus.FAILURE);
            }
            bodyJsonObject = JSONUtil.parseObj(data);
        }
        return AuthUser.builder()
                .rawUserInfo(com.alibaba.fastjson.JSONObject.parseObject(bodyJsonObject.toString()))
                .uuid(bodyJsonObject.getStr(authOauthBaseJson.getSourceProperty()))
                .token(authToken)
                .source(this.source.toString()).build();
    }

    @Override
    public String authorize(String state) {
        String scope = authOauthBaseJson.getScope();
        if(ObjectUtil.isNotEmpty(scope)) {
            List<String> scopeList = StrUtil.split(scope, StrUtil.COMMA);
            return UrlBuilder.fromBaseUrl(super.authorize(state))
                    .queryParam(SaOAuth2Consts.Param.scope, super.getScopes(" ", true, scopeList))
                    .build();
        } else {
            return UrlBuilder.fromBaseUrl(super.authorize(state)).build();
        }
    }
}
