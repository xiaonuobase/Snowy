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
package vip.xiaonuo.auth.core.util;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import vip.xiaonuo.common.exception.CommonException;

import java.util.Map;

/**
 * Token工具类
 *
 * @author xuyuxiang
 * @date 2024/12/30 00:14
 **/
public class AuthTokenUtil {

    /**
     * 生成JWT令牌
     *
     * @param algorithm 算法，RS256或ES256
     * @param privateKeyBase64 私钥Base64
     * @param claims 声明
     * @return JWT令牌
     */
    public static String generateToken(String algorithm, String keyId, String privateKeyBase64, String publicKeyBase64, Map<String, Object> claims) {
        try {
            JWTClaimsSet claimsSet = JWTClaimsSet.parse(claims);
            if (JWSAlgorithm.RS256.getName().equalsIgnoreCase(algorithm)) {
                RSAKey rsaKey = AuthKeyUtil.getRasKey(keyId, privateKeyBase64, publicKeyBase64);
                JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                        .keyID(rsaKey.getKeyID()).build();
                SignedJWT jwt = new SignedJWT(header, claimsSet);
                jwt.sign(new RSASSASigner(rsaKey));
                return jwt.serialize();
            } else if (JWSAlgorithm.ES256.getName().equalsIgnoreCase(algorithm)) {
                ECKey ecKey = AuthKeyUtil.getEcKey(keyId, privateKeyBase64, publicKeyBase64);
                JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256)
                        .keyID(ecKey.getKeyID()).build();
                SignedJWT jwt = new SignedJWT(header, claimsSet);
                jwt.sign(new ECDSASigner(ecKey));
                return jwt.serialize();
            } else {
                throw new CommonException("不支持的算法类型");
            }
        } catch (Exception e) {
            throw new CommonException("令牌生成失败：{}", e);
        }
    }

    /**
     * 验证JWT令牌
     *
     * @param algorithm 算法, RS256或ES256
     * @param publicKeyStr 公钥，Base64编码或JWKS字符串
     * @param token 令牌
     */
    public static boolean verifyToken(String algorithm, String publicKeyStr, String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            String kid = jwt.getHeader().getKeyID();
            if (JWSAlgorithm.RS256.getName().equalsIgnoreCase(algorithm)) {
                return jwt.verify(new RSASSAVerifier(AuthKeyUtil.parseRSAPublicKeyFromStr(publicKeyStr, kid)));
            } else if (JWSAlgorithm.ES256.getName().equalsIgnoreCase(algorithm)) {
                return jwt.verify(new ECDSAVerifier(AuthKeyUtil.parseEcPublicKeyFromStr(publicKeyStr, kid)));
            }
            return false;
        } catch (Exception e) {
            throw new CommonException("令牌验证失败：{}", e);
        }
    }
}
