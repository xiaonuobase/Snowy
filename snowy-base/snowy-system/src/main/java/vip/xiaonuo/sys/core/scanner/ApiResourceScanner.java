/*
Copyright [2020] [https://www.xiaonuo.vip]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：

1.请不要删除和修改根目录下的LICENSE文件。
2.请不要删除和修改Snowy源码头部的版权声明。
3.请保留源码和相关描述文件的项目出处，作者声明等。
4.分发源码时候，请注明软件出处 https://gitee.com/xiaonuobase/snowy
5.在修改包名，模块名称，项目代码等时，请注明软件出处 https://gitee.com/xiaonuobase/snowy
6.若您的项目无法满足以上几点，可申请商业授权，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package vip.xiaonuo.sys.core.scanner;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.core.consts.SymbolConstant;
import vip.xiaonuo.core.context.resources.ApiResourceContext;
import vip.xiaonuo.core.util.AopTargetUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * 资源扫描器
 *
 * @author yubaoshan
 * @date 2018/1/3 14:58
 */
public class ApiResourceScanner implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        //如果controller是代理对象,则需要获取原始类的信息
        Object aopTarget = AopTargetUtil.getTarget(bean);

        if (aopTarget == null) {
            aopTarget = bean;
        }
        Class<?> clazz = aopTarget.getClass();

        //判断是不是控制器,不是控制器就略过
        boolean controllerFlag = getControllerFlag(clazz);
        if (!controllerFlag) {
            return bean;
        }

        //扫描控制器的所有带ApiResource注解的方法
        Set<String> apiUrls = doScan(clazz);

        //将扫描到的注解存储到缓存
        persistApiResources(apiUrls);

        return bean;
    }


    /**
     * 判断一个类是否是控制器
     *
     * @author yubaoshan
     * @date 2020/6/21 18:23
     */
    private boolean getControllerFlag(Class<?> clazz) {
        Annotation[] annotations = clazz.getAnnotations();

        for (Annotation annotation : annotations) {
            if (RestController.class.equals(annotation.annotationType()) || Controller.class.equals(annotation.annotationType())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 扫描整个类中包含的所有控制器
     *
     * @author yubaoshan
     * @date 2020/6/21 18:23
     */
    private Set<String> doScan(Class<?> clazz) {

        // 获取类头的@RequestMapping内容
        String primaryMappingUrl = getRequestMappingUrl(clazz);

        // 获取所有方法的RequestMapping的url
        Set<String> apiResources = CollectionUtil.newHashSet();
        Method[] declaredMethods = clazz.getDeclaredMethods();
        if (declaredMethods.length > 0) {
            for (Method declaredMethod : declaredMethods) {
                String requestMappingUrl = getRequestMappingUrl(declaredMethod);
                apiResources.add(primaryMappingUrl + requestMappingUrl);
            }
        }
        return apiResources;
    }

    /**
     * 存储扫描到的api资源
     *
     * @author yubaoshan
     * @date 2020/6/21 17:43
     */
    private void persistApiResources(Set<String> apiResources) {
        ApiResourceContext.addBatchUrls(apiResources);
    }

    /**
     * 获取@RequestMapping注解的url信息
     *
     * @author yubaoshan
     * @date 2020/6/21 17:43
     */
    private String getRequestMappingUrl(AnnotatedElement annotatedElement) {

        RequestMapping requestMapping = annotatedElement.getAnnotation(RequestMapping.class);
        GetMapping getMapping = annotatedElement.getAnnotation(GetMapping.class);
        PostMapping postMapping = annotatedElement.getAnnotation(PostMapping.class);

        // 分别判断三个注解中有没有value和path的值，优先级是 RequestMapping > GetMapping > PostMapping
        if (requestMapping != null) {
            String[] value = requestMapping.value();
            String[] path = requestMapping.path();
            if (value.length > 0) {
                return getRequestMappingPath(value);
            } else if (path.length > 0) {
                return getRequestMappingPath(path);
            }
        } else if (getMapping != null) {
            String[] value = getMapping.value();
            String[] path = getMapping.path();
            if (value.length > 0) {
                return getRequestMappingPath(value);
            } else if (path.length > 0) {
                return getRequestMappingPath(path);
            }
        } else if (postMapping != null) {
            String[] value = postMapping.value();
            String[] path = postMapping.path();
            if (value.length > 0) {
                return getRequestMappingPath(value);
            } else if (path.length > 0) {
                return getRequestMappingPath(path);
            }
        }

        return "";
    }

    /**
     * 获取数组第一个字符串
     *
     * @author yubaoshan
     * @date 2020/6/21 18:10
     */
    private String getRequestMappingPath(String[] strings) {
        if (strings.length == 0) {
            return "";
        }
        String result = strings[0];

        // 如果RequestMapping的值是“/”直接返回
        if (SymbolConstant.LEFT_DIVIDE.equals(result)) {
            return result;
        }

        // 添加路径首部的"/"
        if (!result.startsWith(SymbolConstant.LEFT_DIVIDE)) {
            result = SymbolConstant.LEFT_DIVIDE + result;
        }

        // 则去掉尾部的"/"
        if (result.endsWith(SymbolConstant.LEFT_DIVIDE)) {
            result = StrUtil.removeSuffix(result, SymbolConstant.LEFT_DIVIDE);
        }

        return result;
    }

}
