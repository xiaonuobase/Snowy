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
package vip.xiaonuo.auth.modular.login.service.impl;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import vip.xiaonuo.auth.api.SaBaseLoginUserApi;
import vip.xiaonuo.auth.core.enums.SaClientTypeEnum;
import vip.xiaonuo.auth.core.pojo.SaBaseClientLoginUser;
import vip.xiaonuo.auth.core.pojo.SaBaseLoginUser;
import vip.xiaonuo.auth.core.util.AuthEmailFormatUtil;
import vip.xiaonuo.auth.core.util.StpClientLoginUserUtil;
import vip.xiaonuo.auth.core.util.StpClientUtil;
import vip.xiaonuo.auth.core.util.StpLoginUserUtil;
import vip.xiaonuo.auth.modular.login.enums.AuthDeviceTypeEnum;
import vip.xiaonuo.auth.modular.login.enums.AuthExceptionEnum;
import vip.xiaonuo.auth.modular.login.enums.AuthPhoneOrEmailTypeEnum;
import vip.xiaonuo.auth.modular.login.enums.AuthStrategyWhenNoUserWithPhoneOrEmailEnum;
import vip.xiaonuo.auth.modular.login.param.*;
import vip.xiaonuo.auth.modular.login.result.AuthPicValidCodeResult;
import vip.xiaonuo.auth.modular.login.service.AuthService;
import vip.xiaonuo.common.cache.CommonCacheOperator;
import vip.xiaonuo.common.consts.CacheConstant;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.util.CommonCryptogramUtil;
import vip.xiaonuo.common.util.CommonEmailUtil;
import vip.xiaonuo.common.util.CommonTimeFormatUtil;
import vip.xiaonuo.dev.api.DevConfigApi;
import vip.xiaonuo.dev.api.DevEmailApi;
import vip.xiaonuo.dev.api.DevSmsApi;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 登录Service接口实现类
 *
 * @author xuyuxiang
 * @date 2021/12/23 21:52
 */
@Service
public class AuthServiceImpl implements AuthService {

    /** B端验证码是否开启（适用图片验证码） */
    private static final String SNOWY_SYS_DEFAULT_CAPTCHA_OPEN_FLAG_FOR_B_KEY = "SNOWY_SYS_DEFAULT_CAPTCHA_OPEN_FLAG_FOR_B";

    /** C端验证码是否开启（适用图片验证码） */
    private static final String SNOWY_SYS_DEFAULT_CAPTCHA_OPEN_FLAG_FOR_C_KEY = "SNOWY_SYS_DEFAULT_CAPTCHA_OPEN_FLAG_FOR_C";

    /** B端验证码失效时间（适用图片验证码和短信验证码，单位：分钟，默认5分钟有效） */
    private static final String SNOWY_SYS_DEFAULT_CAPTCHA_EXPIRED_DURATION_FOR_B_KEY = "SNOWY_SYS_DEFAULT_CAPTCHA_EXPIRED_DURATION_FOR_B";

    /** C端验证码失效时间（适用图片验证码和短信验证码，单位：分钟，默认5分钟有效） */
    private static final String SNOWY_SYS_DEFAULT_CAPTCHA_EXPIRED_DURATION_FOR_C_KEY = "SNOWY_SYS_DEFAULT_CAPTCHA_EXPIRED_DURATION_FOR_C";

    /** B端登录验证码短信消息模板编码 */
    private static final String SNOWY_SMS_TEMPLATE_VALID_CODE_LOGIN_FOR_B_KEY = "SNOWY_SMS_TEMPLATE_VALID_CODE_LOGIN_FOR_B";

    /** C端登录验证码短信消息模板编码 */
    private static final String SNOWY_SMS_TEMPLATE_VALID_CODE_LOGIN_FOR_C_KEY = "SNOWY_SMS_TEMPLATE_VALID_CODE_LOGIN_FOR_C";

    /** B端登录验证码邮件消息模板内容 */
    private static final String SNOWY_EMAIL_TEMPLATE_VALID_CODE_LOGIN_FOR_B_KEY = "SNOWY_EMAIL_TEMPLATE_VALID_CODE_LOGIN_FOR_B";

    /** C端登录验证码邮件消息模板内容 */
    private static final String SNOWY_EMAIL_TEMPLATE_VALID_CODE_LOGIN_FOR_C_KEY = "SNOWY_EMAIL_TEMPLATE_VALID_CODE_LOGIN_FOR_C";

    /** B端连续登录失败持续时间（即N分钟内连续登录失败，单位：分钟） */
    private static final String SNOWY_SYS_DEFAULT_CONTINUOUS_LOGIN_FAIL_DURATION_FOR_B_KEY = "SNOWY_SYS_DEFAULT_CONTINUOUS_LOGIN_FAIL_DURATION_FOR_B";

    /** C端连续登录失败持续时间（即N分钟内连续登录失败，单位：分钟） */
    private static final String SNOWY_SYS_DEFAULT_CONTINUOUS_LOGIN_FAIL_DURATION_FOR_C_KEY = "SNOWY_SYS_DEFAULT_CONTINUOUS_LOGIN_FAIL_DURATION_FOR_C";

    /** B端连续登录失败次数（即指定分钟内连续登录失败N次） */
    private static final String SNOWY_SYS_DEFAULT_CONTINUOUS_LOGIN_FAIL_TIMES_FOR_B_KEY = "SNOWY_SYS_DEFAULT_CONTINUOUS_LOGIN_FAIL_TIMES_FOR_B";

    /** C端连续登录失败次数（即指定分钟内连续登录失败N次） */
    private static final String SNOWY_SYS_DEFAULT_CONTINUOUS_LOGIN_FAIL_TIMES_FOR_C_KEY = "SNOWY_SYS_DEFAULT_CONTINUOUS_LOGIN_FAIL_TIMES_FOR_C";

    /** B端连续登录失败锁定时间（即指定分钟内连续登录失败指定次数，锁定N分钟，单位：分钟） */
    private static final String SNOWY_SYS_DEFAULT_CONTINUOUS_LOGIN_FAIL_LOCK_DURATION_FOR_B_KEY = "SNOWY_SYS_DEFAULT_CONTINUOUS_LOGIN_FAIL_LOCK_DURATION_FOR_B";

    /** C端连续登录失败锁定时间（即指定分钟内连续登录失败指定次数，锁定N分钟，单位：分钟） */
    private static final String SNOWY_SYS_DEFAULT_CONTINUOUS_LOGIN_FAIL_LOCK_DURATION_FOR_C_KEY = "SNOWY_SYS_DEFAULT_CONTINUOUS_LOGIN_FAIL_LOCK_DURATION_FOR_C";

