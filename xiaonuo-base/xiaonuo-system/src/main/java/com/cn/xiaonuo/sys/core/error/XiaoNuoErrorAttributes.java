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

XiaoNuo采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：

1.请不要删除和修改根目录下的LICENSE文件。
2.请不要删除和修改XiaoNuo源码头部的版权声明。
3.请保留源码和相关描述文件的项目出处，作者声明等。
4.分发源码时候，请注明软件出处 https://gitee.com/xiaonuobase/xiaonuo-vue
5.在修改包名，模块名称，项目代码等时，请注明软件出处 https://gitee.com/xiaonuobase/xiaonuo-vue
6.若您的项目无法满足以上几点，可申请商业授权，获取XiaoNuo商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package com.cn.xiaonuo.sys.core.error;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpStatus;
import com.cn.xiaonuo.core.exception.ServiceException;
import com.cn.xiaonuo.core.exception.enums.PermissionExceptionEnum;
import com.cn.xiaonuo.core.exception.enums.ServerExceptionEnum;
import com.cn.xiaonuo.core.pojo.response.ErrorResponseData;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

/**
 * 将系统管理未知错误异常，输出格式重写为我们熟悉的响应格式
 *
 * @author yubaoshan
 * @date 2020/4/14 22:21
 */
public class XiaoNuoErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions attributeOptions) {

        // 1.先获取spring默认的返回内容
        Map<String, Object> defaultErrorAttributes = super.getErrorAttributes(webRequest, attributeOptions);

        // 2.如果返回的异常是ServiceException，则按ServiceException响应的内容进行返回
        Throwable throwable = this.getError(webRequest);
        if (throwable instanceof ServiceException) {
            ServiceException serviceException = (ServiceException) throwable;
            return BeanUtil.beanToMap(new ErrorResponseData(serviceException.getCode(), serviceException.getErrorMessage()));
        }

        // 3.如果返回的是404 http状态码
        Integer status = (Integer) defaultErrorAttributes.get("status");
        if (status.equals(HttpStatus.HTTP_NOT_FOUND)) {
            return BeanUtil.beanToMap(new ErrorResponseData(PermissionExceptionEnum.URL_NOT_EXIST.getCode(), PermissionExceptionEnum.URL_NOT_EXIST.getMessage()));
        }

        // 4.无法确定的返回服务器异常
        return BeanUtil.beanToMap(new ErrorResponseData(ServerExceptionEnum.SERVER_ERROR.getCode(), ServerExceptionEnum.SERVER_ERROR.getMessage()));
    }

}
