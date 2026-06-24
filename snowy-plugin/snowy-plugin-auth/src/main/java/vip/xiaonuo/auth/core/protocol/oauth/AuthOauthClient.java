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
package vip.xiaonuo.auth.core.protocol.oauth;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import vip.xiaonuo.auth.core.protocol.base.AuthBaseClient;
import vip.xiaonuo.auth.core.protocol.oauth.common.AuthOauthCommonClient;
import vip.xiaonuo.common.cache.CommonCacheOperator;
import vip.xiaonuo.common.exception.CommonException;

/**
 * Oauth认证源客户端
 *
 * @author xuyuxiang
 * @date 2024/11/8 15:41
 **/
@Slf4j
public class AuthOauthClient extends AuthBaseClient<AuthOauthBaseJson> {

    public AuthOauthClient(JSONObject authSource, String authPlatform) {
        super(authSource, authPlatform);
    }

    @Override
    public AuthOauthBaseJson getAuthBaseJson() {
        return JSONUtil.toBean(this.getAuthSource(), AuthOauthBaseJson.class);
    }

    @Override
    public void valid() {
        AuthOauthBaseJson authSourceOauthBaseJson = this.getAuthBaseJson();
        if(StrUtil.isBlank(authSourceOauthBaseJson.getClientId())) {
            throw new CommonException("ClientId不能为空");
        }
        if(StrUtil.isBlank(authSourceOauthBaseJson.getClientSecret())) {
            throw new CommonException("ClientSecret不能为空");
        }
        if(StrUtil.isBlank(authSourceOauthBaseJson.getAuthorizeUrl())) {
            throw new CommonException("认证授权url不能为空");
        }
        if(StrUtil.isBlank(authSourceOauthBaseJson.getAccessTokenUrl())) {
            throw new CommonException("获取accessToken的Url不能为空");
        }
        if(StrUtil.isBlank(authSourceOauthBaseJson.getUserInfoUrl())) {
            throw new CommonException("获取用户信息的url不能为空");
        }
        if(StrUtil.isBlank(authSourceOauthBaseJson.getCallbackUrl())) {
            throw new CommonException("回调地址不能为空");
        }
        if(StrUtil.isBlank(authSourceOauthBaseJson.getSourceProperty())) {
            throw new CommonException("关联源属性不能为空");
        }
        if(StrUtil.isBlank(authSourceOauthBaseJson.getTargetProperty())) {
            throw new CommonException("关联目标属性不能为空");
        }
    }

    @Override
    public String getAuthorizeUrl(String clientType) {
        // 校验认证源配置
        this.valid();
        // 获取客户端
        AuthRequest authRequest = new AuthOauthCommonClient(getAuthBaseJson()).getAuthRequest();
        // 获取状态
        String state = AuthStateUtils.createState();
        // 获取缓存操作类
        CommonCacheOperator commonCacheOperator = SpringUtil.getBean(CommonCacheOperator.class);
        // 放入缓存
        commonCacheOperator.put(CONFIG_CACHE_KEY + state, JSONUtil.createObj().set("clientType", clientType), 300);
        // 执行认证
        return authRequest.authorize(state);
    }

    @Override
    public AuthResponse<AuthUser> doLogin() {
        SaRequest request = SaHolder.getRequest();
        String code = request.getParam("code");
        if(ObjectUtil.isEmpty(code)) {
            throw new CommonException("code不能为空");
        }
        String state = request.getParam("state");
        if(ObjectUtil.isEmpty(state)) {
            throw new CommonException("state不能为空");
        }
        AuthRequest authRequest = new AuthOauthCommonClient(getAuthBaseJson()).getAuthRequest();
        AuthResponse<AuthUser> authResponse = authRequest.login(AuthCallback.builder().code(code).state(state).build());
        if(!authResponse.ok()) {
            throw new CommonException(authResponse.getMsg());
        }
        return handleAuthResponse(authResponse);
    }
}
