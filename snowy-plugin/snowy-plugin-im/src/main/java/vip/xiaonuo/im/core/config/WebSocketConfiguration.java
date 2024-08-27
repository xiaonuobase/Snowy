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
package vip.xiaonuo.im.core.config;

import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import vip.xiaonuo.im.core.Interceptor.WebSocketInterceptor;
import vip.xiaonuo.im.core.auth.AuthorizationManager;
import vip.xiaonuo.im.core.handler.ImWebSocketHandler;

import java.util.List;
import java.util.Optional;

/**
 * web套接字配置
 *
 * @author liuchunming
 * @date 2024/5/21 17:35
 */
@EnableWebSocket
@Configuration
@EnableConfigurationProperties(WebSocketConfig.class)
//@ConditionalOnProperty(prefix = "snowy.websocket", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
public class WebSocketConfiguration implements WebSocketConfigurer {

    private final WebSocketConfig webSocketConfig;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        if (ObjectUtil.isEmpty(webSocketConfig) || ObjectUtil.isEmpty(webSocketConfig.getPath())) {
            webSocketConfig.setPath(List.of("/ws/im"));
        }

        if (!AuthorizationManager.verifySign()){
            System.err.println("im模块websocket配置授权码失败");
        }else{
            System.out.println("im模块websocket配置授权码成功");
        }
        registry.addHandler(new ImWebSocketHandler(), webSocketConfig.getPath().toArray(String[]::new))
                .addInterceptors(new WebSocketInterceptor())
                .setAllowedOrigins(Optional.ofNullable(webSocketConfig.getAllowedOrigins()).orElse("*"));
    }
}
