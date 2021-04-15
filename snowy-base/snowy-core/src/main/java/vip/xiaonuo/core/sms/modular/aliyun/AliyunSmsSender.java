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
package vip.xiaonuo.core.sms.modular.aliyun;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import lombok.extern.slf4j.Slf4j;
import vip.xiaonuo.core.sms.SmsSender;
import vip.xiaonuo.core.sms.modular.aliyun.enums.AliyunSmsResultEnum;
import vip.xiaonuo.core.sms.modular.aliyun.exp.AliyunSmsException;
import vip.xiaonuo.core.sms.modular.aliyun.msign.MultiSignManager;
import vip.xiaonuo.core.sms.modular.aliyun.prop.AliyunSmsProperties;

import java.util.Map;

/**
 * 阿里云短信发送服务
 *
 * @author xuyuxiang
 * @date 2018-07-06-下午2:15
 */
@Slf4j
public class AliyunSmsSender implements SmsSender {

    private final MultiSignManager multiSignManager;

    private final AliyunSmsProperties aliyunSmsProperties;

    public AliyunSmsSender(MultiSignManager multiSignManager, AliyunSmsProperties aliyunSmsProperties) {
        this.multiSignManager = multiSignManager;
        this.aliyunSmsProperties = aliyunSmsProperties;
    }

    @Override
    public void sendSms(String phone, String templateCode, Map<String, Object> params) {

        log.info(">>> 开始发送阿里云短信，手机号是：" + phone + ",模板号是：" + templateCode + ",参数是：" + params);

        // 检验参数是否合法
        assertSendSmsParams(phone, templateCode, params, aliyunSmsProperties);

        // 初始化client profile
        IAcsClient iAcsClient = initClient();

        // 组装请求对象
        JSONObject smsRes = createSmsRequest(phone, templateCode, params, iAcsClient);

        // 如果返回ok则发送成功
        if (!AliyunSmsResultEnum.OK.getCode().equals(smsRes.getString("Code"))) {

            // 返回其他状态码根据情况抛出业务异常
            String code = AliyunSmsResultEnum.SYSTEM_ERROR.getCode();
            String errorMessage = AliyunSmsResultEnum.SYSTEM_ERROR.getMessage();
            for (AliyunSmsResultEnum smsExceptionEnum : AliyunSmsResultEnum.values()) {
                if (smsExceptionEnum.getCode().equals(smsRes.getString("Code"))) {
                    code = smsExceptionEnum.getCode();
                    errorMessage = smsExceptionEnum.getMessage();
                }
            }
            log.error(">>> 发送短信异常！code = " + code + ",message = " + errorMessage);
            throw new AliyunSmsException(code, errorMessage);
        }
    }

    /**
     * 初始化短信发送的客户端
     *
     * @author xuyuxiang
     * @date 2018/7/6 下午3:57
     */
    private IAcsClient initClient() {
        final String accessKeyId = aliyunSmsProperties.getAccessKeyId();
        final String accessKeySecret = aliyunSmsProperties.getAccessKeySecret();

        // 创建DefaultAcsClient实例并初始化
        DefaultProfile profile = DefaultProfile.getProfile(aliyunSmsProperties.getRegionId(), accessKeyId, accessKeySecret);
        return new DefaultAcsClient(profile);
    }

    /**
     * 组装请求对象
     *
     * @author xuyuxiang
     * @date 2018/7/6 下午3:00
     */
    private JSONObject createSmsRequest(String phoneNumber, String templateCode, Map<String, Object> params, IAcsClient acsClient) {
        CommonRequest request = new CommonRequest();
        request.setSysDomain(aliyunSmsProperties.getSmsDomain());
        request.setSysVersion(aliyunSmsProperties.getSmsVersion());
        request.setSysAction(aliyunSmsProperties.getSmsSendAction());

        // 接收短信的手机号码
        request.putQueryParameter("PhoneNumbers", phoneNumber);

        // 短信签名名称。请在控制台签名管理页面签名名称一列查看（必须是已添加、并通过审核的短信签名）。
        request.putQueryParameter("SignName", this.getSmsSign(phoneNumber));

        // 短信模板ID
        request.putQueryParameter("TemplateCode", templateCode);

        // 短信模板变量对应的实际值，JSON格式。
        request.putQueryParameter("TemplateParam", JSON.toJSONString(params));

        //请求失败这里会抛ClientException异常
        CommonResponse commonResponse;
        try {
            commonResponse = acsClient.getCommonResponse(request);
            String data = commonResponse.getData();
            String jsonResult = data.replaceAll("'\'", "");
            log.info(">>> 获取到发送短信的响应结果！{}", jsonResult);
            return JSON.parseObject(jsonResult);
        } catch (ClientException e) {
            log.error(">>> 初始化阿里云sms异常！可能是accessKey和secret错误！", e);
            throw new AliyunSmsException(AliyunSmsResultEnum.INIT_SMS_CLIENT_ERROR.getCode(),
                    AliyunSmsResultEnum.INIT_SMS_CLIENT_ERROR.getMessage());
        }
    }

    /**
     * 校验发送短信的参数是否正确
     *
     * @author xuyuxiang
     * @date 2018/7/6 下午3:19
     */
    private void assertSendSmsParams(String phoneNumber, String templateCode, Map<String, Object> params,
                                     AliyunSmsProperties aliyunSmsProperties) {
        if (ObjectUtil.hasEmpty(phoneNumber, templateCode, params, aliyunSmsProperties)) {
            log.error(">>> 阿里云短信发送异常！请求参数存在空！");
            throw new AliyunSmsException(AliyunSmsResultEnum.PARAM_NULL.getCode(), AliyunSmsResultEnum.PARAM_NULL.getMessage());
        }
    }

    /**
     * 获取sms发送的sign标识，参数phone是发送的手机号码
     *
     * @author xuyuxiang
     * @date 2018/8/13 21:23
     */
    private String getSmsSign(String phone) {
        String signName = aliyunSmsProperties.getSignName();

        // 如果是单个签名就用一个签名发
        if (!signName.contains(",")) {
            log.info(">>> 发送短信，签名为：" + signName + ",电话为：" + phone);
            return signName;
        } else {
            return multiSignManager.getSign(phone, signName);
        }
    }

}
