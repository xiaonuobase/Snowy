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
package vip.xiaonuo.sys.modular.user.service;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.sys.modular.org.entity.SysOrg;
import vip.xiaonuo.sys.modular.position.entity.SysPosition;
import vip.xiaonuo.sys.modular.role.entity.SysRole;
import vip.xiaonuo.sys.modular.user.entity.SysUser;
import vip.xiaonuo.sys.modular.user.param.*;
import vip.xiaonuo.sys.modular.user.result.*;

import java.io.IOException;
import java.util.List;

/**
 * 用户Service接口
 *
 * @author xuyuxiang
 * @date 2022/4/21 18:35
 **/
public interface SysUserService extends IService<SysUser> {

    /**
     * 根据id获取用户信息，查不到则返回null
     *
     * @author xuyuxiang
     * @date 2022/4/27 21:38
     */
    SysLoginUser getUserById(String id);

    /**
     * 根据账户获取用户信息，查不到则返回null
     *
     * @author xuyuxiang
     * @date 2022/4/27 21:38
     */
    SysLoginUser getUserByAccount(String account);

    /**
     * 根据手机号获取用户信息，查不到则返回null
     *
     * @author xuyuxiang
     * @date 2022/4/27 21:38
     */
    SysLoginUser getUserByPhone(String phone);

    /**
     * 根据邮箱获取用户信息，查不到则返回null
     *
     * @author xuyuxiang
     * @date 2022/4/27 21:38
     */
    SysLoginUser getUserByEmail(String email);

    /**
     * 获取用户分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    Page<SysUser> page(SysUserPageParam sysUserPageParam);

    /**
     * 添加用户
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:48
     */
    void add(SysUserAddParam sysUserAddParam);

    /**
     * 编辑用户
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:13
     */
    void edit(SysUserEditParam sysUserEditParam);

    /**
     * 删除用户
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    void delete(List<SysUserIdParam> sysUserIdParamList);

    /**
     * 获取用户详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    SysUser detail(SysUserIdParam sysUserIdParam);

    /**
     * 获取用户详情
     *
     * @author xuyuxiang
     * @date 2022/7/26 17:21
     **/
    SysUser queryEntity(String id);

    /**
     * 禁用用户
     *
     * @author xuyuxiang
     * @date 2022/7/5 18:20
     **/
    void disableUser(SysUserIdParam sysUserIdParam);

    /**
     * 启用用户
     *
     * @author xuyuxiang
     * @date 2022/7/5 18:21
     **/
    void enableUser(SysUserIdParam sysUserIdParam);

    /**
     * 重置用户密码
     *
     * @author xuyuxiang
     * @date 2022/7/5 18:22
     **/
    void resetPassword(SysUserIdParam sysUserIdParam);

    /**
     * 获取图片验证码
     *
     * @author xuyuxiang
     * @date 2021/12/28 14:46
     **/
    SysUserPicValidCodeResult getPicCaptcha();

    /**
     * 找回密码获取手机验证码
     *
     * @author xuyuxiang
     * @date 2022/8/25 15:16
     **/
    String findPasswordGetPhoneValidCode(SysUserGetPhoneValidCodeParam sysUserGetPhoneValidCodeParam);

    /**
     * 找回密码获取邮箱验证码
     *
     * @author xuyuxiang
     * @date 2022/8/25 15:16
     **/
    String findPasswordGetEmailValidCode(SysUserGetEmailValidCodeParam sysUserGetEmailValidCodeParam);

    /**
     * 通过手机号找回用户密码
     *
     * @author xuyuxiang
     * @date 2022/4/22 15:53
     **/
    void findPasswordByPhone(SysUserFindPwdByPhoneParam sysUserFindPwdByPhoneParam);

    /**
     * 通过邮箱找回用户密码
     *
     * @author xuyuxiang
     * @date 2022/4/22 15:53
     **/
    void findPasswordByEmail(SysUserFindPwdByEmailParam sysUserFindPwdByEmailParam);

