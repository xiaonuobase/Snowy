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
package vip.xiaonuo.sys.modular.oauth.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthGiteeRequest;
import me.zhyd.oauth.request.AuthGithubRequest;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.core.context.constant.ConstantContextHolder;
import vip.xiaonuo.core.exception.ServiceException;
import vip.xiaonuo.core.pojo.oauth.OauthConfigs;
import vip.xiaonuo.sys.core.cache.OauthCache;
import vip.xiaonuo.sys.core.enums.OauthPlatformEnum;
import vip.xiaonuo.sys.modular.auth.service.AuthService;
import vip.xiaonuo.sys.modular.oauth.entity.SysOauthUser;
import vip.xiaonuo.sys.modular.oauth.enums.SysOauthExceptionEnum;
import vip.xiaonuo.sys.modular.oauth.mapper.SysOauthMapper;
import vip.xiaonuo.sys.modular.oauth.service.SysOauthService;
import vip.xiaonuo.sys.modular.user.entity.SysUser;
import vip.xiaonuo.sys.modular.user.service.SysUserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


/**
 * Oauth登录相关service接口实现类
 *
 * @author xuyuxiang
 * @date 2020/7/28 17:07
 **/
@Service
public class SysOauthServiceImpl extends ServiceImpl<SysOauthMapper, SysOauthUser> implements SysOauthService {

    @Resource
    private OauthCache oauthCache;

    @Resource
    private AuthService authService;

    @Resource
    private SysUserService sysUserService;


    @Override
    public String getAuthorizeUrl(String source) {
        Boolean enableOauthLogin = ConstantContextHolder.getEnableOauthLogin();
        if (!enableOauthLogin) {
            throw new ServiceException(SysOauthExceptionEnum.OAUTH_DISABLED);
        }
        AuthRequest authRequest = this.getAuthRequest(source);
        return authRequest.authorize(AuthStateUtils.createState());
    }

    @SuppressWarnings("all")
    @Override
    public String callback(String source, AuthCallback callback, HttpServletRequest request) {
        AuthRequest authRequest = this.getAuthRequest(source);
        AuthResponse<AuthUser> response = authRequest.login(callback);
        if (response.ok()) {
            AuthUser authUser = response.getData();
            return doLogin(authUser);
        } else {
            throw new ServiceException(response.getCode(), response.getMsg());
        }
    }

    /**
     * 根据用户授权信息进行登录
     *
     * @param authUser 用户授权信息
     * @return token
     * @author xuyuxiang
     * @date 2020/7/29 9:54
     **/
    @Transactional(rollbackFor = Exception.class)
    public String doLogin(AuthUser authUser) {
        //获取uuid
        String uuid = authUser.getUuid();
        LambdaQueryWrapper<SysOauthUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysOauthUser::getUuid, uuid);
        SysOauthUser oauthUser = this.getOne(queryWrapper);
        //从没授权登录过
        if (ObjectUtil.isNull(oauthUser)) {
            //将授权的用户信息保存到sys_oauth_user表和sys_user表
            this.saveByAuthUser(authUser);
            //再获取oauthUser用户
            oauthUser = this.getOne(queryWrapper);
        }
        //获取用户账户信息进行登录
        Long userId = oauthUser.getId();
        SysUser sysUser = sysUserService.getUserById(userId);
        return authService.doLogin(sysUser);
    }

    /**
     * 将授权的用户信息保存到sys_oauth_user表和sys_user表
     *
     * @param authUser 用户授权信息
     * @return void
     * @author xuyuxiang
     * @date 2020/7/29 10:16
     **/
    @Transactional(rollbackFor = Exception.class)
    public void saveByAuthUser(AuthUser authUser) {
        //生成用户id
        long userId = IdWorker.getId();
        //创建oauthUser对象
        SysOauthUser oauthUser = new SysOauthUser();
        oauthUser.setId(userId);
        this.fillOauthUserInfo(oauthUser, authUser);
        //创建sysUser对象
        SysUser sysUser = new SysUser();
        sysUser.setId(userId);
        //将授权的用户信息保存到user表
        sysUserService.saveAuthUserToUser(authUser, sysUser);
        this.save(oauthUser);
    }

    /**
     * 根据具体的授权来源，获取授权请求
     *
     * @param source 授权平台来源
     * @return 授权请求
     * @author xuyuxiang
     * @date 2020/7/28 17:28
     **/
    private AuthRequest getAuthRequest(String source) {
        AuthRequest authRequest;
        if (source.toLowerCase().equals(OauthPlatformEnum.GITEE.getCode())) {
            OauthConfigs giteeOauthConfigs = ConstantContextHolder.getGiteeOauthConfigs();
            authRequest = new AuthGiteeRequest(AuthConfig.builder()
                    .clientId(giteeOauthConfigs.getClientId())
                    .clientSecret(giteeOauthConfigs.getClientSecret())
                    .redirectUri(giteeOauthConfigs.getRedirectUri())
                    .build(), oauthCache);
        } else if (source.toLowerCase().equals(OauthPlatformEnum.GITHUB.getCode())) {
            OauthConfigs githubOauthConfigs = ConstantContextHolder.getGithubOauthConfigs();
            authRequest = new AuthGithubRequest(AuthConfig.builder()
                    .clientId(githubOauthConfigs.getClientId())
                    .clientSecret(githubOauthConfigs.getClientSecret())
                    .redirectUri(githubOauthConfigs.getRedirectUri())
                    .build(), oauthCache);
        } else {
            throw new ServiceException(SysOauthExceptionEnum.OAUTH_NOT_SUPPORT);
        }
        return authRequest;
    }

    /**
     * 将授权用户信息填充到oauthUser
     *
     * @param oauthUser 系统授权用户信息
     * @param authUser  平台授权用户信息
     * @return void
     * @author xuyuxiang
     * @date 2020/7/29 10:42
     **/
    private void fillOauthUserInfo(SysOauthUser oauthUser, AuthUser authUser) {
        oauthUser.setUuid(authUser.getUuid());
        oauthUser.setAccessToken(authUser.getToken().getAccessToken());
        oauthUser.setNickName(authUser.getNickname());
        oauthUser.setAvatar(authUser.getAvatar());
        oauthUser.setBlog(authUser.getBlog());
        oauthUser.setCompany(authUser.getCompany());
        oauthUser.setLocation(authUser.getLocation());
        oauthUser.setEmail(authUser.getEmail());
        oauthUser.setSource(authUser.getSource());
        oauthUser.setRemark(authUser.getRemark());
        if (ObjectUtil.isNotNull(authUser.getGender())) {
            oauthUser.setGender(authUser.getGender().getDesc());
        }
    }
}
