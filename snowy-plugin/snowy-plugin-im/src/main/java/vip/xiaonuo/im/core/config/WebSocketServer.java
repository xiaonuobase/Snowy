///*
// * Copyright [2022] [https://www.xiaonuo.vip]
// *
// * Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
// *
// * 1.请不要删除和修改根目录下的LICENSE文件。
// * 2.请不要删除和修改Snowy源码头部的版权声明。
// * 3.本项目代码可免费商业使用，商业使用请保留源码和相关描述文件的项目出处，作者声明等。
// * 4.分发源码时候，请注明软件出处 https://www.xiaonuo.vip
// * 5.不可二次分发开源参与同类竞品，如有想法可联系团队xiaonuobase@qq.com商议合作。
// * 6.若您的项目无法满足以上几点，需要更多功能代码，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
// */
//package vip.xiaonuo.im.core.config;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import jakarta.websocket.*;
//import jakarta.websocket.server.PathParam;
//import jakarta.websocket.server.ServerEndpoint;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.WebSocketSession;
//
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.atomic.AtomicInteger;
//
///**
// * 前后端交互的类实现消息的接收推送
// *
// * @author liuchunming
// * @date 2024/5/21 17:46
// */
//@Slf4j
////@ServerEndpoint(value = "/ws/{userId}")
////@Component
//public class WebSocketServer {
//    private Map<String, Integer> NAMES;
//
//    /**
//     * 记录当前在线连接数
//     */
//    private static AtomicInteger onlineCount = new AtomicInteger(0);
//
//    /**
//     * 存放所有在线的客户端
//     */
//    private static Map<String, WebSocketSession> clients = new ConcurrentHashMap<>();
//
//    /**
//     * 连接建立成功调用的方法
//     */
//    @OnOpen
//    public void onOpen(Session session, @PathParam("userId") String userId) {
//        // 在线数加1
//        onlineCount.incrementAndGet();
//        clients.put(userId, session);
//        log.info("有新连接加入：{}，当前在线人数为：{}, 用户id:{}", session.getId(), onlineCount.get(), userId);
//    }
//
//    /**
//     * 连接关闭调用的方法
//     */
//    @OnClose
//    public void onClose(Session session, @PathParam("userId") String userId) {
//        // 在线数减1
//        onlineCount.decrementAndGet();
//        clients.remove(userId);
//        log.info("有一连接关闭：{}，当前在线人数为：{}", session.getId(), onlineCount.get());
//    }
//
//    /**
//     * 收到客户端消息后调用的方法
//     *
//     * @param message 客户端发送过来的消息
//     *                这里能过地址获取用户userId与ws session关联至map中
//     */
//    @OnMessage
//    public void onMessage(String message, Session session, @PathParam("userId") String userId) {
//        log.info("服务端收到客户端[{}]的消息[{}]", session.getId(), message);
//        try {
//            JSONObject myMessage = JSON.parseObject(message);
//            if (myMessage != null) {
//                Session toSession = null;
//                if (myMessage.getString("userId") != null) {
//                    // 取到接收userId session
//                    toSession = clients.get(myMessage.getString("userId"));
//                }
//                if (toSession != null) {
//                    this.sendMessage(myMessage.get("message").toString(), toSession);
//                } else {
//                    log.info("type:" + myMessage.getString("type"));
//                    if (myMessage.getString("type").equals("all")) {
//                        //群发
//                        this.sendMessageAll(myMessage.get("message").toString(), userId);
//                    } else {
//                        log.error("session不存在");
//                    }
//
//                }
//            }
//        } catch (Exception e) {
//            log.error("解析失败：{}", e);
//        }
//    }
//
//    @OnError
//    public void onError(Session session, Throwable error) {
//        log.error("发生错误");
//        error.printStackTrace();
//    }
//
//    /**
//     * 服务端发送消息给客户端
//     */
//    private void sendMessage(String message, Session toSession) {
//        try {
//            log.info("服务端给客户端[{}]发送消息[{}]", toSession.getId(), message);
//            toSession.getBasicRemote().sendText(message);
//        } catch (Exception e) {
//            log.error("服务端发送消息给客户端失败：{}", e);
//        }
//    }
//
//    /**
//     * 群发消息
//     *
//     * @param message 消息内容
//     */
//    private void sendMessageAll(String message, String userId) {
//        for (Map.Entry<String, Session> sessionEntry : clients.entrySet()) {
//            Session toSession = sessionEntry.getValue();
//            // 排除掉自己
//            if (!userId.equals(sessionEntry.getKey())) {
//                //log.info("服务端给客户端[{}]发送消息{}", toSession.getId(), message);
//                log.info("服务端给客户端用户id:[{}]发送消息{}", userId, message);
//                toSession.getAsyncRemote().sendText(message);
//            }
//        }
//    }
//
//
//}
