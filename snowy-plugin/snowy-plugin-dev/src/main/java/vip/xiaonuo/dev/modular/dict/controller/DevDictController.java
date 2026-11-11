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
package vip.xiaonuo.dev.modular.dict.controller;

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
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.dev.modular.dict.entity.DevDict;
import vip.xiaonuo.dev.modular.dict.param.*;
import vip.xiaonuo.dev.modular.dict.service.DevDictService;

import java.util.List;

/**
 * 字典控制器
 *
 * @author xuyuxiang
 * @date 2022/6/21 14:58
 **/
@Tag(name = "字典控制器")
@RestController
@Validated
public class DevDictController {

    @Resource
    private DevDictService devDictService;

    /**
     * 获取字典分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @Operation(summary = "获取字典分页")
    @GetMapping("/dev/dict/page")
    public CommonResult<Page<DevDict>> page(DevDictPageParam devDictPageParam) {
        return CommonResult.data(devDictService.page(devDictPageParam));
    }

    /**
     * 获取字典列表
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @Operation(summary = "获取字典列表")
    @GetMapping("/dev/dict/list")
    public CommonResult<List<DevDict>> list(DevDictListParam devDictListParam) {
        return CommonResult.data(devDictService.list(devDictListParam));
    }

    /**
     * 获取字典树
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @Operation(summary = "获取字典树")
    @GetMapping("/dev/dict/tree")
    public CommonResult<List<Tree<String>>> tree(DevDictTreeParam devDictTreeParam) {
        return CommonResult.data(devDictService.tree(devDictTreeParam));
    }

    /**
     * 添加字典
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @Operation(summary = "添加字典")
    @CommonLog("添加字典")
    @PostMapping("/dev/dict/add")
    public CommonResult<String> add(@RequestBody @Valid DevDictAddParam devDictAddParam) {
        devDictService.add(devDictAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑字典
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @Operation(summary = "编辑字典")
    @CommonLog("编辑字典")
    @PostMapping("/dev/dict/edit")
    public CommonResult<String> edit(@RequestBody @Valid DevDictEditParam devDictEditParam) {
        devDictService.edit(devDictEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除字典
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @Operation(summary = "删除字典")
    @CommonLog("删除字典")
    @PostMapping("/dev/dict/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                           List<DevDictIdParam> devDictIdParamList) {
        devDictService.delete(devDictIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取字典详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @Operation(summary = "获取字典详情")
    @GetMapping("/dev/dict/detail")
    public CommonResult<DevDict> detail(@Valid DevDictIdParam devDictIdParam) {
        return CommonResult.data(devDictService.detail(devDictIdParam));
    }
}
