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
package vip.xiaonuo.auth.core.protocol.cas;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import vip.xiaonuo.auth.core.protocol.base.AuthBaseClient;
import vip.xiaonuo.auth.core.protocol.base.AuthBaseRequest;
import vip.xiaonuo.auth.core.protocol.cas.common.AuthCasCommonClient;
import vip.xiaonuo.common.exception.CommonException;

/**
 * CAS认证源客户端
 *
 * @author xuyuxiang
 * @date 2024/11/8 15:41
 **/
@Slf4j
public class AuthCasClient extends AuthBaseClient<AuthCasBaseJson> {

    public AuthCasClient(JSONObject authSource, String authPlatform) {
        super(authSource, authPlatform);
    }

    @Override
    public AuthCasBaseJson getAuthBaseJson() {
        return JSONUtil.toBean(this.getAuthSource(), AuthCasBaseJson.class);
    }

    @Override
    public void valid() {
        AuthCasBaseJson authSourceCasBaseJson = this.getAuthBaseJson();
        if(StrUtil.isBlank(authSourceCasBaseJson.getCasServerLoginUrl())) {
            throw new CommonException("CAS认证服务登录地址不能为空");
        }
        if(StrUtil.isBlank(authSourceCasBaseJson.getCasServerValidateUrl())) {
            throw new CommonException("CAS认证服务验证地址不能为空");
        }
        if(StrUtil.isBlank(authSourceCasBaseJson.getCasServerProtocolVersion())) {
            throw new CommonException("CAS认证服务协议版本不能为空");
        }
        if(StrUtil.isBlank(authSourceCasBaseJson.getServiceUrl())) {
            throw new CommonException("Service地址不能为空");
        }
        if(StrUtil.isBlank(authSourceCasBaseJson.getSourceProperty())) {
            throw new CommonException("关联源属性不能为空");
        }
        if(StrUtil.isBlank(authSourceCasBaseJson.getTargetProperty())) {
            throw new CommonException("关联目标属性不能为空");
        }
    }

    @Override
    public String getAuthorizeUrl(String clientType) {
        // 校验认证源配置
        this.valid();
        // 获取客户端
        AuthBaseRequest authRequest = new AuthCasCommonClient(getAuthBaseJson()).getAuthRequest();
        // 执行认证
        return authRequest.authorize(clientType);
    }

    @Override
    public AuthResponse<AuthUser> doLogin() {
        AuthBaseRequest authRequest = new AuthCasCommonClient(getAuthBaseJson()).getAuthRequest();
        AuthResponse<AuthUser> authResponse = authRequest.login();
        if(!authResponse.ok()) {
            throw new CommonException(authResponse.getMsg());
        }
        return handleAuthResponse(authResponse);
    }
}
