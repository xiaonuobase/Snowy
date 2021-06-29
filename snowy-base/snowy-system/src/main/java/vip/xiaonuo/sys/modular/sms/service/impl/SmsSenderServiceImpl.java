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
package vip.xiaonuo.sys.modular.sms.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.log.Log;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.core.consts.CommonConstant;
import vip.xiaonuo.core.sms.SmsSender;
import vip.xiaonuo.sys.modular.sms.enums.SmsSendStatusEnum;
import vip.xiaonuo.sys.modular.sms.enums.SmsTypeEnum;
import vip.xiaonuo.sys.modular.sms.enums.SmsVerifyEnum;
import vip.xiaonuo.sys.modular.sms.param.SysSmsSendParam;
import vip.xiaonuo.sys.modular.sms.param.SysSmsVerifyParam;
import vip.xiaonuo.sys.modular.sms.service.SmsSenderService;
import vip.xiaonuo.sys.modular.sms.service.SysSmsInfoService;

import javax.annotation.Resource;
import java.util.Map;


/**
 * 短信发送接口实现类
 *
 * @author yubaoshan
 * @date 2018/7/5 21:05
 */
@Component
public class SmsSenderServiceImpl implements SmsSenderService {

    private static final Log log = Log.get();

    @Resource
    private SmsSender smsSender;

    @Resource
    private SysSmsInfoService sysSmsInfoService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean sendShortMessage(SysSmsSendParam sysSmsSendParam) {

        Map<String, Object> params = sysSmsSendParam.getParams();
        log.info(">>> 发送短信Provider接口，参数为：" + JSON.toJSONString(sysSmsSendParam));

        //如果是纯消息发送，直接发送
        if (SmsTypeEnum.MESSAGE.equals(sysSmsSendParam.getSmsTypeEnum())) {
            smsSender.sendSms(sysSmsSendParam.getPhoneNumbers(), sysSmsSendParam.getTemplateCode(), params);
            log.info(">>> 发送短信Provider接口--message，params的map具体为：" + JSON.toJSONString(params));
        } else {

            //如果是验证类消息发送，需要存储到数据库验证码信息
            String validateCode;

            //如果传的参数中没有code，就初始化一个code放到参数map里
            if (params != null && params.get(CommonConstant.CODE) != null) {
                validateCode = params.get(CommonConstant.CODE).toString();
            } else {
                validateCode = RandomUtil.randomNumbers(6);
                if (params == null) {
                    params = CollectionUtil.newHashMap();
                }
                params.put(CommonConstant.CODE, validateCode);
            }

            log.info(">>> 发送短信Provider接口，params的map具体为：" + JSON.toJSONString(params));
            log.info(">>> 发送短信Provider接口，验证码为：" + validateCode);

            //存储短信到数据库
            Long smsId = sysSmsInfoService.saveSmsInfo(sysSmsSendParam, validateCode);

            log.info(">>> 开始发送短信：发送的电话号码= " + sysSmsSendParam.getPhoneNumbers() + ",发送的模板号=" + sysSmsSendParam.getTemplateCode() + "，发送的参数是：" + JSON.toJSONString(params));

            //发送短信
            smsSender.sendSms(sysSmsSendParam.getPhoneNumbers(), sysSmsSendParam.getTemplateCode(), params);

            //更新短信发送状态
            sysSmsInfoService.updateSmsInfo(smsId, SmsSendStatusEnum.SUCCESS);
        }

        return true;
    }

    @Override
    public SmsVerifyEnum verifyShortMessage(SysSmsVerifyParam sysSmsVerifyParam) {
        log.info(">>> 验证短信Provider接口，参数为：" + JSON.toJSONString(sysSmsVerifyParam));
        SmsVerifyEnum smsVerifyEnum = sysSmsInfoService.validateSmsInfo(sysSmsVerifyParam);
        log.info(">>> 验证短信Provider接口，响应结果为：" + JSON.toJSONString(smsVerifyEnum));
        return smsVerifyEnum;
    }

    @Override
    public SmsSendStatusEnum getMessageSendStatus(Integer smsId) {
        return null;
    }

}
