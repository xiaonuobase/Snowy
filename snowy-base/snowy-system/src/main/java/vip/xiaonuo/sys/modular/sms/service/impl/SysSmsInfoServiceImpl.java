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

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.log.Log;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.core.context.constant.ConstantContextHolder;
import vip.xiaonuo.core.exception.ServiceException;
import vip.xiaonuo.core.factory.PageFactory;
import vip.xiaonuo.core.pojo.page.PageResult;
import vip.xiaonuo.core.pojo.sms.AliyunSmsConfigs;
import vip.xiaonuo.sys.modular.sms.entity.SysSms;
import vip.xiaonuo.sys.modular.sms.enums.SmsSendExceptionEnum;
import vip.xiaonuo.sys.modular.sms.enums.SmsSendStatusEnum;
import vip.xiaonuo.sys.modular.sms.enums.SmsVerifyEnum;
import vip.xiaonuo.sys.modular.sms.mapper.SysSmsMapper;
import vip.xiaonuo.sys.modular.sms.param.SysSmsInfoParam;
import vip.xiaonuo.sys.modular.sms.param.SysSmsSendParam;
import vip.xiaonuo.sys.modular.sms.param.SysSmsVerifyParam;
import vip.xiaonuo.sys.modular.sms.service.SysSmsInfoService;

import java.util.Date;
import java.util.List;

/**
 * 系统短信接口实现类
 *
 * @author yubaoshan
 * @date 2018/7/5 13:44
 */

@Service
public class SysSmsInfoServiceImpl extends ServiceImpl<SysSmsMapper, SysSms> implements SysSmsInfoService {

    private static final Log log = Log.get();

    @Override
    public Long saveSmsInfo(SysSmsSendParam sysSmsSendParam, String validateCode) {

        if (ObjectUtil.isEmpty(sysSmsSendParam.getPhoneNumbers())) {
            throw new ServiceException(SmsSendExceptionEnum.PHONE_NUMBER_EMPTY);
        }
        if (ObjectUtil.isEmpty(validateCode)) {
            throw new ServiceException(SmsSendExceptionEnum.VALIDATE_CODE_EMPTY);
        }
        if (ObjectUtil.isEmpty(sysSmsSendParam.getTemplateCode())) {
            throw new ServiceException(SmsSendExceptionEnum.TEMPLATE_CODE_EMPTY);
        }

        //当前时间
        Date nowDate = new Date();

        //短信失效时间
        AliyunSmsConfigs aliyunSmsProperties = ConstantContextHolder.getAliyunSmsConfigs();
        long invalidateTime = nowDate.getTime() + aliyunSmsProperties.getInvalidateMinutes() * 60 * 1000;
        Date invalidate = new Date(invalidateTime);

        SysSms sysSms = new SysSms();
        sysSms.setCreateTime(nowDate);
        sysSms.setInvalidTime(invalidate);
        sysSms.setPhoneNumbers(sysSmsSendParam.getPhoneNumbers());
        sysSms.setStatus(SmsSendStatusEnum.WAITING.getCode());
        sysSms.setSource(sysSmsSendParam.getSmsSendSourceEnum().getCode());
        sysSms.setTemplateCode(sysSmsSendParam.getTemplateCode());
        sysSms.setValidateCode(validateCode);

        this.save(sysSms);

        log.info(">>> 发送短信，存储短信到数据库，数据为：" + JSON.toJSONString(sysSms));

        return sysSms.getId();
    }

