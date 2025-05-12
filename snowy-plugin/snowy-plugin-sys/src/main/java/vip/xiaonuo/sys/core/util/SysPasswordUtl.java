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
package vip.xiaonuo.sys.core.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.util.CommonCryptogramUtil;
import vip.xiaonuo.dev.api.DevConfigApi;
import vip.xiaonuo.dev.api.DevWeakPasswordApi;
import vip.xiaonuo.sys.core.enums.SysPasswordComplexityEnum;
import vip.xiaonuo.sys.modular.user.entity.SysUser;
import vip.xiaonuo.sys.modular.user.entity.SysUserExt;
import vip.xiaonuo.sys.modular.user.result.SysLoginUser;
import vip.xiaonuo.sys.modular.user.service.SysUserExtService;
import vip.xiaonuo.sys.modular.user.service.SysUserPasswordService;
import vip.xiaonuo.sys.modular.user.service.SysUserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统密码工具类
 *
 * @author xuyuxiang
 * @date 2025/3/21 19:07
 **/
public class SysPasswordUtl {

    /** B端系统默认密码 */
    private static final String SNOWY_SYS_DEFAULT_PASSWORD_FOR_B_KEY = "SNOWY_SYS_DEFAULT_PASSWORD_FOR_B";

    /** B端系统密码修改验证方式 */
    private static final String SNOWY_SYS_DEFAULT_PASSWORD_UPDATE_VALID_TYPE_FOR_B_KEY = "SNOWY_SYS_DEFAULT_PASSWORD_UPDATE_VALID_TYPE_FOR_B";

    /** B端密码最小长度 */
    private static final String SNOWY_SYS_DEFAULT_PASSWORD_MIN_LENGTH_FOR_B_KEY = "SNOWY_SYS_DEFAULT_PASSWORD_MIN_LENGTH_FOR_B";

    /** B端密码最大长度 */
    private static final String SNOWY_SYS_DEFAULT_PASSWORD_MAX_LENGTH_FOR_B_KEY = "SNOWY_SYS_DEFAULT_PASSWORD_MAX_LENGTH_FOR_B";

    /** B端密码复杂度 */
    private static final String SNOWY_SYS_DEFAULT_PASSWORD_COMPLEXITY_FOR_B_KEY = "SNOWY_SYS_DEFAULT_PASSWORD_COMPLEXITY_FOR_B";

    /** B端密码不能连续存在相同字符个数 */
    private static final String SNOWY_SYS_DEFAULT_PASSWORD_NOT_ALLOW_CONTINUOUS_SAME_CHARACTER_LENGTH_FOR_B_KEY= "SNOWY_SYS_DEFAULT_PASSWORD_NOT_ALLOW_CONTINUOUS_SAME_CHARACTER_LENGTH_FOR_B";

    /** B端密码不能包含用户信息开关（开启后，密码中将不能包含账号、手机号、邮箱前缀和姓名拼音） */
    private static final String SNOWY_SYS_DEFAULT_PASSWORD_NOT_ALLOW_CONTAINS_USER_INFO_FLAG_FOR_B_KEY = "SNOWY_SYS_DEFAULT_PASSWORD_NOT_ALLOW_CONTAINS_USER_INFO_FLAG_FOR_B";

    /** B端密码不能使用历史密码开关 */
    private static final String SNOWY_SYS_DEFAULT_PASSWORD_NOT_ALLOW_USE_HISTORY_FLAG_FOR_B_KEY = "SNOWY_SYS_DEFAULT_PASSWORD_NOT_ALLOW_USE_HISTORY_FLAG_FOR_B";

    /** B端密码不能使用历史密码范围个数 */
    private static final String SNOWY_SYS_DEFAULT_PASSWORD_NOT_ALLOW_USE_HISTORY_COUNT_FOR_B_KEY = "SNOWY_SYS_DEFAULT_PASSWORD_NOT_ALLOW_USE_HISTORY_COUNT_FOR_B";

