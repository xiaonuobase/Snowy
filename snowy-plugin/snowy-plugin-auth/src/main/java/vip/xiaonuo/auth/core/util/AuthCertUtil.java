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

import cn.hutool.core.io.FileUtil;
import lombok.Getter;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import vip.xiaonuo.common.exception.CommonException;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

/**
 * 证书工具类
 *
 * @author xuyuxiang
 * @date 2024/12/30 00:14
 **/
public class AuthCertUtil {

    private static final String ISSUER_NAME = "SNOWY";

    private static final String PRIVATE_KEY_DATA = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC7hD/DsKIO2Tnl2E1SloZSgMv4pjmRx2vYgc4wIY3DNs9OWQ9Bzwh7F1cufqhMLxpTcs/X3ruo2lBnNyuwSe00NJ/qkpUr2IMD5h7/1IYgHYNZn0e4X7/Hbt2J18SpnBMY2dEhS7aPt0IEhEmGSdBUKbJLJ1P/g0rXAuL/kUQ9mOMPVOLLXfmk/1F/BvylS/vtTE560Bo2MBZL0qvi3ebU5g2gGOcMrIbWm4JAqBS2PXA0XYKu2XEjoqibCyWF1V6/eGSlYQ4wnBmW0skkYG7n4ze9rnUIuK6NUvM9+Ar6wlUGTy9nftZXFnDookrS7Xzl6YQ5eGLulAA3/O2TJtkHAgMBAAECggEAFew6QDiwWagP2s1VDOjIP3ELZ8CXTnHQK/nRD1N87WvFBNXosLrxtOJfsPHh3zZKj6MGpahGrFEJwWdz1iJudwIICDam9f9UIeqPiT1hiPOqXZn/mpCnLTvK7sipF2NF4Evm5Uuixl6Ye0q0Bd0vXAeb7qlkaqUA5O5Td6mWHtVDpyooZTuQM4ukLKDcJ5dgg4cVub+Hg0QfcmJYyUbAPZd0PHmgjjrW7ZCCleta23PxjMGDF7rSMbG9/H2OXxj5+JTZmGS9qzS7A2/FI7wCHsrwGr8YC5pkOu2P/zGOvQz8X5NGxH5ydWxLVpMSgYtB/FHz0Ir42+hBc2+uSSVeQQKBgQDsA+yTAmhmMX2ZAExFbBZF9L6RFobV08OHxQov/JXUCsQCjHGN6h/MRaXwwC23tRVO4V+SB7lW6BtliEaMC2Oxgwfd/iglKJUCId2aNsln5XYTnVi2+N7Zels++lrPHUqihrm992dxlJ8Im8kQSEGb0tEaKZs9LJgUqwftrY/K0QKBgQDLZQYYm/0UP9uKYMIKRQ2dA/5txwJTU4jdsfU3axMqXeOz8Qs27Rx+aWssHdlLEaKmi+PRNFNs4C7dj5vxMgeGj2gZsJD7NYVhW5sE+shZCq1UDJT/ZE0KjjJYFuyfRx/YCMieSLEfRIvUdUNO1dsBXUVy3bhZDHRsh6Piqt0sVwKBgA7hYHUUXcizrhuLbUOjQPRALhOvu+iXZWiV/8JuxnOMCjeu6ynrbP3QFlpLg5ZlkGZcx03JtuHH2JJaOThzL2HtxP02HQnauSpdQD+M3L30NbGe5g0DO+myUTZzSm5shlwg6m0TwC3lFPHSAKdKJvjQHebsYKFGwn/rGKDfpIVBAoGBAMeMpAB8+j1lsVUAPR+qRALqqjK17zglWsB3Gih26uTEHOSr9t66nxv+/WDZIBkggj14PdlAxyCoihJiFRL8jJmeFPovWu67wdKU93BZQzro2Mj/VyeCFnvmqsn2I8zZEQr5AysYEn6k17NoINM1yZp7EenyUi07pPXd1hJKpJgdAoGAYch5UNALRWlnTMN01HqImlK5Tg4LvmzVhV/X9VpQQTRoIgNXLnnkLqVFw1OJoDjjaVp/zlK5xZyCtoQZ2iPb9wiQXtXsIXR/d5MCqTVEVLtCaUC6X6YCOSSABGrrv6K5CMIDPls6pLkC7ddiwwSyYRkz0tsDZsNO4GW42ONp2xw=";

