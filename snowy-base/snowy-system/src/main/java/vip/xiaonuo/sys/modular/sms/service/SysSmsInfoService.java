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
package vip.xiaonuo.sys.modular.sms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.core.pojo.page.PageResult;
import vip.xiaonuo.sys.modular.sms.entity.SysSms;
import vip.xiaonuo.sys.modular.sms.enums.SmsSendStatusEnum;
import vip.xiaonuo.sys.modular.sms.enums.SmsVerifyEnum;
import vip.xiaonuo.sys.modular.sms.param.SysSmsInfoParam;
import vip.xiaonuo.sys.modular.sms.param.SysSmsSendParam;
import vip.xiaonuo.sys.modular.sms.param.SysSmsVerifyParam;

/**
 * 系统短信service接口
 *
 * @author yubaoshan
 * @date 2018/7/5 13:44
 */
public interface SysSmsInfoService extends IService<SysSms> {

    /**
     * 存储短信验证信息
     *
     * @param sysSmsSendParam 发送参数
     * @param validateCode    验证码
     * @return 短信记录id
     * @author yubaoshan
     * @date 2018/7/6 16:47
     */
    Long saveSmsInfo(SysSmsSendParam sysSmsSendParam, String validateCode);

    /**
     * 更新短息发送状态
     *
     * @param smsId             短信记录id
     * @param smsSendStatusEnum 发送状态枚举
     * @author yubaoshan
     * @date 2018/7/6 17:12
     */
    void updateSmsInfo(Long smsId, SmsSendStatusEnum smsSendStatusEnum);

    /**
     * 校验验证码是否正确
     *
     * @param sysSmsVerifyParam 短信校验参数
     * @return 短信校验结果枚举
     * @author yubaoshan
     * @date 2018/7/6 17:16
     */
    SmsVerifyEnum validateSmsInfo(SysSmsVerifyParam sysSmsVerifyParam);

    /**
     * 短信发送记录查询
     *
     * @param sysSmsInfoParam 查询参数
     * @return 查询分页结果
     * @author xuyuxiang
     * @date 2020/7/2 12:08
     */
    PageResult<SysSms> page(SysSmsInfoParam sysSmsInfoParam);
}
