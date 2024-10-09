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
package vip.xiaonuo.gen.modular.basic.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;
import vip.xiaonuo.common.util.CommonDownloadUtil;
import vip.xiaonuo.common.util.CommonResponseUtil;
import vip.xiaonuo.gen.core.util.GenDbTypeUtil;
import vip.xiaonuo.gen.modular.basic.entity.GenBasic;
import vip.xiaonuo.gen.modular.basic.enums.GenEffectTypeEnum;
import vip.xiaonuo.gen.modular.basic.enums.GenYesNoEnum;
import vip.xiaonuo.gen.modular.basic.mapper.GenBasicMapper;
import vip.xiaonuo.gen.modular.basic.param.*;
import vip.xiaonuo.gen.modular.basic.result.*;
import vip.xiaonuo.gen.modular.basic.service.GenBasicService;
import vip.xiaonuo.gen.modular.config.entity.GenConfig;
import vip.xiaonuo.gen.modular.config.param.GenConfigAddParam;
import vip.xiaonuo.gen.modular.config.service.GenConfigService;
import vip.xiaonuo.mobile.api.MobileModuleApi;
import vip.xiaonuo.sys.api.SysButtonApi;
import vip.xiaonuo.sys.api.SysMenuApi;
import vip.xiaonuo.sys.api.SysModuleApi;
import vip.xiaonuo.sys.api.SysRoleApi;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * 代码生成基础Service接口实现类
 *
 * @author yubaoshan
 * @date 2022/10/25 22:33
 **/
@Service
public class GenBasicServiceImpl extends ServiceImpl<GenBasicMapper, GenBasic> implements GenBasicService {

    private static final String DB_URL_KEY = "spring.datasource.dynamic.datasource.master.url";

    private static final String DB_USERNAME_KEY = "spring.datasource.dynamic.datasource.master.username";

    private static final String DB_PASSWORD_KEY = "spring.datasource.dynamic.datasource.master.password";

    private static final String GEN_PROJECT_FRONT_PLUGIN_KEY = "snowy-admin-web";

    private static final String GEN_PROJECT_PLUGIN_KEY = "snowy-plugin";

    private static final List<JSONObject> GEN_SQL_FILE_LIST = CollectionUtil.newArrayList(
            JSONUtil.createObj().set("name", "Mysql.sql.btl"),
            JSONUtil.createObj().set("name", "Oracle.sql.btl"));

    private static final List<JSONObject> GEN_FRONT_FILE_LIST = CollectionUtil.newArrayList(
            JSONUtil.createObj().set("name", "Api.js.btl").set("path", "api"),
            JSONUtil.createObj().set("name", "form.vue.btl").set("path",  "views"),
            JSONUtil.createObj().set("name", "index.vue.btl").set("path",  "views"));

    private static final List<JSONObject> GEN_MOBILE_FILE_LIST = CollectionUtil.newArrayList(
            JSONUtil.createObj().set("name", "page.json.btl"),
            JSONUtil.createObj().set("name", "Api.js.btl").set("path", "api"),
            JSONUtil.createObj().set("name", "search.vue.btl").set("path",  "pages"),
            JSONUtil.createObj().set("name", "form.vue.btl").set("path",  "pages"),
            JSONUtil.createObj().set("name", "more.vue.btl").set("path",  "pages"),
            JSONUtil.createObj().set("name", "index.vue.btl").set("path",  "pages")
    );


    private static final List<JSONObject> GEN_BACKEND_FILE_LIST = CollectionUtil.newArrayList(
            JSONUtil.createObj().set("name", "Controller.java.btl").set("path", "controller"),
            JSONUtil.createObj().set("name", "Entity.java.btl").set("path", "entity"),
            JSONUtil.createObj().set("name", "Enum.java.btl").set("path", "enums"),
            JSONUtil.createObj().set("name", "Mapper.java.btl").set("path", "mapper"),
            JSONUtil.createObj().set("name", "Mapper.xml.btl").set("path", "mapper" + File.separator + "mapping"),
            JSONUtil.createObj().set("name", "AddParam.java.btl").set("path", "param"),
            JSONUtil.createObj().set("name", "EditParam.java.btl").set("path", "param"),
            JSONUtil.createObj().set("name", "IdParam.java.btl").set("path", "param"),
            JSONUtil.createObj().set("name", "PageParam.java.btl").set("path", "param"),
            JSONUtil.createObj().set("name", "Service.java.btl").set("path", "service"),
            JSONUtil.createObj().set("name", "ServiceImpl.java.btl").set("path", "service" + File.separator + "impl"));

    private static final String SORT_CODE_KEY = "SORT_CODE";

    private static final String CREATE_USER_KEY = "CREATE_USER";

