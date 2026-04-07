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

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.github.xiaoymin.knife4j.core.conf.ExtensionsConstants;
import com.github.xiaoymin.knife4j.core.model.MarkdownProperty;
import com.github.xiaoymin.knife4j.extend.filter.basic.JakartaServletSecurityBasicAuthFilter;
import com.github.xiaoymin.knife4j.spring.configuration.Knife4jHttpBasic;
import com.github.xiaoymin.knife4j.spring.configuration.Knife4jProperties;
import com.github.xiaoymin.knife4j.spring.configuration.Knife4jSetting;
import com.github.xiaoymin.knife4j.spring.extension.Knife4jJakartaOperationCustomizer;
import com.github.xiaoymin.knife4j.spring.extension.OpenApiExtensionResolver;
import com.github.xiaoymin.knife4j.spring.filter.JakartaProductionSecurityFilter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.OpenAPI;
import org.apache.commons.lang3.ArrayUtils;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

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
    public GlobalOpenApiCustomizer knife4jOpenApiCustomizer(Knife4jProperties knife4jProperties,
                                                            SpringDocConfigProperties properties) {
        return new CompatibleKnife4jOpenApiCustomizer(knife4jProperties, properties);
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
        private final SpringDocConfigProperties properties;

        public CompatibleKnife4jOpenApiCustomizer(Knife4jProperties knife4jProperties,
                                                  SpringDocConfigProperties properties) {
            this.knife4jProperties = knife4jProperties;
            this.properties = properties;
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
                addOrderExtension(openApi);
            }
        }

        private void addOrderExtension(OpenAPI openApi) {
            // 关键修复：使用Set替代List，兼容springdoc 2.6.0+
            Set<SpringDocConfigProperties.GroupConfig> groupConfigs = properties.getGroupConfigs();
            if (!CollectionUtils.isEmpty(groupConfigs)) {
                Set<String> packagesToScan = groupConfigs.stream()
                        .map(SpringDocConfigProperties.GroupConfig::getPackagesToScan)
                        .filter(toScan -> !CollectionUtils.isEmpty(toScan))
                        .flatMap(Collection::stream)
                        .collect(Collectors.toSet());

                Map<String, Integer> tagOrderMap = new HashMap<>();
                Set<Class<?>> classes = new HashSet<>();
                for (String packageName : packagesToScan) {
                    classes.addAll(scanPackageByAnnotation(packageName));
                }
                for (Class<?> clazz : classes) {
                    Tag tag = getTag(clazz);
                    if (tag != null) {
                        ApiSupport apiSupport = clazz.getAnnotation(ApiSupport.class);
                        if (apiSupport != null) {
                            tagOrderMap.putIfAbsent(tag.name(), apiSupport.order());
                        }
                    }
                }
                if (openApi.getTags() != null) {
                    openApi.getTags().forEach(tag -> {
                        if (tagOrderMap.containsKey(tag.getName())) {
                            tag.addExtension(ExtensionsConstants.EXTENSION_ORDER, tagOrderMap.get(tag.getName()));
                        }
                    });
                }
            }
        }

        private Tag getTag(Class<?> clazz) {
            Tag anno = clazz.getAnnotation(Tag.class);
            if (anno != null) {
                return anno;
            }
            Class<?>[] interfaces = clazz.getInterfaces();
            if (ArrayUtils.isNotEmpty(interfaces)) {
                for (Class<?> interfaceClazz : interfaces) {
                    anno = interfaceClazz.getAnnotation(Tag.class);
                    if (anno != null) {
                        return anno;
                    }
                }
            }
            return null;
        }

        private Set<Class<?>> scanPackageByAnnotation(String packageName) {
            ClassPathScanningCandidateComponentProvider scanner =
                    new ClassPathScanningCandidateComponentProvider(false);
            scanner.addIncludeFilter(new AnnotationTypeFilter(RestController.class));
            Set<Class<?>> classes = new HashSet<>();
            for (BeanDefinition beanDefinition : scanner.findCandidateComponents(packageName)) {
                try {
                    classes.add(Class.forName(beanDefinition.getBeanClassName()));
                } catch (ClassNotFoundException ignored) {
                }
            }
            return classes;
        }
    }
}
