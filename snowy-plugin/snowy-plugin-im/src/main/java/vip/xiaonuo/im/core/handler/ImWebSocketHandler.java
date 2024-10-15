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
package vip.xiaonuo.im.core.handler;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import vip.xiaonuo.im.core.manager.WebSocketSessionManager;
import vip.xiaonuo.im.core.utils.WebSocketUtil;

import java.io.IOException;

/**
 * websocket处理器
 *
 * @author ChengChuanYao
 * @date 2024/7/18 9:55
 */
@Component
@Slf4j
public class ImWebSocketHandler extends TextWebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        WebSocketSessionManager.add(session);
        WebSocketUtil.sendMessage(session, JSONUtil.createObj().set("msg", "连接成功").toString());
        log.info("当前连接数：" + WebSocketSessionManager.SESSIONS.size());
        // 给当前用户发送在线用户列表
        WebSocketUtil.sendOnlineUserList(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        WebSocketUtil.handleMessage(session, message);
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        log.info("发送二进制消息");
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("异常处理");
        WebSocketSessionManager.removeAndClose(session.getAttributes().get(WebSocketSessionManager.USER_ID).toString());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("关闭ws连接");
        WebSocketSessionManager.removeAndClose(session.getAttributes().get(WebSocketSessionManager.USER_ID).toString());
        WebSocketUtil.sendUnOnlineUserList(session);
    }
}
