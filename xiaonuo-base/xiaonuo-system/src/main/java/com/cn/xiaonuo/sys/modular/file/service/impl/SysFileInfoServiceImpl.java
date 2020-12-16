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

XiaoNuo采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：

1.请不要删除和修改根目录下的LICENSE文件。
2.请不要删除和修改XiaoNuo源码头部的版权声明。
3.请保留源码和相关描述文件的项目出处，作者声明等。
4.分发源码时候，请注明软件出处 https://gitee.com/xiaonuobase/xiaonuo-vue
5.在修改包名，模块名称，项目代码等时，请注明软件出处 https://gitee.com/xiaonuobase/xiaonuo-vue
6.若您的项目无法满足以上几点，可申请商业授权，获取XiaoNuo商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package com.cn.xiaonuo.sys.modular.file.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cn.xiaonuo.core.consts.SymbolConstant;
import com.cn.xiaonuo.core.context.requestno.RequestNoContext;
import com.cn.xiaonuo.core.exception.LibreOfficeException;
import com.cn.xiaonuo.core.exception.ServiceException;
import com.cn.xiaonuo.core.factory.PageFactory;
import com.cn.xiaonuo.core.file.FileOperator;
import com.cn.xiaonuo.core.pojo.page.PageResult;
import com.cn.xiaonuo.core.util.LibreOfficeUtil;
import com.cn.xiaonuo.sys.modular.file.entity.SysFileInfo;
import com.cn.xiaonuo.sys.modular.file.enums.FileLocationEnum;
import com.cn.xiaonuo.sys.modular.file.enums.SysFileInfoExceptionEnum;
import com.cn.xiaonuo.sys.modular.file.mapper.SysFileInfoMapper;
import com.cn.xiaonuo.sys.modular.file.param.SysFileInfoParam;
import com.cn.xiaonuo.sys.modular.file.result.SysFileInfoResult;
import com.cn.xiaonuo.sys.modular.file.service.SysFileInfoService;
import com.cn.xiaonuo.sys.modular.file.util.DownloadUtil;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

import static com.cn.xiaonuo.sys.config.FileConfig.DEFAULT_BUCKET;

/**
 * 文件信息表 服务实现类
 *
 * @author yubaoshan
 * @date 2020/6/7 22:15
 */
@Service
public class SysFileInfoServiceImpl extends ServiceImpl<SysFileInfoMapper, SysFileInfo> implements SysFileInfoService {

    private static final Log log = Log.get();

    @Resource
    private FileOperator fileOperator;

    @Override
    public PageResult<SysFileInfo> page(SysFileInfoParam sysFileInfoParam) {

        // 构造条件
        LambdaQueryWrapper<SysFileInfo> queryWrapper = new LambdaQueryWrapper<>();

        // 拼接查询条件-文件存储位置（1:阿里云，2:腾讯云，3:minio，4:本地）
        if (ObjectUtil.isNotNull(sysFileInfoParam)) {
            if (ObjectUtil.isNotEmpty(sysFileInfoParam.getFileLocation())) {
                queryWrapper.like(SysFileInfo::getFileLocation, sysFileInfoParam.getFileLocation());
            }

            // 拼接查询条件-文件仓库
            if (ObjectUtil.isNotEmpty(sysFileInfoParam.getFileBucket())) {
                queryWrapper.like(SysFileInfo::getFileBucket, sysFileInfoParam.getFileBucket());
            }

            // 拼接查询条件-文件名称（上传时候的文件名）
            if (ObjectUtil.isNotEmpty(sysFileInfoParam.getFileOriginName())) {
                queryWrapper.like(SysFileInfo::getFileOriginName, sysFileInfoParam.getFileOriginName());
            }
        }

        // 查询分页结果
        return new PageResult<>(this.page(PageFactory.defaultPage(), queryWrapper));
    }

    @Override
    public List<SysFileInfo> list(SysFileInfoParam sysFileInfoParam) {

        // 构造条件
        LambdaQueryWrapper<SysFileInfo> queryWrapper = new LambdaQueryWrapper<>();

        return this.list(queryWrapper);
    }

