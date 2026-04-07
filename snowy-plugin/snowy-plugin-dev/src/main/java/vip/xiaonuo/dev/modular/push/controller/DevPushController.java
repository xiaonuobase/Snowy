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
package vip.xiaonuo.dev.modular.push.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.dev.modular.push.entity.DevPush;
import vip.xiaonuo.dev.modular.push.param.*;
import vip.xiaonuo.dev.modular.push.service.DevPushService;

import javax.validation.Valid;
import java.util.List;

/**
 * 消息推送控制器
 *
 * @author xuyuxiang
 * @date 2022/2/23 18:26
 **/
@Tag(name = "消息推送控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 11)
@RestController
@Validated
public class DevPushController {

    @Resource
    private DevPushService devPushService;

    /**
     * 动态推送消息
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 1)
    @Operation(summary = "动态推送TEXT消息")
    @CommonLog("动态推送TEXT消息")
    @PostMapping("/dev/push/pushDynamicText")
    public CommonResult<String> pushDynamicText(@RequestBody @Valid DevPushDynamicTextParam devPushDynamicTextParam) {
        devPushService.pushDynamicText(devPushDynamicTextParam);
        return CommonResult.ok();
    }

    /**
     * 推送消息——飞书TXT
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 2)
    @Operation(summary = "推送飞书TEXT消息")
    @CommonLog("推送飞书TEXT消息")
    @PostMapping("/dev/push/pushFeiShuText")
    public CommonResult<String> pushFeiShuText(@RequestBody @Valid DevPushFeiShuTextParam devPushFeiShuTextParam) {
        devPushService.pushFeiShuText(devPushFeiShuTextParam);
        return CommonResult.ok();
    }

    /**
     * 推送消息——钉钉TXT
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 3)
    @Operation(summary = "推送钉钉TEXT消息")
    @CommonLog("推送钉钉TEXT消息")
    @PostMapping("/dev/push/pushDingTalkText")
    public CommonResult<String> pushDingTalkText(@RequestBody @Valid DevPushDingTalkTextParam devPushDingTalkTextParam) {
        devPushService.pushDingTalkText(devPushDingTalkTextParam);
        return CommonResult.ok();
    }

    /**
     * 推送消息——钉钉MARKDOWN
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 4)
    @Operation(summary = "推送钉钉MARKDOWN消息")
    @CommonLog("推送钉钉MARKDOWN消息")
    @PostMapping("/dev/push/pushDingTalkMarkdown")
    public CommonResult<String> pushDingTalkMarkdown(@RequestBody @Valid DevPushDingTalkMarkdownParam devPushDingTalkMarkdownParam) {
        devPushService.pushDingTalkMarkdown(devPushDingTalkMarkdownParam);
        return CommonResult.ok();
    }

    /**
     * 推送消息——钉钉LINK
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 5)
    @Operation(summary = "推送钉钉LINK消息")
    @CommonLog("推送钉钉LINK消息")
    @PostMapping("/dev/push/pushDingTalkLink")
    public CommonResult<String> pushDingTalkLink(@RequestBody @Valid DevPushDingTalkLinkParam devPushDingTalkLinkParam) {
        devPushService.pushDingTalkLink(devPushDingTalkLinkParam);
        return CommonResult.ok();
    }

    /**
     * 推送消息——企业微信TXT
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 6)
    @Operation(summary = "推送企业微信TEXT消息")
    @CommonLog("推送企业微信TEXT消息")
    @PostMapping("/dev/push/pushWorkWechatText")
    public CommonResult<String> pushWorkWechatText(@RequestBody @Valid DevPushWorkWechatTextParam devPushWorkWechatTextParam) {
        devPushService.pushWorkWechatText(devPushWorkWechatTextParam);
        return CommonResult.ok();
    }

    /**
     * 推送消息——企业微信MARKDOWN
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 7)
    @Operation(summary = "推送企业微信MARKDOWN消息")
    @CommonLog("推送企业微信MARKDOWN消息")
    @PostMapping("/dev/push/pushWorkWechatMarkdown")
    public CommonResult<String> pushWorkWechatMarkdown(@RequestBody @Valid DevPushWorkWechatMarkdownParam devPushWorkWechatMarkdownParam) {
        devPushService.pushWorkWechatMarkdown(devPushWorkWechatMarkdownParam);
        return CommonResult.ok();
    }

    /**
     * 推送消息——企业微信NEWS
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 8)
    @Operation(summary = "推送企业微信NEWS消息")
    @CommonLog("推送企业微信NEWS消息")
    @PostMapping("/dev/push/pushWorkWechatNews")
    public CommonResult<String> pushWorkWechatNews(@RequestBody @Valid DevPushWorkWechatNewsParam devPushWorkWechatNewsParam) {
        devPushService.pushWorkWechatNews(devPushWorkWechatNewsParam);
        return CommonResult.ok();
    }

    /**
     * 获取推送消息分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 9)
    @Operation(summary = "获取推送消息分页")
    @GetMapping("/dev/push/page")
    public CommonResult<Page<DevPush>> page(DevPushPageParam devPushPageParam) {
        return CommonResult.data(devPushService.page(devPushPageParam));
    }

    /**
     * 删除推送消息
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 10)
    @Operation(summary = "删除推送消息")
    @CommonLog("删除推送消息")
    @PostMapping("/dev/push/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                       List<DevPushIdParam> devPushIdParamList) {
        devPushService.delete(devPushIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取推送消息详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 11)
    @Operation(summary = "获取推送消息详情")
    @GetMapping("/dev/push/detail")
    public CommonResult<DevPush> detail(@Valid DevPushIdParam devPushIdParam) {
        return CommonResult.data(devPushService.detail(devPushIdParam));
    }
}
