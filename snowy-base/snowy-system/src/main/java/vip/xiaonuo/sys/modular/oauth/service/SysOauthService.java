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
package vip.xiaonuo.sys.modular.oauth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.zhyd.oauth.model.AuthCallback;
import vip.xiaonuo.sys.modular.oauth.entity.SysOauthUser;

import javax.servlet.http.HttpServletRequest;

/**
 * Oauth登录相关service接口
 *
 * @author xuyuxiang
 * @date 2020/7/28 17:06
 **/
public interface SysOauthService extends IService<SysOauthUser> {

    /**
     * 根据授权平台来源获取授权地址
     *
     * @param source 授权平台来源
     * @return 授权地址
     * @author xuyuxiang
     * @date 2020/7/28 17:26
     **/
    String getAuthorizeUrl(String source);

    /**
     * 授权后回调方法
     *
     * @param source   授权来源平台
     * @param callback 授权平台返回的用户信息
     * @param request  request请求
     * @return 登录成功的token
     * @author xuyuxiang
     * @date 2020/7/29 9:48
     **/
    String callback(String source, AuthCallback callback, HttpServletRequest request);
}
