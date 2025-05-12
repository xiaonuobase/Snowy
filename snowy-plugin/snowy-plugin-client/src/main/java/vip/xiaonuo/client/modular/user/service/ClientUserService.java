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
package vip.xiaonuo.client.modular.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.client.modular.user.entity.ClientUser;
import vip.xiaonuo.client.modular.user.param.*;
import vip.xiaonuo.client.modular.user.result.ClientLoginUser;
import vip.xiaonuo.client.modular.user.result.ClientUserPicValidCodeResult;

import java.util.List;

/**
 * 用户Service接口
 *
 * @author xuyuxiang
 * @date 2022/4/21 18:35
 **/
public interface ClientUserService extends IService<ClientUser> {

    /**
     * 根据id获取用户信息，查不到则返回null
     *
     * @author xuyuxiang
     * @date 2022/4/27 21:38
     */
    ClientLoginUser getUserById(String id);

    /**
     * 根据账户获取用户信息，查不到则返回null
     *
     * @author xuyuxiang
     * @date 2022/4/27 21:38
     */
    ClientLoginUser getUserByAccount(String account);

    /**
     * 根据手机号获取用户信息，查不到则返回null
     *
     * @author xuyuxiang
     * @date 2022/4/27 21:38
     */
    ClientLoginUser getUserByPhone(String phone);

    /**
     * 根据邮箱获取用户信息，查不到则返回null
     *
     * @author xuyuxiang
     * @date 2022/4/27 21:38
     */
    ClientLoginUser getUserByEmail(String email);

    /**
     * 获取用户分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    Page<ClientUser> page(ClientUserPageParam clientUserPageParam);

    /**
     * 添加用户
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:48
     */
    void add(ClientUserAddParam clientUserAddParam, String sourceFromType);

    /**
     * 编辑用户
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:13
     */
    void edit(ClientUserEditParam clientUserEditParam);

    /**
     * 删除用户
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    void delete(List<ClientUserIdParam> clientUserIdParamList);

    /**
     * 获取用户详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    ClientUser detail(ClientUserIdParam clientUserIdParam);

    /**
     * 获取用户详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    ClientUser queryEntity(String id);

    /**
     * 获取图片验证码
     *
     * @author xuyuxiang
     * @date 2021/12/28 14:46
     **/
    ClientUserPicValidCodeResult getPicCaptcha();

    /**
     * 找回密码获取手机验证码
     *
     * @author xuyuxiang
     * @date 2022/8/25 15:16
     **/
    String findPasswordGetPhoneValidCode(ClientUserGetPhoneValidCodeParam clientUserGetPhoneValidCodeParam);

    /**
     * 找回密码获取邮箱验证码
     *
     * @author xuyuxiang
     * @date 2022/8/25 15:16
     **/
    String findPasswordGetEmailValidCode(ClientUserGetEmailValidCodeParam clientUserGetEmailValidCodeParam);

    /**
     * 通过手机号找回用户密码
     *
     * @author xuyuxiang
     * @date 2022/4/22 15:53
     **/
    void findPasswordByPhone(ClientUserFindPwdByPhoneParam clientUserFindPwdByPhoneParam);

    /**
     * 通过邮箱找回用户密码
     *
     * @author xuyuxiang
     * @date 2022/4/22 15:53
     **/
    void findPasswordByEmail(ClientUserFindPwdByEmailParam clientUserFindPwdByEmailParam);

    /**
     * 修改密码获取手机验证码
     *
     * @author xuyuxiang
     * @date 2022/8/25 15:16
     **/
    String updatePasswordGetPhoneValidCode(ClientUserGetPhoneValidCodeParam clientUserGetPhoneValidCodeParam);

    /**
     * 修改密码获取邮箱验证码
     *
     * @author xuyuxiang
     * @date 2022/8/25 15:16
     **/
    String updatePasswordGetEmailValidCode(ClientUserGetEmailValidCodeParam clientUserGetEmailValidCodeParam);

    /**
     * 通过验证旧密码修改用户密码
     *
     * @author xuyuxiang
     * @date 2022/4/22 15:53
     **/
    void updatePasswordByOld(ClientUserUpdatePwdByOldParam clientUserUpdatePwdByOldParam);

