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

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.dromara.sms4j.aliyun.config.AlibabaConfig;
import org.dromara.sms4j.api.SmsBlend;
import org.dromara.sms4j.api.dao.SmsDaoDefaultImpl;
import org.dromara.sms4j.api.entity.SmsResponse;
import org.dromara.sms4j.core.factory.SmsFactory;
import org.dromara.sms4j.javase.config.SEInitializer;
import org.dromara.sms4j.provider.config.SmsConfig;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.dev.api.DevConfigApi;

import java.util.LinkedHashMap;

/**
 * 阿里云短信工具类
 *
 * @author xuyuxiang
 * @date 2022/1/2 17:05
 */
@Slf4j
public class DevSmsAliyunUtil {

    private static SmsBlend smsBlend;

    private static final String SNOWY_SMS_ALIYUN_ACCESS_KEY_ID_KEY = "SNOWY_SMS_ALIYUN_ACCESS_KEY_ID";
    private static final String SNOWY_SMS_ALIYUN_ACCESS_KEY_SECRET_KEY = "SNOWY_SMS_ALIYUN_ACCESS_KEY_SECRET";
    private static final String SNOWY_SMS_ALIYUN_DEFAULT_SIGN_NAME_KEY = "SNOWY_SMS_ALIYUN_DEFAULT_SIGN_NAME";

    /**
     * 初始化操作的客户端
     *
     * @author xuyuxiang
     * @date 2022/1/5 23:24
     */
    private static void initClient(String signName) {

        DevConfigApi devConfigApi = SpringUtil.getBean(DevConfigApi.class);

        /* accessKeyId */
        String accessKeyId = devConfigApi.getValueByKey(SNOWY_SMS_ALIYUN_ACCESS_KEY_ID_KEY);

        if(ObjectUtil.isEmpty(accessKeyId)) {
            throw new CommonException("阿里云短信操作客户端未正确配置：accessKeyId为空");
        }

        /* accessKeySecret */
        String accessKeySecret = devConfigApi.getValueByKey(SNOWY_SMS_ALIYUN_ACCESS_KEY_SECRET_KEY);

        if(ObjectUtil.isEmpty(accessKeySecret)) {
            throw new CommonException("阿里云短信操作客户端未正确配置：accessKeySecret为空");
        }

        AlibabaConfig alibabaConfig = new AlibabaConfig();
        alibabaConfig.setConfigId(accessKeyId);
        alibabaConfig.setAccessKeyId(accessKeyId);
        alibabaConfig.setAccessKeySecret(accessKeySecret);
        alibabaConfig.setSignature(signName);
        SEInitializer.initializer().registerSmsDao(SmsDaoDefaultImpl.getInstance()).fromConfig(new SmsConfig(), CollectionUtil.newArrayList(alibabaConfig));
        smsBlend = SmsFactory.getSmsBlend(alibabaConfig.getConfigId());
    }

    /**
     * 发送模板短信
     *
     * @param phoneNumbers 手机号码，支持对多个手机号码发送短信，手机号码之间以半角逗号（,）分隔。
     * @param signName 短信签名，为空则使用默认签名
     * @param templateId 模板id
     * @param templateParam 短信模板变量对应的实际值，JSON格式。支持传入多个参数，示例：{"name":"张三","number":"15038****76"}
     * @return 发送的结果信息
     * @author xuyuxiang
     * @date 2022/2/24 13:42
     **/
    public static String sendSms(String phoneNumbers, String signName, String templateId, String templateParam) {
        LinkedHashMap<String, String> paramMap = new LinkedHashMap<>();
        JSONUtil.parseObj(templateParam).forEach((k, v) -> paramMap.put(k, Convert.toStr(v)));
        return sendSms(phoneNumbers, signName, templateId, paramMap);
    }

    /**
     * 发送模板短信
     *
     * @param phoneNumbers 手机号码，支持对多个手机号码发送短信，手机号码之间以半角逗号（,）分隔。
     * @param signName 短信签名，为空则使用默认签名
     * @param templateId 模板id
     * @param paramMap 短信参数，HashMap
     * @return 发送的结果信息
     * @author xuyuxiang
     * @date 2022/2/24 13:42
     **/
    public static String sendSms(String phoneNumbers, String signName, String templateId, LinkedHashMap<String, String> paramMap) {
        try {
            if(ObjectUtil.isEmpty(signName)) {
                signName = getDefaultSignName();
            }
            // 初始化客户端
            initClient(signName);
            // 发送短信
            SmsResponse smsResponse = smsBlend.massTexting(StrUtil.split(phoneNumbers, StrUtil.COMMA), templateId, paramMap);
            if(smsResponse.isSuccess()) {
                return JSONUtil.toJsonStr(smsResponse.getData());
            } else {
                String data = Convert.toStr(smsResponse.getData());
                if(JSONUtil.isTypeJSON(data)) {
                    JSONObject responseData = JSONUtil.parseObj(smsResponse.getData());
                    throw new CommonException(responseData.getStr("resInfo"));
                } else {
                    throw new CommonException(data);
                }
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
        String signName = devConfigApi.getValueByKey(SNOWY_SMS_ALIYUN_DEFAULT_SIGN_NAME_KEY);
        if(ObjectUtil.isEmpty(signName)) {
            throw new CommonException("阿里云短信操作客户端未正确配置：signName为空");
        }
        return signName;
    }
}
