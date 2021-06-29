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
package vip.xiaonuo.sys.core.filter.security.entrypoint;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.log.Log;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import vip.xiaonuo.core.exception.ServiceException;
import vip.xiaonuo.core.exception.enums.AuthExceptionEnum;
import vip.xiaonuo.core.exception.enums.PermissionExceptionEnum;
import vip.xiaonuo.core.exception.enums.ServerExceptionEnum;
import vip.xiaonuo.core.util.ResponseUtil;
import vip.xiaonuo.sys.core.cache.ResourceCache;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;

/**
 * 未认证用户访问须授权资源端点
 *
 * @author xuyuxiang
 * @date 2020/3/18 11:52
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    private static final Log log = Log.get();

    @Resource
    private ResourceCache resourceCache;

    /**
     * 访问未经授权的接口时执行此方法，未经授权的接口包含系统中存在和不存在的接口，分别处理
     *
     * @author xuyuxiang
     * @date 2020/3/18 19:15
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {

        String requestUri = request.getRequestURI();

        //1.检查redis中RESOURCE缓存是否为空，如果为空，直接抛出系统异常，缓存url作用详见ResourceCollectListener
        Collection<String> urlCollections = resourceCache.getAllResources();
        if (ObjectUtil.isEmpty(urlCollections)) {
            log.error(">>> 获取缓存的Resource Url为空，请检查缓存中是否被误删，requestUri={}", requestUri);
            ResponseUtil.responseExceptionError(response,
                    ServerExceptionEnum.SERVER_ERROR.getCode(),
                    ServerExceptionEnum.SERVER_ERROR.getMessage(),
                    new ServiceException(ServerExceptionEnum.SERVER_ERROR).getStackTrace()[0].toString());
            return;
        }

        //2.判断缓存的url中是否有当前请求的uri，没有的话响应给前端404
        if (!urlCollections.contains(requestUri)) {
            log.error(">>> 当前请求的uri不存在，请检查请求地址是否正确或缓存中是否被误删，requestUri={}", requestUri);
            ResponseUtil.responseExceptionError(response,
                    PermissionExceptionEnum.URL_NOT_EXIST.getCode(),
                    PermissionExceptionEnum.URL_NOT_EXIST.getMessage(),
                    new ServiceException(PermissionExceptionEnum.URL_NOT_EXIST).getStackTrace()[0].toString());
            return;
        }

        //3.响应给前端无权限访问本接口（没有携带token）
        log.error(">>> 没有权限访问该资源，requestUri={}", requestUri);
        ResponseUtil.responseExceptionError(response,
                AuthExceptionEnum.REQUEST_TOKEN_EMPTY.getCode(),
                AuthExceptionEnum.REQUEST_TOKEN_EMPTY.getMessage(),
                new ServiceException(AuthExceptionEnum.REQUEST_TOKEN_EMPTY).getStackTrace()[0].toString());
    }
}
