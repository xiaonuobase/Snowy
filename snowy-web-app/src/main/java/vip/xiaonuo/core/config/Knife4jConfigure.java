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
package vip.xiaonuo.core.config;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.core.conf.ExtensionsConstants;
import com.github.xiaoymin.knife4j.core.model.MarkdownProperty;
import com.github.xiaoymin.knife4j.extend.filter.basic.JakartaServletSecurityBasicAuthFilter;
import com.github.xiaoymin.knife4j.spring.configuration.Knife4jHttpBasic;
import com.github.xiaoymin.knife4j.spring.configuration.Knife4jProperties;
import com.github.xiaoymin.knife4j.spring.configuration.Knife4jSetting;
import com.github.xiaoymin.knife4j.spring.extension.Knife4jJakartaOperationCustomizer;
import com.github.xiaoymin.knife4j.spring.extension.OpenApiExtensionResolver;
import com.github.xiaoymin.knife4j.spring.filter.JakartaProductionSecurityFilter;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.customizers.GlobalOperationCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.web.method.HandlerMethod;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Knife4j兼容性配置，替代原始Knife4jAutoConfiguration（已在Application中排除）。
 * 解决knife4j 4.5.0与springdoc 2.8.x的版本不兼容问题：
 * knife4j 4.5.0编译时SpringDocConfigProperties.getGroupConfigs()返回List，
 * 但springdoc 2.6.0+改为返回Set，导致NoSuchMethodError。
 *
 * @author xuyuxiang
 * @date 2026/4/7
 */
@Configuration
@EnableConfigurationProperties({Knife4jProperties.class, Knife4jSetting.class, Knife4jHttpBasic.class})
@ConditionalOnProperty(name = "knife4j.enable", havingValue = "true")
public class Knife4jConfigure {

    /**
     * 兼容springdoc 2.8.x的OpenApiCustomizer
     */
    @Bean
    @ConditionalOnMissingBean
    public GlobalOpenApiCustomizer knife4jOpenApiCustomizer(Knife4jProperties knife4jProperties) {
        return new CompatibleKnife4jOpenApiCustomizer(knife4jProperties);
    }

    /**
     * knife4j Jakarta操作自定义器
     */
    @Bean
    @ConditionalOnMissingBean
    public Knife4jJakartaOperationCustomizer knife4jJakartaOperationCustomizer() {
        return new Knife4jJakartaOperationCustomizer();
    }

    /**
     * 接口自动排序自定义器：按方法在Controller中的声明顺序自动设置x-order，
     * 无需手动编写@ApiOperationSupport(order = x)。
     * 若方法已通过@ApiOperationSupport指定了order > 0，则优先使用手动指定的值。
     * 使用@Order(LOWEST_PRECEDENCE)确保在Knife4jJakartaOperationCustomizer之后执行，
     * 防止被其覆盖。
     */
    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    public GlobalOperationCustomizer autoOrderOperationCustomizer() {
        return new AutoOrderOperationCustomizer();
    }

    /**
     * knife4j Basic认证过滤器
     */
    @Bean
    @ConditionalOnMissingBean
    public JakartaServletSecurityBasicAuthFilter securityBasicAuthFilter(Knife4jProperties knife4jProperties,
                                                                        Environment environment) {
        JakartaServletSecurityBasicAuthFilter authFilter = new JakartaServletSecurityBasicAuthFilter();
        boolean enableBasicAuth = Boolean.parseBoolean(
                environment.getProperty("knife4j.basic.enable", "false"));
        authFilter.setEnableBasicAuth(enableBasicAuth);
        if (enableBasicAuth) {
            authFilter.setUserName(environment.getProperty("knife4j.basic.username", "admin"));
            authFilter.setPassword(environment.getProperty("knife4j.basic.password", "123321"));
        }
        if (knife4jProperties.getBasic() != null && knife4jProperties.getBasic().isEnable()) {
            authFilter.setEnableBasicAuth(true);
            authFilter.setUserName(knife4jProperties.getBasic().getUsername());
            authFilter.setPassword(knife4jProperties.getBasic().getPassword());
            if (knife4jProperties.getBasic().getInclude() != null) {
                authFilter.addRule(knife4jProperties.getBasic().getInclude());
            }
        }
        return authFilter;
    }

    /**
     * knife4j生产环境安全过滤器
     */
    @Bean
    @ConditionalOnMissingBean
    public JakartaProductionSecurityFilter productionSecurityFilter(Environment environment) {
        JakartaProductionSecurityFilter filter = new JakartaProductionSecurityFilter();
        String prodStr = environment.getProperty("knife4j.production");
        if (prodStr != null) {
            filter.setProduction(Boolean.parseBoolean(prodStr));
        }
        return filter;
    }