    private static final String CREATE_TIME_KEY = "CREATE_TIME";

    private static final String UPDATE_USER_KEY = "UPDATE_USER";

    private static final String UPDATE_TIME_KEY = "UPDATE_TIME";

    private static final String DELETE_FLAG_KEY = "DELETE_FLAG";

    @Resource
    private Environment environment;

    @Resource
    private MybatisPlusProperties mybatisPlusProperties;

    @Resource
    private GenConfigService genConfigService;

    @Resource
    private SysMenuApi sysMenuApi;

    @Resource
    private SysModuleApi sysModuleApi;

    @Resource
    private SysButtonApi sysButtonApi;

    @Resource
    private SysRoleApi sysRoleApi;

    @Resource
    private MobileModuleApi mobileModuleApi;

    @Override
    public Page<GenBasic> page(GenBasicPageParam genBasicPageParam) {
        QueryWrapper<GenBasic> queryWrapper = new QueryWrapper<GenBasic>().checkSqlInjection();

        if(ObjectUtil.isAllNotEmpty(genBasicPageParam.getSortField(), genBasicPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(genBasicPageParam.getSortOrder());
            queryWrapper.orderBy(true, genBasicPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(genBasicPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(GenBasic::getSortCode);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public GenBasic add(GenBasicAddParam genBasicAddParam) {
        GenBasic genBasic = BeanUtil.toBean(genBasicAddParam, GenBasic.class);
        this.save(genBasic);
        addGenConfig(genBasic);
        return genBasic;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public GenBasic edit(GenBasicEditParam genBasicEditParam) {
        GenBasic genBasic = this.queryEntity(genBasicEditParam.getId());
        if (!genBasic.getDbTable().equals(genBasicEditParam.getDbTable())) {
            // 删除配置表内该表的字段
            QueryWrapper<GenConfig> queryWrapper = new QueryWrapper<GenConfig>().checkSqlInjection();
            queryWrapper.lambda().eq(GenConfig::getBasicId, genBasic.getId());
            genConfigService.remove(queryWrapper);
            // 新增新表的数据字段
            addGenConfig(genBasic);
        }
        BeanUtil.copyProperties(genBasicEditParam, genBasic);
        this.updateById(genBasic);
        return genBasic;
    }

    /**
     * 新增表字段至配置内
     *
     * @author yubaoshan
     * @date 2023/02/22 00:54
     */
    private void addGenConfig (GenBasic genBasic) {
        GenBasicTableColumnParam tableColumnParam = new GenBasicTableColumnParam();
        tableColumnParam.setTableName(genBasic.getDbTable());
        List<GenBasicTableColumnResult> resultList = this.tableColumns(tableColumnParam);
        for (int i = 0; i < resultList.size(); i++) {
            GenBasicTableColumnResult item = resultList.get(i);
            GenConfigAddParam addParam = new GenConfigAddParam();
            addParam.setBasicId(genBasic.getId());
            if (item.getColumnName().equals(genBasic.getDbTableKey())) {
                addParam.setIsTableKey(GenYesNoEnum.Y.getValue());
            } else {
                addParam.setIsTableKey(GenYesNoEnum.N.getValue());
            }
            addParam.setFieldName(item.getColumnName());
            addParam.setFieldType(item.getTypeName());
            addParam.setFieldRemark(item.getColumnRemark());
            addParam.setFieldJavaType(GenDbTypeUtil.getJavaTypeBySqlType(item.getTypeName()));
            addParam.setEffectType(GenEffectTypeEnum.INPUT.getValue().toLowerCase());
            // 除主键、删除标志、创建人、创建时间、修改人和修改时间外，所有默认在列表显示、在增改显示、非列省略、非必填，非查询
            String logicDeleteField = mybatisPlusProperties.getGlobalConfig().getDbConfig().getLogicDeleteField();
            if(ObjectUtil.isEmpty(logicDeleteField)) {
                logicDeleteField = "DELETE_FLAG";
            }
            if(genBasic.getDbTableKey().equalsIgnoreCase(item.getColumnName()) ||
                    logicDeleteField.equalsIgnoreCase(item.getColumnName()) ||
                    CREATE_USER_KEY.equalsIgnoreCase(item.getColumnName()) ||
                    CREATE_TIME_KEY.equalsIgnoreCase(item.getColumnName()) ||
                    UPDATE_USER_KEY.equalsIgnoreCase(item.getColumnName()) ||
                    UPDATE_TIME_KEY.equalsIgnoreCase(item.getColumnName())) {
                addParam.setWhetherTable(GenYesNoEnum.N.getValue());
                addParam.setWhetherAddUpdate(GenYesNoEnum.N.getValue());
            } else {
                addParam.setWhetherTable(GenYesNoEnum.Y.getValue());
                addParam.setWhetherAddUpdate(GenYesNoEnum.Y.getValue());
            }
            addParam.setWhetherRetract(GenYesNoEnum.N.getValue());
            addParam.setWhetherRequired(GenYesNoEnum.N.getValue());
            addParam.setQueryWhether(GenYesNoEnum.N.getValue());
            addParam.setSortCode(i);
            GenConfig genConfig = BeanUtil.toBean(addParam, GenConfig.class);
            genConfigService.save(genConfig);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<GenBasicIdParam> genBasicIdParamList) {
        List<String> basicIdIdList = CollStreamUtil.toList(genBasicIdParamList, GenBasicIdParam::getId);
        if(ObjectUtil.isNotEmpty(basicIdIdList)) {
            // 级联删除配置
            genConfigService.remove(new LambdaQueryWrapper<GenConfig>().in(GenConfig::getBasicId, basicIdIdList));
            // 执行删除
            this.removeByIds(basicIdIdList);
        }
    }

    @Override
    public GenBasic detail(GenBasicIdParam genBasicIdParam) {
        return this.queryEntity(genBasicIdParam.getId());
    }

    @Override
    public GenBasic queryEntity(String id) {
        GenBasic genBasic = this.getById(id);
        if(ObjectUtil.isEmpty(genBasic)) {
            throw new CommonException("代码生成基础不存在，id值为：{}", id);
        }
        return genBasic;
    }

    @Override
    public List<GenBasicTableResult> tables() {
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection(Objects.requireNonNull(environment.getProperty(DB_URL_KEY)),
                    Objects.requireNonNull(environment.getProperty(DB_USERNAME_KEY)),
                    Objects.requireNonNull(environment.getProperty(DB_PASSWORD_KEY)));
            DatabaseMetaData metaData = conn.getMetaData();
            String url = metaData.getURL();
            String schema = null;
            if (url.toLowerCase().contains("jdbc:oracle")) {
                schema = metaData.getUserName();
            }
            List<GenBasicTableResult> tables = new ArrayList<>();
            rs = metaData.getTables(null, schema, "%", new String[]{"TABLE"});
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                if (!StrUtil.startWithIgnoreCase(tableName, "ACT_")) {
                    GenBasicTableResult genBasicTableResult = new GenBasicTableResult();
                    genBasicTableResult.setTableName(tableName);
                    String remarks = rs.getString("REMARKS");
                    if(ObjectUtil.isEmpty(remarks)) {
                        genBasicTableResult.setTableRemark(tableName);
                    } else {
                        genBasicTableResult.setTableRemark(remarks);
                    }
                    tables.add(genBasicTableResult);
                }
            }
            return tables;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            throw new CommonException("获取数据库表失败");
        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeConnection(conn);
        }
    }

    @Override
    public List<GenBasicTableColumnResult> tableColumns(GenBasicTableColumnParam genBasicTableColumnParam) {
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection(Objects.requireNonNull(environment.getProperty(DB_URL_KEY)),
                    Objects.requireNonNull(environment.getProperty(DB_USERNAME_KEY)),
                    Objects.requireNonNull(environment.getProperty(DB_PASSWORD_KEY)));
            DatabaseMetaData metaData = conn.getMetaData();
            String url = metaData.getURL();
            String schema = null;
            if (url.toLowerCase().contains("jdbc:oracle")) {
                schema = metaData.getUserName();
            }
            List<GenBasicTableColumnResult> columns = new ArrayList<>();
            rs = metaData.getColumns(null, schema, genBasicTableColumnParam.getTableName(), "%");
            if(!rs.isBeforeFirst()) {
                rs = metaData.getColumns(null, schema, genBasicTableColumnParam.getTableName().toLowerCase(), "%");
            }
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME").toUpperCase();
                GenBasicTableColumnResult genBasicTableColumnResult = new GenBasicTableColumnResult();
                genBasicTableColumnResult.setColumnName(columnName);
                String remarks = rs.getString("REMARKS");
                if(ObjectUtil.isEmpty(remarks)) {
                    genBasicTableColumnResult.setColumnRemark(columnName);
                } else {
                    genBasicTableColumnResult.setColumnRemark(remarks);
                }
                String typeName = rs.getString("TYPE_NAME").toUpperCase();
                if(ObjectUtil.isEmpty(typeName)) {
                    genBasicTableColumnResult.setTypeName("NONE");
                } else {
                    genBasicTableColumnResult.setTypeName(typeName);
                }
                columns.add(genBasicTableColumnResult);
            }
            return columns;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            throw new CommonException("获取数据库表字段失败，表名称：{}", genBasicTableColumnParam.getTableName());
        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeConnection(conn);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void execGenZip(GenBasicIdParam genBasicIdParam, HttpServletResponse response) throws IOException {
        try {
            File tempFolder = this.genTempFolder(genBasicIdParam, response, true);
            if(tempFolder == null) {
                CommonResponseUtil.renderError(response, "代码生成基础不存在，id值为：" + genBasicIdParam.getId());
                return;
            }
            // 压缩
            File zip = ZipUtil.zip(tempFolder);
            // 压缩完毕删除临时目录
            FileUtil.del(tempFolder);
            // 下载
            CommonDownloadUtil.download(zip, response);
        } catch (Exception e) {
            CommonResponseUtil.renderError(response, e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void execGenPro(GenBasicIdParam genBasicIdParam, HttpServletResponse response) throws IOException {
        File tempFolder = this.genTempFolder(genBasicIdParam, response, false);
        // 定义前端生成的目录
        String genProjectFrontendPath = System.getProperty("user.dir") + File.separator + GEN_PROJECT_FRONT_PLUGIN_KEY + File.separator + "src";

        if(!FileUtil.exist(genProjectFrontendPath)) {
            throw new CommonException("前端代码生成位置：{}不存在，请检查位置", genProjectFrontendPath);
        }

        GenBasic genBasic = this.queryEntity(genBasicIdParam.getId());
        String genModuleName = genBasic.getModuleName();
        String genPluginName = genBasic.getPluginName();
        String GEN_PROJECT_PLUGIN_BIZ_KEY = GEN_PROJECT_PLUGIN_KEY + File.separator + genPluginName;

        // 定义后端生成的目录
        String genProjectBackendPath = System.getProperty("user.dir") + File.separator + GEN_PROJECT_PLUGIN_BIZ_KEY + File.separator + "src" +
                File.separator + "main" + File.separator + "java";

        if(!FileUtil.exist(genProjectBackendPath)) {
            throw new CommonException("后端代码生成位置：{}不存在，请检查位置", genProjectBackendPath);
        }
        try {
            // 生成菜单
            String menuId = sysMenuApi.addForGenMenu(genBasic.getMenuPid(), genBasic.getBusName(), genBasic.getModule(), genBasic.getFunctionName(),
                    StrUtil.SLASH + genModuleName + StrUtil.SLASH + genBasic.getBusName());

            // 生成按钮
            sysButtonApi.addForGenButton(menuId, genBasic.getClassName(), genBasic.getFunctionName());

            // 授权菜单
            sysRoleApi.grantForGenMenuAndButton(menuId);

            //前端代码移动到前端
            FileUtil.moveContent(FileUtil.file(tempFolder + File.separator + "frontend"), FileUtil.file(genProjectFrontendPath), true);

            // 后端代码移动到后端
            FileUtil.moveContent(FileUtil.file(tempFolder + File.separator + "backend"), FileUtil.file(genProjectBackendPath), true);

            // 移动完毕删除临时目录
            FileUtil.del(tempFolder);
        } catch (Exception e) {
            log.error(">>> 代码生成异常：", e);
            throw new CommonException("代码生成异常");
        }
    }

    /**
     * 获取临时目录
     *
     * @author xuyuxiang yubaoshan
     * @date 2022/10/28 21:36
     */
    private File genTempFolder(GenBasicIdParam genBasicIdParam, HttpServletResponse response, boolean isZip) throws IOException {
        GenBasic genBasic = this.getById(genBasicIdParam.getId());
        if(ObjectUtil.isEmpty(genBasic)) {
            // 如果是压缩包下载应该使用CommonResponseUtil渲染异常
            if(isZip) {
                return null;
            } else {
                // 否则可以直接抛出异常
                throw new CommonException("代码生成基础不存在，id值为：{}", genBasicIdParam.getId());
            }
        }
        GenBasicPreviewResult genBasicPreviewResult = this.previewGen(genBasicIdParam);
        // 先删除压缩包
        FileUtil.del(FileUtil.getTmpDirPath() + File.separator + genBasic.getFunctionName() + ".zip");
        // 生成临时目录
        File tempFolder = FileUtil.file(FileUtil.getTmpDirPath() + File.separator + genBasic.getFunctionName());
        // 生成SQL代码到临时目录
        genBasicPreviewResult.getGenBasicCodeSqlResultList().forEach(genBasicCodeResult ->
                FileUtil.writeUtf8String(genBasicCodeResult.getCodeFileContent(), FileUtil.file(tempFolder + File.separator +
                        genBasicCodeResult.getCodeFileWithPathName())));
        // 生成前端代码到临时目录
        genBasicPreviewResult.getGenBasicCodeFrontendResultList().forEach(genBasicCodeResult ->
                FileUtil.writeUtf8String(genBasicCodeResult.getCodeFileContent(), FileUtil.file(tempFolder + File.separator
                        + "frontend" + File.separator + genBasicCodeResult.getCodeFileWithPathName())));
        // 生成后端代码到临时目录
        genBasicPreviewResult.getGenBasicCodeBackendResultList().forEach(genBasicCodeResult ->
                FileUtil.writeUtf8String(genBasicCodeResult.getCodeFileContent(), FileUtil.file(tempFolder + File.separator
                        + "backend" + File.separator + genBasicCodeResult.getCodeFileWithPathName())));
        // 生成移动端代码到临时目录
        if (ObjectUtil.isEmpty(genBasic.getMobileModule())){
            return tempFolder;
        }
        genBasicPreviewResult.getGenBasicCodeMobileResultList().forEach(genBasicCodeResult ->
                FileUtil.writeUtf8String(genBasicCodeResult.getCodeFileContent(), FileUtil.file(tempFolder + File.separator
                        + "mobile" + File.separator + genBasicCodeResult.getCodeFileWithPathName())));
        return tempFolder;
    }

    @Override
    public GenBasicPreviewResult previewGen(GenBasicIdParam genBasicIdParam) {
        GenBasic genBasic = this.queryEntity(genBasicIdParam.getId());
        JSONObject bindingJsonObject = this.getBindingJsonObject(genBasic);
        GenBasicPreviewResult genBasicPreviewResult = new GenBasicPreviewResult();
        try {
            // SQL基础路径
            String genSqlBasicPath = "sql";
            // SQL
            GroupTemplate groupTemplateSql = new GroupTemplate(new ClasspathResourceLoader("sqlend"),
                    Configuration.defaultConfiguration());
            List<GenBasicPreviewResult.GenBasicCodeResult> genBasicCodeSqlResultList = CollectionUtil.newArrayList();
            GEN_SQL_FILE_LIST.forEach(fileJsonObject -> {
                String fileTemplateName = fileJsonObject.getStr("name");
                GenBasicPreviewResult.GenBasicCodeResult genBasicCodeSqlResult = new GenBasicPreviewResult.GenBasicCodeResult();
                Template templateSql = groupTemplateSql.getTemplate(fileTemplateName);
                templateSql.binding(bindingJsonObject);
                String resultName = StrUtil.removeSuffix(fileTemplateName, ".btl");
                genBasicCodeSqlResult.setCodeFileName(resultName);
                genBasicCodeSqlResult.setCodeFileWithPathName(genSqlBasicPath + File.separator + resultName);
                genBasicCodeSqlResult.setCodeFileContent(templateSql.render());
                genBasicCodeSqlResultList.add(genBasicCodeSqlResult);
            });
            genBasicPreviewResult.setGenBasicCodeSqlResultList(genBasicCodeSqlResultList);

            // 前端基础路径
            String genFrontBasicPath = "";
            // 前端
            GroupTemplate groupTemplateFront = new GroupTemplate(new ClasspathResourceLoader("frontend"),
                    Configuration.defaultConfiguration());
            List<GenBasicPreviewResult.GenBasicCodeResult> genBasicCodeFrontendResultList = CollectionUtil.newArrayList();
            GEN_FRONT_FILE_LIST.forEach(fileJsonObject -> {
                String fileTemplateName = fileJsonObject.getStr("name");
                String fileTemplatePath = fileJsonObject.getStr("path") + File.separator + genBasic.getModuleName();
                GenBasicPreviewResult.GenBasicCodeResult genBasicCodeFrontResult = new GenBasicPreviewResult.GenBasicCodeResult();
                Template templateFront = groupTemplateFront.getTemplate(fileTemplateName);
                templateFront.binding(bindingJsonObject);
                String resultName = StrUtil.removeSuffix(fileTemplateName, ".btl");
                if("Api.js.btl".equalsIgnoreCase(fileTemplateName)) {
                    resultName = StrUtil.lowerFirst(genBasic.getClassName()) + resultName;
                    genBasicCodeFrontResult.setCodeFileName(resultName);
                    genBasicCodeFrontResult.setCodeFileWithPathName(genFrontBasicPath + fileTemplatePath + File.separator + resultName);
                } else {
                    genBasicCodeFrontResult.setCodeFileName(resultName);
                    genBasicCodeFrontResult.setCodeFileWithPathName(genFrontBasicPath + fileTemplatePath + File.separator + genBasic.getBusName() + File.separator + resultName);
                }
                genBasicCodeFrontResult.setCodeFileContent(templateFront.render());
                genBasicCodeFrontendResultList.add(genBasicCodeFrontResult);
            });
            genBasicPreviewResult.setGenBasicCodeFrontendResultList(genBasicCodeFrontendResultList);

            // 后端基础路径
            String genBackendBasicPath = StrUtil.replace(genBasic.getPackageName(), StrUtil.DOT, File.separator) +
                    File.separator + genBasic.getModuleName() + File.separator + "modular" +  File.separator + genBasic.getBusName() + File.separator;
            // 后端
            GroupTemplate groupTemplateBackEnd = new GroupTemplate(new ClasspathResourceLoader("backend"),
                    Configuration.defaultConfiguration());
            List<GenBasicPreviewResult.GenBasicCodeResult> genBasicCodeBackendResultList = CollectionUtil.newArrayList();
            GEN_BACKEND_FILE_LIST.forEach(fileJsonObject -> {
                String fileTemplateName = fileJsonObject.getStr("name");
                String fileTemplatePath = fileJsonObject.getStr("path");
                GenBasicPreviewResult.GenBasicCodeResult genBasicCodeBackendResult = new GenBasicPreviewResult.GenBasicCodeResult();
                Template templateBackend = groupTemplateBackEnd.getTemplate(fileTemplateName);
                templateBackend.binding(bindingJsonObject);
                String resultName = StrUtil.removeSuffix(fileTemplateName, ".btl");
                if("Entity.java.btl".equalsIgnoreCase(fileTemplateName)) {
                    resultName = ".java";
                }
                genBasicCodeBackendResult.setCodeFileName(genBasic.getClassName() + resultName);
                genBasicCodeBackendResult.setCodeFileWithPathName(genBackendBasicPath + fileTemplatePath + File.separator + genBasic.getClassName() + resultName);
                genBasicCodeBackendResult.setCodeFileContent(templateBackend.render());
                genBasicCodeBackendResultList.add(genBasicCodeBackendResult);
            });
            genBasicPreviewResult.setGenBasicCodeBackendResultList(genBasicCodeBackendResultList);

            // 移动端基础路径
            if (ObjectUtil.isEmpty(genBasic.getMobileModule())){
                return genBasicPreviewResult;
            }
            String genMobileBasicPath = "";
            // 移动端
            GroupTemplate groupTemplateMobile = new GroupTemplate(new ClasspathResourceLoader("mobile"), Configuration.defaultConfiguration());
            List<GenBasicPreviewResult.GenBasicCodeResult> genBasicCodeMobileResultList = CollectionUtil.newArrayList();
            GEN_MOBILE_FILE_LIST.forEach(fileJsonObject -> {
                String fileTemplateName = fileJsonObject.getStr("name");

                GenBasicPreviewResult.GenBasicCodeResult genBasicCodeMobileResult = new GenBasicPreviewResult.GenBasicCodeResult();
                Template templateMobile = groupTemplateMobile.getTemplate(fileTemplateName);
                templateMobile.binding(bindingJsonObject);

                String resultName = StrUtil.removeSuffix(fileTemplateName, ".btl");
                if("Api.js.btl".equalsIgnoreCase(fileTemplateName)) {
                    resultName = StrUtil.toSymbolCase(genBasic.getClassName() + resultName, '-');
                    genBasicCodeMobileResult.setCodeFileName(resultName);
                    genBasicCodeMobileResult.setCodeFileWithPathName(genMobileBasicPath + fileJsonObject.getStr("path") + File.separator + genBasic.getModuleName() + File.separator + resultName);
                } else if("page.json.btl".equals(fileTemplateName)) {
                    genBasicCodeMobileResult.setCodeFileName(resultName);
                    genBasicCodeMobileResult.setCodeFileWithPathName(genMobileBasicPath + File.separator + resultName);
                } else if ("route.js.btl".equals(fileTemplateName)) {
                    resultName =  StrUtil.toSymbolCase(genBasic.getClassName(), '-') + ".js";
                    genBasicCodeMobileResult.setCodeFileName(resultName);
                    genBasicCodeMobileResult.setCodeFileWithPathName(genMobileBasicPath + fileJsonObject.getStr("path") + File.separator + resultName);
                } else {
                    genBasicCodeMobileResult.setCodeFileName(resultName);
                    genBasicCodeMobileResult.setCodeFileWithPathName(genMobileBasicPath + fileJsonObject.getStr("path") + File.separator + genBasic.getModuleName() + File.separator + genBasic.getBusName() + File.separator + resultName);
                }
                genBasicCodeMobileResult.setCodeFileContent(templateMobile.render());
                genBasicCodeMobileResultList.add(genBasicCodeMobileResult);
            });
            genBasicPreviewResult.setGenBasicCodeMobileResultList(genBasicCodeMobileResultList);
        } catch (Exception e) {
            log.error(">>> 代码生成异常：", e);
            throw new CommonException("代码生成异常");
        }
        return genBasicPreviewResult;
    }

    /**
     * 获取移动端模块
     * @author 每天一点
     * @date 2023/7/15 22:38
     */
    @Override
    public List<GenBasicMobileModuleSelectorResult> mobileModuleSelector() {
        return mobileModuleApi.mobileModuleSelector().stream()
                .map(jsonObject -> JSONUtil.toBean(jsonObject, GenBasicMobileModuleSelectorResult.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<GenBasicModuleSelectorResult> moduleSelector() {
        return sysModuleApi.moduleSelector().stream()
                .map(jsonObject -> JSONUtil.toBean(jsonObject, GenBasicModuleSelectorResult.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<Tree<String>> menuTreeSelector(GenBasicSelectorMenuParam genBasicSelectorMenuParam) {
        return sysMenuApi.menuTreeSelector(genBasicSelectorMenuParam.getModule());
    }

    /**
     * 根据代码生成基础获取构造的参数
     *
     * @author xuyuxiang
     * @date 2022/10/28 21:36
     */
    public JSONObject getBindingJsonObject(GenBasic genBasic) {
        JSONObject bindingJsonObject = JSONUtil.createObj();
        // 代码模块名
        bindingJsonObject.set("moduleName", genBasic.getModuleName());
        // 功能名
        bindingJsonObject.set("functionName", genBasic.getFunctionName());
        // 业务名
        bindingJsonObject.set("busName", genBasic.getBusName());
        // 包名
        bindingJsonObject.set("packageName", genBasic.getPackageName());
        // 库名
        bindingJsonObject.set("dbTable", genBasic.getDbTable());
        // 类名
        bindingJsonObject.set("className", genBasic.getClassName());
        // 类首字母小写名
        bindingJsonObject.set("classNameFirstLower", StrUtil.lowerFirst(genBasic.getClassName()));
        // 类小写且以横线分割
        bindingJsonObject.set("classNameLowerKebab", StrUtil.toSymbolCase(genBasic.getClassName(), '-'));
        // 主键名
        bindingJsonObject.set("dbTableKey", genBasic.getDbTableKey());
        // 主键Java类型
        bindingJsonObject.set("dbTableKeyJavaType", "String");
        // 主键名驼峰
        bindingJsonObject.set("dbTableKeyCamelCase", StrUtil.toCamelCase(genBasic.getDbTableKey().toLowerCase()));
        // 主键首字母大写名
        bindingJsonObject.set("dbTableKeyFirstUpper", StrUtil.upperFirst(StrUtil.toCamelCase(genBasic.getDbTableKey().toLowerCase())));
        // 主键注释
        bindingJsonObject.set("dbTableKeyRemark", genBasic.getDbTableKey());
        // 表单布局
        bindingJsonObject.set("formLayout", genBasic.getFormLayout());
        // 使用栅格
        bindingJsonObject.set("gridWhether", genBasic.getGridWhether().equalsIgnoreCase(GenYesNoEnum.Y.getValue()));
        // 父菜单ID
        bindingJsonObject.set("parentId", genBasic.getMenuPid());
        // 菜单ID
        bindingJsonObject.set("menuId", IdWorker.getIdStr());
        // 菜单编码
        bindingJsonObject.set("menuCode", RandomUtil.randomString(10));
        // 菜单路径
        bindingJsonObject.set("menuPath", StrUtil.SLASH + genBasic.getModuleName() + StrUtil.SLASH + genBasic.getBusName());
        // 菜单组件
        bindingJsonObject.set("menuComponent", genBasic.getModuleName() + StrUtil.SLASH + genBasic.getBusName() + StrUtil.SLASH + "index");
        // 模块ID
        bindingJsonObject.set("moduleId", genBasic.getModule());
        // 移动端模块ID
        bindingJsonObject.set("mobileModuleId", genBasic.getMobileModule());
        // 添加按钮ID
        bindingJsonObject.set("addButtonId", IdWorker.getIdStr());
        // 编辑按钮ID
        bindingJsonObject.set("editButtonId", IdWorker.getIdStr());
        // 删除按钮ID
        bindingJsonObject.set("deleteButtonId", IdWorker.getIdStr());
        // 批量删除按钮ID
        bindingJsonObject.set("batchDeleteButtonId", IdWorker.getIdStr());
        // 作者
        bindingJsonObject.set("authorName", genBasic.getAuthorName());
        // 生成时间
        bindingJsonObject.set("genTime", DateUtil.format(DateTime.now(), " yyyy/MM/dd HH:mm"));
        // 定义配置详情列表
        List<JSONObject> configList = CollectionUtil.newArrayList();
        // 定义是否有排序字段
        AtomicBoolean hasSortCodeField = new AtomicBoolean(false);
        genConfigService.list(new LambdaQueryWrapper<GenConfig>().eq(GenConfig::getBasicId, genBasic.getId()))
                .forEach(genConfig -> {
                    // 定义字段信息
                    JSONObject configItem = JSONUtil.createObj();
                    if(genConfig.getFieldName().equalsIgnoreCase(SORT_CODE_KEY)) {
                        hasSortCodeField.set(true);
                    }
                    // 如果是主键，则无需作为添加参数，需要作为编辑参数，需要主键注解
                    if(genConfig.getFieldName().equalsIgnoreCase(genBasic.getDbTableKey())) {
                        configItem.set("needAdd", false);
                        configItem.set("needEdit", true);
                        configItem.set("needPage", false);
                        configItem.set("needPageType", "none");
                        configItem.set("required", true);
                        configItem.set("needTableId", true);
                        bindingJsonObject.set("dbTableKeyJavaType", genConfig.getFieldJavaType());
                        bindingJsonObject.set("dbTableKeyRemark", genConfig.getFieldRemark());
                    } else {
                        // 排除删除标志
                        String logicDeleteField = mybatisPlusProperties.getGlobalConfig().getDbConfig().getLogicDeleteField();
                        if(ObjectUtil.isEmpty(logicDeleteField)) {
                            logicDeleteField = "DELETE_FLAG";
                        }
                        if(genConfig.getFieldName().equalsIgnoreCase(logicDeleteField)) {
                            configItem.set("needAdd", false);
                            configItem.set("needEdit", false);
                            configItem.set("needPage", false);
                            configItem.set("needPageType", "none");
                            configItem.set("required", false);
                            configItem.set("needTableId", false);
                        } else {
                            boolean needAddAndUpdate = genConfig.getWhetherAddUpdate().equalsIgnoreCase(GenYesNoEnum.Y.getValue());
                            configItem.set("needAdd", needAddAndUpdate);
                            configItem.set("needEdit", needAddAndUpdate);
                            configItem.set("needPage", genConfig.getQueryWhether().equalsIgnoreCase(GenYesNoEnum.Y.getValue()));
                            configItem.set("needPageType", genConfig.getQueryType());
                            configItem.set("required", genConfig.getWhetherRequired().equalsIgnoreCase(GenYesNoEnum.Y.getValue()));
                            configItem.set("needTableId", false);
                        }
                    }
                    // 列显示
                    configItem.set("whetherTable", genConfig.getWhetherTable().equalsIgnoreCase(GenYesNoEnum.Y.getValue()));
                    // 列省略
                    configItem.set("whetherRetract", genConfig.getWhetherRetract().equalsIgnoreCase(GenYesNoEnum.Y.getValue()));
                    // 增改
                    configItem.set("whetherAddUpdate", genConfig.getWhetherAddUpdate().equalsIgnoreCase(GenYesNoEnum.Y.getValue()));
                    // 作用类型
                    configItem.set("effectType", genConfig.getEffectType());
                    // 字典值
                    configItem.set("dictTypeCode", genConfig.getDictTypeCode());
                    // 实体类型
                    configItem.set("fieldJavaType", genConfig.getFieldJavaType());
                    // 字段驼峰名
                    configItem.set("fieldNameCamelCase", StrUtil.toCamelCase(genConfig.getFieldName().toLowerCase()));
                    // 字段驼峰首字母大写名
                    configItem.set("fieldNameCamelCaseFirstUpper", StrUtil.upperFirst(StrUtil.toCamelCase(genConfig.getFieldName().toLowerCase())));
                    // 字段注释
                    configItem.set("fieldRemark", genConfig.getFieldRemark());
                    // 是否需要自动插入
                    configItem.set("needAutoInsert", CREATE_USER_KEY.equalsIgnoreCase(genConfig.getFieldName()) ||
                            CREATE_TIME_KEY.equalsIgnoreCase(genConfig.getFieldName()));
                    // 是否需要自动更新
                    configItem.set("needAutoUpdate", UPDATE_USER_KEY.equalsIgnoreCase(genConfig.getFieldName()) ||
                            UPDATE_TIME_KEY.equalsIgnoreCase(genConfig.getFieldName()));
                    // 是否需要逻辑删除
                    configItem.set("needLogicDelete", DELETE_FLAG_KEY.equalsIgnoreCase(genConfig.getFieldName()));
                    configList.add(configItem);

                });
        // 配置信息
        bindingJsonObject.set("configList", configList);
        // 有排序字段
        bindingJsonObject.set("hasSortCodeField", hasSortCodeField.get());
        return bindingJsonObject;
    }
}
