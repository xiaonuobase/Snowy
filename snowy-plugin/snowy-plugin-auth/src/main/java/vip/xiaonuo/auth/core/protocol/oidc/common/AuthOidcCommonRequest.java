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
package vip.xiaonuo.auth.core.protocol.oidc.common;

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
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.enums.AuthResponseStatus;
import me.zhyd.oauth.exception.AuthException;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthDefaultRequest;
import me.zhyd.oauth.utils.HttpUtils;
import me.zhyd.oauth.utils.UrlBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import vip.xiaonuo.auth.core.protocol.oidc.AuthOidcBaseJson;
import vip.xiaonuo.auth.core.util.AuthTokenUtil;

import java.security.Security;
import java.util.List;

/**
 * OIDC认证源通用请求
 *
 * @author xuyuxiang
 * @date 2025/1/24 15:09
 **/
@Slf4j
@Getter
public class AuthOidcCommonRequest extends AuthDefaultRequest {

    private final AuthOidcBaseJson authOidcBaseJson;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public AuthOidcCommonRequest(AuthConfig config, AuthOidcBaseJson authOidcBaseJson) {
        super(config, new AuthOidcCommonSource(authOidcBaseJson.getAuthorizeUrl(),
                authOidcBaseJson.getAccessTokenUrl(),
                authOidcBaseJson.getUserInfoUrl()));
        this.authOidcBaseJson = authOidcBaseJson;
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
        
        this.checkResponse(bodyJsonObject);
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

            // GET/POST降级处理：优先GET，失败后降级POST
            userInfo = fetchUserInfoWithFallback(httpConfig, header);
        } else {
            userInfo = (new HttpUtils(httpConfig)).get(this.userInfoUrl(authToken)).getBody();
        }

        return parseUserInfo(userInfo, authToken);
    }

    /**
     * 获取用户信息（支持GET/POST降级）
     */
    private String fetchUserInfoWithFallback(HttpConfig httpConfig, HttpHeader header) {
        HttpUtils httpUtils = new HttpUtils(httpConfig);
        String userInfoUrl = this.source.userInfo();

        // 优先尝试GET
        try {
            String response = httpUtils.get(userInfoUrl, null, header, false).getBody();
            log.info(">>> OIDC使用GET方法获取用户信息成功");
            return response;
        } catch (Exception e) {
            log.warn(">>> OIDC使用GET方法获取用户信息失败，尝试POST方法: {}", e.getMessage());
        }

        // GET失败，降级POST
        try {
            String response = httpUtils.post(userInfoUrl, null, header, false).getBody();
            log.info(">>> OIDC使用POST方法获取用户信息成功");
            return response;
        } catch (Exception ex) {
            log.error(">>> OIDC使用GET和POST方法均获取用户信息失败", ex);
            throw new AuthException(AuthResponseStatus.FAILURE);
        }
    }

    /**
     * 解析用户信息
     */
    private AuthUser parseUserInfo(String userInfo, AuthToken authToken) {
        JSONObject bodyJsonObject = JSONUtil.parseObj(userInfo);

        log.info(">>> OIDC用户信息原始响应: {}", userInfo);
        log.info(">>> OIDC配置的sourceProperty: {}", authOidcBaseJson.getSourceProperty());

        // 有条件的data解包：只有顶层没有目标字段且有data包装时才解包
        if(!bodyJsonObject.containsKey(authOidcBaseJson.getSourceProperty()) && bodyJsonObject.containsKey("data")) {
            Object data = bodyJsonObject.get("data");
            if(ObjectUtil.isEmpty(data)) {
                log.error(">>> OIDC响应包含data节点但值为空");
                throw new AuthException(AuthResponseStatus.FAILURE);
            }
            bodyJsonObject = JSONUtil.parseObj(data);
            log.info(">>> OIDC检测到响应需要解包data节点");
        }

        String uuidValue = bodyJsonObject.getStr(authOidcBaseJson.getSourceProperty());

        // uuid空值校验
        if(StrUtil.isBlank(uuidValue)) {
            log.error(">>> OIDC无法从响应中提取uuid，sourceProperty={}, 响应={}",
                      authOidcBaseJson.getSourceProperty(), userInfo);
            throw new AuthException(AuthResponseStatus.FAILURE);
        }

        log.info(">>> OIDC成功提取uuid: {}", uuidValue);

        return AuthUser.builder()
                .rawUserInfo(com.alibaba.fastjson.JSONObject.parseObject(bodyJsonObject.toString()))
                .uuid(uuidValue)
                .username(uuidValue)
                .token(authToken)
                .source(this.source.toString()).build();
    }

    @Override
    public String authorize(String state) {
        String scope = authOidcBaseJson.getScope();
        if(ObjectUtil.isNotEmpty(scope)) {
            List<String> scopeList = StrUtil.split(scope, StrUtil.COMMA);
            return UrlBuilder.fromBaseUrl(super.authorize(state))
                    .queryParam(SaOAuth2Consts.Param.scope, super.getScopes(" ", true, scopeList))
                    .build();
        } else {
            return UrlBuilder.fromBaseUrl(super.authorize(state)).build();
        }
    }

    private void checkResponse(JSONObject response) {
        String idToken = response.getStr(SaOAuth2Consts.ExtraField.id_token);
        if(ObjectUtil.isAllNotEmpty(idToken, authOidcBaseJson.getPublicKey(), authOidcBaseJson.getAlgorithm())) {
            boolean verify = AuthTokenUtil.verifyToken(authOidcBaseJson.getAlgorithm(), authOidcBaseJson.getPublicKey(), idToken);
            if(!verify) {
                throw new AuthException(AuthResponseStatus.ILLEGAL_TOKEN);
            }
        }
    }
}
