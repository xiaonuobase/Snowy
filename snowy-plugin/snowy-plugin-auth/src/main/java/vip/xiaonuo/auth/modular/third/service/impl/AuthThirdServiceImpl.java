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
package vip.xiaonuo.auth.modular.third.service.impl;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.oauth2.consts.SaOAuth2Consts;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xkcoding.http.HttpUtil;
import com.xkcoding.http.support.hutool.HutoolImpl;
import jakarta.annotation.Resource;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.auth.api.SaBaseLoginUserApi;
import vip.xiaonuo.auth.core.enums.AuthPlatformEnum;
import vip.xiaonuo.auth.core.enums.AuthPropertyEnum;
import vip.xiaonuo.auth.core.enums.SaClientTypeEnum;
import vip.xiaonuo.auth.core.protocol.AuthClientFactory;
import vip.xiaonuo.auth.core.protocol.base.AuthBaseClient;
import vip.xiaonuo.auth.modular.login.enums.AuthDeviceTypeEnum;
import vip.xiaonuo.auth.modular.login.enums.AuthStrategyWhenNoUserWithPhoneOrEmailEnum;
import vip.xiaonuo.auth.modular.login.param.AuthAccountPasswordLoginParam;
import vip.xiaonuo.auth.modular.login.service.AuthService;
import vip.xiaonuo.auth.modular.third.entity.AuthThirdUser;
import vip.xiaonuo.auth.modular.third.mapper.AuthThirdMapper;
import vip.xiaonuo.auth.modular.third.param.AuthThirdBindAccountParam;
import vip.xiaonuo.auth.modular.third.param.AuthThirdCallbackParam;
import vip.xiaonuo.auth.modular.third.param.AuthThirdRenderParam;
import vip.xiaonuo.auth.modular.third.param.AuthThirdUserPageParam;
import vip.xiaonuo.auth.modular.third.result.AuthThirdRenderResult;
import vip.xiaonuo.auth.modular.third.service.AuthThirdService;
import vip.xiaonuo.common.cache.CommonCacheOperator;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;
import vip.xiaonuo.dev.api.DevConfigApi;

/**
 * 第三方登录Service接口实现类
 *
 * @author xuyuxiang
 * @date 2022/7/8 16:20
 **/
@Service
public class AuthThirdServiceImpl extends ServiceImpl<AuthThirdMapper, AuthThirdUser> implements AuthThirdService {

    /** 缓存前缀 */
    private static final String CONFIG_CACHE_KEY = "auth-third-state:";

    // OAUTH
    private static final String SNOWY_THIRD_OAUTH_ALLOW_LOGIN_FLAG_KEY = "SNOWY_THIRD_OAUTH_ALLOW_LOGIN_FLAG";
    private static final String SNOWY_THIRD_OAUTH_AUTHORIZE_URL_KEY = "SNOWY_THIRD_OAUTH_AUTHORIZE_URL";
    private static final String SNOWY_THIRD_OAUTH_ACCESS_TOKEN_URL_KEY = "SNOWY_THIRD_OAUTH_ACCESS_TOKEN_URL";
    private static final String SNOWY_THIRD_OAUTH_USER_INFO_URL_KEY = "SNOWY_THIRD_OAUTH_USER_INFO_URL";
    private static final String SNOWY_THIRD_OAUTH_SCOPE_KEY = "SNOWY_THIRD_OAUTH_SCOPE";
    private static final String SNOWY_THIRD_OAUTH_CLIENT_ID_KEY = "SNOWY_THIRD_OAUTH_CLIENT_ID";
    private static final String SNOWY_THIRD_OAUTH_CLIENT_SECRET_KEY = "SNOWY_THIRD_OAUTH_CLIENT_SECRET";
    private static final String SNOWY_THIRD_OAUTH_REDIRECT_URL_KEY = "SNOWY_THIRD_OAUTH_REDIRECT_URL";
    private static final String SNOWY_THIRD_OAUTH_SOURCE_PROPERTY_KEY = "SNOWY_THIRD_OAUTH_SOURCE_PROPERTY";
    private static final String SNOWY_THIRD_OAUTH_TARGET_PROPERTY_KEY = "SNOWY_THIRD_OAUTH_TARGET_PROPERTY";

    // OIDC
    private static final String SNOWY_THIRD_OIDC_ALLOW_LOGIN_FLAG_KEY = "SNOWY_THIRD_OIDC_ALLOW_LOGIN_FLAG";
    private static final String SNOWY_THIRD_OIDC_AUTHORIZE_URL_KEY = "SNOWY_THIRD_OIDC_AUTHORIZE_URL";
    private static final String SNOWY_THIRD_OIDC_ACCESS_TOKEN_URL_KEY = "SNOWY_THIRD_OIDC_ACCESS_TOKEN_URL";
    private static final String SNOWY_THIRD_OIDC_USER_INFO_URL_KEY = "SNOWY_THIRD_OIDC_USER_INFO_URL";
    private static final String SNOWY_THIRD_OIDC_SCOPE_KEY = "SNOWY_THIRD_OIDC_SCOPE";
    private static final String SNOWY_THIRD_OIDC_PUBLIC_KEY_KEY = "SNOWY_THIRD_OIDC_PUBLIC_KEY";
    private static final String SNOWY_THIRD_OIDC_ALGORITHM_KEY = "SNOWY_THIRD_OIDC_ALGORITHM";
    private static final String SNOWY_THIRD_OIDC_CLIENT_ID_KEY = "SNOWY_THIRD_OIDC_CLIENT_ID";
    private static final String SNOWY_THIRD_OIDC_CLIENT_SECRET_KEY = "SNOWY_THIRD_OIDC_CLIENT_SECRET";
    private static final String SNOWY_THIRD_OIDC_REDIRECT_URL_KEY = "SNOWY_THIRD_OIDC_REDIRECT_URL";
    private static final String SNOWY_THIRD_OIDC_SOURCE_PROPERTY_KEY = "SNOWY_THIRD_OIDC_SOURCE_PROPERTY";
    private static final String SNOWY_THIRD_OIDC_TARGET_PROPERTY_KEY = "SNOWY_THIRD_OIDC_TARGET_PROPERTY";

