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
package vip.xiaonuo.sys.modular.resource.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.sys.modular.resource.entity.SysButton;
import vip.xiaonuo.sys.modular.resource.param.button.SysButtonAddParam;
import vip.xiaonuo.sys.modular.resource.param.button.SysButtonEditParam;
import vip.xiaonuo.sys.modular.resource.param.button.SysButtonIdParam;
import vip.xiaonuo.sys.modular.resource.param.button.SysButtonPageParam;
import vip.xiaonuo.sys.modular.resource.service.SysButtonService;

import java.util.List;

/**
 * 按钮控制器
 *
 * @author xuyuxiang
 * @date 2022/6/27 13:56
 **/
@Tag(name = "按钮控制器")
@RestController
@Validated
public class SysButtonController {

    @Resource
    private SysButtonService sysButtonService;

    /**
     * 获取按钮分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @Operation(summary = "获取按钮分页")
    @GetMapping("/sys/button/page")
    public CommonResult<Page<SysButton>> page(SysButtonPageParam sysButtonPageParam) {
        return CommonResult.data(sysButtonService.page(sysButtonPageParam));
    }

    /**
     * 添加按钮
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @Operation(summary = "添加按钮")
    @CommonLog("添加按钮")
    @PostMapping("/sys/button/add")
    public CommonResult<String> add(@RequestBody @Valid SysButtonAddParam sysButtonAddParam) {
        sysButtonService.add(sysButtonAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑按钮
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @Operation(summary = "编辑按钮")
    @CommonLog("编辑按钮")
    @PostMapping("/sys/button/edit")
    public CommonResult<String> edit(@RequestBody @Valid SysButtonEditParam sysButtonEditParam) {
        sysButtonService.edit(sysButtonEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除按钮
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @Operation(summary = "删除按钮")
    @CommonLog("删除按钮")
    @PostMapping("/sys/button/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")  List<SysButtonIdParam> sysButtonIdParamList) {
        sysButtonService.delete(sysButtonIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取按钮详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @Operation(summary = "获取按钮详情")
    @GetMapping("/sys/button/detail")
    public CommonResult<SysButton> detail(@Valid SysButtonIdParam sysButtonIdParam) {
        return CommonResult.data(sysButtonService.detail(sysButtonIdParam));
    }
}