    /**
     * 兼容springdoc 2.8.x的Knife4jOpenApiCustomizer实现
     */
    static class CompatibleKnife4jOpenApiCustomizer implements GlobalOpenApiCustomizer {

        private final Knife4jProperties knife4jProperties;

        public CompatibleKnife4jOpenApiCustomizer(Knife4jProperties knife4jProperties) {
            this.knife4jProperties = knife4jProperties;
        }

        @Override
        public void customise(OpenAPI openApi) {
            if (knife4jProperties.isEnable()) {
                Knife4jSetting setting = knife4jProperties.getSetting();
                List<MarkdownProperty> documents = knife4jProperties.getDocuments();
                OpenApiExtensionResolver resolver = new OpenApiExtensionResolver(
                        setting, documents != null ? documents : Collections.emptyList());
                Map<String, Object> objectMap = new HashMap<>();
                objectMap.put("x-setting", setting);
                objectMap.put("x-markdownFiles", resolver.getMarkdownFiles());
                openApi.addExtension("x-openapi", objectMap);
            }
        }

    }

    /**
     * 自动排序自定义器：根据方法在Controller类中的声明顺序自动分配x-order值。
     * 效果：Knife4j文档中接口顺序与源码中方法书写顺序一致，无需手动编号。
     * 兼容：若方法已通过@ApiOperationSupport(order > 0)手动指定顺序，则优先使用手动值。
     * 实现：通过Spring内置的ASM读取.class文件获取可靠的方法声明顺序，
     * 不依赖getDeclaredMethods()（JDK 7+不保证返回源码顺序）。
     */
    static class AutoOrderOperationCustomizer implements GlobalOperationCustomizer {

        private final Map<Class<?>, Map<String, Integer>> classMethodOrderCache = new ConcurrentHashMap<>();

        @Override
        public Operation customize(Operation operation, HandlerMethod handlerMethod) {
            // 如果已通过@ApiOperationSupport手动指定了order > 0，优先使用
            ApiOperationSupport support = handlerMethod.getMethodAnnotation(ApiOperationSupport.class);
            if (support != null && support.order() > 0) {
                operation.addExtension(ExtensionsConstants.EXTENSION_ORDER, support.order());
                return operation;
            }
            // 自动按方法声明顺序分配order
            Class<?> beanType = handlerMethod.getBeanType();
            Map<String, Integer> methodOrderMap = classMethodOrderCache.computeIfAbsent(beanType,
                    this::buildMethodOrderMap);
            String methodKey = handlerMethod.getMethod().getName();
            Integer order = methodOrderMap.get(methodKey);
            if (order != null) {
                operation.addExtension(ExtensionsConstants.EXTENSION_ORDER, order);
            }
            return operation;
        }

        /**
         * 通过ASM读取.class文件，按字节码中方法的声明顺序构建映射。
         * .class文件中的方法顺序与源码声明顺序一致（javac保证），
         * 比getDeclaredMethods()更可靠（后者在JDK 7+不保证返回源码顺序）。
         */
        private Map<String, Integer> buildMethodOrderMap(Class<?> clazz) {
            Map<String, Integer> map = new LinkedHashMap<>();
            try {
                String resourcePath = clazz.getName().replace('.', '/') + ".class";
                try (InputStream is = clazz.getClassLoader().getResourceAsStream(resourcePath)) {
                    if (is != null) {
                        org.springframework.asm.ClassReader reader = new org.springframework.asm.ClassReader(is);
                        int[] counter = {1};
                        reader.accept(new org.springframework.asm.ClassVisitor(org.springframework.asm.Opcodes.ASM9) {
                            @Override
                            public org.springframework.asm.MethodVisitor visitMethod(int access, String name,
                                    String descriptor, String signature, String[] exceptions) {
                                boolean isPublic = (access & org.springframework.asm.Opcodes.ACC_PUBLIC) != 0;
                                boolean isConstructor = "<init>".equals(name) || "<clinit>".equals(name);
                                if (isPublic && !isConstructor) {
                                    map.putIfAbsent(name, counter[0]++);
                                }
                                return null;
                            }
                        }, org.springframework.asm.ClassReader.SKIP_CODE
                                | org.springframework.asm.ClassReader.SKIP_DEBUG
                                | org.springframework.asm.ClassReader.SKIP_FRAMES);
                    }
                }
            } catch (Exception e) {
                // ASM读取失败时降级到getDeclaredMethods
                Method[] methods = clazz.getDeclaredMethods();
                int order = 1;
                for (Method method : methods) {
                    if (java.lang.reflect.Modifier.isPublic(method.getModifiers())) {
                        map.putIfAbsent(method.getName(), order++);
                    }
                }
            }
            return map;
        }
    }
}
