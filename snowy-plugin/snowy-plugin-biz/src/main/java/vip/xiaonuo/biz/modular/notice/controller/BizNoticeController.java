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
package vip.xiaonuo.biz.modular.notice.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
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
import vip.xiaonuo.biz.modular.notice.entity.BizNotice;
import vip.xiaonuo.biz.modular.notice.param.BizNoticeAddParam;
import vip.xiaonuo.biz.modular.notice.param.BizNoticeEditParam;
import vip.xiaonuo.biz.modular.notice.param.BizNoticeIdParam;
import vip.xiaonuo.biz.modular.notice.param.BizNoticePageParam;
import vip.xiaonuo.biz.modular.notice.service.BizNoticeService;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;

import java.util.List;

/**
 * 通知公告控制器
 *
 * @author yubaoshan
 * @date  2024/07/11 14:46
 */
@Tag(name = "通知公告控制器")
@RestController
@Validated
public class BizNoticeController {

    @Resource
    private BizNoticeService bizNoticeService;

    /**
     * 获取通知公告分页
     *
     * @author yubaoshan
     * @date  2024/07/11 14:46
     */
    @Operation(summary = "获取通知公告分页")
    @SaCheckPermission("/biz/notice/page")
    @GetMapping("/biz/notice/page")
    public CommonResult<Page<BizNotice>> page(BizNoticePageParam bizNoticePageParam) {
        return CommonResult.data(bizNoticeService.page(bizNoticePageParam));
    }

    /**
     * 添加通知公告
     *
     * @author yubaoshan
     * @date  2024/07/11 14:46
     */
    @Operation(summary = "添加通知公告")
    @CommonLog("添加通知公告")
    @SaCheckPermission("/biz/notice/add")
    @PostMapping("/biz/notice/add")
    public CommonResult<String> add(@RequestBody @Valid BizNoticeAddParam bizNoticeAddParam) {
        bizNoticeService.add(bizNoticeAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑通知公告
     *
     * @author yubaoshan
     * @date  2024/07/11 14:46
     */
    @Operation(summary = "编辑通知公告")
    @CommonLog("编辑通知公告")
    @SaCheckPermission("/biz/notice/edit")
    @PostMapping("/biz/notice/edit")
    public CommonResult<String> edit(@RequestBody @Valid BizNoticeEditParam bizNoticeEditParam) {
        bizNoticeService.edit(bizNoticeEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除通知公告
     *
     * @author yubaoshan
     * @date  2024/07/11 14:46
     */
    @Operation(summary = "删除通知公告")
    @CommonLog("删除通知公告")
    @SaCheckPermission("/biz/notice/delete")
    @PostMapping("/biz/notice/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<BizNoticeIdParam> bizNoticeIdParamList) {
        bizNoticeService.delete(bizNoticeIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取通知公告详情
     *
     * @author yubaoshan
     * @date  2024/07/11 14:46
     */
    @Operation(summary = "获取通知公告详情")
    @SaCheckPermission("/biz/notice/detail")
    @GetMapping("/biz/notice/detail")
    public CommonResult<BizNotice> detail(@Valid BizNoticeIdParam bizNoticeIdParam) {
        return CommonResult.data(bizNoticeService.detail(bizNoticeIdParam));
    }

    /**
     * 禁用通知公告
     *
     * @author yubaoshan
     * @date  2024/07/11 14:46
     */
    @Operation(summary = "禁用通知公告")
    @CommonLog("禁用通知公告")
    @SaCheckPermission("/biz/notice/disableStatus")
    @PostMapping("/biz/notice/disableStatus")
    public CommonResult<String> disableStatus(@RequestBody @Valid BizNoticeIdParam bizNoticeIdParam) {
        bizNoticeService.disableStatus(bizNoticeIdParam);
        return CommonResult.ok();
    }

    /**
     * 启用通知公告
     *
     * @author yubaoshan
     * @date  2024/07/11 14:46
     */
    @Operation(summary = "启用通知公告")
    @CommonLog("启用通知公告")
    @SaCheckPermission("/biz/notice/enableStatus")
    @PostMapping("/biz/notice/enableStatus")
    public CommonResult<String> enableStatus(@RequestBody @Valid BizNoticeIdParam bizNoticeIdParam) {
        bizNoticeService.enableStatus(bizNoticeIdParam);
        return CommonResult.ok();
    }
}
