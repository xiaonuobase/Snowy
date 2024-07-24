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
package vip.xiaonuo.im.core.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ExecutorBuilder;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import vip.xiaonuo.im.core.manager.WebSocketSessionManager;
import vip.xiaonuo.im.modular.message.entity.ImMessage;
import vip.xiaonuo.im.modular.message.service.ImMessageService;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * websocket工具类
 *
 * @author chengchuanyao
 * @date 2024/7/22 18:23
 */
@Slf4j
public class WebSocketUtil {

    private static ImMessageService imMessageService = SpringUtil.getBean(ImMessageService.class);

    // 线程池 根据自己机器的实际情况调整
    static ExecutorService executor = ExecutorBuilder.create()
            .setCorePoolSize(5)
            .setMaxPoolSize(10)
            .setWorkQueue(new LinkedBlockingQueue<>(100))
            .build();

    /**
     * 接收到消息并处理
     */
    public static String handleMessage(WebSocketSession session, TextMessage message) {
        executor.submit(() -> {
            // 获取消息
            String payload = message.getPayload();
            // 解析消息
            ImMessage imMessage = JSONUtil.toBean(payload, ImMessage.class);
            try {
                imMessage.setIsRead("2");
                imMessage.setStatus("1");
                imMessage.setIsRecall("2");
                imMessage.setId(String.valueOf(IdWorker.getId()));
                imMessage.setCreateTime(DateUtil.date());
                // 获取接收人id
                String toUserId = imMessage.getToUserId();
                // 获取接收人session
                WebSocketSession toSession = WebSocketSessionManager.SESSIONS.get(toUserId);
                // 让发送人回调消息数据
                session.sendMessage(new TextMessage(JSONUtil.toJsonStr(imMessage)));
                if(null!= toSession){
                    // 发送消息
                    toSession.sendMessage(new TextMessage(JSONUtil.toJsonStr(imMessage)));
                }
                // 存储消息
                boolean save = imMessageService.save(imMessage);
                if (!save) {
                    throw new RuntimeException("消息存储失败");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return null;
    }
}
