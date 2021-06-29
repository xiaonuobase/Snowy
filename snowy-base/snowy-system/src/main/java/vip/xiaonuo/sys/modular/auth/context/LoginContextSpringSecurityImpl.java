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
package vip.xiaonuo.sys.modular.auth.context;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import vip.xiaonuo.core.consts.CommonConstant;
import vip.xiaonuo.core.consts.SymbolConstant;
import vip.xiaonuo.core.context.login.LoginContext;
import vip.xiaonuo.core.exception.AuthException;
import vip.xiaonuo.core.exception.enums.AuthExceptionEnum;
import vip.xiaonuo.core.pojo.login.LoginEmpInfo;
import vip.xiaonuo.core.pojo.login.SysLoginUser;
import vip.xiaonuo.sys.core.enums.AdminTypeEnum;
import vip.xiaonuo.sys.modular.auth.service.AuthService;
import vip.xiaonuo.sys.modular.user.entity.SysUser;
import vip.xiaonuo.sys.modular.user.service.SysUserService;

import javax.annotation.Resource;
import java.util.List;

/**
 * 登录用户上下文实现类
 *
 * @author xuyuxiang
 * @date 2020/3/13 12:19
 */
@Component
public class LoginContextSpringSecurityImpl implements LoginContext {

    @Resource
    private AuthService authService;

    @Resource
    private SysUserService sysUserService;

    private LoginContextSpringSecurityImpl() {

    }

    /**
     * 获取当前登录用户
     *
     * @author xuyuxiang
     * @date 2020/3/13 14:42
     */
    @Override
    public SysLoginUser getSysLoginUser() {
        Authentication authentication = authService.getAuthentication();
        if (ObjectUtil.isEmpty(authentication) || authentication.getPrincipal() instanceof String) {
            throw new AuthException(AuthExceptionEnum.NO_LOGIN_USER);
        } else {
            return (SysLoginUser) authentication.getPrincipal();
        }
    }

    /**
     * 获取当前登录用户，如未登录，则返回null，不抛异常
     *
     * @author xuyuxiang
     * @date 2020/3/13 14:42
     */
    @Override
    public SysLoginUser getSysLoginUserWithoutException() {
        Authentication authentication = authService.getAuthentication();
        if (ObjectUtil.isEmpty(authentication) || authentication.getPrincipal() instanceof String) {
            return null;
        } else {
            return (SysLoginUser) authentication.getPrincipal();
        }
    }

    /**
     * 获取当前登录用户的id
     *
     * @author xuyuxiang
     * @date 2020/3/18 19:26
     */
    @Override
    public Long getSysLoginUserId() {
        return this.getSysLoginUser().getId();
    }

