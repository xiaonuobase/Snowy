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
package vip.xiaonuo.auth.modular.third.enums;

import lombok.Getter;
import vip.xiaonuo.common.exception.CommonException;

/**
 * 第三方登录平台枚举
 *
 * @author xuyuxiang
 * @date 2021/10/11 14:02
 **/
@Getter
public enum AuthThirdPlatformEnum {

    /**
     * IAM
     */
    IAM("IAM"),

    /**
     * 钉钉
     */
    DINGTALK("DINGTALK"),

    /**
     * 企业微信
     */
    WORKWECHAT("WORKWECHAT"),

    /**
     * 飞书
     */
    FEISHU("FEISHU"),

    /**
     * WeLink
     */
    WELINK("WELINK"),

    /**
     * 云之家
     */
    YUNZHIJIA("YUNZHIJIA"),

    /**
     * QQ
     */
    QQ("QQ"),

    /**
     * 微信
     */
    WECHAT("WECHAT"),

    /**
     * 微博
     */
    WEIBO("WEIBO"),

    /**
     * 抖音
     */
    DOUYIN("DOUYIN"),

    /**
     * 支付宝
     */
    ALIPAY("ALIPAY");

    private final String value;

    AuthThirdPlatformEnum(String value) {
        this.value = value;
    }

    public static void validate(String value) {
        boolean flag = IAM.getValue().equals(value) ||
                DINGTALK.getValue().equals(value) ||
                WORKWECHAT.getValue().equals(value) ||
                FEISHU.getValue().equals(value) ||
                WELINK.getValue().equals(value) ||
                YUNZHIJIA.getValue().equals(value) ||
                QQ.getValue().equals(value) ||
                WECHAT.getValue().equals(value) ||
                WEIBO.getValue().equals(value) ||
                DOUYIN.getValue().equals(value) ||
                ALIPAY.getValue().equals(value);
        if(!flag) {
            throw new CommonException("不支持的第三方平台：{}", value);
        }
    }
}
