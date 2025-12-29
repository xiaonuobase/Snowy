package vip.xiaonuo.dev.modular.message.websocket;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vip.xiaonuo.dev.modular.message.service.DevMessageService;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 站内信WebSocket
 *
 * @author yubaoshan
 * @date 2025/12/24 18:01
 */
@Component
@Slf4j
@ServerEndpoint("/dev/message/ws")
public class DevMessageWebSocket {

    /**
     * WebSocket Session池，key: sessionId, value: session
     */
    private static final ConcurrentHashMap<String, Session> SESSION_POOL = new ConcurrentHashMap<>();

    /**
     * SessionUser映射，key: sessionId, value: userId
     */
    private static final ConcurrentHashMap<String, String> SESSION_USER_MAP = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        try {
            String queryString = session.getQueryString();
            if (ObjectUtil.isEmpty(queryString)) {
                session.close();
                return;
            }
            Map<String, String> paramMap = HttpUtil.decodeParamMap(queryString, Charset.defaultCharset());
            String token = paramMap.get("token");
            if (ObjectUtil.isEmpty(token)) {
                session.close();
                return;
            }

            // 验证token
            Object loginId = StpUtil.getLoginIdByToken(token);
            if (ObjectUtil.isEmpty(loginId)) {
                session.close();
                return;
            }

            String userId = loginId.toString();
            SESSION_POOL.put(session.getId(), session);
            SESSION_USER_MAP.put(session.getId(), userId);

            // 发送初始未读数量
            sendUnreadCount(session, userId);

        } catch (Exception e) {
            log.error("WebSocket onOpen error", e);
        }
    }

    @OnClose
    public void onClose(Session session) {
        SESSION_POOL.remove(session.getId());
        SESSION_USER_MAP.remove(session.getId());
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket onError", error);
        onClose(session);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // 收到消息，暂不处理
    }

    /**
     * 发送未读消息数量给指定用户
     *
     * @param userId 用户ID
     */
    public static void sendMessage(String userId) {
        SESSION_USER_MAP.forEach((sessionId, sessionUserId) -> {
            if (sessionUserId.equals(userId)) {
                Session session = SESSION_POOL.get(sessionId);
                if (session != null && session.isOpen()) {
                    sendUnreadCount(session, userId);
                }
            }
        });
    }

    /**
     * 发送未读消息数量给指定用户列表
     *
     * @author yubaoshan
     * @date 2025/12/24 22:20
     * @param userIds 用户ID列表
     */
    public static void sendMessage(List<String> userIds) {
        Set<String> userIdSet = new HashSet<>(userIds);
        SESSION_USER_MAP.forEach((sessionId, sessionUserId) -> {
            if (userIdSet.contains(sessionUserId)) {
                Session session = SESSION_POOL.get(sessionId);
                if (session != null && session.isOpen()) {
                    sendUnreadCount(session, sessionUserId);
                }
            }
        });
    }

    /**
     * 发送未读消息数量
     */
    private static void sendUnreadCount(Session session, String userId) {
        try {
            if (session.isOpen()) {
                DevMessageService devMessageService = SpringUtil.getBean(DevMessageService.class);
                Long count = devMessageService.unreadCount(userId);
                Map<String, Object> result = Map.of("code", 200, "data", count);
                session.getBasicRemote().sendText(JSONUtil.toJsonStr(result));
            }
        } catch (IOException e) {
            log.error("Send message error", e);
        }
    }
}
