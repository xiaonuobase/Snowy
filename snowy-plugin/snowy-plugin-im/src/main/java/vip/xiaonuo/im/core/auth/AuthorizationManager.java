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
package vip.xiaonuo.im.core.auth;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.antherd.smcrypto.sm2.Sm2;

import static vip.xiaonuo.im.core.config.WebSocketConfig.b;
import static vip.xiaonuo.im.core.manager.WebSocketSessionManager.a;

/**
 * 授权管理器
 *
 * @author chengchuanyao
 * @date 2024/8/6 9:23
 */
public class AuthorizationManager {

    private static String AUTH_CODE = null;

    public static Boolean AUTH_FLAG = null;


    static {
        String authCode = SpringUtil.getApplicationContext().getEnvironment().getProperty("snowy.im.auth.code");
        if (ObjectUtil.isNotNull(authCode) && authCode.indexOf("-") >= 0) {
            AUTH_CODE = authCode;
        }
    }

    /**
     * 单例模式进行验签判断
     */
    public static boolean verifySign() {
        if (AUTH_CODE == null) {
            return false;
        }
        if (AUTH_FLAG == null && ObjectUtil.isNotNull(AUTH_CODE) && AUTH_CODE.indexOf("-") >= 0) {
            String[] split = AUTH_CODE.split("-");
            String str = split[0];
            String sign = split[1];
            AUTH_FLAG = Sm2.doVerifySignature(str, sign, a + b + c);
        }
        return AUTH_FLAG;
    }

    public final static String c = "346150e54c2f4ee5febab66aa9dda2";

}
