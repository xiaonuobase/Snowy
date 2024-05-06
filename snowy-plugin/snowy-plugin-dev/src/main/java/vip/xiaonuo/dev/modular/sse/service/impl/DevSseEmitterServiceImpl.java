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
package vip.xiaonuo.dev.modular.sse.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.common.sse.CommonSseParam;
import vip.xiaonuo.dev.modular.sse.service.DevSseEmitterService;
import vip.xiaonuo.dev.modular.sse.util.DevSseCacheUtil;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * SSE通信Service接口实现类
 *
 * @author diantu
 * @date 2023/7/3
 **/
@Slf4j
@Service
public class DevSseEmitterServiceImpl implements DevSseEmitterService {

    /**
     * 心跳线程池
     */
    private static final ScheduledExecutorService heartbeatExecutors = Executors.newScheduledThreadPool(10);


    /**
     * 创建连接
     *
     * @author diantu
     * @date 2023/7/3
     **/
    @Override
    public SseEmitter createSseConnect(String clientId, Boolean setHeartBeat, Boolean defaultHeartbeat, Consumer<CommonSseParam> consumer) {
        // 设置超时时间，0表示不过期。默认30秒，超过时间未完成会抛出异常：AsyncRequestTimeoutException
        SseEmitter sseEmitter = new SseEmitter(0L);
        String loginId = StpUtil.getLoginIdAsString();
        // 判断连接是否有效
        if (DevSseCacheUtil.connectionValidity(clientId,loginId)) {
            return DevSseCacheUtil.getSseEmitterByClientId(clientId);
        }else{
            DevSseCacheUtil.removeConnection(clientId);
        }
        clientId = IdUtil.simpleUUID();
        String finalClientId = clientId;
        // 增加心跳
        final ScheduledFuture<?> future;
        // 是否自定义心跳任务
        if (setHeartBeat!=null&&setHeartBeat) {
            //是否使用默认心跳任务
            if(defaultHeartbeat!=null&&defaultHeartbeat){
                //默认心跳任务
                future = heartbeatExecutors.scheduleAtFixedRate(() ->
                                DevSseCacheUtil.sendMessageToOneClient(finalClientId,finalClientId+"-"+loginId),
                        2, 10, TimeUnit.SECONDS);
            }else{
                //自定义心跳任务
                CommonSseParam commonSseParam = new CommonSseParam();
                commonSseParam.setClientId(clientId);
                commonSseParam.setLoginId(loginId);
                future = heartbeatExecutors.scheduleAtFixedRate(() -> consumer.accept(commonSseParam),
                        2, 10, TimeUnit.SECONDS);
            }
            // 增加连接
            DevSseCacheUtil.addConnection(clientId, loginId, sseEmitter, future);
        } else {
            // 增加连接
            DevSseCacheUtil.addConnection(clientId, loginId, sseEmitter, null);
        }
        // 长链接完成后回调(即关闭连接时调用)
        sseEmitter.onCompletion(DevSseCacheUtil.completionCallBack(clientId));
        // 连接超时回调
        sseEmitter.onTimeout(DevSseCacheUtil.timeoutCallBack(clientId));
        // 推送消息异常回调
        sseEmitter.onError(DevSseCacheUtil.errorCallBack(clientId));
        // 初次建立连接,推送客户端id
        CommonResult<String> message = new CommonResult<>(0,"",clientId);
        DevSseCacheUtil.sendMessageToClientByClientId(clientId,message);
        return sseEmitter;
    }

    /**
     * 关闭连接
     *
     * @author diantu
     * @date 2023/7/3
     **/
    @Override
    public void closeSseConnect(String clientId){
        DevSseCacheUtil.removeConnection(clientId);
    }

    /**
     * 推送消息到所有客户端
     *
     * @author diantu
     * @date 2023/7/3
     **/
    @Override
    public void sendMessageToAllClient(String msg) {
        DevSseCacheUtil.sendMessageToAllClient(msg);
    }

    /**
     * 根据clientId发送消息给某一客户端
     *
     * @author diantu
     * @date 2023/7/3
     **/
    @Override
    public void sendMessageToOneClient(String clientId, String msg) {
        DevSseCacheUtil.sendMessageToOneClient(clientId,msg);
    }

}