    /**
     * 判断用户是否登录
     *
     * @author xuyuxiang
     * @date 2020/3/18 19:23
     */
    @Override
    public boolean hasLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (ObjectUtil.isEmpty(authentication) || authentication.getPrincipal() instanceof String) {
            return false;
        } else {
            return !(authentication instanceof AnonymousAuthenticationToken);
        }
    }

    /**
     * 获取当前登录的用户账号
     *
     * @author xuyuxiang
     * @date 2020/3/23 8:49
     */
    @Override
    public String getSysLoginUserAccount() {
        return this.getSysLoginUser().getAccount();
    }

    /**
     * 判断当前登录用户是否有某资源的访问权限
     *
     * @author xuyuxiang
     * @date 2020/3/23 8:49
     */
    @Override
    public boolean hasPermission(String requestUri) {
        String removePrefix = StrUtil.removePrefix(requestUri, SymbolConstant.LEFT_DIVIDE);
        String requestPermission = removePrefix.replaceAll(SymbolConstant.LEFT_DIVIDE, SymbolConstant.COLON);
        return this.getSysLoginUser().getPermissions().contains(requestPermission);
    }

    /**
     * 判断当前登录用户是否包含某个角色
     *
     * @author xuyuxiang
     * @date 2020/3/23 8:55
     */
    @Override
    public boolean hasRole(String roleCode) {
        List<String> roleCodeList = this.getLoginUserRoleCodeList();
        return roleCodeList.contains(roleCode);
    }

    /**
     * 判断当前登录用户是否包含任意一个角色
     *
     * @author xuyuxiang
     * @date 2020/3/23 8:55
     */
    @Override
    public boolean hasAnyRole(String roleCodes) {
        boolean flag = false;
        List<String> loginUserRoleCodeList = this.getLoginUserRoleCodeList();
        String[] roleCodeArr = StrUtil.split(roleCodes, SymbolConstant.COMMA);
        for (String roleCode : roleCodeArr) {
            if (loginUserRoleCodeList.contains(roleCode)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * 管理员类型（0超级管理员 1非管理员）
     * 判断当前登录用户是否是超级管理员
     *
     * @author xuyuxiang
     * @date 2020/3/23 17:51
     */
    @Override
    public boolean isSuperAdmin() {
        return this.isAdmin(AdminTypeEnum.SUPER_ADMIN.getCode());
    }

    /**
     * 判断当前登录用户是否包含所有角色
     *
     * @author xuyuxiang
     * @date 2020/4/5 10:28
     */
    @Override
    public boolean hasAllRole(String roleCodes) {
        boolean flag = true;
        List<String> loginUserRoleCodeList = this.getLoginUserRoleCodeList();
        String[] roleCodeArr = StrUtil.split(roleCodes, SymbolConstant.COMMA);
        for (String roleCode : roleCodeArr) {
            if (!loginUserRoleCodeList.contains(roleCode)) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    /**
     * 判断当前登录用户是否是指定类型的管理员
     *
     * @author xuyuxiang
     * @date 2020/4/5 11:43
     */
    private boolean isAdmin(Integer adminTypeCode) {
        Integer adminType = this.getSysLoginUser().getAdminType();
        boolean flag = false;
        if (adminType.equals(adminTypeCode)) {
            flag = true;
        }
        return flag;
    }

    /**
     * 当前登录用户的数据范围（组织机构id集合）
     *
     * @author xuyuxiang
     * @date 2020/4/5 17:20
     */
    @Override
    public List<Long> getLoginUserDataScopeIdList() {
        return this.getSysLoginUser().getDataScopes();
    }

    /**
     * 获取当前登录用户的组织机构id集合
     *
     * @author xuyuxiang
     * @date 2020/4/5 18:32
     */
    @Override
    public Long getSysLoginUserOrgId() {
        LoginEmpInfo loginEmpInfo = this.getSysLoginUser().getLoginEmpInfo();
        if (ObjectUtil.isNotNull(loginEmpInfo)) {
            if (ObjectUtil.isNotEmpty(loginEmpInfo.getOrgId())) {
                return loginEmpInfo.getOrgId();
            }
        }
        return null;
    }

    /**
     * 获取当前登录用户的角色id集合
     *
     * @author xuyuxiang
     * @date 2020/4/20 16:04
     */
    @Override
    public List<String> getLoginUserRoleIds() {
        List<String> resultList = CollectionUtil.newArrayList();
        this.getSysLoginUser().getRoles().forEach(dict -> resultList.add(dict.getStr(CommonConstant.ID)));
        return resultList;
    }

    @Override
    public SysLoginUser getSysLoginUserUpToDate() {
        SysLoginUser sysLoginUser = this.getSysLoginUser();
        Long loginUserId = sysLoginUser.getId();
        SysUser sysUser = sysUserService.getById(loginUserId);
        //构造SysLoginUser
        return authService.genSysLoginUser(sysUser);
    }

    /**
     * 获取当前用户的角色编码集合
     *
     * @author xuyuxiang
     * @date 2020/3/23 8:58
     */
    private List<String> getLoginUserRoleCodeList() {
        List<String> roleCodeList = CollectionUtil.newArrayList();
        this.getSysLoginUser().getRoles().forEach(dict -> roleCodeList.add(dict.getStr(CommonConstant.CODE)));
        return roleCodeList;
    }
}
