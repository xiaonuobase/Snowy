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
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import com.antherd.smcrypto.sm2.Sm2;
import com.antherd.smcrypto.sm4.Sm4;
import com.antherd.smcrypto.sm4.Sm4Options;
import lombok.extern.slf4j.Slf4j;

/**
 * 加密工具类，本框架目前使用 <a href="https://github.com/antherd/sm-crypto">sm-crypto</a> 项目中一些加解密方式
 * 使用小伙伴需要过等保密评相关，请在此处更改为自己的加密方法，或加密机，使用加密机同时需要替换公钥，私钥在内部无法导出，提供加密的方法
 * 如果不涉及到加密机方面的内容，请更改公私要为自己重新生成的，生成方式请看集成的sm-crypto主页
 *
 * 密钥已通过 CommonKeyUtil 进行混淆存储，防止反编译直接获取明文
 * 如需重新生成密钥对，调用 CommonKeyUtil.generateSm2KeyPair() 方法，将输出的混淆代码替换下方对应数组即可
 *
 * @author yubaoshan
 * @date 2022/9/15 21:51
 */
@Slf4j
public class CommonCryptogramUtil {

    /** 公钥 */
    private static final String PUBLIC_KEY = "04298364ec840088475eae92a591e01284d1abefcda348b47eb324bb521bb03b0b2a5bc393f6b71dabb8f15c99a0050818b56b23f31743b93df9cf8948f15ddb54";

    /** 私钥混淆后的密文数据，运行时通过XOR还原，防止反编译直接获取明文，更换密钥请运行CommonKeyUtil.main() */
    private static final byte[] PRIVATE_KEY_DATA = {
        (byte)0x32, (byte)0x10, (byte)0xA6, (byte)0xC8, (byte)0xAB, (byte)0x84, (byte)0xFD, (byte)0x19, (byte)0x1D, (byte)0xDE,
        (byte)0x51, (byte)0xE9, (byte)0x48, (byte)0x71, (byte)0xA5, (byte)0x27, (byte)0x26, (byte)0x62, (byte)0x25, (byte)0x6B,
        (byte)0xE6, (byte)0x12, (byte)0x52, (byte)0x27, (byte)0x52, (byte)0x14, (byte)0x66, (byte)0xFC, (byte)0x63, (byte)0xA2,
        (byte)0x43, (byte)0xB0, (byte)0x1F, (byte)0xB7, (byte)0x9E, (byte)0xFE, (byte)0x0E, (byte)0xA6, (byte)0x84, (byte)0xE8,
        (byte)0x61, (byte)0x63, (byte)0xFB, (byte)0xCB, (byte)0x50, (byte)0x6B, (byte)0xFF, (byte)0x79, (byte)0x48, (byte)0xBF,
        (byte)0x2E, (byte)0x3E, (byte)0x9D, (byte)0x9F, (byte)0xD8, (byte)0x6E, (byte)0xE8, (byte)0x33, (byte)0x70, (byte)0x45,
        (byte)0xC6, (byte)0x9C, (byte)0x76, (byte)0x8D
    };
    /** 私钥混淆掩码，与PRIVATE_KEY_DATA逐字节XOR可还原出私钥明文 */
    private static final byte[] PRIVATE_KEY_MASK = {
        (byte)0x01, (byte)0x20, (byte)0x95, (byte)0xFF, (byte)0x9C, (byte)0xB6, (byte)0xCE, (byte)0x7D, (byte)0x29, (byte)0xE9,
        (byte)0x63, (byte)0xD0, (byte)0x7A, (byte)0x40, (byte)0x92, (byte)0x16, (byte)0x10, (byte)0x55, (byte)0x12, (byte)0x0E,
        (byte)0x85, (byte)0x2A, (byte)0x30, (byte)0x43, (byte)0x65, (byte)0x70, (byte)0x05, (byte)0xC5, (byte)0x02, (byte)0xC4,
        (byte)0x75, (byte)0x89, (byte)0x29, (byte)0xD4, (byte)0xA9, (byte)0xCA, (byte)0x39, (byte)0x94, (byte)0xE6, (byte)0x8B,
        (byte)0x54, (byte)0x05, (byte)0xC9, (byte)0xFE, (byte)0x61, (byte)0x09, (byte)0xCD, (byte)0x1A, (byte)0x2D, (byte)0xDC,
        (byte)0x1E, (byte)0x09, (byte)0xF8, (byte)0xA9, (byte)0xED, (byte)0x08, (byte)0x8C, (byte)0x56, (byte)0x16, (byte)0x77,
        (byte)0xF4, (byte)0xF9, (byte)0x44, (byte)0xB8
    };

