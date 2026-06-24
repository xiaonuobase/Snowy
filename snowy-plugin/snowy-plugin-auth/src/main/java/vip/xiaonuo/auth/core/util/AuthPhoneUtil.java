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

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.PhoneUtil;

/**
 * 手机号工具类
 *
 * @author xuyuxiang
 * @date 2024/12/30 00:14
 **/
public class AuthPhoneUtil {

    private static final String[] IPPFXS4 = { "1790", "1791", "1793", "1795",
            "1796", "1797", "1799" };
    private static final String[] IPPFXS5 = { "12583", "12593", "12589",
            "12520", "10193", "11808" };
    private static final String[] IPPFXS6 = { "118321" };

    public static void main(String[] args) {
        //测试数据
        String telNum = "+8618611503575";
        System.out.println("before trim telNum=" + telNum);
        telNum = trimTelNum(telNum);
        System.out.println("trimTelNum telNum=" + telNum);

        telNum = "008618611503575";
        System.out.println("before trim telNum=" + telNum);
        telNum = trimTelNum(telNum);
        System.out.println("trimTelNum telNum=" + telNum);

        telNum = "17951+8618211503458";
        System.out.println("before trim telNum=" + telNum);
        telNum = trimTelNum(telNum);
        System.out.println("trimTelNum telNum=" + telNum);

        telNum = "1795818211503458";
        System.out.println("before trim telNum=" + telNum);
        telNum = trimTelNum(telNum);
        System.out.println("trimTelNum telNum=" + telNum);

        telNum = "1252015611503575";
        System.out.println("before trim telNum=" + telNum);
        telNum = trimTelNum(telNum);
        System.out.println("trimTelNum telNum=" + telNum);

        telNum = "11832115611503575";
        System.out.println("before trim telNum=" + telNum);
        telNum = trimTelNum(telNum);
        System.out.println("trimTelNum telNum=" + telNum);

        telNum = "118321+8615611503575";
        System.out.println("before trim telNum=" + telNum);
        telNum = trimTelNum(telNum);
        System.out.println("trimTelNum telNum=" + telNum);

        telNum = "1644632132@dasd.cindasdas";
        System.out.println("before trim telNum=" + telNum);
        telNum = trimTelNum(telNum);
        System.out.println("trimTelNum telNum=" + telNum);

        boolean mobile = PhoneUtil.isMobile("+86 15228937093");
        System.out.println(mobile);
    }

    /**
     * 消除电话号码中 可能含有的 IP号码、+86、0086等前缀
     *
     * @author xuyuxiang
     * @date 2024/12/30 00:15
     **/
    public static String trimTelNum(String telNum) {

        if (ObjectUtil.isEmpty(telNum)) {
            return null;
        }

        String ippfx6 = substring(telNum,0, 6);
        String ippfx5 = substring(telNum,0, 5);
        String ippfx4 = substring(telNum,0, 4);

        if (telNum.length() > 7
                && (substring(telNum, 5, 1).equals("0")
                || substring(telNum, 5, 1).equals("1") || substring(
                telNum, 5, 3).equals("400") || substring(
                telNum, 5, 3).equals("+86"))
                && (inArray(ippfx5, IPPFXS5) || inArray(ippfx4, IPPFXS4)))
            telNum = substring(telNum, 5);
        else if (telNum.length() > 8
                && (substring(telNum, 6, 1).equals("0")
                || substring(telNum, 6, 1).equals("1") || substring(
                telNum, 6, 3).equals("400") || substring(
                telNum, 6, 3).equals("+86"))
                && inArray(ippfx6, IPPFXS6))
            telNum = substring(telNum, 6);
        // remove ip dial

        telNum = telNum.replace("-", "");
        telNum = telNum.replace(" ", "");

        if (substring(telNum, 0, 4).equals("0086"))
            telNum = substring(telNum, 4);
        else if (substring(telNum, 0, 3).equals("+86"))
            telNum = substring(telNum, 3);
        else if (substring(telNum, 0, 5).equals("00186"))
            telNum = substring(telNum, 5);

        return telNum;
    }

    /**
     * 截取字符串
     *
     * @author xuyuxiang
     * @date 2024/12/30 00:15
     **/
    protected static String substring(String s, int from) {
        try {
            return s.substring(from);
        } catch (Exception ignored) {
        }
        return "";
    }

    /**
     * 截取字符串
     *
     * @author xuyuxiang
     * @date 2024/12/30 00:15
     **/
    protected static String substring(String s, int from, int len) {
        try {
            return s.substring(from, from + len);
        } catch (Exception ignored) {
        }
        return "";
    }

    /**
     * 判断一个字符串是否在一个字符串数组中
     *
     * @author xuyuxiang
     * @date 2024/12/30 00:15
     **/
    protected static boolean inArray(String target, String[] arr) {
        if (ObjectUtil.isEmpty(arr)) {
            return false;
        }
        if (target == null) {
            return false;
        }
        for (String s : arr) {
            if (target.equals(s)) {
                return true;
            }
        }
        return false;
    }
}
