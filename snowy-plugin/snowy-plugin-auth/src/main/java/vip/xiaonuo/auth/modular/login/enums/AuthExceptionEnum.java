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
package vip.xiaonuo.auth.modular.login.enums;

import lombok.Getter;

/**
 * 登录异常提示语枚举
 *
 * @author xuyuxiang
 * @date 2021/10/11 14:02
 **/
@Getter
public enum AuthExceptionEnum {

    /**
     * 验证码不能为空
     */
    VALID_CODE_EMPTY("验证码不能为空"),

    /**
     * 验证码请求号不能为空
     */
    VALID_CODE_REQ_NO_EMPTY("验证码请求号不能为空"),

    /**
     * 验证码错误
     */
    VALID_CODE_ERROR("验证码错误"),

    /**
     * 账号错误
     */
    ACCOUNT_ERROR("账号错误"),

    /**
     * 账号已停用
     */
    ACCOUNT_DISABLED("账号已停用"),

    /**
     * 密码错误
     */
    PWD_ERROR("密码错误"),

    /**
     * 手机号格式错误
     */
    PHONE_FORMAT_ERROR("手机号格式错误"),

    /**
     * 手机号不存在
     */
    PHONE_ERROR("手机号不存在"),

    /**
     * 客户端类型不能为空
     */
    CLIENT_TYPE_EMPTY("客户端类型不能为空"),

    /**
     * 客户端类型错误
     */
    CLIENT_TYPE_ERROR("客户端类型错误"),

    /**
     * 密码解密失败，请检查前端公钥
     */
    PWD_DECRYPT_ERROR("密码解密失败，请检查前端公钥");

    private final String value;

    AuthExceptionEnum(String value) {
        this.value = value;
    }
}
