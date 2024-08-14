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
package vip.xiaonuo.im.modular.message.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.im.modular.message.entity.ImMessage;
import vip.xiaonuo.im.modular.message.param.*;
import vip.xiaonuo.im.modular.message.service.ImMessageService;

import java.util.List;

/**
 * IM-消息控制器
 *
 * @author liuchunming
 * @date 2024/05/27 16:52
 */
@Tag(name = "IM-消息控制器")
@RestController
@Validated
public class ImMessageController {

    @Resource
    private ImMessageService imMessageService;

    /**
     * 获取IM-消息分页
     *
     * @author liuchunming
     * @date 2024/05/27 16:52
     */
    @Operation(summary = "获取IM-消息分页")
    @SaCheckPermission("/im/message/page")
    @GetMapping("/im/message/page")
    public CommonResult<Page<ImMessage>> page(ImMessagePageParam imMessagePageParam) {
        return CommonResult.data(imMessageService.page(imMessagePageParam));
    }

    /**
     * 添加IM-消息
     *
     * @author liuchunming
     * @date 2024/05/27 16:52
     */
    @Operation(summary = "添加IM-消息")
    @CommonLog("添加IM-消息")
    @SaCheckPermission("/im/message/add")
    @PostMapping("/im/message/add")
    public CommonResult<String> add(@RequestBody @Valid ImMessageAddParam imMessageAddParam) {
        imMessageService.add(imMessageAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑IM-消息
     *
     * @author liuchunming
     * @date 2024/05/27 16:52
     */
    @Operation(summary = "编辑IM-消息")
    @CommonLog("编辑IM-消息")
    @SaCheckPermission("/im/message/edit")
    @PostMapping("/im/message/edit")
    public CommonResult<String> edit(@RequestBody @Valid ImMessageEditParam imMessageEditParam) {
        imMessageService.edit(imMessageEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除IM-消息
     *
     * @author liuchunming
     * @date 2024/05/27 16:52
     */
    @Operation(summary = "删除IM-消息")
    @CommonLog("删除IM-消息")
    @SaCheckPermission("/im/message/delete")
    @PostMapping("/im/message/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                       List<ImMessageIdParam> imMessageIdParamList) {
        imMessageService.delete(imMessageIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取IM-消息详情
     *
     * @author liuchunming
     * @date 2024/05/27 16:52
     */
    @Operation(summary = "获取IM-消息详情")
    @SaCheckPermission("/im/message/detail")
    @GetMapping("/im/message/detail")
    public CommonResult<ImMessage> detail(@Valid ImMessageIdParam imMessageIdParam) {
        return CommonResult.data(imMessageService.detail(imMessageIdParam));
    }


    /**
     * 查询跟当前用户聊天的所有用户-分页
     *
     * @author chengchuanyao
     * @date 2024/7/19 18:43
     */
    @Operation(summary = "查询跟当前用户聊天的所有用户")
    @GetMapping("/im/message/queryChatRecord")
    public CommonResult<Page<ImMessageUserParam>> queryChatRecord() {
        return CommonResult.data(imMessageService.queryChatRecord());
    }

    /**
     * 查询当前用户和指定用户的聊天记录-分页
     *
     * @author chengchuanyao
     * @date 2024/7/20 11:51
     */
    @Operation(summary = "查询当前用户和指定用户的聊天记录")
    @GetMapping("/im/message/queryChatRecordWithUser")
    public CommonResult<Page<ImMessage>> queryChatRecordWithUser(@RequestParam(value = "userId") String userId, @RequestParam(defaultValue = "1", required = false, value = "chatType") String chatType) {
        return CommonResult.data(imMessageService.queryChatRecordWithUser(userId, chatType));
    }


    /**
     * 将消息设为已读
     *
     * @author chengchuanyao
     * @date 2024/7/24 14:44
     */
    @Operation(summary = "将消息设为已读")
    @PostMapping("/im/message/setRead")
    public CommonResult<String> setRead(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                        List<ImMessageIdParam> imMessageIdParamList) {
        imMessageService.setRead(imMessageIdParamList);
        return CommonResult.ok();
    }

    /**
     * 撤回消息
     *
     * @author chengchuanyao
     * @date 2024/7/25 18:12
     */
    @Operation(summary = "撤回消息")
    @PostMapping("/im/message/recall")
    public CommonResult<String> recall(@RequestBody @Valid ImMessageIdParam imMessageIdParam) {
        imMessageService.recall(imMessageIdParam);
        return CommonResult.ok();
    }

}
