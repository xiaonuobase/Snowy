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
package vip.xiaonuo.auth.modular.sso.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.auth.core.enums.SaClientTypeEnum;
import vip.xiaonuo.auth.modular.sso.param.AuthSsoTicketLoginParam;
import vip.xiaonuo.auth.modular.sso.service.AuthSsoService;
import vip.xiaonuo.common.pojo.CommonResult;

import javax.validation.Valid;

/**
 * 单点登录控制器
 *
 * @author xuyuxiang
 * @date 2022/8/30 9:20
 **/
@Tag(name = "单点登录控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 4)
@RestController
@Validated
public class AuthSsoController {

    @Resource
    private AuthSsoService authSsoService;

    /**
     * 根据ticket执行单点登录
     *
     * @author xuyuxiang
     * @date 2021/10/15 13:12
     **/
    @ApiOperationSupport(order = 1)
    @Operation(summary = "根据ticket执行单点登录")
    @PostMapping("/auth/sso/doLogin")
    public CommonResult<String> doLogin(@RequestBody @Valid AuthSsoTicketLoginParam authAccountPasswordLoginParam) {
        return CommonResult.data(authSsoService.doLogin(authAccountPasswordLoginParam, SaClientTypeEnum.B.getValue()));
    }
}
