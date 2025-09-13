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
package vip.xiaonuo.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base32;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

/**
 * 动态口令工具类
 *
 * @author xuyuxiang
 * @date 2021/12/23 21:51
 */
@Slf4j
public class CommonOtpUtil {

    /**
     * 生成动态口令密钥
     *
     * @return 动态口令密钥
     */
    public static String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[20]; // 必须为 20 字节（160 位）
        random.nextBytes(key);
        Base32 base32 = new Base32();
        return base32.encodeToString(key).replace("=", ""); // 移除填充符
    }

    /**
     * 获取动态口令 URI
     *
     * @param secretKey 密钥
     * @param issuer 发行者
     * @param account 账号
     * @return 动态口令 URI
     */
    public static String getTotUri(String secretKey, String issuer, String account) {
        return String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s&algorithm=SHA1&digits=6&period=30",
                urlEncode(issuer),
                urlEncode(account),
                secretKey,
                urlEncode(issuer)
        );
    }

    /**
     * URL 编码
     *
     * @param value 待编码的值
     * @return 编码后的值
     */
    public static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    /**
     * 校验动态口令
     *
     * @param secretKey 密钥
     * @param code 动态口令
     * @param timeWindow 时间窗口
     * @return 是否校验通过
     */
    public static boolean validateCode(String secretKey, String code, int timeWindow) {
        try {
            byte[] key = decodeSecretKey(secretKey);
            long time = System.currentTimeMillis() / 1000 / 30;

            for (int i = -timeWindow; i <= timeWindow; i++) {
                String calculatedCode = getOtpCode(key, time + i);
                if (calculatedCode.equals(code)) {
                    return true;
                }
            }
        } catch (Exception e) {
            log.error(">>> 校验出现异常：", e);
        }
        return false;
    }

    /**
     * 解码密钥
     *
     * @param secretKey 密钥
     * @return 解码后的密钥
     */
    public static byte[] decodeSecretKey(String secretKey) {
        Base32 base32 = new Base32();
        // 手动补全 Base32 填充符"="
        int padding = (8 - (secretKey.length() % 8)) % 8;
        return base32.decode(secretKey + "=".repeat(padding));
    }

    /**
     * 获取动态口令
     *
     * @param key 密钥
     * @param time 时间
     * @return 动态口令
     */
    public static String getOtpCode(byte[] key, long time) {
        byte[] counter = new byte[8];
        for (int i = 7; i >= 0; i--) {
            counter[i] = (byte) (time & 0xFF);
            time >>= 8;
        }

        Mac mac;
        try {
            mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key, "HmacSHA1"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        byte[] hash = mac.doFinal(counter);

        int offset = hash[hash.length - 1] & 0xF;
        int binary = ((hash[offset] & 0x7F) << 24) |
                ((hash[offset + 1] & 0xFF) << 16) |
                ((hash[offset + 2] & 0xFF) << 8) |
                (hash[offset + 3] & 0xFF);

        int otp = binary % 1000000;
        return String.format("%06d", otp);
    }
}
