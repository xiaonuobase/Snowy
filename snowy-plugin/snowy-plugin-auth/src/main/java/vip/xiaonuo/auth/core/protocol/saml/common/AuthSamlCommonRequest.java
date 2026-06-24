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
package vip.xiaonuo.auth.core.protocol.saml.common;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import me.zhyd.oauth.enums.AuthResponseStatus;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.utils.AuthStateUtils;
import vip.xiaonuo.auth.core.protocol.base.AuthBaseRequest;
import vip.xiaonuo.auth.core.protocol.saml.AuthSamlBaseJson;
import vip.xiaonuo.auth.core.protocol.saml.core.SamlClient;
import vip.xiaonuo.auth.core.protocol.saml.core.SamlException;
import vip.xiaonuo.auth.core.protocol.saml.core.SamlResponse;
import vip.xiaonuo.auth.core.util.AuthCertUtil;
import vip.xiaonuo.common.cache.CommonCacheOperator;
import vip.xiaonuo.common.exception.CommonException;

import java.io.StringReader;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Map;

import static vip.xiaonuo.auth.core.protocol.base.AuthBaseClient.CONFIG_CACHE_KEY;


/**
 * SAML认证源通用请求
 *
 * @author xuyuxiang
 * @date 2025/1/24 15:09
 **/
public record AuthSamlCommonRequest(AuthSamlBaseJson authSamlBaseJson)
        implements AuthBaseRequest {

    public SamlClient getClient() {
        String idpMetaData = authSamlBaseJson.getIdpMetaData();
        try {
            StringReader reader = StrUtil.getReader(idpMetaData);
            SamlClient client = SamlClient.fromMetadata(authSamlBaseJson.getSpEntityId(),
                    authSamlBaseJson.getSpAclUrl(), reader, SamlClient.SamlIdpBinding.valueOf(authSamlBaseJson.getBindingType()));
            X509Certificate x509Certificate = AuthCertUtil.parseCertificateFromPem(authSamlBaseJson.getCertificate());
            PrivateKey privateKey = AuthCertUtil.parsePrivateKeyFromPem(authSamlBaseJson.getPrivateKey());
            client.setSPKeys(x509Certificate, privateKey);
            reader.close();
            return client;
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public String authorize(String clientType) {
        try {
            // 获取idp地址
            String identityProviderUrl = this.getClient().getIdentityProviderUrl();
            // 构造SamlRequest
            String samlRequest = getClient().getSamlRequest();
            // 获取状态
            String state = AuthStateUtils.createState();
            // 获取缓存操作类
            CommonCacheOperator commonCacheOperator = SpringUtil.getBean(CommonCacheOperator.class);
            // 放入缓存
            commonCacheOperator.put(CONFIG_CACHE_KEY + state, JSONUtil.createObj().set("clientType", clientType), 300);
            // 构造授权地址
            String authorizeUrl = SaFoxUtil.joinParam(identityProviderUrl, "SAMLRequest", URLUtil.encodeAll(samlRequest));
            // 追加state
            authorizeUrl= SaFoxUtil.joinParam(authorizeUrl, "RelayState", state);
            // 返回授权地址
            return authorizeUrl;
        } catch (SamlException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AuthResponse<AuthUser> login() {
        AuthResponse<AuthUser> authResponse = new AuthResponse<>();
        SamlClient client = this.getClient();
        Map<String, String> paramMap = SaHolder.getRequest().getParamMap();
        String encodedResponse = paramMap.get("SAMLResponse");
        if(ObjectUtil.isEmpty(encodedResponse)){
            throw new CommonException("SAMLResponse不能为空");
        }
        SamlResponse response;
        try {
            response = client.decodeAndValidateSamlResponse(encodedResponse, authSamlBaseJson.getBindingType());
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
        String authenticatedUser = response.getNameID();
        authResponse.setCode(AuthResponseStatus.SUCCESS.getCode());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(authSamlBaseJson.getSourceProperty(), authenticatedUser);
        authResponse.setData(AuthUser.builder().rawUserInfo(jsonObject).uuid(authenticatedUser).build());
        return authResponse;
    }
}
