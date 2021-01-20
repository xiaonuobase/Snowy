package com.cn.xiaonuo.generate.core.util;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.app.Velocity;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 代码生成工具类
 *
 * @author yubaoshan
 * @Date 2020年12月16日23:29:53
 */
public class Util {

    /**
     * 初始化vm
     *
     * @author yubaoshan
     * @Date 2020年12月16日23:29:53
     */
    public static void initVelocity() {
        Properties properties = new Properties();
        try {
            properties.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            properties.setProperty(Velocity.ENCODING_DEFAULT, "UTF-8");
            properties.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
            Velocity.init(properties);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 生成压缩包文件
     *
     * @author yubaoshan
     * @Date 2020年12月16日23:29:53
     */
    public static void DownloadGen(HttpServletResponse response, byte[] bytes) throws IOException {
        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\"xiaonuoVue.zip\"");
        response.addHeader("Content-Length", "" + bytes.length);
        response.setContentType("application/octet-stream; charset=UTF-8");
        IOUtils.write(bytes, response.getOutputStream());
    }

    /**
     * 查询某字符串第i次出现的游标
     *
     * @param string 字符串
     * @param i      第i次出现
     * @param str    子字符串
     * @author yubaoshan
     * @date 2020年12月16日23:29:53
     */
    public static int getIndex(String string, int i, String str) {
        Matcher slashMatcher = Pattern.compile(str).matcher(string);
        int mIdx = 0;
        while (slashMatcher.find()) {
            mIdx++;
            //当"/"符号第三次出现的位置
            if (mIdx == i) {
                break;
            }
        }
        return slashMatcher.start();
    }

}