    @Override
    public void updateSmsInfo(Long smsId, SmsSendStatusEnum smsSendStatusEnum) {
        SysSms sysSms = this.getById(smsId);
        sysSms.setStatus(smsSendStatusEnum.getCode());
        this.updateById(sysSms);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public SmsVerifyEnum validateSmsInfo(SysSmsVerifyParam sysSmsVerifyParam) {

        if (ObjectUtil.isEmpty(sysSmsVerifyParam.getPhoneNumbers())) {
            throw new ServiceException(SmsSendExceptionEnum.PHONE_NUMBER_EMPTY);
        }
        if (ObjectUtil.isEmpty(sysSmsVerifyParam)) {
            throw new ServiceException(SmsSendExceptionEnum.VALIDATE_CODE_EMPTY);
        }
        if (ObjectUtil.isEmpty(sysSmsVerifyParam.getTemplateCode())) {
            throw new ServiceException(SmsSendExceptionEnum.TEMPLATE_CODE_EMPTY);
        }

        //查询有没有这条记录
        LambdaQueryWrapper<SysSms> smsQueryWrapper = new LambdaQueryWrapper<>();

        smsQueryWrapper.eq(SysSms::getPhoneNumbers, sysSmsVerifyParam.getPhoneNumbers())
                .and(f -> f.eq(SysSms::getSource, sysSmsVerifyParam.getSmsSendSourceEnum().getCode()))
                .and(f -> f.eq(SysSms::getTemplateCode, sysSmsVerifyParam.getTemplateCode()));

        smsQueryWrapper.orderByDesc(SysSms::getCreateTime);

        List<SysSms> sysSmsList = this.list(smsQueryWrapper);

        log.info(">>> 验证短信Provider接口，查询到sms记录：" + JSON.toJSONString(sysSmsList));

        //如果找不到记录，提示验证失败
        if (ObjectUtil.isEmpty(sysSmsList)) {
            log.info(">>> 验证短信Provider接口，找不到验证码记录，响应验证失败！");
            return SmsVerifyEnum.ERROR;
        } else {

            //获取最近发送的第一条
            SysSms sysSms = sysSmsList.get(0);

            //先判断状态是不是失效的状态
            if (SmsSendStatusEnum.INVALID.getCode().equals(sysSms.getStatus())) {
                log.info(">>> 验证短信Provider接口，短信状态是失效，响应验证失败！");
                return SmsVerifyEnum.ERROR;
            }

            //如果验证码和传过来的不一致
            if (!sysSmsVerifyParam.getCode().equals(sysSms.getValidateCode())) {
                log.info(">>> 验证短信Provider接口，验证手机号和验证码不一致，响应验证失败！");
                return SmsVerifyEnum.ERROR;
            }

            //判断是否超时
            Date invalidTime = sysSms.getInvalidTime();
            if (ObjectUtil.isEmpty(invalidTime) || new Date().after(invalidTime)) {
                log.info(">>> 验证短信Provider接口，验证码超时，响应验证失败！");
                return SmsVerifyEnum.EXPIRED;
            }

            //验证成功把短信设置成失效
            sysSms.setStatus(SmsSendStatusEnum.INVALID.getCode());
            this.updateById(sysSms);

            log.info(">>> 验证短信Provider接口，验证码验证成功！");
            return SmsVerifyEnum.SUCCESS;
        }
    }

    @Override
    public PageResult<SysSms> page(SysSmsInfoParam sysSmsInfoParam) {
        LambdaQueryWrapper<SysSms> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(sysSmsInfoParam)) {
            //根据手机号模糊查询
            if (ObjectUtil.isNotEmpty(sysSmsInfoParam.getPhoneNumbers())) {
                queryWrapper.like(SysSms::getPhoneNumbers, sysSmsInfoParam.getPhoneNumbers());
            }
            //根据发送状态查询（字典 0 未发送，1 发送成功，2 发送失败，3 失效）
            if (ObjectUtil.isNotEmpty(sysSmsInfoParam.getStatus())) {
                queryWrapper.eq(SysSms::getStatus, sysSmsInfoParam.getStatus());
            }
            //根据来源查询（字典 1 app， 2 pc， 3 其他）
            if (ObjectUtil.isNotEmpty(sysSmsInfoParam.getSource())) {
                queryWrapper.eq(SysSms::getSource, sysSmsInfoParam.getSource());
            }
        }
        return new PageResult<>(this.page(PageFactory.defaultPage(), queryWrapper));
    }


}
