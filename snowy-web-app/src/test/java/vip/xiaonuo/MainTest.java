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
package vip.xiaonuo;

import com.antherd.smcrypto.sm2.Sm2;
import com.baomidou.dynamic.datasource.toolkit.CryptoUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 主测试类
 *
 * @author xuyuxiang
 * @date 2022/9/17 17:09
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class MainTest {

    private final static String publicKey = "04269d089bf57ff2e9242a5692f1a79970616bbff5ccfe5f3b52b7d0cd1479791e4c722eb6f6813ead82a7777bc947a0a61c346150e54c2f4ee5febab66aa9dda2";

    private final static String privateKey = "00df47e8486e6eb98a345a6dad46bfa6119bf1d77e295660c2a4fa449af3642ba2";

    @Test
    public void test() throws Exception {
        String root = CryptoUtils.encrypt("root");
        System.out.println(root);
    }

    public static void main(String[] args) {
        String s = Sm2.doEncrypt("IM-202408141024", publicKey);
        String s1 = Sm2.doSignature(s, privateKey);
        System.out.println(s + '-' + s1);
    }

}
