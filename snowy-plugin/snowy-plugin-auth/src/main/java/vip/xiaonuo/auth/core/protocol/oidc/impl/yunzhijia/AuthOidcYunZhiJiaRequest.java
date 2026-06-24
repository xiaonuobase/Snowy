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
package vip.xiaonuo.auth.core.protocol.oidc.impl.yunzhijia;

import cn.dev33.satoken.util.SaFoxUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.enums.AuthUserGender;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthDefaultRequest;
import me.zhyd.oauth.utils.UrlBuilder;
import vip.xiaonuo.common.exception.CommonException;

/**
 * OIDC认证源云之家请求
 *
 * @author xuyuxiang
 * @date 2025/1/24 15:09
 **/
public class AuthOidcYunZhiJiaRequest extends AuthDefaultRequest {

    public AuthOidcYunZhiJiaRequest(AuthConfig config) {
        super(config, new AuthOidcYunZhiJiaSource());
    }

    @Override
    public AuthToken getAccessToken(AuthCallback authCallback) {
        JSONObject accessTokenJsonObject = this.getAccessToken("app");
        return AuthToken.builder()
                .accessToken(accessTokenJsonObject.getStr("accessToken"))
                .refreshToken(accessTokenJsonObject.getStr("refreshToken"))
                .expireIn(accessTokenJsonObject.getInt("expireIn"))
                .code(authCallback.getCode())
                .build();
    }

    @Override
    public AuthUser getUserInfo(AuthToken authToken) {
        JSONObject param = JSONUtil.createObj();
        param.set("appid", this.config.getClientId());
        param.set("ticket", authToken.getCode());
        Object userBody = this.doExecute(SaFoxUtil.joinParam(this.source.userInfo(), "accessToken", authToken.getAccessToken()),
                param, false, null);
        com.alibaba.fastjson.JSONObject userBodyJSONObject = com.alibaba.fastjson.JSONObject.parseObject(JSONUtil.toJsonStr(userBody));
        return AuthUser.builder()
                .uuid(userBodyJSONObject.getString("userid"))
                .username(userBodyJSONObject.getString("username"))
                .nickname(userBodyJSONObject.getString("username"))
                .rawUserInfo(userBodyJSONObject)
                .gender(AuthUserGender.UNKNOWN)
                .token(authToken)
                .source(source.toString())
                .build();
    }

    public JSONObject getAccessToken(String scopeType) {
        JSONObject param = JSONUtil.createObj();
        if(scopeType.equals("app")) {
            param.set("appId", this.config.getClientId());
            param.set("secret", this.config.getClientSecret());
        } else {
            throw new CommonException("错误的云之家授权类型，{}", scopeType);
        }
        param.set("scope", scopeType);
        param.set("timestamp", DateUtil.current());
        Object o = this.doExecute(this.source.accessToken(), param, false, null);
        return JSONUtil.parseObj(o);
    }

    public Object doExecute(String url, JSONObject param, Boolean needAccessToken, String scopeType) {
        JSONObject result;
        try {
            if(needAccessToken) {
                url = SaFoxUtil.joinParam(url, "accessToken", this.getAccessToken(scopeType));
            }
            String post = HttpUtil.post(url, param);
            result = JSONUtil.parseObj(post);
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
        Boolean success = result.getBool("success");
        if(!success) {
            throw new CommonException(result.getStr("error"));
        }
        return result.getObj("data");
    }

    @Override
    public String authorize(String state) {
        return UrlBuilder.fromBaseUrl(this.source.authorize())
                .queryParam("appid", this.config.getClientId())
                .queryParam("redirect_uri", this.config.getRedirectUri())
                .queryParam("state", this.getRealState(state)).build();
    }
}