    /** SM4对称密钥混淆后的密文数据（生产环境需要改成自己的，运行CommonKeyUtil.main()生成） */
    private static final byte[] SM4_KEY_DATA = {
        (byte)0x51, (byte)0x99, (byte)0x90, (byte)0x06, (byte)0x38, (byte)0x18, (byte)0xB4, (byte)0x8C, (byte)0x1D, (byte)0xE4,
        (byte)0x8C, (byte)0x2E, (byte)0x35, (byte)0x6E, (byte)0x1F, (byte)0xDD, (byte)0x96, (byte)0xF5, (byte)0x8D, (byte)0x1E,
        (byte)0x5A, (byte)0xB9, (byte)0x70, (byte)0xB0, (byte)0xDD, (byte)0xEC, (byte)0x01, (byte)0x2C, (byte)0xBF, (byte)0x46,
        (byte)0x2D, (byte)0x9D
    };
    /** SM4对称密钥混淆掩码，与SM4_KEY_DATA逐字节XOR可还原出密钥明文 */
    private static final byte[] SM4_KEY_MASK = {
        (byte)0x61, (byte)0xA8, (byte)0xA2, (byte)0x35, (byte)0x0C, (byte)0x2D, (byte)0x82, (byte)0xBB, (byte)0x25, (byte)0xDD,
        (byte)0xED, (byte)0x4C, (byte)0x56, (byte)0x0A, (byte)0x7A, (byte)0xBB, (byte)0xF0, (byte)0x90, (byte)0xE9, (byte)0x7D,
        (byte)0x38, (byte)0xD8, (byte)0x49, (byte)0x88, (byte)0xEA, (byte)0xDA, (byte)0x34, (byte)0x18, (byte)0x8C, (byte)0x74,
        (byte)0x1C, (byte)0xAD
    };

    /** SM4初始化向量IV混淆后的密文数据 */
    private static final byte[] SM4_IV_DATA = {
        (byte)0x1C, (byte)0xDB, (byte)0x85, (byte)0x32, (byte)0x42, (byte)0x38, (byte)0x33, (byte)0x1A, (byte)0x79, (byte)0x2B,
        (byte)0xB9, (byte)0xB3, (byte)0x30, (byte)0x92, (byte)0xCA, (byte)0xB7, (byte)0x51, (byte)0xE8, (byte)0xE1, (byte)0x89,
        (byte)0x0E, (byte)0xCA, (byte)0x9A, (byte)0x86, (byte)0x28, (byte)0xBB, (byte)0x78, (byte)0x75, (byte)0x5D, (byte)0x0B,
        (byte)0x84, (byte)0x02
    };
    /** SM4初始化向量IV混淆掩码，与SM4_IV_DATA逐字节XOR可还原出IV明文 */
    private static final byte[] SM4_IV_MASK = {
        (byte)0x7A, (byte)0xBE, (byte)0xE1, (byte)0x51, (byte)0x20, (byte)0x59, (byte)0x0A, (byte)0x22, (byte)0x4E, (byte)0x1D,
        (byte)0x8C, (byte)0x87, (byte)0x03, (byte)0xA0, (byte)0xFB, (byte)0x87, (byte)0x61, (byte)0xD9, (byte)0xD3, (byte)0xBA,
        (byte)0x3A, (byte)0xFF, (byte)0xAC, (byte)0xB1, (byte)0x10, (byte)0x82, (byte)0x19, (byte)0x17, (byte)0x3E, (byte)0x6F,
        (byte)0xE1, (byte)0x64
    };