    private static final String CRT_DATA = "MIICoTCCAYmgAwIBAgIGAZkvN3ZkMA0GCSqGSIb3DQEBCwUAMBAxDjAMBgNVBAMMBVNOT1dZMB4XDTI1MDkwOTE2MDMwOFoXDTI2MDkwOTE2MDMwOFowEzERMA8GA1UEAwwIU0FNTF9JZFAwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQC7hD/DsKIO2Tnl2E1SloZSgMv4pjmRx2vYgc4wIY3DNs9OWQ9Bzwh7F1cufqhMLxpTcs/X3ruo2lBnNyuwSe00NJ/qkpUr2IMD5h7/1IYgHYNZn0e4X7/Hbt2J18SpnBMY2dEhS7aPt0IEhEmGSdBUKbJLJ1P/g0rXAuL/kUQ9mOMPVOLLXfmk/1F/BvylS/vtTE560Bo2MBZL0qvi3ebU5g2gGOcMrIbWm4JAqBS2PXA0XYKu2XEjoqibCyWF1V6/eGSlYQ4wnBmW0skkYG7n4ze9rnUIuK6NUvM9+Ar6wlUGTy9nftZXFnDookrS7Xzl6YQ5eGLulAA3/O2TJtkHAgMBAAEwDQYJKoZIhvcNAQELBQADggEBAEIwrfq4z/lifXYT+2R5l6vi1/9RuLbjp3o2+eFEalIPp1QRJJky5wgeVbbDAd8ZuUCaEkz8mS4A60oQ56iyPmqN/8dwiy0dhkyCqUPCCdgzPTzRmCP4PSTdSMeKRcuNXe3O3s3ms23MhKJty0kkE1QcB514ofAXS7VaLkU+VHrA2jwGD+7z0nJebNNP2PUjJPoaYJDOOyBmc2oOue+PzTeSdIiIUtXU2rbbeL4hzcoIkj7/5aLEcrolz8s+wq2yKwALl3d+bD8ywcQU+AgJSTIhS0NQ2oL8vcjH/EdOviOyl/xxIwu0yWkDhL/MmRF7nqipq+S2UueK7K2sGP4Uaqw=";

    /**
     * 生成SP的自签名证书及密钥对（适用于开发环境）
     */
    public static CertificateKeyPair generateSelfSignedCertificateKeyPairForSp() {
        return generateSelfSignedCertificateKeyPair(ISSUER_NAME, "SAML_SP");
    }

    /**
     * 生成IdP的自签名证书及密钥对（适用于开发环境）
     */
    public static CertificateKeyPair generateSelfSignedCertificateKeyPairForIdp() {
        return generateSelfSignedCertificateKeyPair(ISSUER_NAME, "SAML_IdP");
    }

    /**
     * 生成自签名证书及密钥对（适用于开发环境）
     */
    public static CertificateKeyPair generateSelfSignedCertificateKeyPair(String issuerName, String subjectName) {
        try {
            // 生成RSA密钥对
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // 设置证书信息
            X500Name issuer = new X500Name("CN=" + issuerName);
            X500Name subject = new X500Name("CN=" + subjectName);
            BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());
            Date notBefore = new Date();
            Date notAfter = new Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000); // 有效期1年

            // 构建证书
            X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                    issuer, serial, notBefore, notAfter, subject, keyPair.getPublic()
            );

            // 使用SHA256withRSA签名
            ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSAEncryption")
                    .build(keyPair.getPrivate());

            // 生成并验证证书
            X509Certificate cert = new JcaX509CertificateConverter()
                    .getCertificate(certBuilder.build(signer));
            cert.verify(keyPair.getPublic());
            String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
            // 返回包含证书和密钥对的容器
            return new CertificateKeyPair(cert, publicKey, privateKey);
        } catch (Exception e) {
            throw new CommonException("生成自签名证书失败：{}", e);
        }
    }

    /**
     * 加载内置证书（适用于生产环境）
     */
    public static X509Certificate loadBuiltInCertificate() {
        return parseCertificateFromPem(CRT_DATA);
    }

    /**
     * 加载内置私钥（适用于生产环境）
     */
    public static PrivateKey loadBuiltInPrivateKey() {
        return parsePrivateKeyFromPem(PRIVATE_KEY_DATA);
    }

    /**
     * 将X509证书保存到文件（PEM格式）
     */
    public static void saveCertificateToFile(X509Certificate cert, String filePath) throws Exception {
        // 转换为PEM格式
        String pemCert = convertCertificateToPem(cert);

        // 确保父目录存在
        File file =  FileUtil.file(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            FileUtil.mkdir(parentDir);
        }

        // 写入文件
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(pemCert);
        }
    }

    /**
     * 将X509证书转换为PEM格式字符串
     */
    public static String convertCertificateToPem(X509Certificate cert) {
        try {
            return Base64.getEncoder().encodeToString(cert.getEncoded());
        } catch (Exception e) {
            throw new CommonException("证书转换失败：{}", e);
        }
    }

    /**
     * 从PEM格式字符串解析X509证书
     */
    public static X509Certificate parseCertificateFromPem(String pemCertificate) {
        try {
            byte[] certBytes = Base64.getDecoder().decode(pemCertificate);
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            return (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(certBytes));
        } catch (Exception e) {
            throw new CommonException("证书解析失败：{}", e);
        }
    }

    /**
     * 从PEM格式字符串解析公钥
     */
    public static PublicKey parsePublicKeyFromPem(String pemPublicKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(pemPublicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new CommonException("公钥解析失败：{}", e);
        }
    }

    /**
     * 从PEM格式字符串解析私钥
     */
    public static PrivateKey parsePrivateKeyFromPem(String pemPrivateKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(pemPrivateKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new CommonException("私钥解析失败：{}", e);
        }
    }

    @Getter
    public static class CertificateKeyPair {
        // Getters
        private final X509Certificate certificate;
        private final String publicKey;
        private final String privateKey;

        public CertificateKeyPair(X509Certificate certificate, String publicKey, String privateKey) {
            this.certificate = certificate;
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }
    }
}
