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
package vip.xiaonuo.im.core.Interceptor;

import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import vip.xiaonuo.auth.core.enums.SaClientTypeEnum;

import java.net.URI;
import java.util.Map;

/**
 * WebSocket 握手的前置拦截器
 *
 * @author ChengChuanYao
 * @date 2024/7/18 14:21
 */
@Slf4j
public class WebSocketInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        URI uri = request.getURI();
        String path = uri.getPath();
        String rawQuery = uri.getRawQuery();
        log.info("请求参数:{}", rawQuery);
        String query = uri.getQuery();
        String value = SaClientTypeEnum.B.getValue();

        log.info("请求参数:{}", query);
        log.info("请求路径:{}", path);
        log.info("WebSocket 握手之前拦截");
        String tokenValue = StpUtil.getTokenValue();
        if (tokenValue == null) {
            log.error("未登录用户禁止连接WebSocket");
            return false;
        }
        attributes.put("userId", StpUtil.getLoginId());
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        log.info("WebSocket 握手之后拦截");
    }
}
