package vip.xiaonuo.dev.core.config;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletContext;
import jakarta.websocket.server.ServerContainer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket配置
 *
 * @author yubaoshan
 * @date 2025/12/24 18:01
 */
@Configuration
@EnableScheduling
public class WebSocketConfig {

    @Resource
    private ApplicationContext applicationContext;

    /**
     * 注入ServerEndpointExporter，这个bean会自动注册使用了@ServerEndpoint注解声明的Websocket endpoint
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        if (!(applicationContext instanceof WebApplicationContext webAppContext)) return null;

        ServletContext servletContext = webAppContext.getServletContext();
        if (servletContext == null) return null;

        ServerContainer serverContainer = (ServerContainer) servletContext.getAttribute(ServerContainer.class.getName());
        if (serverContainer == null) return null;

        return new ServerEndpointExporter();
    }
}