    /**
     * 通过验证手机号修改用户密码
     *
     * @author xuyuxiang
     * @date 2022/4/22 15:53
     **/
    void updatePasswordByPhone(ClientUserUpdatePwdByPhoneParam clientUserUpdatePwdByPhoneParam);

    /**
     * 通过验证邮箱修改用户密码
     *
     * @author xuyuxiang
     * @date 2022/4/22 15:53
     **/
    void updatePasswordByEmail(ClientUserUpdatePwdByEmailParam clientUserUpdatePwdByEmailParam);

    /**
     * 绑定手机号获取手机验证码
     *
     * @author xuyuxiang
     * @date 2022/8/25 15:16
     **/
    String bindPhoneGetPhoneValidCode(ClientUserGetPhoneValidCodeParam clientUserGetPhoneValidCodeParam);

    /**
     * 修改绑定手机号获取手机验证码
     *
     * @author xuyuxiang
     * @date 2022/8/25 15:16
     **/
    String updateBindPhoneGetPhoneValidCode(ClientUserGetPhoneValidCodeParam clientUserGetPhoneValidCodeParam);

    /**
     * 绑定手机号
     *
     * @author xuyuxiang
     * @date 2022/8/25 15:16
     **/
    void bindPhone(ClientUserBindPhoneParam clientUserBindPhoneParam);

    /**
     * 绑定邮箱获取邮箱验证码
     *
     * @author xuyuxiang
     * @date 2022/8/25 15:16
     **/
    String bindEmailGetEmailValidCode(ClientUserGetEmailValidCodeParam clientUserGetEmailValidCodeParam);

    /**
     * 修改绑定邮箱获取邮箱验证码
     *
     * @author xuyuxiang
     * @date 2022/8/25 15:16
     **/
    String updateBindEmailGetEmailValidCode(ClientUserGetEmailValidCodeParam clientUserGetEmailValidCodeParam);

    /**
     * 绑定邮箱
     *
     * @author xuyuxiang
     * @date 2022/8/25 15:16
     **/
    void bindEmail(ClientUserBindEmailParam clientUserBindEmailParam);

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
    void updateSignature(ClientUserSignatureParam clientUserSignatureParam);

    /**
     * 更新用户的登录时间和登录ip等信息
     *
     * @author xuyuxiang
     * @date 2022/4/27 22:58
     */
    void updateUserLoginInfo(String userId, String device);

    /**
     * 编辑个人信息
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    void updateUserInfo(ClientUserUpdateInfoParam clientUserUpdateInfoParam);

    /**
     * 根据id获取头像
     *
     * @author xuyuxiang
     * @date 2023/8/28 10:10
     **/
    String getAvatarById(ClientUserIdParam clientUserIdParam);

    /**
     * 根据手机号创建用户
     *
     * @author xuyuxiang
     * @date 2022/8/25 15:16
     **/
    ClientUser createUserWithPhone(String phone);

    /**
     * 根据邮箱创建用户
     *
     * @author xuyuxiang
     * @date 2022/8/25 15:16
     **/
    ClientUser createUserWithEmail(String email);

    /**
     * 根据账号密码创建用户
     *
     * @author xuyuxiang
     * @date 2022/8/25 15:16
     **/
    ClientUser createUserWithAccount(String account, String password);

    /**
     * 判断当前用户密码是否过期
     *
     * @author xuyuxiang
     * @date 2022/8/25 15:16
     **/
    Boolean isUserPasswordExpired();

    /**
     * 判断当前用户是否需要绑定手机号
     *
     * @author xuyuxiang
     * @date 2022/8/25 15:16
     **/
    Boolean isUserNeedBindPhone();

    /**
     * 判断当前用户是否需要绑定邮箱
     *
     * @author xuyuxiang
     * @date 2022/8/25 15:16
     **/
    Boolean isUserNeedBindEmail();

    /**
     * 通知用户密码即将到期
     *
     * @author xuyuxiang
     * @date 2022/8/25 15:16
     **/
    void noticeUserPasswordAboutToExpired();

    /**
     * 执行注册
     *
     * @author xuyuxiang
     * @date 2022/8/25 15:16
     **/
    void doRegister(String account, String password);
}