    /** 运行时还原的密钥 */
    private static final String PRIVATE_KEY = CommonKeyUtil.reconstruct(PRIVATE_KEY_DATA, PRIVATE_KEY_MASK);
    private static final String KEY = CommonKeyUtil.reconstruct(SM4_KEY_DATA, SM4_KEY_MASK);
    private static final String IV = CommonKeyUtil.reconstruct(SM4_IV_DATA, SM4_IV_MASK);

    /** Hutool SM2 对象 */
    private static final SM2 sm2 = SmUtil.sm2(HexUtil.decodeHex(PRIVATE_KEY), HexUtil.decodeHex(PUBLIC_KEY));

    /**
     * 加密方法（Sm2 的专门针对前后端分离，非对称秘钥对的方式，暴露出去的公钥，对传输过程中的密码加个密）
     *
     * @author yubaoshan
     * @date 2022/9/15 21:51
     * @param str 待加密数据
     * @return 加密后的密文
     */
    public static String doSm2Encrypt(String str) {
        return sm2.encryptHex(str, KeyType.PublicKey);
    }

    /**
     * 解密方法
     * 如果采用加密机的方法，用try catch 捕捉异常，返回原文值即可
     *
     * @author yubaoshan
     * @date 2022/9/15 21:51
     * @param str 密文
     * @return 解密后的明文
     */
    public static String doSm2Decrypt(String str) {
        try {
            // 兼容性处理：Hutool要求密文以04开头（Uncompressed），如果前端传来的是不带04的（虽然sm-crypto默认带），则补上
            String text = str;
            if (StrUtil.isNotBlank(text) && !text.startsWith("04")) {
                text = "04" + text;
            }
            return sm2.decryptStr(text, KeyType.PrivateKey);
        } catch (Exception e) {
            // 降级处理：使用原版 antherd sm-crypto 解密（兼容性好但性能稍差）
            return Sm2.doDecrypt(str, PRIVATE_KEY);
        }
    }

    /**
     * 加密方法
     *
     * @author yubaoshan
     * @date 2022/9/15 21:51
     * @param str 待加密数据
     * @return 加密后的密文
     */
    public static String doSm4CbcEncrypt(String str) {
        // SM4 加密  cbc模式
        Sm4Options sm4Options4 = new Sm4Options();
        sm4Options4.setMode("cbc");
        sm4Options4.setIv(IV);
        return Sm4.encrypt(str, KEY, sm4Options4);
    }

    /**
     * 解密方法
     * 如果采用加密机的方法，用try catch 捕捉异常，返回原文值即可
     *
     * @author yubaoshan
     * @date 2022/9/15 21:51
     * @param str 密文
     * @return 解密后的明文
     */
    public static String doSm4CbcDecrypt(String str) {
        // 解密，cbc 模式，输出 utf8 字符串
        Sm4Options sm4Options8 = new Sm4Options();
        sm4Options8.setMode("cbc");
        sm4Options8.setIv(IV);
        String docString =  Sm4.decrypt(str, KEY, sm4Options8);
        if ("".equals(docString)) {
            log.warn(">>> 字段解密失败，返回原文值：{}", str);
            return str;
        } else {
            return docString;
        }
    }

    /**
     * 纯签名
     *
     * @author yubaoshan
     * @date 2022/9/15 21:51
     * @param str 待签名数据
     * @return 签名结果
     */
    public static String doSignature(String str) {
        return HexUtil.encodeHexStr(sm2.sign(StrUtil.utf8Bytes(str)));
    }

    /**
     * 验证签名结果
     *
     * @author yubaoshan
     * @date 2022/9/15 21:51
     * @param originalStr 签名原文数据
     * @param str 签名结果
     * @return 是否通过
     */
    public static boolean doVerifySignature(String originalStr, String str) {
        return sm2.verify(StrUtil.utf8Bytes(originalStr), HexUtil.decodeHex(str));
    }

    /**
     * 通过杂凑算法取得hash值，用于做数据完整性保护
     *
     * @author yubaoshan
     * @date 2022/9/15 21:51
     * @param str 字符串
     * @return hash 值
     */
    public static String doHashValue(String str) {
        return SmUtil.sm3(str);
    }
}
