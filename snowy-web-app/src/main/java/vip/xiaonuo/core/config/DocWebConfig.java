package vip.xiaonuo.core.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.util.Base64;

@Configuration
public class DocWebConfig implements WebMvcConfigurer {

    @Value("${knife4j.basic.username}")
    private String ymlUsername;

    @Value("${knife4j.basic.password}")
    private String ymlPassword;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Basic ")) {
                    String base64Credentials = authHeader.substring("Basic ".length());
                    String credentials = new String(Base64.getDecoder().decode(base64Credentials));
                    final String[] parts = credentials.split(":", 2);
                    String username = parts[0];
                    String password = parts[1];

                    // 这里硬编码测试，实际应从配置或数据库读取
                    if (ymlUsername.equals(username) && ymlPassword.equals(password)) {
                        return true; // 认证通过
                    }
                }

                response.setHeader("WWW-Authenticate", "Basic realm=\"API Docs\"");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                return false;
            }
        }).addPathPatterns("/doc.html", "/v3/api-docs/**");
    }
}
