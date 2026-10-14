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
package vip.xiaonuo.dev.modular.file.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.dev.modular.file.entity.DevFile;
import vip.xiaonuo.dev.modular.file.param.DevFileDownloadParam;
import vip.xiaonuo.dev.modular.file.param.DevFileIdParam;
import vip.xiaonuo.dev.modular.file.param.DevFileListParam;
import vip.xiaonuo.dev.modular.file.param.DevFilePageParam;
import vip.xiaonuo.dev.modular.file.param.DevFileUrlListParam;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * 文件Service接口
 *
 * @author xuyuxiang
 * @date 2022/2/23 18:27
 **/
public interface DevFileService extends IService<DevFile> {

    /**
     * MultipartFile文件上传，返回文件id
     *
     * @author xuyuxiang
     * @date 2022/4/22 15:53
     **/
    String uploadReturnId(String engine, MultipartFile file);

    /**
     * MultipartFile文件上传，返回文件id（带后缀校验）
     *
     * @author xuyuxiang
     * @date 2025/6/26 15:53
     **/
    String uploadReturnIdWithValidation(String engine, MultipartFile file, List<String> allowedExtensions);

    /**
     * MultipartFile文件上传，返回文件Url
     *
     * @author xuyuxiang
     * @date 2022/4/22 15:53
     **/
    String uploadReturnUrl(String engine, MultipartFile file);

    /**
     * MultipartFile文件上传，返回文件Url（带后缀校验）
     *
     * @author xuyuxiang
     * @date 2025/6/26 15:53
     **/
    String uploadReturnUrlWithValidation(String engine, MultipartFile file, List<String> allowedExtensions);

    /**
     * 文件分页列表接口
     *
     * @author xuyuxiang
     * @date 2022/6/21 15:44
     **/
    Page<DevFile> page(DevFilePageParam devFilePageParam);

    /**
     * 文件列表接口
     *
     * @author xuyuxiang
     * @date 2022/6/21 15:44
     **/
    List<DevFile> list(DevFileListParam devFileListParam);

    /**
     * 下载文件
     *
     * @author xuyuxiang
     * @date 2022/6/21 15:44
     **/
    void download(DevFileDownloadParam devFileDownloadParam, HttpServletResponse response) throws IOException;


    /**
     * 授权下载文件
     *
     * @author xuyuxiang
     * @date 2022/6/21 15:44
     **/
    void authDownload(DevFileIdParam devFileIdParam, HttpServletResponse response) throws IOException;


    /**
     * 删除文件
     *
     * @author xuyuxiang
     * @date 2022/8/4 10:36
     **/
    void delete(List<DevFileIdParam> devFileIdParamList);

    /**
     * 物理删除文件
     *
     * @author xuyuxiang
     * @date 2022/8/4 10:36
     **/
    void deleteAbsolute(DevFileIdParam devFileIdParam);

    /**
     * 获取文件详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    DevFile detail(DevFileIdParam devFileIdParam);

    /**
     * 根据文件url集合获取文件集合
     *
     * @author yubaoshan
     * @date 2024/6/9 23:52
     **/
    List<DevFile> getFileListByUrlList(DevFileUrlListParam devFileUrlListParam);

    /**
     * 获取文件详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    DevFile queryEntity(String id);

    /**
     * 根据文件ids获得文件列表
     *
     * @author yubaoshan
     * @date 2025/8/25 21:18
     */
    List<DevFile> getFileListByIds(List<String> ids);

    /**
     * 登记对象存储中已存在的对象为底座文件（只登记不上传），校验桶、对象存在与大小，返回文件id
     *
     * @author yubaoshan
     * @date 2026/10/13 14:00
     */
    String registerExistingFile(String engine, String bucket, String objectKey, String fileName, Long sizeBytes);

    /**
     * 按流存储文件（不整读进内存），下载需鉴权，返回文件id
     *
     * @author yubaoshan
     * @date 2026/10/13 14:00
     */
    String uploadStreamReturnId(String engine, InputStream inputStream, String fileName, Long size);

    /**
     * 为没有缩略图的图片文件补生成缩略图，非图片、已有缩略图或文件过大时跳过
     *
     * @author yubaoshan
     * @date 2026/10/14 15:00
     */
    void generateThumbnail(String fileId);

    /**
     * 新建一条底座文件记录指向源记录的同一存储对象（名称用新的），源对象不在目标引擎或大小不符时返回null
     *
     * @author yubaoshan
     * @date 2026/10/14 16:00
     */
    String registerSharedFile(String sourceFileId, String targetEngine, String fileName, Long sizeBytes);

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
}
