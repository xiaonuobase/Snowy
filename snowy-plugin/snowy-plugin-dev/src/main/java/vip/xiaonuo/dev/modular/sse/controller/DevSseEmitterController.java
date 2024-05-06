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
package vip.xiaonuo.dev.modular.sse.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import vip.xiaonuo.common.sse.CommonSseParam;
import vip.xiaonuo.dev.modular.sse.service.DevSseEmitterService;

import java.util.function.Consumer;

/**
 * SSE通信控制器
 *
 * @author diantu
 * @date 2023/7/3
 **/
@Tag(name = "SSE通信控制器")
@RestController
@Validated
public class DevSseEmitterController {

    @Resource
    private DevSseEmitterService devSseEmitterService;

    /**
     * 创建sse连接
     *
     * @author diantu
     * @date 2023/7/3
     **/
    @Operation(summary = "创建sse连接")
    @GetMapping("/dev/sse/createConnect")
    public SseEmitter createConnect(String clientId,
                                    @RequestParam(required = false)Boolean setHeartBeat,
                                    @RequestParam(required = false)Boolean defaultHeartbeat,
                                    @RequestParam(required = false)Consumer<CommonSseParam> consumer){
        return devSseEmitterService.createSseConnect(clientId,setHeartBeat,defaultHeartbeat,consumer);
    }

    /**
     * 关闭sse连接
     *
     * @author diantu
     * @date 2023/7/3
     **/
    @Operation(summary = "关闭sse连接")
    @GetMapping("/dev/sse/closeSseConnect")
    public void closeSseConnect(String clientId){
        devSseEmitterService.closeSseConnect(clientId);
    }

    /**
     * 推送消息到所有客户端
     *
     * @author diantu
     * @date 2023/7/3
     **/
    @Operation(summary = "推送消息到所有客户端")
    @PostMapping("/dev/sse/broadcast")
    public void sendMessageToAllClient(@RequestBody(required = false) String msg){
        devSseEmitterService.sendMessageToAllClient(msg);
    }

    /**
     * 根据clientId发送消息给某一客户端
     *
     * @author diantu
     * @date 2023/7/3
     **/
    @Operation(summary = "根据clientId发送消息给某一客户端")
    @PostMapping("/dev/sse/sendMessage")
    public void sendMessageToOneClient(String clientId,String msg){
        devSseEmitterService.sendMessageToOneClient(clientId,msg);
    }

}