    // JWT
    private static final String SNOWY_THIRD_JWT_ALLOW_LOGIN_FLAG_KEY = "SNOWY_THIRD_JWT_ALLOW_LOGIN_FLAG";
    private static final String SNOWY_THIRD_JWT_AUTHORIZE_URL_KEY = "SNOWY_THIRD_JWT_AUTHORIZE_URL";
    private static final String SNOWY_THIRD_JWT_PUBLIC_KEY_KEY = "SNOWY_THIRD_JWT_PUBLIC_KEY";
    private static final String SNOWY_THIRD_JWT_ALGORITHM_KEY = "SNOWY_THIRD_JWT_ALGORITHM";
    private static final String SNOWY_THIRD_JWT_CLIENT_ID_KEY = "SNOWY_THIRD_JWT_CLIENT_ID";
    private static final String SNOWY_THIRD_JWT_CLIENT_SECRET_KEY = "SNOWY_THIRD_JWT_CLIENT_SECRET";
    private static final String SNOWY_THIRD_JWT_REDIRECT_URL_KEY = "SNOWY_THIRD_JWT_REDIRECT_URL";
    private static final String SNOWY_THIRD_JWT_SOURCE_PROPERTY_KEY = "SNOWY_THIRD_JWT_SOURCE_PROPERTY";
    private static final String SNOWY_THIRD_JWT_TARGET_PROPERTY_KEY = "SNOWY_THIRD_JWT_TARGET_PROPERTY";

    // CAS
    private static final String SNOWY_THIRD_CAS_ALLOW_LOGIN_FLAG_KEY = "SNOWY_THIRD_CAS_ALLOW_LOGIN_FLAG";
    private static final String SNOWY_THIRD_CAS_SERVER_LOGIN_URL_KEY = "SNOWY_THIRD_CAS_SERVER_LOGIN_URL";
    private static final String SNOWY_THIRD_CAS_SERVER_VALIDATE_URL_KEY = "SNOWY_THIRD_CAS_SERVER_VALIDATE_URL";
    private static final String SNOWY_THIRD_CAS_SERVER_PROTOCOL_VERSION_KEY = "SNOWY_THIRD_CAS_SERVER_PROTOCOL_VERSION";
    private static final String SNOWY_THIRD_CAS_SERVICE_URL_KEY = "SNOWY_THIRD_CAS_SERVICE_URL";
    private static final String SNOWY_THIRD_CAS_SOURCE_PROPERTY_KEY = "SNOWY_THIRD_CAS_SOURCE_PROPERTY";
    private static final String SNOWY_THIRD_CAS_TARGET_PROPERTY_KEY = "SNOWY_THIRD_CAS_TARGET_PROPERTY";

    // SAML
    private static final String SNOWY_THIRD_SAML_ALLOW_LOGIN_FLAG_KEY = "SNOWY_THIRD_SAML_ALLOW_LOGIN_FLAG";
    private static final String SNOWY_THIRD_SAML_SP_META_DATA_KEY = "SNOWY_THIRD_SAML_SP_META_DATA";
    private static final String SNOWY_THIRD_SAML_CERTIFICATE_KEY = "SNOWY_THIRD_SAML_CERTIFICATE";
    private static final String SNOWY_THIRD_SAML_PRIVATE_KEY_KEY = "SNOWY_THIRD_SAML_PRIVATE_KEY";
    private static final String SNOWY_THIRD_SAML_PUBLIC_KEY_KEY = "SNOWY_THIRD_SAML_PUBLIC_KEY";
    private static final String SNOWY_THIRD_SAML_SP_ENTITY_ID_KEY = "SNOWY_THIRD_SAML_SP_ENTITY_ID";
    private static final String SNOWY_THIRD_SAML_SP_ACL_URL_KEY = "SNOWY_THIRD_SAML_SP_ACL_URL";
    private static final String SNOWY_THIRD_SAML_IDP_META_DATA_KEY = "SNOWY_THIRD_SAML_IDP_META_DATA";
    private static final String SNOWY_THIRD_SAML_BINDING_TYPE_KEY = "SNOWY_THIRD_SAML_BINDING_TYPE";
    private static final String SNOWY_THIRD_SAML_SOURCE_PROPERTY_KEY = "SNOWY_THIRD_SAML_SOURCE_PROPERTY";
    private static final String SNOWY_THIRD_SAML_TARGET_PROPERTY_KEY = "SNOWY_THIRD_SAML_TARGET_PROPERTY";

    // IAM
    private static final String SNOWY_THIRD_IAM_ALLOW_LOGIN_FLAG_KEY = "SNOWY_THIRD_IAM_ALLOW_LOGIN_FLAG";
    private static final String SNOWY_THIRD_IAM_AUTHORIZE_URL_KEY = "SNOWY_THIRD_IAM_AUTHORIZE_URL";
    private static final String SNOWY_THIRD_IAM_ACCESS_TOKEN_URL_KEY = "SNOWY_THIRD_IAM_ACCESS_TOKEN_URL";
    private static final String SNOWY_THIRD_IAM_USER_INFO_URL_KEY = "SNOWY_THIRD_IAM_USER_INFO_URL";
    private static final String SNOWY_THIRD_IAM_CLIENT_ID_KEY = "SNOWY_THIRD_IAM_CLIENT_ID";
    private static final String SNOWY_THIRD_IAM_CLIENT_SECRET_KEY = "SNOWY_THIRD_IAM_CLIENT_SECRET";
    private static final String SNOWY_THIRD_IAM_REDIRECT_URL_KEY = "SNOWY_THIRD_IAM_REDIRECT_URL";

    // 微信
    private static final String SNOWY_THIRD_WECHAT_ALLOW_LOGIN_FLAG_KEY = "SNOWY_THIRD_WECHAT_ALLOW_LOGIN_FLAG";
    private static final String SNOWY_THIRD_WECHAT_CLIENT_ID_KEY = "SNOWY_THIRD_WECHAT_CLIENT_ID";
    private static final String SNOWY_THIRD_WECHAT_CLIENT_SECRET_KEY = "SNOWY_THIRD_WECHAT_CLIENT_SECRET";
    private static final String SNOWY_THIRD_WECHAT_REDIRECT_URL_KEY = "SNOWY_THIRD_WECHAT_REDIRECT_URL";

