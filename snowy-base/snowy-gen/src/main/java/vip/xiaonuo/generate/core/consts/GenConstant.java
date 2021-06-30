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
package vip.xiaonuo.generate.core.consts;


import java.io.File;

/**
 * 代码生成配置
 *
 * @author yubaoshan
 * @date 2020-12-19 02:30:56
 */
public class GenConstant {

    /**
     * 路径分离（不通的机器，取不同的路径）
     */
    public static String FILE_SEP = File.separator;

    /**
     * 存放vm模板位置
     */
    public static String templatePath = "template" + FILE_SEP;

    /**
     * 主键标识
     */
    public static String DB_TABLE_COM_KRY = "PRI";

    /**
     * 模块名（一般为modular，无特殊要求一般不改）
     */
    public static String MODULAR_NAME = "modular";

    /**
     * 本项目生成时是否覆盖
     */
    public static final boolean FLAG = false;

    /**
     * 大模块名称（生成到代码中哪个模块下）
     */
    public static String BASE_MODULAR_NAME = "snowy-main";

    /**
     * java文件夹
     */
    public static String BASE_JAVA_PATH = FILE_SEP + "src" + FILE_SEP + "main" + FILE_SEP + "java" + FILE_SEP;

    /**
     * vue文件夹
     */
    public static String BASE_VUE_PATH = FILE_SEP + "_web" + FILE_SEP + "src" + FILE_SEP;

    /**
     * sql文件夹
     */
    public static String BASE_SQL_PATH = FILE_SEP + "_sql" + FILE_SEP;

    /**
     * 代码生成路径
     */
    public static String controllerPath;
    public static String entityPath;
    public static String enumsPath;
    public static String mapperPath;
    public static String mappingPath;
    public static String paramPath;
    public static String servicePath;
    public static String serviceImplPath;
    public static String manageJsPath;
    public static String vueIndexPath;
    public static String vueAddFromPath;
    public static String vueEditFromPath;
    public static String mysqlSqlPath;
    public static String oracleSqlPath;

    /**
     * 各个代码存放路径文件夹
     */
    public static String[] xnCodeGenFilePath (String busName, String packageName) {
        String packageNameString = packageName.replace(".",FILE_SEP) + FILE_SEP;
        controllerPath = BASE_JAVA_PATH + packageNameString + MODULAR_NAME + FILE_SEP + busName + FILE_SEP + "controller" + FILE_SEP;
        entityPath = BASE_JAVA_PATH + packageNameString + MODULAR_NAME + FILE_SEP + busName + FILE_SEP + "entity" + FILE_SEP;
        enumsPath = BASE_JAVA_PATH+ packageNameString  + MODULAR_NAME + FILE_SEP + busName + FILE_SEP + "enums" + FILE_SEP;
        mapperPath = BASE_JAVA_PATH + packageNameString  + MODULAR_NAME + FILE_SEP + busName + FILE_SEP + "mapper" + FILE_SEP;
        mappingPath = mapperPath + FILE_SEP + "mapping" + FILE_SEP;
        paramPath = BASE_JAVA_PATH+ FILE_SEP + packageNameString  + MODULAR_NAME + FILE_SEP + busName + FILE_SEP + "param" + FILE_SEP;
        servicePath = BASE_JAVA_PATH+ FILE_SEP + packageNameString  + MODULAR_NAME + FILE_SEP + busName + FILE_SEP + "service" + FILE_SEP;
        serviceImplPath = servicePath + FILE_SEP + "impl" + FILE_SEP;
        manageJsPath = BASE_VUE_PATH + FILE_SEP + "api" + FILE_SEP + MODULAR_NAME + FILE_SEP + "main" + FILE_SEP + busName + FILE_SEP;
        vueIndexPath = BASE_VUE_PATH + FILE_SEP + "views" + FILE_SEP + "main" + FILE_SEP + busName + FILE_SEP;
        vueAddFromPath = BASE_VUE_PATH + FILE_SEP + "views" + FILE_SEP + "main" + FILE_SEP + busName + FILE_SEP;
        vueEditFromPath = BASE_VUE_PATH + FILE_SEP + "views" + FILE_SEP + "main" + FILE_SEP + busName + FILE_SEP;
        mysqlSqlPath = BASE_SQL_PATH;
        oracleSqlPath = BASE_SQL_PATH;
        return new String[] {
                controllerPath, entityPath, enumsPath, mapperPath, mappingPath, paramPath, servicePath, serviceImplPath, manageJsPath, vueIndexPath, vueAddFromPath, vueEditFromPath, mysqlSqlPath, oracleSqlPath
        };
    }

    /**
     * 模板文件
     */
    public static String[] xnCodeGenTempFile = {
            "Controller.java.vm",
            "entity.java.vm",
            "ExceptionEnum.java.vm",
            "Mapper.java.vm",
            "Mapper.xml.vm",
            "Param.java.vm",
            "Service.java.vm",
            "ServiceImpl.java.vm",
            "Manage.js.vm",
            "index.vue.vm",
            "addForm.vue.vm",
            "editForm.vue.vm",
            "XnMysql.sql.vm",
            "XnOracle.sql.vm",
    };

    /**
     * 本地项目根目录
     */
    public static String getLocalPath () {
        return System.getProperty("user.dir") + FILE_SEP + BASE_MODULAR_NAME + FILE_SEP;
    }

    /**
     * vue前端
     */
    public static String getLocalFrontPath () {
        return System.getProperty("user.dir") + FILE_SEP ;
    }
}
