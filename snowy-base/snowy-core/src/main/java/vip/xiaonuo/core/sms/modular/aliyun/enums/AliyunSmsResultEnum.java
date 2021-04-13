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
package vip.xiaonuo.core.sms.modular.aliyun.enums;


import lombok.Getter;

/**
 * 短信发送异常相关枚举
 *
 * @author xuyuxiang
 * @date 2018/1/4 22:40
 */
@Getter
public enum AliyunSmsResultEnum {


    /**
     * 初始化sms客户端错误，accessKey错误
     */
    INIT_SMS_CLIENT_ERROR("SMS_CLIENT_INIT_ERROR", "初始化sms客户端错误，accessKey错误"),

    /**
     * 请求参数为空
     */
    PARAM_NULL("NULL", "请求参数为空"),

    /**
     * 请求成功
     */
    OK("OK", "请求成功"),

    /**
     * RAM权限DENY
     */
    RAM_PERMISSION_DENY("isp.RAM_PERMISSION_DENY", "RAM权限DENY"),

    /**
     * 产品未开通
     */
    PRODUCT_UNSUBSCRIBE("isv.PRODUCT_UNSUBSCRIBE", "产品未开通"),

    /**
     * 账户不存在
     */
    ACCOUNT_NOT_EXISTS("isv.ACCOUNT_NOT_EXISTS", "账户不存在"),

    /**
     * 账户异常
     */
    ACCOUNT_ABNORMAL("isv.ACCOUNT_ABNORMAL", "账户异常"),

    /**
     * 短信模板不合法
     */
    SMS_TEMPLATE_ILLEGAL("isv.SMS_TEMPLATE_ILLEGAL", "短信模板不合法"),

    /**
     * 短信签名不合法
     */
    SMS_SIGNATURE_ILLEGAL("isv.SMS_SIGNATURE_ILLEGAL", "短信签名不合法"),

    /**
     * 参数异常
     */
    INVALID_PARAMETERS("isv.INVALID_PARAMETERS", "参数异常"),

    /**
     * 系统错误
     */
    SYSTEM_ERROR("isp.SYSTEM_ERROR", "系统错误"),

    /**
     * 非法手机号
     */
    MOBILE_NUMBER_ILLEGAL("isv.MOBILE_NUMBER_ILLEGAL", "非法手机号"),

    /**
     * 手机号码数量超过限制
     */
    MOBILE_COUNT_OVER_LIMIT("isv.MOBILE_COUNT_OVER_LIMIT", "手机号码数量超过限制"),

    /**
     * 模板缺少变量
     */
    TEMPLATE_MISSING_PARAMETERS("isv.TEMPLATE_MISSING_PARAMETERS", "模板缺少变量"),

    /**
     * 发送短信过于频繁，请稍后再试
     */
    BUSINESS_LIMIT_CONTROL("isv.BUSINESS_LIMIT_CONTROL", "发送短信过于频繁，请稍后再试"),

    /**
     * JSON参数不合法，只接受字符串值
     */
    INVALID_JSON_PARAM("isv.INVALID_JSON_PARAM", "JSON参数不合法，只接受字符串值"),

    /**
     * 黑名单管控
     */
    BLACK_KEY_CONTROL_LIMIT("isv.BLACK_KEY_CONTROL_LIMIT", "黑名单管控"),

    /**
     * 参数超出长度限制
     */
    PARAM_LENGTH_LIMIT("isv.PARAM_LENGTH_LIMIT", "参数超出长度限制"),

    /**
     * 不支持URL
     */
    PARAM_NOT_SUPPORT_URL("isv.PARAM_NOT_SUPPORT_URL", "不支持URL"),

    /**
     * 账户余额不足
     */
    AMOUNT_NOT_ENOUGH("isv.AMOUNT_NOT_ENOUGH", "账户余额不足");

    private String code;

    private final String message;

    AliyunSmsResultEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
