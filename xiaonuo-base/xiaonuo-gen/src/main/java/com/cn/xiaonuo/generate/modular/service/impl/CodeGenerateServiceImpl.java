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
4.分发源码时候，请注明软件出处 https://gitee.com/xiaonuobase/xiaonuo-layui
5.在修改包名，模块名称，项目代码等时，请注明软件出处 https://gitee.com/xiaonuobase/xiaonuo-layui
6.若您的项目无法满足以上几点，可申请商业授权，获取XiaoNuo商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package com.cn.xiaonuo.generate.modular.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cn.xiaonuo.core.consts.CommonConstant;
import com.cn.xiaonuo.core.context.constant.ConstantContext;
import com.cn.xiaonuo.core.exception.ServiceException;
import com.cn.xiaonuo.core.factory.PageFactory;
import com.cn.xiaonuo.core.pojo.page.PageResult;
import com.cn.xiaonuo.generate.core.config.Config;
import com.cn.xiaonuo.generate.core.context.XnVelocityContext;
import com.cn.xiaonuo.generate.core.enums.TableFilteredFieldsEnum;
import com.cn.xiaonuo.generate.core.param.TableField;
import com.cn.xiaonuo.generate.core.param.XnCodeGenParam;
import com.cn.xiaonuo.generate.core.tool.JavaSqlTool;
import com.cn.xiaonuo.generate.core.tool.NamingConTool;
import com.cn.xiaonuo.generate.core.tool.StringDateTool;
import com.cn.xiaonuo.generate.core.util.Util;
import com.cn.xiaonuo.generate.modular.entity.CodeGenerate;
import com.cn.xiaonuo.generate.modular.enums.CodeGenerateExceptionEnum;
import com.cn.xiaonuo.generate.modular.mapper.CodeGenerateMapper;
import com.cn.xiaonuo.generate.modular.param.CodeGenerateParam;
import com.cn.xiaonuo.generate.modular.result.InforMationColumnsResult;
import com.cn.xiaonuo.generate.modular.result.InformationResult;
import com.cn.xiaonuo.generate.modular.service.CodeGenerateService;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * 代码生成基础配置service接口实现类
 *
 * @author yubaoshan
 * @date 2020年12月16日21:31:57
 */
@Service
public class CodeGenerateServiceImpl extends ServiceImpl<CodeGenerateMapper, CodeGenerate> implements CodeGenerateService {

    /**
     * 模板后缀
     */
    private static String TEMP_SUFFIX = ".vm";

    /**
     * 转换的编码
     */
    private static String ENCODED = "UTF-8";

    /**
     * 转换模板名称所需变量
     */
    private static String ADD_FORM_PAGE_NAME = "addForm.vue";
    private static String EDIT_FORM_PAGE_NAME = "editForm.vue";
    private static String INDEX_PAGE_NAME = "index.vue";
    private static String MANAGE_JS_NAME = "Manage.js";
    private static String JAVA_SUFFIX = ".java";
    private static String TEMP_ENTITY_NAME = "entity";


