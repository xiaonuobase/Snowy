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
package vip.xiaonuo.biz.modular.index.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.biz.modular.index.param.BizIndexNoticeIdParam;
import vip.xiaonuo.biz.modular.index.param.BizIndexNoticeListParam;
import vip.xiaonuo.biz.modular.index.param.BizIndexSlideshowListParam;
import vip.xiaonuo.biz.modular.index.result.BizIndexNoticeListResult;
import vip.xiaonuo.biz.modular.index.result.BizIndexSlideshowDetailResult;
import vip.xiaonuo.biz.modular.index.result.BizIndexSlideshowListResult;
import vip.xiaonuo.biz.modular.index.service.BizIndexService;
import vip.xiaonuo.common.pojo.CommonResult;

import java.util.List;

/**
 * 业务首页控制器
 *
 * @author yubaoshan
 * @date 2024/7/13 21:02
 */
@Tag(name = "业务首页控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 0)
@RestController
@Validated
public class BizIndexController {

    @Resource
    private BizIndexService bizIndexService;

    /**
     * 获取轮播图列表
     *
     * @author yubaoshan
     * @date 2024/7/13 21:02
     */
    @ApiOperationSupport(order = 1)
    @Operation(summary = "获取轮播图列表")
    @GetMapping("/biz/index/slideshow/list")
    public CommonResult<List<BizIndexSlideshowListResult>> slideshowListByPlace(@Valid BizIndexSlideshowListParam bizIndexSlideshowListParam) {
        return CommonResult.data(bizIndexService.slideshowListByPlace(bizIndexSlideshowListParam));
    }

    /**
     * 获取通知公告列表
     *
     * @author yubaoshan
     * @date 2024/7/13 21:02
     */
    @ApiOperationSupport(order = 2)
    @Operation(summary = "获取通知公告列表")
    @GetMapping("/biz/index/notice/list")
    public CommonResult<List<BizIndexNoticeListResult>> noticeListByLimit(BizIndexNoticeListParam bizIndexNoticeListParam) {
        return CommonResult.data(bizIndexService.noticeListByLimit(bizIndexNoticeListParam));
    }

    /**
     * 获取通知公告详情
     *
     * @author yubaoshan
     * @date 2024/7/13 21:02
     */
    @ApiOperationSupport(order = 3)
    @Operation(summary = "获取通知公告详情")
    @GetMapping("/biz/index/notice/detail")
    public CommonResult<BizIndexSlideshowDetailResult> noticeDetailById(@Valid BizIndexNoticeIdParam bizIndexNoticeIdParam) {
        return CommonResult.data(bizIndexService.noticeDetailById(bizIndexNoticeIdParam));
    }
}
