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
package vip.xiaonuo.auth.core.protocol.jwt.common;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.oauth2.consts.SaOAuth2Consts;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import com.alibaba.fastjson.JSONObject;
import me.zhyd.oauth.enums.AuthResponseStatus;
import me.zhyd.oauth.exception.AuthException;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.utils.AuthStateUtils;
import vip.xiaonuo.auth.core.protocol.base.AuthBaseRequest;
import vip.xiaonuo.auth.core.protocol.jwt.AuthJwtBaseJson;
import vip.xiaonuo.auth.core.util.AuthTokenUtil;
import vip.xiaonuo.common.cache.CommonCacheOperator;
import vip.xiaonuo.common.exception.CommonException;

import java.util.Map;

import static vip.xiaonuo.auth.core.protocol.base.AuthBaseClient.CONFIG_CACHE_KEY;

/**
 * CAS认证源通用请求
 *
 * @author xuyuxiang
 * @date 2025/1/24 15:09
 **/
public record AuthJwtCommonRequest(AuthJwtBaseJson authJwtBaseJson) implements AuthBaseRequest {

    @Override
    public String authorize(String clientType) {
        String authorizeUrl = authJwtBaseJson.getAuthorizeUrl();
        String callbackUrl = authJwtBaseJson.getCallbackUrl();
        // 获取状态
        String state = AuthStateUtils.createState();
        // 获取缓存操作类
        CommonCacheOperator commonCacheOperator = SpringUtil.getBean(CommonCacheOperator.class);
        // 放入缓存
        commonCacheOperator.put(CONFIG_CACHE_KEY + state, JSONUtil.createObj().set("clientType", clientType), 300);
        // 追加state
        callbackUrl = SaFoxUtil.joinParam(callbackUrl, SaOAuth2Consts.Param.state, state);
        // 追加client_id
        authorizeUrl = SaFoxUtil.joinParam(authorizeUrl, SaOAuth2Consts.Param.client_id, authJwtBaseJson.getClientId());
        // 追加redirect_uri
        authorizeUrl = SaFoxUtil.joinParam(authorizeUrl, SaOAuth2Consts.Param.redirect_uri, URLUtil.encodeAll(callbackUrl));
        // 返回授权地址
        return authorizeUrl;
    }

    @Override
    public AuthResponse<AuthUser> login() {
        AuthResponse<AuthUser> authResponse = new AuthResponse<>();
        Map<String, String> paramMap = SaHolder.getRequest().getParamMap();
        String token = paramMap.get(SaOAuth2Consts.Param.token);
        if(ObjectUtil.isEmpty(token)) {
            throw new CommonException("token不能为空");
        }
        String sub = checkResponse(token);
        authResponse.setCode(AuthResponseStatus.SUCCESS.getCode());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(authJwtBaseJson.getSourceProperty(), sub);
        authResponse.setData(AuthUser.builder().rawUserInfo(jsonObject).uuid(sub).build());
        return authResponse;
    }

    private String checkResponse(String token) {
        boolean verify = AuthTokenUtil.verifyToken(authJwtBaseJson.getAlgorithm(), authJwtBaseJson.getPublicKey(), token);
        if(!verify) {
            throw new AuthException(AuthResponseStatus.ILLEGAL_TOKEN);
        }
        JWTPayload payload = JWTUtil.parseToken(token).getPayload();
        Object claim = payload.getClaim(authJwtBaseJson.getSourceProperty());
        if(ObjectUtil.isEmpty(claim)) {
            throw new CommonException("不存在属性：" + authJwtBaseJson.getSourceProperty());
        }
        return claim.toString();
    }
}
