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
package vip.xiaonuo.auth.core.protocol.oidc;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.exception.AuthException;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import vip.xiaonuo.auth.core.enums.AuthPlatformEnum;
import vip.xiaonuo.auth.core.protocol.base.AuthBaseClient;
import vip.xiaonuo.auth.core.protocol.oidc.common.AuthOidcCommonClient;
import vip.xiaonuo.auth.core.protocol.oidc.impl.alipay.AuthOidcAlipayClient;
import vip.xiaonuo.auth.core.protocol.oidc.impl.dingtalk.AuthOidcDingTalkClient;
import vip.xiaonuo.auth.core.protocol.oidc.impl.douyin.AuthOidcDouyinClient;
import vip.xiaonuo.auth.core.protocol.oidc.impl.feishu.AuthOidcFeiShuClient;
import vip.xiaonuo.auth.core.protocol.oidc.impl.iam.AuthOidcIamClient;
import vip.xiaonuo.auth.core.protocol.oidc.impl.qq.AuthOidcQqClient;
import vip.xiaonuo.auth.core.protocol.oidc.impl.wechat.AuthOidcWechatClient;
import vip.xiaonuo.auth.core.protocol.oidc.impl.weibo.AuthOidcWeiboClient;
import vip.xiaonuo.auth.core.protocol.oidc.impl.welink.AuthOidcWeLinkClient;
import vip.xiaonuo.auth.core.protocol.oidc.impl.workwechat.AuthOidcWorkWechatClient;
import vip.xiaonuo.auth.core.protocol.oidc.impl.yunzhijia.AuthOidcYunZhiJiaClient;
import vip.xiaonuo.common.cache.CommonCacheOperator;
import vip.xiaonuo.common.exception.CommonException;

/**
 * OIDC认证源客户端
 *
 * @author xuyuxiang
 * @date 2024/11/8 15:41
 **/
@Slf4j
public class AuthOidcClient extends AuthBaseClient<AuthOidcBaseJson> {

    public AuthOidcClient(JSONObject authSource, String authPlatform) {
        super(authSource, authPlatform);
    }

    @Override
    public AuthOidcBaseJson getAuthBaseJson() {
        return JSONUtil.toBean(this.getAuthSource(), AuthOidcBaseJson.class);
    }

    @Override
    public void valid() {
        AuthOidcBaseJson authOidcBaseJson = this.getAuthBaseJson();
        if(StrUtil.isBlank(authOidcBaseJson.getClientId())) {
            throw new CommonException("ClientId不能为空");
        }
        if(StrUtil.isBlank(authOidcBaseJson.getClientSecret())) {
            throw new CommonException("ClientSecret不能为空");
        }
        if(StrUtil.isBlank(authOidcBaseJson.getCallbackUrl())) {
            throw new CommonException("回调地址不能为空");
        }
        if(this.getAuthPlatform().equals(AuthPlatformEnum.OIDC.getValue())) {
            if(StrUtil.isBlank(authOidcBaseJson.getAuthorizeUrl())) {
                throw new CommonException("认证授权url不能为空");
            }
            if(StrUtil.isBlank(authOidcBaseJson.getAccessTokenUrl())) {
                throw new CommonException("获取accessToken的Url不能为空");
            }
            if(StrUtil.isBlank(authOidcBaseJson.getUserInfoUrl())) {
                throw new CommonException("获取用户信息的url不能为空");
            }
            if(StrUtil.isBlank(authOidcBaseJson.getPublicKey())) {
                throw new CommonException("公钥不能为空");
            }
            if(StrUtil.isBlank(authOidcBaseJson.getAlgorithm())) {
                throw new CommonException("签名算法不能为空");
            }
            if(StrUtil.isBlank(authOidcBaseJson.getSourceProperty())) {
                throw new CommonException("关联源属性不能为空");
            }
            if(StrUtil.isBlank(authOidcBaseJson.getTargetProperty())) {
                throw new CommonException("关联目标属性不能为空");
            }
        }
    }

