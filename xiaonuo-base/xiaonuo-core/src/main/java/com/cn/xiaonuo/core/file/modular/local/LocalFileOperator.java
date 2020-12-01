package com.cn.xiaonuo.core.file.modular.local;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import com.cn.xiaonuo.core.file.FileOperator;
import com.cn.xiaonuo.core.file.common.enums.BucketAuthEnum;
import com.cn.xiaonuo.core.file.common.exp.FileServiceException;
import com.cn.xiaonuo.core.file.modular.local.prop.LocalFileProperties;

import java.io.File;
import java.io.InputStream;

/**
 * 阿里云文件操作
 *
 * @author xuyuxiang
 * @date 2020/5/25 2:33 下午
 */
public class LocalFileOperator implements FileOperator {

    private final LocalFileProperties localFileProperties;

    private String currentSavePath = "";

    public LocalFileOperator(LocalFileProperties localFileProperties) {
        this.localFileProperties = localFileProperties;
        initClient();
    }

    @Override
    public void initClient() {
        if (SystemUtil.getOsInfo().isWindows()) {
            String savePathWindows = localFileProperties.getLocalFileSavePathWin();
            if (!FileUtil.exist(savePathWindows)) {
                FileUtil.mkdir(savePathWindows);
            }
            currentSavePath = savePathWindows;
        } else {
            String savePathLinux = localFileProperties.getLocalFileSavePathLinux();
            if (!FileUtil.exist(savePathLinux)) {
                FileUtil.mkdir(savePathLinux);
            }
            currentSavePath = savePathLinux;
        }
    }

    @Override
    public void destroyClient() {
        // empty
    }

    @Override
    public Object getClient() {
        // empty
        return null;
    }

    @Override
    public boolean doesBucketExist(String bucketName) {
        String absolutePath = currentSavePath + File.separator + bucketName;
        return FileUtil.exist(absolutePath);
    }

    @Override
    public void setBucketAcl(String bucketName, BucketAuthEnum bucketAuthEnum) {
        // empty
    }

    @Override
    public boolean isExistingFile(String bucketName, String key) {
        String absoluteFile = currentSavePath + File.separator + bucketName + File.separator + key;
        return FileUtil.exist(absoluteFile);
    }

    @Override
    public void storageFile(String bucketName, String key, byte[] bytes) {

        // 判断bucket存在不存在
        String bucketPath = currentSavePath + File.separator + bucketName;
        if (!FileUtil.exist(bucketPath)) {
            FileUtil.mkdir(bucketPath);
        }

        // 存储文件
        String absoluteFile = currentSavePath + File.separator + bucketName + File.separator + key;
        FileUtil.writeBytes(bytes, absoluteFile);
    }

    @Override
    public void storageFile(String bucketName, String key, InputStream inputStream) {

        // 判断bucket存在不存在
        String bucketPath = currentSavePath + File.separator + bucketName;
        if (!FileUtil.exist(bucketPath)) {
            FileUtil.mkdir(bucketPath);
        }

        // 存储文件
        String absoluteFile = currentSavePath + File.separator + bucketName + File.separator + key;
        FileUtil.writeFromStream(inputStream, absoluteFile);
    }

    @Override
    public byte[] getFileBytes(String bucketName, String key) {

        // 判断文件存在不存在
        String absoluteFile = currentSavePath + File.separator + bucketName + File.separator + key;
        if (!FileUtil.exist(absoluteFile)) {
            String message = StrUtil.format("文件不存在,bucket={},key={}", bucketName, key);
            throw new FileServiceException(message);
        } else {
            return FileUtil.readBytes(absoluteFile);
        }
    }

    @Override
    public void setFileAcl(String bucketName, String key, BucketAuthEnum bucketAuthEnum) {
        // empty
    }

    @Override
    public void copyFile(String originBucketName, String originFileKey, String newBucketName, String newFileKey) {

        // 判断文件存在不存在
        String originFile = currentSavePath + File.separator + originBucketName + File.separator + originFileKey;
        if (!FileUtil.exist(originFile)) {
            String message = StrUtil.format("源文件不存在,bucket={},key={}", originBucketName, originFileKey);
            throw new FileServiceException(message);
        } else {

            // 拷贝文件
            String destFile = currentSavePath + File.separator + newBucketName + File.separator + newFileKey;
            FileUtil.copy(originFile, destFile, true);
        }
    }

    @Override
    public String getFileAuthUrl(String bucketName, String key, Long timeoutMillis) {
        // empty
        return null;
    }

    @Override
    public void deleteFile(String bucketName, String key) {

        // 判断文件存在不存在
        String file = currentSavePath + File.separator + bucketName + File.separator + key;
        if (!FileUtil.exist(file)) {
            return;
        }

        // 删除文件
        FileUtil.del(file);

    }
}
