package com.cn.xiaonuo.generate.core.tool;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间与String转换工具类
 *
 * @author yubaoshan
 * @date 2020-12-17 23:42
 */
public class StringDateTool {


    /**
     * 获取现在时间
     *
     * @author yubaoshan
     * @date 2020-12-17 23:42
     */
    public static String getStringDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }
}
