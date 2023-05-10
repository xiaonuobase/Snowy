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
package vip.xiaonuo.common.util;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;

import java.util.Date;

/**
 * 时间格式化工具类
 *
 * @author xuyuxiang
 * @date 2022/6/24 15:28
 **/
public class CommonTimeFormatUtil {

    private static final long ONE_MINUTE_SECONDS = 60;

    private static final int BEFORE_DAWN_HOUR = 6;

    private static final int MORNING_END_HOUR = 12;

    private static final int NOON_END_HOUR = 13;

    private static final int AFTERNOON_END_HOUR = 18;

    private static final int NIGHT_END_HOUR = 24;

    /**
     * 将日期格式化为仿微信的日期
     *
     * @author xuyuxiang
     * @date 2022/6/24 15:28
     **/
    public static String formatWxPastTime(Date date) {
        if (DateUtil.between(date, DateUtil.date(), DateUnit.SECOND, false) < 0) {
            //今天之后的时间显示年月日时分
            return DateUtil.format(date, DatePattern.NORM_DATETIME_MINUTE_PATTERN);
        } else {
            //如果是今年
            if (DateUtil.thisYear() == DateUtil.year(date)) {
                //如果是今天
                if (DateUtil.isSameDay(date, DateUtil.date())) {
                    //相差分钟数
                    long betweenMinute = DateUtil.between(date, DateUtil.date(), DateUnit.MINUTE);
                    //如果在1小时之内
                    if (betweenMinute < ONE_MINUTE_SECONDS) {
                        //一分钟之内，显示刚刚
                        if (betweenMinute < 1) {
                            return "刚刚";
                        } else {
                            //一分钟之外，显示xx分钟前
                            return betweenMinute + "分钟前";
                        }
                    } else {
                        //一小时之外，显示时分
                        return getTodayHour(date) + " " + DateUtil.format(date, "HH:mm");
                    }
                } else if (DateUtil.isSameDay(date, DateUtil.yesterday())) {
                    //如果是昨天，显示昨天时分
                    return "昨天 " + DateUtil.format(date, "HH:mm");
                } else if (isThisWeek(date)) {
                    //如果是本周
                    String weekday;
                    //获取是本周的第几天
                    int dayOfWeek = DateUtil.dayOfWeek(date) - 1;
                    switch (dayOfWeek) {
                        case 1:
                            weekday = "周一";
                            break;
                        case 2:
                            weekday = "周二";
                            break;
                        case 3:
                            weekday = "周三";
                            break;
                        case 4:
                            weekday = "周四";
                            break;
                        case 5:
                            weekday = "周五";
                            break;
                        case 6:
                            weekday = "周六";
                            break;
                        default:
                            weekday = "周日";
                            break;
                    }
                    //显示本周时分
                    return weekday + " " + DateUtil.format(date, "HH:mm");
                } else {
                    //否则显示月日时分
                    return DateUtil.format(date, "MM-dd HH:mm");
                }
            } else {
                //本年之外显示年月日时分
                return DateUtil.format(date, DatePattern.NORM_DATETIME_MINUTE_PATTERN);
            }
        }
    }

    /**
     * 将秒数格式化为天时分秒
     *
     * @author xuyuxiang
     * @date 2022/6/24 15:29
     **/
    public static String formatSeconds(long secondsParam) {
        String result;
        long days = secondsParam / ( ONE_MINUTE_SECONDS * ONE_MINUTE_SECONDS * NIGHT_END_HOUR);
        long hours = (secondsParam % ( ONE_MINUTE_SECONDS * ONE_MINUTE_SECONDS * NIGHT_END_HOUR)) / (ONE_MINUTE_SECONDS * ONE_MINUTE_SECONDS);
        long minutes = (secondsParam % ( ONE_MINUTE_SECONDS * ONE_MINUTE_SECONDS)) /ONE_MINUTE_SECONDS;
        long seconds = secondsParam % ONE_MINUTE_SECONDS;
        if(days > 0) {
            result = days + "天" + hours + "小时" + minutes + "分钟" + seconds + "秒";
        } else if(hours > 0) {
            result = hours + "小时" + minutes + "分钟" + seconds + "秒";
        } else if(minutes > 0) {
            result = minutes + "分钟" + seconds + "秒";
        } else {
            result = seconds + "秒";
        }
        return result;
    }

    /**
     * 判断日期是否是本周
     *
     * @param date 要判断的日期
     * @return boolean
     * @author xuyuxiang
     * @date 2020/8/6 12:10
     **/
    private static boolean isThisWeek(Date date) {
        //获取本周开始时间
        DateTime beginOfWeek = DateUtil.beginOfWeek(DateUtil.date());
        //获取与本周开始时间相差的天数
        long betweenBegin = DateUtil.between(date, beginOfWeek, DateUnit.DAY, false) + 1;
        //如果是同一天，或相差天数小于0，则是本周
        return DateUtil.isSameDay(date, beginOfWeek) || betweenBegin < 0;
    }

    /**
     * 根据今天日期获取早中晚
     *
     * @author xuyuxiang
     * @date 2020/8/6 14:42
     **/
    private static String getTodayHour(Date date) {
        String result = "";
        int hour = DateUtil.hour(date, true);
        if (hour >= 0 && hour <= BEFORE_DAWN_HOUR) {
            result = "凌晨";
        }
        if (hour > BEFORE_DAWN_HOUR && hour < MORNING_END_HOUR) {
            result = "上午";
        }
        if (hour == MORNING_END_HOUR) {
            result = "中午";
        }
        if (hour >= NOON_END_HOUR && hour <= AFTERNOON_END_HOUR) {
            result = "下午";
        }
        if (hour > AFTERNOON_END_HOUR && hour <= NIGHT_END_HOUR) {
            result = "晚上";
        }
        return result;
    }
}
