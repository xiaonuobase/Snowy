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

XiaoNuo采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：

1.请不要删除和修改根目录下的LICENSE文件。
2.请不要删除和修改XiaoNuo源码头部的版权声明。
3.请保留源码和相关描述文件的项目出处，作者声明等。
4.分发源码时候，请注明软件出处 https://gitee.com/xiaonuobase/xiaonuo-vue
5.在修改包名，模块名称，项目代码等时，请注明软件出处 https://gitee.com/xiaonuobase/xiaonuo-vue
6.若您的项目无法满足以上几点，可申请商业授权，获取XiaoNuo商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package com.cn.xiaonuo.sys.modular.sms.enums;

import com.cn.xiaonuo.core.annotion.ExpEnumType;
import com.cn.xiaonuo.core.exception.enums.abs.AbstractBaseExceptionEnum;
import com.cn.xiaonuo.core.factory.ExpEnumCodeFactory;
import com.cn.xiaonuo.sys.core.consts.SysExpEnumConstant;

/**
 * 短信发送相关异常枚举
 *
 * @author xuyuxiang
 * @date 2020/7/7 11:30
 */
@ExpEnumType(module = SysExpEnumConstant.XIAONUO_SYS_MODULE_EXP_CODE, kind = SysExpEnumConstant.SMS_EXCEPTION_ENUM)
public enum SmsSendExceptionEnum implements AbstractBaseExceptionEnum {

    /**
     * 手机号码不能为空
     */
    PHONE_NUMBER_EMPTY(1, "手机号码不能为空，请检查phoneNumbers参数"),

    /**
     * 验证码不能为空
     */
    VALIDATE_CODE_EMPTY(2, "验证码不能为空，请检查validateCode参数"),

    /**
     * 模板号不能为空
     */
    TEMPLATE_CODE_EMPTY(3, "模板号不能为空，请检查templateCode参数");

    private final Integer code;

    private final String message;

    SmsSendExceptionEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return ExpEnumCodeFactory.getExpEnumCode(this.getClass(), code);
    }

    @Override
    public String getMessage() {
        return message;
    }

}
