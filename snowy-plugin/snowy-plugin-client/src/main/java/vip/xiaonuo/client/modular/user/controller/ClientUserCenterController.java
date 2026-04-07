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
package vip.xiaonuo.client.modular.user.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.client.modular.user.param.*;
import vip.xiaonuo.client.modular.user.result.ClientUserPicValidCodeResult;
import vip.xiaonuo.client.modular.user.service.ClientUserService;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;

import javax.validation.Valid;

/**
 * C端用户个人控制器
 *
 * @author xuyuxiang
 * @date 2022/4/22 9:34
 **/
@Tag(name = "C端用户个人控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 2)
@RestController
@Validated
public class ClientUserCenterController {

    @Resource
    private ClientUserService clientUserService;

    /**
     * 获取图片验证码
     *
     * @author xuyuxiang
     * @date 2022/7/8 9:26
     **/
    @ApiOperationSupport(order = 1)
    @Operation(summary = "获取图片验证码")
    @GetMapping("/client/userCenter/getPicCaptcha")
    public CommonResult<ClientUserPicValidCodeResult> getPicCaptcha() {
        return CommonResult.data(clientUserService.getPicCaptcha());
    }

    /**
     * 找回密码获取手机验证码
     *
     * @author xuyuxiang
     * @date 2021/10/13 14:01
     **/
    @ApiOperationSupport(order = 2)
    @Operation(summary = "找回密码获取手机验证码")
    @GetMapping("/client/userCenter/findPasswordGetPhoneValidCode")
    public CommonResult<String> findPasswordGetPhoneValidCode(@Valid ClientUserGetPhoneValidCodeParam clientUserGetPhoneValidCodeParam) {
        return CommonResult.data(clientUserService.findPasswordGetPhoneValidCode(clientUserGetPhoneValidCodeParam));
    }

    /**
     * 找回密码获取邮箱验证码
     *
     * @author xuyuxiang
     * @date 2021/10/13 14:01
     **/
    @ApiOperationSupport(order = 3)
    @Operation(summary = "找回密码获取邮箱验证码")
    @GetMapping("/client/userCenter/findPasswordGetEmailValidCode")
    public CommonResult<String> findPasswordGetEmailValidCode(@Valid ClientUserGetEmailValidCodeParam clientUserGetEmailValidCodeParam) {
        return CommonResult.data(clientUserService.findPasswordGetEmailValidCode(clientUserGetEmailValidCodeParam));
    }

    /**
     * 通过手机号找回用户密码
     *
     * @author xuyuxiang
     * @date 2021/10/13 14:01
     **/
    @ApiOperationSupport(order = 4)
    @Operation(summary = "通过手机号找回用户密码")
    @CommonLog("通过手机号找回用户密码")
    @PostMapping("/client/userCenter/findPasswordByPhone")
    public CommonResult<String> findPasswordByPhone(@RequestBody @Valid ClientUserFindPwdByPhoneParam clientUserFindPwdByPhoneParam) {
        clientUserService.findPasswordByPhone(clientUserFindPwdByPhoneParam);
        return CommonResult.ok();
    }

    /**
     * 通过邮箱找回用户密码
     *
     * @author xuyuxiang
     * @date 2021/10/13 14:01
     **/
    @ApiOperationSupport(order = 5)
    @Operation(summary = "通过邮箱找回用户密码")
    @CommonLog("通过邮箱找回用户密码")
    @PostMapping("/client/userCenter/findPasswordByEmail")
    public CommonResult<String> findPasswordByEmail(@RequestBody @Valid ClientUserFindPwdByEmailParam clientUserFindPwdByEmailParam) {
        clientUserService.findPasswordByEmail(clientUserFindPwdByEmailParam);
        return CommonResult.ok();
    }

    /**
     * 修改密码获取手机验证码
     *
     * @author xuyuxiang
     * @date 2021/10/13 14:01
     **/
    @ApiOperationSupport(order = 6)
    @Operation(summary = "修改密码获取手机验证码")
    @GetMapping("/client/userCenter/updatePasswordGetPhoneValidCode")
    public CommonResult<String> updatePasswordGetPhoneValidCode(@Valid ClientUserGetPhoneValidCodeParam clientUserGetPhoneValidCodeParam) {
        return CommonResult.data(clientUserService.updatePasswordGetPhoneValidCode(clientUserGetPhoneValidCodeParam));
    }

    /**
     * 修改密码获取邮箱验证码
     *
     * @author xuyuxiang
     * @date 2021/10/13 14:01
     **/
    @ApiOperationSupport(order = 7)
    @Operation(summary = "修改密码获取邮箱验证码")
    @GetMapping("/client/userCenter/updatePasswordGetEmailValidCode")
    public CommonResult<String> updatePasswordGetEmailValidCode(@Valid ClientUserGetEmailValidCodeParam clientUserGetEmailValidCodeParam) {
        return CommonResult.data(clientUserService.updatePasswordGetEmailValidCode(clientUserGetEmailValidCodeParam));
    }

    /**
     * 通过验证旧密码修改用户密码
     *
     * @author xuyuxiang
     * @date 2021/10/13 14:01
     **/
    @ApiOperationSupport(order = 8)
    @Operation(summary = "通过验证旧密码修改用户密码")
    @CommonLog("通过验证旧密码修改用户密码")
    @PostMapping("/client/userCenter/updatePasswordByOld")
    public CommonResult<String> updatePasswordByOld(@RequestBody @Valid ClientUserUpdatePwdByOldParam clientUserUpdatePwdByOldParam) {
        clientUserService.updatePasswordByOld(clientUserUpdatePwdByOldParam);
        return CommonResult.ok();
    }

    /**
     * 通过验证手机号修改用户密码
     *
     * @author xuyuxiang
     * @date 2021/10/13 14:01
     **/
    @ApiOperationSupport(order = 9)
    @Operation(summary = "通过验证手机号修改用户密码")
    @CommonLog("通过验证手机号修改用户密码")
    @PostMapping("/client/userCenter/updatePasswordByPhone")
    public CommonResult<String> updatePasswordByPhone(@RequestBody @Valid ClientUserUpdatePwdByPhoneParam clientUserUpdatePwdByPhoneParam) {
        clientUserService.updatePasswordByPhone(clientUserUpdatePwdByPhoneParam);
        return CommonResult.ok();
    }

    /**
     * 通过验证邮箱修改用户密码
     *
     * @author xuyuxiang
     * @date 2021/10/13 14:01
     **/
    @ApiOperationSupport(order = 10)
    @Operation(summary = "通过验证邮箱修改用户密码")
    @CommonLog("通过验证邮箱修改用户密码")
    @PostMapping("/client/userCenter/updatePasswordByEmail")
    public CommonResult<String> updatePasswordByEmail(@RequestBody @Valid ClientUserUpdatePwdByEmailParam clientUserUpdatePwdByEmailParam) {
        clientUserService.updatePasswordByEmail(clientUserUpdatePwdByEmailParam);
        return CommonResult.ok();
    }

    /**
     * 绑定手机号获取手机验证码
     *
     * @author xuyuxiang
     * @date 2021/10/13 14:01
     **/
    @ApiOperationSupport(order = 11)
    @Operation(summary = "绑定手机号获取手机验证码")
    @GetMapping("/client/userCenter/bindPhoneGetPhoneValidCode")
    public CommonResult<String> bindPhoneGetPhoneValidCode(@Valid ClientUserGetPhoneValidCodeParam clientUserGetPhoneValidCodeParam) {
        return CommonResult.data(clientUserService.bindPhoneGetPhoneValidCode(clientUserGetPhoneValidCodeParam));
    }

    /**
     * 修改绑定手机号获取手机验证码
     *
     * @author xuyuxiang
     * @date 2021/10/13 14:01
     **/
    @ApiOperationSupport(order = 12)
    @Operation(summary = "修改绑定手机号获取手机验证码")
    @GetMapping("/client/userCenter/updateBindPhoneGetPhoneValidCode")
    public CommonResult<String> updateBindPhoneGetPhoneValidCode(@Valid ClientUserGetPhoneValidCodeParam clientUserGetPhoneValidCodeParam) {
        return CommonResult.data(clientUserService.updateBindPhoneGetPhoneValidCode(clientUserGetPhoneValidCodeParam));
    }

    /**
     * 绑定手机号
     *
     * @author xuyuxiang
     * @date 2021/10/13 14:01
     **/
    @ApiOperationSupport(order = 13)
    @Operation(summary = "绑定手机号")
    @CommonLog("绑定手机号")
    @PostMapping("/client/userCenter/bindPhone")
    public CommonResult<String> bindPhone(@RequestBody @Valid ClientUserBindPhoneParam clientUserBindPhoneParam) {
        clientUserService.bindPhone(clientUserBindPhoneParam);
        return CommonResult.ok();
    }

    /**
     * 绑定邮箱获取邮箱验证码
     *
     * @author xuyuxiang
     * @date 2021/10/13 14:01
     **/
    @ApiOperationSupport(order = 14)
    @Operation(summary = "绑定邮箱获取邮箱验证码")
    @GetMapping("/client/userCenter/bindEmailGetEmailValidCode")
    public CommonResult<String> bindEmailGetEmailValidCode(@Valid ClientUserGetEmailValidCodeParam clientUserGetEmailValidCodeParam) {
        return CommonResult.data(clientUserService.bindEmailGetEmailValidCode(clientUserGetEmailValidCodeParam));
    }

    /**
     * 修改绑定邮箱获取邮箱验证码
     *
     * @author xuyuxiang
     * @date 2021/10/13 14:01
     **/
    @ApiOperationSupport(order = 15)
    @Operation(summary = "修改绑定邮箱获取邮箱验证码")
    @GetMapping("/client/userCenter/updateBindEmailGetEmailValidCode")
    public CommonResult<String> updateBindEmailGetEmailValidCode(@Valid ClientUserGetEmailValidCodeParam clientUserGetEmailValidCodeParam) {
        return CommonResult.data(clientUserService.updateBindEmailGetEmailValidCode(clientUserGetEmailValidCodeParam));
    }

    /**
     * 绑定邮箱
     *
     * @author xuyuxiang
     * @date 2021/10/13 14:01
     **/
    @ApiOperationSupport(order = 16)
    @Operation(summary = "绑定邮箱")
    @CommonLog("绑定邮箱")
    @PostMapping("/client/userCenter/bindEmail")
    public CommonResult<String> bindEmail(@RequestBody @Valid ClientUserBindEmailParam clientUserBindEmailParam) {
        clientUserService.bindEmail(clientUserBindEmailParam);
        return CommonResult.ok();
    }

    /**
     * 修改用户头像
     *
     * @author xuyuxiang
     * @date 2021/10/13 14:01
     **/
    @ApiOperationSupport(order = 17)
    @Operation(summary = "修改用户头像")
    @CommonLog("修改用户头像")
    @PostMapping("/client/userCenter/updateAvatar")
    public CommonResult<String> updateAvatar(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(clientUserService.updateAvatar(file));
    }

    /**
     * 修改用户签名图片
     *
     * @author xuyuxiang
     * @date 2021/10/13 14:01
     **/
    @ApiOperationSupport(order = 18)
    @Operation(summary = "修改用户签名图片")
    @CommonLog("修改用户签名图片")
    @PostMapping("/client/userCenter/updateSignature")
    public CommonResult<String> updateSignature(@RequestBody @Valid ClientUserSignatureParam clientUserSignatureParam) {
        clientUserService.updateSignature(clientUserSignatureParam);
        return CommonResult.ok();
    }

    /**
     * 编辑个人信息
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 19)
    @Operation(summary = "编辑个人信息")
    @CommonLog("编辑个人信息")
    @PostMapping("/client/userCenter/updateUserInfo")
    public CommonResult<String> updateUserInfo(@RequestBody @Valid ClientUserUpdateInfoParam clientUserUpdateInfoParam) {
        clientUserService.updateUserInfo(clientUserUpdateInfoParam);
        return CommonResult.ok();
    }

    /**
     * 根据id获取头像
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 20)
    @Operation(summary = "根据id获取头像")
    @GetMapping("/client/userCenter/getAvatarById")
    public CommonResult<String> getAvatarById(@Valid ClientUserIdParam clientUserIdParam) {
        return CommonResult.data(clientUserService.getAvatarById(clientUserIdParam));
    }

    /**
     * 判断当前用户是否需要绑定手机号
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 21)
    @Operation(summary = "判断当前用户是否需要绑定手机号")
    @GetMapping("/client/userCenter/isUserNeedBindPhone")
    public CommonResult<Boolean> isUserNeedBindPhone() {
        return CommonResult.data(clientUserService.isUserNeedBindPhone());
    }

    /**
     * 判断当前用户是否需要绑定邮箱
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 22)
    @Operation(summary = "判断当前用户是否需要绑定邮箱")
    @GetMapping("/client/userCenter/isUserNeedBindEmail")
    public CommonResult<Boolean> isUserNeedBindEmail() {
        return CommonResult.data(clientUserService.isUserNeedBindEmail());
    }

    /**
     * 判断当前用户密码是否过期
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 21)
    @Operation(summary = "判断当前用户密码是否过期")
    @GetMapping("/client/userCenter/isUserPasswordExpired")
    public CommonResult<Boolean> isUserPasswordExpired() {
        return CommonResult.data(clientUserService.isUserPasswordExpired());
    }
}
