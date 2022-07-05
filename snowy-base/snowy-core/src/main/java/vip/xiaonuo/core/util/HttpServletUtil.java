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
package vip.xiaonuo.core.util;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import vip.xiaonuo.core.consts.CommonConstant;
import vip.xiaonuo.core.context.constant.ConstantContextHolder;
import vip.xiaonuo.core.exception.ServiceException;
import vip.xiaonuo.core.exception.enums.ServerExceptionEnum;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;

/**
 * HttpServlet工具类，获取当前request和response
 *
 * @author xuyuxiang
 * @date 2020/3/30 15:09
 */
public class HttpServletUtil {
    /** 编码 **/
    private static final String ENCODED = "UTF-8";

    /** 引用 **/
    private static final String REFERER = "referer";

    /**
     * 获取当前请求的request对象
     *
     * @author xuyuxiang
     * @date 2020/3/30 15:10
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            throw new ServiceException(ServerExceptionEnum.REQUEST_EMPTY);
        } else {
            return requestAttributes.getRequest();
        }
    }

    /**
     * 获取当前请求的response对象
     *
     * @author xuyuxiang
     * @date 2020/3/30 15:10
     */
    public static HttpServletResponse getResponse() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            throw new ServiceException(ServerExceptionEnum.REQUEST_EMPTY);
        } else {
            return requestAttributes.getResponse();
        }
    }

    /**
     * @description 从header.referer获取token信息
     * @author dongxiayu
     * @date 2022/7/2 0:46
     * @return
     **/
    public static String getTokenFromReferer(){
        String refererContent = HttpServletUtil.getRequest().getHeader(REFERER);
        Map<String,String> paramMap = HttpUtil.decodeParamMap(refererContent, CharsetUtil.charset(ENCODED));
        String token = Objects.nonNull(paramMap)?paramMap.get(CommonConstant.TOKEN_NAME):null;
        if(StrUtil.isNotBlank(token)){
            // 判断是否开启了加密
            if (ConstantContextHolder.getCryptogramConfigs().getTokenEncDec()) {
                // 解密token
                token = CryptogramUtil.doDecrypt(token);
            }
        }
        return token;
    }
}
