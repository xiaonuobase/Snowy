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
package vip.xiaonuo.im.modular.member.controller;

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
import vip.xiaonuo.im.modular.member.entity.ImGroupMember;
import vip.xiaonuo.im.modular.member.param.ImGroupMemberAddParam;
import vip.xiaonuo.im.modular.member.param.ImGroupMemberEditParam;
import vip.xiaonuo.im.modular.member.param.ImGroupMemberIdParam;
import vip.xiaonuo.im.modular.member.param.ImGroupMemberPageParam;
import vip.xiaonuo.im.modular.member.service.ImGroupMemberService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * IM-群组成员控制器
 *
 * @author liuchunming
 * @date  2024/05/27 16:48
 */
@Tag(name = "IM-群组成员控制器")
@RestController
@Validated
public class ImGroupMemberController {

    @Resource
    private ImGroupMemberService imGroupMemberService;

    /**
     * 获取IM-群组成员分页
     *
     * @author liuchunming
     * @date  2024/05/27 16:48
     */
    @Operation(summary = "获取IM-群组成员分页")
    @SaCheckPermission("/im/member/page")
    @GetMapping("/im/member/page")
    public CommonResult<Page<ImGroupMember>> page(ImGroupMemberPageParam imGroupMemberPageParam) {
        return CommonResult.data(imGroupMemberService.page(imGroupMemberPageParam));
    }

    /**
     * 添加IM-群组成员
     *
     * @author liuchunming
     * @date  2024/05/27 16:48
     */
    @Operation(summary = "添加IM-群组成员")
    @CommonLog("添加IM-群组成员")
    @SaCheckPermission("/im/member/add")
    @PostMapping("/im/member/add")
    public CommonResult<String> add(@RequestBody @Valid ImGroupMemberAddParam imGroupMemberAddParam) {
        imGroupMemberService.add(imGroupMemberAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑IM-群组成员
     *
     * @author liuchunming
     * @date  2024/05/27 16:48
     */
    @Operation(summary = "编辑IM-群组成员")
    @CommonLog("编辑IM-群组成员")
    @SaCheckPermission("/im/member/edit")
    @PostMapping("/im/member/edit")
    public CommonResult<String> edit(@RequestBody @Valid ImGroupMemberEditParam imGroupMemberEditParam) {
        imGroupMemberService.edit(imGroupMemberEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除IM-群组成员
     *
     * @author liuchunming
     * @date  2024/05/27 16:48
     */
    @Operation(summary = "删除IM-群组成员")
    @CommonLog("删除IM-群组成员")
    @SaCheckPermission("/im/member/delete")
    @PostMapping("/im/member/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<ImGroupMemberIdParam> imGroupMemberIdParamList) {
        imGroupMemberService.delete(imGroupMemberIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取IM-群组成员详情
     *
     * @author liuchunming
     * @date  2024/05/27 16:48
     */
    @Operation(summary = "获取IM-群组成员详情")
    @SaCheckPermission("/im/member/detail")
    @GetMapping("/im/member/detail")
    public CommonResult<ImGroupMember> detail(@Valid ImGroupMemberIdParam imGroupMemberIdParam) {
        return CommonResult.data(imGroupMemberService.detail(imGroupMemberIdParam));
    }
}
