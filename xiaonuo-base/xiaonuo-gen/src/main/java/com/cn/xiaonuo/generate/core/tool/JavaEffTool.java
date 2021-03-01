package com.cn.xiaonuo.generate.core.tool;

/**
 * java与effect工具类
 *
 * @author yubaoshan
 * @date 2021-2-8 02:30
 */
public class JavaEffTool {

    /**
     * java转显示类型
     *
     * @author yubaoshan
     * @date 2021-2-8 02:30
     */
    public static String javaToEff (String javaType) {
        if( javaType == null || javaType.trim().length() == 0 ) return javaType;
        switch(javaType){
            case "String":return "input";
            case "Integer":return "input";
            case "Long":return "input";
            case "Date":return "datepicker";
            default:
                System.out.println(">>> 转化失败：未发现的类型" + javaType);
                break;
        }
        return javaType;
    }
}
