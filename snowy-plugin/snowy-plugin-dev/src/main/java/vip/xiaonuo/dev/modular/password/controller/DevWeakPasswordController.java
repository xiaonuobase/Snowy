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
package vip.xiaonuo.dev.modular.password.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.dev.modular.password.entity.DevWeakPassword;
import vip.xiaonuo.dev.modular.password.param.DevWeakPasswordAddParam;
import vip.xiaonuo.dev.modular.password.param.DevWeakPasswordEditParam;
import vip.xiaonuo.dev.modular.password.param.DevWeakPasswordIdParam;
import vip.xiaonuo.dev.modular.password.param.DevWeakPasswordPageParam;
import vip.xiaonuo.dev.modular.password.service.DevWeakPasswordService;

import javax.validation.Valid;
import java.util.List;

/**
 * 弱密码库控制器
 *
 * @author xuyuxiang
 * @date 2022/4/25 20:40
 */
@Tag(name = "弱密码库控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 2)
@RestController
@Validated
public class DevWeakPasswordController {

    @Resource
    private DevWeakPasswordService devWeakPasswordService;

    /**
     * 获取弱密码库分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 1)
    @Operation(summary = "获取弱密码库分页")
    @GetMapping("/dev/weakPassword/page")
    public CommonResult<Page<DevWeakPassword>> page(DevWeakPasswordPageParam devWeakPasswordPageParam) {
        return CommonResult.data(devWeakPasswordService.page(devWeakPasswordPageParam));
    }

    /**
     * 添加弱密码库
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 2)
    @Operation(summary = "添加弱密码库")
    @CommonLog("添加弱密码库")
    @PostMapping("/dev/weakPassword/add")
    public CommonResult<String> add(@RequestBody @Valid DevWeakPasswordAddParam devWeakPasswordAddParam) {
        devWeakPasswordService.add(devWeakPasswordAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑弱密码库
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 3)
    @Operation(summary = "编辑弱密码库")
    @CommonLog("编辑弱密码库")
    @PostMapping("/dev/weakPassword/edit")
    public CommonResult<String> edit(@RequestBody @Valid DevWeakPasswordEditParam devWeakPasswordEditParam) {
        devWeakPasswordService.edit(devWeakPasswordEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除弱密码库
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 4)
    @Operation(summary = "删除弱密码库")
    @CommonLog("删除弱密码库")
    @PostMapping("/dev/weakPassword/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<DevWeakPasswordIdParam> devWeakPasswordIdParamList) {
        devWeakPasswordService.delete(devWeakPasswordIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取弱密码库详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 5)
    @Operation(summary = "获取弱密码库详情")
    @GetMapping("/dev/weakPassword/detail")
    public CommonResult<DevWeakPassword> detail(@Valid DevWeakPasswordIdParam devWeakPasswordIdParam) {
        return CommonResult.data(devWeakPasswordService.detail(devWeakPasswordIdParam));
    }
}