    /** B端手机号登录是否开启 */
    private static final String SNOWY_SYS_DEFAULT_ALLOW_PHONE_LOGIN_FLAG_FOR_B_KEY = "SNOWY_SYS_DEFAULT_ALLOW_PHONE_LOGIN_FLAG_FOR_B";

    /** C端手机号登录是否开启 */
    private static final String SNOWY_SYS_DEFAULT_ALLOW_PHONE_LOGIN_FLAG_FOR_C_KEY = "SNOWY_SYS_DEFAULT_ALLOW_PHONE_LOGIN_FLAG_FOR_C";

    /** B端邮箱登录是否开启 */
    private static final String SNOWY_SYS_DEFAULT_ALLOW_EMAIL_LOGIN_FLAG_FOR_B_KEY = "SNOWY_SYS_DEFAULT_ALLOW_EMAIL_LOGIN_FLAG_FOR_B";

    /** C端邮箱登录是否开启 */
    private static final String SNOWY_SYS_DEFAULT_ALLOW_EMAIL_LOGIN_FLAG_FOR_C_KEY = "SNOWY_SYS_DEFAULT_ALLOW_EMAIL_LOGIN_FLAG_FOR_C";

    /** B端手机号无对应用户时策略 */
    private static final String SNOWY_SYS_DEFAULT_STRATEGY_WHEN_NO_USER_WITH_PHONE_FOR_B_KEY = "SNOWY_SYS_DEFAULT_STRATEGY_WHEN_NO_USER_WITH_PHONE_FOR_B";

    /** C端手机号无对应用户时策略 */
    private static final String SNOWY_SYS_DEFAULT_STRATEGY_WHEN_NO_USER_WITH_PHONE_FOR_C_KEY = "SNOWY_SYS_DEFAULT_STRATEGY_WHEN_NO_USER_WITH_PHONE_FOR_C";

    /** B端邮箱无对应用户时策略 */
    private static final String SNOWY_SYS_DEFAULT_STRATEGY_WHEN_NO_USER_WITH_EMAIL_FOR_B_KEY = "SNOWY_SYS_DEFAULT_STRATEGY_WHEN_NO_USER_WITH_EMAIL_FOR_B";

    /** C端邮箱无对应用户时策略 */
    private static final String SNOWY_SYS_DEFAULT_STRATEGY_WHEN_NO_USER_WITH_EMAIL_FOR_C_KEY = "SNOWY_SYS_DEFAULT_STRATEGY_WHEN_NO_USER_WITH_EMAIL_FOR_C";

    /** B端注册是否开启 */
    private static final String SNOWY_SYS_DEFAULT_ALLOW_REGISTER_FLAG_FOR_B_KEY = "SNOWY_SYS_DEFAULT_ALLOW_REGISTER_FLAG_FOR_B";

    /** C端注册是否开启 */
    private static final String SNOWY_SYS_DEFAULT_ALLOW_REGISTER_FLAG_FOR_C_KEY = "SNOWY_SYS_DEFAULT_ALLOW_REGISTER_FLAG_FOR_C";

    /** 验证码缓存前缀 */
    private static final String AUTH_VALID_CODE_CACHE_KEY = "auth-validCode:";

    /** 失败次数缓存前缀 */
    private static final String LOGIN_ERROR_TIMES_KEY_PREFIX = "login-error-times:";

    @Resource(name = "loginUserApi")
    private SaBaseLoginUserApi loginUserApi;

    @Resource(name = "clientLoginUserApi")
    private SaBaseLoginUserApi clientLoginUserApi;

    @Resource
    private DevConfigApi devConfigApi;

    @Resource
    private DevSmsApi devSmsApi;

    @Resource
    private DevEmailApi devEmailApi;

    @Resource
    private CommonCacheOperator commonCacheOperator;

    @Override
    public AuthPicValidCodeResult getPicCaptcha(String type) {
        // 生成验证码，随机4位字符
        CircleCaptcha circleCaptcha = CaptchaUtil.createCircleCaptcha(100, 38, 4, 10);
        // 定义返回结果
        AuthPicValidCodeResult authPicValidCodeResult = new AuthPicValidCodeResult();
        // 获取验证码的值
        String validCode = circleCaptcha.getCode();
        // 获取验证码的base64
        String validCodeBase64 = circleCaptcha.getImageBase64Data();
        // 生成请求号
        String validCodeReqNo = IdWorker.getIdStr();
        // 将base64返回前端
        authPicValidCodeResult.setValidCodeBase64(validCodeBase64);
        // 将请求号返回前端
        authPicValidCodeResult.setValidCodeReqNo(validCodeReqNo);
        // 获取验证码失效时间（单位：秒）
        long validCodeExpiredDuration = this.getValidCodeExpiredDuration(type);
        // 将请求号作为key，验证码的值作为value放到redis，用于校验
        commonCacheOperator.put(AUTH_VALID_CODE_CACHE_KEY + validCodeReqNo, validCode, validCodeExpiredDuration);
        return authPicValidCodeResult;
    }

    @Override
    public String getPhoneValidCode(AuthGetPhoneValidCodeParam authGetPhoneValidCodeParam, String type) {
        // 校验是否允许手机号登录
        this.checkAllowPhoneLoginFlag(type);
        // 手机号
        String phone = authGetPhoneValidCodeParam.getPhone();
        // 验证码
        String validCode = authGetPhoneValidCodeParam.getValidCode();
        // 验证码请求号
        String validCodeReqNo = authGetPhoneValidCodeParam.getValidCodeReqNo();
        // 校验参数
        validValidCode(null, validCode, validCodeReqNo);
        // 生成手机验证码的值，随机6为数字
        String phoneValidCode = RandomUtil.randomNumbers(6);
        // 生成手机验证码的请求号
        String phoneValidCodeReqNo = IdWorker.getIdStr();
        // 登录验证码短信消息模板编码
        String smsTemplateCode;
        if(SaClientTypeEnum.B.getValue().equals(type)) {
            smsTemplateCode = devConfigApi.getValueByKey(SNOWY_SMS_TEMPLATE_VALID_CODE_LOGIN_FOR_B_KEY);
        } else {
            smsTemplateCode = devConfigApi.getValueByKey(SNOWY_SMS_TEMPLATE_VALID_CODE_LOGIN_FOR_C_KEY);
        }
        if(ObjectUtil.isEmpty(smsTemplateCode)){
            throw new CommonException("请联系管理员配置{}端登录验证码短信消息模板编码", type);
        }
        // 获取验证码失效时间（单位：秒）
        long validCodeExpiredDuration = this.getValidCodeExpiredDuration(type);
        // 模板内容转为JSONObject
        JSONObject contentJSONObject = JSONUtil.parseObj(smsTemplateCode);
        // 定义变量参数
        JSONObject paramMap = JSONUtil.createObj().set("userPhone", phone).set("validCode", phoneValidCode).set("validTime", validCodeExpiredDuration/60);
        // 获取编码
        String codeValue = contentJSONObject.getStr("code");
        // 发送短信
        devSmsApi.sendDynamicSms(phone, codeValue, paramMap);
        // 将请求号作为key，验证码的值作为value放到redis，用于校验
        commonCacheOperator.put(AUTH_VALID_CODE_CACHE_KEY + phone + StrUtil.UNDERLINE + phoneValidCodeReqNo, phoneValidCode, validCodeExpiredDuration);
        // 返回请求号
        return phoneValidCodeReqNo;
    }

