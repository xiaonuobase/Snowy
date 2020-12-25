package com.cn.xiaonuo.generate.core.tool;

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
        switch(sqlType){
            case "nvarchar":return "String";
            case "char":return "String";
            case "varchar":return "String";
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
            case "float":return "Fload";
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
                System.out.println("-----------------》转化失败：未发现的类型" + sqlType);
                break;
        }
        return sqlType;
    }
}
