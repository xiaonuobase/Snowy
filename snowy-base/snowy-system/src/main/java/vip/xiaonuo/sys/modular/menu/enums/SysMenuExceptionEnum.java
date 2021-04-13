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
package vip.xiaonuo.sys.modular.menu.enums;

import vip.xiaonuo.core.annotion.ExpEnumType;
import vip.xiaonuo.core.exception.enums.abs.AbstractBaseExceptionEnum;
import vip.xiaonuo.core.factory.ExpEnumCodeFactory;
import vip.xiaonuo.sys.core.consts.SysExpEnumConstant;

/**
 * 系统菜单相关异常枚举
 *
 * @author xuyuxiang
 * @date 2020/3/26 10:12
 */
@ExpEnumType(module = SysExpEnumConstant.SNOWY_SYS_MODULE_EXP_CODE, kind = SysExpEnumConstant.SYS_MENU_EXCEPTION_ENUM)
public enum SysMenuExceptionEnum implements AbstractBaseExceptionEnum {

    /**
     * 菜单不存在
     */
    MENU_NOT_EXIST(1, "菜单不存在"),

    /**
     * 菜单编码重复
     */
    MENU_CODE_REPEAT(2, "菜单编码重复，请检查code参数"),

    /**
     * 菜单名称重复
     */
    MENU_NAME_REPEAT(3, "菜单名称重复，请检查name参数"),

    /**
     * 路由地址为空
     */
    MENU_ROUTER_EMPTY(4, "路由地址为空，请检查router参数"),

    /**
     * 组件地址为空
     */
    MENU_COMPONENT_EMPTY(5, "组件地址为空，请检查component参数"),

    /**
     * 打开方式为空
     */
    MENU_OPEN_TYPE_EMPTY(6, "打开方式为空，请检查openType参数"),

    /**
     * 权限标识格式为空
     */
    MENU_PERMISSION_EMPTY(7, "权限标识为空，请检查permission参数"),

    /**
     * 权限标识格式错误
     */
    MENU_PERMISSION_ERROR(8, "权限标识格式错误，请检查permission参数"),

    /**
     * 权限不存在
     */
    MENU_PERMISSION_NOT_EXIST(9, "权限不存在，请检查permission参数"),

    /**
     * 不能移动根节点
     */
    CANT_MOVE_APP(10, "父节点不是根节点不能移动应用"),

    /**
     * 父级菜单不能为当前节点，请重新选择父级菜单
     */
    PID_CANT_EQ_ID(11, "父级菜单不能为当前节点，请重新选择父级菜单"),

    /**
     * 父节点不能为本节点的子节点，请重新选择父节点
     */
    PID_CANT_EQ_CHILD_ID(6, "父节点不能为本节点的子节点，请重新选择父节点");

    private final Integer code;

    private final String message;

    SysMenuExceptionEnum(Integer code, String message) {
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
