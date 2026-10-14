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
package vip.xiaonuo.dev.api;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * 文件API接口，可参考vip.xiaonuo.dev.core.util.file包下的工具类扩展更多需要的方法
 *
 * @author xuyuxiang
 * @date 2022/6/22 15:21
 **/
public interface DevFileApi {

    /**
     * 动态上传文件返回id（使用系统配置的默认文件引擎）
     *
     * @author xuyuxiang
     * @date 2021/10/13 14:01
     **/
    String uploadDynamicReturnId(MultipartFile file);

    /**
     * 动态上传文件返回url（使用系统配置的默认文件引擎）
     *
     * @author xuyuxiang
     * @date 2021/10/13 14:01
     **/
    String uploadDynamicReturnUrl(MultipartFile file);

    /* =========本地文件========= */

    /**
     * 上传文件返回Url
     *
     * @param file 文件
     * @author xuyuxiang
     * @date 2022/6/22 17:44
     **/
    String storageFileWithReturnUrlLocal(MultipartFile file);

    /**
     * 上传文件返回Id
     *
     * @param file 文件
     * @author xuyuxiang
     * @date 2022/6/22 17:44
     **/
    String storageFileWithReturnIdLocal(MultipartFile file);

    /* =========阿里云文件========= */

    /**
     * 上传文件返回Url
     *
     * @param file 文件
     * @author xuyuxiang
     * @date 2022/6/22 17:44
     **/
    String storageFileWithReturnUrlAliyun(MultipartFile file);

    /**
     * 上传文件返回Id
     *
     * @param file 文件
     * @author xuyuxiang
     * @date 2022/6/22 17:44
     **/
    String storageFileWithReturnIdAliyun(MultipartFile file);

    /* =========腾讯云件========= */

    /**
     * 上传文件返回Url
     *
     * @param file 文件
     * @author xuyuxiang
     * @date 2022/6/22 17:44
     **/
    String storageFileWithReturnUrlTencent(MultipartFile file);

    /**
     * 上传文件返回Id
     *
     * @param file 文件
     * @author xuyuxiang
     * @date 2022/6/22 17:44
     **/
    String storageFileWithReturnIdTencent(MultipartFile file);

    /* =========MINIO件========= */

    /**
     * 上传文件返回Url
     *
     * @param file 文件
     * @author xuyuxiang
     * @date 2022/6/22 17:44
     **/
    String storageFileWithReturnUrlMinio(MultipartFile file);

    /**
     * 上传文件返回Id
     *
     * @param file 文件
     * @author xuyuxiang
     * @date 2022/6/22 17:44
     **/
    String storageFileWithReturnIdMinio(MultipartFile file);

    /* =========RUSTFS文件========= */

    /**
     * RUSTFS上传文件返回Url
     *
     * @author yubaoshan
     * @date 2026/10/13 10:00
     **/
    String storageFileWithReturnUrlRustfs(MultipartFile file);

    /**
     * RUSTFS上传文件返回Id
     *
     * @author yubaoshan
     * @date 2026/10/13 10:00
     **/
    String storageFileWithReturnIdRustfs(MultipartFile file);

    /* =========FTP文件========= */

    /**
     * 上传文件返回Url
     *
     * @param file 文件
     * @author lilang
     * @date 2026/06/30
     **/
    String storageFileWithReturnUrlFtp(MultipartFile file);

    /**
     * 上传文件返回Id
     *
     * @param file 文件
     * @author lilang
     * @date 2026/06/30
     **/
    String storageFileWithReturnIdFtp(MultipartFile file);

    /**
     * 通过文件id查询文件详情
     *
     * @author chengchuanyao
     * @date 2024/7/26 16:10
     */
    JSONObject getFileInfoById(String id);


    /**
     * 根据文件id物理删除文件
     *
     * @author xuyuxiang
     * @date 2022/8/4 10:36
     **/
    void deleteAbsoluteById(String id);

    /**
     * 根据文件ids获得文件列表
     *
     * @author yubaoshan
     * @date 2025/8/25 21:18
     */
    JSONArray getFileListByIds(List<String> ids);

    /**
     * 根据配置的存储类型，返回不同的URL
     *
     * @author yubaoshan
     * @date 2025/8/25 21:18
     */
    String storageFileWithReturnUrlOss(MultipartFile file);

    /**
     * 根据存储引擎、桶名和文件键读取文件字节数组
     *
     * @param engine 存储引擎（LOCAL/ALIYUN/TENCENT/MINIO/RUSTFS/FTP）
     * @param bucket 桶名
     * @param fileKey 文件键
     * @return 文件字节数组
     * @author xuyuxiang
     * @date 2026/09/17 22:00
     */
    byte[] getFileBytes(String engine, String bucket, String fileKey);

    /**
     * 根据存储引擎、桶名和文件键读取文件实际字节数，文件不存在返回null
     *
     * @author yubaoshan
     * @date 2026/10/13 23:50
     */
    Long getFileSize(String engine, String bucket, String fileKey);

    /**
     * 根据存储引擎、桶名和文件键把文件边读边写入输出流（不整读进内存，不关闭输出流）
     *
     * @author yubaoshan
     * @date 2026/10/13 23:50
     */
    void writeFileToStream(String engine, String bucket, String fileKey, OutputStream outputStream);

    /**
     * 登记对象存储中已存在的对象为底座文件（只登记不上传，暂只支持RUSTFS，桶为空取默认桶），下载需鉴权，返回文件id
     *
     * @author yubaoshan
     * @date 2026/10/13 14:00
     */
    String registerExistingFileReturnId(String engine, String bucket, String objectKey, String fileName, Long sizeBytes);

    /**
     * 按流存储文件（不整读进内存，引擎为空用系统默认引擎），下载需鉴权，返回文件id
     *
     * @author yubaoshan
     * @date 2026/10/13 14:00
     */
    String storageStreamReturnId(String engine, InputStream inputStream, String fileName, Long size);

    /**
     * 为没有缩略图的图片底座文件补生成缩略图（从存储读回后压缩），非图片、已有缩略图或文件过大时跳过
     *
     * @author yubaoshan
     * @date 2026/10/14 15:00
     */
    void generateThumbnail(String fileId);

    /**
     * 按内容复用已有存储对象登记一条新的底座文件记录（引擎为空用系统默认引擎），对象不在目标引擎或大小不符时返回null
     *
     * @author yubaoshan
     * @date 2026/10/14 16:00
     */
    String registerSharedFileReturnId(String sourceFileId, String targetEngine, String fileName, Long sizeBytes);
}