    /**
     * 修改用户密码
     *
     * @author xuyuxiang
     * @date 2022/4/22 15:53
     **/
    void updatePassword(SysUserUpdatePwdParam sysUserUpdatePwdParam);

    /**
     * 修改用户头像返回base64
     *
     * @author xuyuxiang
     * @date 2022/4/22 15:53
     **/
    String updateAvatar(MultipartFile file);

    /**
     * 修改用户签名图片返回base64
     *
     * @author xuyuxiang yubaoshan
     * @date 2022/4/22 15:53
     **/
    void updateSignature(SysUserSignatureParam sysUserSignatureParam);

    /**
     * 更新用户的登录时间和登录ip等信息
     *
     * @author xuyuxiang
     * @date 2022/4/27 22:58
     */
    void updateUserLoginInfo(String userId, String device);

    /**
     * 获取用户拥有菜单
     *
     * @author xuyuxiang
     * @date 2022/6/25 18:55
     */
    List<Tree<String>> ownMenu(SysUserIdParam sysUserIdParam);

    /**
     * 获取用户拥有移动端菜单
     *
     * @author xuyuxiang
     * @date 2022/6/25 18:55
     */
    List<Tree<String>> ownMobileMenu(SysUserIdParam sysUserIdParam);

    /**
     * 获取用户拥有角色
     *
     * @author xuyuxiang
     * @date 2022/5/13 21:00
     */
    List<String> ownRole(SysUserIdParam sysUserIdParam);

    /**
     * 给用户授权角色
     *
     * @author xuyuxiang
     * @date 2022/4/29 11:13
     **/
    void grantRole(SysUserGrantRoleParam sysUserGrantRoleParam);

    /**
     * 获取用户拥有资源
     *
     * @author xuyuxiang
     * @date 2022/5/13 20:51
     */
    SysUserOwnResourceResult ownResource(SysUserIdParam sysUserIdParam);

    /**
     * 给用户授权资源
     *
     * @author xuyuxiang
     * @date 2022/4/29 10:12
     **/
    void grantResource(SysUserGrantResourceParam sysUserGrantResourceParam);

    /**
     * 获取用户拥有权限
     *
     * @author xuyuxiang
     * @date 2022/5/13 20:51
     */
    SysUserOwnPermissionResult ownPermission(SysUserIdParam sysUserIdParam);

    /**
     * 给用户授权权限
     *
     * @author xuyuxiang
     * @date 2022/4/29 10:12
     **/
    void grantPermission(SysUserGrantPermissionParam sysUserGrantPermissionParam);

    /**
     * 获取用户组织树
     *
     * @author xuyuxiang
     * @date 2022/6/25 18:55
     */
    List<Tree<String>> loginOrgTree(SysUserIdParam sysUserIdParam);

    /**
     * 编辑个人信息
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    void updateUserInfo(SysUserUpdateInfoParam sysUserUpdateInfoParam);

    /**
     * 编辑个人工作台
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    void updateUserWorkbench(SysUserUpdateWorkbenchParam sysUserUpdateWorkbenchParam);

    /**
     * 获取用户工作台数据
     *
     * @author xuyuxiang
     * @date 2022/5/13 21:00
     */
    String loginWorkbench(SysUserIdParam sysUserIdParam);

    /**
     * 获取角色集合
     *
     * @author xuyuxiang
     * @date 2022/4/29 11:13
     **/
    List<JSONObject> getRoleList(String userId);

    /**
     * 获取按钮编码集合
     *
     * @author xuyuxiang
     * @date 2022/4/29 11:13
     **/
    List<String> getButtonCodeList(List<String> userAndRoleIdList);

    /**
     * 获取移动端按钮编码集合
     *
     * @author xuyuxiang
     * @date 2022/4/29 11:13
     **/
    List<String> getMobileButtonCodeList(List<String> userAndRoleIdList);

