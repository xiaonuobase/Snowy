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
package vip.xiaonuo.auth.core.protocol.saml;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import vip.xiaonuo.auth.core.protocol.base.AuthBaseClient;
import vip.xiaonuo.auth.core.protocol.base.AuthBaseRequest;
import vip.xiaonuo.auth.core.protocol.saml.common.AuthSamlCommonClient;
import vip.xiaonuo.auth.core.protocol.saml.core.MetadataUtils;
import vip.xiaonuo.auth.core.protocol.saml.core.SamlClient;
import vip.xiaonuo.auth.core.util.AuthCertUtil;
import vip.xiaonuo.common.exception.CommonException;

import java.security.cert.X509Certificate;

/**
 * SAML认证源客户端
 *
 * @author xuyuxiang
 * @date 2024/11/8 15:41
 **/
@Slf4j
public class AuthSamlClient extends AuthBaseClient<AuthSamlBaseJson> {

    public AuthSamlClient(JSONObject authSource, String authPlatform) {
        super(authSource, authPlatform);
    }

    @Override
    public AuthSamlBaseJson getAuthBaseJson() {
        return JSONUtil.toBean(this.getAuthSource(), AuthSamlBaseJson.class);
    }

    @Override
    public void valid() {
        AuthSamlBaseJson authSourceSamlBaseJson = getAuthBaseJson();
        if(StrUtil.isBlank(authSourceSamlBaseJson.getIdpMetaData())) {
            throw new CommonException("IdP元数据不能为空");
        }
        if(StrUtil.isBlank(authSourceSamlBaseJson.getSpMetaData())) {
            throw new CommonException("SP元数据不能为空");
        }
        if(StrUtil.isBlank(authSourceSamlBaseJson.getCertificate())) {
            throw new CommonException("自签证书不能为空");
        }
        if(StrUtil.isBlank(authSourceSamlBaseJson.getPrivateKey())) {
            throw new CommonException("签名私钥不能为空");
        }
        if(StrUtil.isBlank(authSourceSamlBaseJson.getPublicKey())) {
            throw new CommonException("验证公钥不能为空");
        }
        if(StrUtil.isBlank(authSourceSamlBaseJson.getSpEntityId())) {
            throw new CommonException("SP的entityID不能为空");
        }
        if(StrUtil.isBlank(authSourceSamlBaseJson.getSpAclUrl())) {
            throw new CommonException("SP的AclUrl不能为空");
        }
        if(StrUtil.isBlank(authSourceSamlBaseJson.getBindingType())) {
            throw new CommonException("绑定类型不能为空");
        }
        if(StrUtil.isBlank(authSourceSamlBaseJson.getSourceProperty())) {
            throw new CommonException("关联源属性不能为空");
        }
        if(StrUtil.isBlank(authSourceSamlBaseJson.getTargetProperty())) {
            throw new CommonException("关联目标属性不能为空");
        }
    }

    @Override
    public String getAuthorizeUrl(String clientType) {
        // 获取客户端
        AuthBaseRequest authRequest = new AuthSamlCommonClient(getAuthBaseJson()).getAuthRequest();
        // 执行认证
        return authRequest.authorize(clientType);
    }

    @Override
    public AuthResponse<AuthUser> doLogin(AuthCallback authCallback) {
        AuthBaseRequest authRequest = new AuthSamlCommonClient(getAuthBaseJson()).getAuthRequest();
        AuthResponse<AuthUser> authResponse = authRequest.login();
        if(!authResponse.ok()) {
            throw new CommonException(authResponse.getMsg());
        }
        // 处理认证响应
        return handleAuthResponse(authResponse);
    }

    public static void main(String[] args) {
        String templateJson = "{\"callbackUrl\":\"{frontUrlUrl}/callback/SAML\",\"spEntityId\":\"{frontUrlUrl}/callback/SAML\",\"spAclUrl\":\"{frontUrlUrl}/callback/SAML\"}";
        JSONObject paramJsonObject = JSONUtil.createObj();
        // 替换成你的前端地址
        String frontUrl = "http://localhost:81";
        // 替换成你的后端地址
        String backendUrl = "http://localhost:82";
        paramJsonObject.set("frontUrl", frontUrl);
        paramJsonObject.set("backendUrl", backendUrl);
        templateJson = StrUtil.format(templateJson, paramJsonObject);
        AuthSamlBaseJson authSourceSamlBaseJson = JSONUtil.toBean(templateJson, AuthSamlBaseJson.class);
        // 生成证书及密钥对
        AuthCertUtil.CertificateKeyPair certKeyPair = AuthCertUtil.generateSelfSignedCertificateKeyPairForSp();
        X509Certificate certificate = certKeyPair.getCertificate();
        // 证书
        String certificateBase64 = AuthCertUtil.convertCertificateToPem(certificate);
        authSourceSamlBaseJson.setCertificate(certificateBase64);
        // 私钥
        authSourceSamlBaseJson.setPrivateKey(certKeyPair.getPrivateKey());
        // 公钥
        authSourceSamlBaseJson.setPublicKey(certKeyPair.getPublicKey());
        authSourceSamlBaseJson.setBindingType(SamlClient.SamlIdpBinding.Redirect.name());
        // 根据模板生成元数据
        String spMetaData = MetadataUtils.generateSpMetadata(authSourceSamlBaseJson.getSpEntityId(), authSourceSamlBaseJson.getSpAclUrl(),
                backendUrl + "/auth/b/doLogout", certificate);
        authSourceSamlBaseJson.setSpMetaData(spMetaData);
        spMetaData = JSONUtil.toJsonStr(authSourceSamlBaseJson, new JSONConfig().setIgnoreNullValue(false));
        // 最终元数据spMetaData
        System.out.println(spMetaData);
    }
}