    /** B端密码不能使用弱密码库中密码开关 */
    private static final String SNOWY_SYS_DEFAULT_PASSWORD_NOT_ALLOW_USE_WEAK_FLAG_FOR_B_KEY = "SNOWY_SYS_DEFAULT_PASSWORD_NOT_ALLOW_USE_WEAK_FLAG_FOR_B";

    /** B端密码自定义额外弱密码库 */
    private static final String SNOWY_SYS_DEFAULT_PASSWORD_DEFINE_WEAK_DATABASE_FOR_B_KEY = "SNOWY_SYS_DEFAULT_PASSWORD_DEFINE_WEAK_DATABASE_FOR_B";

    /** B端密码有效期天数 */
    private static final String SNOWY_SYS_DEFAULT_PASSWORD_EXPIRED_DAYS_FOR_B_KEY = "SNOWY_SYS_DEFAULT_PASSWORD_EXPIRED_DAYS_FOR_B";

    /** B端密码过期前提醒天数 */
    private static final String SNOWY_SYS_DEFAULT_PASSWORD_EXPIRED_NOTICE_DAYS_FOR_B_KEY = "SNOWY_SYS_DEFAULT_PASSWORD_EXPIRED_NOTICE_DAYS_FOR_B";

    /**
     * 获取系统默认密码
     *
     * @author xuyuxiang
     * @date 2025/3/21 19:08
     **/
    public static String getDefaultPassword() {
        DevConfigApi devConfigApi = SpringUtil.getBean(DevConfigApi.class);
        String defaultPassword = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_PASSWORD_FOR_B_KEY);
        if(ObjectUtil.isEmpty(defaultPassword)){
            throw new CommonException("请联系管理员配置系统默认密码");
        }
        return defaultPassword;
    }

    /**
     * 获取修改密码验证方式及配置
     *
     * @author xuyuxiang
     * @date 2025/3/21 19:08
     **/
    public static JSONObject getUpdatePasswordValidConfig() {
        JSONObject jsonObject = new JSONObject();
        DevConfigApi devConfigApi = SpringUtil.getBean(DevConfigApi.class);
        // 修改密码验证方式
        String passwordUpdateValidType = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_PASSWORD_UPDATE_VALID_TYPE_FOR_B_KEY);
        if(ObjectUtil.isEmpty(passwordUpdateValidType)){
            throw new CommonException("请联系管理员配置系统密码修改验证方式");
        }
        jsonObject.set(SNOWY_SYS_DEFAULT_PASSWORD_UPDATE_VALID_TYPE_FOR_B_KEY, passwordUpdateValidType);
        // 密码最小长度
        String passwordUpdateMinLength = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_PASSWORD_MIN_LENGTH_FOR_B_KEY);
        if(ObjectUtil.isEmpty(passwordUpdateMinLength)){
            throw new CommonException("请联系管理员配置系统密码修改最小长度");
        }
        jsonObject.set(SNOWY_SYS_DEFAULT_PASSWORD_MIN_LENGTH_FOR_B_KEY, passwordUpdateMinLength);
        // 密码最大长度
        String passwordUpdateMaxLength = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_PASSWORD_MAX_LENGTH_FOR_B_KEY);
        if(ObjectUtil.isEmpty(passwordUpdateMaxLength)){
            throw new CommonException("请联系管理员配置系统密码修改密码最大长度");
        }
        jsonObject.set(SNOWY_SYS_DEFAULT_PASSWORD_MAX_LENGTH_FOR_B_KEY, passwordUpdateMaxLength);
        // 密码复杂度
        String passwordUpdateComplexity = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_PASSWORD_COMPLEXITY_FOR_B_KEY);
        if(ObjectUtil.isEmpty(passwordUpdateValidType)){
            throw new CommonException("请联系管理员配置系统密码修改密码复杂度");
        }
        jsonObject.set(SNOWY_SYS_DEFAULT_PASSWORD_COMPLEXITY_FOR_B_KEY, passwordUpdateComplexity);
        // 不能连续出现相同字符个数
        String passwordUpdateNotAllowContinuousSame = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_PASSWORD_NOT_ALLOW_CONTINUOUS_SAME_CHARACTER_LENGTH_FOR_B_KEY);
        if(ObjectUtil.isEmpty(passwordUpdateValidType)){
            throw new CommonException("请联系管理员配置系统密码修改联系出现相同字符的个数");
        }
        jsonObject.set(SNOWY_SYS_DEFAULT_PASSWORD_NOT_ALLOW_CONTINUOUS_SAME_CHARACTER_LENGTH_FOR_B_KEY, passwordUpdateNotAllowContinuousSame);
        return jsonObject;
    }

    /**
     * 获取修改密码验证方式
     *
     * @author xuyuxiang
     * @date 2025/3/21 19:08
     **/
    public static String getUpdatePasswordValidType() {
        DevConfigApi devConfigApi = SpringUtil.getBean(DevConfigApi.class);
        String passwordUpdateValidType = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_PASSWORD_UPDATE_VALID_TYPE_FOR_B_KEY);
        if(ObjectUtil.isEmpty(passwordUpdateValidType)){
            throw new CommonException("请联系管理员配置系统密码修改验证方式");
        }
        return passwordUpdateValidType;
    }

    /**
     * 校验修改密码验证方式
     *
     * @author xuyuxiang
     * @date 2025/3/21 19:08
     **/
    public static void validUpdatePasswordValidType(String type) {
        String updatePasswordValidType = getUpdatePasswordValidType();
        if(!updatePasswordValidType.equals(type)){
            throw new CommonException("系统配置不支持此方式修改密码");
        }
    }

    /**
     * 校验新密码
     *
     * @author xuyuxiang
     * @date 2025/3/21 19:08
     **/
    public static void validNewPassword(SysLoginUser sysLoginUser, String newPassword) {
        String userId = sysLoginUser.getId();
        String account = sysLoginUser.getAccount();
        String name = sysLoginUser.getName();
        String phone = sysLoginUser.getPhone();
        String email = sysLoginUser.getEmail();
        validNewPassword(newPassword);
        validNewPassword(userId, account, name, phone, email, newPassword);
    }

    /**
     * 校验新密码
     *
     * @author xuyuxiang
     * @date 2025/3/21 19:08
     **/
    public static void validNewPassword(SysUser sysUser, String newPassword) {
        String userId = sysUser.getId();
        String account = sysUser.getAccount();
        String name = sysUser.getName();
        String phone = sysUser.getPhone();
        String email = sysUser.getEmail();
        validNewPassword(newPassword);
        validNewPassword(userId, account, name, phone, email, newPassword);
    }

    /**
     * 校验新密码（基本要求）
     *
     * @author xuyuxiang
     * @date 2025/3/21 19:08
     **/
    public static void validNewPassword(String newPassword) {
        DevConfigApi devConfigApi = SpringUtil.getBean(DevConfigApi.class);
        // 密码最小长度
        String passwordMinLength = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_PASSWORD_MIN_LENGTH_FOR_B_KEY);
        if(ObjectUtil.isNotEmpty(passwordMinLength)){
            Integer minLengthInt = Convert.toInt(passwordMinLength);
            if(newPassword.length() < minLengthInt){
                throw new CommonException("密码最小长度应为：{}", minLengthInt);
            }
        }
        // 密码最大长度
        String passwordMaxLength = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_PASSWORD_MAX_LENGTH_FOR_B_KEY);
        if(ObjectUtil.isNotEmpty(passwordMaxLength)){
            Integer maxLengthInt = Convert.toInt(passwordMaxLength);
            if(newPassword.length() > maxLengthInt){
                throw new CommonException("密码最大长度应为：{}", maxLengthInt);
            }
        }
        // 密码复杂度
        String passwordComplexity = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_PASSWORD_COMPLEXITY_FOR_B_KEY);
        if(ObjectUtil.isNotEmpty(passwordComplexity)){
            // 据复杂度配置判断
            boolean notLimit = SysPasswordComplexityEnum.REG0.getValue().equals(passwordComplexity);
            if(!notLimit){
                if(SysPasswordComplexityEnum.REG1.getValue().equals(passwordComplexity)) {
                    String reg = "^(?=.*[0-9])(?=.*[a-zA-Z]).+$";
                    if(!newPassword.matches(reg)) {
                        throw new CommonException("密码{}", SysPasswordComplexityEnum.REG1.getMessage());
                    }
                }
                if(SysPasswordComplexityEnum.REG2.getValue().equals(passwordComplexity)) {
                    String reg = "^(?=.*[0-9])(?=.*[A-Z]).+$";
                    if(!newPassword.matches(reg)) {
                        throw new CommonException("密码{}", SysPasswordComplexityEnum.REG2.getMessage());
                    }
                }
                if(SysPasswordComplexityEnum.REG3.getValue().equals(passwordComplexity)) {
                    String reg = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*[^a-zA-Z0-9]).+$";
                    if(!newPassword.matches(reg)) {
                        throw new CommonException("密码{}", SysPasswordComplexityEnum.REG3.getMessage());
                    }
                }
                if(SysPasswordComplexityEnum.REG4.getValue().equals(passwordComplexity)) {
                    String reg = "^(?:(?=.*[0-9])(?=.*[a-zA-Z])|(?=.*[0-9])(?=.*[^a-zA-Z0-9])|(?=.*[a-zA-Z])(?=.*[^a-zA-Z0-9])).+$";
                    if(!newPassword.matches(reg)) {
                        throw new CommonException("密码{}", SysPasswordComplexityEnum.REG4.getMessage());
                    }
                }
                if(SysPasswordComplexityEnum.REG5.getValue().equals(passwordComplexity)) {
                    String reg = "^(?:(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])|(?=.*[0-9])(?=.*[A-Z])(?=.*[^a-zA-Z0-9])|(?=.*[0-9])(?=.*[a-z])(?=.*[^a-zA-Z0-9])|(?=.*[A-Z])(?=.*[a-z])(?=.*[^a-zA-Z0-9])).+$";
                    if(!newPassword.matches(reg)) {
                        throw new CommonException("密码{}", SysPasswordComplexityEnum.REG5.getMessage());
                    }
                }
            }
        }
        // 密码不能连续存在相同字符个数
        String passwordNotAllowContinuousSameCharacterLength = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_PASSWORD_NOT_ALLOW_CONTINUOUS_SAME_CHARACTER_LENGTH_FOR_B_KEY);
        if(ObjectUtil.isNotEmpty(passwordNotAllowContinuousSameCharacterLength)){
            // 密码不能连续存在相同字符个数
            Integer passwordNotAllowContinuousSameCharacterLengthInt = Convert.toInt(passwordNotAllowContinuousSameCharacterLength);
            boolean hasConsecutiveChars = hasConsecutiveChars(newPassword, passwordNotAllowContinuousSameCharacterLengthInt);
            if(hasConsecutiveChars){
                throw new CommonException("密码不可存在" + passwordNotAllowContinuousSameCharacterLengthInt + "个连续相同的字符");
            }
        }
        // 密码不能使用弱密码库中密码开关
        String passwordNotAllowUseWeakFlag = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_PASSWORD_NOT_ALLOW_USE_WEAK_FLAG_FOR_B_KEY);
        if(ObjectUtil.isNotEmpty(passwordNotAllowUseWeakFlag)){
            if(Convert.toBool(passwordNotAllowUseWeakFlag)){
                // 不能包含内置弱密码和自定义额外弱密码库中密码
                DevWeakPasswordApi devWeakPasswordApi = SpringUtil.getBean(DevWeakPasswordApi.class);
                List<String> weakPasswordList = devWeakPasswordApi.weakPasswordList();
                // 获取自定义额外弱密码库
                String passwordDefineWeakDatabase = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_PASSWORD_DEFINE_WEAK_DATABASE_FOR_B_KEY);
                if(ObjectUtil.isNotEmpty(passwordDefineWeakDatabase)){
                    weakPasswordList.addAll(StrUtil.split(passwordDefineWeakDatabase, StrUtil.COMMA));
                }
                if(weakPasswordList.contains(newPassword)){
                    throw new CommonException("密码不可使用弱密码");
                }
            }
        }
    }

    /**
     * 校验新密码（含用户信息等）
     *
     * @author xuyuxiang
     * @date 2025/3/21 19:08
     **/
    public static void validNewPassword(String userId, String account, String name, String phone, String email, String newPassword) {
        DevConfigApi devConfigApi = SpringUtil.getBean(DevConfigApi.class);
        // 密码不能包含用户信息开关
        String passwordNotAllowContainsUserInfoFlag = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_PASSWORD_NOT_ALLOW_CONTAINS_USER_INFO_FLAG_FOR_B_KEY);
        if(ObjectUtil.isNotEmpty(passwordNotAllowContainsUserInfoFlag)){
            if(Convert.toBool(passwordNotAllowContainsUserInfoFlag)){
                // 账号，不能包含
                if(ObjectUtil.isNotEmpty(account) && newPassword.contains(account)){
                    throw new CommonException("密码不可包含账号");
                }
                // 姓名，不能包含拼音
                if(ObjectUtil.isNotEmpty(name) && newPassword.contains(PinyinUtil.getPinyin(name, ""))){
                    throw new CommonException("密码不可包含姓名拼音");
                }
                // 手机号，不能包含
                if(ObjectUtil.isNotEmpty(phone) && newPassword.contains(phone)){
                    throw new CommonException("密码不可包含手机号");
                }
                // 邮箱，不能包含前缀，即@字符之前内容
                if(ObjectUtil.isNotEmpty(email) && newPassword.contains(StrUtil.split(email, StrUtil.AT).get(0))){
                    throw new CommonException("密码不可包含邮箱前缀");
                }
            }
        }
        // 密码不能使用历史密码开关
        String passwordNotAllowUseHistoryFlag = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_PASSWORD_NOT_ALLOW_USE_HISTORY_FLAG_FOR_B_KEY);
        if(ObjectUtil.isNotEmpty(passwordNotAllowUseHistoryFlag)){
            if(Convert.toBool(passwordNotAllowUseHistoryFlag)){
                // 密码不能使用历史密码范围个数
                String passwordNotAllowContainsHistoryCount = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_PASSWORD_NOT_ALLOW_USE_HISTORY_COUNT_FOR_B_KEY);
                if(ObjectUtil.isNotEmpty(passwordNotAllowContainsHistoryCount)){
                    Integer passwordNotAllowContainsHistoryCountInt = Convert.toInt(passwordNotAllowContainsHistoryCount);
                    // 取前N次密码，进行判断
                    SysUserPasswordService sysUserPasswordService = SpringUtil.getBean(SysUserPasswordService.class);
                    List<String> userPasswordHistoryLimit = sysUserPasswordService.getUserPasswordHistoryLimit(userId, passwordNotAllowContainsHistoryCountInt);
                    if(ObjectUtil.isNotEmpty(userPasswordHistoryLimit)){
                        if(userPasswordHistoryLimit.contains(CommonCryptogramUtil.doHashValue(newPassword))) {
                            throw new CommonException("密码不可包含历史密码");
                        }
                    }
                }
            }
        }
    }

    /**
     * 判断当前用户密码是否过期
     *
     * @author xuyuxiang
     * @date 2025/3/21 19:08
     **/
    public static boolean isUserPasswordExpired(String userId) {
        // 先判断是否系统默认密码
        SysUserService sysUserService = SpringUtil.getBean(SysUserService.class);
        SysUser sysUser = sysUserService.queryEntity(userId);
        String defaultPassword = getDefaultPassword();
        // 若是，则认为已过期，需要修改
        if(CommonCryptogramUtil.doHashValue(defaultPassword).equals(sysUser.getPassword())){
            return true;
        }
        SysUserExtService sysUserExtService = SpringUtil.getBean(SysUserExtService.class);
        // 获取用户扩展信息
        SysUserExt sysUserExt = sysUserExtService.getOne(new LambdaQueryWrapper<SysUserExt>().eq(SysUserExt::getUserId, userId));
        if(ObjectUtil.isEmpty(sysUserExt)){
            // 无扩展信息的，直接认定为密码过期
            return true;
        } else {
            DevConfigApi devConfigApi = SpringUtil.getBean(DevConfigApi.class);
            // 获取密码有效期天数
            String passwordExpiredDays = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_PASSWORD_EXPIRED_DAYS_FOR_B_KEY);
            if(ObjectUtil.isEmpty(passwordExpiredDays)){
                throw new CommonException("请联系管理员配置密码有效期天数");
            } else {
                // 判断上次密码修改时间距今天的天数
                long betweenDays = DateUtil.between(sysUserExt.getPasswordUpdateTime(), DateTime.now(), DateUnit.DAY, false);
                // 如果距离今天已经超过了系统配置的有效期，则认为已过期
                return betweenDays >= Convert.toInt(passwordExpiredDays);
            }
        }
    }

    /**
     * 获取今日需要提醒密码到期的用户集合
     *
     * @author xuyuxiang
     * @date 2025/3/21 19:08
     **/
    public static List<SysUser> thisDayPasswordExpiredNeedNoticeUserIdList() {
        List<SysUser> resultUserList = new ArrayList<>();
        DevConfigApi devConfigApi = SpringUtil.getBean(DevConfigApi.class);
        // 获取密码有效期天数
        String passwordExpiredDays = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_PASSWORD_EXPIRED_DAYS_FOR_B_KEY);
        // 密码有效期配置如果不为空
        if(ObjectUtil.isNotEmpty(passwordExpiredDays)){
            // 获取密码过期前提醒天数
            String passwordExpiredNoticeDays = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_PASSWORD_EXPIRED_NOTICE_DAYS_FOR_B_KEY);
            // 密码过期前提醒天数如果不为空
            if(ObjectUtil.isNotEmpty(passwordExpiredDays)){
                // 获取密码修改日期距离提醒日的天数N
                int noticeOffsetDays = Convert.toInt(passwordExpiredDays) - Convert.toInt(passwordExpiredNoticeDays);
                // 提前提醒天数必须大于过期天数
                if(noticeOffsetDays > 0) {
                    // 今日需要提醒的，即密码修改日期为N天前的
                    DateTime passWordUpdateTime = DateUtil.offsetDay(DateTime.now(), -noticeOffsetDays);
                    // 获取当天的开始时间
                    DateTime beginDay = DateUtil.beginOfDay(passWordUpdateTime);
                    // 获取当天的结束时间
                    DateTime endDay = DateUtil.endOfDay(passWordUpdateTime);
                    // 获取SysUserExtService
                    SysUserExtService sysUserExtService = SpringUtil.getBean(SysUserExtService.class);
                    // 查询密码修改日期在该范围内的（无修改密码历史（即扩展信息为空）的无需提醒，会强制修改密码）
                    List<String> userIdList = sysUserExtService.list(new LambdaQueryWrapper<SysUserExt>()
                                    .between(SysUserExt::getPasswordUpdateTime,beginDay, endDay)).stream()
                            .map(SysUserExt::getUserId).collect(Collectors.toList());
                    if(ObjectUtil.isNotEmpty(userIdList)){
                        // 获取SysUserXService
                        SysUserService sysUserService = SpringUtil.getBean(SysUserService.class);
                        resultUserList = sysUserService.listByIds(userIdList);
                    }
                }
            }
        }
        return resultUserList;
    }

    /**
     * 判断字符串是否存在N个连续相同的字符
     *
     * @author xuyuxiang
     * @date 2025/3/21 19:08
     **/
    private static boolean hasConsecutiveChars(String str, int n) {
        // 处理无效输入：字符串为空、n非正、或字符串长度不足
        if (str == null || n <= 0 || str.length() < n) {
            return false;
        }
        // 特殊情况：n=1时只要字符串非空即存在连续1个字符
        if (n == 1) {
            return true;
        }

        int maxStartIndex = str.length() - n;
        for (int i = 0; i <= maxStartIndex; i++) {
            char currentChar = str.charAt(i);
            boolean isConsecutive = true;
            // 检查后续n-1个字符是否相同
            for (int j = 1; j < n; j++) {
                if (str.charAt(i + j) != currentChar) {
                    isConsecutive = false;
                    break;
                }
            }
            if (isConsecutive) {
                return true;
            }
        }
        return false;
    }
}
