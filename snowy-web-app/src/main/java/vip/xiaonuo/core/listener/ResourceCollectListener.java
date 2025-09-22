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
package vip.xiaonuo.core.listener;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.log.Log;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import vip.xiaonuo.common.cache.CommonCacheOperator;
import vip.xiaonuo.common.consts.CacheConstant;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 资源搜集器，将项目中所有接口（带@RequestMapping的）都搜集起来
 * <p>
 * 搜集到的接口会被缓存
 *
 * @author dongxiayu
 * @date 2023/1/29 23:24
 **/
@Component
@Order(1)
public class ResourceCollectListener implements CommandLineRunner {

    private static final Log log = Log.get();

    @Resource
    private CommonCacheOperator commonCacheOperator;

    @Override
    public void run(String... args) {
        //1.获取所有后端接口
        List<String> permissionResult = CollectionUtil.newArrayList();
        Map<String, String> permissionMethodMap = MapUtil.newHashMap();
        SpringUtil.getApplicationContext().getBeansOfType(RequestMappingHandlerMapping.class).values()
                .forEach(requestMappingHandlerMapping -> requestMappingHandlerMapping.getHandlerMethods()
                        .forEach((key, value) -> {
                            SaCheckPermission saCheckPermission = value.getMethod().getAnnotation(SaCheckPermission.class);
                            if (ObjectUtil.isNotEmpty(saCheckPermission)) {
                                String path = null;
                                // Spring Boot 3.x
                                PathPatternsRequestCondition pathPatternsCondition = key.getPathPatternsCondition();
                                if (pathPatternsCondition != null) {
                                    path = pathPatternsCondition.getPatterns().iterator().next().getPatternString();
                                }
                                // Spring Boot 2.x
                                PatternsRequestCondition patternsCondition = key.getPatternsCondition();
                                if (patternsCondition != null) {
                                    path = patternsCondition.getPatterns().iterator().next();
                                }
                                if (path != null) {
                                    String apiName = "未定义接口名称";
                                    Operation apiOperation = value.getMethod().getAnnotation(Operation.class);
                                    if (ObjectUtil.isNotEmpty(apiOperation)) {
                                        String annotationValue = apiOperation.summary();
                                        if (ObjectUtil.isNotEmpty(annotationValue)) {
                                            apiName = annotationValue;
                                        }
                                    }

                                    String permissionKey = path + StrUtil.BRACKET_START + apiName + StrUtil.BRACKET_END;
                                    permissionResult.add(permissionKey);

                                    // build permission method map data
                                    buildPermissionMethodMapData(value, permissionMethodMap, permissionKey);
                                }
                            }
                        }));

        //2.汇总添加到缓存
        Object permissionResourceObject = commonCacheOperator.get(CacheConstant.PERMISSION_RESOURCE_CACHE_KEY);
        List<String> permissionResource;
        if (Objects.isNull(permissionResourceObject)) {
            permissionResource = CollUtil.newArrayList();
        } else {
            permissionResource = Convert.toList(String.class, permissionResourceObject);
        }
        if (CollUtil.isNotEmpty(permissionResult)) {
            for (String permission : permissionResult) {
                if (!permissionResource.contains(permission)) {
                    permissionResource.add(permission);
                }
            }

            // 刷新缓存
            commonCacheOperator.put(CacheConstant.PERMISSION_RESOURCE_CACHE_KEY, permissionResource);
        }

        // 3.汇总添加Permission Method Map数据到缓存
        refreshPermissionMethodMapDataToCache(permissionMethodMap);

        log.info(">>> 缓存资源URL集合完成!资源数量：{}", permissionResult.size());
    }

    /**
     * refreshPermissionMethodMapDataToCache
     *
     * @param permissionMethodMap map of key {@link String},value {@link String}
     */
    private void refreshPermissionMethodMapDataToCache(Map<String, String> permissionMethodMap) {
        Object permissionMethodMapObject = commonCacheOperator.get(CacheConstant.PERMISSION_RESOURCE_METHOD_CACHE_KEY);
        Map<String, String> permissionMethodMapFromCache = null;
        if (Objects.isNull(permissionMethodMapObject)) {
            permissionMethodMapFromCache = MapUtil.newHashMap();
        } else {
            permissionMethodMapFromCache = Convert.toMap(String.class, String.class, permissionMethodMapObject);
        }
        if (CollUtil.isNotEmpty(permissionMethodMap)) {
            for (Map.Entry<String, String> permissionMethodEntry : permissionMethodMap.entrySet()) {
                String permissionKey = permissionMethodEntry.getKey();
                String permissionMethod = permissionMethodEntry.getValue();
                permissionMethodMapFromCache.put(permissionKey, permissionMethod);
            }

            // 刷新缓存
            commonCacheOperator.put(CacheConstant.PERMISSION_RESOURCE_METHOD_CACHE_KEY, permissionMethodMapFromCache);
        }
    }

    /**
     * buildPermissionMethodMapData
     *
     * @param value               {@link HandlerMethod}
     * @param permissionMethodMap map of key {@link String},value {@link String}
     * @param permissionKey       {@link String}
     */
    private static void buildPermissionMethodMapData(HandlerMethod value, Map<String, String> permissionMethodMap, String permissionKey) {
        GetMapping getMappingAnno = value.getMethod().getAnnotation(GetMapping.class);
        PostMapping postMappingAnno = value.getMethod().getAnnotation(PostMapping.class);

        String permissionMethod = null;
        if (Objects.nonNull(getMappingAnno)) {
            permissionMethod = HttpMethod.GET.name();
        }
        if (Objects.nonNull(postMappingAnno)) {
            permissionMethod = HttpMethod.POST.name();
        }
        permissionMethodMap.put(permissionKey, permissionMethod);
    }
}
