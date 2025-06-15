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
package vip.xiaonuo.auth.modular.login.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.auth.core.annotation.SaClientCheckLogin;
import vip.xiaonuo.auth.core.enums.SaClientTypeEnum;
import vip.xiaonuo.auth.core.pojo.SaBaseClientLoginUser;
import vip.xiaonuo.auth.core.util.StpClientUtil;
import vip.xiaonuo.auth.modular.login.param.*;
import vip.xiaonuo.auth.modular.login.result.AuthPicValidCodeResult;
import vip.xiaonuo.auth.modular.login.service.AuthService;
import vip.xiaonuo.common.pojo.CommonResult;

import javax.validation.Valid;

/**
 * C端登录控制器
 *
 * @author xuyuxiang
 * @date 2021/12/23 21:50
 */
@Tag(name = "C端登录控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 1)
@RestController
@Validated
public class AuthClientController {

    @Resource
    private AuthService authService;

    /**
     * C端获取图片验证码
     *
     * @author xuyuxiang
     * @date 2022/7/8 9:26
     **/
    @ApiOperationSupport(order = 1)
    @Operation(summary = "C端获取图片验证码")
    @GetMapping("/auth/c/getPicCaptcha")
    public CommonResult<AuthPicValidCodeResult> getPicCaptcha() {
        return CommonResult.data(authService.getPicCaptcha(SaClientTypeEnum.C.getValue()));
    }

    /**
     * C端获取手机登录验证码
     *
     * @author xuyuxiang
     * @date 2022/7/8 9:26
     **/
    @ApiOperationSupport(order = 2)
    @Operation(summary = "C端获取手机登录验证码")
    @GetMapping("/auth/c/getPhoneValidCode")
    public CommonResult<String> getPhoneValidCode(@Valid AuthGetPhoneValidCodeParam authGetPhoneValidCodeParam) {
        return CommonResult.data(authService.getPhoneValidCode(authGetPhoneValidCodeParam, SaClientTypeEnum.C.getValue()));
    }

    /**
     * C端获取邮箱登录验证码
     *
     * @author xuyuxiang
     * @date 2022/7/8 9:26
     **/
    @ApiOperationSupport(order = 3)
    @Operation(summary = "C端获取邮箱登录验证码")
    @GetMapping("/auth/c/getEmailValidCode")
    public CommonResult<String> getEmailValidCode(@Valid AuthGetEmailValidCodeParam authGetEmailValidCodeParam) {
        return CommonResult.data(authService.getEmailValidCode(authGetEmailValidCodeParam, SaClientTypeEnum.C.getValue()));
    }

    /**
     * C端账号密码登录
     *
     * @author xuyuxiang
     * @date 2021/10/15 13:12
     **/
    @ApiOperationSupport(order = 4)
    @Operation(summary = "C端账号密码登录")
    @PostMapping("/auth/c/doLogin")
    public CommonResult<String> doLogin(@RequestBody @Valid AuthAccountPasswordLoginParam authAccountPasswordLoginParam) {
        return CommonResult.data(authService.doLogin(authAccountPasswordLoginParam, SaClientTypeEnum.C.getValue()));
    }

    /**
     * C端手机验证码登录
     *
     * @author xuyuxiang
     * @date 2021/10/15 13:12
     **/
    @ApiOperationSupport(order = 5)
    @Operation(summary = "C端手机验证码登录")
    @PostMapping("/auth/c/doLoginByPhone")
    public CommonResult<String> doLoginByPhone(@RequestBody @Valid AuthPhoneValidCodeLoginParam authPhoneValidCodeLoginParam) {
        return CommonResult.data(authService.doLoginByPhone(authPhoneValidCodeLoginParam, SaClientTypeEnum.C.getValue()));
    }

    /**
     * C端邮箱验证码登录
     *
     * @author xuyuxiang
     * @date 2021/10/15 13:12
     **/
    @ApiOperationSupport(order = 6)
    @Operation(summary = "C端邮箱验证码登录")
    @PostMapping("/auth/c/doLoginByEmail")
    public CommonResult<String> doLoginByEmail(@RequestBody @Valid AuthEmailValidCodeLoginParam authEmailValidCodeLoginParam) {
        return CommonResult.data(authService.doLoginByEmail(authEmailValidCodeLoginParam, SaClientTypeEnum.C.getValue()));
    }

    /**
     * C端退出
     *
     * @author xuyuxiang
     * @date 2021/10/15 13:12
     **/
    @ApiOperationSupport(order = 7)
    @Operation(summary = "C端退出")
    @SaClientCheckLogin
    @GetMapping("/auth/c/doLogout")
    public CommonResult<String> doLogout() {
        StpClientUtil.logout();
        return CommonResult.ok();
    }

    /**
     * C端获取用户信息
     *
     * @author xuyuxiang
     * @date 2021/10/15 13:12
     **/
    @ApiOperationSupport(order = 8)
    @Operation(summary = "C端获取用户信息")
    @SaClientCheckLogin
    @GetMapping("/auth/c/getLoginUser")
    public CommonResult<SaBaseClientLoginUser> getLoginUser() {
        return CommonResult.data(authService.getClientLoginUser());
    }

    /**
     * C端注册
     *
     * @author xuyuxiang
     * @date 2021/10/15 13:12
     **/
    @ApiOperationSupport(order = 9)
    @Operation(summary = "C端注册")
    @PostMapping("/auth/c/register")
    public CommonResult<String> register(@RequestBody @Valid AuthRegisterParam authRegisterParam) {
        authService.register(authRegisterParam, SaClientTypeEnum.C.getValue());
        return CommonResult.ok();
    }
}
