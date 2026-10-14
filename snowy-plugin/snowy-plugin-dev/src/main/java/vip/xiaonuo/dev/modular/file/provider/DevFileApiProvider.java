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
package vip.xiaonuo.dev.modular.file.provider;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.dev.api.DevConfigApi;
import vip.xiaonuo.dev.api.DevFileApi;
import vip.xiaonuo.dev.modular.file.enums.DevFileEngineTypeEnum;
import vip.xiaonuo.dev.modular.file.param.DevFileIdParam;
import vip.xiaonuo.dev.modular.file.service.DevFileService;
import vip.xiaonuo.dev.modular.file.util.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 文件API接口提供者
 *
 * @author xuyuxiang
 * @date 2022/6/22 15:32
 **/
@Service
public class DevFileApiProvider implements DevFileApi {

    /** 默认文件引擎 */
    private static final String SNOWY_SYS_DEFAULT_FILE_ENGINE_KEY = "SNOWY_SYS_DEFAULT_FILE_ENGINE";

    @Resource
    private DevConfigApi devConfigApi;

    @Resource
    private DevFileService devFileService;

    @Override
    public String uploadDynamicReturnId(MultipartFile file) {
        return devFileService.uploadReturnId(devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_FILE_ENGINE_KEY), file);
    }

    @Override
    public String uploadDynamicReturnUrl(MultipartFile file) {
        return devFileService.uploadReturnUrl(devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_FILE_ENGINE_KEY), file);
    }

    @Override
    public String storageFileWithReturnUrlLocal(MultipartFile file) {
        return devFileService.uploadReturnUrl(DevFileEngineTypeEnum.LOCAL.getValue(), file);
    }

    @Override
    public String storageFileWithReturnIdLocal(MultipartFile file) {
        return devFileService.uploadReturnId(DevFileEngineTypeEnum.LOCAL.getValue(), file);
    }

    @Override
    public String storageFileWithReturnUrlAliyun(MultipartFile file) {
        return devFileService.uploadReturnUrl(DevFileEngineTypeEnum.ALIYUN.getValue(), file);
    }

    @Override
    public String storageFileWithReturnIdAliyun(MultipartFile file) {
        return devFileService.uploadReturnId(DevFileEngineTypeEnum.ALIYUN.getValue(), file);
    }

    @Override
    public String storageFileWithReturnUrlTencent(MultipartFile file) {
        return devFileService.uploadReturnUrl(DevFileEngineTypeEnum.TENCENT.getValue(), file);
    }

    @Override
    public String storageFileWithReturnIdTencent(MultipartFile file) {
        return devFileService.uploadReturnId(DevFileEngineTypeEnum.TENCENT.getValue(), file);
    }

    @Override
    public String storageFileWithReturnUrlMinio(MultipartFile file) {
        return devFileService.uploadReturnUrl(DevFileEngineTypeEnum.MINIO.getValue(), file);
    }

    @Override
    public String storageFileWithReturnIdMinio(MultipartFile file) {
        return devFileService.uploadReturnId(DevFileEngineTypeEnum.MINIO.getValue(), file);
    }

    @Override
    public String storageFileWithReturnUrlRustfs(MultipartFile file) {
        return devFileService.uploadReturnUrl(DevFileEngineTypeEnum.RUSTFS.getValue(), file);
    }

    @Override
    public String storageFileWithReturnIdRustfs(MultipartFile file) {
        return devFileService.uploadReturnId(DevFileEngineTypeEnum.RUSTFS.getValue(), file);
    }

    @Override
    public String storageFileWithReturnUrlFtp(MultipartFile file) {
        return devFileService.uploadReturnUrl(DevFileEngineTypeEnum.FTP.getValue(), file);
    }

    @Override
    public String storageFileWithReturnIdFtp(MultipartFile file) {
        return devFileService.uploadReturnId(DevFileEngineTypeEnum.FTP.getValue(), file);
    }

    @Override
    public JSONObject getFileInfoById(String id) {
        return Optional.ofNullable(devFileService.getById(id))
                .map(JSONUtil::parseObj)
                .orElse(new JSONObject());
    }

    @Override
    public void deleteAbsoluteById(String id) {
        DevFileIdParam devFileIdParam = new DevFileIdParam();
        devFileIdParam.setId(id);
        devFileService.deleteAbsolute(devFileIdParam);
    }

    @Override
    public JSONArray getFileListByIds(List<String> ids) {
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(devFileService.getFileListByIds(ids));
        return jsonArray;
    }

    @Override
    public String storageFileWithReturnUrlOss(MultipartFile file) {
        // 获取系统配置的默认文件存储引擎
        String defaultEngine = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_FILE_ENGINE_KEY);
        // 如果配置为空，默认使用本地存储
        if (ObjectUtil.isEmpty(defaultEngine)) {
            defaultEngine = DevFileEngineTypeEnum.LOCAL.getValue();
        }
        // 根据配置的引擎类型调用相应的存储方法
        return devFileService.uploadReturnUrl(defaultEngine, file);
    }

    @Override
    public byte[] getFileBytes(String engine, String bucket, String fileKey) {
        if (StrUtil.isEmpty(engine) || StrUtil.isEmpty(bucket) || StrUtil.isEmpty(fileKey)) {
            throw new CommonException("文件存储信息不完整");
        }
        // 按引擎值匹配存储引擎枚举，忽略大小写
        DevFileEngineTypeEnum engineType = Arrays.stream(DevFileEngineTypeEnum.values())
                .filter(item -> item.getValue().equalsIgnoreCase(engine))
                .findFirst()
                .orElseThrow(() -> new CommonException("不支持的存储引擎：{}", engine));

        try {
            switch (engineType) {
                case LOCAL:
                    return DevFileLocalUtil.getFileBytes(bucket, fileKey);
                case ALIYUN:
                    return DevFileAliyunUtil.getFileBytes(bucket, fileKey);
                case TENCENT:
                    return DevFileTencentUtil.getFileBytes(bucket, fileKey);
                case MINIO:
                    return DevFileMinIoUtil.getFileBytes(bucket, fileKey);
                case RUSTFS:
                    return DevFileRustFsUtil.getFileBytes(bucket, fileKey);
                case FTP:
                    return DevFileFtpUtil.getFileBytes(bucket, fileKey);
                default:
                    throw new CommonException("不支持的存储引擎：{}", engine);
            }
        } catch (Exception e) {
            throw new CommonException("读取文件失败：{}", e.getMessage());
        }
    }

    @Override
    public Long getFileSize(String engine, String bucket, String fileKey) {
        return devFileService.getFileSize(engine, bucket, fileKey);
    }

    @Override
    public void writeFileToStream(String engine, String bucket, String fileKey, OutputStream outputStream) {
        devFileService.writeFileToStream(engine, bucket, fileKey, outputStream);
    }

    @Override
    public String registerExistingFileReturnId(String engine, String bucket, String objectKey, String fileName, Long sizeBytes) {
        return devFileService.registerExistingFile(engine, bucket, objectKey, fileName, sizeBytes);
    }

    @Override
    public String storageStreamReturnId(String engine, InputStream inputStream, String fileName, Long size) {
        // 引擎为空时取系统配置的默认引擎，与动态上传口径一致
        String targetEngine = ObjectUtil.isNotEmpty(engine) ? engine : devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_FILE_ENGINE_KEY);
        return devFileService.uploadStreamReturnId(targetEngine, inputStream, fileName, size);
    }

    @Override
    public void generateThumbnail(String fileId) {
        devFileService.generateThumbnail(fileId);
    }

    @Override
    public String registerSharedFileReturnId(String sourceFileId, String targetEngine, String fileName, Long sizeBytes) {
        // 目标引擎为空时取系统配置的默认引擎，与流式存储口径一致
        String engine = ObjectUtil.isNotEmpty(targetEngine) ? targetEngine : devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_FILE_ENGINE_KEY);
        return devFileService.registerSharedFile(sourceFileId, engine, fileName, sizeBytes);
    }
}
