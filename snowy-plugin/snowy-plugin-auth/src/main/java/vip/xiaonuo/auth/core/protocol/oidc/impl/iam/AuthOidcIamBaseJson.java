package vip.xiaonuo.auth.core.protocol.oidc.impl.iam;

import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.auth.core.protocol.base.AuthBaseJson;

/**
 * 山信通认证源基础配置
 *
 * @author xuyuxiang
 * @date 2024/5/31 10:06
 **/
@Getter
@Setter
public class AuthOidcIamBaseJson extends AuthBaseJson {

    /** clientId */
    private String clientId;

    /** clientSecret */
    private String clientSecret;

    /** 认证授权url */
    private String authorizeUrl;

    /** 获取accessToken的Url */
    private String accessTokenUrl;

    /** 获取用户信息的url */
    private String userInfoUrl;

    /** 回调地址（自动生成） */
    private String callbackUrl;

    /** 权限范围 */
    private String scope;
}
