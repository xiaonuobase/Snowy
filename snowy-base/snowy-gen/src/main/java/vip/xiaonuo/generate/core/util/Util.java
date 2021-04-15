/*
Copyright [2020] [https://www.xiaonuo.vip]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：

1.请不要删除和修改根目录下的LICENSE文件。
2.请不要删除和修改Snowy源码头部的版权声明。
3.请保留源码和相关描述文件的项目出处，作者声明等。
4.分发源码时候，请注明软件出处 https://gitee.com/xiaonuobase/snowy
5.在修改包名，模块名称，项目代码等时，请注明软件出处 https://gitee.com/xiaonuobase/snowy
6.若您的项目无法满足以上几点，可申请商业授权，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package vip.xiaonuo.generate.core.util;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.app.Velocity;
import vip.xiaonuo.core.consts.CommonConstant;
import vip.xiaonuo.core.context.constant.ConstantContext;
import vip.xiaonuo.core.enums.DbIdEnum;

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
        response.setHeader("Content-Disposition", "attachment; filename=\"Snowy.zip\"");
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
    private static int getIndex(String string, int i, String str) {
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

    /**
     * 获取数据库用户
     *
     * @author yubaoshan
     * @date 2021-03-11 18:37:00
     */
    public static String getDataBasename () {
        String dataUrl = ConstantContext.me().getStr(CommonConstant.DATABASE_URL_NAME);
        String driverName = ConstantContext.me().getStr(CommonConstant.DATABASE_DRIVER_NAME);
        if (driverName.contains(DbIdEnum.MYSQL.getCode())) {
            return dataUrl.substring(getIndex(dataUrl, 3, "/") + 1, dataUrl.indexOf("?"));
        } else if (driverName.contains(DbIdEnum.ORACLE.getCode())) {
            return ConstantContext.me().getStr(CommonConstant.DATABASE_USER_NAME);
        } else {
            return "";
        }
    }

}