    // 钉钉
    private static final String SNOWY_THIRD_DINGTALK_ALLOW_LOGIN_FLAG_KEY = "SNOWY_THIRD_DINGTALK_ALLOW_LOGIN_FLAG";
    private static final String SNOWY_THIRD_DINGTALK_CLIENT_ID_KEY = "SNOWY_THIRD_DINGTALK_CLIENT_ID";
    private static final String SNOWY_THIRD_DINGTALK_CLIENT_SECRET_KEY = "SNOWY_THIRD_DINGTALK_CLIENT_SECRET";
    private static final String SNOWY_THIRD_DINGTALK_REDIRECT_URL_KEY = "SNOWY_THIRD_DINGTALK_REDIRECT_URL";

    // 企业微信
    private static final String SNOWY_THIRD_WORKWECHAT_ALLOW_LOGIN_FLAG_KEY = "SNOWY_THIRD_WORKWECHAT_ALLOW_LOGIN_FLAG";
    private static final String SNOWY_THIRD_WORKWECHAT_AGENT_ID_KEY = "SNOWY_THIRD_WORKWECHAT_AGENT_ID";
    private static final String SNOWY_THIRD_WORKWECHAT_CLIENT_ID_KEY = "SNOWY_THIRD_WORKWECHAT_CLIENT_ID";
    private static final String SNOWY_THIRD_WORKWECHAT_CLIENT_SECRET_KEY = "SNOWY_THIRD_WORKWECHAT_CLIENT_SECRET";
    private static final String SNOWY_THIRD_WORKWECHAT_REDIRECT_URL_KEY = "SNOWY_THIRD_WORKWECHAT_REDIRECT_URL";

    // 飞书
    private static final String SNOWY_THIRD_FEISHU_ALLOW_LOGIN_FLAG_KEY = "SNOWY_THIRD_FEISHU_ALLOW_LOGIN_FLAG";
    private static final String SNOWY_THIRD_FEISHU_CLIENT_ID_KEY = "SNOWY_THIRD_FEISHU_CLIENT_ID";
    private static final String SNOWY_THIRD_FEISHU_CLIENT_SECRET_KEY = "SNOWY_THIRD_FEISHU_CLIENT_SECRET";
    private static final String SNOWY_THIRD_FEISHU_REDIRECT_URL_KEY = "SNOWY_THIRD_FEISHU_REDIRECT_URL";

    // WeLink
    private static final String SNOWY_THIRD_WELINK_ALLOW_LOGIN_FLAG_KEY = "SNOWY_THIRD_WELINK_ALLOW_LOGIN_FLAG";
    private static final String SNOWY_THIRD_WELINK_CLIENT_ID_KEY = "SNOWY_THIRD_WELINK_CLIENT_ID";
    private static final String SNOWY_THIRD_WELINK_CLIENT_SECRET_KEY = "SNOWY_THIRD_WELINK_CLIENT_SECRET";
    private static final String SNOWY_THIRD_WELINK_REDIRECT_URL_KEY = "SNOWY_THIRD_WELINK_REDIRECT_URL";

    // 云之家
    private static final String SNOWY_THIRD_YUNZHIJIA_ALLOW_LOGIN_FLAG_KEY = "SNOWY_THIRD_YUNZHIJIA_ALLOW_LOGIN_FLAG";
    private static final String SNOWY_THIRD_YUNZHIJIA_CLIENT_ID_KEY = "SNOWY_THIRD_YUNZHIJIA_CLIENT_ID";
    private static final String SNOWY_THIRD_YUNZHIJIA_CLIENT_SECRET_KEY = "SNOWY_THIRD_YUNZHIJIA_CLIENT_SECRET";
    private static final String SNOWY_THIRD_YUNZHIJIA_REDIRECT_URL_KEY = "SNOWY_THIRD_YUNZHIJIA_REDIRECT_URL";

    // QQ
    private static final String SNOWY_THIRD_QQ_ALLOW_LOGIN_FLAG_KEY = "SNOWY_THIRD_QQ_ALLOW_LOGIN_FLAG";
    private static final String SNOWY_THIRD_QQ_CLIENT_ID_KEY = "SNOWY_THIRD_QQ_CLIENT_ID";
    private static final String SNOWY_THIRD_QQ_CLIENT_SECRET_KEY = "SNOWY_THIRD_QQ_CLIENT_SECRET";
    private static final String SNOWY_THIRD_QQ_REDIRECT_URL_KEY = "SNOWY_THIRD_QQ_REDIRECT_URL";

    // 微博
    private static final String SNOWY_THIRD_WEIBO_ALLOW_LOGIN_FLAG_KEY = "SNOWY_THIRD_WEIBO_ALLOW_LOGIN_FLAG";
    private static final String SNOWY_THIRD_WEIBO_CLIENT_ID_KEY = "SNOWY_THIRD_WEIBO_CLIENT_ID";
    private static final String SNOWY_THIRD_WEIBO_CLIENT_SECRET_KEY = "SNOWY_THIRD_WEIBO_CLIENT_SECRET";
    private static final String SNOWY_THIRD_WEIBO_REDIRECT_URL_KEY = "SNOWY_THIRD_WEIBO_REDIRECT_URL";

    // 抖音
    private static final String SNOWY_THIRD_DOUYIN_ALLOW_LOGIN_FLAG_KEY = "SNOWY_THIRD_DOUYIN_ALLOW_LOGIN_FLAG";
    private static final String SNOWY_THIRD_DOUYIN_CLIENT_ID_KEY = "SNOWY_THIRD_DOUYIN_CLIENT_ID";
    private static final String SNOWY_THIRD_DOUYIN_CLIENT_SECRET_KEY = "SNOWY_THIRD_DOUYIN_CLIENT_SECRET";
    private static final String SNOWY_THIRD_DOUYIN_REDIRECT_URL_KEY = "SNOWY_THIRD_DOUYIN_REDIRECT_URL";

