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
package vip.xiaonuo.generate.core.tool;

/**
 * java与sql工具类
 *
 * @author yubaoshan
 * @date 2020-12-17 23:42
 */
public class JavaSqlTool {

    /**
     * 数据类型转化JAVA
     *
     * @author yubaoshan
     * @date 2020-12-17 23:42
     */
    public static String sqlToJava (String sqlType) {
        if( sqlType == null || sqlType.trim().length() == 0 ) return sqlType;
        sqlType = sqlType.toLowerCase();
        if(sqlType.startsWith("int")) {
            //如果以int开头，则直接返回int，兼容pgsql中int2 int8等
            return "Integer";
        }
        switch(sqlType){
            case "nvarchar":return "String";
            case "nvarchar2":return "String";
            case "char":return "String";
            case "varchar":return "String";
            case "enum":return "String";
            case "set":return "String";
            case "text":return "String";
            case "nchar":return "String";
            case "blob":return "byte[]";
            case "integer":return "Long";
            case "int":return "Integer";
            case "tinyint":return "Integer";
            case "smallint":return "Integer";
            case "mediumint":return "Integer";
            case "bit":return "Boolean";
            case "bigint":return "Long";
            case "number":return "Long";
            case "float":return "Float";
            case "double":return "Double";
            case "decimal":return "BigDecimal";
            case "boolean":return "Boolean";
            case "id":return "Long";
            case "date":return "Date";
            case "datetime":return "Date";
            case "year":return "Date";
            case "time":return "Time";
            case "timestamp":return "Timestamp";
            case "numeric":return "BigDecimal";
            case "real":return "BigDecimal";
            case "money":return "Double";
            case "smallmoney":return "Double";
            case "image":return "byte[]";
            default:
                System.out.println(">>> 转化失败：未发现的类型" + sqlType);
                break;
        }
        return sqlType;
    }
}
