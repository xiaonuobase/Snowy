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
package vip.xiaonuo.client.modular.user.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fhs.trans.service.impl.TransService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.auth.core.util.StpClientUtil;
import vip.xiaonuo.auth.core.util.StpLoginUserUtil;
import vip.xiaonuo.client.core.util.ClientEmailFormatUtl;
import vip.xiaonuo.client.core.util.ClientPasswordUtl;
import vip.xiaonuo.client.modular.user.entity.ClientUser;
import vip.xiaonuo.client.modular.user.entity.ClientUserExt;
import vip.xiaonuo.client.modular.user.enums.ClientUpdatePasswordValidTypeEnum;
import vip.xiaonuo.client.modular.user.enums.ClientUserSourceFromTypeEnum;
import vip.xiaonuo.client.modular.user.enums.ClientUserStatusEnum;
import vip.xiaonuo.client.modular.user.mapper.ClientUserMapper;
import vip.xiaonuo.client.modular.user.param.*;
import vip.xiaonuo.client.modular.user.result.ClientLoginUser;
import vip.xiaonuo.client.modular.user.result.ClientUserPicValidCodeResult;
import vip.xiaonuo.client.modular.user.service.ClientUserExtService;
import vip.xiaonuo.client.modular.user.service.ClientUserPasswordService;
import vip.xiaonuo.client.modular.user.service.ClientUserService;
import vip.xiaonuo.common.cache.CommonCacheOperator;
import vip.xiaonuo.common.enums.CommonGenderEnum;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;
import vip.xiaonuo.common.util.*;
import vip.xiaonuo.dev.api.DevConfigApi;
import vip.xiaonuo.dev.api.DevEmailApi;
import vip.xiaonuo.dev.api.DevSmsApi;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * C端用户Service接口实现类
 *
 * @author xuyuxiang
 * @date 2022/2/23 18:43
 **/
@Service
public class ClientUserServiceImpl extends ServiceImpl<ClientUserMapper, ClientUser> implements ClientUserService {

    /** C端验证码失效时间（适用图片验证码和短信验证码，单位：分钟，默认5分钟有效） */
    private static final String SNOWY_SYS_DEFAULT_CAPTCHA_EXPIRED_DURATION_FOR_C_KEY = "SNOWY_SYS_DEFAULT_CAPTCHA_EXPIRED_DURATION_FOR_C";

    /** C端重置密码验证码短信消息模板 */
    private static final String SNOWY_SMS_TEMPLATE_VALID_CODE_RESET_PASSWORD_FOR_C_KEY = "SNOWY_SMS_TEMPLATE_VALID_CODE_RESET_PASSWORD_FOR_C";

    /** C端重置密码验证码邮件消息模板 */
    private static final String SNOWY_EMAIL_TEMPLATE_VALID_CODE_RESET_PASSWORD_FOR_C_KEY = "SNOWY_EMAIL_TEMPLATE_VALID_CODE_RESET_PASSWORD_FOR_C";

    /** C端修改密码验证码短信消息模板 */
    private static final String SNOWY_SMS_TEMPLATE_VALID_CODE_UPDATE_PASSWORD_FOR_C_KEY = "SNOWY_SMS_TEMPLATE_VALID_CODE_UPDATE_PASSWORD_FOR_C";

    /** C端修改密码验证码邮件消息模板 */
    private static final String SNOWY_EMAIL_TEMPLATE_VALID_CODE_UPDATE_PASSWORD_FOR_C_KEY = "SNOWY_EMAIL_TEMPLATE_VALID_CODE_UPDATE_PASSWORD_FOR_C";

    /** C端重置密码成功短信消息模板 */
    private static final String SNOWY_SMS_TEMPLATE_NOTICE_PASSWORD_RESET_SUCCESS_FOR_C_KEY = "SNOWY_SMS_TEMPLATE_NOTICE_PASSWORD_RESET_SUCCESS_FOR_C";

    /** C端重置密码成功邮件消息模板 */
    private static final String SNOWY_EMAIL_TEMPLATE_NOTICE_PASSWORD_RESET_SUCCESS_FOR_C_KEY = "SNOWY_EMAIL_TEMPLATE_NOTICE_PASSWORD_RESET_SUCCESS_FOR_C";

    /** C端密码即将到期短信消息模板 */
    private static final String SNOWY_SMS_TEMPLATE_NOTICE_PASSWORD_EXPIRED_FOR_C_KEY = "SNOWY_SMS_TEMPLATE_NOTICE_PASSWORD_EXPIRED_FOR_C";

    /** C端密码即将到期邮件消息模板 */
    private static final String SNOWY_EMAIL_TEMPLATE_NOTICE_PASSWORD_EXPIRED_FOR_C_KEY = "SNOWY_EMAIL_TEMPLATE_NOTICE_PASSWORD_EXPIRED_FOR_C";

    /** C端绑定手机验证码短信消息模板 */
    private static final String SNOWY_SMS_TEMPLATE_VALID_CODE_BINDING_PHONE_FOR_C_KEY = "SNOWY_SMS_TEMPLATE_VALID_CODE_BINDING_PHONE_FOR_C";

    /** C端绑定邮箱验证码邮件消息模板 */
    private static final String SNOWY_EMAIL_TEMPLATE_VALID_CODE_BINDING_EMAIL_FOR_C_KEY = "SNOWY_EMAIL_TEMPLATE_VALID_CODE_BINDING_EMAIL_FOR_C";

    /** C端修改绑定手机验证码短信消息模板 */
    private static final String SNOWY_SMS_TEMPLATE_VALID_CODE_UPDATE_BINDING_PHONE_FOR_C_KEY = "SNOWY_SMS_TEMPLATE_VALID_CODE_UPDATE_BINDING_PHONE_FOR_C";

    /** C端修改绑定邮箱验证码邮件消息模板 */
    private static final String SNOWY_EMAIL_TEMPLATE_VALID_CODE_UPDATE_BINDING_EMAIL_FOR_C_KEY = "SNOWY_EMAIL_TEMPLATE_VALID_CODE_UPDATE_BINDING_EMAIL_FOR_C";

    /** C端注册账号成功短信消息模板 */
    private static final String SNOWY_SMS_TEMPLATE_NOTICE_REGISTER_SUCCESS_FOR_C_KEY = "SNOWY_SMS_TEMPLATE_NOTICE_REGISTER_SUCCESS_FOR_C";

    /** C端注册账号成功邮件消息模板 */
    private static final String SNOWY_EMAIL_TEMPLATE_NOTICE_REGISTER_SUCCESS_FOR_C_KEY = "SNOWY_EMAIL_TEMPLATE_NOTICE_REGISTER_SUCCESS_FOR_C";

    /** C端注册后是否需要绑定手机号 */
    private static final String SNOWY_SYS_DEFAULT_REGISTER_NEED_BIND_PHONE_FOR_C_KEY = "SNOWY_SYS_DEFAULT_REGISTER_NEED_BIND_PHONE_FOR_C";

    /** C端注册后是否需要绑定邮箱 */
    private static final String SNOWY_SYS_DEFAULT_REGISTER_NEED_BIND_EMAIL_FOR_C_KEY = "SNOWY_SYS_DEFAULT_REGISTER_NEED_BIND_EMAIL_FOR_C";

    /** 验证码缓存前缀 */
    private static final String USER_VALID_CODE_CACHE_KEY = "user-validCode:";

    @Resource
    private CommonCacheOperator commonCacheOperator;

    @Resource
    private DevSmsApi devSmsApi;

    @Resource
    private TransService transService;

    @Resource
    private DevEmailApi devEmailApi;

    @Resource
    private DevConfigApi devConfigApi;

    @Resource
    private ClientUserExtService clientUserExtService;

