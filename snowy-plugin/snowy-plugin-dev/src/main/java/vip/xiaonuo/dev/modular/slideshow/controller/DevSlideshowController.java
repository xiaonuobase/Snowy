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
package vip.xiaonuo.dev.modular.slideshow.controller;

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
import vip.xiaonuo.dev.modular.slideshow.entity.DevSlideshow;
import vip.xiaonuo.dev.modular.slideshow.param.DevSlideshowAddParam;
import vip.xiaonuo.dev.modular.slideshow.param.DevSlideshowEditParam;
import vip.xiaonuo.dev.modular.slideshow.param.DevSlideshowIdParam;
import vip.xiaonuo.dev.modular.slideshow.param.DevSlideshowPageParam;
import vip.xiaonuo.dev.modular.slideshow.service.DevSlideshowService;

import java.util.List;

/**
 * 轮播图控制器
 *
 * @author yubaoshan
 * @date  2024/07/13 00:31
 */
@Tag(name = "轮播图控制器")
@RestController
@Validated
public class DevSlideshowController {

    @Resource
    private DevSlideshowService devSlideshowService;

    /**
     * 获取轮播图分页
     *
     * @author yubaoshan
     * @date  2024/07/13 00:31
     */
    @Operation(summary = "获取轮播图分页")
    @GetMapping("/dev/slideshow/page")
    public CommonResult<Page<DevSlideshow>> page(DevSlideshowPageParam devSlideshowPageParam) {
        return CommonResult.data(devSlideshowService.page(devSlideshowPageParam));
    }

    /**
     * 添加轮播图
     *
     * @author yubaoshan
     * @date  2024/07/13 00:31
     */
    @Operation(summary = "添加轮播图")
    @CommonLog("添加轮播图")
    @PostMapping("/dev/slideshow/add")
    public CommonResult<String> add(@RequestBody @Valid DevSlideshowAddParam devSlideshowAddParam) {
        devSlideshowService.add(devSlideshowAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑轮播图
     *
     * @author yubaoshan
     * @date  2024/07/13 00:31
     */
    @Operation(summary = "编辑轮播图")
    @CommonLog("编辑轮播图")
    @PostMapping("/dev/slideshow/edit")
    public CommonResult<String> edit(@RequestBody @Valid DevSlideshowEditParam devSlideshowEditParam) {
        devSlideshowService.edit(devSlideshowEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除轮播图
     *
     * @author yubaoshan
     * @date  2024/07/13 00:31
     */
    @Operation(summary = "删除轮播图")
    @CommonLog("删除轮播图")
    @PostMapping("/dev/slideshow/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<DevSlideshowIdParam> devSlideshowIdParamList) {
        devSlideshowService.delete(devSlideshowIdParamList);
        return CommonResult.ok();
    }

    /**
     * 禁用轮播图
     *
     * @author yubaoshan
     * @date  2024/07/13 00:31
     */
    @Operation(summary = "禁用轮播图")
    @CommonLog("禁用轮播图")
    @PostMapping("/dev/slideshow/disableStatus")
    public CommonResult<String> disableStatus(@RequestBody @Valid DevSlideshowIdParam devSlideshowIdParam) {
        devSlideshowService.disableStatus(devSlideshowIdParam);
        return CommonResult.ok();
    }

    /**
     * 启用轮播图
     *
     * @author yubaoshan
     * @date  2024/07/13 00:31
     */
    @Operation(summary = "启用轮播图")
    @CommonLog("启用轮播图")
    @PostMapping("/dev/slideshow/enableStatus")
    public CommonResult<String> enableStatus(@RequestBody @Valid DevSlideshowIdParam devSlideshowIdParam) {
        devSlideshowService.enableStatus(devSlideshowIdParam);
        return CommonResult.ok();
    }
}
