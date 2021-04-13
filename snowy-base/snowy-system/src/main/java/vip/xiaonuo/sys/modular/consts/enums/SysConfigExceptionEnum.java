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
package vip.xiaonuo.sys.modular.consts.enums;

import vip.xiaonuo.core.annotion.ExpEnumType;
import vip.xiaonuo.core.exception.enums.abs.AbstractBaseExceptionEnum;
import vip.xiaonuo.core.factory.ExpEnumCodeFactory;
import vip.xiaonuo.sys.core.consts.SysExpEnumConstant;

/**
 * 系统参数配置相关异常枚举
 *
 * @author xuyuxiang
 * @date 2020/4/14 11:24
 */
@ExpEnumType(module = SysExpEnumConstant.SNOWY_SYS_MODULE_EXP_CODE, kind = SysExpEnumConstant.SYS_CONFIG_EXCEPTION_ENUM)
public enum SysConfigExceptionEnum implements AbstractBaseExceptionEnum {

    /**
     * 数据库连接配置不存在
     */
    DATA_SOURCE_NOT_EXIST(1, "数据库连接配置不存在，请检查spring.datasource配置用户名密码是否正确"),

    /**
     * 系统参数配置不存在
     */
    CONFIG_NOT_EXIST(2, "系统参数配置不存在"),

    /**
     * 系统参数配置编码重复
     */
    CONFIG_CODE_REPEAT(3, "系统参数配置编码重复，请检查code参数"),

    /**
     * 系统参数配置名称重复
     */
    CONFIG_NAME_REPEAT(4, "系统参数配置名称重复，请检查name参数"),

    /**
     * 不能删除系统参数
     */
    CONFIG_SYS_CAN_NOT_DELETE(5, "系统参数配置不能删除"),

    /**
     * 常量分类在字典中未找到
     */
    NOT_EXIST_DICT_TYPE(6, "字典类型中未找到常量分类，请检查字典类型表");

    private final Integer code;

    private final String message;

    SysConfigExceptionEnum(Integer code, String message) {
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
