package vip.xiaonuo.im.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * web套接字配置
 *
 * @author liuchunming
 * @date 2024/5/21 17:35
 */
@Configuration
public class WebSocketConfigure
{
    /**
     * 注入一个ServerEndpointExporter,该Bean会自动注册使用@ServerEndpoint注解申明的websocket endpoint
     *
     * @return {@link ServerEndpointExporter }
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter()
    {
        return new ServerEndpointExporter();
    }
}
