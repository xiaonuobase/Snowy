/*
Copyright [2020] [https://www.xiaonuo.vip]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：

1.请不要删除和修改根目录下的LICENSE文件。
2.请不要删除和修改Snowy源码头部的版权声明。
3.请保留源码和相关描述文件的项目出处，作者声明等。
4.分发源码时候，请注明软件出处 https://gitee.com/xiaonuobase/snowy
5.在修改包名，模块名称，项目代码等时，请注明软件出处 https://gitee.com/xiaonuobase/snowy
6.若您的项目无法满足以上几点，可申请商业授权，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package vip.xiaonuo.core.util;

import cn.hutool.log.Log;
import com.antherd.smcrypto.sm2.Sm2;
import com.antherd.smcrypto.sm3.Sm3;
import com.antherd.smcrypto.sm4.Sm4;
import com.antherd.smcrypto.sm4.Sm4Options;
import vip.xiaonuo.core.cryptogram.keypair;

/**
 * 加密工具类，本框架目前使用 https://github.com/antherd/sm-crypto 项目中一些加解密方式
 * 使用小伙伴需要过等保密评相关，请在此处更改为自己的加密方法，或加密机，使用加密机同时需要替换公钥，私钥在内部无法导出，提供加密的方法
 *
 * @author yubaoshan
 */
public class CryptogramUtil {

    private static final Log log = Log.get();

    /**
     * 加密方法（Sm2 的专门针对前后端分离，非对称秘钥对的方式，暴露出去的公钥，对传输过程中的密码加个密）
     *
     * @author yubaoshan
     * @param str 待加密数据
     * @return 加密后的密文
     */
    public static String doSm2Encrypt (String str) {
        return Sm2.doEncrypt(str, keypair.PUBLIC_KEY);
    }

    /**
     * 解密方法
     * 如果采用加密机的方法，用try catch 捕捉异常，返回原文值即可
     *
     * @author yubaoshan
     * @param str 密文
     * @return 解密后的明文
     */
    public static String doSm2Decrypt (String str) {
        // 解密
        return Sm2.doDecrypt(str, keypair.PRIVATE_KEY);
    }

    /**
     * 加密方法
     *
     * @author yubaoshan
     * @param str 待加密数据
     * @return 加密后的密文
     */
    public static String doEncrypt (String str) {
        // SM4 加密  cbc模式
        Sm4Options sm4Options4 = new Sm4Options();
        sm4Options4.setMode("cbc");
        sm4Options4.setIv("fedcba98765432100123456789abcdef");
        return Sm4.encrypt(str, keypair.KEY, sm4Options4);
    }

    /**
     * 解密方法
     * 如果采用加密机的方法，用try catch 捕捉异常，返回原文值即可
     *
     * @author yubaoshan
     * @param str 密文
     * @return 解密后的明文
     */
    public static String doDecrypt (String str) {
        // 解密，cbc 模式，输出 utf8 字符串
        Sm4Options sm4Options8 = new Sm4Options();
        sm4Options8.setMode("cbc");
        sm4Options8.setIv("fedcba98765432100123456789abcdef");
        String docString =  Sm4.decrypt(str, keypair.KEY, sm4Options8);
        if (docString.equals("")) {
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
     * @param str 待签名数据
     * @return 签名结果
     */
    public static String doSignature (String str) {
        return Sm2.doSignature(str, keypair.PRIVATE_KEY);
    }

    /**
     * 验证签名结果
     *
     * @author yubaoshan
     * @param originalStr 签名原文数据
     * @param str 签名结果
     * @return 是否通过
     */
    public static boolean doVerifySignature (String originalStr, String str) {
        return Sm2.doVerifySignature(originalStr, str, keypair.PUBLIC_KEY);
    }

    /**
     * 通过杂凑算法取得hash值，用于做数据完整性保护
     *
     * @author yubaoshan
     * @param str 字符串
     * @return hash 值
     */
    public static String doHashValue (String str) {
        return Sm3.sm3(str);
    }

}
