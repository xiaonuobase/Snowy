package vip.xiaonuo.im.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author ChengChuanYao
 * @date 2024/7/18 16:50
 */

@Data
@ConfigurationProperties("snowy.websocket")
public class WebSocketConfig {

    private Boolean enabled;

    private List<String> path;

    private String allowedOrigins;

}