    /**
     * 校验是否允许手机号登录
     *
     * @author xuyuxiang
     * @date 2022/8/25 15:16
     **/
    private void checkAllowPhoneLoginFlag(String type) {
        // 是否允许手机号登录
        String allowPhoneLoginFlag;
        if(SaClientTypeEnum.B.getValue().equals(type)) {
            allowPhoneLoginFlag = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_ALLOW_PHONE_LOGIN_FLAG_FOR_B_KEY);
        } else {
            allowPhoneLoginFlag = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_ALLOW_PHONE_LOGIN_FLAG_FOR_C_KEY);
        }
        if(ObjectUtil.isNotEmpty(allowPhoneLoginFlag)) {
            if(!Convert.toBool(allowPhoneLoginFlag)) {
                throw new CommonException("管理员未开启手机号登录");
            }
        }
    }

    @Override
    public String getEmailValidCode(AuthGetEmailValidCodeParam authGetEmailValidCodeParam, String type) {
        // 校验是否允许邮箱登录
        this.checkAllowEmailLoginFlag(type);
        // 邮箱 */
        String email = authGetEmailValidCodeParam.getEmail();
        // 验证码
        String validCode = authGetEmailValidCodeParam.getValidCode();
        // 验证码请求号
        String validCodeReqNo = authGetEmailValidCodeParam.getValidCodeReqNo();
        // 校验参数
        validValidCode(null, validCode, validCodeReqNo);
        // 生成邮箱验证码的值，随机6为数字
        String emailValidCode = RandomUtil.randomNumbers(6);
        // 生成邮箱验证码的请求号
        String emailValidCodeReqNo = IdWorker.getIdStr();
        // 登录验证码邮件消息模板内容
        String emailTemplateContent;
        if(SaClientTypeEnum.B.getValue().equals(type)) {
            emailTemplateContent = devConfigApi.getValueByKey(SNOWY_EMAIL_TEMPLATE_VALID_CODE_LOGIN_FOR_B_KEY);
        } else {
            emailTemplateContent = devConfigApi.getValueByKey(SNOWY_EMAIL_TEMPLATE_VALID_CODE_LOGIN_FOR_C_KEY);
        }
        if(ObjectUtil.isEmpty(emailTemplateContent)){
            throw new CommonException("请联系管理员配置{}端登录验证码邮件消息模板内容", type);
        }
        // 获取验证码失效时间（单位：秒）
        long validCodeExpiredDuration = this.getValidCodeExpiredDuration(type);
        // 模板内容转为JSONObject
        JSONObject contentJSONObject = JSONUtil.parseObj(emailTemplateContent);
        // 定义变量参数
        JSONObject paramMap = JSONUtil.createObj().set("userEmail", email).set("validCode", emailValidCode).set("validTime", validCodeExpiredDuration/60);
        // 获取格式化后的主题
        String subject = AuthEmailFormatUtil.format(contentJSONObject.getStr("subject"), paramMap);;
        // 获取格式化后的内容
        String content = AuthEmailFormatUtil.format(contentJSONObject.getStr("content"), paramMap);;
        // 发送邮件
        devEmailApi.sendDynamicHtmlEmail(email, subject, content);
        // 将请求号作为key，验证码的值作为value放到redis，用于校验
        commonCacheOperator.put(AUTH_VALID_CODE_CACHE_KEY + email + StrUtil.UNDERLINE + emailValidCodeReqNo, emailValidCode, validCodeExpiredDuration);
        // 返回请求号
        return emailValidCodeReqNo;
    }

    /**
     * 校验是否允许邮箱登录
     *
     * @author xuyuxiang
     * @date 2022/8/25 15:16
     **/
    private void checkAllowEmailLoginFlag(String type) {
        // 是否允许邮箱登录
        String allowEmailLoginFlag;
        if(SaClientTypeEnum.B.getValue().equals(type)) {
            allowEmailLoginFlag = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_ALLOW_EMAIL_LOGIN_FLAG_FOR_B_KEY);
        } else {
            allowEmailLoginFlag = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_ALLOW_EMAIL_LOGIN_FLAG_FOR_C_KEY);
        }
        if(ObjectUtil.isNotEmpty(allowEmailLoginFlag)) {
            if(!Convert.toBool(allowEmailLoginFlag)) {
                throw new CommonException("管理员未开启邮箱登录");
            }
        }
    }

    /**
     * 校验验证码方法
     *
     * @author xuyuxiang
     * @date 2022/8/25 15:26
     **/
    private void validValidCode(String phoneOrEmail, String validCode, String validCodeReqNo) {
        // 依据请求号，取出缓存中的验证码进行校验
        Object existValidCode;
        if(ObjectUtil.isEmpty(phoneOrEmail)) {
            // 图形验证码
            existValidCode = commonCacheOperator.get(AUTH_VALID_CODE_CACHE_KEY + validCodeReqNo);
        } else {
            // 手机或者邮箱验证码
            existValidCode = commonCacheOperator.get(AUTH_VALID_CODE_CACHE_KEY + phoneOrEmail + StrUtil.UNDERLINE + validCodeReqNo);
        }
        // 缓存中不存在验证码则返回失效错误
        if (ObjectUtil.isEmpty(existValidCode)){
            throw new CommonException(AuthExceptionEnum.VALID_CODE_EXPIRED.getValue());
        }
        // 不一致则直接验证码错误
        if (!validCode.equalsIgnoreCase(Convert.toStr(existValidCode))) {
            throw new CommonException(AuthExceptionEnum.VALID_CODE_ERROR.getValue());
        }
        // 验证成功，移除该验证码
        if(ObjectUtil.isEmpty(phoneOrEmail)) {
            // 图形验证码
            commonCacheOperator.remove(AUTH_VALID_CODE_CACHE_KEY + validCodeReqNo);
        } else {
            // 手机或者邮箱验证码
            commonCacheOperator.remove(AUTH_VALID_CODE_CACHE_KEY + phoneOrEmail + StrUtil.UNDERLINE + validCodeReqNo);
        }
    }

    /**
     * 校验手机号与验证码等参数
     *
     * @author xuyuxiang
     * @date 2022/8/25 14:29
     **/
    private String validPhoneOrEmailValidCodeParam(String phoneOrEmail, String phoneOrEmailType,
                                                   String validCode, String validCodeReqNo, String type) {
        // 校验手机号或邮箱类型
        AuthPhoneOrEmailTypeEnum.validate(phoneOrEmailType);
        // 校验手机号或邮箱格式
        if(phoneOrEmailType.equals(AuthPhoneOrEmailTypeEnum.PHONE.getValue())) {
            if(!PhoneUtil.isMobile(phoneOrEmail)) {
                throw new CommonException(AuthExceptionEnum.PHONE_FORMAT_ERROR.getValue());
            }
        } else {
            if(CommonEmailUtil.isNotEmail(phoneOrEmail)) {
                throw new CommonException(AuthExceptionEnum.PHONE_FORMAT_ERROR.getValue());
            }
        }
        // 先校验验证码
        validValidCode(phoneOrEmail, validCode, validCodeReqNo);
        // 根据手机号或者邮箱获取用户信息，判断用户是否存在，根据B端或C端判断
        if(SaClientTypeEnum.B.getValue().equals(type)) {
            if(phoneOrEmailType.equals(AuthPhoneOrEmailTypeEnum.PHONE.getValue())) {
                SaBaseLoginUser saBaseLoginUser = loginUserApi.getUserByPhone(phoneOrEmail);
                if(ObjectUtil.isEmpty(saBaseLoginUser)) {
                    // B端手机号无对应用户时策略
                    return devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_STRATEGY_WHEN_NO_USER_WITH_PHONE_FOR_B_KEY);
                } else {
                    // 存在则允许登录
                    return AuthStrategyWhenNoUserWithPhoneOrEmailEnum.ALLOW_LOGIN.getValue();
                }
            } else {
                SaBaseLoginUser saBaseLoginUser = loginUserApi.getUserByEmail(phoneOrEmail);
                if(ObjectUtil.isEmpty(saBaseLoginUser)) {
                    // B端邮箱无对应用户时策略
                    return devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_STRATEGY_WHEN_NO_USER_WITH_EMAIL_FOR_B_KEY);
                } else {
                    // 存在则允许登录
                    return AuthStrategyWhenNoUserWithPhoneOrEmailEnum.ALLOW_LOGIN.getValue();
                }
            }
        } else {
            if(phoneOrEmailType.equals(AuthPhoneOrEmailTypeEnum.PHONE.getValue())) {
                SaBaseClientLoginUser saBaseClientLoginUser = clientLoginUserApi.getClientUserByPhone(phoneOrEmail);
                if(ObjectUtil.isEmpty(saBaseClientLoginUser)) {
                    // C端手机号无对应用户时策略
                    return devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_STRATEGY_WHEN_NO_USER_WITH_PHONE_FOR_C_KEY);
                } else {
                    // 存在则允许登录
                    return AuthStrategyWhenNoUserWithPhoneOrEmailEnum.ALLOW_LOGIN.getValue();
                }
            } else {
                SaBaseClientLoginUser saBaseClientLoginUser = clientLoginUserApi.getClientUserByEmail(phoneOrEmail);
                if(ObjectUtil.isEmpty(saBaseClientLoginUser)) {
                    // C端邮箱无对应用户时策略
                    return devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_STRATEGY_WHEN_NO_USER_WITH_EMAIL_FOR_C_KEY);
                } else {
                    // 存在则允许登录
                    return AuthStrategyWhenNoUserWithPhoneOrEmailEnum.ALLOW_LOGIN.getValue();
                }
            }
        }
    }

    @Override
    public String doLogin(AuthAccountPasswordLoginParam authAccountPasswordLoginParam, String type) {
        // 判断账号是否被封禁
        isDisableTime(authAccountPasswordLoginParam.getAccount());
        // 获取账号
        String account = authAccountPasswordLoginParam.getAccount();
        // 获取密码
        String password = authAccountPasswordLoginParam.getPassword();
        // 获取设备
        String device = authAccountPasswordLoginParam.getDevice();
        // 默认指定为PC，如在小程序跟移动端的情况下，自行指定即可
        if(ObjectUtil.isEmpty(device)) {
            device = AuthDeviceTypeEnum.PC.getValue();
        } else {
            AuthDeviceTypeEnum.validate(device);
        }
        // 校验验证码
        String defaultCaptchaOpen;
        if(SaClientTypeEnum.B.getValue().equals(type)) {
            defaultCaptchaOpen = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_CAPTCHA_OPEN_FLAG_FOR_B_KEY);
        } else {
            defaultCaptchaOpen = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_CAPTCHA_OPEN_FLAG_FOR_C_KEY);
        }
        if(ObjectUtil.isNotEmpty(defaultCaptchaOpen)) {
            if(Convert.toBool(defaultCaptchaOpen)) {
                // 获取验证码
                String validCode = authAccountPasswordLoginParam.getValidCode();
                // 获取验证码请求号
                String validCodeReqNo = authAccountPasswordLoginParam.getValidCodeReqNo();
                // 开启验证码则必须传入验证码
                if(ObjectUtil.isEmpty(validCode)) {
                    throw new CommonException(AuthExceptionEnum.VALID_CODE_EMPTY.getValue());
                }
                // 开启验证码则必须传入验证码请求号
                if(ObjectUtil.isEmpty(validCodeReqNo)) {
                    throw new CommonException(AuthExceptionEnum.VALID_CODE_REQ_NO_EMPTY.getValue());
                }
                // 执行校验图形验证码
                validValidCode(null, validCode, validCodeReqNo);
            }
        }
        // SM2解密并获得前端传来的密码哈希值
        String passwordHash;
        try {
            // 解密，并做哈希值
            passwordHash = CommonCryptogramUtil.doHashValue(CommonCryptogramUtil.doSm2Decrypt(password));
        } catch (Exception e) {
            throw new CommonException(AuthExceptionEnum.PWD_DECRYPT_ERROR.getValue());
        }
        // 根据账号获取用户信息，根据B端或C端判断
        if(SaClientTypeEnum.B.getValue().equals(type)) {
            SaBaseLoginUser saBaseLoginUser = loginUserApi.getUserByAccount(account);
            if(ObjectUtil.isEmpty(saBaseLoginUser)) {
                // 提示账号错误
                throw new CommonException(AuthExceptionEnum.ACCOUNT_ERROR.getValue());
            }
            if (!saBaseLoginUser.getPassword().equals(passwordHash)) {
                // 密码错误，处理剩余次数提示信息
                handleRemainingTimes(account, AuthExceptionEnum.PWD_ERROR.getValue(), type);
            }
            // 执行B端登录
            return execLoginB(saBaseLoginUser, device);
        } else {
            SaBaseClientLoginUser saBaseClientLoginUser = clientLoginUserApi.getClientUserByAccount(account);
            if(ObjectUtil.isEmpty(saBaseClientLoginUser)) {
                // 提示账号错误
                throw new CommonException(AuthExceptionEnum.ACCOUNT_ERROR.getValue());
            }
            if (!saBaseClientLoginUser.getPassword().equals(passwordHash)) {
                // 密码错误，处理剩余次数提示信息
                handleRemainingTimes(account, AuthExceptionEnum.PWD_ERROR.getValue(), type);
            }
            // 执行C端登录
            return execLoginC(saBaseClientLoginUser, device);
        }
    }

    @Override
    public String doLoginByPhone(AuthPhoneValidCodeLoginParam authPhoneValidCodeLoginParam, String type) {
        // 校验是否允许手机号登录
        this.checkAllowPhoneLoginFlag(type);
        // 手机号
        String phone = authPhoneValidCodeLoginParam.getPhone();
        // 校验参数，返回手机号无对应用户时的策略
        String strategyWhenNoUserWithPhoneOrEmail = validPhoneOrEmailValidCodeParam(phone,
                AuthPhoneOrEmailTypeEnum.PHONE.getValue(), authPhoneValidCodeLoginParam.getValidCode(),
                authPhoneValidCodeLoginParam.getValidCodeReqNo(), type);
        // 设备
        String device = authPhoneValidCodeLoginParam.getDevice();
        // 默认指定为PC，如在小程序跟移动端的情况下，自行指定即可
        if(ObjectUtil.isEmpty(device)) {
            device = AuthDeviceTypeEnum.PC.getValue();
        } else {
            AuthDeviceTypeEnum.validate(device);
        }
        // 根据手机号获取用户信息，根据B端或C端判断
        if(SaClientTypeEnum.B.getValue().equals(type)) {
            // 判断手机号无对应用户时的策略，如果为空则直接抛出异常
            if(ObjectUtil.isEmpty(strategyWhenNoUserWithPhoneOrEmail)) {
                throw new CommonException("手机号码：{}不存在对应用户", phone);
            } else {
                // 如果不允许登录，则抛出异常
                if(AuthStrategyWhenNoUserWithPhoneOrEmailEnum.NOT_ALLOW_LOGIN.getValue().equals(strategyWhenNoUserWithPhoneOrEmail)) {
                    throw new CommonException("手机号码：{}不存在对应用户", phone);
                } else {
                    // 定义B端用户
                    SaBaseLoginUser saBaseLoginUser;
                    if(AuthStrategyWhenNoUserWithPhoneOrEmailEnum.ALLOW_LOGIN.getValue().equals(strategyWhenNoUserWithPhoneOrEmail)) {
                        // 允许登录，即用户存在
                        saBaseLoginUser = loginUserApi.getUserByPhone(phone);
                    }else if(AuthStrategyWhenNoUserWithPhoneOrEmailEnum.AUTO_CREATE_USER.getValue().equals(strategyWhenNoUserWithPhoneOrEmail)) {
                        // 根据手机号自动创建B端用户
                        saBaseLoginUser = loginUserApi.createUserWithPhone(phone);
                    } else {
                        throw new CommonException("不支持的手机号或邮箱无对应用户时策略类型：{}", strategyWhenNoUserWithPhoneOrEmail);
                    }
                    // 执行B端登录
                    return execLoginB(saBaseLoginUser, device);
                }
            }
        } else {
            // 判断手机号无对应用户时的策略，如果为空则直接抛出异常
            if(ObjectUtil.isEmpty(strategyWhenNoUserWithPhoneOrEmail)) {
                throw new CommonException("手机号码：{}不存在对应用户", phone);
            } else {
                // 如果不允许登录，则抛出异常
                if(AuthStrategyWhenNoUserWithPhoneOrEmailEnum.NOT_ALLOW_LOGIN.getValue().equals(strategyWhenNoUserWithPhoneOrEmail)) {
                    throw new CommonException("手机号码：{}不存在对应用户", phone);
                } else {
                    // 定义C端用户
                    SaBaseClientLoginUser saBaseClientLoginUser;
                    if(AuthStrategyWhenNoUserWithPhoneOrEmailEnum.ALLOW_LOGIN.getValue().equals(strategyWhenNoUserWithPhoneOrEmail)) {
                        // 允许登录，即用户存在
                        saBaseClientLoginUser = clientLoginUserApi.getClientUserByPhone(phone);
                    }else if(AuthStrategyWhenNoUserWithPhoneOrEmailEnum.AUTO_CREATE_USER.getValue().equals(strategyWhenNoUserWithPhoneOrEmail)) {
                        // 根据手机号自动创建B端用户
                        saBaseClientLoginUser = clientLoginUserApi.createClientUserWithPhone(phone);
                    } else {
                        throw new CommonException("不支持的手机号或邮箱无对应用户时策略类型：{}", strategyWhenNoUserWithPhoneOrEmail);
                    }
                    // 执行C端登录
                    return execLoginC(saBaseClientLoginUser, device);
                }
            }
        }
    }

    @Override
    public String doLoginByEmail(AuthEmailValidCodeLoginParam authEmailValidCodeLoginParam, String type) {
        // 校验是否允许邮箱登录
        this.checkAllowEmailLoginFlag(type);
        // 邮箱
        String email = authEmailValidCodeLoginParam.getEmail();
        // 校验参数，返回邮箱无对应用户时的策略
        String strategyWhenNoUserWithPhoneOrEmail = validPhoneOrEmailValidCodeParam(email,
                AuthPhoneOrEmailTypeEnum.EMAIL.getValue(), authEmailValidCodeLoginParam.getValidCode(),
                authEmailValidCodeLoginParam.getValidCodeReqNo(), type);
        // 设备
        String device = authEmailValidCodeLoginParam.getDevice();
        // 默认指定为PC，如在小程序跟移动端的情况下，自行指定即可
        if(ObjectUtil.isEmpty(device)) {
            device = AuthDeviceTypeEnum.PC.getValue();
        } else {
            AuthDeviceTypeEnum.validate(device);
        }
        // 根据邮箱获取用户信息，根据B端或C端判断
        if(SaClientTypeEnum.B.getValue().equals(type)) {
            // 判断邮箱无对应用户时的策略，如果为空则直接抛出异常
            if(ObjectUtil.isEmpty(strategyWhenNoUserWithPhoneOrEmail)) {
                throw new CommonException("邮箱：{}不存在对应用户", email);
            } else {
                // 如果不允许登录，则抛出异常
                if(AuthStrategyWhenNoUserWithPhoneOrEmailEnum.NOT_ALLOW_LOGIN.getValue().equals(strategyWhenNoUserWithPhoneOrEmail)) {
                    throw new CommonException("邮箱：{}不存在对应用户", email);
                } else {
                    // 定义B端用户
                    SaBaseLoginUser saBaseLoginUser;
                    if(AuthStrategyWhenNoUserWithPhoneOrEmailEnum.ALLOW_LOGIN.getValue().equals(strategyWhenNoUserWithPhoneOrEmail)) {
                        // 允许登录，即用户存在
                        saBaseLoginUser = loginUserApi.getUserByEmail(email);
                    }else if(AuthStrategyWhenNoUserWithPhoneOrEmailEnum.AUTO_CREATE_USER.getValue().equals(strategyWhenNoUserWithPhoneOrEmail)) {
                        // 根据邮箱自动创建B端用户
                        saBaseLoginUser = loginUserApi.createUserWithEmail(email);
                    } else {
                        throw new CommonException("不支持的手机号或邮箱无对应用户时策略类型：{}", strategyWhenNoUserWithPhoneOrEmail);
                    }
                    // 执行B端登录
                    return execLoginB(saBaseLoginUser, device);
                }
            }
        } else {
            // 判断邮箱无对应用户时的策略，如果为空则直接抛出异常
            if(ObjectUtil.isEmpty(strategyWhenNoUserWithPhoneOrEmail)) {
                throw new CommonException("邮箱：{}不存在对应用户", email);
            } else {
                // 如果不允许登录，则抛出异常
                if(AuthStrategyWhenNoUserWithPhoneOrEmailEnum.NOT_ALLOW_LOGIN.getValue().equals(strategyWhenNoUserWithPhoneOrEmail)) {
                    throw new CommonException("邮箱：{}不存在对应用户", email);
                } else {
                    // 定义C端用户
                    SaBaseClientLoginUser saBaseClientLoginUser;
                    if(AuthStrategyWhenNoUserWithPhoneOrEmailEnum.ALLOW_LOGIN.getValue().equals(strategyWhenNoUserWithPhoneOrEmail)) {
                        // 允许登录，即用户存在
                        saBaseClientLoginUser = clientLoginUserApi.getClientUserByEmail(email);
                    }else if(AuthStrategyWhenNoUserWithPhoneOrEmailEnum.AUTO_CREATE_USER.getValue().equals(strategyWhenNoUserWithPhoneOrEmail)) {
                        // 根据邮箱自动创建B端用户
                        saBaseClientLoginUser = loginUserApi.createClientUserWithEmail(email);
                    } else {
                        throw new CommonException("不支持的手机号或邮箱无对应用户时策略类型：{}", strategyWhenNoUserWithPhoneOrEmail);
                    }
                    // 执行C端登录
                    return execLoginC(saBaseClientLoginUser, device);
                }
            }
        }
    }

    /**
     * 处理剩余次数提示信息
     */
    private void handleRemainingTimes(String account, String errorMessage, String type) {
        // 记录登录次数 和 过期时间
        int remainingTimes = saveLoginTimes(account, type);
        if(remainingTimes == 0) {
            // 此时已封禁，返回提示语
            isDisableTime(account);
        } else {
            // 提示错误
            throw new CommonException(errorMessage + "，您还可以尝试【" + remainingTimes + "】次");
        }
    }

    /**
     * 是否封禁状态
     * 如果被封禁了，执行以下逻辑，返回前端还需等待的时间
     */
    private void isDisableTime(String userAccount) {
        // disableTime = -2表示未被封禁
        long disableTime = StpUtil.getDisableTime(userAccount);
        if (disableTime > 0) {
            String formatTime = CommonTimeFormatUtil.formatSeconds(disableTime);
            throw new CommonException("账号" + userAccount + "已被封禁, 请在"+ formatTime+ "后重新尝试登录!");
        }
    }

    /**
     * redis中保存登录错误次数
     */
    private int saveLoginTimes(String userAccount, String type){
        // 获取连续登录失败持续时间
        String configContinuousLoginFailDuration;
        // 获取连续登录失败次数
        String configContinuousLoginFailTimes;
        // 获取连续登录失败锁定时间
        String configContinuousLoginFailLockDuration;
        if(SaClientTypeEnum.B.getValue().equals(type)) {
            configContinuousLoginFailDuration = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_CONTINUOUS_LOGIN_FAIL_DURATION_FOR_B_KEY);
            configContinuousLoginFailTimes = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_CONTINUOUS_LOGIN_FAIL_TIMES_FOR_B_KEY);
            configContinuousLoginFailLockDuration = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_CONTINUOUS_LOGIN_FAIL_LOCK_DURATION_FOR_B_KEY);
        } else {
            configContinuousLoginFailDuration = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_CONTINUOUS_LOGIN_FAIL_DURATION_FOR_C_KEY);
            configContinuousLoginFailTimes = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_CONTINUOUS_LOGIN_FAIL_TIMES_FOR_C_KEY);
            configContinuousLoginFailLockDuration = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_CONTINUOUS_LOGIN_FAIL_LOCK_DURATION_FOR_C_KEY);
        }
        // 连续登录失败持续时间默认5分钟
        long continuousLoginFailDuration = 5 * 60;
        if(ObjectUtil.isNotEmpty(configContinuousLoginFailDuration)){
            // 配置了则使用配置的失效时间
            continuousLoginFailDuration = Convert.toLong(configContinuousLoginFailDuration) * 60;
        }

        // 连续登录失败次数默认5次
        int continuousLoginFailTimes = 5;
        if(ObjectUtil.isNotEmpty(configContinuousLoginFailTimes)){
            // 配置了则使用配置的失效时间
            continuousLoginFailTimes = Convert.toInt(configContinuousLoginFailTimes);
        }

        // 连续登录失败锁定时间默认5分钟
        long continuousLoginFailLockDuration = 5 * 60;
        if(ObjectUtil.isNotEmpty(configContinuousLoginFailLockDuration)){
            // 配置了则使用配置的失效时间
            continuousLoginFailLockDuration = Convert.toLong(configContinuousLoginFailLockDuration) * 60;
        }
        // 获取登录失败次数缓存键
        String loginErrorKey = LOGIN_ERROR_TIMES_KEY_PREFIX  + ":" + userAccount;
        // 获取登录失败次数缓存值
        Integer number = (Integer) commonCacheOperator.get(loginErrorKey);
        if (number == null) {
            // 如果redis中没有保存，代表失败第一次，如果配置的值为1次
            if(continuousLoginFailTimes  == 1) {
                // 直接进入isDisableTime方法，返回用户还需等待时间
                StpUtil.disable(userAccount, continuousLoginFailLockDuration);
                // 删除redis 中的key
                clearLoginErrorTimes(userAccount);
                return 0;
            } else {
                // 否则失败次数为2
                number = 2;
                commonCacheOperator.put(loginErrorKey, number, continuousLoginFailDuration);
                return continuousLoginFailTimes - number + 1;
            }
        } else {
            if (number < continuousLoginFailTimes) {
                number++;
                commonCacheOperator.put(loginErrorKey, number, continuousLoginFailDuration);
                return continuousLoginFailTimes - number + 1;
            } else {
                // 第N次封禁账号，第N+1次进入isDisableTime方法，返回用户还需等待时间
                StpUtil.disable(userAccount, continuousLoginFailLockDuration);
                // 删除redis 中的key
                clearLoginErrorTimes(userAccount);
                return 0;
            }
        }
    }

    /**
     * 登录成功、清空登录次数
     * @param userAccount 账号
     */
    private void clearLoginErrorTimes(String userAccount) {
        // 获取登录失败次数缓存键
        String loginErrorKey = LOGIN_ERROR_TIMES_KEY_PREFIX  + ":" + userAccount;
        // 删除redis中的key
        commonCacheOperator.remove(loginErrorKey);
    }

    /**
     * 执行B端登录
     *
     * @author xuyuxiang
     * @date 2022/8/25 14:36
     **/
    private String execLoginB(SaBaseLoginUser saBaseLoginUser, String device) {
        // 校验状态
        if(!saBaseLoginUser.getEnabled()) {
            throw new CommonException(AuthExceptionEnum.ACCOUNT_DISABLED.getValue());
        }
        // 执行登录
        StpUtil.login(saBaseLoginUser.getId(), new SaLoginModel().setDevice(device).setExtra("name", saBaseLoginUser.getName()));
        // 填充B端用户信息并更新缓存
        fillSaBaseLoginUserAndUpdateCache(saBaseLoginUser);
        // 返回token
        return StpUtil.getTokenInfo().tokenValue;
    }

    /**
     * 填充B端用户信息并更新缓存
     *
     * @author xuyuxiang
     * @date 2024/7/22 22:00
     */
    private void fillSaBaseLoginUserAndUpdateCache(SaBaseLoginUser saBaseLoginUser) {
        // 角色集合
        List<JSONObject> roleList = loginUserApi.getRoleListByUserId(saBaseLoginUser.getId());
        // 角色id集合
        List<String> roleIdList = roleList.stream().map(jsonObject -> jsonObject.getStr("id")).collect(Collectors.toList());
        // 角色码集合
        List<String> roleCodeList = roleList.stream().map(jsonObject -> jsonObject.getStr("code")).collect(Collectors.toList());
        // 角色id和用户id集合
        List<String> userAndRoleIdList = CollectionUtil.unionAll(roleIdList, CollectionUtil.newArrayList(saBaseLoginUser.getId()));
        // 获取按钮码
        saBaseLoginUser.setButtonCodeList(loginUserApi.getButtonCodeListListByUserAndRoleIdList(userAndRoleIdList));
        // 获取移动端按钮码
        saBaseLoginUser.setMobileButtonCodeList(loginUserApi.getMobileButtonCodeListListByUserIdAndRoleIdList(userAndRoleIdList));
        // 获取数据范围
        saBaseLoginUser.setDataScopeList(Convert.toList(SaBaseLoginUser.DataScope.class,
                loginUserApi.getPermissionListByUserIdAndRoleIdList(userAndRoleIdList, saBaseLoginUser.getOrgId())));
        // 获取权限码
        List<String> permissionCodeList = saBaseLoginUser.getDataScopeList().stream()
                .map(SaBaseLoginUser.DataScope::getApiUrl).collect(Collectors.toList());
        // 设置权限码
        saBaseLoginUser.setPermissionCodeList(permissionCodeList);
        // 权限码列表存入缓存
        commonCacheOperator.put(CacheConstant.AUTH_B_PERMISSION_LIST_CACHE_KEY + saBaseLoginUser.getId(),permissionCodeList);
        // 获取角色码
        saBaseLoginUser.setRoleCodeList(roleCodeList);
        // 缓存用户信息，此处使用TokenSession为了指定时间内无操作则自动下线
        StpUtil.getTokenSession().set("loginUser", saBaseLoginUser);
    }

    /**
     * 执行C端登录
     *
     * @author xuyuxiang
     * @date 2022/8/25 14:37
     **/
    private String execLoginC(SaBaseClientLoginUser saBaseClientLoginUser, String device) {
        // 校验状态
        if(!saBaseClientLoginUser.getEnabled()) {
            throw new CommonException(AuthExceptionEnum.ACCOUNT_DISABLED.getValue());
        }
        // 执行登录
        StpClientUtil.login(saBaseClientLoginUser.getId(), new SaLoginModel().setDevice(device).setExtra("name", saBaseClientLoginUser.getName()));
        // 填充C端用户信息并更新缓存
        fillSaBaseClientLoginUserAndUpdateCache(saBaseClientLoginUser);
        // 返回token
        return StpClientUtil.getTokenInfo().tokenValue;
    }

    /**
     * 填充C端用户信息
     *
     * @author xuyuxiang
     * @date 2024/7/22 22:00
     */
    private void fillSaBaseClientLoginUserAndUpdateCache(SaBaseClientLoginUser saBaseClientLoginUser) {
        // 角色集合
        List<JSONObject> roleList = clientLoginUserApi.getRoleListByUserId(saBaseClientLoginUser.getId());
        // 角色id集合
        List<String> roleIdList = roleList.stream().map(jsonObject -> jsonObject.getStr("id")).collect(Collectors.toList());
        // 角色码集合
        List<String> roleCodeList = roleList.stream().map(jsonObject -> jsonObject.getStr("code")).collect(Collectors.toList());
        // 角色id和用户id集合
        List<String> userAndRoleIdList = CollectionUtil.unionAll(roleIdList, CollectionUtil.newArrayList(saBaseClientLoginUser.getId()));
        // 获取按钮码
        saBaseClientLoginUser.setButtonCodeList(clientLoginUserApi.getButtonCodeListListByUserAndRoleIdList(userAndRoleIdList));
        // 获取移动端按钮码
        saBaseClientLoginUser.setMobileButtonCodeList(clientLoginUserApi.getMobileButtonCodeListListByUserIdAndRoleIdList(userAndRoleIdList));
        // 获取数据范围
        saBaseClientLoginUser.setDataScopeList(Convert.toList(SaBaseClientLoginUser.DataScope.class,
                clientLoginUserApi.getPermissionListByUserIdAndRoleIdList(userAndRoleIdList, null)));
        // 获取权限码
        List<String> permissionCodeList = saBaseClientLoginUser.getDataScopeList().stream()
                .map(SaBaseClientLoginUser.DataScope::getApiUrl).collect(Collectors.toList());
        // 设置权限码
        saBaseClientLoginUser.setPermissionCodeList(permissionCodeList);
        // 权限码列表存入缓存
        commonCacheOperator.put(CacheConstant.AUTH_C_PERMISSION_LIST_CACHE_KEY + saBaseClientLoginUser.getId(),permissionCodeList);
        // 获取角色码
        saBaseClientLoginUser.setRoleCodeList(roleCodeList);
        // 缓存用户信息，此处使用TokenSession为了指定时间内无操作则自动下线
        StpClientUtil.getTokenSession().set("loginUser", saBaseClientLoginUser);
    }

    /**
     * 获取B端登录用户信息
     *
     * @author xuyuxiang
     * @date 2021/10/12 15:59
     **/
    @Override
    public SaBaseLoginUser getLoginUser() {
        // 获取当前缓存的用户信息
        SaBaseLoginUser saBaseLoginUser = StpLoginUserUtil.getLoginUser();
        // 获取B端用户信息
        saBaseLoginUser = loginUserApi.getUserById(saBaseLoginUser.getId());
        // 填充B端用户信息并更新缓存
        fillSaBaseLoginUserAndUpdateCache(saBaseLoginUser);
        // 去掉密码
        saBaseLoginUser.setPassword("******");
        // 去掉权限码
        saBaseLoginUser.setPermissionCodeList(CollectionUtil.newArrayList());
        // 去掉数据范围
        saBaseLoginUser.setDataScopeList(CollectionUtil.newArrayList());
        // 返回
        return saBaseLoginUser;
    }

    /**
     * 获取C端登录用户信息
     *
     * @author xuyuxiang
     * @date 2021/10/12 15:59
     **/
    @Override
    public SaBaseClientLoginUser getClientLoginUser() {
        // 获取当前缓存的用户信息
        SaBaseClientLoginUser saBaseClientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
        // 获取C端用户信息
        saBaseClientLoginUser = clientLoginUserApi.getClientUserById(saBaseClientLoginUser.getId());
        // 填充C端用户信息并更新缓存
        fillSaBaseClientLoginUserAndUpdateCache(saBaseClientLoginUser);
        // 去掉密码
        saBaseClientLoginUser.setPassword("******");
        // 去掉权限码
        saBaseClientLoginUser.setPermissionCodeList(CollectionUtil.newArrayList());
        // 去掉数据范围
        saBaseClientLoginUser.setDataScopeList(CollectionUtil.newArrayList());
        // 返回
        return saBaseClientLoginUser;
    }

    @Override
    public String doLoginById(String userId, String device, String type) {
        // 根据id获取用户信息，根据B端或C端判断
        if(SaClientTypeEnum.B.getValue().equals(type)) {
            SaBaseLoginUser saBaseLoginUser = loginUserApi.getUserById(userId);
            if (ObjectUtil.isEmpty(saBaseLoginUser)) {
                throw new CommonException(AuthExceptionEnum.ACCOUNT_ERROR.getValue());
            }
            // 执行B端登录
            return execLoginB(saBaseLoginUser, device);
        } else {
            SaBaseClientLoginUser saBaseClientLoginUser = clientLoginUserApi.getClientUserById(userId);
            if (ObjectUtil.isEmpty(saBaseClientLoginUser)) {
                throw new CommonException(AuthExceptionEnum.ACCOUNT_ERROR.getValue());
            }
            // 执行C端登录
            return execLoginC(saBaseClientLoginUser, device);
        }
    }

    @Override
    public void register(AuthRegisterParam authRegisterParam, String type) {
        // 校验是否允许注册
        this.checkAllowRegisterFlag(type);
        // 获取账号
        String account = authRegisterParam.getAccount();
        // 获取密码
        String password = authRegisterParam.getPassword();
        // 校验验证码
        String defaultCaptchaOpen;
        if(SaClientTypeEnum.B.getValue().equals(type)) {
            defaultCaptchaOpen = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_CAPTCHA_OPEN_FLAG_FOR_B_KEY);
        } else {
            defaultCaptchaOpen = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_CAPTCHA_OPEN_FLAG_FOR_C_KEY);
        }
        if(ObjectUtil.isNotEmpty(defaultCaptchaOpen)) {
            if(Convert.toBool(defaultCaptchaOpen)) {
                // 获取验证码
                String validCode = authRegisterParam.getValidCode();
                // 获取验证码请求号
                String validCodeReqNo = authRegisterParam.getValidCodeReqNo();
                // 开启验证码则必须传入验证码
                if(ObjectUtil.isEmpty(validCode)) {
                    throw new CommonException(AuthExceptionEnum.VALID_CODE_EMPTY.getValue());
                }
                // 开启验证码则必须传入验证码请求号
                if(ObjectUtil.isEmpty(validCodeReqNo)) {
                    throw new CommonException(AuthExceptionEnum.VALID_CODE_REQ_NO_EMPTY.getValue());
                }
                // 执行校验图形验证码
                validValidCode(null, validCode, validCodeReqNo);
            }
        }
        // SM2解密前端传来的密码
        String passwordDecrypt = CommonCryptogramUtil.doSm2Decrypt(password);
        // 根据账号获取用户信息，根据B端或C端判断
        if(SaClientTypeEnum.B.getValue().equals(type)) {
            loginUserApi.doRegister(account, passwordDecrypt);
        } else {
            clientLoginUserApi.doRegister(account, passwordDecrypt);
        }
    }

    /**
     * 校验是否开启注册
     *
     * @author xuyuxiang
     * @date 2022/8/25 15:16
     **/
    private void checkAllowRegisterFlag(String type) {
        // 是否允许注册
        String allowRegisterFlag;
        if(SaClientTypeEnum.B.getValue().equals(type)) {
            allowRegisterFlag = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_ALLOW_REGISTER_FLAG_FOR_B_KEY);
        } else {
            allowRegisterFlag = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_ALLOW_REGISTER_FLAG_FOR_C_KEY);
        }
        if(ObjectUtil.isNotEmpty(allowRegisterFlag)) {
            if(!Convert.toBool(allowRegisterFlag)) {
                throw new CommonException("管理员未开启注册");
            }
        }
    }

    /**
     * 获取验证码失效时间（单位：秒）
     *
     * @author xuyuxiang
     * @date 2025/3/21 20:25
     **/
    private long getValidCodeExpiredDuration(String type) {
        // 默认5分钟
        int defaultExpiredTime = 5;
        // 获取配置验证码失效时间（单位：分钟）
        String configCaptchaExpiredDuration;
        if(SaClientTypeEnum.B.getValue().equals(type)) {
            configCaptchaExpiredDuration = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_CAPTCHA_EXPIRED_DURATION_FOR_B_KEY);
        } else {
            configCaptchaExpiredDuration = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_CAPTCHA_EXPIRED_DURATION_FOR_C_KEY);
        }
        // 判断是否为空
        if(ObjectUtil.isNotEmpty(configCaptchaExpiredDuration)){
            // 配置了则使用配置的失效时间
            defaultExpiredTime = Convert.toInt(configCaptchaExpiredDuration);
        }
        // 转为秒
        return defaultExpiredTime * 60L;
    }
}
