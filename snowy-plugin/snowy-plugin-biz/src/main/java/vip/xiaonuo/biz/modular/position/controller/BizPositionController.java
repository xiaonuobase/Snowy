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
package vip.xiaonuo.biz.modular.position.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.lang.tree.Tree;
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
import vip.xiaonuo.biz.modular.position.entity.BizPosition;
import vip.xiaonuo.biz.modular.position.param.*;
import vip.xiaonuo.biz.modular.position.service.BizPositionService;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;

import java.util.List;

/**
 * 岗位控制器
 *
 * @author xuyuxiang
 * @date 2022/4/25 20:40
 */
@Tag(name = "岗位控制器")
@RestController
@Validated
public class BizPositionController {

    @Resource
    private BizPositionService bizPositionService;

    /**
     * 获取岗位分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @Operation(summary = "获取岗位分页")
    @SaCheckPermission("/biz/position/page")
    @GetMapping("/biz/position/page")
    public CommonResult<Page<BizPosition>> page(BizPositionPageParam bizPositionPageParam) {
        return CommonResult.data(bizPositionService.page(bizPositionPageParam));
    }

    /**
     * 添加岗位
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @Operation(summary = "添加岗位")
    @CommonLog("添加岗位")
    @SaCheckPermission("/biz/position/add")
    @PostMapping("/biz/position/add")
    public CommonResult<String> add(@RequestBody @Valid BizPositionAddParam bizPositionAddParam) {
        bizPositionService.add(bizPositionAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑岗位
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @Operation(summary = "编辑岗位")
    @CommonLog("编辑岗位")
    @SaCheckPermission("/biz/position/edit")
    @PostMapping("/biz/position/edit")
    public CommonResult<String> edit(@RequestBody @Valid BizPositionEditParam bizPositionEditParam) {
        bizPositionService.edit(bizPositionEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除岗位
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @Operation(summary = "删除岗位")
    @CommonLog("删除岗位")
    @SaCheckPermission("/biz/position/delete")
    @PostMapping("/biz/position/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<BizPositionIdParam> bizPositionIdParamList) {
        bizPositionService.delete(bizPositionIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取岗位详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @Operation(summary = "获取岗位详情")
    @SaCheckPermission("/biz/position/detail")
    @GetMapping("/biz/position/detail")
    public CommonResult<BizPosition> detail(@Valid BizPositionIdParam bizPositionIdParam) {
        return CommonResult.data(bizPositionService.detail(bizPositionIdParam));
    }

    /* ====岗位部分所需要用到的选择器==== */

    /**
     * 获取组织树选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @Operation(summary = "获取组织树选择器")
    @SaCheckPermission("/biz/position/orgTreeSelector")
    @GetMapping("/biz/position/orgTreeSelector")
    public CommonResult<List<Tree<String>>> orgTreeSelector() {
        return CommonResult.data(bizPositionService.orgTreeSelector());
    }

    /**
     * 获取岗位选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @Operation(summary = "获取岗位选择器")
    @SaCheckPermission("/biz/position/positionSelector")
    @GetMapping("/biz/position/positionSelector")
    public CommonResult<Page<BizPosition>> positionSelector(BizPositionSelectorPositionParam bizPositionSelectorPositionParam) {
        return CommonResult.data(bizPositionService.positionSelector(bizPositionSelectorPositionParam));
    }
}