    public AuthRequest getAuthRequest() {
        String authPlatform = this.getAuthPlatform();
        AuthPlatformEnum authPlatformEnum = AuthPlatformEnum.valueOf(authPlatform);
        AuthRequest authRequest;
        switch (authPlatformEnum) {
            case IAM -> authRequest = new AuthOidcIamClient(getAuthBaseJson()).getAuthRequest();
            case QQ -> authRequest = new AuthOidcQqClient(getAuthBaseJson()).getAuthRequest();
            case WECHAT -> authRequest = new AuthOidcWechatClient(getAuthBaseJson()).getAuthRequest();
            case WEIBO -> authRequest = new AuthOidcWeiboClient(getAuthBaseJson()).getAuthRequest();
            case DOUYIN -> authRequest = new AuthOidcDouyinClient(getAuthBaseJson()).getAuthRequest();
            case ALIPAY -> authRequest = new AuthOidcAlipayClient(getAuthBaseJson()).getAuthRequest();
            case DINGTALK -> authRequest = new AuthOidcDingTalkClient(getAuthBaseJson()).getAuthRequest();
            case WORKWECHAT -> authRequest = new AuthOidcWorkWechatClient(getAuthBaseJson()).getAuthRequest();
            case FEISHU -> authRequest = new AuthOidcFeiShuClient(getAuthBaseJson()).getAuthRequest();
            case WELINK -> authRequest = new AuthOidcWeLinkClient(getAuthBaseJson()).getAuthRequest();
            case YUNZHIJIA -> authRequest = new AuthOidcYunZhiJiaClient(getAuthBaseJson()).getAuthRequest();
            default -> authRequest = new AuthOidcCommonClient(getAuthBaseJson()).getAuthRequest();
        }
        return authRequest;
    }

    @Override
    public String getAuthorizeUrl(String clientType) {
        // 校验认证源配置
        this.valid();
        // 获取客户端
        AuthRequest authRequest = this.getAuthRequest();
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
        AuthRequest authRequest = this.getAuthRequest();

        // 尝试正常登录流程（包含state校验）
        try {
            AuthResponse<AuthUser> authResponse = authRequest.login(AuthCallback.builder().code(code).state(state).build());

            // 检查响应是否失败
            if(!authResponse.ok()) {
                String errorMsg = authResponse.getMsg();
                // 如果是state校验失败，尝试降级处理
                if(errorMsg != null && errorMsg.contains("Illegal state")) {
                    log.warn(">>> OIDC state校验失败（响应失败），尝试跳过state校验直接获取token，state={}, error={}", state, errorMsg);
                    return doLoginWithoutStateCheck(code);
                }
                throw new CommonException(errorMsg);
            }

            return handleAuthResponse(authResponse);
        } catch (AuthException e) {
            // 如果是异常形式的state校验失败
            if(e.getMessage() != null && e.getMessage().contains("Illegal state")) {
                log.warn(">>> OIDC state校验失败（异常），尝试跳过state校验直接获取token，state={}, error={}", state, e.getMessage());
                return doLoginWithoutStateCheck(code);
            }
            throw e;
        }
    }

    /**
     * 跳过state校验，直接用code换token和用户信息
     */
    private AuthResponse<AuthUser> doLoginWithoutStateCheck(String code) {
        try {
            AuthRequest authRequest = this.getAuthRequest();
            // 直接用code换token（不校验state）
            AuthCallback authCallback = AuthCallback.builder()
                    .code(code)
                    .state("bypass-state-check")  // 使用占位state
                    .build();

            // 获取token
            AuthToken authToken = authRequest.getAccessToken(authCallback);
            // 获取用户信息
            AuthUser authUser = authRequest.getUserInfo(authToken);

            log.info(">>> OIDC跳过state校验成功获取用户信息，userId={}", authUser.getUuid());

            return AuthResponse.<AuthUser>builder()
                    .code(me.zhyd.oauth.enums.AuthResponseStatus.SUCCESS.getCode())
                    .data(authUser)
                    .build();
        } catch (Exception e) {
            log.error(">>> OIDC跳过state校验后仍然失败", e);
            throw new CommonException("OIDC登录失败：{}", e.getMessage());
        }
    }
}