    @Override
    public void add(SysFileInfoParam sysFileInfoParam) {

        // 将dto转为实体
        SysFileInfo sysFileInfo = new SysFileInfo();
        BeanUtil.copyProperties(sysFileInfoParam, sysFileInfo);

        this.save(sysFileInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(SysFileInfoParam sysFileInfoParam) {

        // 查询文件的信息
        SysFileInfo sysFileInfo = this.getById(sysFileInfoParam.getId());

        // 删除文件记录
        this.removeById(sysFileInfoParam.getId());

        // 删除具体文件
        this.fileOperator.deleteFile(sysFileInfo.getFileBucket(), sysFileInfo.getFileObjectName());
    }

    @Override
    public void edit(SysFileInfoParam sysFileInfoParam) {

        // 根据id查询实体
        SysFileInfo sysFileInfo = this.querySysFileInfo(sysFileInfoParam);

        // 请求参数转化为实体
        BeanUtil.copyProperties(sysFileInfoParam, sysFileInfo);

        this.updateById(sysFileInfo);
    }

    @Override
    public SysFileInfo detail(SysFileInfoParam sysFileInfoParam) {
        return this.querySysFileInfo(sysFileInfoParam);
    }

    @Override
    public Long uploadFile(MultipartFile file) {

        // 生成文件的唯一id
        Long fileId = IdWorker.getId();

        // 获取文件原始名称
        String originalFilename = file.getOriginalFilename();

        // 获取文件后缀
        String fileSuffix = null;

        if (ObjectUtil.isNotEmpty(originalFilename)) {
            fileSuffix = StrUtil.subAfter(originalFilename, SymbolConstant.PERIOD, true);
        }
        // 生成文件的最终名称
        String finalName = fileId + SymbolConstant.PERIOD + fileSuffix;

        // 存储文件
        byte[] bytes;
        try {
            bytes = file.getBytes();
            fileOperator.storageFile(DEFAULT_BUCKET, finalName, bytes);
        } catch (IOException e) {
            throw new ServiceException(SysFileInfoExceptionEnum.ERROR_FILE);
        }

        // 计算文件大小kb
        long fileSizeKb = Convert.toLong(NumberUtil.div(new BigDecimal(file.getSize()), BigDecimal.valueOf(1024))
                .setScale(0, BigDecimal.ROUND_HALF_UP));

        //计算文件大小信息
        String fileSizeInfo = FileUtil.readableFileSize(file.getSize());

        // 存储文件信息
        SysFileInfo sysFileInfo = new SysFileInfo();
        sysFileInfo.setId(fileId);
        sysFileInfo.setFileLocation(FileLocationEnum.LOCAL.getCode());
        sysFileInfo.setFileBucket(DEFAULT_BUCKET);
        sysFileInfo.setFileObjectName(finalName);
        sysFileInfo.setFileOriginName(originalFilename);
        sysFileInfo.setFileSuffix(fileSuffix);
        sysFileInfo.setFileSizeKb(fileSizeKb);
        sysFileInfo.setFileSizeInfo(fileSizeInfo);
        this.save(sysFileInfo);

        // 返回文件id
        return fileId;
    }

    @Override
    public SysFileInfoResult getFileInfoResult(Long fileId) {
        byte[] fileBytes;
        // 获取文件名
        SysFileInfo sysFileInfo = this.getById(fileId);
        if (sysFileInfo == null) {
            throw new ServiceException(SysFileInfoExceptionEnum.NOT_EXISTED_FILE);
        }
        try {
            // 返回文件字节码
            fileBytes = fileOperator.getFileBytes(DEFAULT_BUCKET, sysFileInfo.getFileObjectName());
        } catch (Exception e) {
            log.error(">>> 获取文件流异常，请求号为：{}，具体信息为：{}", RequestNoContext.get(), e.getMessage());
            throw new ServiceException(SysFileInfoExceptionEnum.FILE_STREAM_ERROR);
        }

        SysFileInfoResult sysFileInfoResult = new SysFileInfoResult();
        BeanUtil.copyProperties(sysFileInfo, sysFileInfoResult);
        sysFileInfoResult.setFileBytes(fileBytes);

        return sysFileInfoResult;
    }

    @Override
    public void assertFile(Long field) {
        SysFileInfo sysFileInfo = this.getById(field);
        if (ObjectUtil.isEmpty(sysFileInfo)) {
            throw new ServiceException(SysFileInfoExceptionEnum.NOT_EXISTED);
        }
    }

    @Override
    public void preview(SysFileInfoParam sysFileInfoParam, HttpServletResponse response) {

        byte[] fileBytes;
        //根据文件id获取文件信息结果集
        SysFileInfoResult sysFileInfoResult = this.getFileInfoResult(sysFileInfoParam.getId());
        //获取文件后缀
        String fileSuffix = sysFileInfoResult.getFileSuffix().toLowerCase();
        //获取文件字节码
        fileBytes = sysFileInfoResult.getFileBytes();
        //如果是图片类型，则直接输出
        if (LibreOfficeUtil.isPic(fileSuffix)) {
            try {
                //设置contentType
                response.setContentType(MediaType.IMAGE_JPEG_VALUE);
                //获取outputStream
                ServletOutputStream outputStream = response.getOutputStream();
                //输出
                IoUtil.write(outputStream, true, fileBytes);
            } catch (IOException e) {
                throw new ServiceException(SysFileInfoExceptionEnum.PREVIEW_ERROR_NOT_SUPPORT);
            }

        } else if (LibreOfficeUtil.isDoc(fileSuffix)) {
            try {
                //如果是文档类型，则使用libreoffice转换为pdf或html
                InputStream inputStream = IoUtil.toStream(fileBytes);

                //获取目标contentType（word和ppt和text转成pdf，excel转成html)
                String targetContentType = LibreOfficeUtil.getTargetContentTypeBySuffix(fileSuffix);

                //设置contentType
                response.setContentType(targetContentType);

                //获取outputStream
                ServletOutputStream outputStream = response.getOutputStream();

                //转换
                LibreOfficeUtil.convertToPdf(inputStream, outputStream, fileSuffix);

                //输出
                IoUtil.write(outputStream, true, fileBytes);
            } catch (IOException e) {
                log.error(">>> 预览文件异常", e.getMessage());
                throw new ServiceException(SysFileInfoExceptionEnum.PREVIEW_ERROR_NOT_SUPPORT);

            } catch (LibreOfficeException e) {
                log.error(">>> 初始化LibreOffice失败", e.getMessage());
                throw new ServiceException(SysFileInfoExceptionEnum.PREVIEW_ERROR_LIBREOFFICE);
            }

        } else {
            //否则不支持预览（暂时）
            throw new ServiceException(SysFileInfoExceptionEnum.PREVIEW_ERROR_NOT_SUPPORT);
        }
    }

    @Override
    public void download(SysFileInfoParam sysFileInfoParam, HttpServletResponse response) {
        // 获取文件信息结果集
        SysFileInfoResult sysFileInfoResult = this.getFileInfoResult(sysFileInfoParam.getId());
        String fileName = sysFileInfoResult.getFileOriginName();
        byte[] fileBytes = sysFileInfoResult.getFileBytes();
        DownloadUtil.download(fileName, fileBytes, response);
    }

    /**
     * 获取文件信息表
     *
     * @author yubaoshan
     * @date 2020/6/7 22:15
     */
    private SysFileInfo querySysFileInfo(SysFileInfoParam sysFileInfoParam) {
        SysFileInfo sysFileInfo = this.getById(sysFileInfoParam.getId());
        if (ObjectUtil.isEmpty(sysFileInfo)) {
            throw new ServiceException(SysFileInfoExceptionEnum.NOT_EXISTED);
        }
        return sysFileInfo;
    }

}
