package com.cn.xiaonuo.core.util;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.log.Log;
import com.cn.xiaonuo.core.consts.MediaTypeConstant;
import com.cn.xiaonuo.core.enums.DocumentFormatEnum;
import com.cn.xiaonuo.core.exception.LibreOfficeException;
import org.jodconverter.DocumentConverter;
import org.jodconverter.document.DocumentFormat;
import org.jodconverter.office.OfficeException;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * LibreOffice工具类，用于将word,excel,ppt等格式文件转为pdf预览
 *
 * @author xuyuxiang
 * @date 2020/7/6 14:55
 */
public class LibreOfficeUtil {

    private static final Log log = Log.get();

    private static DocumentConverter documentConverter;

    private static void init() {
        try {
            documentConverter = SpringUtil.getBean(DocumentConverter.class);
        } catch (Exception e) {
            throw new LibreOfficeException();
        }
    }

    /**
     * 将文件流转换为PDF流
     *
     * @param inputStream：输入流
     * @param outputStream：输入pdf流
     * @param fileSuffix：源文件后缀
     * @return 目标类型的contentType
     * @author xuyuxiang
     * @date 2020/7/6 15:02
     */
    public static void convertToPdf(InputStream inputStream, OutputStream outputStream, String fileSuffix) {
        if(!MediaTypeConstant.DOC_PDF.equals(fileSuffix)) {
            init();
            final DocumentFormatEnum documentFormatEnum = DocumentFormatEnum.valueOf(fileSuffix.toUpperCase());
            final DocumentFormat format = documentFormatEnum.getFormFormat();
            log.info(">>> 待转换的文档类型：{}", format);
            final DocumentFormat targetFormat = documentFormatEnum.getTargetFormat();
            log.info(">>> 转换的目标文档类型：{}", targetFormat);
            try {
                final InputStream is = documentFormatEnum.getInputStream(inputStream);
                documentConverter.convert(is).as(format).to(outputStream).as(targetFormat).execute();
            } catch (IOException | OfficeException e) {
                e.printStackTrace();
            }
            log.info(">>> 文件转换结束");
        }
    }

    /**
     * 根据文件后缀判断是否图片
     *
     * @author xuyuxiang
     * @date 2020/7/6 15:31
     */
    public static boolean isPic(String fileSuffix) {
        return MediaTypeConstant.IMG_JPG.equals(fileSuffix)
                || MediaTypeConstant.IMG_JPEG.equals(fileSuffix)
                || MediaTypeConstant.IMG_PNG.equals(fileSuffix)
                || MediaTypeConstant.IMG_GIF.equals(fileSuffix)
                || MediaTypeConstant.IMG_TIF.equals(fileSuffix)
                || MediaTypeConstant.IMG_BMP.equals(fileSuffix);
    }

    /**
     * 根据文件后缀判断是否文档
     *
     * @author xuyuxiang
     * @date 2020/7/6 15:31
     */
    public static boolean isDoc(String fileSuffix) {
        return MediaTypeConstant.DOC_TXT.equals(fileSuffix)
                || MediaTypeConstant.DOC_DOC.equals(fileSuffix)
                || MediaTypeConstant.DOC_DOCX.equals(fileSuffix)
                || MediaTypeConstant.DOC_XLS.equals(fileSuffix)
                || MediaTypeConstant.DOC_XLSX.equals(fileSuffix)
                || MediaTypeConstant.DOC_PPT.equals(fileSuffix)
                || MediaTypeConstant.DOC_PPTX.equals(fileSuffix)
                || MediaTypeConstant.DOC_PDF.equals(fileSuffix);
    }

    /**
     * 根据文件后缀获取转换目标类型
     *
     * @author xuyuxiang
     * @date 2020/7/6 17:03
     */
    public static String getTargetContentTypeBySuffix(String fileSuffix) {
        //如果目标类型是pdf
        if (MediaTypeConstant.DOC_TXT.equals(fileSuffix)
                || MediaTypeConstant.DOC_DOC.equals(fileSuffix)
                || MediaTypeConstant.DOC_DOCX.equals(fileSuffix)
                || MediaTypeConstant.DOC_PPT.equals(fileSuffix)
                || MediaTypeConstant.DOC_PPTX.equals(fileSuffix)
                || MediaTypeConstant.DOC_PDF.equals(fileSuffix)) {
            return MediaType.APPLICATION_PDF_VALUE;
        } else {
            //否则是html类型
            return MediaType.TEXT_HTML_VALUE;
        }
    }
}
