package vip.xiaonuo.auth.core.protocol.saml;

import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.auth.core.protocol.base.AuthBaseJson;

/**
 * SAML认证源基础配置
 *
 * @author xuyuxiang
 * @date 2024/5/31 10:06
 **/
@Getter
@Setter
public class AuthSamlBaseJson extends AuthBaseJson {

    /** SP元数据（自动生成） */
    private String spMetaData;

    /** 自签证书（自动生成） */
    private String certificate;

    /** 签名私钥（自动生成） */
    private String privateKey;

    /** 验证公钥（自动生成） */
    private String publicKey;

    /** SP的entityID（自动生成） */
    private String spEntityId;

    /** SP的AclUrl（自动生成） */
    private String spAclUrl;

    /** IdP元数据 */
    private String idpMetaData;

    /** 绑定类型 */
    private String bindingType;
}
