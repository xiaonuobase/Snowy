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

import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.SM2;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

/**
 * 密钥保护工具类，提供以下能力：
 * 1. 密钥混淆存储：通过XOR+随机掩码将密钥打散为字节数组，防止反编译直接获取明文
 * 2. 运行时还原：将混淆后的字节数组还原为原始密钥
 * 3. SM2密钥对生成：生成新的SM2公私钥对，并输出混淆后的Java代码
 *
 * @author yubaoshan
 * @date 2022/9/15 21:51
 */
public class CommonKeyUtil {

    /**
     * 运行时还原密钥（XOR解混淆）
     *
     * @param data 混淆后的数据字节数组
     * @param mask 混淆掩码字节数组
     * @return 还原后的密钥字符串
     */
    public static String reconstruct(byte[] data, byte[] mask) {
        byte[] result = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = (byte) (data[i] ^ mask[i]);
        }
        return new String(result, StandardCharsets.UTF_8);
    }

    /**
     * 生成SM2密钥对，打印公私钥十六进制字符串及对应的混淆代码
     * 生成后将混淆代码替换到 CommonCryptogramUtil 中即可
     */
    public static void generateSm2KeyPair() {
        SM2 sm2 = SmUtil.sm2();
        BCECPublicKey publicKey = (BCECPublicKey) sm2.getPublicKey();
        BCECPrivateKey privateKey = (BCECPrivateKey) sm2.getPrivateKey();
        String pubHex = HexUtil.encodeHexStr(publicKey.getQ().getEncoded(false));
        String priHex = privateKey.getD().toString(16);
        System.out.println("========== SM2 密钥对 ==========");
        System.out.println("公钥: " + pubHex);
        System.out.println("私钥: " + priHex);
        System.out.println("================================");
        System.out.println();
        System.out.println("--- 公钥（明文，直接替换 PUBLIC_KEY 的值）---");
        System.out.println("private static final String PUBLIC_KEY = \"" + pubHex + "\";");
        System.out.println();
        System.out.println("--- 私钥混淆代码（替换 PRIVATE_KEY_DATA 和 PRIVATE_KEY_MASK）---");
        printObfuscatedCode("PRIVATE_KEY", priHex);
    }

    /**
     * 将密钥进行混淆处理，输出可直接嵌入Java源码的混淆字节数组定义
     *
     * @param name 变量名前缀
     * @param key 十六进制密钥字符串
     */
    public static void printObfuscatedCode(String name, String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] mask = new byte[keyBytes.length];
        new SecureRandom().nextBytes(mask);
        byte[] data = new byte[keyBytes.length];
        for (int i = 0; i < keyBytes.length; i++) {
            data[i] = (byte) (keyBytes[i] ^ mask[i]);
        }
        System.out.println("private static final byte[] " + name + "_DATA = {");
        printByteArray(data);
        System.out.println("};");
        System.out.println("private static final byte[] " + name + "_MASK = {");
        printByteArray(mask);
        System.out.println("};");
    }

    private static void printByteArray(byte[] arr) {
        StringBuilder sb = new StringBuilder("    ");
        for (int i = 0; i < arr.length; i++) {
            sb.append(String.format("(byte)0x%02X", arr[i] & 0xFF));
            if (i < arr.length - 1) {
                sb.append(", ");
                if ((i + 1) % 10 == 0) {
                    sb.append("\n    ");
                }
            }
        }
        System.out.println(sb);
    }

    /**
     * 生成SM4对称密钥（128位，16进制字符串）和IV向量，并输出混淆代码
     */
    public static void generateSm4Key() {
        byte[] keyBytes = new byte[16];
        byte[] ivBytes = new byte[16];
        new SecureRandom().nextBytes(keyBytes);
        new SecureRandom().nextBytes(ivBytes);
        String sm4Key = HexUtil.encodeHexStr(keyBytes);
        String sm4Iv = HexUtil.encodeHexStr(ivBytes);
        System.out.println("========== SM4 密钥 ==========");
        System.out.println("SM4密钥: " + sm4Key);
        System.out.println("SM4 IV: " + sm4Iv);
        System.out.println("================================");
        System.out.println();
        System.out.println("--- SM4密钥混淆代码（替换 SM4_KEY_DATA 和 SM4_KEY_MASK）---");
        printObfuscatedCode("SM4_KEY", sm4Key);
        System.out.println();
        System.out.println("--- SM4 IV混淆代码（替换 SM4_IV_DATA 和 SM4_IV_MASK）---");
        printObfuscatedCode("SM4_IV", sm4Iv);
    }

    /**
     * 直接运行此方法即可生成全套新密钥及混淆代码
     * 将输出的混淆数组替换到 CommonCryptogramUtil 中对应位置即可
     */
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("       全套密钥生成 & 混淆代码输出");
        System.out.println("==========================================");
        System.out.println();
        generateSm2KeyPair();
        System.out.println();
        generateSm4Key();
        System.out.println();
        System.out.println("==========================================");
        System.out.println("使用方式：");
        System.out.println("1. 将【公钥】明文字符串替换 CommonCryptogramUtil 中的 PUBLIC_KEY");
        System.out.println("2. 将【私钥混淆代码】替换 PRIVATE_KEY_DATA 和 PRIVATE_KEY_MASK");
        System.out.println("3. 将【SM4密钥混淆代码】替换 SM4_KEY_DATA 和 SM4_KEY_MASK");
        System.out.println("4. 将【SM4 IV混淆代码】替换 SM4_IV_DATA 和 SM4_IV_MASK");
        System.out.println("注意：更换密钥后，已加密的历史数据将无法解密，请确保数据已迁移！");
        System.out.println("==========================================");
    }
}
