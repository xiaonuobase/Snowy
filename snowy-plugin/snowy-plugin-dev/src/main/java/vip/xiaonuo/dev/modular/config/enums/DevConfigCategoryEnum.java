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
package vip.xiaonuo.dev.modular.config.enums;

import lombok.Getter;
import vip.xiaonuo.common.exception.CommonException;

/**
 * 配置分类枚举
 *
 * @author xuyuxiang
 * @date 2022/7/6 22:21
 */
@Getter
public enum DevConfigCategoryEnum {

    /**
     * 系统基础
     */
    SYS_BASE("SYS_BASE"),

    /**
     * 业务定义
     */
    BIZ_DEFINE("BIZ_DEFINE"),

    /**
     * 三方登录-码云
     */
    THIRD_GITEE("THIRD_GITEE"),

    /**
     * 三方登录-微信
     */
    THIRD_WECHAT("THIRD_WECHAT"),

    /**
     * 文件-本地
     */
    FILE_LOCAL("FILE_LOCAL"),

    /**
     * 文件-腾讯云
     */
    FILE_TENCENT("FILE_TENCENT"),

    /**
     * 文件-阿里云
     */
    FILE_ALIYUN("FILE_ALIYUN"),

    /**
     * 文件-MINIO
     */
    FILE_MINIO("FILE_MINIO"),

    /**
     * 邮件-本地
     */
    EMAIL_LOCAL("EMAIL_LOCAL"),

    /**
     * 邮件-腾讯云
     */
    EMAIL_TENCENT("EMAIL_TENCENT"),

    /**
     * 邮件-阿里云
     */
    EMAIL_ALIYUN("EMAIL_ALIYUN"),

    /**
     * 短信-腾讯云
     */
    SMS_TENCENT("SMS_TENCENT"),

    /**
     * 短信-阿里云
     */
    SMS_ALIYUN("SMS_ALIYUN"),

    /**
     * 短信-小诺短信
     */
    SMS_XIAONUO("SMS_XIAONUO");

    private final String value;

    DevConfigCategoryEnum(String value) {
        this.value = value;
    }

    public static void validate(String value) {
        boolean flag = SYS_BASE.getValue().equals(value) || BIZ_DEFINE.getValue().equals(value) ||
                THIRD_GITEE.getValue().equals(value) || THIRD_WECHAT.getValue().equals(value) ||
                FILE_LOCAL.getValue().equals(value) || FILE_TENCENT.getValue().equals(value) ||
                FILE_ALIYUN.getValue().equals(value) || FILE_MINIO.getValue().equals(value) ||
                EMAIL_TENCENT.getValue().equals(value) || EMAIL_ALIYUN.getValue().equals(value) ||
                SMS_TENCENT.getValue().equals(value) || SMS_ALIYUN.getValue().equals(value) ||
                SMS_XIAONUO.getValue().equals(value);
        if(!flag) {
            throw new CommonException("不支持的配置分类：{}", value);
        }
    }
}