    // 支付宝
    private static final String SNOWY_THIRD_ALIPAY_ALLOW_LOGIN_FLAG_KEY = "SNOWY_THIRD_ALIPAY_ALLOW_LOGIN_FLAG";
    private static final String SNOWY_THIRD_ALIPAY_CLIENT_ID_KEY = "SNOWY_THIRD_ALIPAY_CLIENT_ID";
    private static final String SNOWY_THIRD_ALIPAY_CLIENT_SECRET_KEY = "SNOWY_THIRD_ALIPAY_CLIENT_SECRET";
    private static final String SNOWY_THIRD_ALIPAY_PUBLIC_KEY_KEY = "SNOWY_THIRD_ALIPAY_PUBLIC_KEY";
    private static final String SNOWY_THIRD_ALIPAY_REDIRECT_URL_KEY = "SNOWY_THIRD_ALIPAY_REDIRECT_URL";

    @Resource
    private CommonCacheOperator commonCacheOperator;

    @Resource
    private DevConfigApi devConfigApi;

    @Resource
    private AuthService authService;

    @Resource(name = "loginUserApi")
    private SaBaseLoginUserApi loginUserApi;

    @Resource(name = "clientLoginUserApi")
    private SaBaseLoginUserApi clientLoginUserApi;

    @Override
    public AuthThirdRenderResult render(AuthThirdRenderParam authThirdRenderParam) {
        // 校验认证源是否存在
        String platform = authThirdRenderParam.getPlatform();
        AuthPlatformEnum.validate(platform);
        // 校验登录端类型
        String clientType = authThirdRenderParam.getClientType();
        SaClientTypeEnum.validate(clientType);
        // 创建客户端
        AuthBaseClient<?> authSourceBaseClient = this.getAuthClient(platform);
        // 获取认证地址，需客户端类型
        String authorizeUrl = authSourceBaseClient.getAuthorizeUrl(clientType);
        // 构造结果
        AuthThirdRenderResult authThirdRenderResult = new AuthThirdRenderResult();
        // 设置授权地址
        authThirdRenderResult.setAuthorizeUrl(authorizeUrl);
        // 返回结果
        return authThirdRenderResult;
    }

