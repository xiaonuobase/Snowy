package vip.xiaonuo.auth.core.protocol.oidc;

import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.auth.core.protocol.base.AuthBaseJson;

/**
 * OIDC认证源基础配置
 *
 * @author xuyuxiang
 * @date 2024/5/31 10:06
 **/
@Getter
@Setter
public class AuthOidcBaseJson extends AuthBaseJson {

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

    /** 公钥（公钥base64字符串或者jwk字符串） */
    private String publicKey;

    /** 签名算法 */
    private String algorithm;

    //=====以下为可选配置=====//

    /** 企业微信，授权方的网页应用ID */
    private String agentId;
}
