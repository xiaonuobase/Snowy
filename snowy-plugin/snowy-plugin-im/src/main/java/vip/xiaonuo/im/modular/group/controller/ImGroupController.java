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
package vip.xiaonuo.im.modular.group.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.im.modular.group.entity.ImGroup;
import vip.xiaonuo.im.modular.group.param.ImGroupAddParam;
import vip.xiaonuo.im.modular.group.param.ImGroupEditParam;
import vip.xiaonuo.im.modular.group.param.ImGroupIdParam;
import vip.xiaonuo.im.modular.group.param.ImGroupPageParam;
import vip.xiaonuo.im.modular.group.service.ImGroupService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * IM-群组控制器
 *
 * @author liuchunming
 * @date  2024/05/27 16:40
 */
@Tag(name = "IM-群组控制器")
@RestController
@Validated
public class ImGroupController {

    @Resource
    private ImGroupService imGroupService;

    /**
     * 获取IM-群组分页
     *
     * @author liuchunming
     * @date  2024/05/27 16:40
     */
    @Operation(summary = "获取IM-群组分页")
    @SaCheckPermission("/im/group/page")
    @GetMapping("/im/group/page")
    public CommonResult<Page<ImGroup>> page(ImGroupPageParam imGroupPageParam) {
        return CommonResult.data(imGroupService.page(imGroupPageParam));
    }

    /**
     * 查询当前用户的群组列表
     *
     * @author chengchuanyao
     * @date 2024/7/27 14:28
     */
    @Operation(summary = "查询当前用户的群组列表")
    @GetMapping("/im/group/listByUser")
    public CommonResult<List<ImGroup>> listByUser() {
        return CommonResult.data(imGroupService.listByUser());
    }


    /**
     * 添加IM-群组
     *
     * @author liuchunming
     * @date  2024/05/27 16:40
     */
    @Operation(summary = "添加IM-群组")
    @CommonLog("添加IM-群组")
    @SaCheckPermission("/im/group/add")
    @PostMapping("/im/group/add")
    public CommonResult<String> add(@RequestBody @Valid ImGroupAddParam imGroupAddParam) {
        imGroupService.add(imGroupAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑IM-群组
     *
     * @author liuchunming
     * @date  2024/05/27 16:40
     */
    @Operation(summary = "编辑IM-群组")
    @CommonLog("编辑IM-群组")
    @SaCheckPermission("/im/group/edit")
    @PostMapping("/im/group/edit")
    public CommonResult<String> edit(@RequestBody @Valid ImGroupEditParam imGroupEditParam) {
        imGroupService.edit(imGroupEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除IM-群组
     *
     * @author liuchunming
     * @date  2024/05/27 16:40
     */
    @Operation(summary = "删除IM-群组")
    @CommonLog("删除IM-群组")
    @SaCheckPermission("/im/group/delete")
    @PostMapping("/im/group/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<ImGroupIdParam> imGroupIdParamList) {
        imGroupService.delete(imGroupIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取IM-群组详情
     *
     * @author liuchunming
     * @date  2024/05/27 16:40
     */
    @Operation(summary = "获取IM-群组详情")
    @GetMapping("/im/group/detail")
    public CommonResult<ImGroup> detail(@Valid ImGroupIdParam imGroupIdParam) {
        return CommonResult.data(imGroupService.detail(imGroupIdParam));
    }
}
