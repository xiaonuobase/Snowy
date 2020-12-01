package com.cn.xiaonuo.sys.modular.file.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.log.Log;
import com.cn.xiaonuo.core.context.requestno.RequestNoContext;
import com.cn.xiaonuo.core.exception.ServiceException;
import com.cn.xiaonuo.sys.modular.file.enums.SysFileInfoExceptionEnum;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
}