    @SuppressWarnings("ALL")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String callback(AuthThirdCallbackParam authThirdCallbackParam, AuthCallback authCallback) {
        // 校验认证源是否存在
        String platform = authThirdCallbackParam.getPlatform();
        AuthPlatformEnum.validate(platform);
        // 创建客户端
        AuthBaseClient<?> authSourceBaseClient = this.getAuthClient(platform);
        // 获取state
        String state = SaHolder.getRequest().getParam(SaOAuth2Consts.Param.state);
        // 校验state
        if(ObjectUtil.isEmpty(state)) {
            state = SaHolder.getRequest().getParam("RelayState");
            if(ObjectUtil.isEmpty(state)) {
                throw new CommonException("state不能为空");
            }
        }
        // 获取缓存操作类
        CommonCacheOperator commonCacheOperator = SpringUtil.getBean(CommonCacheOperator.class);
        // 获取缓存值
        Object stateCacheValueObj = commonCacheOperator.get(CONFIG_CACHE_KEY + state);
        // 判断是否为空
        if(ObjectUtil.isEmpty(stateCacheValueObj)){
            throw new CommonException("state已失效");
        }
        // 转换为json对象
        JSONObject stateCacheValueJsonObject = JSONUtil.parseObj(stateCacheValueObj);
        // 获取登录端类型
        String clientType = stateCacheValueJsonObject.getStr("clientType");
        // 移除缓存
        commonCacheOperator.remove(CONFIG_CACHE_KEY + state);
        // 执行请求
        AuthResponse<AuthUser> authResponse = authSourceBaseClient.doLogin();
        if (authResponse.ok()) {
            // 授权的用户信息
            AuthUser authUser = authResponse.getData();
            // 获取第三方用户id
            String uuid = authUser.getUuid();
            // 获取第三方用户来源
            String source = authUser.getSource();
            // 根据第三方用户id和用户来源获取用户信息
            AuthThirdUser authThirdUser = this.getOne(new LambdaQueryWrapper<AuthThirdUser>().eq(AuthThirdUser::getThirdId, uuid)
                    .eq(AuthThirdUser::getCategory, source));
            // 定义系统用户id
            String userId;
            if(ObjectUtil.isEmpty(authThirdUser)) {
                // 如果用户不存在，则需要绑定用户，先将第三方用户id插入数据库
                String id = this.insertAuthThirdUser(authUser);
                // 返回
                return "needBind:" + id;
            } else {
                // 否则直接获取用户id，判断是否存在（有可能没绑定）
                userId = authThirdUser.getUserId();
                if(ObjectUtil.isEmpty(userId)) {
                    return "needBind:" + authThirdUser.getId();
                }
            }
            // 定义生成的token
            String token;
            // 根据客户端类型执行登录，返回token
            if(SaClientTypeEnum.B.getValue().equals(clientType)) {
                return authService.doLoginById(userId, AuthDeviceTypeEnum.PC.getValue(), SaClientTypeEnum.B.getValue());
            } else {
                return authService.doLoginById(userId, AuthDeviceTypeEnum.PC.getValue(), SaClientTypeEnum.C.getValue());
            }
        } else {
            throw new CommonException("第三方登录授权回调失败，原因：{}", authResponse.getMsg());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String bindAccount(AuthThirdBindAccountParam authThirdBindAccountParam) {
        AuthThirdUser authThirdUser = this.getById(authThirdBindAccountParam.getThirdId());
        if(ObjectUtil.isEmpty(authThirdUser)) {
            throw new CommonException("三方用户不存在");
        }
        if(ObjectUtil.isNotEmpty(authThirdUser.getUserId())) {
            throw new CommonException("三方用户已绑定，不可重复绑定");
        }
        AuthAccountPasswordLoginParam authAccountPasswordLoginParam = new AuthAccountPasswordLoginParam();
        authAccountPasswordLoginParam.setAccount(authThirdBindAccountParam.getAccount());
        authAccountPasswordLoginParam.setPassword(authThirdBindAccountParam.getPassword());
        authAccountPasswordLoginParam.setValidCode(authThirdBindAccountParam.getValidCode());
        authAccountPasswordLoginParam.setValidCodeReqNo(authThirdBindAccountParam.getValidCodeReqNo());
        String token = authService.doLogin(authAccountPasswordLoginParam, SaClientTypeEnum.B.getValue());
        String userId = StpUtil.getLoginIdAsString();
        authThirdUser.setUserId(userId);
        this.updateById(authThirdUser);
        return token;
    }

    @Override
    public Page<AuthThirdUser> page(AuthThirdUserPageParam authThirdUserPageParam) {
        QueryWrapper<AuthThirdUser> queryWrapper = new QueryWrapper<AuthThirdUser>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(authThirdUserPageParam.getCategory())) {
            queryWrapper.lambda().eq(AuthThirdUser::getCategory, authThirdUserPageParam.getCategory());
        }
        if(ObjectUtil.isNotEmpty(authThirdUserPageParam.getSearchKey())) {
            queryWrapper.and(q -> q.lambda().like(AuthThirdUser::getName, authThirdUserPageParam.getSearchKey())
                    .or().like(AuthThirdUser::getNickname, authThirdUserPageParam.getSearchKey()));
        }
        if(ObjectUtil.isAllNotEmpty(authThirdUserPageParam.getSortField(), authThirdUserPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(authThirdUserPageParam.getSortOrder());
            queryWrapper.orderBy(true, authThirdUserPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(authThirdUserPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByDesc(AuthThirdUser::getCreateTime);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    /**
     * 保存三方用户并返回主键
     *
     * @author xuyuxiang
     * @date 2022/7/9 14:58
     */
    private String insertAuthThirdUser(AuthUser authUser) {
        AuthThirdUser authThirdUser = new AuthThirdUser();
        authThirdUser.setThirdId(authUser.getUuid());
        authThirdUser.setUserId(null);
        authThirdUser.setAvatar(authUser.getAvatar());
        authThirdUser.setName(authUser.getUsername());
        authThirdUser.setNickname(authUser.getNickname());
        authThirdUser.setGender(authUser.getGender().getDesc());
        authThirdUser.setCategory(authUser.getSource());
        authThirdUser.setExtJson(JSONUtil.toJsonStr(authUser.getRawUserInfo()));
        this.save(authThirdUser);
        return authThirdUser.getId();
    }

    /**
     * 创建授权请求客户端
     *
     * @author xuyuxiang
     * @date 2022/7/8 16:48
     **/
    private AuthBaseClient<?> getAuthClient(String platform) {
        AuthBaseClient<?> authClient = null;
        platform = platform.toUpperCase();
        HttpUtil.setHttp(new HutoolImpl());
        AuthPlatformEnum.validate(platform);
        if(platform.equals(AuthPlatformEnum.OAUTH.getValue())) {
            // 检查是否允许登录
            if(!Boolean.parseBoolean(devConfigApi.getValueByKey(SNOWY_THIRD_OAUTH_ALLOW_LOGIN_FLAG_KEY))) {
                throw new CommonException("OAUTH登录已禁用");
            }
            if(!devConfigApi.getValueByKey(SNOWY_THIRD_OAUTH_REDIRECT_URL_KEY).startsWith("http")) {
                throw new CommonException("重定向地址配置错误");
            }
            // OAUTH登录
            authClient = AuthClientFactory.createClient(platform, JSONUtil.createObj()
                    .set("clientId", devConfigApi.getValueByKey(SNOWY_THIRD_OAUTH_CLIENT_ID_KEY))
                    .set("clientSecret", devConfigApi.getValueByKey(SNOWY_THIRD_OAUTH_CLIENT_SECRET_KEY))
                    .set("callbackUrl", devConfigApi.getValueByKey(SNOWY_THIRD_OAUTH_REDIRECT_URL_KEY))
                    .set("authorizeUrl", devConfigApi.getValueByKey(SNOWY_THIRD_OAUTH_AUTHORIZE_URL_KEY))
                    .set("accessTokenUrl", devConfigApi.getValueByKey(SNOWY_THIRD_OAUTH_ACCESS_TOKEN_URL_KEY))
                    .set("userInfoUrl", devConfigApi.getValueByKey(SNOWY_THIRD_OAUTH_USER_INFO_URL_KEY))
                    .set("scope", devConfigApi.getValueByKey(SNOWY_THIRD_OAUTH_SCOPE_KEY))
                    .set("sourceProperty", devConfigApi.getValueByKey(SNOWY_THIRD_OAUTH_SOURCE_PROPERTY_KEY))
                    .set("targetProperty", devConfigApi.getValueByKey(SNOWY_THIRD_OAUTH_TARGET_PROPERTY_KEY)));
        }
        if(platform.equals(AuthPlatformEnum.OIDC.getValue())) {
            // 检查是否允许登录
            if(!Boolean.parseBoolean(devConfigApi.getValueByKey(SNOWY_THIRD_OIDC_ALLOW_LOGIN_FLAG_KEY))) {
                throw new CommonException("OIDC登录已禁用");
            }
            if(!devConfigApi.getValueByKey(SNOWY_THIRD_OIDC_REDIRECT_URL_KEY).startsWith("http")) {
                throw new CommonException("重定向地址配置错误");
            }
            // OIDC登录
            authClient = AuthClientFactory.createClient(platform, JSONUtil.createObj()
                    .set("clientId", devConfigApi.getValueByKey(SNOWY_THIRD_OIDC_CLIENT_ID_KEY))
                    .set("clientSecret", devConfigApi.getValueByKey(SNOWY_THIRD_OIDC_CLIENT_SECRET_KEY))
                    .set("callbackUrl", devConfigApi.getValueByKey(SNOWY_THIRD_OIDC_REDIRECT_URL_KEY))
                    .set("authorizeUrl", devConfigApi.getValueByKey(SNOWY_THIRD_OIDC_AUTHORIZE_URL_KEY))
                    .set("accessTokenUrl", devConfigApi.getValueByKey(SNOWY_THIRD_OIDC_ACCESS_TOKEN_URL_KEY))
                    .set("userInfoUrl", devConfigApi.getValueByKey(SNOWY_THIRD_OIDC_USER_INFO_URL_KEY))
                    .set("scope", devConfigApi.getValueByKey(SNOWY_THIRD_OIDC_SCOPE_KEY))
                    .set("publicKey", devConfigApi.getValueByKey(SNOWY_THIRD_OIDC_PUBLIC_KEY_KEY))
                    .set("algorithm", devConfigApi.getValueByKey(SNOWY_THIRD_OIDC_ALGORITHM_KEY))
                    .set("sourceProperty", devConfigApi.getValueByKey(SNOWY_THIRD_OIDC_SOURCE_PROPERTY_KEY))
                    .set("targetProperty", devConfigApi.getValueByKey(SNOWY_THIRD_OIDC_TARGET_PROPERTY_KEY)));
        }

        if(platform.equals(AuthPlatformEnum.JWT.getValue())) {
            // 检查是否允许登录
            if(!Boolean.parseBoolean(devConfigApi.getValueByKey(SNOWY_THIRD_JWT_ALLOW_LOGIN_FLAG_KEY))) {
                throw new CommonException("JWT登录已禁用");
            }
            if(!devConfigApi.getValueByKey(SNOWY_THIRD_JWT_REDIRECT_URL_KEY).startsWith("http")) {
                throw new CommonException("重定向地址配置错误");
            }
            // JWT登录
            authClient = AuthClientFactory.createClient(platform, JSONUtil.createObj()
                    .set("clientId", devConfigApi.getValueByKey(SNOWY_THIRD_JWT_CLIENT_ID_KEY))
                    .set("clientSecret", devConfigApi.getValueByKey(SNOWY_THIRD_JWT_CLIENT_SECRET_KEY))
                    .set("callbackUrl", devConfigApi.getValueByKey(SNOWY_THIRD_JWT_REDIRECT_URL_KEY))
                    .set("authorizeUrl", devConfigApi.getValueByKey(SNOWY_THIRD_JWT_AUTHORIZE_URL_KEY))
                    .set("publicKey", devConfigApi.getValueByKey(SNOWY_THIRD_JWT_PUBLIC_KEY_KEY))
                    .set("algorithm", devConfigApi.getValueByKey(SNOWY_THIRD_JWT_ALGORITHM_KEY))
                    .set("sourceProperty", devConfigApi.getValueByKey(SNOWY_THIRD_JWT_SOURCE_PROPERTY_KEY))
                    .set("targetProperty", devConfigApi.getValueByKey(SNOWY_THIRD_JWT_TARGET_PROPERTY_KEY)));
        }

        if(platform.equals(AuthPlatformEnum.CAS.getValue())) {
            // 检查是否允许登录
            if(!Boolean.parseBoolean(devConfigApi.getValueByKey(SNOWY_THIRD_CAS_ALLOW_LOGIN_FLAG_KEY))) {
                throw new CommonException("CAS登录已禁用");
            }
            // CAS登录
            authClient = AuthClientFactory.createClient(platform, JSONUtil.createObj()
                    .set("casServerLoginUrl", devConfigApi.getValueByKey(SNOWY_THIRD_CAS_SERVER_LOGIN_URL_KEY))
                    .set("casServerValidateUrl", devConfigApi.getValueByKey(SNOWY_THIRD_CAS_SERVER_VALIDATE_URL_KEY))
                    .set("casServerProtocolVersion", devConfigApi.getValueByKey(SNOWY_THIRD_CAS_SERVER_PROTOCOL_VERSION_KEY))
                    .set("serviceUrl", devConfigApi.getValueByKey(SNOWY_THIRD_CAS_SERVICE_URL_KEY))
                    .set("sourceProperty", devConfigApi.getValueByKey(SNOWY_THIRD_CAS_SOURCE_PROPERTY_KEY))
                    .set("targetProperty", devConfigApi.getValueByKey(SNOWY_THIRD_CAS_TARGET_PROPERTY_KEY)));
        }

        if(platform.equals(AuthPlatformEnum.SAML.getValue())) {
            // 检查是否允许登录
            if(!Boolean.parseBoolean(devConfigApi.getValueByKey(SNOWY_THIRD_SAML_ALLOW_LOGIN_FLAG_KEY))) {
                throw new CommonException("SAML登录已禁用");
            }
            // SAML登录
            authClient = AuthClientFactory.createClient(platform, JSONUtil.createObj()
                    .set("spMetaData", devConfigApi.getValueByKey(SNOWY_THIRD_SAML_SP_META_DATA_KEY))
                    .set("certificate", devConfigApi.getValueByKey(SNOWY_THIRD_SAML_CERTIFICATE_KEY))
                    .set("privateKey", devConfigApi.getValueByKey(SNOWY_THIRD_SAML_PRIVATE_KEY_KEY))
                    .set("publicKey", devConfigApi.getValueByKey(SNOWY_THIRD_SAML_PUBLIC_KEY_KEY))
                    .set("spEntityId", devConfigApi.getValueByKey(SNOWY_THIRD_SAML_SP_ENTITY_ID_KEY))
                    .set("spAclUrl", devConfigApi.getValueByKey(SNOWY_THIRD_SAML_SP_ACL_URL_KEY))
                    .set("idpMetaData", devConfigApi.getValueByKey(SNOWY_THIRD_SAML_IDP_META_DATA_KEY))
                    .set("bindingType", devConfigApi.getValueByKey(SNOWY_THIRD_SAML_BINDING_TYPE_KEY))
                    .set("sourceProperty", devConfigApi.getValueByKey(SNOWY_THIRD_SAML_SOURCE_PROPERTY_KEY))
                    .set("targetProperty", devConfigApi.getValueByKey(SNOWY_THIRD_SAML_TARGET_PROPERTY_KEY)));
        }

        // =======以下为OIDC协议的具体实现======= //

        if(platform.equals(AuthPlatformEnum.IAM.getValue())) {
            // 检查是否允许登录
            if(!Boolean.parseBoolean(devConfigApi.getValueByKey(SNOWY_THIRD_IAM_ALLOW_LOGIN_FLAG_KEY))) {
                throw new CommonException("IAM登录已禁用");
            }
            if(!devConfigApi.getValueByKey(SNOWY_THIRD_IAM_REDIRECT_URL_KEY).startsWith("http")) {
                throw new CommonException("重定向地址配置错误");
            }
            // IAM登录
            authClient = AuthClientFactory.createClient(platform, JSONUtil.createObj()
                    .set("clientId", devConfigApi.getValueByKey(SNOWY_THIRD_IAM_CLIENT_ID_KEY))
                    .set("clientSecret", devConfigApi.getValueByKey(SNOWY_THIRD_IAM_CLIENT_SECRET_KEY))
                    .set("callbackUrl", devConfigApi.getValueByKey(SNOWY_THIRD_IAM_REDIRECT_URL_KEY))
                    .set("authorizeUrl", devConfigApi.getValueByKey(SNOWY_THIRD_IAM_AUTHORIZE_URL_KEY))
                    .set("accessTokenUrl", devConfigApi.getValueByKey(SNOWY_THIRD_IAM_ACCESS_TOKEN_URL_KEY))
                    .set("userInfoUrl", devConfigApi.getValueByKey(SNOWY_THIRD_IAM_USER_INFO_URL_KEY)));
        }
        if(platform.equals(AuthPlatformEnum.WECHAT.getValue())){
            // 检查是否允许登录
            if(!Boolean.parseBoolean(devConfigApi.getValueByKey(SNOWY_THIRD_WECHAT_ALLOW_LOGIN_FLAG_KEY))) {
                throw new CommonException("微信登录已禁用");
            }
            if(!devConfigApi.getValueByKey(SNOWY_THIRD_WECHAT_REDIRECT_URL_KEY).startsWith("http")) {
                throw new CommonException("重定向地址配置错误");
            }
            // 微信登录
            authClient = AuthClientFactory.createClient(platform, JSONUtil.createObj()
                    .set("clientId", devConfigApi.getValueByKey(SNOWY_THIRD_WECHAT_CLIENT_ID_KEY))
                    .set("clientSecret", devConfigApi.getValueByKey(SNOWY_THIRD_WECHAT_CLIENT_SECRET_KEY))
                    .set("callbackUrl", devConfigApi.getValueByKey(SNOWY_THIRD_WECHAT_REDIRECT_URL_KEY)));
        }
        if(platform.equals(AuthPlatformEnum.DINGTALK.getValue())) {
            // 检查是否允许登录
            if(!Boolean.parseBoolean(devConfigApi.getValueByKey(SNOWY_THIRD_DINGTALK_ALLOW_LOGIN_FLAG_KEY))) {
                throw new CommonException("钉钉登录已禁用");
            }
            if(!devConfigApi.getValueByKey(SNOWY_THIRD_DINGTALK_REDIRECT_URL_KEY).startsWith("http")) {
                throw new CommonException("重定向地址配置错误");
            }
            // 钉钉登录
            authClient = AuthClientFactory.createClient(platform, JSONUtil.createObj()
                    .set("clientId", devConfigApi.getValueByKey(SNOWY_THIRD_DINGTALK_CLIENT_ID_KEY))
                    .set("clientSecret", devConfigApi.getValueByKey(SNOWY_THIRD_DINGTALK_CLIENT_SECRET_KEY))
                    .set("callbackUrl", devConfigApi.getValueByKey(SNOWY_THIRD_DINGTALK_REDIRECT_URL_KEY)));
        }
        if(platform.equals(AuthPlatformEnum.WORKWECHAT.getValue())) {
            // 检查是否允许登录
            if(!Boolean.parseBoolean(devConfigApi.getValueByKey(SNOWY_THIRD_WORKWECHAT_ALLOW_LOGIN_FLAG_KEY))) {
                throw new CommonException("企业微信登录已禁用");
            }
            if(!devConfigApi.getValueByKey(SNOWY_THIRD_WORKWECHAT_REDIRECT_URL_KEY).startsWith("http")) {
                throw new CommonException("重定向地址配置错误");
            }
            // 企业微信登录
            authClient = AuthClientFactory.createClient(platform, JSONUtil.createObj()
                    .set("agentId", devConfigApi.getValueByKey(SNOWY_THIRD_WORKWECHAT_AGENT_ID_KEY))
                    .set("clientId", devConfigApi.getValueByKey(SNOWY_THIRD_WORKWECHAT_CLIENT_ID_KEY))
                    .set("clientSecret", devConfigApi.getValueByKey(SNOWY_THIRD_WORKWECHAT_CLIENT_SECRET_KEY))
                    .set("callbackUrl", devConfigApi.getValueByKey(SNOWY_THIRD_WORKWECHAT_REDIRECT_URL_KEY)));
        }
        if (platform.equals(AuthPlatformEnum.FEISHU.getValue())) {
            // 检查是否允许登录
            if(!Boolean.parseBoolean(devConfigApi.getValueByKey(SNOWY_THIRD_FEISHU_ALLOW_LOGIN_FLAG_KEY))) {
                throw new CommonException("飞书登录已禁用");
            }
            if(!devConfigApi.getValueByKey(SNOWY_THIRD_FEISHU_REDIRECT_URL_KEY).startsWith("http")) {
                throw new CommonException("重定向地址配置错误");
            }
            // 飞书登录
            authClient = AuthClientFactory.createClient(platform, JSONUtil.createObj()
                    .set("clientId", devConfigApi.getValueByKey(SNOWY_THIRD_FEISHU_CLIENT_ID_KEY))
                    .set("clientSecret", devConfigApi.getValueByKey(SNOWY_THIRD_FEISHU_CLIENT_SECRET_KEY))
                    .set("callbackUrl", devConfigApi.getValueByKey(SNOWY_THIRD_FEISHU_REDIRECT_URL_KEY)));
        }
        if (platform.equals(AuthPlatformEnum.WELINK.getValue())) {
            // 检查是否允许登录
            if(!Boolean.parseBoolean(devConfigApi.getValueByKey(SNOWY_THIRD_WELINK_ALLOW_LOGIN_FLAG_KEY))) {
                throw new CommonException("企业微信登录已禁用");
            }
            if(!devConfigApi.getValueByKey(SNOWY_THIRD_WELINK_REDIRECT_URL_KEY).startsWith("http")) {
                throw new CommonException("重定向地址配置错误");
            }
            // WeLink登录
            authClient = AuthClientFactory.createClient(platform, JSONUtil.createObj()
                    .set("clientId", devConfigApi.getValueByKey(SNOWY_THIRD_WELINK_CLIENT_ID_KEY))
                    .set("clientSecret", devConfigApi.getValueByKey(SNOWY_THIRD_WELINK_CLIENT_SECRET_KEY))
                    .set("callbackUrl", devConfigApi.getValueByKey(SNOWY_THIRD_WELINK_REDIRECT_URL_KEY)));
        }
        if(platform.equals(AuthPlatformEnum.YUNZHIJIA.getValue())){
            // 检查是否允许登录
            if(!Boolean.parseBoolean(devConfigApi.getValueByKey(SNOWY_THIRD_YUNZHIJIA_ALLOW_LOGIN_FLAG_KEY))) {
                throw new CommonException("云之家登录已禁用");
            }
            if(!devConfigApi.getValueByKey(SNOWY_THIRD_YUNZHIJIA_REDIRECT_URL_KEY).startsWith("http")) {
                throw new CommonException("重定向地址配置错误");
            }
            // 云之家登录
            authClient = AuthClientFactory.createClient(platform, JSONUtil.createObj()
                    .set("clientId", devConfigApi.getValueByKey(SNOWY_THIRD_YUNZHIJIA_CLIENT_ID_KEY))
                    .set("clientSecret", devConfigApi.getValueByKey(SNOWY_THIRD_YUNZHIJIA_CLIENT_SECRET_KEY))
                    .set("callbackUrl", devConfigApi.getValueByKey(SNOWY_THIRD_YUNZHIJIA_REDIRECT_URL_KEY)));
        }
        if(platform.equals(AuthPlatformEnum.QQ.getValue())){
            // 检查是否允许登录
            if(!Boolean.parseBoolean(devConfigApi.getValueByKey(SNOWY_THIRD_QQ_ALLOW_LOGIN_FLAG_KEY))) {
                throw new CommonException("QQ登录已禁用");
            }
            if(!devConfigApi.getValueByKey(SNOWY_THIRD_QQ_REDIRECT_URL_KEY).startsWith("http")) {
                throw new CommonException("重定向地址配置错误");
            }
            // QQ登录
            authClient = AuthClientFactory.createClient(platform, JSONUtil.createObj()
                    .set("clientId", devConfigApi.getValueByKey(SNOWY_THIRD_QQ_CLIENT_ID_KEY))
                    .set("clientSecret", devConfigApi.getValueByKey(SNOWY_THIRD_QQ_CLIENT_SECRET_KEY))
                    .set("callbackUrl", devConfigApi.getValueByKey(SNOWY_THIRD_QQ_REDIRECT_URL_KEY)));
        }
        if(platform.equals(AuthPlatformEnum.WEIBO.getValue())){
            // 检查是否允许登录
            if(!Boolean.parseBoolean(devConfigApi.getValueByKey(SNOWY_THIRD_WEIBO_ALLOW_LOGIN_FLAG_KEY))) {
                throw new CommonException("微博登录已禁用");
            }
            if(!devConfigApi.getValueByKey(SNOWY_THIRD_WEIBO_REDIRECT_URL_KEY).startsWith("http")) {
                throw new CommonException("重定向地址配置错误");
            }
            // 微博登录
            authClient = AuthClientFactory.createClient(platform, JSONUtil.createObj()
                    .set("clientId", devConfigApi.getValueByKey(SNOWY_THIRD_WEIBO_CLIENT_ID_KEY))
                    .set("clientSecret", devConfigApi.getValueByKey(SNOWY_THIRD_WEIBO_CLIENT_SECRET_KEY))
                    .set("callbackUrl", devConfigApi.getValueByKey(SNOWY_THIRD_WEIBO_REDIRECT_URL_KEY)));
        }
        if(platform.equals(AuthPlatformEnum.DOUYIN.getValue())){
            // 检查是否允许登录
            if(!Boolean.parseBoolean(devConfigApi.getValueByKey(SNOWY_THIRD_DOUYIN_ALLOW_LOGIN_FLAG_KEY))) {
                throw new CommonException("抖音登录已禁用");
            }
            if(!devConfigApi.getValueByKey(SNOWY_THIRD_DOUYIN_REDIRECT_URL_KEY).startsWith("http")) {
                throw new CommonException("重定向地址配置错误");
            }
            // 抖音登录
            authClient = AuthClientFactory.createClient(platform, JSONUtil.createObj()
                    .set("clientId", devConfigApi.getValueByKey(SNOWY_THIRD_DOUYIN_CLIENT_ID_KEY))
                    .set("clientSecret", devConfigApi.getValueByKey(SNOWY_THIRD_DOUYIN_CLIENT_SECRET_KEY))
                    .set("callbackUrl", devConfigApi.getValueByKey(SNOWY_THIRD_DOUYIN_REDIRECT_URL_KEY)));
        }
        if(platform.equals(AuthPlatformEnum.ALIPAY.getValue())){
            // 检查是否允许登录
            if(!Boolean.parseBoolean(devConfigApi.getValueByKey(SNOWY_THIRD_ALIPAY_ALLOW_LOGIN_FLAG_KEY))) {
                throw new CommonException("支付宝登录已禁用");
            }
            if(!devConfigApi.getValueByKey(SNOWY_THIRD_ALIPAY_REDIRECT_URL_KEY).startsWith("http")) {
                throw new CommonException("重定向地址配置错误");
            }
            // 支付宝登录
            authClient = AuthClientFactory.createClient(platform, JSONUtil.createObj()
                    .set("clientId", devConfigApi.getValueByKey(SNOWY_THIRD_ALIPAY_CLIENT_ID_KEY))
                    .set("clientSecret", devConfigApi.getValueByKey(SNOWY_THIRD_ALIPAY_CLIENT_SECRET_KEY))
                    .set("callbackUrl", devConfigApi.getValueByKey(SNOWY_THIRD_ALIPAY_REDIRECT_URL_KEY))
                    .set("publicKey", devConfigApi.getValueByKey(SNOWY_THIRD_ALIPAY_PUBLIC_KEY_KEY)));
        }
        return authClient;
    }
}
