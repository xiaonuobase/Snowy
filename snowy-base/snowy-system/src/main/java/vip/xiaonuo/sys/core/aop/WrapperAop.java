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
package vip.xiaonuo.sys.core.aop;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import vip.xiaonuo.core.annotion.Wrapper;
import vip.xiaonuo.core.consts.AopSortConstant;
import vip.xiaonuo.core.exception.ServiceException;
import vip.xiaonuo.core.exception.enums.WrapperExceptionEnum;
import vip.xiaonuo.core.pojo.base.wrapper.BaseWrapper;
import vip.xiaonuo.core.pojo.page.PageResult;
import vip.xiaonuo.core.pojo.response.ResponseData;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * controller结果包装的aop
 *
 * @author xuyuxiang
 * @date 2020/7/24 17:42
 */
@Aspect
@Order(AopSortConstant.WRAPPER_AOP)
public class WrapperAop {

    private static final Log log = Log.get();

    /**
     * 切入点
     *
     * @author xuyuxiang
     * @date 2020/7/24 17:42
     */
    @Pointcut("@annotation(vip.xiaonuo.core.annotion.Wrapper)")
    private void wrapperPointcut() {
    }

    /**
     * 执行具体的包装过程
     *
     * @author xuyuxiang
     * @date 2020/7/24 17:44
     */
    @Around("wrapperPointcut()")
    public Object doWrapper(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        // 直接执行原有业务逻辑
        Object proceedResult = proceedingJoinPoint.proceed();

        return processWrapping(proceedingJoinPoint, proceedResult);
    }

    /**
     * 具体包装过程
     *
     * @author xuyuxiang
     * @date 2020/7/24 17:53
     */
    @SuppressWarnings("all")
    private Object processWrapping(ProceedingJoinPoint proceedingJoinPoint, Object originResult) throws IllegalAccessException, InstantiationException {

        // 获取@Wrapper注解
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Wrapper wrapperAnnotation = method.getAnnotation(Wrapper.class);

        // 获取注解上的处理类
        Class<? extends BaseWrapper<?>>[] baseWrapperClasses = wrapperAnnotation.value();

        // 如果注解上的为空直接返回
        if (ObjectUtil.isEmpty(baseWrapperClasses)) {
            return originResult;
        }

        // 获取原有返回结果，如果不是ResponseData则不进行处理(需要遵守这个约定)
        if (!(originResult instanceof ResponseData)) {
            log.warn(">>> 当前请求的返回结果不是ResponseData类型，直接返回原值！");
            return originResult;
        }

        // 获取ResponseData中的值
        ResponseData responseData = (ResponseData) originResult;
        Object beWrapped = responseData.getData();

        // 如果是基本类型，不进行加工处理
        if (ObjectUtil.isBasicType(beWrapped)) {
            throw new ServiceException(WrapperExceptionEnum.BASIC_TYPE_ERROR);
        }

        // 如果是Page类型
        if (beWrapped instanceof Page) {

            // 获取Page原有对象
            Page page = (Page) beWrapped;

            // 将page中所有records都包装一遍
            ArrayList<Map<String, Object>> maps = new ArrayList<>();
            for (Object wrappedItem : page.getRecords()) {
                maps.add(this.wrapPureObject(wrappedItem, baseWrapperClasses));
            }

            page.setRecords(maps);
            responseData.setData(page);
        }

        // 如果是PageResult类型
        else if (beWrapped instanceof PageResult) {

            // 获取PageResult原有对象
            PageResult pageResult = (PageResult) beWrapped;

            // 将PageResult中所有rows都包装一遍
            ArrayList<Map<String, Object>> maps = new ArrayList<>();
            for (Object wrappedItem : pageResult.getRows()) {
                maps.add(this.wrapPureObject(wrappedItem, baseWrapperClasses));
            }

            pageResult.setRows(maps);
            responseData.setData(pageResult);
        }

        // 如果是List类型
        else if (beWrapped instanceof Collection) {

            // 获取原有的List
            Collection collection = (Collection) beWrapped;

            // 将page中所有records都包装一遍
            ArrayList<Map<String, Object>> maps = new ArrayList<>();
            for (Object wrappedItem : collection) {
                maps.add(this.wrapPureObject(wrappedItem, baseWrapperClasses));
            }

            responseData.setData(maps);
        }

        // 如果是Array类型
        else if (ArrayUtil.isArray(beWrapped)) {

            // 获取原有的Array
            Object[] objects = this.objToArray(beWrapped);

            // 将array中所有records都包装一遍
            ArrayList<Map<String, Object>> maps = new ArrayList<>();
            for (Object wrappedItem : objects) {
                maps.add(this.wrapPureObject(wrappedItem, baseWrapperClasses));
            }

            responseData.setData(maps);
        }

        // 如果是Object类型
        else {
            responseData.setData(this.wrapPureObject(beWrapped, baseWrapperClasses));
        }


        return responseData;
    }

    /**
     * 原始对象包装成一个map的过程
     * <p>
     * 期间多次根据BaseWrapper接口方法执行包装过程
     *
     * @author xuyuxiang
     * @date 2020/7/24 21:40
     */
    @SuppressWarnings("all")
    private Map<String, Object> wrapPureObject(Object originModel, Class<? extends BaseWrapper<?>>[] baseWrapperClasses) {

        // 首先将原始的对象转化为map
        Map<String, Object> originMap = BeanUtil.beanToMap(originModel);

        // 经过多个包装类填充属性
        try {
            for (Class<? extends BaseWrapper<?>> baseWrapperClass : baseWrapperClasses) {
                BaseWrapper baseWrapper = baseWrapperClass.newInstance();
                Map<String, Object> incrementFieldsMap = baseWrapper.doWrap(originModel);
                originMap.putAll(incrementFieldsMap);
            }
        } catch (Exception e) {
            log.error(">>> 原始对象包装过程，字段转化异常：{}", e.getMessage());
            throw new ServiceException(WrapperExceptionEnum.TRANSFER_ERROR);
        }

        return originMap;
    }

    /**
     * Object转为一个array，确保object为数组类型
     *
     * @author xuyuxiang
     * @date 2020/7/24 22:06
     */
    private Object[] objToArray(Object object) {
        int length = Array.getLength(object);
        Object[] result = new Object[length];
        for (int i = 0; i < result.length; i++) {
            result[i] = Array.get(object, i);
        }
        return result;
    }

}
