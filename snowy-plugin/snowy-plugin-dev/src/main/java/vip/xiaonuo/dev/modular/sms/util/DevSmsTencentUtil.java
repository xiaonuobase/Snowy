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
package vip.xiaonuo.dev.modular.sms.util;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import com.tencentcloudapi.sms.v20210111.models.SendStatus;
import lombok.extern.slf4j.Slf4j;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.dev.api.DevConfigApi;

/**
 * 腾讯云短信工具类
 * 参考文档：https://cloud.tencent.com/document/product/382/43194
 *
 * @author xuyuxiang
 * @date 2022/1/2 17:05
 */
@Slf4j
public class DevSmsTencentUtil {

    private static SmsClient client;

    private static final String SNOWY_SMS_TENCENT_SECRET_ID_KEY = "SNOWY_SMS_TENCENT_SECRET_ID";
    private static final String SNOWY_SMS_TENCENT_SECRET_KEY_KEY = "SNOWY_SMS_TENCENT_SECRET_KEY";
    private static final String SNOWY_SMS_TENCENT_REGION_ID_KEY = "SNOWY_SMS_TENCENT_REGION_ID";
    private static final String SNOWY_SMS_TENCENT_DEFAULT_SDK_APP_ID_KEY = "SNOWY_SMS_TENCENT_DEFAULT_SDK_APP_ID";
    private static final String SNOWY_SMS_TENCENT_DEFAULT_SIGN_NAME_KEY = "SNOWY_SMS_TENCENT_DEFAULT_SIGN_NAME";

    /**
     * 初始化操作的客户端
     *
     * @author xuyuxiang
     * @date 2022/1/5 23:24
     */
    private static void initClient() {

        DevConfigApi devConfigApi = SpringUtil.getBean(DevConfigApi.class);

        /* secretId */
        String secretId = devConfigApi.getValueByKey(SNOWY_SMS_TENCENT_SECRET_ID_KEY);

        if(ObjectUtil.isEmpty(secretId)) {
            throw new CommonException("腾讯云短信操作客户端未正确配置：secretId为空");
        }

        /* secretKey */
        String secretKey = devConfigApi.getValueByKey(SNOWY_SMS_TENCENT_SECRET_KEY_KEY);

        if(ObjectUtil.isEmpty(secretKey)) {
            throw new CommonException("腾讯云短信操作客户端未正确配置：secretKey为空");
        }

        /* regionId */
        String regionId = devConfigApi.getValueByKey(SNOWY_SMS_TENCENT_REGION_ID_KEY);

        if(ObjectUtil.isEmpty(regionId)) {
            throw new CommonException("腾讯云短信操作客户端未正确配置：regionId为空");
        }

        client = new SmsClient(new Credential(secretId, secretKey), regionId);
    }

    /**
     * 发送短信
     *
     * @param sdkAppId 短信 SdkAppId，在 短信控制台 添加应用后生成的实际 SdkAppId，示例如1400006666。
     *                 可前往 [短信控制台](https://console.cloud.tencent.com/smsv2/app-manage) 查看
     * @param phoneNumbers 手机号码，支持对多个手机号码发送短信，手机号码之间以半角逗号（,）分隔。
     *                     上限为1000个手机号码。批量调用相对于单条调用及时性稍有延迟。
     * @param signName 短信服务控制台配置且审核通过的短信签名，为空则使用默认签名
     * @param templateCode 短信服务控制台配置且审核通过的模板编码
     * @param templateParam 短信模板变量对应的顺序。支持传入多个参数，逗号拼接，示例："张三,15038****76,进行中"
     * @return 发送的结果信息集合 com.tencentcloudapi.sms.v20210111.models.SendStatus
     * @author xuyuxiang
     * @date 2022/2/24 13:42
     **/
    public static String sendSms(String sdkAppId, String phoneNumbers, String signName, String templateCode, String templateParam) {
        try {
            initClient();
            if(ObjectUtil.isEmpty(sdkAppId)) {
                sdkAppId = getDefaultSdkAppId();
            }
            if(ObjectUtil.isEmpty(signName)) {
                signName = getDefaultSignName();
            }
            SendSmsRequest sendSmsRequest = new SendSmsRequest();
            sendSmsRequest.setSmsSdkAppId(sdkAppId);
            sendSmsRequest.setPhoneNumberSet(StrUtil.splitToArray(phoneNumbers, StrUtil.COMMA));
            sendSmsRequest.setSignName(signName);
            sendSmsRequest.setTemplateId(templateCode);
            sendSmsRequest.setTemplateParamSet(ObjectUtil.isNotEmpty(templateParam)?StrUtil.splitToArray(templateParam, StrUtil.COMMA):null);
            SendSmsResponse sendSmsResponse = client.SendSms(sendSmsRequest);
            SendStatus sendStatus = sendSmsResponse.getSendStatusSet()[0];
            String code = sendStatus.getCode().toLowerCase();
            if("ok".equals(code)) {
                return JSONUtil.toJsonStr(sendSmsResponse);
            } else {
                throw new CommonException(sendStatus.getMessage());
            }
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 获取默认签名
     *
     * @author xuyuxiang
     * @date 2024/1/26 16:40
     **/
    public static String getDefaultSignName() {
        // 签名为空，则获取默认签名
        DevConfigApi devConfigApi = SpringUtil.getBean(DevConfigApi.class);
        String signName = devConfigApi.getValueByKey(SNOWY_SMS_TENCENT_DEFAULT_SIGN_NAME_KEY);
        if(ObjectUtil.isEmpty(signName)) {
            throw new CommonException("腾讯云短信操作客户端未正确配置：signName为空");
        }
        return signName;
    }

    /**
     * 获取默认sdkAppId
     *
     * @author xuyuxiang
     * @date 2024/1/26 16:40
     **/
    public static String getDefaultSdkAppId() {
        // sdkAppId为空，则获取默认sdkAppId
        DevConfigApi devConfigApi = SpringUtil.getBean(DevConfigApi.class);
        String sdkAppId = devConfigApi.getValueByKey(SNOWY_SMS_TENCENT_DEFAULT_SDK_APP_ID_KEY);
        if(ObjectUtil.isEmpty(sdkAppId)) {
            throw new CommonException("腾讯云短信操作客户端未正确配置：sdkAppId为空");
        }
        return sdkAppId;
    }
}