    /**
     * 获取权限集合
     *
     * @author xuyuxiang
     * @date 2022/4/29 11:13
     **/
    List<JSONObject> getPermissionList(List<String> userAndRoleIdList, String orgId);

    /**
     * 下载用户导入模板
     *
     * @author xuyuxiang
     * @date 2022/8/8 13:16
     **/
    void downloadImportUserTemplate(HttpServletResponse response) throws IOException;

    /**
     * 用户导入
     *
     * @author xuyuxiang
     * @date 2022/8/8 13:16
     **/
    JSONObject importUser(MultipartFile file);

    /**
     * 用户导出
     *
     * @author xuyuxiang
     * @date 2022/8/8 13:16
     **/
    void exportUser(SysUserExportParam sysUserExportParam, HttpServletResponse response) throws IOException;

    /**
     * 导出用户个人信息
     *
     * @author xuyuxiang
     * @date 2022/8/8 13:16
     **/
    void exportUserInfo(SysUserIdParam sysUserIdParam, HttpServletResponse response) throws IOException;

    /**
     * 获取登录用户的职位信息
     *
     * @author xuyuxiang
     * @date 2022/8/22 9:03
     **/
    List<SysUserPositionResult> loginPositionInfo(SysUserIdParam sysUserIdParam);

    /**
     * 获取所有用户选择器
     *
     * @author xuyuxiang
     * @date 2022/7/25 19:42
     **/
    Page<SysUser> getAllUserSelectorList();

    /* ====用户部分所需要用到的选择器==== */

    /**
     * 获取组织树选择器
     *
     * @author xuyuxiang
     * @date 2022/5/13 21:00
     */
    List<Tree<String>> orgTreeSelector();

    /**
     * 获取组织列表选择器
     *
     * @author xuyuxiang
     * @date 2022/7/22 13:34
     **/
    Page<SysOrg> orgListSelector(SysUserSelectorOrgListParam sysUserSelectorOrgListParam);

    /**
     * 获取职位选择器
     *
     * @author xuyuxiang
     * @date 2022/5/13 21:00
     */
    Page<SysPosition> positionSelector(SysUserSelectorPositionParam sysUserSelectorPositionParam);

    /**
     * 获取角色选择器
     *
     * @author xuyuxiang
     * @date 2022/5/13 21:00
     */
    Page<SysRole> roleSelector(SysUserSelectorRoleParam sysUserSelectorRoleParam);

    /**
     * 获取用户选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    Page<SysUser> userSelector(SysUserSelectorUserParam sysUserSelectorUserParam);

    /**
     * 获取登录用户的站内信分页
     *
     * @author xuyuxiang
     * @date 2022/9/6 17:31
     */
    Page<SysUserMessageResult> loginMessagePage(SysUserMessagePageParam sysUserMessagePageParam);

    /**
     * 读取登录用户站内信详情
     *
     * @author xuyuxiang
     * @date 2022/9/6 17:39
     */
    SysUserMessageDetailResult loginMessageDetail(SysUserMessageIdParam sysUserMessageIdParam);

    /**
     * 根据id集合获取组织集合
     *
     * @author xuyuxiang
     * @date 2023/6/25 11:03
     **/
    List<SysOrg> getOrgListByIdList(SysUserIdListParam sysUserIdListParam);

    /**
     * 根据id集合获取用户集合
     *
     * @author xuyuxiang
     * @date 2023/6/25 11:03
     **/
    List<SysUser> getUserListByIdList(SysUserIdListParam sysUserIdListParam);

    /**
     * 根据id集合获取职位集合
     *
     * @author xuyuxiang
     * @date 2023/6/25 11:03
     **/
    List<SysPosition> getPositionListByIdList(SysUserIdListParam sysUserIdListParam);

    /**
     * 根据id集合获取角色集合
     *
     * @author xuyuxiang
     * @date 2023/6/25 11:03
     **/
    List<SysRole> getRoleListByIdList(SysUserIdListParam sysUserIdListParam);
}
