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
package vip.xiaonuo.core.file.modular.local;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import vip.xiaonuo.core.file.FileOperator;
import vip.xiaonuo.core.file.common.enums.BucketAuthEnum;
import vip.xiaonuo.core.file.common.exp.FileServiceException;
import vip.xiaonuo.core.file.modular.local.prop.LocalFileProperties;

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

    private Dict localClient;

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
        localClient = Dict.create();
        localClient.put("currentSavePath", currentSavePath);
        localClient.put("localFileProperties", localFileProperties);
    }

    @Override
    public void destroyClient() {
        // empty
    }

    @Override
    public Object getClient() {
        // empty
        return localClient;
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
