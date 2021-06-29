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
package vip.xiaonuo.sys.modular.oauth.controller;

import me.zhyd.oauth.model.AuthCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.core.consts.CommonConstant;
import vip.xiaonuo.core.consts.SymbolConstant;
import vip.xiaonuo.core.context.constant.ConstantContextHolder;
import vip.xiaonuo.sys.modular.oauth.service.SysOauthService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Oauth登录控制器
 *
 * @author xuyuxiang
 * @date 2020/7/28 16:38
 **/
@RestController
public class SysOauthController {

    @Resource
    private SysOauthService sysOauthService;

    /**
     * oauth登录
     *
     * @author xuyuxiang
     * @date 2020/7/29 12:18
     **/
    @GetMapping("/oauth/render/{source}")
    public void renderAuth(@PathVariable("source") String source, HttpServletResponse response) throws IOException {
        String authorizeUrl = sysOauthService.getAuthorizeUrl(source);
        response.sendRedirect(authorizeUrl);
    }

    /**
     * oauth平台中配置的授权回调地址
     *
     * @author xuyuxiang
     * @date 2020/7/29 12:19
     **/
    @GetMapping("/oauth/callback/{source}")
    public void callback(@PathVariable("source") String source, AuthCallback callback, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = sysOauthService.callback(source, callback, request);
        String webUrl = ConstantContextHolder.getWebUrl();
        response.sendRedirect(webUrl + SymbolConstant.QUESTION_MARK + CommonConstant.TOKEN_NAME + SymbolConstant.EQUAL + token);
    }
}
