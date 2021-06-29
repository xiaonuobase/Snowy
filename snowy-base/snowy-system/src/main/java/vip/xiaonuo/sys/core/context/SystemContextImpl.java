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
package vip.xiaonuo.sys.core.context;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import org.springframework.stereotype.Component;
import vip.xiaonuo.core.context.system.SystemContext;
import vip.xiaonuo.core.pojo.base.validate.UniqueValidateParam;
import vip.xiaonuo.core.pojo.login.SysLoginUser;
import vip.xiaonuo.sys.modular.auth.service.AuthService;
import vip.xiaonuo.sys.modular.dict.service.SysDictDataService;
import vip.xiaonuo.sys.modular.role.entity.SysRole;
import vip.xiaonuo.sys.modular.role.param.SysRoleParam;
import vip.xiaonuo.sys.modular.role.service.SysRoleService;
import vip.xiaonuo.sys.modular.user.entity.SysUser;
import vip.xiaonuo.sys.modular.user.param.SysUserParam;
import vip.xiaonuo.sys.modular.user.service.SysUserService;

import javax.annotation.Resource;
import java.util.List;

/**
 * 系统相关上下文接口实现类
 *
 * @author xuyuxiang
 * @date 2020/5/6 14:59
 */
@Component
public class SystemContextImpl implements SystemContext {

    Log log = Log.get();

    @Resource
    private AuthService authService;

    @Resource
    private SysUserService sysUserService;

    @Resource
    private SysRoleService sysRoleService;

    @Resource
    private SysDictDataService sysDictDataService;

    @Override
    public String getNameByUserId(Long userId) {
        return sysUserService.getNameByUserId(userId);
    }

    @Override
    public String getNameByRoleId(Long roleId) {
        return sysRoleService.getNameByRoleId(roleId);
    }

    @Override
    public SysLoginUser getLoginUserByToken(String token) {
        return authService.getLoginUserByToken(token);
    }

    @Override
    public List<Dict> listUser(String account) {
        SysUserParam sysUserParam = new SysUserParam();
        if (ObjectUtil.isNotEmpty(account)) {
            sysUserParam.setAccount(account);
        }
        return sysUserService.list(sysUserParam);
    }

    @Override
    public List<Dict> listRole(String name) {
        SysRoleParam sysRoleParam = new SysRoleParam();
        if (ObjectUtil.isNotEmpty(name)) {
            sysRoleParam.setName(name);
        }
        return sysRoleService.list(sysRoleParam);
    }

    @Override
    public boolean isUser(Long userOrRoleId) {
        SysUser sysUser = sysUserService.getById(userOrRoleId);
        return !ObjectUtil.isNull(sysUser);
    }

    @Override
    public boolean isRole(Long userOrRoleId) {
        SysRole sysRole = sysRoleService.getById(userOrRoleId);
        return !ObjectUtil.isNull(sysRole);
    }

    @Override
    public List<String> getDictCodesByDictTypeCode(String... dictTypeCodes) {
        return sysDictDataService.getDictCodesByDictTypeCode(dictTypeCodes);
    }

    @Override
    public boolean tableUniValueFlag(UniqueValidateParam uniqueValidateParam) {
        int resultCount = 0;

        // 参数校验
        paramValidate(uniqueValidateParam);

        // 不排除当前记录，不排除逻辑删除的内容
        if (!uniqueValidateParam.getExcludeCurrentRecord()
                && !uniqueValidateParam.getExcludeLogicDeleteItems()) {
            resultCount = SqlRunner.db().selectCount(
                    "select count(*) from " + uniqueValidateParam.getTableName() + " where " + uniqueValidateParam.getColumnName() + " = {0}",
                    uniqueValidateParam.getValue());
        }

        // 不排除当前记录，排除逻辑删除的内容
        if (!uniqueValidateParam.getExcludeCurrentRecord()
                && uniqueValidateParam.getExcludeLogicDeleteItems()) {
            resultCount = SqlRunner.db().selectCount(
                    "select count(*) from " + uniqueValidateParam.getTableName()
                            + " where " + uniqueValidateParam.getColumnName() + " = {0} "
                            + " and "
                            + "(" + uniqueValidateParam.getLogicDeleteFieldName() + " is null || "
                            + uniqueValidateParam.getLogicDeleteFieldName() + " <> " + uniqueValidateParam.getLogicDeleteValue() + ")",
                    uniqueValidateParam.getValue());
        }

        // 排除当前记录，不排除逻辑删除的内容
        if (uniqueValidateParam.getExcludeCurrentRecord()
                && !uniqueValidateParam.getExcludeLogicDeleteItems()) {

            // id判空
            paramIdValidate(uniqueValidateParam);

            resultCount = SqlRunner.db().selectCount(
                    "select count(*) from " + uniqueValidateParam.getTableName()
                            + " where " + uniqueValidateParam.getColumnName() + " = {0} "
                            + " and id <> {1}",
                    uniqueValidateParam.getValue(), uniqueValidateParam.getId());
        }

        // 排除当前记录，排除逻辑删除的内容
        if (uniqueValidateParam.getExcludeCurrentRecord()
                && uniqueValidateParam.getExcludeLogicDeleteItems()) {

            // id判空
            paramIdValidate(uniqueValidateParam);

            resultCount = SqlRunner.db().selectCount(
                    "select count(*) from " + uniqueValidateParam.getTableName()
                            + " where " + uniqueValidateParam.getColumnName() + " = {0} "
                            + " and id <> {1} "
                            + " and "
                            + "(" + uniqueValidateParam.getLogicDeleteFieldName() + " is null || "
                            + uniqueValidateParam.getLogicDeleteFieldName() + " <> " + uniqueValidateParam.getLogicDeleteValue() + ")",
                    uniqueValidateParam.getValue(), uniqueValidateParam.getId());
        }

        // 如果大于0，代表不是唯一的当前校验的值
        return resultCount <= 0;
    }

    @Override
    public List<Long> getAllUserIdList() {
        return sysUserService.getAllUserIdList();
    }

    /**
     * 几个参数的为空校验
     *
     * @author xuyuxiang
     * @date 2020/8/17 22:00
     */
    private void paramValidate(UniqueValidateParam uniqueValidateParam) {
        if (StrUtil.isBlank(uniqueValidateParam.getTableName())) {
            throw new IllegalArgumentException("当前table字段值唯一性校验失败，tableName为空");
        }
        if (StrUtil.isBlank(uniqueValidateParam.getColumnName())) {
            throw new IllegalArgumentException("当前table字段值唯一性校验失败，columnName为空");
        }
        if (StrUtil.isBlank(uniqueValidateParam.getValue())) {
            throw new IllegalArgumentException("当前table字段值唯一性校验失败，字段值value为空");
        }
    }

    /**
     * id参数的为空校验
     *
     * @author xuyuxiang
     * @date 2020/8/17 22:00
     */
    private void paramIdValidate(UniqueValidateParam uniqueValidateParam) {
        if (uniqueValidateParam.getId() == null) {
            throw new IllegalArgumentException("当前table字段值唯一性校验失败，id为空");
        }
    }

}
