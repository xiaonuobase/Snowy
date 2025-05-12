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
package vip.xiaonuo.dev.api;

import cn.hutool.json.JSONObject;

/**
 * 短信API
 *
 * @author xuyuxiang
 * @date 2022/6/22 15:22
 **/
public interface DevSmsApi {

    /**
     * 动态发送短信（使用系统配置的默认短信引擎）
     *
     * @param phoneNumbers 手机号
     * @param templateCodeOrId 模板id或编码
     * @param paramMap 发送参数
     * @author xuyuxiang
     * @date 2022/2/7 22:29
     */
    void sendDynamicSms(String phoneNumbers, String templateCodeOrId, JSONObject paramMap);

    /* =========阿里云短信========= */

    /**
     * 发送短信
     *
     * @param phoneNumbers 手机号码，支持对多个手机号码发送短信，手机号码之间以半角逗号（,）分隔。
     *                     上限为1000个手机号码。批量调用相对于单条调用及时性稍有延迟。
     * @param signName 短信服务控制台配置且审核通过的短信签名
     * @param templateCode 短信服务控制台配置且审核通过的模板编码
     * @param templateParam 短信模板变量对应的实际值，JSON格式。支持传入多个参数，示例：{"name":"张三","number":"15038****76"}
     * @author xuyuxiang
     * @date 2022/2/24 13:42
     **/
    void sendSmsAliyun(String phoneNumbers, String signName, String templateCode, String templateParam);

    /* =========腾讯云短信========= */

    /**
     * 发送短信
     *
     * @param phoneNumbers 手机号码，支持对多个手机号码发送短信，手机号码之间以半角逗号（,）分隔。
     *                     上限为1000个手机号码。批量调用相对于单条调用及时性稍有延迟。
     * @param signName 短信服务控制台配置且审核通过的短信签名
     * @param templateCode 短信服务控制台配置且审核通过的模板编码
     * @param templateParam 短信模板变量对应的顺序。支持传入多个参数，逗号拼接，示例："张三,15038****76,进行中"}
     * @author xuyuxiang
     * @date 2022/2/24 13:42
     **/
    void sendSmsTencent(String phoneNumbers, String signName, String templateCode, String templateParam);

    /* =========小诺短信========= */

    /**
     * 发送短信
     *
     * @param phoneNumbers 手机号码，支持对多个手机号码发送短信，手机号码之间以半角逗号（,）分隔。
     *                     上限为1000个手机号码。批量调用相对于单条调用及时性稍有延迟。
     * @param signName 短信服务控制台配置且审核通过的短信签名
     * @param message 短信内容，发送时编写好的整条短信内容，不带签名【】
     * @author yubaoshan
     * @date 2024/5/20 12:00
     **/
    void sendSmsXiaonuo(String phoneNumbers, String signName, String message);
}
