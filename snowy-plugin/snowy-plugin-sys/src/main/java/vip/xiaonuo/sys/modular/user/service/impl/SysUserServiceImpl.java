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
package vip.xiaonuo.sys.modular.user.service.impl;

import cn.afterturn.easypoi.cache.manager.POICacheManager;
import cn.afterturn.easypoi.entity.ImageEntity;
import cn.afterturn.easypoi.word.WordExportUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.lang.tree.parser.DefaultNodeParser;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.excel.write.style.row.AbstractRowHeightStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fhs.trans.service.impl.TransService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.auth.core.util.StpLoginUserUtil;
import vip.xiaonuo.common.cache.CommonCacheOperator;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.excel.CommonExcelCustomMergeStrategy;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.listener.CommonDataChangeEventCenter;
import vip.xiaonuo.common.page.CommonPageRequest;
import vip.xiaonuo.common.util.*;
import vip.xiaonuo.dev.api.DevConfigApi;
import vip.xiaonuo.dev.api.DevEmailApi;
import vip.xiaonuo.dev.api.DevMessageApi;
import vip.xiaonuo.dev.api.DevSmsApi;
import vip.xiaonuo.mobile.api.MobileButtonApi;
import vip.xiaonuo.mobile.api.MobileMenuApi;
import vip.xiaonuo.sys.core.enums.SysBuildInEnum;
import vip.xiaonuo.sys.core.enums.SysDataTypeEnum;
import vip.xiaonuo.sys.modular.org.entity.SysOrg;
import vip.xiaonuo.sys.modular.org.service.SysOrgService;
import vip.xiaonuo.sys.modular.position.entity.SysPosition;
import vip.xiaonuo.sys.modular.position.service.SysPositionService;
import vip.xiaonuo.sys.modular.relation.entity.SysRelation;
import vip.xiaonuo.sys.modular.relation.enums.SysRelationCategoryEnum;
import vip.xiaonuo.sys.modular.relation.service.SysRelationService;
import vip.xiaonuo.sys.modular.resource.entity.SysButton;
import vip.xiaonuo.sys.modular.resource.entity.SysMenu;
import vip.xiaonuo.sys.modular.resource.entity.SysModule;
import vip.xiaonuo.sys.modular.resource.enums.SysResourceCategoryEnum;
import vip.xiaonuo.sys.modular.resource.enums.SysResourceMenuTypeEnum;
import vip.xiaonuo.sys.modular.resource.service.SysButtonService;
import vip.xiaonuo.sys.modular.resource.service.SysMenuService;
import vip.xiaonuo.sys.modular.resource.service.SysModuleService;
import vip.xiaonuo.sys.modular.role.entity.SysRole;
import vip.xiaonuo.sys.modular.role.enums.SysRoleDataScopeCategoryEnum;
import vip.xiaonuo.sys.modular.role.service.SysRoleService;
import vip.xiaonuo.sys.modular.user.entity.SysUser;
import vip.xiaonuo.sys.modular.user.enums.SysUserStatusEnum;
import vip.xiaonuo.sys.modular.user.mapper.SysUserMapper;
import vip.xiaonuo.sys.modular.user.param.*;
import vip.xiaonuo.sys.modular.user.result.*;
import vip.xiaonuo.sys.modular.user.service.SysUserService;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户Service接口实现类
 *
 * @author xuyuxiang
 * @date 2022/2/23 18:43
 **/
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private static final String SNOWY_SYS_DEFAULT_PASSWORD_KEY = "SNOWY_SYS_DEFAULT_PASSWORD";

    private static final String SNOWY_SYS_DEFAULT_WORKBENCH_DATA_KEY = "SNOWY_SYS_DEFAULT_WORKBENCH_DATA";

    private static final String USER_VALID_CODE_CACHE_KEY = "user-validCode:";

    public static final String USER_CACHE_ALL_KEY = "sys-user:all";

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
    private DevMessageApi devMessageApi;

    @Resource
    private MobileMenuApi mobileMenuApi;

    @Resource
    private SysOrgService sysOrgService;

    @Resource
    private SysPositionService sysPositionService;

    @Resource
    private SysRoleService sysRoleService;

    @Resource
    private SysModuleService sysModuleService;

    @Resource
    private SysMenuService sysMenuService;

    @Resource
    private SysButtonService sysButtonService;

    @Resource
    private SysRelationService sysRelationService;

    @Resource
    private MobileButtonApi mobileButtonApi;

    @Override
    public SysLoginUser getUserById(String id) {
        SysUser sysUser = this.getById(id);
        if (ObjectUtil.isNotEmpty(sysUser)) {
            transService.transOne(sysUser);
            return BeanUtil.copyProperties(sysUser, SysLoginUser.class);
        }
        return null;
    }

    @Override
    public SysLoginUser getUserByAccount(String account) {
        SysUser sysUser = this.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getAccount, account));
        if (ObjectUtil.isNotEmpty(sysUser)) {
            transService.transOne(sysUser);
            return BeanUtil.copyProperties(sysUser, SysLoginUser.class);
        }
        return null;
    }

    @Override
    public SysLoginUser getUserByPhone(String phone) {
        SysUser sysUser = this.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getPhone, CommonCryptogramUtil.doSm4CbcEncrypt(phone)));
        if (ObjectUtil.isNotEmpty(sysUser)) {
            transService.transOne(sysUser);
            return BeanUtil.copyProperties(sysUser, SysLoginUser.class);
        }
        return null;
    }

    @Override
    public SysLoginUser getUserByEmail(String email) {
        SysUser sysUser = this.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getEmail, email));
        if (ObjectUtil.isNotEmpty(sysUser)) {
            transService.transOne(sysUser);
            return BeanUtil.copyProperties(sysUser, SysLoginUser.class);
        }
        return null;
    }

    @Override
    public Page<SysUser> page(SysUserPageParam sysUserPageParam) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<SysUser>().checkSqlInjection();
        if (ObjectUtil.isNotEmpty(sysUserPageParam.getSearchKey())) {
            queryWrapper.lambda().and(q -> q.like(SysUser::getAccount, sysUserPageParam.getSearchKey()).or()
                    .like(SysUser::getName, sysUserPageParam.getSearchKey()));
        }
        if (ObjectUtil.isNotEmpty(sysUserPageParam.getOrgId())) {
            queryWrapper.lambda().eq(SysUser::getOrgId, sysUserPageParam.getOrgId());
        }
        if (ObjectUtil.isNotEmpty(sysUserPageParam.getUserStatus())) {
            queryWrapper.lambda().eq(SysUser::getUserStatus, sysUserPageParam.getUserStatus());
        }
        if (ObjectUtil.isAllNotEmpty(sysUserPageParam.getSortField(), sysUserPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(sysUserPageParam.getSortOrder());
            queryWrapper.orderBy(true, sysUserPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(sysUserPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(SysUser::getSortCode);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(SysUserAddParam sysUserAddParam) {
        checkParam(sysUserAddParam);
        SysUser sysUser = BeanUtil.toBean(sysUserAddParam, SysUser.class);
        if (ObjectUtil.isEmpty(sysUser.getAvatar())) {
            // 设置默认头像
            sysUser.setAvatar(CommonAvatarUtil.generateImg(sysUser.getName()));
        }
        // 设置默认密码
        sysUser.setPassword(CommonCryptogramUtil.doHashValue(devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_PASSWORD_KEY)));
        // 设置状态
        sysUser.setUserStatus(SysUserStatusEnum.ENABLE.getValue());
        this.save(sysUser);

        // 发布增加事件
        CommonDataChangeEventCenter.doAddWithData(SysDataTypeEnum.USER.getValue(), JSONUtil.createArray().put(sysUser));
    }

    private void checkParam(SysUserAddParam sysUserAddParam) {
        if (this.count(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getAccount, sysUserAddParam.getAccount())) > 0) {
            throw new CommonException("存在重复的账号，账号为：{}", sysUserAddParam.getAccount());
        }
        if (ObjectUtil.isNotEmpty(sysUserAddParam.getPhone())) {
            if (!PhoneUtil.isMobile(sysUserAddParam.getPhone())) {
                throw new CommonException("手机号码：{}格式错误", sysUserAddParam.getPhone());
            }
            if (this.count(new LambdaQueryWrapper<SysUser>()
                    .eq(SysUser::getPhone, CommonCryptogramUtil.doSm4CbcEncrypt(sysUserAddParam.getPhone()))) > 0) {
                throw new CommonException("存在重复的手机号，手机号为：{}", sysUserAddParam.getPhone());
            }
        }
        if (ObjectUtil.isNotEmpty(sysUserAddParam.getEmail())) {
            if (!CommonEmailUtil.isEmail(sysUserAddParam.getEmail())) {
                throw new CommonException("邮箱：{}格式错误", sysUserAddParam.getEmail());
            }
            if (this.count(new LambdaQueryWrapper<SysUser>()
                    .eq(SysUser::getEmail, sysUserAddParam.getEmail())) > 0) {
                throw new CommonException("存在重复的邮箱，邮箱为：{}", sysUserAddParam.getEmail());
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(SysUserEditParam sysUserEditParam) {
        SysUser sysUser = this.queryEntity(sysUserEditParam.getId());
        checkParam(sysUserEditParam);
        boolean updateSuperAdminAccount = sysUser.getAccount().equals(SysBuildInEnum.BUILD_IN_USER_ACCOUNT.getValue()) &&
                !sysUserEditParam.getAccount().equals(SysBuildInEnum.BUILD_IN_USER_ACCOUNT.getValue());
        if(updateSuperAdminAccount) {
            throw new CommonException("不可修改系统内置超管用户账号");
        }
        BeanUtil.copyProperties(sysUserEditParam, sysUser);
        this.updateById(sysUser);

        // 发布更新事件
        CommonDataChangeEventCenter.doUpdateWithData(SysDataTypeEnum.USER.getValue(), JSONUtil.createArray().put(sysUser));
    }

    private void checkParam(SysUserEditParam sysUserEditParam) {
        if (this.count(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getAccount, sysUserEditParam.getAccount())
                .ne(SysUser::getId, sysUserEditParam.getId())) > 0) {
            throw new CommonException("存在重复的账号，账号为：{}", sysUserEditParam.getAccount());
        }
        if (ObjectUtil.isNotEmpty(sysUserEditParam.getPhone())) {
            if (!PhoneUtil.isMobile(sysUserEditParam.getPhone())) {
                throw new CommonException("手机号码：{}格式错误", sysUserEditParam.getPhone());
            }
            if (this.count(new LambdaQueryWrapper<SysUser>()
                    .eq(SysUser::getPhone, CommonCryptogramUtil.doSm4CbcEncrypt(sysUserEditParam.getPhone()))
                    .ne(SysUser::getId, sysUserEditParam.getId())) > 0) {
                throw new CommonException("存在重复的手机号，手机号为：{}", sysUserEditParam.getPhone());
            }
        }
        if (ObjectUtil.isNotEmpty(sysUserEditParam.getEmail())) {
            if (!CommonEmailUtil.isEmail(sysUserEditParam.getEmail())) {
                throw new CommonException("邮箱：{}格式错误", sysUserEditParam.getEmail());
            }
            if (this.count(new LambdaQueryWrapper<SysUser>()
                    .eq(SysUser::getEmail, sysUserEditParam.getEmail())
                    .ne(SysUser::getId, sysUserEditParam.getId())) > 0) {
                throw new CommonException("存在重复的邮箱，邮箱为：{}", sysUserEditParam.getEmail());
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<SysUserIdParam> sysUserIdParamList) {
        List<String> sysUserIdList = CollStreamUtil.toList(sysUserIdParamList, SysUserIdParam::getId);
        if (ObjectUtil.isNotEmpty(sysUserIdList)) {
            boolean containsSuperAdminAccount = this.listByIds(sysUserIdList).stream().map(SysUser::getAccount)
                    .collect(Collectors.toSet()).contains(SysBuildInEnum.BUILD_IN_USER_ACCOUNT.getValue());
            if (containsSuperAdminAccount) {
                throw new CommonException("不可删除系统内置超管用户");
            }

            // 清除【将这些用户作为主管】的信息
            this.update(new LambdaUpdateWrapper<SysUser>().in(SysUser::getDirectorId, sysUserIdList).set(SysUser::getDirectorId, null));

            // 清除【将这些用户作为兼任职位的主管】的信息
            this.list(new LambdaQueryWrapper<SysUser>().isNotNull(SysUser::getPositionJson)).forEach(sysUser -> {
                List<JSONObject> handledJsonObjectList = JSONUtil.toList(JSONUtil.parseArray(sysUser.getPositionJson()),
                        JSONObject.class).stream().peek(jsonObject -> {
                    String directorId = jsonObject.getStr("directorId");
                    if (ObjectUtil.isNotEmpty(directorId) && sysUserIdList.contains(directorId)) {
                        jsonObject.remove("directorId");
                    }
                }).collect(Collectors.toList());
                this.update(new LambdaUpdateWrapper<SysUser>().eq(SysUser::getId, sysUser.getId())
                        .set(SysUser::getPositionJson, JSONUtil.toJsonStr(handledJsonObjectList)));
            });

            // 清除【将这些用户作为主管】的机构的主管信息
            sysOrgService.update(new LambdaUpdateWrapper<SysOrg>().in(SysOrg::getDirectorId, sysUserIdList).set(SysOrg::getDirectorId, null));

            // 执行删除
            this.removeByIds(sysUserIdList);

            // 发布删除事件
            CommonDataChangeEventCenter.doDeleteWithDataId(SysDataTypeEnum.USER.getValue(), sysUserIdList);
        }
    }

    @Override
    public SysUser detail(SysUserIdParam sysUserIdParam) {
        return this.queryEntity(sysUserIdParam.getId());
    }

    @Override
    public void disableUser(SysUserIdParam sysUserIdParam) {
        this.update(new LambdaUpdateWrapper<SysUser>().eq(SysUser::getId,
                sysUserIdParam.getId()).set(SysUser::getUserStatus, SysUserStatusEnum.DISABLED.getValue()));
    }

    @Override
    public void enableUser(SysUserIdParam sysUserIdParam) {

        this.update(new LambdaUpdateWrapper<SysUser>().eq(SysUser::getId,
                sysUserIdParam.getId()).set(SysUser::getUserStatus, SysUserStatusEnum.ENABLE.getValue()));
    }

    @Override
    public void resetPassword(SysUserIdParam sysUserIdParam) {
        this.update(new LambdaUpdateWrapper<SysUser>().eq(SysUser::getId,
                sysUserIdParam.getId()).set(SysUser::getPassword,
                CommonCryptogramUtil.doHashValue(devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_PASSWORD_KEY))));
    }

    @Override
    public SysUserPicValidCodeResult getPicCaptcha() {
        // 生成验证码，随机4位字符
        CircleCaptcha circleCaptcha = CaptchaUtil.createCircleCaptcha(100, 38, 4, 10);
        // 定义返回结果
        SysUserPicValidCodeResult sysUserPicValidCodeResult = new SysUserPicValidCodeResult();
        // 获取验证码的值
        String validCode = circleCaptcha.getCode();
        // 获取验证码的base64
        String validCodeBase64 = circleCaptcha.getImageBase64Data();
        // 生成请求号
        String validCodeReqNo = IdWorker.getIdStr();
        // 将base64返回前端
        sysUserPicValidCodeResult.setValidCodeBase64(validCodeBase64);
        // 将请求号返回前端
        sysUserPicValidCodeResult.setValidCodeReqNo(validCodeReqNo);
        // 将请求号作为key，验证码的值作为value放到redis，用于校验，5分钟有效
        commonCacheOperator.put(USER_VALID_CODE_CACHE_KEY + validCodeReqNo, validCode, 5 * 60);
        return sysUserPicValidCodeResult;
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
    public String findPasswordGetPhoneValidCode(SysUserGetPhoneValidCodeParam sysUserGetPhoneValidCodeParam) {
        // 手机号
        String phone = sysUserGetPhoneValidCodeParam.getPhone();
        // 验证码正确则校验手机号格式
        if (!PhoneUtil.isMobile(phone)) {
            throw new CommonException("手机号码：{}格式错误", phone);
        }
        // 执行校验验证码
        validValidCode(null, sysUserGetPhoneValidCodeParam.getValidCode(), sysUserGetPhoneValidCodeParam.getValidCodeReqNo());
        // 根据手机号获取用户信息，判断用户是否存在
        if (ObjectUtil.isEmpty(this.getUserByPhone(phone))) {
            throw new CommonException("手机码：{}不存在", phone);
        }
        // 生成手机验证码的值，随机6为数字
        String phoneValidCode = RandomUtil.randomNumbers(6);
        // 生成手机验证码的请求号
        String phoneValidCodeReqNo = IdWorker.getIdStr();

        // TODO 使用阿里云执行发送验证码，将验证码作为短信内容的参数变量放入，
        // TODO 签名不传则使用系统默认配置的签名，支持传入多个参数，示例：{"name":"张三","number":"15038****76"}
        //devSmsApi.sendSmsAliyun(phone, null, "验证码模板号", JSONUtil.toJsonStr(JSONUtil.createObj().set("validCode", phoneValidCode)));

        // TODO 使用腾讯云执行发送验证码，将验证码作为短信内容的参数变量放入，
        // TODO sdkAppId和签名不传则使用系统默认配置的sdkAppId和签名，支持传入多个参数，逗号拼接，示例："张三,15038****76,进行中"
        //devSmsApi.sendSmsTencent(null, phone, null, "验证码模板号", phoneValidCode);

        // 将请求号作为key，验证码的值作为value放到redis，用于校验，5分钟有效
        commonCacheOperator.put(USER_VALID_CODE_CACHE_KEY + phone + StrUtil.UNDERLINE + phoneValidCodeReqNo, phoneValidCode, 5 * 60);
        // 返回请求号
        return phoneValidCodeReqNo;
    }

    @Override
    public String findPasswordGetEmailValidCode(SysUserGetEmailValidCodeParam sysUserGetEmailValidCodeParam) {
        // 邮箱
        String email = sysUserGetEmailValidCodeParam.getEmail();
        // 验证码正确则校验邮箱格式
        if (!CommonEmailUtil.isEmail(email)) {
            throw new CommonException("邮箱：{}格式错误", email);
        }
        // 执行校验验证码
        validValidCode(null, sysUserGetEmailValidCodeParam.getValidCode(), sysUserGetEmailValidCodeParam.getValidCodeReqNo());
        // 根据邮箱获取用户信息，判断用户是否存在
        if (ObjectUtil.isEmpty(this.getUserByEmail(email))) {
            throw new CommonException("邮箱：{}不存在", email);
        }
        // 生成邮箱验证码的值，随机6为数字
        String emailValidCode = RandomUtil.randomNumbers(6);
        // 生成邮箱验证码的请求号
        String emailValidCodeReqNo = IdWorker.getIdStr();

        // TODO 使用本地发送邮件
        String content = "您正在找回密码，验证码为：" + emailValidCode + "，5分钟内有效。";
        devEmailApi.sendTextEmailLocal(email, "找回密码邮件", content, CollectionUtil.newArrayList());

        // 将请求号作为key，验证码的值作为value放到redis，用于校验，5分钟有效
        commonCacheOperator.put(USER_VALID_CODE_CACHE_KEY + email + StrUtil.UNDERLINE + emailValidCodeReqNo, emailValidCode, 5 * 60);
        // 返回请求号
        return emailValidCodeReqNo;
    }

    @Override
    public void findPasswordByPhone(SysUserFindPwdByPhoneParam sysUserFindPwdByPhoneParam) {
        // 执行校验验证码
        validValidCode(sysUserFindPwdByPhoneParam.getPhone(), sysUserFindPwdByPhoneParam.getValidCode(), sysUserFindPwdByPhoneParam.getValidCodeReqNo());
        this.update(new LambdaUpdateWrapper<SysUser>().eq(SysUser::getPhone,
                CommonCryptogramUtil.doSm4CbcEncrypt(sysUserFindPwdByPhoneParam.getPhone())).set(SysUser::getPassword,
                CommonCryptogramUtil.doHashValue(CommonCryptogramUtil.doSm2Decrypt(sysUserFindPwdByPhoneParam.getNewPassword()))));
    }

    @Override
    public void findPasswordByEmail(SysUserFindPwdByEmailParam sysUserFindPwdByEmailParam) {
        // 执行校验验证码
        validValidCode(sysUserFindPwdByEmailParam.getEmail(), sysUserFindPwdByEmailParam.getValidCode(), sysUserFindPwdByEmailParam.getValidCodeReqNo());
        this.update(new LambdaUpdateWrapper<SysUser>().eq(SysUser::getEmail,
                sysUserFindPwdByEmailParam.getEmail()).set(SysUser::getPassword,
                CommonCryptogramUtil.doHashValue(CommonCryptogramUtil.doSm2Decrypt(sysUserFindPwdByEmailParam.getNewPassword()))));
    }

    @Override
    public void updatePassword(SysUserUpdatePwdParam sysUserUpdatePwdParam) {
        SysUser sysUser = this.queryEntity(StpUtil.getLoginIdAsString());
        String password = sysUserUpdatePwdParam.getPassword();
        String newPassword = sysUserUpdatePwdParam.getNewPassword();
        if (!CommonCryptogramUtil.doHashValue(password).equals(sysUser.getPassword())) {
            throw new CommonException("原密码错误");
        }
        this.update(new LambdaUpdateWrapper<SysUser>().eq(SysUser::getId,
                sysUser.getId()).set(SysUser::getPassword,
                CommonCryptogramUtil.doHashValue(newPassword)));
    }

    @Override
    public String updateAvatar(MultipartFile file) {
        SysUser sysUser = this.queryEntity(StpUtil.getLoginIdAsString());
        try {
            String suffix = Objects.requireNonNull(FileUtil.getSuffix(file.getOriginalFilename())).toLowerCase();
            BufferedImage image = ImgUtil.toImage(file.getBytes());
            String base64;
            if(image.getWidth() <= 200 && image.getHeight() <= 200) {
                base64 = ImgUtil.toBase64DataUri(image, suffix);
            } else {
                base64 = ImgUtil.toBase64DataUri(ImgUtil.scale(image, 200, 200, null), suffix);
            }
            this.update(new LambdaUpdateWrapper<SysUser>().eq(SysUser::getId,
                    sysUser.getId()).set(SysUser::getAvatar, base64));
            return base64;
        } catch (IOException e) {
            log.error(">>> 头像修改失败：", e);
            throw new CommonException("头像修改失败，用户id值为：{}", sysUser.getId());
        }
    }

    @Override
    public void updateSignature(SysUserSignatureParam sysUserSignatureParam) {
        SysUser sysUser = this.queryEntity(StpUtil.getLoginIdAsString());
        String sysUserSignatureStr = sysUserSignatureParam.getSignature();
        if(sysUserSignatureParam.getSignature().contains(StrUtil.COMMA)) {
            sysUserSignatureStr = StrUtil.split(sysUserSignatureStr, StrUtil.COMMA).get(1);
        }
        String base64 = ImgUtil.toBase64DataUri(ImgUtil.scale(ImgUtil.toImage(sysUserSignatureStr),
                100, 50, null), ImgUtil.IMAGE_TYPE_PNG);
        // 更新指定字段
        this.update(new LambdaUpdateWrapper<SysUser>().eq(SysUser::getId, sysUser.getId())
                .set(SysUser::getSignature, base64));
    }

    @Override
    public void updateUserLoginInfo(String userId, String device) {
        SysUser sysUser = this.queryEntity(userId);
        sysUser.setLastLoginTime(sysUser.getLatestLoginTime());
        sysUser.setLastLoginIp(sysUser.getLatestLoginIp());
        sysUser.setLastLoginAddress(sysUser.getLatestLoginAddress());
        sysUser.setLastLoginDevice(sysUser.getLatestLoginDevice());
        sysUser.setLatestLoginTime(DateTime.now());
        String ip = CommonIpAddressUtil.getIp(CommonServletUtil.getRequest());
        sysUser.setLatestLoginIp(ip);
        sysUser.setLatestLoginAddress(CommonIpAddressUtil.getCityInfo(ip));
        sysUser.setLatestLoginDevice(device);
        this.updateById(sysUser);
    }

    @Override
    public List<Tree<String>> ownMenu(SysUserIdParam sysUserIdParam) {

        // 获取角色id列表
        List<String> roleIdList = this.ownRole(sysUserIdParam);

        // 获取菜单id列表
        List<String> menuIdList = sysRelationService.getRelationTargetIdListByObjectIdAndCategory(sysUserIdParam.getId(),
                SysRelationCategoryEnum.SYS_USER_HAS_RESOURCE.getValue());

        if (ObjectUtil.isNotEmpty(roleIdList)) {
            menuIdList.addAll(sysRelationService.getRelationTargetIdListByObjectIdListAndCategory(roleIdList,
                    SysRelationCategoryEnum.SYS_ROLE_HAS_RESOURCE.getValue()));
        }

        // 获取所有的菜单和模块列表，并按分类和排序码排序
        List<SysMenu> allModuleAndMenuAndSpaList = sysMenuService.list(new LambdaQueryWrapper<SysMenu>()
                .in(SysMenu::getCategory, SysResourceCategoryEnum.MODULE.getValue(), SysResourceCategoryEnum.MENU.getValue())
                .orderByAsc(CollectionUtil.newArrayList(SysMenu::getCategory,SysMenu::getSortCode)));
        // 全部以菜单承载
        List<SysMenu> allModuleList = CollectionUtil.newArrayList();
        List<SysMenu> allMenuList = CollectionUtil.newArrayList();
        // 根据类型抽取
        allModuleAndMenuAndSpaList.forEach(sysMenu -> {
            boolean isModule = sysMenu.getCategory().equals(SysResourceCategoryEnum.MODULE.getValue());
            if (isModule) {
                // 抽取所有的模块列表
                allModuleList.add(sysMenu);
            }
            boolean isMenu = sysMenu.getCategory().equals(SysResourceCategoryEnum.MENU.getValue());
            if (isMenu) {
                // 抽取所有的菜单列表
                allMenuList.add(sysMenu);
            }
        });

        // 定义结果
        List<SysMenu> resultList = CollectionUtil.newArrayList();

        // 获取拥有的菜单列表
        List<SysMenu> menuList = allMenuList.stream().filter(sysMenu ->
                menuIdList.contains(sysMenu.getId())).collect(Collectors.toList());

        // 对获取到的角色对应的菜单列表进行处理，获取父列表
        menuList.forEach(sysMenu -> execRecursionFindParent(allMenuList, sysMenu.getId(), resultList));

        // 将拥有的菜单列表添加
        resultList.addAll(menuList);

        // 获取模块id集合
        Set<String> moduleIdSet = resultList.stream().map(SysMenu::getModule).collect(Collectors.toSet());

        // 抽取拥有的模块列表
        List<SysMenu> moduleList = allModuleList.stream().filter(sysMenu ->
                moduleIdSet.contains(sysMenu.getId())).collect(Collectors.toList());

        // 如果一个模块都没拥有
        if (ObjectUtil.isEmpty(moduleList)) {
            // 如果系统中无模块（极端情况）
            if (ObjectUtil.isEmpty(allModuleList)) {
                // 如果系统中无菜单，则返回空列表
                if (ObjectUtil.isEmpty(allMenuList)) {
                    return CollectionUtil.newArrayList();
                } else {
                    // 否则构造一个模块，并添加到拥有模块
                    SysMenu sysMenu = new SysMenu();
                    sysMenu.setId(IdWorker.getIdStr());
                    sysMenu.setPath(StrUtil.SLASH + RandomUtil.randomString(10));
                    sysMenu.setCategory(SysResourceCategoryEnum.MODULE.getValue());
                    allModuleList.add(sysMenu);
                    moduleList.add(sysMenu);
                }
            } else {
                // 否则将系统中第一个模块作为拥有的模块
                moduleList.add(allModuleList.get(0));
            }
        }

        // 将拥有的模块放入集合
        resultList.addAll(moduleList);

        // 获取第一个模块
        SysMenu firstModule = moduleList.get(0);

        // 获取第一个模块下的第一个菜单
        Optional<SysMenu> sysMenus = menuList.stream()
                .filter(sysMenu -> sysMenu.getModule().equals(firstModule.getId()))
                .findFirst()
                .filter(sysMenu -> !sysMenu.getMenuType().equals(SysResourceMenuTypeEnum.CATALOG.getValue()));

        // 最终处理，构造meta
        List<JSONObject> resultJsonObjectList = resultList.stream().map(sysMenu -> {

            // 将模块的父id设置为0，设置随机path
            if (sysMenu.getCategory().equals(SysResourceCategoryEnum.MODULE.getValue())) {
                sysMenu.setParentId("0");
                sysMenu.setPath(StrUtil.SLASH + RandomUtil.randomString(10));
            }
            // 将根菜单的父id设置为模块的id
            if (sysMenu.getCategory().equals(SysResourceCategoryEnum.MENU.getValue())) {
                if ("0".equals(sysMenu.getParentId())) {
                    sysMenu.setParentId(sysMenu.getModule());
                }
            }
            JSONObject menuJsonObject = JSONUtil.parseObj(sysMenu);
            JSONObject metaJsonObject = JSONUtil.createObj();
            metaJsonObject.set("icon", sysMenu.getIcon());
            metaJsonObject.set("title", sysMenu.getTitle());
            metaJsonObject.set("type", ObjectUtil.isEmpty(sysMenu.getMenuType())
                    ? sysMenu.getCategory().toLowerCase() : sysMenu.getMenuType().toLowerCase());
            // 如果是菜单，则设置type菜单类型为小写
            if (sysMenu.getCategory().equals(SysResourceCategoryEnum.MENU.getValue())) {
                if (!sysMenu.getMenuType().equals(SysResourceMenuTypeEnum.CATALOG.getValue())) {
                    metaJsonObject.set("type", sysMenu.getMenuType().toLowerCase());
                }
                if (sysMenu.getId().equals(sysMenus.orElse(null).getId())) {
                    // 如果是首页，则设置affix
                    metaJsonObject.set("affix", true);
                }
            }
            // 如果设置了不可见，那么设置为false，为了兼容已有，所以只是false的为不显示
            if (ObjectUtil.isNotEmpty(sysMenu.getVisible()) && sysMenu.getVisible().equals("FALSE")) {
                metaJsonObject.set("hidden", true);
            }
            menuJsonObject.set("meta", metaJsonObject);
            return menuJsonObject;
        }).collect(Collectors.toList());

        // 执行构造树
        List<TreeNode<String>> treeNodeList = resultJsonObjectList.stream().map(jsonObject ->
                        new TreeNode<>(jsonObject.getStr("id"), jsonObject.getStr("parentId"),
                                jsonObject.getStr("title"), jsonObject.getInt("sortCode")).setExtra(JSONUtil.parseObj(jsonObject)))
                .collect(Collectors.toList());
        return TreeUtil.build(treeNodeList, "0");
    }

    @Override
    public List<Tree<String>> ownMobileMenu(SysUserIdParam sysUserIdParam) {
        // 获取角色id列表
        List<String> roleIdList = this.ownRole(sysUserIdParam);
        List<Tree<String>> resultList = CollectionUtil.newArrayList();
        if (ObjectUtil.isNotEmpty(roleIdList)) {
            resultList = mobileMenuApi.loginMobileMenuTree(sysRelationService.getRelationTargetIdListByObjectIdListAndCategory(roleIdList,
                    SysRelationCategoryEnum.SYS_ROLE_HAS_MOBILE_MENU.getValue()));
        }
        return resultList;
    }

    /**
     * 递归获取父节点
     *
     * @author xuyuxiang
     * @date 2022/6/27 17:56
     **/
    private void execRecursionFindParent(List<SysMenu> originDataList, String id, List<SysMenu> resultList) {
        originDataList.forEach(item -> {
            if (item.getId().equals(id)) {
                int index = CollStreamUtil.toList(originDataList, SysMenu::getId).indexOf(id);
                SysMenu parent = index == -1 ? null : originDataList.get(index);
                if (ObjectUtil.isNotEmpty(parent)) {
                    if (!CollectionUtil.contains(resultList, parent)) {
                        resultList.add(parent);
                    }
                }
                execRecursionFindParent(originDataList, item.getParentId(), resultList);
            }
        });
    }

    @Override
    public List<String> ownRole(SysUserIdParam sysUserIdParam) {
        return sysRelationService.getRelationTargetIdListByObjectIdAndCategory(sysUserIdParam.getId(),
                SysRelationCategoryEnum.SYS_USER_HAS_ROLE.getValue());
    }

    @Override
    public void grantRole(SysUserGrantRoleParam sysUserGrantRoleParam) {
        sysRelationService.saveRelationBatchWithClear(sysUserGrantRoleParam.getId(), sysUserGrantRoleParam.getRoleIdList(),
                SysRelationCategoryEnum.SYS_USER_HAS_ROLE.getValue());
    }

    @Override
    public SysUserOwnResourceResult ownResource(SysUserIdParam sysUserIdParam) {
        SysUserOwnResourceResult sysUserOwnResourceResult = new SysUserOwnResourceResult();
        sysUserOwnResourceResult.setId(sysUserIdParam.getId());
        sysUserOwnResourceResult.setGrantInfoList(sysRelationService.getRelationListByObjectIdAndCategory(sysUserIdParam.getId(),
                SysRelationCategoryEnum.SYS_USER_HAS_RESOURCE.getValue()).stream().map(sysRelation ->
                JSONUtil.toBean(sysRelation.getExtJson(), SysUserOwnResourceResult.SysUserOwnResource.class)).collect(Collectors.toList()));
        return sysUserOwnResourceResult;
    }

    @Override
    public void grantResource(SysUserGrantResourceParam sysUserGrantResourceParam) {
        List<String> menuIdList = sysUserGrantResourceParam.getGrantInfoList().stream()
                .map(SysUserGrantResourceParam.SysUserGrantResource::getMenuId).collect(Collectors.toList());
        if(ObjectUtil.isNotEmpty(menuIdList)) {
            SysUserIdParam sysUserIdParam = new SysUserIdParam();
            sysUserIdParam.setId(sysUserGrantResourceParam.getId());
            List<String> roleIdList = this.ownRole(sysUserIdParam);
            Set<String> sysModuleIdList = sysMenuService.listByIds(menuIdList).stream().map(SysMenu::getModule).collect(Collectors.toSet());
            boolean containsSystemModule = sysModuleService.listByIds(sysModuleIdList).stream().map(SysModule::getCode)
                    .collect(Collectors.toSet()).contains(SysBuildInEnum.BUILD_IN_MODULE_CODE.getValue());
            if(containsSystemModule) {
                if(ObjectUtil.isEmpty(roleIdList)) {
                    throw new CommonException("非超管角色用户不可被授权系统模块菜单资源");
                } else {
                    boolean hasSuperAdminRole = sysRoleService.listByIds(roleIdList).stream().map(SysRole::getCode).collect(Collectors.toSet())
                            .contains(SysBuildInEnum.BUILD_IN_ROLE_CODE.getValue());
                    if(!hasSuperAdminRole) {
                        throw new CommonException("非超管角色用户不可被授权系统模块菜单资源");
                    }
                }
            }
        }
        List<String> extJsonList = sysUserGrantResourceParam.getGrantInfoList().stream()
                .map(JSONUtil::toJsonStr).collect(Collectors.toList());
        sysRelationService.saveRelationBatchWithClear(sysUserGrantResourceParam.getId(), menuIdList, SysRelationCategoryEnum.SYS_USER_HAS_RESOURCE.getValue(), extJsonList);
    }

    @Override
    public SysUserOwnPermissionResult ownPermission(SysUserIdParam sysUserIdParam) {
        SysUserOwnPermissionResult sysUserOwnPermissionResult = new SysUserOwnPermissionResult();
        sysUserOwnPermissionResult.setId(sysUserIdParam.getId());
        sysUserOwnPermissionResult.setGrantInfoList(sysRelationService.getRelationListByObjectIdAndCategory(sysUserIdParam.getId(),
                SysRelationCategoryEnum.SYS_USER_HAS_PERMISSION.getValue()).stream().map(sysRelation ->
                JSONUtil.toBean(sysRelation.getExtJson(), SysUserOwnPermissionResult.SysUserOwnPermission.class)).collect(Collectors.toList()));
        return sysUserOwnPermissionResult;
    }

    @Override
    public void grantPermission(SysUserGrantPermissionParam sysUserGrantPermissionParam) {
        String id = sysUserGrantPermissionParam.getId();
        List<String> apiUrlList = sysUserGrantPermissionParam.getGrantInfoList().stream()
                .map(SysUserGrantPermissionParam.SysUserGrantPermission::getApiUrl).collect(Collectors.toList());
        List<String> extJsonList = sysUserGrantPermissionParam.getGrantInfoList().stream()
                .map(JSONUtil::toJsonStr).collect(Collectors.toList());
        sysRelationService.saveRelationBatchWithClear(id, apiUrlList, SysRelationCategoryEnum.SYS_USER_HAS_PERMISSION.getValue(),
                extJsonList);
    }

    @Override
    public List<Tree<String>> loginOrgTree(SysUserIdParam sysUserIdParam) {
        SysUser sysUser = this.queryEntity(sysUserIdParam.getId());
        List<SysOrg> originDataList = sysOrgService.getAllOrgList();
        List<SysOrg> sysOrgList = sysOrgService.getParentListById(originDataList, sysUser.getOrgId(), true);
        List<TreeNode<String>> treeNodeList = sysOrgList.stream().map(sysOrg -> {
            TreeNode<String> treeNode = new TreeNode<>(sysOrg.getId(), sysOrg.getParentId(), sysOrg.getName(), sysOrg.getSortCode());
            if (sysOrg.getId().equals(sysUser.getOrgId())) {
                treeNode.setExtra(JSONUtil.createObj().set("style", JSONUtil.createObj().set("color", "#FFF")
                        .set("background", "var(--primary-color)")));
            }
            return treeNode;
        }).collect(Collectors.toList());
        return TreeUtil.build(treeNodeList, "0", new TreeNodeConfig().setParentIdKey("pid")
                .setNameKey("label"), new DefaultNodeParser<>());
    }

    @Override
    public void updateUserInfo(SysUserUpdateInfoParam sysUserUpdateInfoParam) {
        String id = StpLoginUserUtil.getLoginUser().getId();
        if (!StrUtil.equals(id,sysUserUpdateInfoParam.getId())){
            throw new CommonException("禁止修改他人信息");
        }

        SysUser sysUser = this.queryEntity(sysUserUpdateInfoParam.getId());

        if (ObjectUtil.isNotEmpty(sysUserUpdateInfoParam.getPhone())) {
            if (!PhoneUtil.isMobile(sysUserUpdateInfoParam.getPhone())) {
                throw new CommonException("手机号码：{}格式错误", sysUserUpdateInfoParam.getPhone());
            }
            if (this.count(new LambdaQueryWrapper<SysUser>().ne(SysUser::getId, sysUser.getId())
                    .eq(SysUser::getPhone, CommonCryptogramUtil.doSm4CbcEncrypt(sysUserUpdateInfoParam.getPhone()))) > 0) {
                throw new CommonException("存在重复的手机号，手机号为：{}", sysUserUpdateInfoParam.getPhone());
            }
        }
        LambdaUpdateWrapper<SysUser> lambdaUpdateWrapper = new LambdaUpdateWrapper<SysUser>().eq(SysUser::getId, sysUser.getId());
        if(ObjectUtil.isNotEmpty(sysUserUpdateInfoParam.getName())) {
            lambdaUpdateWrapper.set(SysUser::getName, sysUserUpdateInfoParam.getName());
        }
        if(ObjectUtil.isNotEmpty(sysUserUpdateInfoParam.getPhone())) {
            lambdaUpdateWrapper.set(SysUser::getPhone, CommonCryptogramUtil.doSm4CbcEncrypt(sysUserUpdateInfoParam.getPhone()));
        }
        if(ObjectUtil.isNotEmpty(sysUserUpdateInfoParam.getNickname())) {
            lambdaUpdateWrapper.set(SysUser::getNickname, sysUserUpdateInfoParam.getNickname());
        }
        if(ObjectUtil.isNotEmpty(sysUserUpdateInfoParam.getGender())) {
            lambdaUpdateWrapper.set(SysUser::getGender, sysUserUpdateInfoParam.getGender());
        }
        if(ObjectUtil.isNotEmpty(sysUserUpdateInfoParam.getBirthday())) {
            lambdaUpdateWrapper.set(SysUser::getBirthday, sysUserUpdateInfoParam.getBirthday());
        }
        if(ObjectUtil.isNotEmpty(sysUserUpdateInfoParam.getEmail())) {
            lambdaUpdateWrapper.set(SysUser::getEmail, sysUserUpdateInfoParam.getEmail());
        }
        if(ObjectUtil.isNotEmpty(sysUserUpdateInfoParam.getSignature())) {
            lambdaUpdateWrapper.set(SysUser::getSignature, sysUserUpdateInfoParam.getSignature());
        }
        // 更新指定字段
        this.update(lambdaUpdateWrapper);
    }

    @Override
    public void updateUserWorkbench(SysUserUpdateWorkbenchParam sysUserUpdateWorkbenchParam) {
        sysRelationService.saveRelationWithClear(StpUtil.getLoginIdAsString(), null,
                SysRelationCategoryEnum.SYS_USER_WORKBENCH_DATA.getValue(), sysUserUpdateWorkbenchParam.getWorkbenchData());
    }

    @Override
    public String loginWorkbench(SysUserIdParam sysUserIdParam) {
        SysUser sysUser = this.queryEntity(sysUserIdParam.getId());
        SysRelation sysRelation = sysRelationService.getOne(new LambdaQueryWrapper<SysRelation>().eq(SysRelation::getObjectId, sysUser.getId())
                .eq(SysRelation::getCategory, SysRelationCategoryEnum.SYS_USER_WORKBENCH_DATA.getValue()));
        if (ObjectUtil.isNotEmpty(sysRelation)) {
            return sysRelation.getExtJson();
        }
        return devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_WORKBENCH_DATA_KEY);
    }

    @Override
    public SysUser queryEntity(String id) {
        SysUser sysUser = this.getById(id);
        if (ObjectUtil.isEmpty(sysUser)) {
            throw new CommonException("用户不存在，id值为：{}", id);
        }
        return sysUser;
    }

    @Override
    public List<JSONObject> getRoleList(String userId) {
        List<String> roleIdList = sysRelationService.getRelationTargetIdListByObjectIdAndCategory(userId,
                SysRelationCategoryEnum.SYS_USER_HAS_ROLE.getValue());
        if (ObjectUtil.isNotEmpty(roleIdList)) {
            return sysRoleService.listByIds(roleIdList).stream().map(JSONUtil::parseObj).collect(Collectors.toList());
        }
        return CollectionUtil.newArrayList();
    }

    @Override
    public List<String> getButtonCodeList(List<String> userAndRoleIdList) {
        List<String> buttonIdList = CollectionUtil.newArrayList();
        sysRelationService.list(new LambdaQueryWrapper<SysRelation>().in(SysRelation::getObjectId, userAndRoleIdList)
                .in(SysRelation::getCategory, SysRelationCategoryEnum.SYS_USER_HAS_RESOURCE.getValue(),
                        SysRelationCategoryEnum.SYS_ROLE_HAS_RESOURCE.getValue())).forEach(sysRelation -> {
            if (ObjectUtil.isNotEmpty(sysRelation.getExtJson())) {
                buttonIdList.addAll(JSONUtil.parseObj(sysRelation.getExtJson()).getBeanList("buttonInfo", String.class));
            }
        });
        if (ObjectUtil.isNotEmpty(buttonIdList)) {
            return sysButtonService.listByIds(buttonIdList).stream().map(SysButton::getCode).collect(Collectors.toList());
        }
        return CollectionUtil.newArrayList();
    }

    @Override
    public List<String> getMobileButtonCodeList(List<String> userAndRoleIdList) {
        List<String> buttonIdList = CollectionUtil.newArrayList();
        sysRelationService.getRelationListByObjectIdListAndCategory(userAndRoleIdList,
                SysRelationCategoryEnum.SYS_ROLE_HAS_MOBILE_MENU.getValue()).forEach(sysRelation -> {
            if (ObjectUtil.isNotEmpty(sysRelation.getExtJson())) {
                buttonIdList.addAll(JSONUtil.parseObj(sysRelation.getExtJson()).getBeanList("buttonInfo", String.class));
            }
        });
        if (ObjectUtil.isNotEmpty(buttonIdList)) {
            return mobileButtonApi.listByIds(buttonIdList);
        }
        return CollectionUtil.newArrayList();
    }

    @Override
    public List<JSONObject> getPermissionList(List<String> userAndRoleIdList, String orgId) {
        Map<String, List<SysRelation>> map = sysRelationService.list(new LambdaQueryWrapper<SysRelation>()
                .in(SysRelation::getObjectId, userAndRoleIdList).in(SysRelation::getCategory,
                        SysRelationCategoryEnum.SYS_USER_HAS_PERMISSION.getValue(),
                        SysRelationCategoryEnum.SYS_ROLE_HAS_PERMISSION.getValue())).stream()
                .collect(Collectors.groupingBy(SysRelation::getTargetId));
        return getScopeListByMap(map, orgId);
    }

    public List<JSONObject> getScopeListByMap(Map<String, List<SysRelation>> groupMap, String orgId) {
        List<JSONObject> resultList = CollectionUtil.newArrayList();
        List<SysOrg> sysOrgList = sysOrgService.getAllOrgList();
        List<String> scopeAllList = sysOrgList.stream().map(SysOrg::getId).collect(Collectors.toList());
        List<String> scopeOrgList = CollectionUtil.newArrayList(orgId);
        List<String> scopeOrgChildList = sysOrgService.getChildListById(sysOrgList, orgId, true)
                .stream().map(SysOrg::getId).collect(Collectors.toList());
        groupMap.forEach((key, value) -> {
            JSONObject jsonObject = JSONUtil.createObj().set("apiUrl", key);
            Set<String> scopeSet = CollectionUtil.newHashSet();
            value.forEach(sysRelation -> {
                JSONObject extJsonObject = JSONUtil.parseObj(sysRelation.getExtJson());
                String scopeCategory = extJsonObject.getStr("scopeCategory");
                if (!scopeCategory.equals(SysRoleDataScopeCategoryEnum.SCOPE_SELF.getValue())) {
                    if (scopeCategory.equals(SysRoleDataScopeCategoryEnum.SCOPE_ALL.getValue())) {
                        scopeSet.addAll(scopeAllList);
                    } else if (scopeCategory.equals(SysRoleDataScopeCategoryEnum.SCOPE_ORG.getValue())) {
                        scopeSet.addAll(scopeOrgList);
                    } else if (scopeCategory.equals(SysRoleDataScopeCategoryEnum.SCOPE_ORG_CHILD.getValue())) {
                        scopeSet.addAll(scopeOrgChildList);
                    } else {
                        scopeSet.addAll(extJsonObject.getBeanList("scopeDefineOrgIdList", String.class));
                    }
                }
            });
            resultList.add(jsonObject.set("dataScope", CollectionUtil.newArrayList(scopeSet)));
        });
        return resultList;
    }

    @Override
    public void downloadImportUserTemplate(HttpServletResponse response) throws IOException {
        try {
            InputStream inputStream = POICacheManager.getFile("userImportTemplate.xlsx");
            byte[] bytes = IoUtil.readBytes(inputStream);
            CommonDownloadUtil.download("SNOWY系统B端用户导入模板.xlsx", bytes, response);
        } catch (Exception e) {
            log.error(">>> 下载用户导入模板失败：", e);
            CommonResponseUtil.renderError(response, "下载用户导入模板失败");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public JSONObject importUser(MultipartFile file) {
        try {
            int successCount = 0;
            int errorCount = 0;
            JSONArray errorDetail = JSONUtil.createArray();
            // 创建临时文件
            File tempFile = FileUtil.writeBytes(file.getBytes(), FileUtil.file(FileUtil.getTmpDir() +
                    FileUtil.FILE_SEPARATOR + "userImportTemplate.xlsx"));
            // 读取excel
            List<SysUserImportParam> sysUserImportParamList =  EasyExcel.read(tempFile).head(SysUserImportParam.class).sheet()
                    .headRowNumber(2).doReadSync();
            List<SysUser> allUserList = this.list();
            for (int i = 0; i < sysUserImportParamList.size(); i++) {
                JSONObject jsonObject = this.doImport(allUserList, sysUserImportParamList.get(i), i);
                if(jsonObject.getBool("success")) {
                    successCount += 1;
                } else {
                    errorCount += 1;
                    errorDetail.add(jsonObject);
                }
            }
            return JSONUtil.createObj()
                    .set("totalCount", sysUserImportParamList.size())
                    .set("successCount", successCount)
                    .set("errorCount", errorCount)
                    .set("errorDetail", errorDetail);
        } catch (Exception e) {
            log.error(">>> 用户导入失败：", e);
            throw new CommonException("用户导入失败");
        }
    }

    /**
     * 执行导入
     *
     * @author xuyuxiang
     * @date 2023/3/7 13:22
     **/
    public JSONObject doImport(List<SysUser> allUserList, SysUserImportParam sysUserImportParam, int i) {
        String account = sysUserImportParam.getAccount();
        String name = sysUserImportParam.getName();
        String orgFullName = sysUserImportParam.getOrgName();
        String positionFullName = sysUserImportParam.getPositionName();
        // 校验必填参数
        if(ObjectUtil.hasEmpty(account, name, orgFullName, positionFullName)) {
            return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "必填字段存在空值");
        } else {
            try {
                // 机构名称
                String orgName = CollectionUtil.getLast(StrUtil.split(orgFullName, StrUtil.DASHED));
                // 职位名称
                String positionName = CollectionUtil.getLast(StrUtil.split(positionFullName, StrUtil.DASHED));
                // 机构id
                String orgId = sysOrgService.getOrgIdByOrgFullNameWithCreate(orgFullName);
                // 职位id
                String positionId = sysPositionService.getPositionIdByPositionNameWithCreate(orgId, positionName);

                // 查找账号对应索引
                int index = CollStreamUtil.toList(allUserList, SysUser::getAccount).indexOf(account);
                SysUser sysUser = new SysUser();
                boolean isAdd = false;
                if(index == -1) {
                    isAdd = true;
                } else {
                    sysUser = allUserList.get(index);
                }

                // 获取手机号和邮箱
                String phone = sysUserImportParam.getPhone();
                String email = sysUserImportParam.getEmail();

                // 判断手机号是否跟系统现有的重复
                if(ObjectUtil.isNotEmpty(phone)) {
                    if(isAdd) {
                        boolean repeatPhone = allUserList.stream().anyMatch(tempSysUser -> ObjectUtil
                                .isNotEmpty(tempSysUser.getPhone()) && tempSysUser.getPhone().equals(phone));
                        if(repeatPhone) {
                            // 新增用户手机号重复则不导入该手机号
                            sysUserImportParam.setPhone(null);
                        }
                    } else {
                        String finalExistUserId = sysUser.getId();
                        boolean repeatPhone = allUserList.stream().anyMatch(tempSysUser -> ObjectUtil
                                .isNotEmpty(tempSysUser.getPhone()) && tempSysUser.getPhone()
                                .equals(phone) && !tempSysUser.getId().equals(finalExistUserId));
                        if(repeatPhone) {
                            // 更新用户手机号重复则使用原手机号
                            sysUser.setPhone(sysUser.getPhone());
                        }
                    }
                }
                // 判断邮箱是否跟系统现有的重复
                if(ObjectUtil.isNotEmpty(email)) {
                    if(isAdd) {
                        boolean repeatEmail = allUserList.stream().anyMatch(tempSysUser -> ObjectUtil
                                .isNotEmpty(tempSysUser.getEmail()) && tempSysUser.getEmail().equals(email));
                        if(repeatEmail) {
                            // 新增邮箱重复则不导入该邮箱
                            sysUserImportParam.setEmail(null);
                        }
                    } else {
                        String finalExistUserId = sysUser.getId();
                        boolean repeatEmail = allUserList.stream().anyMatch(tempSysUser -> ObjectUtil
                                .isNotEmpty(tempSysUser.getEmail()) && tempSysUser.getEmail()
                                .equals(email) && !tempSysUser.getId().equals(finalExistUserId));
                        if(repeatEmail) {
                            // 更新用户手机号重复则使用原邮箱
                            sysUser.setEmail(sysUser.getEmail());
                        }
                    }
                }
                // 拷贝属性
                BeanUtil.copyProperties(sysUserImportParam, sysUser);

                // 设置机构id和职位id
                sysUser.setOrgId(orgId);
                sysUser.setPositionId(positionId);

                // 设置机构名称和职位名称（暂时无作用）
                sysUser.setOrgName(orgName);
                sysUser.setPositionName(positionName);

                // 发布事件
                if(isAdd) {
                    // 设置id
                    sysUser.setId(IdWorker.getIdStr());
                    // 设置默认头像
                    sysUser.setAvatar(CommonAvatarUtil.generateImg(sysUser.getName()));
                    // 设置默认密码
                    sysUser.setPassword(CommonCryptogramUtil.doHashValue(devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_PASSWORD_KEY)));
                    // 设置排序码
                    sysUser.setSortCode(99);
                    // 设置状态
                    sysUser.setUserStatus(SysUserStatusEnum.ENABLE.getValue());
                    // 发布增加事件
                    CommonDataChangeEventCenter.doAddWithData(SysDataTypeEnum.USER.getValue(), JSONUtil.createArray().put(sysUser));
                    // 更新全部用户
                    allUserList.add(sysUser);
                } else {
                    // 发布更新事件
                    CommonDataChangeEventCenter.doUpdateWithData(SysDataTypeEnum.USER.getValue(), JSONUtil.createArray().put(sysUser));
                    // 删除指定索引元素
                    allUserList.remove(index);
                    // 插入指定索引元素
                    allUserList.add(index, sysUser);
                }

                // 保存或更新
                this.saveOrUpdate(sysUser);

                // 返回成功
                return JSONUtil.createObj().set("success", true);
            } catch (Exception e) {
                log.error(">>> 数据导入异常：", e);
                return JSONUtil.createObj().set("success", false).set("index", i + 1).set("msg", "数据导入异常");
            }
        }
    }

    @Override
    public void exportUser(SysUserExportParam sysUserExportParam, HttpServletResponse response) throws IOException {
        File tempFile = null;
        try {
            QueryWrapper<SysUser> queryWrapper = new QueryWrapper<SysUser>().checkSqlInjection();
            if(ObjectUtil.isNotEmpty(sysUserExportParam.getUserIds())) {
                queryWrapper.lambda().in(SysUser::getId, StrUtil.split(sysUserExportParam.getUserIds(), StrUtil.COMMA));
            } else {
                if (ObjectUtil.isNotEmpty(sysUserExportParam.getSearchKey())) {
                    queryWrapper.lambda().and(q -> q.like(SysUser::getAccount, sysUserExportParam.getSearchKey())
                            .or().like(SysUser::getName, sysUserExportParam.getSearchKey())
                            .or().like(SysUser::getPhone, sysUserExportParam.getSearchKey()));
                }
                if (ObjectUtil.isNotEmpty(sysUserExportParam.getUserStatus())) {
                    queryWrapper.lambda().eq(SysUser::getUserStatus, sysUserExportParam.getUserStatus());
                }
            }
            String fileName = "SNOWY系统B端用户信息清单.xlsx";
            List<SysUser> sysUserList = this.list(queryWrapper);
            if(ObjectUtil.isEmpty(sysUserList)) {
                throw new CommonException("无数据可导出");
            }
            transService.transBatch(sysUserList);
            sysUserList = CollectionUtil.sort(sysUserList, Comparator.comparing(SysUser::getOrgId));
            List<SysUserExportResult> sysUserExportResultList = sysUserList.stream()
                    .map(sysUser -> {
                        SysUserExportResult sysUserExportResult = new SysUserExportResult();
                        BeanUtil.copyProperties(sysUser, sysUserExportResult);
                        sysUserExportResult.setGroupName(ObjectUtil.isNotEmpty(sysUserExportResult.getOrgName())?
                                sysUserExportResult.getOrgName():"无组织");
                        // 状态枚举转为文字
                        sysUserExportResult.setUserStatus(sysUserExportResult.getUserStatus()
                                .equalsIgnoreCase(SysUserStatusEnum.ENABLE.getValue())?"正常":"停用");
                        // 将base64转为byte数组
                        if (ObjectUtil.isNotEmpty(sysUser.getAvatar())) {
                            sysUserExportResult.setAvatar(ImgUtil.toBytes(ImgUtil.toImage(StrUtil
                                    .split(sysUser.getAvatar(), StrUtil.COMMA).get(1)), ImgUtil.IMAGE_TYPE_PNG));
                        }
                        return sysUserExportResult;
                    }).collect(Collectors.toList());
            // 创建临时文件
            tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);

            // 头的策略
            WriteCellStyle headWriteCellStyle = new WriteCellStyle();
            WriteFont headWriteFont = new WriteFont();
            headWriteFont.setFontHeightInPoints((short) 14);
            headWriteCellStyle.setWriteFont(headWriteFont);
            // 水平垂直居中
            headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
            headWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // 内容的策略
            WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
            // 这里需要指定 FillPatternType 为FillPatternType.SOLID_FOREGROUND 不然无法显示背景颜色.头默认了 FillPatternType所以可以不指定
            contentWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
            // 内容背景白色
            contentWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
            WriteFont contentWriteFont = new WriteFont();

            // 内容字体大小
            contentWriteFont.setFontHeightInPoints((short) 12);
            contentWriteCellStyle.setWriteFont(contentWriteFont);

            //设置边框样式，细实线
            contentWriteCellStyle.setBorderLeft(BorderStyle.THIN);
            contentWriteCellStyle.setBorderTop(BorderStyle.THIN);
            contentWriteCellStyle.setBorderRight(BorderStyle.THIN);
            contentWriteCellStyle.setBorderBottom(BorderStyle.THIN);

            // 水平垂直居中
            contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.LEFT);
            contentWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
            HorizontalCellStyleStrategy horizontalCellStyleStrategy = new HorizontalCellStyleStrategy(headWriteCellStyle,
                    contentWriteCellStyle);

            // 写excel
            EasyExcel.write(tempFile.getPath(), SysUserExportResult.class)
                    // 自定义样式
                    .registerWriteHandler(horizontalCellStyleStrategy)
                    // 自动列宽
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    // 机构分组合并单元格
                    .registerWriteHandler(new CommonExcelCustomMergeStrategy(sysUserExportResultList.stream().map(SysUserExportResult::getGroupName)
                            .collect(Collectors.toList()), 0))
                    // 设置第一行字体
                    .registerWriteHandler(new CellWriteHandler() {
                        @Override
                        public void afterCellDispose(CellWriteHandlerContext context) {
                            WriteCellData<?> cellData = context.getFirstCellData();
                            WriteCellStyle writeCellStyle = cellData.getOrCreateStyle();
                            Integer rowIndex = context.getRowIndex();
                            if(rowIndex == 0) {
                                WriteFont headWriteFont = new WriteFont();
                                headWriteFont.setFontName("宋体");
                                headWriteFont.setBold(true);
                                headWriteFont.setFontHeightInPoints((short) 16);
                                writeCellStyle.setWriteFont(headWriteFont);
                            }
                        }
                    })
                    // 设置表头行高
                    .registerWriteHandler(new AbstractRowHeightStyleStrategy() {
                        @Override
                        protected void setHeadColumnHeight(Row row, int relativeRowIndex) {
                            if(relativeRowIndex == 0) {
                                // 表头第一行
                                row.setHeightInPoints(34);
                            } else {
                                // 表头其他行
                                row.setHeightInPoints(30);
                            }
                        }
                        @Override
                        protected void setContentColumnHeight(Row row, int relativeRowIndex) {
                            // 内容行
                            row.setHeightInPoints(20);
                        }
                    })
                    .sheet("用户信息")
                    .doWrite(sysUserExportResultList);
            CommonDownloadUtil.download(tempFile, response);
        } catch (Exception e) {
            log.error(">>> 用户导出失败：", e);
            CommonResponseUtil.renderError(response, "用户导出失败");
        } finally {
            FileUtil.del(tempFile);
        }
    }

    @Override
    public void exportUserInfo(SysUserIdParam sysUserIdParam, HttpServletResponse response) throws IOException {
        File destTemplateFile = null;
        File resultFile = null;
        try {
            SysUser sysUser = this.queryEntity(sysUserIdParam.getId());
            transService.transOne(sysUser);
            // 读取模板流
            InputStream inputStream = POICacheManager.getFile("userExportTemplate.docx");
            // 创建一个临时模板
            destTemplateFile = FileUtil.writeFromStream(inputStream, FileUtil.file(FileUtil.getTmpDir() +
                    File.separator + "userExportTemplate.docx"));
            // 构造填充的参数
            Map<String, Object> map = BeanUtil.beanToMap(sysUser);
            String avatarBase64;
            if(ObjectUtil.isNotEmpty(sysUser.getAvatar())) {
                avatarBase64 = sysUser.getAvatar();
            } else {
                avatarBase64 = CommonAvatarUtil.generateImg(sysUser.getName());
            }
            // 头像
            ImageEntity imageEntity = new ImageEntity(ImgUtil.toBytes(ImgUtil.toImage(StrUtil
                    .split(avatarBase64, StrUtil.COMMA).get(1)), ImgUtil.IMAGE_TYPE_PNG), 120, 160);
            map.put("avatar", imageEntity);
            if(ObjectUtil.isNotEmpty(sysUser.getBirthday())) {
                try {
                    // 年龄
                    long age = DateUtil.betweenYear(DateUtil.parseDate(sysUser.getBirthday()), DateTime.now(), true);
                    if(age != 0) {
                        map.put("age", age + "岁");
                    }
                } catch (Exception ignored) {
                }
            }
            // 导出时间
            map.put("exportDateTime", DateUtil.format(DateTime.now(), DatePattern.CHINESE_DATE_PATTERN));
            // 生成doc
            XWPFDocument doc = WordExportUtil.exportWord07(destTemplateFile.getAbsolutePath(), map);
            // 生成临时导出文件
            resultFile = FileUtil.file(FileUtil.getTmpDir() + File.separator + "SNOWY系统B端用户信息_" + sysUser.getName() + ".docx");
            // 写入
            BufferedOutputStream outputStream = FileUtil.getOutputStream(resultFile);
            doc.write(outputStream);
            outputStream.close();
            // 下载
            CommonDownloadUtil.download(resultFile, response);
        } catch (Exception e) {
            log.error(">>> 导出用户个人信息失败：", e);
            CommonResponseUtil.renderError(response, "导出失败");
        } finally {
            // 删除临时文件
            if(ObjectUtil.isNotEmpty(destTemplateFile)) {
                FileUtil.del(destTemplateFile);
            }
            if(ObjectUtil.isNotEmpty(resultFile)) {
                FileUtil.del(resultFile);
            }
        }
    }

    @Override
    public List<SysUserPositionResult> loginPositionInfo(SysUserIdParam sysUserIdParam) {
        SysUser sysUser = this.queryEntity(sysUserIdParam.getId());
        List<SysUserPositionResult> sysUserPositionResultList = CollectionUtil.newArrayList();
        List<SysOrg> sysOrgList = sysOrgService.getAllOrgList();
        String primaryOrgId = sysUser.getOrgId();
        SysOrg primarySysOrg = sysOrgService.getById(sysOrgList, primaryOrgId);
        if (ObjectUtil.isEmpty(primarySysOrg)) {
            throw new CommonException("组织不存在，id值为：{}", primaryOrgId);
        }
        String primaryOrgName = primarySysOrg.getName();
        List<SysPosition> sysPositionList = sysPositionService.list();
        String primaryPositionId = sysUser.getPositionId();
        SysPosition primaryPosition = sysPositionService.getById(sysPositionList, primaryPositionId);
        if (ObjectUtil.isEmpty(primaryPosition)) {
            throw new CommonException("职位不存在，id值为：{}", primaryPositionId);
        }
        String primaryPositionName = primaryPosition.getName();
        SysUserPositionResult primarySysUserPositionResult = new SysUserPositionResult();
        primarySysUserPositionResult.setOrgId(primaryOrgId);
        primarySysUserPositionResult.setOrgName(primaryOrgName);
        primarySysUserPositionResult.setPositionId(primaryPositionId);
        primarySysUserPositionResult.setPositionName(primaryPositionName);
        primarySysUserPositionResult.setCategory(primarySysOrg.getCategory());
        primarySysUserPositionResult.setType("primary");
        sysUserPositionResultList.add(primarySysUserPositionResult);
        String positionJson = sysUser.getPositionJson();
        if (ObjectUtil.isNotEmpty(positionJson)) {
            JSONArray jsonArray = JSONUtil.parseArray(positionJson);
            if (ObjectUtil.isNotEmpty(jsonArray)) {
                jsonArray.forEach(obj -> {
                    JSONObject jsonObject = JSONUtil.parseObj(obj);
                    String slaveOrgId = jsonObject.getStr("orgId");
                    String slavePositionId = jsonObject.getStr("positionId");
                    if (ObjectUtil.hasEmpty(slaveOrgId, slavePositionId)) {
                        throw new CommonException("兼任职位数据不完整，用户id值为：{}", sysUserIdParam.getId());
                    }
                    SysOrg slaveSysOrg = sysOrgService.getById(sysOrgList, slaveOrgId);
                    if (ObjectUtil.isEmpty(slaveSysOrg)) {
                        throw new CommonException("组织不存在，id值为：{}", slaveSysOrg);
                    }
                    String slaveOrgName = slaveSysOrg.getName();

                    SysPosition slavePosition = sysPositionService.getById(sysPositionList, slavePositionId);
                    if (ObjectUtil.isEmpty(slavePosition)) {
                        throw new CommonException("职位不存在，id值为：{}", slavePositionId);
                    }
                    String slavePositionName = slavePosition.getName();
                    SysUserPositionResult slaveSysUserPositionResult = new SysUserPositionResult();
                    slaveSysUserPositionResult.setOrgId(slaveOrgId);
                    slaveSysUserPositionResult.setOrgName(slaveOrgName);
                    slaveSysUserPositionResult.setPositionId(slavePositionId);
                    slaveSysUserPositionResult.setPositionName(slavePositionName);
                    slaveSysUserPositionResult.setCategory(slaveSysOrg.getCategory());
                    slaveSysUserPositionResult.setType("slave");
                    sysUserPositionResultList.add(slaveSysUserPositionResult);
                });
            }
        }
        return sysUserPositionResultList;
    }

    @Override
    public Page<SysUser> getAllUserSelectorList() {
        return this.page(CommonPageRequest.defaultPage(), new LambdaQueryWrapper<SysUser>().select(SysUser::getId,
                SysUser::getAvatar, SysUser::getOrgId, SysUser::getPositionId, SysUser::getAccount, SysUser::getName,
                SysUser::getSortCode, SysUser::getGender, SysUser::getEntryDate).orderByAsc(SysUser::getSortCode));
    }

    /* ====用户部分所需要用到的选择器==== */

    @Override
    public List<Tree<String>> orgTreeSelector() {
        List<SysOrg> sysOrgList = sysOrgService.getAllOrgList();
        List<TreeNode<String>> treeNodeList = sysOrgList.stream().map(sysOrg ->
                        new TreeNode<>(sysOrg.getId(), sysOrg.getParentId(), sysOrg.getName(), sysOrg.getSortCode()))
                .collect(Collectors.toList());
        return TreeUtil.build(treeNodeList, "0");
    }

    @Override
    public Page<SysOrg> orgListSelector(SysUserSelectorOrgListParam sysUserSelectorOrgListParam) {
        LambdaQueryWrapper<SysOrg> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 查询部分字段
        lambdaQueryWrapper.select(SysOrg::getId, SysOrg::getParentId, SysOrg::getName,
                SysOrg::getCategory, SysOrg::getSortCode);
        if (ObjectUtil.isNotEmpty(sysUserSelectorOrgListParam.getParentId())) {
            lambdaQueryWrapper.eq(SysOrg::getParentId, sysUserSelectorOrgListParam.getParentId());
        }
        if (ObjectUtil.isNotEmpty(sysUserSelectorOrgListParam.getSearchKey())) {
            lambdaQueryWrapper.like(SysOrg::getName, sysUserSelectorOrgListParam.getSearchKey());
        }
        lambdaQueryWrapper.orderByAsc(SysOrg::getSortCode);
        return sysOrgService.page(CommonPageRequest.defaultPage(), lambdaQueryWrapper);
    }

    @Override
    public Page<SysPosition> positionSelector(SysUserSelectorPositionParam sysUserSelectorPositionParam) {
        LambdaQueryWrapper<SysPosition> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 查询部分字段
        lambdaQueryWrapper.select(SysPosition::getId, SysPosition::getOrgId, SysPosition::getName,
                SysPosition::getCategory, SysPosition::getSortCode);
        if (ObjectUtil.isNotEmpty(sysUserSelectorPositionParam.getOrgId())) {
            lambdaQueryWrapper.eq(SysPosition::getOrgId, sysUserSelectorPositionParam.getOrgId());
        }
        if (ObjectUtil.isNotEmpty(sysUserSelectorPositionParam.getSearchKey())) {
            lambdaQueryWrapper.like(SysPosition::getName, sysUserSelectorPositionParam.getSearchKey());
        }
        lambdaQueryWrapper.orderByAsc(SysPosition::getSortCode);
        return sysPositionService.page(CommonPageRequest.defaultPage(), lambdaQueryWrapper);
    }

    @Override
    public Page<SysRole> roleSelector(SysUserSelectorRoleParam sysUserSelectorRoleParam) {
        LambdaQueryWrapper<SysRole> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(SysRole::getId, SysRole::getOrgId, SysRole::getName,
                SysRole::getCategory, SysRole::getSortCode);
        if (ObjectUtil.isNotEmpty(sysUserSelectorRoleParam.getOrgId())) {
            lambdaQueryWrapper.eq(SysRole::getOrgId, sysUserSelectorRoleParam.getOrgId());
        }
        if (ObjectUtil.isNotEmpty(sysUserSelectorRoleParam.getCategory())) {
            lambdaQueryWrapper.eq(SysRole::getCategory, sysUserSelectorRoleParam.getCategory());
        }
        if (ObjectUtil.isNotEmpty(sysUserSelectorRoleParam.getSearchKey())) {
            lambdaQueryWrapper.like(SysRole::getName, sysUserSelectorRoleParam.getSearchKey());
        }
        lambdaQueryWrapper.orderByAsc(SysRole::getSortCode);
        return sysRoleService.page(CommonPageRequest.defaultPage(), lambdaQueryWrapper);
    }

    @Override
    public Page<SysUser> userSelector(SysUserSelectorUserParam sysUserSelectorUserParam) {
        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 只查询部分字段
        lambdaQueryWrapper.select(SysUser::getId, SysUser::getAvatar, SysUser::getOrgId, SysUser::getPositionId, SysUser::getAccount,
                SysUser::getName, SysUser::getSortCode, SysUser::getGender, SysUser::getEntryDate);
        // 如果查询条件为空，则直接查询
        if(ObjectUtil.isAllEmpty(sysUserSelectorUserParam.getOrgId(), sysUserSelectorUserParam.getSearchKey())) {
            return this.getAllUserSelectorList();
        } else {
            if (ObjectUtil.isNotEmpty(sysUserSelectorUserParam.getOrgId())) {
                // 如果组织id不为空，则查询该组织及其子组织下的所有人
                List<String> childOrgIdList = CollStreamUtil.toList(sysOrgService.getChildListById(sysOrgService
                        .getAllOrgList(), sysUserSelectorUserParam.getOrgId(), true), SysOrg::getId);
                if (ObjectUtil.isNotEmpty(childOrgIdList)) {
                    lambdaQueryWrapper.in(SysUser::getOrgId, childOrgIdList);
                } else {
                    return new Page<>();
                }
            }
            if (ObjectUtil.isNotEmpty(sysUserSelectorUserParam.getSearchKey())) {
                lambdaQueryWrapper.like(SysUser::getName, sysUserSelectorUserParam.getSearchKey());
            }
            lambdaQueryWrapper.orderByAsc(SysUser::getSortCode);
            return this.page(CommonPageRequest.defaultPage(), lambdaQueryWrapper);
        }
    }

    @Override
    public Page<SysUserMessageResult> loginMessagePage(SysUserMessagePageParam sysUserMessagePageParam) {
        Page<SysUserMessageResult> resultPage = new Page<>();
        Page<JSONObject> devMessagePage = devMessageApi.page(CollectionUtil.newArrayList(StpUtil.getLoginIdAsString()), sysUserMessagePageParam.getCategory());
        BeanUtil.copyProperties(devMessagePage, resultPage);
        return resultPage;
    }

    @Override
    public SysUserMessageDetailResult loginMessageDetail(SysUserMessageIdParam sysUserMessageIdParam) {
        return JSONUtil.toBean(devMessageApi.detail(sysUserMessageIdParam.getId()), SysUserMessageDetailResult.class);
    }

    @Override
    public List<SysOrg> getOrgListByIdList(SysUserIdListParam sysUserIdListParam) {
        if (ObjectUtil.isEmpty(sysUserIdListParam.getIdList())) {
            return CollectionUtil.newArrayList();
        }
        LambdaQueryWrapper<SysOrg> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 查询部分字段
        lambdaQueryWrapper.select(SysOrg::getId, SysOrg::getParentId, SysOrg::getName, SysOrg::getCategory, SysOrg::getSortCode)
                .in(SysOrg::getId, sysUserIdListParam.getIdList()).orderByAsc(SysOrg::getSortCode);
        return sysOrgService.list(lambdaQueryWrapper);
    }

    @Override
    public List<SysUser> getUserListByIdList(SysUserIdListParam sysUserIdListParam) {
        if (ObjectUtil.isEmpty(sysUserIdListParam.getIdList())) {
            return CollectionUtil.newArrayList();
        }
        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 只查询部分字段
        lambdaQueryWrapper.select(SysUser::getId, SysUser::getOrgId, SysUser::getAvatar, SysUser::getAccount, SysUser::getName,
                        SysUser::getSortCode)
                .in(SysUser::getId, sysUserIdListParam.getIdList()).orderByAsc(SysUser::getSortCode);
        return this.list(lambdaQueryWrapper);
    }

    @Override
    public List<SysPosition> getPositionListByIdList(SysUserIdListParam sysUserIdListParam) {
        if (ObjectUtil.isEmpty(sysUserIdListParam.getIdList())) {
            return CollectionUtil.newArrayList();
        }
        LambdaQueryWrapper<SysPosition> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 查询部分字段
        lambdaQueryWrapper.select(SysPosition::getId, SysPosition::getOrgId, SysPosition::getName,
                SysPosition::getCategory, SysPosition::getSortCode)
                .in(SysPosition::getId, sysUserIdListParam.getIdList()).orderByAsc(SysPosition::getSortCode);
        return sysPositionService.list(lambdaQueryWrapper);
    }

    @Override
    public List<SysRole> getRoleListByIdList(SysUserIdListParam sysUserIdListParam) {
        if (ObjectUtil.isEmpty(sysUserIdListParam.getIdList())) {
            return CollectionUtil.newArrayList();
        }
        LambdaQueryWrapper<SysRole> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 查询部分字段
        lambdaQueryWrapper.select(SysRole::getId, SysRole::getOrgId, SysRole::getName,
                SysRole::getCategory, SysRole::getSortCode)
                .in(SysRole::getId, sysUserIdListParam.getIdList()).orderByAsc(SysRole::getSortCode);
        return sysRoleService.list(lambdaQueryWrapper);
    }
}
