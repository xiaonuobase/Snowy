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
import vip.xiaonuo.gen.modular.basic.enums.GenCategoryEnum;
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
            JSONUtil.createObj().set("name", "index.vue.btl").set("path",  "views"),
            JSONUtil.createObj().set("name", "importModel.vue.btl").set("path",  "views"));

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

    // 单树表模板目录名
    private static final String TEMPLATE_BACKEND_TREE = "backend-tree";
    private static final String TEMPLATE_FRONTEND_TREE = "frontend-tree";

    // 左树右表模板目录名
    private static final String TEMPLATE_BACKEND_LEFT_TREE_TABLE = "backend-left-tree-table";
    private static final String TEMPLATE_FRONTEND_LEFT_TREE_TABLE = "frontend-left-tree-table";

    // 主子表模板目录名
    private static final String TEMPLATE_BACKEND_MASTER_DETAIL = "backend-master-detail";
    private static final String TEMPLATE_FRONTEND_MASTER_DETAIL = "frontend-master-detail";

    // 双表类型的子表后端文件列表
    private static final List<JSONObject> GEN_BACKEND_SUB_FILE_LIST = CollectionUtil.newArrayList(
            JSONUtil.createObj().set("name", "SubEntity.java.btl").set("path", "entity"),
            JSONUtil.createObj().set("name", "SubMapper.java.btl").set("path", "mapper"),
            JSONUtil.createObj().set("name", "SubMapper.xml.btl").set("path", "mapper" + File.separator + "mapping"),
            JSONUtil.createObj().set("name", "SubAddParam.java.btl").set("path", "param"),
            JSONUtil.createObj().set("name", "SubEditParam.java.btl").set("path", "param"),
            JSONUtil.createObj().set("name", "SubIdParam.java.btl").set("path", "param"),
            JSONUtil.createObj().set("name", "SubPageParam.java.btl").set("path", "param"),
            JSONUtil.createObj().set("name", "SubService.java.btl").set("path", "service"),
            JSONUtil.createObj().set("name", "SubServiceImpl.java.btl").set("path", "service" + File.separator + "impl"));

    // 主子表类型的前端文件列表（比标准多一个subForm.vue）
    private static final List<JSONObject> GEN_FRONT_MASTER_DETAIL_FILE_LIST = CollectionUtil.newArrayList(
            JSONUtil.createObj().set("name", "Api.js.btl").set("path", "api"),
            JSONUtil.createObj().set("name", "form.vue.btl").set("path", "views"),
            JSONUtil.createObj().set("name", "subForm.vue.btl").set("path", "views"),
            JSONUtil.createObj().set("name", "index.vue.btl").set("path", "views"),
            JSONUtil.createObj().set("name", "importModel.vue.btl").set("path", "views"),
            JSONUtil.createObj().set("name", "subImportModel.vue.btl").set("path", "views"));

    // 左树右表类型的前端文件列表（比标准多一个treeForm.vue）
    private static final List<JSONObject> GEN_FRONT_LEFT_TREE_TABLE_FILE_LIST = CollectionUtil.newArrayList(
            JSONUtil.createObj().set("name", "Api.js.btl").set("path", "api"),
            JSONUtil.createObj().set("name", "form.vue.btl").set("path", "views"),
            JSONUtil.createObj().set("name", "treeForm.vue.btl").set("path", "views"),
            JSONUtil.createObj().set("name", "index.vue.btl").set("path", "views"),
            JSONUtil.createObj().set("name", "importModel.vue.btl").set("path", "views"));

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
        // 校验生成类型及条件字段
        validateGenTypeFields(genBasicAddParam.getGenType(), genBasicAddParam.getTreeParentField(),
                genBasicAddParam.getTreeNameField(), genBasicAddParam.getSubDbTable(),
                genBasicAddParam.getSubDbTableKey(), genBasicAddParam.getSubForeignKey(),
                genBasicAddParam.getSubClassName());
        GenBasic genBasic = BeanUtil.toBean(genBasicAddParam, GenBasic.class);
        this.save(genBasic);
        addGenConfig(genBasic);
        return genBasic;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public GenBasic edit(GenBasicEditParam genBasicEditParam) {
        GenBasic genBasic = this.queryEntity(genBasicEditParam.getId());
        // 校验生成类型及条件字段
        validateGenTypeFields(genBasicEditParam.getGenType(), genBasicEditParam.getTreeParentField(),
                genBasicEditParam.getTreeNameField(), genBasicEditParam.getSubDbTable(),
                genBasicEditParam.getSubDbTableKey(), genBasicEditParam.getSubForeignKey(),
                genBasicEditParam.getSubClassName());
        // 主表变更：刷新全部字段配置
        if (!StrUtil.equals(genBasic.getDbTable(), genBasicEditParam.getDbTable())) {
            // 删除配置表内该表的字段
            QueryWrapper<GenConfig> queryWrapper = new QueryWrapper<GenConfig>().checkSqlInjection();
            queryWrapper.lambda().eq(GenConfig::getBasicId, genBasic.getId());
            genConfigService.remove(queryWrapper);
            BeanUtil.copyProperties(genBasicEditParam, genBasic);
            this.updateById(genBasic);
            // 新增新表的数据字段
            addGenConfig(genBasic);
        } else if (GenCategoryEnum.isDualTable(genBasicEditParam.getGenType()) &&
                !StrUtil.equals(genBasic.getSubDbTable(), genBasicEditParam.getSubDbTable())) {
            // 子表变更：仅刷新子表字段配置
            genConfigService.remove(new LambdaQueryWrapper<GenConfig>()
                    .eq(GenConfig::getBasicId, genBasic.getId())
                    .eq(GenConfig::getTableType, "SUB"));
            BeanUtil.copyProperties(genBasicEditParam, genBasic);
            this.updateById(genBasic);
            // 重新扫描子表字段
            addSubGenConfig(genBasic);
        } else {
            BeanUtil.copyProperties(genBasicEditParam, genBasic);
            this.updateById(genBasic);
        }
        return genBasic;
    }

    /**
     * 校验生成类型及其关联的条件字段
     */
    private void validateGenTypeFields(String genType, String treeParentField, String treeNameField,
                                       String subDbTable, String subDbTableKey, String subForeignKey, String subClassName) {
        GenCategoryEnum.validate(genType);
        if (GenCategoryEnum.isTreeType(genType)) {
            if (ObjectUtil.isEmpty(treeParentField)) {
                throw new CommonException("树类型必须指定树父级字段（treeParentField）");
            }
            if (ObjectUtil.isEmpty(treeNameField)) {
                throw new CommonException("树类型必须指定树显示名称字段（treeNameField）");
            }
        }
        if (GenCategoryEnum.isDualTable(genType)) {
            if (ObjectUtil.isEmpty(subDbTable)) {
                throw new CommonException("双表类型必须指定子表名称（subDbTable）");
            }
            if (ObjectUtil.isEmpty(subDbTableKey)) {
                throw new CommonException("双表类型必须指定子表主键（subDbTableKey）");
            }
            if (ObjectUtil.isEmpty(subForeignKey)) {
                throw new CommonException("双表类型必须指定子表关联外键（subForeignKey）");
            }
            if (ObjectUtil.isEmpty(subClassName)) {
                throw new CommonException("双表类型必须指定子表类名（subClassName）");
            }
        }
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
            addParam.setWhetherUnique(GenYesNoEnum.N.getValue());
            addParam.setQueryWhether(GenYesNoEnum.N.getValue());
            addParam.setSortCode(i);
            GenConfig genConfig = BeanUtil.toBean(addParam, GenConfig.class);
            genConfig.setTableType("MAIN");
            genConfigService.save(genConfig);
        }
        // 双表类型：额外扫描子表字段
        if (GenCategoryEnum.isDualTable(genBasic.getGenType())) {
            addSubGenConfig(genBasic);
        }
    }

    /**
     * 新增子表字段至配置内（双表类型专用）
     */
    private void addSubGenConfig(GenBasic genBasic) {
        GenBasicTableColumnParam subTableColumnParam = new GenBasicTableColumnParam();
        subTableColumnParam.setTableName(genBasic.getSubDbTable());
        List<GenBasicTableColumnResult> subResultList = this.tableColumns(subTableColumnParam);
        for (int j = 0; j < subResultList.size(); j++) {
            GenBasicTableColumnResult subItem = subResultList.get(j);
            GenConfigAddParam subAddParam = new GenConfigAddParam();
            subAddParam.setBasicId(genBasic.getId());
            if (subItem.getColumnName().equals(genBasic.getSubDbTableKey())) {
                subAddParam.setIsTableKey(GenYesNoEnum.Y.getValue());
            } else {
                subAddParam.setIsTableKey(GenYesNoEnum.N.getValue());
            }
            subAddParam.setFieldName(subItem.getColumnName());
            subAddParam.setFieldType(subItem.getTypeName());
            subAddParam.setFieldRemark(subItem.getColumnRemark());
            subAddParam.setFieldJavaType(GenDbTypeUtil.getJavaTypeBySqlType(subItem.getTypeName()));
            subAddParam.setEffectType(GenEffectTypeEnum.INPUT.getValue().toLowerCase());
            String logicDeleteField = mybatisPlusProperties.getGlobalConfig().getDbConfig().getLogicDeleteField();
            if(ObjectUtil.isEmpty(logicDeleteField)) {
                logicDeleteField = "DELETE_FLAG";
            }
            if(genBasic.getSubDbTableKey().equalsIgnoreCase(subItem.getColumnName()) ||
                    logicDeleteField.equalsIgnoreCase(subItem.getColumnName()) ||
                    CREATE_USER_KEY.equalsIgnoreCase(subItem.getColumnName()) ||
                    CREATE_TIME_KEY.equalsIgnoreCase(subItem.getColumnName()) ||
                    UPDATE_USER_KEY.equalsIgnoreCase(subItem.getColumnName()) ||
                    UPDATE_TIME_KEY.equalsIgnoreCase(subItem.getColumnName())) {
                subAddParam.setWhetherTable(GenYesNoEnum.N.getValue());
                subAddParam.setWhetherAddUpdate(GenYesNoEnum.N.getValue());
            } else {
                subAddParam.setWhetherTable(GenYesNoEnum.Y.getValue());
                subAddParam.setWhetherAddUpdate(GenYesNoEnum.Y.getValue());
            }
            subAddParam.setWhetherRetract(GenYesNoEnum.N.getValue());
            subAddParam.setWhetherRequired(GenYesNoEnum.N.getValue());
            subAddParam.setWhetherUnique(GenYesNoEnum.N.getValue());
            subAddParam.setQueryWhether(GenYesNoEnum.N.getValue());
            subAddParam.setSortCode(j);
            GenConfig subGenConfig = BeanUtil.toBean(subAddParam, GenConfig.class);
            subGenConfig.setTableType("SUB");
            genConfigService.save(subGenConfig);
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
            rs = metaData.getColumns(conn.getCatalog(), schema, genBasicTableColumnParam.getTableName(), "%");
            if(!rs.isBeforeFirst()) {
                rs = metaData.getColumns(conn.getCatalog(), schema, genBasicTableColumnParam.getTableName().toLowerCase(), "%");
            }
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME").toUpperCase();
                boolean exist = columns.stream().anyMatch(dbsTableColumnResult -> dbsTableColumnResult.getColumnName().equals(columnName));
                if(!exist) {
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
            sysButtonApi.addForGenButton(menuId, genBasic.getClassName(), genBasic.getFunctionName(),
                    genBasic.getGenType(), genBasic.getSubFunctionName());

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
            String genType = ObjectUtil.isEmpty(genBasic.getGenType()) ? GenCategoryEnum.TABLE.getValue() : genBasic.getGenType();
            GroupTemplate groupTemplateFront = new GroupTemplate(new ClasspathResourceLoader(getFrontendTemplateDir(genType)),
                    Configuration.defaultConfiguration());
            List<GenBasicPreviewResult.GenBasicCodeResult> genBasicCodeFrontendResultList = CollectionUtil.newArrayList();
            getFrontendFileList(genType).forEach(fileJsonObject -> {
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
            GroupTemplate groupTemplateBackEnd = new GroupTemplate(new ClasspathResourceLoader(getBackendTemplateDir(genType)),
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

            // 双表类型：生成子表后端代码
            if (GenCategoryEnum.isDualTable(genType)) {
                GroupTemplate groupTemplateSubBackEnd = new GroupTemplate(new ClasspathResourceLoader(getBackendTemplateDir(genType)),
                        Configuration.defaultConfiguration());
                GEN_BACKEND_SUB_FILE_LIST.forEach(fileJsonObject -> {
                    String fileTemplateName = fileJsonObject.getStr("name");
                    String fileTemplatePath = fileJsonObject.getStr("path");
                    GenBasicPreviewResult.GenBasicCodeResult genBasicCodeBackendResult = new GenBasicPreviewResult.GenBasicCodeResult();
                    Template templateBackend = groupTemplateSubBackEnd.getTemplate(fileTemplateName);
                    templateBackend.binding(bindingJsonObject);
                    String resultName = StrUtil.removeSuffix(fileTemplateName, ".btl");
                    // 去掉 Sub 前缀得到实际文件名后缀
                    String actualSuffix = resultName.substring(3); // "SubEntity.java" -> "Entity.java"
                    if("SubEntity.java.btl".equalsIgnoreCase(fileTemplateName)) {
                        actualSuffix = ".java";
                    }
                    genBasicCodeBackendResult.setCodeFileName(genBasic.getSubClassName() + actualSuffix);
                    genBasicCodeBackendResult.setCodeFileWithPathName(genBackendBasicPath + fileTemplatePath + File.separator + genBasic.getSubClassName() + actualSuffix);
                    genBasicCodeBackendResult.setCodeFileContent(templateBackend.render());
                    genBasicCodeBackendResultList.add(genBasicCodeBackendResult);
                });
            }

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
        // 生成类型（用于SQL模板区分）
        bindingJsonObject.set("genType", genBasic.getGenType());
        // 左树按钮ID（左树右表类型使用）
        bindingJsonObject.set("treeAddButtonId", IdWorker.getIdStr());
        bindingJsonObject.set("treeEditButtonId", IdWorker.getIdStr());
        bindingJsonObject.set("treeDeleteButtonId", IdWorker.getIdStr());
        // 添加按钮ID
        bindingJsonObject.set("addButtonId", IdWorker.getIdStr());
        // 编辑按钮ID
        bindingJsonObject.set("editButtonId", IdWorker.getIdStr());
        // 删除按钮ID
        bindingJsonObject.set("deleteButtonId", IdWorker.getIdStr());
        // 批量删除按钮ID
        bindingJsonObject.set("batchDeleteButtonId", IdWorker.getIdStr());
        // 导入按钮ID
        bindingJsonObject.set("importButtonId", IdWorker.getIdStr());
        // 导出按钮ID
        bindingJsonObject.set("exportButtonId", IdWorker.getIdStr());
        // 子表按钮ID（主子表类型使用）
        bindingJsonObject.set("subAddButtonId", IdWorker.getIdStr());
        bindingJsonObject.set("subEditButtonId", IdWorker.getIdStr());
        bindingJsonObject.set("subDeleteButtonId", IdWorker.getIdStr());
        // 子表导入按钮ID（主子表类型使用）
        bindingJsonObject.set("subImportButtonId", IdWorker.getIdStr());
        // 子表导出按钮ID（主子表类型使用）
        bindingJsonObject.set("subExportButtonId", IdWorker.getIdStr());
        // 作者
        bindingJsonObject.set("authorName", genBasic.getAuthorName());
        // 生成时间
        bindingJsonObject.set("genTime", DateUtil.format(DateTime.now(), " yyyy/MM/dd HH:mm"));
        // 定义配置详情列表
        List<JSONObject> configList = CollectionUtil.newArrayList();
        // 定义是否有排序字段
        AtomicBoolean hasSortCodeField = new AtomicBoolean(false);
        genConfigService.list(new LambdaQueryWrapper<GenConfig>().eq(GenConfig::getBasicId, genBasic.getId())
                .and(wrapper -> wrapper.eq(GenConfig::getTableType, "MAIN").or().isNull(GenConfig::getTableType)))
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
                        configItem.set("unique", true);
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
                            configItem.set("unique", false);
                            configItem.set("needTableId", false);
                        } else {
                            boolean needAddAndUpdate = genConfig.getWhetherAddUpdate().equalsIgnoreCase(GenYesNoEnum.Y.getValue());
                            configItem.set("needAdd", needAddAndUpdate);
                            configItem.set("needEdit", needAddAndUpdate);
                            configItem.set("needPage", genConfig.getQueryWhether().equalsIgnoreCase(GenYesNoEnum.Y.getValue()));
                            configItem.set("needPageType", genConfig.getQueryType());
                            configItem.set("required", genConfig.getWhetherRequired().equalsIgnoreCase(GenYesNoEnum.Y.getValue()));
                            configItem.set("unique", ObjectUtil.isNotEmpty(genConfig.getWhetherUnique()) && genConfig.getWhetherUnique().equalsIgnoreCase(GenYesNoEnum.Y.getValue()));
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
        // 获取必填字段
        List<JSONObject> requiredFieldList = configList.stream().filter(configItem -> configItem.getBool("required")).toList();
        // 必填字段列表
        bindingJsonObject.set("requiredFieldList", requiredFieldList);
        // 必填字段字符串
        bindingJsonObject.set("requiredFieldStr", StrUtil.join(StrUtil.COMMA + " ", requiredFieldList.stream().map(configItem ->
                configItem.getStr("fieldNameCamelCase")).collect(Collectors.toList())));
        // 获取唯一字段
        List<JSONObject> uniqueFieldList = configList.stream().filter(configItem -> configItem.getBool("unique")).toList();
        // 唯一字段列表
        bindingJsonObject.set("uniqueFieldList", uniqueFieldList);
        // 唯一字段字符串
        bindingJsonObject.set("uniqueFieldStr", StrUtil.join(StrUtil.COMMA + " ", uniqueFieldList.stream().map(configItem ->
                configItem.getStr("fieldNameCamelCase")).collect(Collectors.toList())));
        // 有排序字段
        bindingJsonObject.set("hasSortCodeField", hasSortCodeField.get());

        // 计算选择器类型标志
        boolean hasUserSelector = configList.stream().anyMatch(c -> {
            String et = c.getStr("effectType");
            return "userSelector".equals(et) || "userSelectorMulti".equals(et);
        });
        boolean hasOrgSelector = configList.stream().anyMatch(c -> {
            String et = c.getStr("effectType");
            return "orgSelector".equals(et) || "orgSelectorMulti".equals(et);
        });
        boolean hasPositionSelector = configList.stream().anyMatch(c -> {
            String et = c.getStr("effectType");
            return "positionSelector".equals(et) || "positionSelectorMulti".equals(et);
        });
        boolean hasGroupSelector = configList.stream().anyMatch(c -> {
            String et = c.getStr("effectType");
            return "groupSelector".equals(et) || "groupSelectorMulti".equals(et);
        });
        bindingJsonObject.set("hasUserSelector", hasUserSelector);
        bindingJsonObject.set("hasOrgSelector", hasOrgSelector);
        bindingJsonObject.set("hasPositionSelector", hasPositionSelector);
        bindingJsonObject.set("hasGroupSelector", hasGroupSelector);

        // 生成类型
        String genType = ObjectUtil.isEmpty(genBasic.getGenType()) ? GenCategoryEnum.TABLE.getValue() : genBasic.getGenType();
        bindingJsonObject.set("genType", genType);

        // 单树表相关变量
        if (GenCategoryEnum.TREE.getValue().equals(genType) || GenCategoryEnum.LEFT_TREE_TABLE.getValue().equals(genType)) {
            String treeParentField = genBasic.getTreeParentField();
            String treeNameField = genBasic.getTreeNameField();
            if (ObjectUtil.isNotEmpty(treeParentField)) {
                bindingJsonObject.set("treeParentFieldCamelCase", StrUtil.toCamelCase(treeParentField.toLowerCase()));
                bindingJsonObject.set("treeParentFieldCamelCaseFirstUpper", StrUtil.upperFirst(StrUtil.toCamelCase(treeParentField.toLowerCase())));
            }
            if (ObjectUtil.isNotEmpty(treeNameField)) {
                bindingJsonObject.set("treeNameFieldCamelCase", StrUtil.toCamelCase(treeNameField.toLowerCase()));
                bindingJsonObject.set("treeNameFieldCamelCaseFirstUpper", StrUtil.upperFirst(StrUtil.toCamelCase(treeNameField.toLowerCase())));
            }
        }

        // 双表相关变量
        if (GenCategoryEnum.isDualTable(genType)) {
            bindingJsonObject.set("subDbTable", genBasic.getSubDbTable());
            bindingJsonObject.set("subDbTableKey", genBasic.getSubDbTableKey());
            bindingJsonObject.set("subDbTableKeyCamelCase", StrUtil.toCamelCase(genBasic.getSubDbTableKey().toLowerCase()));
            bindingJsonObject.set("subDbTableKeyFirstUpper", StrUtil.upperFirst(StrUtil.toCamelCase(genBasic.getSubDbTableKey().toLowerCase())));
            bindingJsonObject.set("subForeignKey", genBasic.getSubForeignKey());
            bindingJsonObject.set("subForeignKeyCamelCase", StrUtil.toCamelCase(genBasic.getSubForeignKey().toLowerCase()));
            bindingJsonObject.set("subForeignKeyFirstUpper", StrUtil.upperFirst(StrUtil.toCamelCase(genBasic.getSubForeignKey().toLowerCase())));
            bindingJsonObject.set("subClassName", genBasic.getSubClassName());
            bindingJsonObject.set("subClassNameFirstLower", StrUtil.lowerFirst(genBasic.getSubClassName()));
            bindingJsonObject.set("subFunctionName", genBasic.getSubFunctionName());
            bindingJsonObject.set("subBusName", genBasic.getSubBusName());
            // 子表主键Java类型（默认String）
            bindingJsonObject.set("subDbTableKeyJavaType", "String");
            bindingJsonObject.set("subDbTableKeyRemark", genBasic.getSubDbTableKey());

            // 子表字段配置列表
            List<JSONObject> subConfigList = CollectionUtil.newArrayList();
            AtomicBoolean subHasSortCodeField = new AtomicBoolean(false);
            genConfigService.list(new LambdaQueryWrapper<GenConfig>()
                    .eq(GenConfig::getBasicId, genBasic.getId())
                    .eq(GenConfig::getTableType, "SUB"))
                    .forEach(genConfig -> {
                        JSONObject subConfigItem = JSONUtil.createObj();
                        if(genConfig.getFieldName().equalsIgnoreCase(SORT_CODE_KEY)) {
                            subHasSortCodeField.set(true);
                        }
                        if(genConfig.getFieldName().equalsIgnoreCase(genBasic.getSubDbTableKey())) {
                            subConfigItem.set("needAdd", false);
                            subConfigItem.set("needEdit", true);
                            subConfigItem.set("needPage", false);
                            subConfigItem.set("needPageType", "none");
                            subConfigItem.set("required", true);
                            subConfigItem.set("unique", true);
                            subConfigItem.set("needTableId", true);
                            bindingJsonObject.set("subDbTableKeyJavaType", genConfig.getFieldJavaType());
                            bindingJsonObject.set("subDbTableKeyRemark", genConfig.getFieldRemark());
                        } else {
                            String logicDeleteField = mybatisPlusProperties.getGlobalConfig().getDbConfig().getLogicDeleteField();
                            if(ObjectUtil.isEmpty(logicDeleteField)) {
                                logicDeleteField = "DELETE_FLAG";
                            }
                            if(genConfig.getFieldName().equalsIgnoreCase(logicDeleteField)) {
                                subConfigItem.set("needAdd", false);
                                subConfigItem.set("needEdit", false);
                                subConfigItem.set("needPage", false);
                                subConfigItem.set("needPageType", "none");
                                subConfigItem.set("required", false);
                                subConfigItem.set("unique", false);
                                subConfigItem.set("needTableId", false);
                            } else {
                                boolean needAddAndUpdate = genConfig.getWhetherAddUpdate().equalsIgnoreCase(GenYesNoEnum.Y.getValue());
                                subConfigItem.set("needAdd", needAddAndUpdate);
                                subConfigItem.set("needEdit", needAddAndUpdate);
                                // 子表外键字段必须在AddParam和EditParam中，无论是否配置增改
                                if(genConfig.getFieldName().equalsIgnoreCase(genBasic.getSubForeignKey())) {
                                    subConfigItem.set("needAdd", true);
                                    subConfigItem.set("needEdit", true);
                                }
                                subConfigItem.set("needPage", genConfig.getQueryWhether().equalsIgnoreCase(GenYesNoEnum.Y.getValue()));
                                subConfigItem.set("needPageType", genConfig.getQueryType());
                                subConfigItem.set("required", genConfig.getWhetherRequired().equalsIgnoreCase(GenYesNoEnum.Y.getValue()));
                                subConfigItem.set("unique", ObjectUtil.isNotEmpty(genConfig.getWhetherUnique()) && genConfig.getWhetherUnique().equalsIgnoreCase(GenYesNoEnum.Y.getValue()));
                                subConfigItem.set("needTableId", false);
                            }
                        }
                        subConfigItem.set("whetherTable", genConfig.getWhetherTable().equalsIgnoreCase(GenYesNoEnum.Y.getValue()));
                        subConfigItem.set("whetherRetract", genConfig.getWhetherRetract().equalsIgnoreCase(GenYesNoEnum.Y.getValue()));
                        subConfigItem.set("whetherAddUpdate", genConfig.getWhetherAddUpdate().equalsIgnoreCase(GenYesNoEnum.Y.getValue()));
                        subConfigItem.set("effectType", genConfig.getEffectType());
                        subConfigItem.set("dictTypeCode", genConfig.getDictTypeCode());
                        subConfigItem.set("fieldJavaType", genConfig.getFieldJavaType());
                        subConfigItem.set("fieldNameCamelCase", StrUtil.toCamelCase(genConfig.getFieldName().toLowerCase()));
                        subConfigItem.set("fieldNameCamelCaseFirstUpper", StrUtil.upperFirst(StrUtil.toCamelCase(genConfig.getFieldName().toLowerCase())));
                        subConfigItem.set("fieldRemark", genConfig.getFieldRemark());
                        subConfigItem.set("needAutoInsert", CREATE_USER_KEY.equalsIgnoreCase(genConfig.getFieldName()) ||
                                CREATE_TIME_KEY.equalsIgnoreCase(genConfig.getFieldName()));
                        subConfigItem.set("needAutoUpdate", UPDATE_USER_KEY.equalsIgnoreCase(genConfig.getFieldName()) ||
                                UPDATE_TIME_KEY.equalsIgnoreCase(genConfig.getFieldName()));
                        subConfigItem.set("needLogicDelete", DELETE_FLAG_KEY.equalsIgnoreCase(genConfig.getFieldName()));
                        subConfigList.add(subConfigItem);
                    });
            bindingJsonObject.set("subConfigList", subConfigList);
            bindingJsonObject.set("subHasSortCodeField", subHasSortCodeField.get());
            // 子表必填字段
            List<JSONObject> subRequiredFieldList = subConfigList.stream().filter(item -> item.getBool("required")).toList();
            bindingJsonObject.set("subRequiredFieldList", subRequiredFieldList);
            bindingJsonObject.set("subRequiredFieldStr", StrUtil.join(StrUtil.COMMA + " ", subRequiredFieldList.stream().map(item ->
                    item.getStr("fieldNameCamelCase")).collect(Collectors.toList())));
            // 子表唯一字段
            List<JSONObject> subUniqueFieldList = subConfigList.stream().filter(item -> item.getBool("unique")).toList();
            bindingJsonObject.set("subUniqueFieldList", subUniqueFieldList);
            bindingJsonObject.set("subUniqueFieldStr", StrUtil.join(StrUtil.COMMA + " ", subUniqueFieldList.stream().map(item ->
                    item.getStr("fieldNameCamelCase")).collect(Collectors.toList())));
        }

        return bindingJsonObject;
    }

    /**
     * 根据生成类型获取后端模板目录
     */
    private String getBackendTemplateDir(String genType) {
        if (GenCategoryEnum.TREE.getValue().equals(genType)) {
            return TEMPLATE_BACKEND_TREE;
        } else if (GenCategoryEnum.LEFT_TREE_TABLE.getValue().equals(genType)) {
            return TEMPLATE_BACKEND_LEFT_TREE_TABLE;
        } else if (GenCategoryEnum.MASTER_DETAIL.getValue().equals(genType)) {
            return TEMPLATE_BACKEND_MASTER_DETAIL;
        }
        return "backend-table";
    }

    /**
     * 根据生成类型获取前端模板目录
     */
    private String getFrontendTemplateDir(String genType) {
        if (GenCategoryEnum.TREE.getValue().equals(genType)) {
            return TEMPLATE_FRONTEND_TREE;
        } else if (GenCategoryEnum.LEFT_TREE_TABLE.getValue().equals(genType)) {
            return TEMPLATE_FRONTEND_LEFT_TREE_TABLE;
        } else if (GenCategoryEnum.MASTER_DETAIL.getValue().equals(genType)) {
            return TEMPLATE_FRONTEND_MASTER_DETAIL;
        }
        return "frontend-table";
    }

    /**
     * 根据生成类型获取前端文件列表
     */
    private List<JSONObject> getFrontendFileList(String genType) {
        if (GenCategoryEnum.MASTER_DETAIL.getValue().equals(genType)) {
            return GEN_FRONT_MASTER_DETAIL_FILE_LIST;
        }
        if (GenCategoryEnum.LEFT_TREE_TABLE.getValue().equals(genType)) {
            return GEN_FRONT_LEFT_TREE_TABLE_FILE_LIST;
        }
        return GEN_FRONT_FILE_LIST;
    }
}
