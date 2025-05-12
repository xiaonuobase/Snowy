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
package vip.xiaonuo.biz.modular.group.controller;

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
import vip.xiaonuo.biz.modular.group.entity.BizGroup;
import vip.xiaonuo.biz.modular.group.param.*;
import vip.xiaonuo.biz.modular.group.service.BizGroupService;
import vip.xiaonuo.biz.modular.user.entity.BizUser;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;

import java.util.List;

/**
 * 用户组控制器
 *
 * @author yubaoshan
 * @date  2024/12/24 03:33
 */
@Tag(name = "用户组控制器")
@RestController
@Validated
public class BizGroupController {

    @Resource
    private BizGroupService bizGroupService;

    /**
     * 获取用户组分页
     *
     * @author yubaoshan
     * @date  2024/12/24 03:33
     */
    @Operation(summary = "获取用户组分页")
    @SaCheckPermission("/biz/group/page")
    @GetMapping("/biz/group/page")
    public CommonResult<Page<BizGroup>> page(BizGroupPageParam bizGroupPageParam) {
        return CommonResult.data(bizGroupService.page(bizGroupPageParam));
    }

    /**
     * 添加用户组
     *
     * @author yubaoshan
     * @date  2024/12/24 03:33
     */
    @Operation(summary = "添加用户组")
    @CommonLog("添加用户组")
    @SaCheckPermission("/biz/group/add")
    @PostMapping("/biz/group/add")
    public CommonResult<String> add(@RequestBody @Valid BizGroupAddParam bizGroupAddParam) {
        bizGroupService.add(bizGroupAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑用户组
     *
     * @author yubaoshan
     * @date  2024/12/24 03:33
     */
    @Operation(summary = "编辑用户组")
    @CommonLog("编辑用户组")
    @SaCheckPermission("/biz/group/edit")
    @PostMapping("/biz/group/edit")
    public CommonResult<String> edit(@RequestBody @Valid BizGroupEditParam bizGroupEditParam) {
        bizGroupService.edit(bizGroupEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除用户组
     *
     * @author yubaoshan
     * @date  2024/12/24 03:33
     */
    @Operation(summary = "删除用户组")
    @CommonLog("删除用户组")
    @SaCheckPermission("/biz/group/delete")
    @PostMapping("/biz/group/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<BizGroupIdParam> bizGroupIdParamList) {
        bizGroupService.delete(bizGroupIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取用户组详情
     *
     * @author yubaoshan
     * @date  2024/12/24 03:33
     */
    @Operation(summary = "获取用户组详情")
    @SaCheckPermission("/biz/group/detail")
    @GetMapping("/biz/group/detail")
    public CommonResult<BizGroup> detail(@Valid BizGroupIdParam bizGroupIdParam) {
        return CommonResult.data(bizGroupService.detail(bizGroupIdParam));
    }

    /**
     * 获取用户组下的用户
     *
     * @author yubaoshan
     * @date 2024/12/21 01:25
     */
    @Operation(summary = "获取用户组下的用户")
    @SaCheckPermission("/biz/group/ownUser")
    @GetMapping("/biz/group/ownUser")
    public CommonResult<List<String>> ownUser(@Valid BizGroupIdParam bizGroupIdParam) {
        return CommonResult.data(bizGroupService.ownUser(bizGroupIdParam));
    }

    /**
     * 获取组织树选择器
     *
     * @author yubaoshan
     * @date 2024/12/21 01:25
     */
    @Operation(summary = "获取组织树选择器")
    @SaCheckPermission("/biz/group/orgTreeSelector")
    @GetMapping("/biz/group/orgTreeSelector")
    public CommonResult<List<Tree<String>>> orgTreeSelector() {
        return CommonResult.data(bizGroupService.orgTreeSelector());
    }

    /**
     * 获取用户选择器
     *
     * @author yubaoshan
     * @date 2024/12/21 01:25
     */
    @Operation(summary = "获取用户选择器")
    @SaCheckPermission("/biz/group/userSelector")
    @GetMapping("/biz/group/userSelector")
    public CommonResult<Page<BizUser>> userSelector(BizGroupSelectorUserParam bizGroupSelectorUserParam) {
        return CommonResult.data(bizGroupService.userSelector(bizGroupSelectorUserParam));
    }

    /**
     * 给用户组授权用户
     *
     * @author yubaoshan
     * @date 2024/12/21 01:25
     */
    @Operation(summary = "给用户组授权用户")
    @CommonLog("给用户组授权用户")
    @SaCheckPermission("/biz/group/grantUser")
    @PostMapping("/biz/group/grantUser")
    public CommonResult<String> grantUser(@RequestBody @Valid BizGroupGrantUserParam bizGroupGrantUserParam) {
        bizGroupService.grantUser(bizGroupGrantUserParam);
        return CommonResult.ok();
    }
}
