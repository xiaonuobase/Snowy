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
package vip.xiaonuo.auth.core.protocol.jwt;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import vip.xiaonuo.auth.core.protocol.base.AuthBaseClient;
import vip.xiaonuo.auth.core.protocol.base.AuthBaseRequest;
import vip.xiaonuo.auth.core.protocol.jwt.common.AuthJwtCommonClient;
import vip.xiaonuo.common.exception.CommonException;

/**
 * JWT认证源客户端
 *
 * @author xuyuxiang
 * @date 2024/11/8 15:41
 **/
@Slf4j
public class AuthJwtClient extends AuthBaseClient<AuthJwtBaseJson> {

    public AuthJwtClient(JSONObject authSource, String authPlatform) {
        super(authSource, authPlatform);
    }

    @Override
    public AuthJwtBaseJson getAuthBaseJson() {
        return JSONUtil.toBean(this.getAuthSource(), AuthJwtBaseJson.class);
    }

    @Override
    public void valid() {
        AuthJwtBaseJson authSourceJwtBaseJson = this.getAuthBaseJson();
        if(StrUtil.isBlank(authSourceJwtBaseJson.getClientId())) {
            throw new CommonException("ClientId不能为空");
        }
        if(StrUtil.isBlank(authSourceJwtBaseJson.getClientSecret())) {
            throw new CommonException("ClientSecret不能为空");
        }
        if(StrUtil.isBlank(authSourceJwtBaseJson.getAuthorizeUrl())) {
            throw new CommonException("认证授权url不能为空");
        }
        if(StrUtil.isBlank(authSourceJwtBaseJson.getCallbackUrl())) {
            throw new CommonException("回调地址不能为空");
        }
        if(StrUtil.isBlank(authSourceJwtBaseJson.getPublicKey())) {
            throw new CommonException("公钥不能为空");
        }
        if(StrUtil.isBlank(authSourceJwtBaseJson.getAlgorithm())) {
            throw new CommonException("签名算法不能为空");
        }
        if(StrUtil.isBlank(authSourceJwtBaseJson.getSourceProperty())) {
            throw new CommonException("关联源属性不能为空");
        }
        if(StrUtil.isBlank(authSourceJwtBaseJson.getTargetProperty())) {
            throw new CommonException("关联目标属性不能为空");
        }
    }

    @Override
    public String getAuthorizeUrl(String clientType) {
        // 校验认证源配置
        this.valid();
        // 获取客户端
        AuthBaseRequest authRequest = new AuthJwtCommonClient(getAuthBaseJson()).getAuthRequest();
        // 执行认证
        return authRequest.authorize(clientType);
    }

    @Override
    public AuthResponse<AuthUser> doLogin(AuthCallback authCallback) {
        AuthBaseRequest authRequest = new AuthJwtCommonClient(getAuthBaseJson()).getAuthRequest();
        AuthResponse<AuthUser> authResponse = authRequest.login();
        if(!authResponse.ok()) {
            throw new CommonException(authResponse.getMsg());
        }
        return handleAuthResponse(authResponse);
    }
}
