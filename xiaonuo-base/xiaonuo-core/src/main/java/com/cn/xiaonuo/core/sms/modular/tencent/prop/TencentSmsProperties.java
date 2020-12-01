package com.cn.xiaonuo.core.sms.modular.tencent.prop;

import lombok.Data;

/**
 * 腾讯云短信配置
 *
 * @author xuyuxiang
 * @date 2020/5/24 18:01
 */
@Data
public class TencentSmsProperties {

    /**
     * secretId
     */
    private String secretId;

    /**
     * secretKey
     */
    private String secretKey;

    /**
     * 应用控制台应用管理中的应用id
     * <p>
     * 在 [短信控制台] 添加应用后生成的实际 SDKAppID，例如1400006666
     * <p>
     * 短信控制台：https://console.cloud.tencent.com/smsv2
     */
    private String sdkAppId;

    /**
     * 签名，一般为中文名
     * <p>
     * 短信签名内容: 使用 UTF-8 编码，必须填写已审核通过的签名，可登录 [短信控制台] 查看签名信息
     */
    private String sign;

}
