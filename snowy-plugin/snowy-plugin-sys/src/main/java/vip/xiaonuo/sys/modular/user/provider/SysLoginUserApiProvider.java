/*
 * Copyright [2022] [https://www.xiaonuo.vip]
 *
 * Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *
 * 1.请不要删除和修改根目录下的LICENSE文件。
 * 2.请不要删除和修改Snowy源码头部的版权声明。
 * 3.本项目代码可免费商业使用，商业使用请保留源码和相关描述文件的项目出处，作者声明等。
 * 4.分发源码时候，请注明软件出处 https://www.xiaonuo.vip
 * 5.不可二次分发开源参与同类竞品，如有想法可联系团队xiaonuobase@qq.com商议合作。
 * 6.若您的项目无法满足以上几点，需要更多功能代码，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package vip.xiaonuo.sys.modular.user.provider;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vip.xiaonuo.auth.api.SaBaseLoginUserApi;
import vip.xiaonuo.auth.core.pojo.SaBaseClientLoginUser;
import vip.xiaonuo.auth.core.pojo.SaBaseLoginUser;
import vip.xiaonuo.sys.modular.org.service.SysUserDataScopeService;
import vip.xiaonuo.sys.modular.user.entity.SysUser;
import vip.xiaonuo.sys.modular.user.result.SysLoginUser;
import vip.xiaonuo.sys.modular.user.service.SysUserService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 登录用户API接口实现类
 *
 * @author xuyuxiang
 * @date 2022/4/29 13:36
 **/
@Slf4j
@Service("loginUserApi")
public class SysLoginUserApiProvider implements SaBaseLoginUserApi {

    @Resource
    private SysUserService sysUserService;

    @Resource
    private SysUserDataScopeService sysUserDataScopeService;

    /**
     * 根据id获取B端用户信息，查不到则返回null
     *
     * @author xuyuxiang
     * @date 2022/3/10 16:14
     **/
    @Override
    public SaBaseLoginUser getUserById(String id) {
        return sysUserService.getUserById(id);
    }

    /**
     * 不实现C端用户信息
     *
     * @author xuyuxiang
     * @date 2022/3/10 16:14
     **/
    @Override
    public SaBaseClientLoginUser getClientUserById(String id) {
        return null;
    }

    /**
     * 根据账号获取B端用户信息，查不到则返回null
     *
     * @author xuyuxiang
     * @date 2021/12/28 15:35
     **/
    @Override
    public SysLoginUser getUserByAccount(String account) {
        return sysUserService.getUserByAccount(account);
    }

    /**
     * 不实现C端用户信息
     *
     * @author xuyuxiang
     * @date 2022/7/8 10:36
     **/
    @Override
    public SaBaseClientLoginUser getClientUserByAccount(String account) {
        return null;
    }

    /**
     * 根据手机号获取B端用户信息，查不到则返回null
     *
     * @author xuyuxiang
     * @date 2022/8/25 14:09
     **/
    @Override
    public SaBaseLoginUser getUserByPhone(String phone) {
        return sysUserService.getUserByPhone(phone);
    }

    @Override
    public SaBaseLoginUser getUserByEmail(String email) {
        return sysUserService.getUserByEmail(email);
    }

    /**
     * 不实现C端用户信息
     *
     * @author xuyuxiang
     * @date 2022/8/25 14:09
     **/
    @Override
    public SaBaseClientLoginUser getClientUserByPhone(String phone) {
        return null;
    }

    @Override
    public SaBaseClientLoginUser getClientUserByEmail(String email) {
        return null;
    }

    /**
     * 根据用户id获取用户集合
     *
     * @author xuyuxiang
     * @date 2022/4/27 22:53
     */
    @Override
    public List<JSONObject> listUserByUserIdList(List<String> userIdList) {
        return sysUserService.listByIds(userIdList).stream().map(JSONUtil::parseObj).collect(Collectors.toList());
    }

    /**
     * 根据用户id获取角色集合
     *
     * @author xuyuxiang
     * @date 2022/4/27 22:53
     */
    @Override
    public List<JSONObject> getRoleListByUserId(String userId) {
        return sysUserService.getRoleList(userId);
    }

    /**
     * 根据角色id和用户id集合获取按钮码集合
     *
     * @author xuyuxiang
     * @date 2022/4/27 22:54
     */
    @Override
    public List<String> getButtonCodeListListByUserAndRoleIdList(List<String> userAndRoleIdList) {
        return sysUserService.getButtonCodeList(userAndRoleIdList);
    }

