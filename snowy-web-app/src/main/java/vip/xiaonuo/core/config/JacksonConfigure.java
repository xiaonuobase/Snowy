package vip.xiaonuo.core.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Jackson序列化配置
 * 用于处理返回结果null值改为类型初始值等问题
 */
@Configuration
public class JacksonConfigure {

    private static final ThreadLocal<Boolean> IS_APP_REQUEST = ThreadLocal.withInitial(() -> false);

    @Bean
    @Primary
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();

        objectMapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
            @Override
            public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
                // 若是APP端请求，则处理响应结果字段默认值
                if (IS_APP_REQUEST.get()) {
                    handleNullValue(gen);
                } else {
                    gen.writeNull();
                }
            }

            private void handleNullValue(JsonGenerator gen) throws IOException {
                JsonStreamContext context = gen.getOutputContext();
                if (context.inObject()) {
                    String fieldName = context.getCurrentName();
                    Object currentObj = context.getCurrentValue();
                    if (currentObj != null) {
                        Class<?> clazz = currentObj.getClass();
                        Field field = getField(clazz, fieldName);
                        if (field != null) {
                            setDefaultByType(field.getType(), gen);
                            return;
                        }
                    }
                }
                // 默认处理
                gen.writeString("");
            }

            private Field getField(Class<?> clazz, String fieldName) {
                try {
                    Field field = clazz.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    return field;
                } catch (NoSuchFieldException e) {
                    // 查找父类字段
                    Class<?> superClass = clazz.getSuperclass();
                    while (superClass != null) {
                        try {
                            Field field = superClass.getDeclaredField(fieldName);
                            field.setAccessible(true);
                            return field;
                        } catch (NoSuchFieldException ex) {
                            superClass = superClass.getSuperclass();
                        }
                    }
                }
                return null;
            }

            private void setDefaultByType(Class<?> type, JsonGenerator gen) throws IOException {
                if (type.isPrimitive()) {
                    handlePrimitive(type, gen);
                } else if (type == String.class) {
                    gen.writeString("");
                } else if (Number.class.isAssignableFrom(type)) {
                    gen.writeNumber(0);
                } else if (type == Boolean.class) {
                    gen.writeBoolean(false);
                } else if (type == Date.class) {
                    gen.writeString("");
                } else if (type.isArray() || Collection.class.isAssignableFrom(type)) {
                    gen.writeStartArray();
                    gen.writeEndArray();
                } else if (Map.class.isAssignableFrom(type)) {
                    gen.writeStartObject();
                    gen.writeEndObject();
                } else {
                    gen.writeStartObject();
                    gen.writeEndObject();
                }
            }

            private void handlePrimitive(Class<?> type, JsonGenerator gen) throws IOException {
                if (type == int.class) {
                    gen.writeNumber(0);
                } else if (type == long.class) {
                    gen.writeNumber(0L);
                } else if (type == double.class) {
                    gen.writeNumber(0.0);
                } else if (type == float.class) {
                    gen.writeNumber(0.0f);
                } else if (type == boolean.class) {
                    gen.writeBoolean(false);
                } else if (type == char.class) {
                    gen.writeString(String.valueOf('\0'));
                } else {
                    gen.writeNull();
                }
            }
        });

        return objectMapper;
    }

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(new HandlerInterceptor() {
                    @Override
                    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                        IS_APP_REQUEST.set(isAppRequest(request));
                        return true;
                    }

                    @Override
                    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
                        IS_APP_REQUEST.remove();
                    }
                });
            }
        };
    }

    /**
     * 判断是否为移动端请求
     * path根据自己项目的实际路径进行判断
     */
    private boolean isAppRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        String userAgent = request.getHeader("User-Agent");
        return path.startsWith("/client/c/app/") || path.startsWith("/auth/c/");
    }
}
