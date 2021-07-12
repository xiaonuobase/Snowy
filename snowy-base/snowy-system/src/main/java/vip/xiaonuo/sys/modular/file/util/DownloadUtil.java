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
package vip.xiaonuo.sys.modular.file.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.log.Log;
import vip.xiaonuo.core.context.requestno.RequestNoContext;
import vip.xiaonuo.core.exception.ServiceException;
import vip.xiaonuo.sys.modular.file.enums.SysFileInfoExceptionEnum;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * 文件下载工具类
 *
 * @author xuyuxiang
 * @date 2020/8/5 21:45
 */
public class DownloadUtil {

    private static final Log log = Log.get();

    public static void download(String fileName, byte[] fileBytes, HttpServletResponse response) {
        try {
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=\"" + URLUtil.encode(fileName) + "\"");
            response.addHeader("Content-Length", "" + fileBytes.length);
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setContentType("application/octet-stream;charset=UTF-8");
            IoUtil.write(response.getOutputStream(), true, fileBytes);
        } catch (IOException e) {
            log.error(">>> 下载文件异常，请求号为：{}，具体信息为：{}", RequestNoContext.get(), e.getMessage());
            throw new ServiceException(SysFileInfoExceptionEnum.DOWNLOAD_FILE_ERROR);
        }
    }

    /**
     * 下载文件
     *
     * @param file     要下载的文件
     * @param response 响应
     * @author xuyuxiang
     * @date 2020/8/5 21:46
     */
    public static void download(File file, HttpServletResponse response) {
        // 获取文件字节
        byte[] fileBytes = FileUtil.readBytes(file);
        //获取文件名称
        String fileName;
        try {
            fileName = URLEncoder.encode(file.getName(), CharsetUtil.UTF_8);
        } catch (UnsupportedEncodingException e) {
            log.error(">>> 下载文件异常，请求号为：{}，具体信息为：{}", RequestNoContext.get(), e.getMessage());
            throw new ServiceException(SysFileInfoExceptionEnum.DOWNLOAD_FILE_ERROR);
        }
        //下载文件
        download(fileName, fileBytes, response);
    }

    /**
     * 将url的文件下载到目标文件
     *
     * @param url 下载url
     * @param file 目标文件
     * @author xuyuxiang
     * @date 2021/3/25 16:51
     */
    public static void downloadToFile(String url, File file) {
        if (url == null || url.isEmpty()) throw new ServiceException(SysFileInfoExceptionEnum.DOWNLOAD_FILE_ERROR);

        if (file == null) throw new ServiceException(SysFileInfoExceptionEnum.NOT_EXISTED_FILE);

        try {
            URL uri = new URL(url);
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) uri.openConnection();
            InputStream stream = connection.getInputStream();

            if (stream == null) {
                throw new ServiceException(SysFileInfoExceptionEnum.FILE_STREAM_ERROR);
            }
            FileUtil.writeFromStream(stream, file);
            connection.disconnect();
        } catch (Exception e) {
            log.error(">>> 下载文件异常，请求号为：{}，具体信息为：{}", RequestNoContext.get(), e.getMessage());
            throw new ServiceException(SysFileInfoExceptionEnum.DOWNLOAD_FILE_ERROR);
        }
    }
}
