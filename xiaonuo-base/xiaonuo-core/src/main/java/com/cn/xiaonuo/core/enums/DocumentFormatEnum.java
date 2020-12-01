package com.cn.xiaonuo.core.enums;

import org.jodconverter.document.DefaultDocumentFormatRegistry;
import org.jodconverter.document.DocumentFormat;

import java.io.IOException;
import java.io.InputStream;

/**
 * 文档类型枚举
 *
 * @author xuyuxiang
 * @date 2020/7/6 15:00
 */
public enum DocumentFormatEnum {

    /**
     * 文档doc格式
     */
    DOC {
        @Override
        public DocumentFormat getFormFormat() {
            return DefaultDocumentFormatRegistry.DOC;
        }
    },

    /**
     * 文档docx格式
     */
    DOCX {
        @Override
        public DocumentFormat getFormFormat() {
            return DefaultDocumentFormatRegistry.DOCX;
        }
    },

    /**
     * 演示文稿ppt格式
     */
    PPT {
        @Override
        public DocumentFormat getFormFormat() {
            return DefaultDocumentFormatRegistry.PPT;
        }
    },

    /**
     * 演示文稿pptx格式
     */
    PPTX {
        @Override
        public DocumentFormat getFormFormat() {
            return DefaultDocumentFormatRegistry.PPTX;
        }
    },

    /**
     * 电子表格xls格式
     */
    XLS {
        @Override
        public DocumentFormat getFormFormat() {
            return DefaultDocumentFormatRegistry.XLS;
        }

        @Override
        public DocumentFormat getTargetFormat() {
            return DefaultDocumentFormatRegistry.HTML;
        }
    },

    /**
     * 电子表格xlsx格式
     */
    XLSX {
        @Override
        public DocumentFormat getFormFormat() {
            return DefaultDocumentFormatRegistry.XLSX;
        }

        @Override
        public DocumentFormat getTargetFormat() {
            return DefaultDocumentFormatRegistry.HTML;
        }
    },

    /**
     * 文本txt格式
     */
    TXT {
        @Override
        public DocumentFormat getFormFormat() {
            return DefaultDocumentFormatRegistry.TXT;
        }
    };

    public InputStream getInputStream(InputStream inputStream) throws IOException {
        return inputStream;
    }

    public abstract DocumentFormat getFormFormat();

    public DocumentFormat getTargetFormat() {
        return DefaultDocumentFormatRegistry.PDF;
    }
}
