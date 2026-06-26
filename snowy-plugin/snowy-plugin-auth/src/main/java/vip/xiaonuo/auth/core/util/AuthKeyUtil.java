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

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import vip.xiaonuo.common.exception.CommonException;

import java.security.KeyFactory;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * 密钥工具类
 *
 * @author xuyuxiang
 * @date 2024/12/30 00:14
 **/
public class AuthKeyUtil {

    /**
     * 生成密钥对
     *
     * @param algorithm 算法, RS256或ES256
     * @return 密钥对
     */
    public static JWK generateKeyPair(String algorithm) {
        if (algorithm.equals(JWSAlgorithm.ES256.getName())) {
            return generateESKeyPair();
        } else if (algorithm.equals(JWSAlgorithm.RS256.getName())) {
            return generateRSAKeyPair();
        }
        throw new CommonException("不支持的算法：{}", algorithm);
    }

    /**
     * 生成RSA密钥对
     *
     * @return RSA密钥对
     */
    public static RSAKey generateRSAKeyPair()  {
        try {
            return  new RSAKeyGenerator(2048)
                    .keyID(RandomUtil.randomString(32))
                    .algorithm(new Algorithm(JWSAlgorithm.RS256.getName()))
                    .generate();
        } catch (JOSEException e) {
            throw new CommonException("生成RSA密钥对失败：{}", e);
        }
    }

    /**
     * 生成EC密钥对
     *
     * @return EC密钥对
     */
    public static ECKey generateESKeyPair() {
        try {
            return new ECKeyGenerator(Curve.P_256)
                    .keyID(RandomUtil.randomString(32))
                    .algorithm(new Algorithm(JWSAlgorithm.ES256.getName()))
                    .generate();
        } catch (JOSEException e) {
            throw new CommonException("生成ES密钥对失败：{}", e);
        }
    }

    /**
     * 获取私钥
     *
     * @param rsaKey 密钥对
     * @return 私钥
     */
    public static String getPrivateKey(RSAKey rsaKey) {
        try {
            return Base64.getEncoder().encodeToString(rsaKey.toPrivateKey().getEncoded());
        } catch (JOSEException e) {
            throw new CommonException("获取私钥失败：{}", e);
        }
    }

    /**
     * 获取私钥
     *
     * @param ecKey 密钥对
     * @return 私钥
     */
    public static String getPrivateKey(ECKey ecKey) {
        try {
            return Base64.getEncoder().encodeToString(ecKey.toPrivateKey().getEncoded());
        } catch (JOSEException e) {
            throw new CommonException("获取私钥失败：{}", e);
        }
    }

    /**
     * 获取公钥
     *
     * @param rsaKey 密钥对
     * @return 公钥
     */
    public static String getPublicKey(RSAKey rsaKey) {
        try {
            return Base64.getEncoder().encodeToString(rsaKey.toPublicKey().getEncoded());
        } catch (JOSEException e) {
            throw new CommonException("获取公钥失败：{}", e);
        }
    }

    /**
     * 获取公钥
     *
     * @param ecKey 密钥对
     * @return 公钥
     */
    public static String getPublicKey(ECKey ecKey) {
        try {
            return Base64.getEncoder().encodeToString(ecKey.toPublicKey().getEncoded());
        } catch (JOSEException e) {
            throw new CommonException("获取公钥失败：{}", e);
        }
    }