    /**
     * 根据角色id和用户id集合获取移动端按钮码集合
     *
     * @author xuyuxiang
     * @date 2022/4/27 22:54
     */
    @Override
    public List<String> getMobileButtonCodeListListByUserIdAndRoleIdList(List<String> userAndRoleIdList) {
        return sysUserService.getMobileButtonCodeList(userAndRoleIdList);
    }

    /**
     * 根据角色id和用户id集合获取权限集合
     *
     * @author xuyuxiang
     * @date 2022/4/27 22:54
     */
    @Override
    public List<JSONObject> getPermissionListByUserIdAndRoleIdList(List<String> userAndRoleIdList, String orgId) {
        return sysUserService.getPermissionList(userAndRoleIdList, orgId);
    }

    /**
     * 更新用户的登录时间和登录ip等信息
     *
     * @author xuyuxiang
     * @date 2022/4/27 22:57
     */
    @Override
    public void updateUserLoginInfo(String userId, String device) {
        sysUserService.updateUserLoginInfo(userId, device);
    }

    @Override
    public SaBaseLoginUser createUserWithPhone(String phone) {
        SysUser sysUser = sysUserService.createUserWithPhone(phone);
        return BeanUtil.copyProperties(sysUser, SysLoginUser.class);
    }

    @Override
    public SaBaseClientLoginUser createClientUserWithPhone(String phone) {
        return null;
    }

    @Override
    public SaBaseLoginUser createUserWithEmail(String email) {
        SysUser sysUser = sysUserService.createUserWithEmail(email);
        return BeanUtil.copyProperties(sysUser, SysLoginUser.class);
    }

    @Override
    public SaBaseClientLoginUser createClientUserWithEmail(String email) {
        return null;
    }

    @Override
    public void doRegister(String account, String password) {
        sysUserService.doRegister(account, password);
    }

    @Override
    public void refreshUserDataScope(String userId, List<SaBaseLoginUser.DataScope> dataScopeList) {
        sysUserDataScopeService.refreshByUserId(userId, dataScopeList);
    }

    @Override
    public void refreshOnlineUserPermission(String userId) {
        // 获取该用户所有在线token
        List<String> tokenList = StpUtil.getTokenValueListByLoginId(userId);
        if (ObjectUtil.isEmpty(tokenList)) {
            return;
        }
        // 获取用户基本信息
        SaBaseLoginUser saBaseLoginUser = this.getUserById(userId);
        if (ObjectUtil.isEmpty(saBaseLoginUser)) {
            return;
        }
        // 获取角色列表
        List<JSONObject> roleList = this.getRoleListByUserId(userId);
        List<String> roleIdList = roleList.stream().map(j -> j.getStr("id")).collect(Collectors.toList());
        List<String> roleCodeList = roleList.stream().map(j -> j.getStr("code")).collect(Collectors.toList());
        List<String> userAndRoleIdList = CollectionUtil.unionAll(roleIdList, CollectionUtil.newArrayList(userId));
        // 重新计算权限数据
        List<String> buttonCodeList = this.getButtonCodeListListByUserAndRoleIdList(userAndRoleIdList);
        List<String> mobileButtonCodeList = this.getMobileButtonCodeListListByUserIdAndRoleIdList(userAndRoleIdList);
        List<SaBaseLoginUser.DataScope> dataScopeList = Convert.toList(SaBaseLoginUser.DataScope.class,
                this.getPermissionListByUserIdAndRoleIdList(userAndRoleIdList, saBaseLoginUser.getOrgId()));
        List<String> permissionCodeList = dataScopeList.stream()
                .map(SaBaseLoginUser.DataScope::getApiUrl).collect(Collectors.toList());
        // 填充到用户对象
        saBaseLoginUser.setButtonCodeList(buttonCodeList);
        saBaseLoginUser.setMobileButtonCodeList(mobileButtonCodeList);
        saBaseLoginUser.setDataScopeList(dataScopeList);
        saBaseLoginUser.setPermissionCodeList(permissionCodeList);
        saBaseLoginUser.setRoleCodeList(roleCodeList);
        // 写入该用户的所有TokenSession
        for (String token : tokenList) {
            try {
                SaSession session = StpUtil.getTokenSessionByToken(token);
                if (session != null) {
                    session.set("loginUser", saBaseLoginUser);
                }
            } catch (Exception e) {
                log.warn(">>> 刷新用户权限缓存时跳过无效token，userId：{}，token：{}", userId, token);
            }
        }
        // 刷新预计算表
        this.refreshUserDataScope(userId, dataScopeList);
        log.info(">>> 已刷新在线用户权限缓存，userId：{}，token数：{}", userId, tokenList.size());
    }
}
