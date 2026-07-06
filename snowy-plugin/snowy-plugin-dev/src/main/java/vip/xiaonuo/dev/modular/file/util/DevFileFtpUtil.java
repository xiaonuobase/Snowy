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
package vip.xiaonuo.dev.modular.file.util;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.ftp.Ftp;
import cn.hutool.extra.ftp.FtpMode;
import cn.hutool.extra.spring.SpringUtil;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.dev.api.DevConfigApi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * FTP文件工具类
 *
 * @author lilang
 * @date 2026/06/30
 */
public class DevFileFtpUtil {

    private static final String SNOWY_FILE_FTP_HOST_KEY = "SNOWY_FILE_FTP_HOST";
    private static final String SNOWY_FILE_FTP_PORT_KEY = "SNOWY_FILE_FTP_PORT";
    private static final String SNOWY_FILE_FTP_USERNAME_KEY = "SNOWY_FILE_FTP_USERNAME";
    private static final String SNOWY_FILE_FTP_PASSWORD_KEY = "SNOWY_FILE_FTP_PASSWORD";
    private static final String SNOWY_FILE_FTP_BASE_PATH_KEY = "SNOWY_FILE_FTP_BASE_PATH";
    private static final String SNOWY_FILE_FTP_DEFAULT_BUCKET_NAME_KEY = "SNOWY_FILE_FTP_DEFAULT_BUCKET_NAME";

    private static Ftp initClient() {
        DevConfigApi devConfigApi = SpringUtil.getBean(DevConfigApi.class);
        String host = devConfigApi.getValueByKey(SNOWY_FILE_FTP_HOST_KEY);
        if (ObjectUtil.isEmpty(host)) {
            throw new CommonException("FTP文件操作客户端未正确配置：host为空");
        }
        String portStr = devConfigApi.getValueByKey(SNOWY_FILE_FTP_PORT_KEY);
        if (ObjectUtil.isEmpty(portStr)) {
            throw new CommonException("FTP文件操作客户端未正确配置：port为空");
        }
        Integer port;
        try {
            port = Integer.valueOf(portStr);
        } catch (Exception e) {
            throw new CommonException("FTP文件操作客户端未正确配置：port格式错误");
        }
        String username = devConfigApi.getValueByKey(SNOWY_FILE_FTP_USERNAME_KEY);
        if (ObjectUtil.isEmpty(username)) {
            throw new CommonException("FTP文件操作客户端未正确配置：username为空");
        }
        String password = devConfigApi.getValueByKey(SNOWY_FILE_FTP_PASSWORD_KEY);
        if (ObjectUtil.isEmpty(password)) {
            throw new CommonException("FTP文件操作客户端未正确配置：password为空");
        }
        try {
            return new Ftp(host, port, username, password, StandardCharsets.UTF_8, null, null, FtpMode.Passive);
        } catch (Exception e) {
            throw new CommonException("FTP文件操作客户端连接失败：{}", e.getMessage());
        }
    }

    public static String getDefaultBucketName() {
        DevConfigApi devConfigApi = SpringUtil.getBean(DevConfigApi.class);
        String defaultBucketName = devConfigApi.getValueByKey(SNOWY_FILE_FTP_DEFAULT_BUCKET_NAME_KEY);
        if (ObjectUtil.isEmpty(defaultBucketName)) {
            throw new CommonException("FTP文件操作客户端未正确配置：defaultBucketName为空");
        }
        return defaultBucketName;
    }

    public static void storageFile(String bucketName, String key, MultipartFile multipartFile) {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            storageFile(bucketName, key, inputStream);
        } catch (IOException e) {
            throw new CommonException("获取文件流异常，名称是：{}", multipartFile.getName());
        }
    }

    public static void storageFile(String bucketName, String key, InputStream inputStream) {
        Ftp ftp = initClient();
        try {
            String dir = getRemoteDir(bucketName, key);
            String fileName = StrUtil.subAfter(key, StrUtil.SLASH, true);
            boolean success = ftp.upload(dir, fileName, inputStream);
            if (!success) {
                throw new CommonException("FTP文件上传失败：{}", key);
            }
        } finally {
            IoUtil.close(ftp);
        }
    }

    public static String storageFileWithReturnUrl(String bucketName, String key, MultipartFile multipartFile) {
        storageFile(bucketName, key, multipartFile);
        return getFileAuthUrl(bucketName, key);
    }

    public static byte[] getFileBytes(String bucketName, String key) {
        Ftp ftp = initClient();
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            String dir = getRemoteDir(bucketName, key);
            String fileName = StrUtil.subAfter(key, StrUtil.SLASH, true);
            ftp.download(dir, fileName, outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new CommonException("FTP文件下载失败：{}", e.getMessage());
        } finally {
            IoUtil.close(ftp);
        }
    }

    public static String getFileAuthUrl(String bucketName, String key) {
        return "ftp://" + getHost() + normalizePath(StrUtil.SLASH + getBasePath() + StrUtil.SLASH + bucketName + StrUtil.SLASH + key);
    }

    public static void deleteFile(String bucketName, String key) {
        Ftp ftp = initClient();
        try {
            String path = normalizePath(getBasePath() + StrUtil.SLASH + bucketName + StrUtil.SLASH + key);
            if (!ftp.delFile(path)) {
                throw new CommonException("FTP文件删除失败：{}", path);
            }
        } finally {
            IoUtil.close(ftp);
        }
    }

    private static String getRemoteDir(String bucketName, String key) {
        String dir = StrUtil.subBefore(key, StrUtil.SLASH, true);
        return normalizePath(getBasePath() + StrUtil.SLASH + bucketName + StrUtil.SLASH + dir);
    }

    private static String getHost() {
        DevConfigApi devConfigApi = SpringUtil.getBean(DevConfigApi.class);
        return devConfigApi.getValueByKey(SNOWY_FILE_FTP_HOST_KEY);
    }

    private static String getBasePath() {
        DevConfigApi devConfigApi = SpringUtil.getBean(DevConfigApi.class);
        return ObjectUtil.defaultIfEmpty(devConfigApi.getValueByKey(SNOWY_FILE_FTP_BASE_PATH_KEY), StrUtil.EMPTY);
    }

    private static String normalizePath(String path) {
        String normalized = path.replace("\\", StrUtil.SLASH);
        while (normalized.contains("//")) {
            normalized = normalized.replace("//", StrUtil.SLASH);
        }
        return normalized;
    }
}
