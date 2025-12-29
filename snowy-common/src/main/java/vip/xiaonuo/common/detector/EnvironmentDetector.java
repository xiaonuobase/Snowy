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
package vip.xiaonuo.common.detector;

import cn.hutool.core.util.ObjectUtil;
import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import vip.xiaonuo.common.prop.CommonProperties;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 环境检测器
 *
 * @author jiangcs
 * @since 2025/9/13 23:20
 */
@Configuration
public class EnvironmentDetector implements ApplicationListener<ApplicationReadyEvent> {

    // 是否是SnowyCloud
    private static boolean snowyCloud = false;

    @Resource
    private Environment environment;
    @Resource
    private CommonProperties commonProperties;

    @Override
    public void onApplicationEvent(@Nullable ApplicationReadyEvent event) {
        // 检查是否存在 Nacos 相关配置
        boolean hasNacosConfig = environment.containsProperty("spring.cloud.nacos.config.server-addr");
        boolean hasNacosDiscovery = environment.containsProperty("spring.cloud.nacos.discovery.server-addr");
        snowyCloud = hasNacosConfig || hasNacosDiscovery;
    }

    /**
     * 转换后端路径
     * <p>Cloud版本会匹配网关路由，返回拼接路径；单体版本直接返回原路径</p>
     *
     * @param path 路径
     * @return 路径
     */
    public String convertBackendPath(String path) {
        if (snowyCloud) {
            List<Map<String, String>> list = commonProperties.getBackendPaths();
            Set<Map<String, String>> urlSet = list.stream().filter(m -> path.startsWith(m.get("name")))
                    .collect(Collectors.toSet());
            if (ObjectUtil.isNotEmpty(urlSet)) {
                return urlSet.iterator().next().get("value") + path;
            }
        }
        return path;
    }

}