    @Override
    public PageResult<CodeGenerate> page(CodeGenerateParam codeGenerateParam) {
        QueryWrapper<CodeGenerate> queryWrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotNull(codeGenerateParam)) {
            //根据表名模糊查询
            if (ObjectUtil.isNotEmpty(codeGenerateParam.getTableName())) {
                queryWrapper.lambda().like(CodeGenerate::getTableName, codeGenerateParam.getTableName());
            }
        }
        return new PageResult<>(this.page(PageFactory.defaultPage(), queryWrapper));
    }

    @Override
    public void add(CodeGenerateParam codeGenerateParam) {
        CodeGenerate codeGenerate = new CodeGenerate();
        BeanUtil.copyProperties(codeGenerateParam, codeGenerate);
        if (!vldTablePri(codeGenerate.getTableName())) {
            throw new ServiceException(CodeGenerateExceptionEnum.CODE_GEN_TABLE_NOT_PRI);
        }
        this.save(codeGenerate);
    }

    @Override
    public void delete(List<CodeGenerateParam> codeGenerateParamList) {
        codeGenerateParamList.forEach(codeGenerateParam -> {
            this.removeById(codeGenerateParam.getId());
        });
    }

    @Override
    public void edit(CodeGenerateParam codeGenerateParam) {
        CodeGenerate codeGenerate = this.queryCodeGenerate(codeGenerateParam);
        BeanUtil.copyProperties(codeGenerateParam, codeGenerate);
        if (!vldTablePri(codeGenerate.getTableName())) {
            throw new ServiceException(CodeGenerateExceptionEnum.CODE_GEN_TABLE_NOT_PRI);
        }
        this.updateById(codeGenerate);
    }

    @Override
    public CodeGenerate detail(CodeGenerateParam codeGenerateParam) {
        return this.queryCodeGenerate(codeGenerateParam);
    }

    /**
     * 获取代码生成基础配置
     *
     * @author yubaoshan
     * @date 2020年12月16日21:19:10
     */
    private CodeGenerate queryCodeGenerate(CodeGenerateParam codeGenerateParam) {
        CodeGenerate codeGenerate = this.getById(codeGenerateParam.getId());
        if (ObjectUtil.isNull(codeGenerate)) {
            throw new ServiceException(CodeGenerateExceptionEnum.CODE_GEN_NOT_EXIST);
        }
        return codeGenerate;
    }

    @Override
    public List<InformationResult> InformationTableList () {
        String databaseUrl = ConstantContext.me().getStr(CommonConstant.DATABASE_URL_NAME);
        String dbName = databaseUrl.substring(Util.getIndex(databaseUrl, 3, "/") + 1, databaseUrl.indexOf("?"));
        return this.baseMapper.selectInformationTable(dbName);
    }

    @Override
    public void runLocal(CodeGenerateParam codeGenerateParam) {
        XnCodeGenParam xnCodeGenParam = copyParams(codeGenerateParam);
        codeGenLocal(xnCodeGenParam);
    }

    @Override
    public void runDown(CodeGenerateParam codeGenerateParam, HttpServletResponse response) {
        XnCodeGenParam xnCodeGenParam = copyParams(codeGenerateParam);
        downloadCode(xnCodeGenParam, response);
    }

    /**
     * 校验表中是否包含主键
     *
     * @author yubaoshan
     * @date 2020年12月23日 00点32分
     */
    private boolean vldTablePri (String tableName) {
        String databaseUrl = ConstantContext.me().getStr(CommonConstant.DATABASE_URL_NAME);
        String dbName = databaseUrl.substring(Util.getIndex(databaseUrl, 3, "/") + 1, databaseUrl.indexOf("?"));
        List<InforMationColumnsResult> inforMationColumnsResultList =  this.baseMapper.selectInformationColumns(dbName, tableName);
        for (int a = 0; a < inforMationColumnsResultList.size(); a++) {
            if (inforMationColumnsResultList.get(a).columnKey.equals(Config.DB_TABLE_COM_KRY)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 下载方式组装代码基础
     *
     * @author yubaoshan
     * @date 2020年12月23日 00点32分
     */
    private void downloadCode(XnCodeGenParam xnCodeGenParam, HttpServletResponse response) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
        codeGenDown(xnCodeGenParam, zipOutputStream);
        IOUtils.closeQuietly(zipOutputStream);
        outputStream.toByteArray();
        try {
            Util.DownloadGen(response, outputStream.toByteArray());
        } catch (Exception e) {
            throw new ServiceException(CodeGenerateExceptionEnum.CODE_GEN_NOT_PATH);
        }
    }

    /**
     * 转换数据为代码生成上下文中所使用的数据
     *
     * @author yubaoshan
     * @date 2020年12月17日 23点30分
     */
    private XnCodeGenParam copyParams (CodeGenerateParam codeGenerateParam) {
        CodeGenerate codeGenerate = this.queryCodeGenerate(codeGenerateParam);
        String databaseUrl = ConstantContext.me().getStr(CommonConstant.DATABASE_URL_NAME);
        String dbName = databaseUrl.substring(Util.getIndex(databaseUrl, 3, "/") + 1, databaseUrl.indexOf("?"));
        List<InforMationColumnsResult> inforMationColumnsResultList =  this.baseMapper.selectInformationColumns(dbName, codeGenerate.getTableName());

        XnCodeGenParam param = new XnCodeGenParam();
        List<TableField> tableFieldList = new ArrayList<TableField>();
        inforMationColumnsResultList.forEach(item -> {
            TableField tableField = new TableField();
            BeanUtil.copyProperties(item, tableField);
            if (tableField.getColumnKey().equals(Config.DB_TABLE_COM_KRY)) {
                tableField.setPrimaryKeyFlag(true);
            }

            // 加入后端查询参数get set参数
            String columnName = NamingConTool.UnderlineToHump(item.getColumnName(),"");
            tableField.setColumnKeyName(columnName.substring(0,1).toUpperCase() + columnName.substring(1,columnName.length()));

            // 字段类型转换Java类型
            tableField.setJavaType(JavaSqlTool.sqlToJava(item.getDataType()));

            // 字段名称转换
            tableField.setColumnName(NamingConTool.UnderlineToHump(item.getColumnName(), codeGenerate.getTablePrefix()));

            // 过滤掉通用字段
            if (!TableFilteredFieldsEnum.contains(item.getColumnName())) {
                tableFieldList.add(tableField);
            }
        });
        BeanUtil.copyProperties(codeGenerate, param);
        // 功能名
        param.setFunctionName(codeGenerate.getTableComment());
        param.setTableField(tableFieldList);
        param.setCreateTimeString(StringDateTool.getStringDate());
        return param;
    }

    /**
     * 本地项目生成
     */
    private void codeGenLocal (XnCodeGenParam xnCodeGenParam) {
        XnVelocityContext context = new XnVelocityContext();
        //初始化参数
        Properties properties=new Properties();
        //设置velocity资源加载方式为class
        properties.setProperty("resource.loader", "class");
        //设置velocity资源加载方式为file时的处理类
        properties.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        //实例化一个VelocityEngine对象
        VelocityEngine velocityEngine=new VelocityEngine(properties);

        String[] filePath = Config.xnCodeGenFilePath(xnCodeGenParam.getBusName(), xnCodeGenParam.getPackageName());
        for (int i = 0; i < filePath.length; i++) {
            String templateName = Config.xnCodeGenTempFile[i];

            String fileBaseName = ResetFileBaseName(xnCodeGenParam.getClassName(),
                    templateName.substring(templateName.indexOf(Config.FILE_SEP) + 1, templateName.lastIndexOf(TEMP_SUFFIX)));
            String path = Config.getLocalPath ();
            // 前端VUE位置有所变化
            if (fileBaseName.contains(INDEX_PAGE_NAME) || fileBaseName.contains(ADD_FORM_PAGE_NAME) ||
                    fileBaseName.contains(EDIT_FORM_PAGE_NAME) ||fileBaseName.contains(MANAGE_JS_NAME)) {
                path = Config.getLocalFrontPath();
            }

            File file = new File(path + filePath[i] + fileBaseName);

            //判断是否覆盖存在的文件
            if(file.exists() && !Config.FLAG){
                continue;
            }

            //获取父目录
            File parentFile = file.getParentFile();
            if(!parentFile.exists()){
                parentFile.mkdirs();
            }
            try {
                Writer writer = new FileWriter(file);
                velocityEngine.mergeTemplate(Config.templatePath + templateName,ENCODED,context.createVelContext(xnCodeGenParam),writer);
                writer.close();
            } catch (Exception e) {
                throw new ServiceException(CodeGenerateExceptionEnum.CODE_GEN_NOT_PATH);
            }
        }
    }

    /**
     * 下载ZIP方式
     */
    private void codeGenDown (XnCodeGenParam xnCodeGenParam,ZipOutputStream zipOutputStream) {
        Util.initVelocity();
        XnVelocityContext context = new XnVelocityContext();

        String[] filePath = Config.xnCodeGenFilePath(xnCodeGenParam.getBusName(), xnCodeGenParam.getPackageName());
        for (int a = 0; a < filePath.length; a++) {
            String templateName = Config.xnCodeGenTempFile[a];

            String fileBaseName = ResetFileBaseName(xnCodeGenParam.getClassName(),
                    templateName.substring(templateName.indexOf(Config.FILE_SEP) + 1, templateName.lastIndexOf(TEMP_SUFFIX)));
            XnZipOutputStream(context.createVelContext(xnCodeGenParam),
                    Config.templatePath + templateName,
                    filePath[a] + fileBaseName,
                    zipOutputStream);
        }
    }

    /**
     * 重置文件名称
     */
    private static String ResetFileBaseName (String className,String fileName) {
        String fileBaseName = className + fileName;
        // 实体类名称单独处理
        if (fileBaseName.contains(TEMP_ENTITY_NAME)) {
            return className + JAVA_SUFFIX;
        }
        // 首页index.vue界面
        if (fileBaseName.contains(INDEX_PAGE_NAME)) {
            return INDEX_PAGE_NAME;
        }
        // 表单界面名称
        if (fileBaseName.contains(ADD_FORM_PAGE_NAME)) {
            return ADD_FORM_PAGE_NAME;
        }
        if (fileBaseName.contains(EDIT_FORM_PAGE_NAME)) {
            return EDIT_FORM_PAGE_NAME;
        }
        // js名称
        if (fileBaseName.contains(MANAGE_JS_NAME)) {
            return className.substring(0,1).toLowerCase() + className.substring(1) + MANAGE_JS_NAME;
        }
        return fileBaseName;
    }

    /**
     * 生成ZIP
     */
    private void XnZipOutputStream (VelocityContext velContext,String tempName, String fileBaseName, ZipOutputStream zipOutputStream) {
        StringWriter sw = new StringWriter();
        Template tpl = Velocity.getTemplate(tempName, ENCODED);
        tpl.merge(velContext, sw);
        try {
            // 添加到zip
            zipOutputStream.putNextEntry(new ZipEntry(fileBaseName));
            IOUtils.write(sw.toString(), zipOutputStream, ENCODED);
            IOUtils.closeQuietly(sw);
            zipOutputStream.flush();
            zipOutputStream.closeEntry();
        } catch (IOException e) {
            throw new ServiceException(CodeGenerateExceptionEnum.CODE_GEN_NOT_PATH);
        }
    }

}
