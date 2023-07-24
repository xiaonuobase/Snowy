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

import com.antherd.smcrypto.sm2.Sm2;
import com.antherd.smcrypto.sm3.Sm3;
import com.antherd.smcrypto.sm4.Sm4;
import com.antherd.smcrypto.sm4.Sm4Options;
import lombok.extern.slf4j.Slf4j;

/**
 * 加密工具类，本框架目前使用 https://github.com/antherd/sm-crypto 项目中一些加解密方式
 * 使用小伙伴需要过等保密评相关，请在此处更改为自己的加密方法，或加密机，使用加密机同时需要替换公钥，私钥在内部无法导出，提供加密的方法
 * 如果不涉及到加密机方面的内容，请更改公私要为自己重新生成的，生成方式请看集成的sm-crypto主页
 *
 * @author yubaoshan
 * @date 2022/9/15 21:51
 */
@Slf4j
public class CommonCryptogramUtil {

    /** 公钥 */
    private static final String PUBLIC_KEY = "04298364ec840088475eae92a591e01284d1abefcda348b47eb324bb521bb03b0b2a5bc393f6b71dabb8f15c99a0050818b56b23f31743b93df9cf8948f15ddb54";

    /** 私钥 */
    private static final String PRIVATE_KEY = "3037723d47292171677ec8bd7dc9af696c7472bc5f251b2cec07e65fdef22e25";

    /** SM4的对称秘钥（生产环境需要改成自己使用的） 16 进制字符串，要求为 128 比特 */
    private static final String KEY = "0123456789abcdeffedcba9876543210";

    /**
     * 加密方法（Sm2 的专门针对前后端分离，非对称秘钥对的方式，暴露出去的公钥，对传输过程中的密码加个密）
     *
     * @author yubaoshan
     * @date 2022/9/15 21:51
     * @param str 待加密数据
     * @return 加密后的密文
     */
    public static String doSm2Encrypt(String str) {
        return Sm2.doEncrypt(str, PUBLIC_KEY);
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
        // 解密
        return Sm2.doDecrypt(str, PRIVATE_KEY);
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
        sm4Options4.setIv("fedcba98765432100123456789abcdef");
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
        sm4Options8.setIv("fedcba98765432100123456789abcdef");
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
        return Sm2.doSignature(str, PRIVATE_KEY);
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
        return Sm2.doVerifySignature(originalStr, str, PUBLIC_KEY);
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
        return Sm3.sm3(str);
    }
}