    @Resource
    private ClientUserPasswordService clientUserPasswordService;

    @Override
    public ClientLoginUser getUserById(String id) {
        ClientUser clientUser = this.getById(id);
        if(ObjectUtil.isNotEmpty(clientUser)) {
            transService.transOne(clientUser);
            return BeanUtil.copyProperties(clientUser, ClientLoginUser.class);
        }
        return null;
    }

    @Override
    public ClientLoginUser getUserByAccount(String account) {
        ClientUser clientUser = this.getOne(new LambdaQueryWrapper<ClientUser>().eq(ClientUser::getAccount, account));
        if(ObjectUtil.isNotEmpty(clientUser)) {
            transService.transOne(clientUser);
            return BeanUtil.copyProperties(clientUser, ClientLoginUser.class);
        }
        return null;
    }

    @Override
    public ClientLoginUser getUserByPhone(String phone) {
        ClientUser clientUser = this.getOne(new LambdaQueryWrapper<ClientUser>().eq(ClientUser::getPhone, CommonCryptogramUtil.doSm4CbcEncrypt(phone)));
        if(ObjectUtil.isNotEmpty(clientUser)) {
            transService.transOne(clientUser);
            return BeanUtil.copyProperties(clientUser, ClientLoginUser.class);
        }
        return null;
    }

    @Override
    public ClientLoginUser getUserByEmail(String email) {
        ClientUser clientUser = this.getOne(new LambdaQueryWrapper<ClientUser>().eq(ClientUser::getEmail, email));
        if(ObjectUtil.isNotEmpty(clientUser)) {
            transService.transOne(clientUser);
            return BeanUtil.copyProperties(clientUser, ClientLoginUser.class);
        }
        return null;
    }