    /**
     * 获取RSAKey
     *
     * @param keyId 密钥ID
     * @param privateKeyBase64 私钥Base64
     * @param publicKeyBase64 公钥Base64
     * @return RSAKey
     */
    public static RSAKey getRasKey(String keyId, String privateKeyBase64, String publicKeyBase64) {
        try {
            // 解析私钥
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64);
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            RSAPrivateKey privateKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(privateKeySpec);

            // 解析公钥
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            RSAPublicKey publicKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(publicKeySpec);

            return new RSAKey.Builder(publicKey)
                    .privateKey(privateKey)
                    .keyID(keyId)
                    .algorithm(JWSAlgorithm.RS256)
                    .keyUse(KeyUse.SIGNATURE)
                    .build();
        } catch (Exception e) {
            throw new CommonException("RSA密钥解析失败：{}", e.getMessage());
        }
    }

    /**
     * 获取ECKey
     *
     * @param keyId 密钥ID
     * @param privateKeyBase64 私钥Base64
     * @param publicKeyBase64 公钥Base64
     * @return ECKey
     */
    public static ECKey getEcKey(String keyId, String privateKeyBase64, String publicKeyBase64) {
        try {
            // 解析私钥
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64);
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            ECPrivateKey privateKey = (ECPrivateKey) KeyFactory.getInstance("EC").generatePrivate(privateKeySpec);

            // 解析公钥
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            ECPublicKey publicKey = (ECPublicKey) KeyFactory.getInstance("EC").generatePublic(publicKeySpec);

            return new ECKey.Builder(Curve.P_256, publicKey)
                    .privateKey(privateKey)
                    .keyID(keyId)
                    .algorithm(JWSAlgorithm.ES256)
                    .keyUse(KeyUse.SIGNATURE)
                    .build();
        } catch (Exception e) {
            throw new CommonException("EC密钥解析失败：{}", e.getMessage());
        }
    }

    /**
     * RSA公钥Base64字符串或JWKS字符串转RSA公钥
     *
     * @param publicKeyStr RSA公钥Base64字符串或JWKS字符串
     * @param kid 密钥ID
     * @return RSA公钥
     */
    public static RSAPublicKey parseRSAPublicKeyFromStr(String publicKeyStr, String kid) {
        try {
            if(publicKeyStr.startsWith("http")) {
                String jwksJson = HttpUtil.get(publicKeyStr);
                return parseRSAPublicKeyFromStr(jwksJson, kid);
            } else if(JSONUtil.isTypeJSON(publicKeyStr)) {
                JWKSet jwkSet = JWKSet.parse(publicKeyStr);
                JWK jwk = jwkSet.getKeyByKeyId(kid);
                return ((RSAKey) jwk).toRSAPublicKey();
            } else {
                byte[] decoded = Base64.getDecoder().decode(publicKeyStr);
                X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
                return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(spec);
            }
        } catch (Exception e) {
            throw new CommonException("公钥解析失败：{}", e);
        }
    }

    /**
     * EC公钥Base64字符串或JWKS字符串转EC公钥
     *
     * @param publicKeyStr EC公钥Base64字符串或JWKS字符串
     * @param kid 密钥ID
     * @return EC公钥
     */
    public static ECPublicKey parseEcPublicKeyFromStr(String publicKeyStr, String kid) {
        try {
            if(publicKeyStr.startsWith("http")) {
                String jwksJson = HttpUtil.get(publicKeyStr);
                return parseEcPublicKeyFromStr(jwksJson, kid);
            } else if(JSONUtil.isTypeJSON(publicKeyStr)) {
                JWKSet jwkSet = JWKSet.parse(publicKeyStr);
                JWK jwk = jwkSet.getKeyByKeyId(kid);
                return ((ECKey) jwk).toECPublicKey();
            } else {
                byte[] decoded = Base64.getDecoder().decode(publicKeyStr);
                X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
                return (ECPublicKey) KeyFactory.getInstance("EC").generatePublic(spec);
            }
        } catch (Exception e) {
            throw new CommonException("公钥解析失败：{}", e);
        }
    }

    /**
     * 将Base64公钥转换为JWK
     *
     * @param algorithm 算法 RS256/ES256
     * @param publicKeyBase64 X.509格式的公钥Base64
     * @param keyId 密钥唯一标识
     */
    public static JWK convertPublicKeyToJwk(String algorithm, String publicKeyBase64, String keyId) {
        try {
            if (algorithm.equals(JWSAlgorithm.RS256.getName())) {
                RSAPublicKey rsaPublicKey = parseRSAPublicKeyFromStr(publicKeyBase64, keyId);
                return new RSAKey.Builder(rsaPublicKey)
                        .keyID(keyId)
                        .algorithm(new Algorithm(algorithm))
                        .keyUse(KeyUse.SIGNATURE)
                        .build();
            } else if (algorithm.equals(JWSAlgorithm.ES256.getName())) {
                ECPublicKey ecPublicKey = parseEcPublicKeyFromStr(publicKeyBase64, keyId);
                return new ECKey.Builder(Curve.P_256, ecPublicKey)
                        .keyID(keyId)
                        .algorithm(new Algorithm(algorithm))
                        .keyUse(KeyUse.SIGNATURE)
                        .build();
            }
            throw new CommonException("不支持的算法类型");
        } catch (Exception e) {
            throw new CommonException("公钥转换失败：{}", e.getMessage());
        }
    }
}
