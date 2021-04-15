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
package vip.xiaonuo.core.util;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.log.Log;
import org.jodconverter.DocumentConverter;
import org.jodconverter.document.DocumentFormat;
import org.jodconverter.office.OfficeException;
import org.springframework.http.MediaType;
import vip.xiaonuo.core.consts.MediaTypeConstant;
import vip.xiaonuo.core.enums.DocumentFormatEnum;
import vip.xiaonuo.core.exception.LibreOfficeException;

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