    @Override
    public Page<ClientUser> page(ClientUserPageParam clientUserPageParam) {
        QueryWrapper<ClientUser> queryWrapper = new QueryWrapper<ClientUser>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(clientUserPageParam.getSearchKey())) {
            queryWrapper.lambda().and(q -> q.like(ClientUser::getName, clientUserPageParam.getSearchKey())
                    .or().like(ClientUser::getAccount, clientUserPageParam.getSearchKey()));
        }
        if(ObjectUtil.isAllNotEmpty(clientUserPageParam.getSortField(), clientUserPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(clientUserPageParam.getSortOrder());
            queryWrapper.orderBy(true, clientUserPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(clientUserPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(ClientUser::getSortCode);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(ClientUserAddParam clientUserAddParam, String sourceFromType) {
        checkParam(clientUserAddParam);
        ClientUser clientUser = BeanUtil.toBean(clientUserAddParam, ClientUser.class);
        if(ObjectUtil.isEmpty(clientUser.getAvatar())) {
            // 设置默认头像
            clientUser.setAvatar(CommonAvatarUtil.generateImg(clientUser.getName()));
        }
        if(ObjectUtil.isEmpty(clientUser.getPassword())) {
            // 设置默认密码
            clientUser.setPassword(CommonCryptogramUtil.doHashValue(ClientPasswordUtl.getDefaultPassword()));
        } else {
            // 设置传入的密码
            clientUser.setPassword(CommonCryptogramUtil.doHashValue(clientUser.getPassword()));
        }
        // 设置状态
        clientUser.setUserStatus(ClientUserStatusEnum.ENABLE.getValue());
        // 保存用户
        this.save(clientUser);
        // 插入扩展信息
        clientUserExtService.createExtInfo(clientUser.getId(), sourceFromType);
    }

    private void checkParam(ClientUserAddParam clientUserAddParam) {
        if (this.count(new LambdaQueryWrapper<ClientUser>()
                .eq(ClientUser::getAccount, clientUserAddParam.getAccount())) > 0) {
            throw new CommonException("存在重复的账号，账号为：{}", clientUserAddParam.getAccount());
        }
        if(ObjectUtil.isNotEmpty(clientUserAddParam.getPhone())) {
            if(!PhoneUtil.isMobile(clientUserAddParam.getPhone())) {
                throw new CommonException("手机号码：{}格式错误", clientUserAddParam.getPhone());
            }
            if (this.count(new LambdaQueryWrapper<ClientUser>()
                    .eq(ClientUser::getPhone, CommonCryptogramUtil.doSm4CbcEncrypt(clientUserAddParam.getPhone()))) > 0) {
                throw new CommonException("存在重复的手机号，手机号为：{}", clientUserAddParam.getPhone());
            }
        }
        if(ObjectUtil.isNotEmpty(clientUserAddParam.getEmail())) {
            if(CommonEmailUtil.isNotEmail(clientUserAddParam.getEmail())) {
                throw new CommonException("邮箱：{}格式错误", clientUserAddParam.getEmail());
            }
            if (this.count(new LambdaQueryWrapper<ClientUser>()
                    .eq(ClientUser::getEmail, clientUserAddParam.getEmail())) > 0) {
                throw new CommonException("存在重复的邮箱，邮箱为：{}", clientUserAddParam.getEmail());
            }
        }
    }

    @Override
    public void edit(ClientUserEditParam clientUserEditParam) {
        ClientUser clientUser = this.queryEntity(clientUserEditParam.getId());
        checkParam(clientUserEditParam);
        BeanUtil.copyProperties(clientUserEditParam, clientUser);
        this.updateById(clientUser);
    }

    private void checkParam(ClientUserEditParam clientUserEditParam) {
        if (this.count(new LambdaQueryWrapper<ClientUser>()
                .eq(ClientUser::getAccount, clientUserEditParam.getAccount())
                .ne(ClientUser::getId, clientUserEditParam.getId())) > 0) {
            throw new CommonException("存在重复的账号，账号为：{}", clientUserEditParam.getAccount());
        }
        if(ObjectUtil.isNotEmpty(clientUserEditParam.getPhone())) {
            if(!PhoneUtil.isMobile(clientUserEditParam.getPhone())) {
                throw new CommonException("手机号码：{}格式错误", clientUserEditParam.getPhone());
            }
            if (this.count(new LambdaQueryWrapper<ClientUser>()
                    .eq(ClientUser::getPhone, CommonCryptogramUtil.doSm4CbcEncrypt(clientUserEditParam.getPhone()))
                    .ne(ClientUser::getId, clientUserEditParam.getId())) > 0) {
                throw new CommonException("存在重复的手机号，手机号为：{}", clientUserEditParam.getPhone());
            }
        }
        if(ObjectUtil.isNotEmpty(clientUserEditParam.getEmail())) {
            if(CommonEmailUtil.isNotEmail(clientUserEditParam.getEmail())) {
                throw new CommonException("邮箱：{}格式错误", clientUserEditParam.getEmail());
            }
            if (this.count(new LambdaQueryWrapper<ClientUser>()
                    .eq(ClientUser::getEmail, clientUserEditParam.getEmail())
                    .ne(ClientUser::getId, clientUserEditParam.getId())) > 0) {
                throw new CommonException("存在重复的邮箱，邮箱为：{}", clientUserEditParam.getEmail());
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<ClientUserIdParam> clientUserIdParamList) {
        this.removeByIds(CollStreamUtil.toList(clientUserIdParamList, ClientUserIdParam::getId));
    }

    @Override
    public ClientUser detail(ClientUserIdParam clientUserIdParam) {
        return this.queryEntity(clientUserIdParam.getId());
    }

    @Override
    public void updateUserLoginInfo(String userId, String device) {
        ClientUser clientUser = this.queryEntity(userId);
        clientUser.setLastLoginTime(clientUser.getLatestLoginTime());
        clientUser.setLastLoginIp(clientUser.getLatestLoginIp());
        clientUser.setLastLoginAddress(clientUser.getLatestLoginAddress());
        clientUser.setLastLoginDevice(clientUser.getLatestLoginDevice());
        clientUser.setLatestLoginTime(DateTime.now());
        String ip = CommonIpAddressUtil.getIp(CommonServletUtil.getRequest());
        clientUser.setLatestLoginIp(ip);
        clientUser.setLatestLoginAddress(CommonIpAddressUtil.getCityInfo(ip));
        clientUser.setLatestLoginDevice(device);
        this.updateById(clientUser);
    }

    @Override
    public void updateUserInfo(ClientUserUpdateInfoParam clientUserUpdateInfoParam) {
        String id = StpLoginUserUtil.getLoginUser().getId();
        if (!StrUtil.equals(id,clientUserUpdateInfoParam.getId())){
            throw new CommonException("被修改用户与当前登录用户不匹配");
        }
        ClientUser clientUser = this.queryEntity(clientUserUpdateInfoParam.getId());
        LambdaUpdateWrapper<ClientUser> lambdaUpdateWrapper = new LambdaUpdateWrapper<ClientUser>().eq(ClientUser::getId, clientUser.getId());
        if(ObjectUtil.isNotEmpty(clientUserUpdateInfoParam.getName())) {
            lambdaUpdateWrapper.set(ClientUser::getName, clientUserUpdateInfoParam.getName());
        }
        if(ObjectUtil.isNotEmpty(clientUserUpdateInfoParam.getNickname())) {
            lambdaUpdateWrapper.set(ClientUser::getNickname, clientUserUpdateInfoParam.getNickname());
        }
        if(ObjectUtil.isNotEmpty(clientUserUpdateInfoParam.getGender())) {
            lambdaUpdateWrapper.set(ClientUser::getGender, clientUserUpdateInfoParam.getGender());
        }
        if(ObjectUtil.isNotEmpty(clientUserUpdateInfoParam.getBirthday())) {
            lambdaUpdateWrapper.set(ClientUser::getBirthday, clientUserUpdateInfoParam.getBirthday());
        }
        // 更新指定字段
        this.update(lambdaUpdateWrapper);
    }

    @Override
    public String getAvatarById(ClientUserIdParam clientUserIdParam) {
        return this.detail(clientUserIdParam).getAvatar();
    }

    @Override
    public ClientUser queryEntity(String id) {
        ClientUser clientUser = this.getById(id);
        if(ObjectUtil.isEmpty(clientUser)) {
            throw new CommonException("用户不存在，id值为：{}", id);
        }
        return clientUser;
    }

    @Override
    public ClientUserPicValidCodeResult getPicCaptcha() {
        // 生成验证码，随机4位字符
        CircleCaptcha circleCaptcha = CaptchaUtil.createCircleCaptcha(100, 38, 4, 10);
        // 定义返回结果
        ClientUserPicValidCodeResult clientUserPicValidCodeResult = new ClientUserPicValidCodeResult();
        // 获取验证码的值
        String validCode = circleCaptcha.getCode();
        // 获取验证码的base64
        String validCodeBase64 = circleCaptcha.getImageBase64Data();
        // 生成请求号
        String validCodeReqNo = IdWorker.getIdStr();
        // 将base64返回前端
        clientUserPicValidCodeResult.setValidCodeBase64(validCodeBase64);
        // 将请求号返回前端
        clientUserPicValidCodeResult.setValidCodeReqNo(validCodeReqNo);
        // 获取验证码失效时间（单位：秒）
        long validCodeExpiredDuration = this.getValidCodeExpiredDuration();
        // 将请求号作为key，验证码的值作为value放到redis，用于校验
        commonCacheOperator.put(USER_VALID_CODE_CACHE_KEY + validCodeReqNo, validCode, validCodeExpiredDuration);
        return clientUserPicValidCodeResult;
    }

    /**
     * 校验验证码
     *
     * @author xuyuxiang
     * @date 2022/8/25 14:29
     **/
    private void validValidCode(String phoneOrEmail, String validCode, String validCodeReqNo) {
        // 依据请求号，取出缓存中的验证码进行校验
        Object existValidCode;
        if(ObjectUtil.isEmpty(phoneOrEmail)) {
            existValidCode = commonCacheOperator.get(USER_VALID_CODE_CACHE_KEY + validCodeReqNo);
        } else {
            existValidCode = commonCacheOperator.get(USER_VALID_CODE_CACHE_KEY + phoneOrEmail + StrUtil.UNDERLINE + validCodeReqNo);
        }
        // 为空则直接验证码错误
        if (ObjectUtil.isEmpty(existValidCode)) {
            throw new CommonException("验证码错误");
        }
        // 移除该验证码
        if(ObjectUtil.isEmpty(phoneOrEmail)) {
            commonCacheOperator.remove(USER_VALID_CODE_CACHE_KEY + validCodeReqNo);
        } else {
            commonCacheOperator.remove(USER_VALID_CODE_CACHE_KEY + phoneOrEmail + StrUtil.UNDERLINE + validCodeReqNo);
        }
        // 不一致则直接验证码错误
        if (!validCode.equalsIgnoreCase(Convert.toStr(existValidCode))) {
            throw new CommonException("验证码错误");
        }
    }

    @Override
    public String findPasswordGetPhoneValidCode(ClientUserGetPhoneValidCodeParam clientUserGetPhoneValidCodeParam) {
        return this.getPhoneValidCode(clientUserGetPhoneValidCodeParam, "重置密码验证码短信消息模板编码", SNOWY_SMS_TEMPLATE_VALID_CODE_RESET_PASSWORD_FOR_C_KEY);
    }

    /**
     * 获取手机验证码
     *
     * @author xuyuxiang
     * @date 2022/8/25 15:16
     **/
    private String getPhoneValidCode(ClientUserGetPhoneValidCodeParam clientUserGetPhoneValidCodeParam, String templateCodeName, String templateCodeValue) {
        // 手机号
        String phone = clientUserGetPhoneValidCodeParam.getPhone();
        // 验证码正确则校验手机号格式
        if (!PhoneUtil.isMobile(phone)) {
            throw new CommonException("手机号码：{}格式错误", phone);
        }
        // 执行校验验证码
        validValidCode(null, clientUserGetPhoneValidCodeParam.getValidCode(), clientUserGetPhoneValidCodeParam.getValidCodeReqNo());
        // 根据手机号获取用户信息，判断用户是否存在
        if (ObjectUtil.isEmpty(this.getUserByPhone(phone))) {
            throw new CommonException("手机码：{}不存在", phone);
        }
        // 生成手机验证码的值，随机6为数字
        String phoneValidCode = RandomUtil.randomNumbers(6);
        // 生成手机验证码的请求号
        String phoneValidCodeReqNo = IdWorker.getIdStr();
        // 验证码短信消息模板编码
        String smsTemplateCode = devConfigApi.getValueByKey(templateCodeValue);
        if(ObjectUtil.isEmpty(smsTemplateCode)){
            throw new CommonException("请联系管理员配置{}", templateCodeName);
        }
        // 获取验证码失效时间（单位：秒）
        long validCodeExpiredDuration = this.getValidCodeExpiredDuration();
        // 模板内容转为JSONObject
        JSONObject contentJSONObject = JSONUtil.parseObj(smsTemplateCode);
        // 定义变量参数
        JSONObject paramMap = JSONUtil.createObj().set("userPhone", phone).set("validCode", phoneValidCode).set("validTime", validCodeExpiredDuration/60);
        // 获取编码
        String codeValue = contentJSONObject.getStr("code");
        // 发送短信
        devSmsApi.sendDynamicSms(phone, codeValue, paramMap);
        // 将请求号作为key，验证码的值作为value放到redis，用于校验
        commonCacheOperator.put(USER_VALID_CODE_CACHE_KEY + phone + StrUtil.UNDERLINE + phoneValidCodeReqNo, phoneValidCode, validCodeExpiredDuration);
        // 返回请求号
        return phoneValidCodeReqNo;
    }

    @Override
    public String findPasswordGetEmailValidCode(ClientUserGetEmailValidCodeParam clientUserGetEmailValidCodeParam) {
        return this.getEmailValidCode(clientUserGetEmailValidCodeParam, "重置密码验证码邮件消息模板内容", SNOWY_EMAIL_TEMPLATE_VALID_CODE_RESET_PASSWORD_FOR_C_KEY);
    }

    /**
     * 获取邮箱验证码
     *
     * @author xuyuxiang
     * @date 2022/8/25 15:16
     **/
    private String getEmailValidCode(ClientUserGetEmailValidCodeParam clientUserGetEmailValidCodeParam, String templateContentName, String templateContentValue) {
        // 邮箱
        String email = clientUserGetEmailValidCodeParam.getEmail();
        // 验证码正确则校验邮箱格式
        if (CommonEmailUtil.isNotEmail(email)) {
            throw new CommonException("邮箱：{}格式错误", email);
        }
        // 执行校验验证码
        validValidCode(null, clientUserGetEmailValidCodeParam.getValidCode(), clientUserGetEmailValidCodeParam.getValidCodeReqNo());
        // 根据邮箱获取用户信息，判断用户是否存在
        if (ObjectUtil.isEmpty(this.getUserByEmail(email))) {
            throw new CommonException("邮箱：{}不存在", email);
        }
        // 生成邮箱验证码的值，随机6为数字
        String emailValidCode = RandomUtil.randomNumbers(6);
        // 生成邮箱验证码的请求号
        String emailValidCodeReqNo = IdWorker.getIdStr();
        // 重置密码验证码邮件消息模板内容
        String emailTemplateContent = devConfigApi.getValueByKey(templateContentValue);
        if(ObjectUtil.isEmpty(emailTemplateContent)){
            throw new CommonException("请联系管理员配置{}", templateContentName);
        }
        // 获取验证码失效时间（单位：秒）
        long validCodeExpiredDuration = this.getValidCodeExpiredDuration();
        // 模板内容转为JSONObject
        JSONObject contentJSONObject = JSONUtil.parseObj(emailTemplateContent);
        // 定义变量参数
        JSONObject paramMap = JSONUtil.createObj().set("userEmail", email).set("validCode", emailValidCode).set("validTime", validCodeExpiredDuration/60);
        // 获取格式化后的主题
        String subject = ClientEmailFormatUtl.format(contentJSONObject.getStr("subject"), paramMap);;
        // 获取格式化后的内容
        String content = ClientEmailFormatUtl.format(contentJSONObject.getStr("content"), paramMap);;
        // 发送邮件
        devEmailApi.sendDynamicHtmlEmail(email, subject, content);
        // 将请求号作为key，验证码的值作为value放到redis，用于校验
        commonCacheOperator.put(USER_VALID_CODE_CACHE_KEY + email + StrUtil.UNDERLINE + emailValidCodeReqNo, emailValidCode, validCodeExpiredDuration);
        // 返回请求号
        return emailValidCodeReqNo;
    }

    @Override
    public void findPasswordByPhone(ClientUserFindPwdByPhoneParam clientUserFindPwdByPhoneParam) {
        // 再次校验手机号是否合法
        String phone = clientUserFindPwdByPhoneParam.getPhone();
        // 校验手机号格式
        if (!PhoneUtil.isMobile(phone)) {
            throw new CommonException("手机号码：{}格式错误", phone);
        }
        // 根据手机号获取用户信息，判断用户是否存在
        ClientLoginUser clientLoginUser = this.getUserByPhone(phone);
        if (ObjectUtil.isEmpty(clientLoginUser)) {
            throw new CommonException("手机号码：{}不存在对应用户", phone);
        }
        // 执行校验验证码
        validValidCode(phone, clientUserFindPwdByPhoneParam.getValidCode(), clientUserFindPwdByPhoneParam.getValidCodeReqNo());
        // 获取新密码
        String newPassword = CommonCryptogramUtil.doSm2Decrypt(clientUserFindPwdByPhoneParam.getNewPassword()).trim();
        // 校验新密码
        ClientPasswordUtl.validNewPassword(clientLoginUser, newPassword);
        // 修改密码
        this.update(new LambdaUpdateWrapper<ClientUser>().eq(ClientUser::getPhone,
                CommonCryptogramUtil.doSm4CbcEncrypt(phone)).set(ClientUser::getPassword,
                CommonCryptogramUtil.doHashValue(newPassword)));
        // 更新用户最新修改密码时间
        clientUserExtService.updatePasswordLastTime(clientLoginUser.getId());
        // 追加用户历史密码信息
        clientUserPasswordService.insertUserPasswordHistory(clientLoginUser.getId(), newPassword);
        // 重置密码成功短信消息模板编码
        String smsTemplateCode = devConfigApi.getValueByKey(SNOWY_SMS_TEMPLATE_NOTICE_PASSWORD_RESET_SUCCESS_FOR_C_KEY);
        // 不为空才发送
        if(ObjectUtil.isNotEmpty(smsTemplateCode)){
            // 模板内容转为JSONObject
            JSONObject contentJSONObject = JSONUtil.parseObj(smsTemplateCode);
            // 定义变量参数
            JSONObject paramMap = JSONUtil.createObj().set("userPhone", phone).set("userNewPassword", newPassword);
            // 获取编码
            String codeValue = contentJSONObject.getStr("code");
            // 发送短信
            devSmsApi.sendDynamicSms(phone, codeValue, paramMap);
        }
    }

    @Override
    public void findPasswordByEmail(ClientUserFindPwdByEmailParam clientUserFindPwdByEmailParam) {
        // 再次校验邮箱是否合法
        String email = clientUserFindPwdByEmailParam.getEmail();
        // 校验邮箱格式
        if (CommonEmailUtil.isNotEmail(email)) {
            throw new CommonException("邮箱：{}格式错误", email);
        }
        // 根据邮箱获取用户信息，判断用户是否存在
        ClientLoginUser clientLoginUser = this.getUserByEmail(email);
        if (ObjectUtil.isEmpty(clientLoginUser)) {
            throw new CommonException("邮箱：{}不存在对应用户", email);
        }
        // 执行校验验证码
        validValidCode(email, clientUserFindPwdByEmailParam.getValidCode(), clientUserFindPwdByEmailParam.getValidCodeReqNo());
        // 获取新密码
        String newPassword = CommonCryptogramUtil.doSm2Decrypt(clientUserFindPwdByEmailParam.getNewPassword()).trim();
        // 校验新密码
        ClientPasswordUtl.validNewPassword(clientLoginUser, newPassword);
        // 修改密码
        this.update(new LambdaUpdateWrapper<ClientUser>().eq(ClientUser::getEmail, email).set(ClientUser::getPassword,
                CommonCryptogramUtil.doHashValue(newPassword)));
        // 更新用户最新修改密码时间
        clientUserExtService.updatePasswordLastTime(clientLoginUser.getId());
        // 追加用户历史密码信息
        clientUserPasswordService.insertUserPasswordHistory(clientLoginUser.getId(), newPassword);
        // 重置密码成功邮件消息模板内容
        String emailTemplateContent = devConfigApi.getValueByKey(SNOWY_EMAIL_TEMPLATE_NOTICE_PASSWORD_RESET_SUCCESS_FOR_C_KEY);
        // 不为空才发送
        if(ObjectUtil.isNotEmpty(emailTemplateContent)){
            // 模板内容转为JSONObject
            JSONObject contentJSONObject = JSONUtil.parseObj(emailTemplateContent);
            // 定义变量参数
            JSONObject paramMap = JSONUtil.createObj().set("userEmail", clientUserFindPwdByEmailParam.getEmail()).set("userNewPassword", newPassword);
            // 获取格式化后的主题
            String subject = ClientEmailFormatUtl.format(contentJSONObject.getStr("subject"), paramMap);;
            // 获取格式化后的内容
            String content = ClientEmailFormatUtl.format(contentJSONObject.getStr("content"), paramMap);;
            // 发送邮件
            devEmailApi.sendDynamicHtmlEmail(email, subject, content);
        }
    }

    @Override
    public String updatePasswordGetPhoneValidCode(ClientUserGetPhoneValidCodeParam clientUserGetPhoneValidCodeParam) {
        // 判断密码验证方式
        ClientPasswordUtl.validUpdatePasswordValidType(ClientUpdatePasswordValidTypeEnum.PHONE.getValue());
        return this.getPhoneValidCode(clientUserGetPhoneValidCodeParam, "修改密码验证码短信消息模板编码", SNOWY_SMS_TEMPLATE_VALID_CODE_UPDATE_PASSWORD_FOR_C_KEY);
    }

    @Override
    public String updatePasswordGetEmailValidCode(ClientUserGetEmailValidCodeParam clientUserGetEmailValidCodeParam) {
        // 判断密码验证方式
        ClientPasswordUtl.validUpdatePasswordValidType(ClientUpdatePasswordValidTypeEnum.EMAIL.getValue());
        return this.getEmailValidCode(clientUserGetEmailValidCodeParam, "修改密码验证码邮件消息模板内容", SNOWY_EMAIL_TEMPLATE_VALID_CODE_UPDATE_PASSWORD_FOR_C_KEY);
    }

    @Override
    public void updatePasswordByOld(ClientUserUpdatePwdByOldParam clientUserUpdatePwdByOldParam) {
        // 判断密码验证方式
        ClientPasswordUtl.validUpdatePasswordValidType(ClientUpdatePasswordValidTypeEnum.OLD.getValue());
        ClientUser clientUser = this.queryEntity(StpClientUtil.getLoginIdAsString());
        String password = CommonCryptogramUtil.doSm2Decrypt(clientUserUpdatePwdByOldParam.getPassword()).trim();
        String newPassword = CommonCryptogramUtil.doSm2Decrypt(clientUserUpdatePwdByOldParam.getNewPassword()).trim();
        if (!CommonCryptogramUtil.doHashValue(password).equals(clientUser.getPassword())) {
            throw new CommonException("原密码错误");
        }
        // 校验新密码
        ClientPasswordUtl.validNewPassword(clientUser, newPassword);
        // 修改密码
        this.update(new LambdaUpdateWrapper<ClientUser>().eq(ClientUser::getId,
                clientUser.getId()).set(ClientUser::getPassword,
                CommonCryptogramUtil.doHashValue(newPassword)));
        // 更新用户最新修改密码时间
        clientUserExtService.updatePasswordLastTime(clientUser.getId());
        // 追加用户历史密码信息
        clientUserPasswordService.insertUserPasswordHistory(clientUser.getId(), newPassword);
        // 获取手机号
        String phone = clientUser.getPhone();
        // 手机号不为空则发送密码重置成功短信
        if(ObjectUtil.isNotEmpty(phone)){
            // 重置密码成功短信消息模板编码
            String smsTemplateCode = devConfigApi.getValueByKey(SNOWY_SMS_TEMPLATE_NOTICE_PASSWORD_RESET_SUCCESS_FOR_C_KEY);
            // 不为空才发送
            if(ObjectUtil.isNotEmpty(smsTemplateCode)){
                // 模板内容转为JSONObject
                JSONObject contentJSONObject = JSONUtil.parseObj(smsTemplateCode);
                // 定义变量参数
                JSONObject paramMap = JSONUtil.createObj().set("userPhone", phone).set("userNewPassword", newPassword);
                // 获取编码
                String codeValue = contentJSONObject.getStr("code");
                // 编码不为空
                if(ObjectUtil.isNotEmpty(codeValue)){
                    try {
                        // 发送短信
                        devSmsApi.sendDynamicSms(phone, codeValue, paramMap);
                    } catch (Exception e) {
                        log.error(">>> 短信发送失败", e);
                    }
                }
            }
        }
        // 获取手机号
        String email = clientUser.getEmail();
        // 密码不为空则发送密码重置成功邮件
        if(ObjectUtil.isNotEmpty(email)){
            // 重置密码成功邮件消息模板内容
            String emailTemplateContent = devConfigApi.getValueByKey(SNOWY_EMAIL_TEMPLATE_NOTICE_PASSWORD_RESET_SUCCESS_FOR_C_KEY);
            // 不为空才发送
            if(ObjectUtil.isNotEmpty(emailTemplateContent)){
                // 模板内容转为JSONObject
                JSONObject contentJSONObject = JSONUtil.parseObj(emailTemplateContent);
                // 定义变量参数
                JSONObject paramMap = JSONUtil.createObj().set("userEmail", email).set("userNewPassword", newPassword);
                // 获取格式化后的主题
                String subject = ClientEmailFormatUtl.format(contentJSONObject.getStr("subject"), paramMap);;
                // 获取格式化后的内容
                String content = ClientEmailFormatUtl.format(contentJSONObject.getStr("content"), paramMap);;
                try {
                    // 发送邮件
                    devEmailApi.sendDynamicHtmlEmail(email, subject, content);
                } catch (Exception e) {
                    log.error(">>> 邮件发送失败", e);
                }
            }
        }
    }

    @Override
    public void updatePasswordByPhone(ClientUserUpdatePwdByPhoneParam clientUserUpdatePwdByPhoneParam) {
        // 判断密码验证方式
        ClientPasswordUtl.validUpdatePasswordValidType(ClientUpdatePasswordValidTypeEnum.PHONE.getValue());
        ClientUserFindPwdByPhoneParam clientUserFindPwdByPhoneParam = new ClientUserFindPwdByPhoneParam();
        BeanUtil.copyProperties(clientUserUpdatePwdByPhoneParam, clientUserFindPwdByPhoneParam);
        this.findPasswordByPhone(clientUserFindPwdByPhoneParam);
    }

    @Override
    public void updatePasswordByEmail(ClientUserUpdatePwdByEmailParam clientUserUpdatePwdByEmailParam) {
        // 判断密码验证方式
        ClientPasswordUtl.validUpdatePasswordValidType(ClientUpdatePasswordValidTypeEnum.EMAIL.getValue());
        ClientUserFindPwdByEmailParam clientUserFindPwdByEmailParam = new ClientUserFindPwdByEmailParam();
        BeanUtil.copyProperties(clientUserUpdatePwdByEmailParam, clientUserFindPwdByEmailParam);
        this.findPasswordByEmail(clientUserFindPwdByEmailParam);
    }

    @Override
    public String bindPhoneGetPhoneValidCode(ClientUserGetPhoneValidCodeParam clientUserGetPhoneValidCodeParam) {
        // 手机号
        String phone = clientUserGetPhoneValidCodeParam.getPhone();
        // 校验手机号格式
        if (!PhoneUtil.isMobile(phone)) {
            throw new CommonException("手机号码：{}格式错误", phone);
        }
        // 执行校验验证码
        validValidCode(null, clientUserGetPhoneValidCodeParam.getValidCode(), clientUserGetPhoneValidCodeParam.getValidCodeReqNo());
        // 根据手机号获取用户信息，判断用户是否存在，如果存在则不能绑定该手机号
        if (ObjectUtil.isNotEmpty(this.getUserByPhone(phone))) {
            throw new CommonException("手机号码：{}已存在对应用户", phone);
        }
        // 生成手机验证码的值，随机6为数字
        String phoneValidCode = RandomUtil.randomNumbers(6);
        // 生成手机验证码的请求号
        String phoneValidCodeReqNo = IdWorker.getIdStr();
        // 绑定手机验证码短信消息模板编码
        String smsTemplateCode = devConfigApi.getValueByKey(SNOWY_SMS_TEMPLATE_VALID_CODE_BINDING_PHONE_FOR_C_KEY);
        if(ObjectUtil.isEmpty(smsTemplateCode)){
            throw new CommonException("请联系管理员配置绑定手机验证码短信消息模板编码");
        }
        // 获取验证码失效时间（单位：秒）
        long validCodeExpiredDuration = this.getValidCodeExpiredDuration();
        // 模板内容转为JSONObject
        JSONObject contentJSONObject = JSONUtil.parseObj(smsTemplateCode);
        // 定义变量参数
        JSONObject paramMap = JSONUtil.createObj().set("userPhone", phone).set("validCode", phoneValidCode)
                .set("validTime", validCodeExpiredDuration/60);
        // 获取编码
        String codeValue = contentJSONObject.getStr("code");
        // 发送短信
        devSmsApi.sendDynamicSms(phone, codeValue, paramMap);
        // 将请求号作为key，验证码的值作为value放到redis，用于校验
        commonCacheOperator.put(USER_VALID_CODE_CACHE_KEY + phone + StrUtil.UNDERLINE + phoneValidCodeReqNo, phoneValidCode, validCodeExpiredDuration);
        // 返回请求号
        return phoneValidCodeReqNo;
    }

    @Override
    public String updateBindPhoneGetPhoneValidCode(ClientUserGetPhoneValidCodeParam clientUserGetPhoneValidCodeParam) {
        // 手机号
        String phone = clientUserGetPhoneValidCodeParam.getPhone();
        // 校验手机号格式
        if (!PhoneUtil.isMobile(phone)) {
            throw new CommonException("手机号码：{}格式错误", phone);
        }
        // 执行校验验证码
        validValidCode(null, clientUserGetPhoneValidCodeParam.getValidCode(), clientUserGetPhoneValidCodeParam.getValidCodeReqNo());
        // 根据手机号获取用户信息，判断用户是否存在，如果存在则不能绑定该手机号
        if (ObjectUtil.isNotEmpty(this.getUserByPhone(phone))) {
            throw new CommonException("手机号码：{}已存在对应用户", phone);
        }
        // 生成手机验证码的值，随机6为数字
        String phoneValidCode = RandomUtil.randomNumbers(6);
        // 生成手机验证码的请求号
        String phoneValidCodeReqNo = IdWorker.getIdStr();
        // 修改绑定手机验证码短信消息模板编码
        String smsTemplateCode = devConfigApi.getValueByKey(SNOWY_SMS_TEMPLATE_VALID_CODE_UPDATE_BINDING_PHONE_FOR_C_KEY);
        if(ObjectUtil.isEmpty(smsTemplateCode)){
            throw new CommonException("请联系管理员配置修改绑定手机验证码短信消息模板编码");
        }
        // 获取验证码失效时间（单位：秒）
        long validCodeExpiredDuration = this.getValidCodeExpiredDuration();
        // 模板内容转为JSONObject
        JSONObject contentJSONObject = JSONUtil.parseObj(smsTemplateCode);
        // 定义变量参数
        JSONObject paramMap = JSONUtil.createObj().set("userPhone", phone).set("validCode", phoneValidCode)
                .set("validTime", validCodeExpiredDuration/60);
        // 获取编码
        String codeValue = contentJSONObject.getStr("code");
        // 发送短信
        devSmsApi.sendDynamicSms(phone, codeValue, paramMap);
        // 将请求号作为key，验证码的值作为value放到redis，用于校验
        commonCacheOperator.put(USER_VALID_CODE_CACHE_KEY + phone + StrUtil.UNDERLINE + phoneValidCodeReqNo, phoneValidCode, validCodeExpiredDuration);
        // 返回请求号
        return phoneValidCodeReqNo;
    }

    @Override
    public void bindPhone(ClientUserBindPhoneParam clientUserBindPhoneParam) {
        // 再次校验手机号是否合法
        String phone = clientUserBindPhoneParam.getPhone();
        // 校验手机号格式
        if (!PhoneUtil.isMobile(phone)) {
            throw new CommonException("手机号码：{}格式错误", phone);
        }
        // 根据手机号获取用户信息，判断用户是否存在，如果存在则不能绑定该手机号
        if (ObjectUtil.isNotEmpty(this.getUserByPhone(phone))) {
            throw new CommonException("手机号码：{}已存在对应用户", phone);
        }
        // 执行校验验证码
        validValidCode(phone, clientUserBindPhoneParam.getValidCode(), clientUserBindPhoneParam.getValidCodeReqNo());
        // 修改手机号
        this.update(new LambdaUpdateWrapper<ClientUser>().eq(ClientUser::getId, StpClientUtil.getLoginIdAsString())
                .set(ClientUser::getPhone, CommonCryptogramUtil.doSm4CbcEncrypt(phone)));
    }

    @Override
    public String bindEmailGetEmailValidCode(ClientUserGetEmailValidCodeParam clientUserGetEmailValidCodeParam) {
        // 邮箱
        String email = clientUserGetEmailValidCodeParam.getEmail();
        // 校验邮箱格式
        if (CommonEmailUtil.isNotEmail(email)) {
            throw new CommonException("邮箱：{}格式错误", email);
        }
        // 执行校验验证码
        validValidCode(null, clientUserGetEmailValidCodeParam.getValidCode(), clientUserGetEmailValidCodeParam.getValidCodeReqNo());
        // 根据邮箱获取用户信息，判断用户是否存在，如果存在则不能绑定该邮箱
        if (ObjectUtil.isEmpty(this.getUserByEmail(email))) {
            throw new CommonException("邮箱：{}已存在对应用户", email);
        }
        // 生成邮箱验证码的值，随机6为数字
        String emailValidCode = RandomUtil.randomNumbers(6);
        // 生成邮箱验证码的请求号
        String emailValidCodeReqNo = IdWorker.getIdStr();
        // 绑定邮箱验证码邮件消息模板内容
        String emailTemplateContent = devConfigApi.getValueByKey(SNOWY_EMAIL_TEMPLATE_VALID_CODE_BINDING_EMAIL_FOR_C_KEY);
        if(ObjectUtil.isEmpty(emailTemplateContent)){
            throw new CommonException("请联系管理员配置绑定邮箱验证码邮件消息模板内容");
        }
        // 获取验证码失效时间（单位：秒）
        long validCodeExpiredDuration = this.getValidCodeExpiredDuration();
        // 模板内容转为JSONObject
        JSONObject contentJSONObject = JSONUtil.parseObj(emailTemplateContent);
        // 定义变量参数
        JSONObject paramMap = JSONUtil.createObj().set("userEmail", email).set("validCode", emailValidCode)
                .set("validTime", validCodeExpiredDuration/60);
        // 获取格式化后的主题
        String subject = ClientEmailFormatUtl.format(contentJSONObject.getStr("subject"), paramMap);;
        // 获取格式化后的内容
        String content = ClientEmailFormatUtl.format(contentJSONObject.getStr("content"), paramMap);;
        // 发送邮件
        devEmailApi.sendDynamicHtmlEmail(email, subject, content);
        // 将请求号作为key，验证码的值作为value放到redis，用于校验
        commonCacheOperator.put(USER_VALID_CODE_CACHE_KEY + email + StrUtil.UNDERLINE + emailValidCodeReqNo, emailValidCode, validCodeExpiredDuration);
        // 返回请求号
        return emailValidCodeReqNo;
    }

    @Override
    public String updateBindEmailGetEmailValidCode(ClientUserGetEmailValidCodeParam clientUserGetEmailValidCodeParam) {
        // 邮箱
        String email = clientUserGetEmailValidCodeParam.getEmail();
        // 校验邮箱格式
        if (CommonEmailUtil.isNotEmail(email)) {
            throw new CommonException("邮箱：{}格式错误", email);
        }
        // 执行校验验证码
        validValidCode(null, clientUserGetEmailValidCodeParam.getValidCode(), clientUserGetEmailValidCodeParam.getValidCodeReqNo());
        // 根据邮箱获取用户信息，判断用户是否存在，如果存在则不能绑定该邮箱
        if (ObjectUtil.isEmpty(this.getUserByEmail(email))) {
            throw new CommonException("邮箱：{}已存在对应用户", email);
        }
        // 生成邮箱验证码的值，随机6为数字
        String emailValidCode = RandomUtil.randomNumbers(6);
        // 生成邮箱验证码的请求号
        String emailValidCodeReqNo = IdWorker.getIdStr();
        // 修改绑定邮箱验证码邮件消息模板内容
        String emailTemplateContent = devConfigApi.getValueByKey(SNOWY_EMAIL_TEMPLATE_VALID_CODE_UPDATE_BINDING_EMAIL_FOR_C_KEY);
        if(ObjectUtil.isEmpty(emailTemplateContent)){
            throw new CommonException("请联系管理员配置修改绑定邮箱验证码邮件消息模板内容");
        }
        // 获取验证码失效时间（单位：秒）
        long validCodeExpiredDuration = this.getValidCodeExpiredDuration();
        // 模板内容转为JSONObject
        JSONObject contentJSONObject = JSONUtil.parseObj(emailTemplateContent);
        // 定义变量参数
        JSONObject paramMap = JSONUtil.createObj().set("userEmail", email).set("validCode", emailValidCode)
                .set("validTime", validCodeExpiredDuration/60);
        // 获取格式化后的主题
        String subject = ClientEmailFormatUtl.format(contentJSONObject.getStr("subject"), paramMap);;
        // 获取格式化后的内容
        String content = ClientEmailFormatUtl.format(contentJSONObject.getStr("content"), paramMap);;
        // 发送邮件
        devEmailApi.sendDynamicHtmlEmail(email, subject, content);
        // 将请求号作为key，验证码的值作为value放到redis，用于校验
        commonCacheOperator.put(USER_VALID_CODE_CACHE_KEY + email + StrUtil.UNDERLINE + emailValidCodeReqNo, emailValidCode, validCodeExpiredDuration);
        // 返回请求号
        return emailValidCodeReqNo;
    }

    @Override
    public void bindEmail(ClientUserBindEmailParam clientUserBindEmailParam) {
        // 再次校验邮箱是否合法
        String email = clientUserBindEmailParam.getEmail();
        // 校验邮箱格式
        if (CommonEmailUtil.isNotEmail(email)) {
            throw new CommonException("邮箱：{}格式错误", email);
        }
        // 根据邮箱获取用户信息，判断用户是否存在，如果存在则不能绑定该邮箱
        if (ObjectUtil.isEmpty(this.getUserByEmail(email))) {
            throw new CommonException("邮箱：{}已存在对应用户", email);
        }
        // 执行校验验证码
        validValidCode(email, clientUserBindEmailParam.getValidCode(), clientUserBindEmailParam.getValidCodeReqNo());
        // 修改邮箱
        this.update(new LambdaUpdateWrapper<ClientUser>().eq(ClientUser::getId, StpClientUtil.getLoginIdAsString())
                .set(ClientUser::getEmail, email));
    }

    @Override
    public String updateAvatar(MultipartFile file) {
        ClientUser clientUser = this.queryEntity(StpClientUtil.getLoginIdAsString());
        try {
            String suffix = Objects.requireNonNull(FileUtil.getSuffix(file.getOriginalFilename())).toLowerCase();
            BufferedImage image = ImgUtil.toImage(file.getBytes());
            String base64;
            if(image.getWidth() <= 200 && image.getHeight() <= 200) {
                base64 = ImgUtil.toBase64DataUri(image, suffix);
            } else {
                base64 = ImgUtil.toBase64DataUri(ImgUtil.scale(image, 200, 200, null), suffix);
            }
            this.update(new LambdaUpdateWrapper<ClientUser>().eq(ClientUser::getId,
                    clientUser.getId()).set(ClientUser::getAvatar, base64));
            return base64;
        } catch (IOException e) {
            log.error(">>> 头像修改失败：", e);
            throw new CommonException("头像修改失败，用户id值为：{}", clientUser.getId());
        }
    }

    @Override
    public void updateSignature(ClientUserSignatureParam clientUserSignatureParam) {
        ClientUser clientUser = this.queryEntity(StpClientUtil.getLoginIdAsString());
        String clientUserSignatureStr = clientUserSignatureParam.getSignature();
        if(clientUserSignatureParam.getSignature().contains(StrUtil.COMMA)) {
            clientUserSignatureStr = StrUtil.split(clientUserSignatureStr, StrUtil.COMMA).get(1);
        }
        String base64 = ImgUtil.toBase64DataUri(ImgUtil.scale(ImgUtil.toImage(clientUserSignatureStr),
                100, 50, null), ImgUtil.IMAGE_TYPE_PNG);
        // 更新指定字段
        this.update(new LambdaUpdateWrapper<ClientUser>().eq(ClientUser::getId, clientUser.getId())
                .set(ClientUser::getSignature, base64));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ClientUser createUserWithPhone(String phone) {
        ClientUserAddParam clientUserAddParam = new ClientUserAddParam();
        clientUserAddParam.setAccount(phone);
        clientUserAddParam.setName(phone);
        clientUserAddParam.setPhone(phone);
        clientUserAddParam.setGender(CommonGenderEnum.UNKNOWN.getValue());
        // 保存用户
        this.add(clientUserAddParam, ClientUserSourceFromTypeEnum.SYSTEM_REGISTER.getValue());
        // 获取用户信息
        ClientUser clientUser = this.getOne(new LambdaQueryWrapper<ClientUser>().eq(ClientUser::getPhone, CommonCryptogramUtil.doSm4CbcEncrypt(phone)));
        // 发送注册成功短信
        String smsTemplateCode = devConfigApi.getValueByKey(SNOWY_SMS_TEMPLATE_NOTICE_REGISTER_SUCCESS_FOR_C_KEY);
        // 不为空才发送
        if(ObjectUtil.isNotEmpty(smsTemplateCode)){
            // 模板内容转为JSONObject
            JSONObject contentJSONObject = JSONUtil.parseObj(smsTemplateCode);
            // 定义变量参数
            JSONObject paramMap = JSONUtil.createObj().set("userPhone", phone);
            // 获取编码
            String codeValue = contentJSONObject.getStr("code");
            // 发送短信
            devSmsApi.sendDynamicSms(phone, codeValue, paramMap);
        }
        // 返回用户
        return clientUser;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ClientUser createUserWithEmail(String email) {
        ClientUserAddParam clientUserAddParam = new ClientUserAddParam();
        clientUserAddParam.setAccount(email);
        clientUserAddParam.setName(email);
        clientUserAddParam.setEmail(email);
        clientUserAddParam.setGender(CommonGenderEnum.UNKNOWN.getValue());
        // 保存用户
        this.add(clientUserAddParam, ClientUserSourceFromTypeEnum.SYSTEM_REGISTER.getValue());
        // 获取用户信息
        ClientUser clientUser = this.getOne(new LambdaQueryWrapper<ClientUser>().eq(ClientUser::getEmail, email));
        // 发送注册成功邮件
        String emailTemplateContent = devConfigApi.getValueByKey(SNOWY_EMAIL_TEMPLATE_NOTICE_REGISTER_SUCCESS_FOR_C_KEY);
        // 不为空才发送
        if(ObjectUtil.isNotEmpty(emailTemplateContent)) {
            // 模板内容转为JSONObject
            JSONObject contentJSONObject = JSONUtil.parseObj(emailTemplateContent);
            // 定义变量参数
            JSONObject paramMap = JSONUtil.createObj().set("userEmail", email);
            // 获取格式化后的主题
            String subject = ClientEmailFormatUtl.format(contentJSONObject.getStr("subject"), paramMap);
            // 获取格式化后的内容
            String content = ClientEmailFormatUtl.format(contentJSONObject.getStr("content"), paramMap);
            // 发送邮件
            devEmailApi.sendDynamicHtmlEmail(email, subject, content);
        }
        // 返回用户
        return clientUser;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ClientUser createUserWithAccount(String account, String password) {
        ClientUserAddParam clientUserAddParam = new ClientUserAddParam();
        clientUserAddParam.setAccount(account);
        clientUserAddParam.setName(account);
        clientUserAddParam.setPassword(password);
        clientUserAddParam.setGender(CommonGenderEnum.UNKNOWN.getValue());
        // 保存用户
        this.add(clientUserAddParam, ClientUserSourceFromTypeEnum.SYSTEM_REGISTER.getValue());
        // 返回用户
        return this.getOne(new LambdaQueryWrapper<ClientUser>().eq(ClientUser::getAccount, account));
    }

    @Override
    public Boolean isUserPasswordExpired() {
        return ClientPasswordUtl.isUserPasswordExpired(StpClientUtil.getLoginIdAsString());
    }

    @Override
    public Boolean isUserNeedBindPhone() {
        // 获取当前用户
        ClientUser clientUser = this.queryEntity(StpClientUtil.getLoginIdAsString());
        // 查询当前用户是否注册的
        ClientUserExt clientUserExt = clientUserExtService.getOne(new LambdaQueryWrapper<ClientUserExt>().eq(ClientUserExt::getUserId, StpClientUtil.getLoginIdAsString())
                .eq(ClientUserExt::getSourceFromType, ClientUserSourceFromTypeEnum.SYSTEM_REGISTER.getValue()));
        // 不为空，则判断手机号是否为空
        if(ObjectUtil.isNotEmpty(clientUserExt)){
            // 手机号为空，判断系统注册后是否需要绑定手机号
            if(ObjectUtil.isEmpty(clientUser.getPhone())) {
                String registerNeedBindPhone = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_REGISTER_NEED_BIND_PHONE_FOR_C_KEY);
                if(ObjectUtil.isNotEmpty(registerNeedBindPhone)){
                    return Convert.toBool(registerNeedBindPhone);
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public Boolean isUserNeedBindEmail() {
        // 获取当前用户
        ClientUser clientUser = this.queryEntity(StpClientUtil.getLoginIdAsString());
        // 查询当前用户是否注册的
        ClientUserExt clientUserExt = clientUserExtService.getOne(new LambdaQueryWrapper<ClientUserExt>().eq(ClientUserExt::getUserId, StpClientUtil.getLoginIdAsString())
                .eq(ClientUserExt::getSourceFromType, ClientUserSourceFromTypeEnum.SYSTEM_REGISTER.getValue()));
        // 不为空，则判断邮箱是否为空
        if(ObjectUtil.isNotEmpty(clientUserExt)){
            // 邮箱为空，判断系统注册后是否需要绑定邮箱
            if(ObjectUtil.isEmpty(clientUser.getEmail())) {
                String registerNeedBindEmail = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_REGISTER_NEED_BIND_EMAIL_FOR_C_KEY);
                if(ObjectUtil.isNotEmpty(registerNeedBindEmail)){
                    return Convert.toBool(registerNeedBindEmail);
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void noticeUserPasswordAboutToExpired() {
        // 密码即将到期短信消息模板
        String smsTemplateCode = devConfigApi.getValueByKey(SNOWY_SMS_TEMPLATE_NOTICE_PASSWORD_EXPIRED_FOR_C_KEY);
        // 密码即将到期邮件消息模板
        String emailTemplateContent = devConfigApi.getValueByKey(SNOWY_EMAIL_TEMPLATE_NOTICE_PASSWORD_EXPIRED_FOR_C_KEY);
        // 获取今日需要提醒密码到期的用户集合
        ClientPasswordUtl.thisDayPasswordExpiredNeedNoticeUserIdList().forEach(clientUser -> {
            // 获取手机号
            String phone = clientUser.getPhone();
            // 不为空才发送
            if(ObjectUtil.isAllNotEmpty(phone, smsTemplateCode)){
                // 模板内容转为JSONObject
                JSONObject contentJSONObject = JSONUtil.parseObj(smsTemplateCode);
                // 定义变量参数
                JSONObject paramMap = JSONUtil.createObj().set("userPhone", phone);
                // 获取编码
                String codeValue = contentJSONObject.getStr("code");
                // 编码不为空
                if(ObjectUtil.isNotEmpty(codeValue)){
                    try {
                        // 发送短信
                        devSmsApi.sendDynamicSms(phone, codeValue, paramMap);
                    } catch (Exception e) {
                        log.error(">>> 短信发送失败", e);
                    }
                }
            }
            // 获取邮箱
            String email = clientUser.getEmail();
            // 不为空才发送
            if(ObjectUtil.isAllNotEmpty(email, emailTemplateContent)){
                // 模板内容转为JSONObject
                JSONObject contentJSONObject = JSONUtil.parseObj(emailTemplateContent);
                // 定义变量参数
                JSONObject paramMap = JSONUtil.createObj().set("userEmail", email);
                // 获取格式化后的主题
                String subject = ClientEmailFormatUtl.format(contentJSONObject.getStr("subject"), paramMap);;
                // 获取格式化后的内容
                String content = ClientEmailFormatUtl.format(contentJSONObject.getStr("content"), paramMap);;
                try {
                    // 发送邮件
                    devEmailApi.sendDynamicHtmlEmail(email, subject, content);
                } catch (Exception e) {
                    log.error(">>> 邮件发送失败", e);
                }
            }
        });
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void doRegister(String account, String password) {
        // 校验账号
        ClientLoginUser clientLoginUser = this.getUserByAccount(account);
        if(ObjectUtil.isNotEmpty(clientLoginUser)) {
            throw new CommonException("账号已存在");
        }
        // 校验密码
        ClientPasswordUtl.validNewPassword(password);
        // 根据账号密码创建用户
        this.createUserWithAccount(account, password);
    }

    /**
     * 获取验证码失效时间（单位：秒）
     *
     * @author xuyuxiang
     * @date 2025/3/21 20:25
     **/
    private long getValidCodeExpiredDuration() {
        // 默认5分钟
        int defaultExpiredTime = 5;
        // 获取配置验证码失效时间
        String configCaptchaExpiredDuration = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_CAPTCHA_EXPIRED_DURATION_FOR_C_KEY);
        // 判断是否为空
        if(ObjectUtil.isNotEmpty(configCaptchaExpiredDuration)){
            // 配置了则使用配置的失效时间
            defaultExpiredTime = Convert.toInt(configCaptchaExpiredDuration);
        }
        // 转为秒
        return defaultExpiredTime * 60L;
    }
}
