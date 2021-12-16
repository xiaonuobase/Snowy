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
package vip.xiaonuo.sys.modular.user.factory;

import cn.hutool.core.util.ObjectUtil;
import vip.xiaonuo.core.context.constant.ConstantContextHolder;
import vip.xiaonuo.core.enums.CommonStatusEnum;
import vip.xiaonuo.core.util.CryptogramUtil;
import vip.xiaonuo.sys.core.enums.AdminTypeEnum;
import vip.xiaonuo.sys.core.enums.SexEnum;
import vip.xiaonuo.sys.modular.user.entity.SysUser;

/**
 * 填充用户附加信息工厂
 *
 * @author xuyuxiang
 * @date 2020/3/23 16:40
 */
public class SysUserFactory {

    /**
     * 管理员类型（1超级管理员 2非管理员）
     * 新增普通用户时填充相关信息
     *
     * @author xuyuxiang
     * @date 2020/3/23 16:41
     */
    public static void fillAddCommonUserInfo(SysUser sysUser) {
        fillBaseUserInfo(sysUser);
        sysUser.setAdminType(AdminTypeEnum.NONE.getCode());
    }

    /**
     * 填充用户基本字段
     *
     * @author xuyuxiang
     * @date 2020/3/23 16:50
     */
    public static void fillBaseUserInfo(SysUser sysUser) {
        //密码为空则设置密码（密码都为哈希值哦）
        if (ObjectUtil.isEmpty(sysUser.getPwdHashValue())) {
            //没有密码则设置默认密码
            String password = ConstantContextHolder.getDefaultPassWord();
            //设置密码为Sm3的哈希值，这里代表保护密码的完整性
            sysUser.setPwdHashValue(CryptogramUtil.doHashValue(password));
        }

        if (ObjectUtil.isEmpty(sysUser.getAvatar())) {
            sysUser.setAvatar(null);
        }

        if (ObjectUtil.isEmpty(sysUser.getSex())) {
            sysUser.setSex(SexEnum.NONE.getCode());
        }

        sysUser.setStatus(CommonStatusEnum.ENABLE.getCode());
    }
}
