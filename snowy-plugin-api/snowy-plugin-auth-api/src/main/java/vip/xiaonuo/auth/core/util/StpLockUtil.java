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
package vip.xiaonuo.auth.core.util;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import vip.xiaonuo.auth.core.exception.AuthLockedException;

/**
 * B端锁屏工具类
 *
 * 锁屏状态存放于TokenSession中，即与token一一对应，锁定某台设备不会波及该账号的其他登录设备。
 * 除{@link #unlockByToken}外，其余方法均需在登录校验通过后调用，否则将抛出未登录异常。
 *
 * @author xuyuxiang
 * @date 2026/8/6 10:20
 **/
public class StpLockUtil {

    /** 锁屏状态在TokenSession中的存储键 */
    private static final String LOCK_STATUS_KEY = "screenLocked";

    private StpLockUtil() {
    }

    /**
     * 锁定当前会话
     *
     * @author xuyuxiang
     * @date 2026/8/6 10:20
     **/
    public static void lock() {
        StpUtil.getTokenSession().set(LOCK_STATUS_KEY, true);
    }

    /**
     * 解锁当前会话
     *
     * @author xuyuxiang
     * @date 2026/8/6 10:20
     **/
    public static void unlock() {
        SaSession tokenSession = StpUtil.getStpLogic().getTokenSession(false);
        if (ObjectUtil.isNotNull(tokenSession)) {
            tokenSession.delete(LOCK_STATUS_KEY);
        }
    }

    /**
     * 解锁指定token的会话，用于登录成功时清理复用token上残留的锁屏状态
     *
     * @author xuyuxiang
     * @date 2026/8/6 10:20
     **/
    public static void unlockByToken(String tokenValue) {
        SaSession tokenSession = StpUtil.getStpLogic().getTokenSessionByToken(tokenValue, false);
        if (ObjectUtil.isNotNull(tokenSession)) {
            tokenSession.delete(LOCK_STATUS_KEY);
        }
    }

    /**
     * 当前会话是否处于锁屏状态，未锁屏时不会创建TokenSession，避免每次请求校验产生额外的缓存写入
     *
     * @author xuyuxiang
     * @date 2026/8/6 10:20
     **/
    public static boolean isLocked() {
        SaSession tokenSession = StpUtil.getStpLogic().getTokenSession(false);
        if (ObjectUtil.isNull(tokenSession)) {
            return false;
        }
        return Convert.toBool(tokenSession.get(LOCK_STATUS_KEY), false);
    }

    /**
     * 校验当前会话未锁屏，已锁屏则抛出异常
     *
     * @author xuyuxiang
     * @date 2026/8/6 10:20
     **/
    public static void checkLock() {
        if (isLocked()) {
            throw new AuthLockedException();
        }
    }
}
