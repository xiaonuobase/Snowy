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
package vip.xiaonuo.im.core.manager;

import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

/**
 * websocket session 管理器
 *
 * @author ChengChuanYao
 * @date 2024/7/18 10:09
 */
public class WebSocketSessionManager {

    public final static String USER_ID = "userId";

    public final static String FROM_USER_TYPE = "fromUserType";

    /**
     * session集合
     */
    public static final ConcurrentHashMap<String, WebSocketSession> SESSIONS = new ConcurrentHashMap<>();

    /**
     * 添加session
     */
    public static void add(WebSocketSession session) {
        SESSIONS.put(session.getAttributes().get(USER_ID).toString(), session);
    }

    /**
     * 移除session
     */
    public static WebSocketSession remove(String userId) {
        if (SESSIONS.containsKey(userId)) {
            return SESSIONS.remove(userId);
        } else {
            return null;
        }
    }

    /**
     * 移除session 并关闭连接
     */
    public static void removeAndClose(String userId) {
        WebSocketSession remove = remove(userId);
        if (remove != null) {
            try {
                remove.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取session
     */
    public static WebSocketSession get(String userId) {
        return SESSIONS.get(userId);
    }

}
