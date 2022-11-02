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
package vip.xiaonuo.gen.core.util;

import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import vip.xiaonuo.gen.modular.basic.enums.GenJavaTypeEnum;

/**
 * 数据库类型工具类
 *
 * @author xuyuxiang
 * @date 2022/10/26 16:33
 **/
@Slf4j
public class GenDbTypeUtil {

    /**
     * 根据数据库字段类型获取Java类型
     *
     * @author xuyuxiang
     * @date 2022/10/26 16:34
     **/
    public static String getJavaTypeBySqlType(String dataType) {
        if(ObjectUtil.isEmpty(dataType)) {
            log.info(">>> 字段的数据库类型为空，使用默认值String");
            return GenJavaTypeEnum.String.getValue();
        }
        dataType = dataType.toUpperCase();
        if(dataType.startsWith("INT")) {
            //如果以int开头，则直接返回int，兼容pgsql中int2 int8等
            return GenJavaTypeEnum.Integer.getValue();
        }
        switch(dataType){
            case "NVARCHAR":
            case "NVARCHAR2":
            case "CHAR":
            case "VARCHAR":
            case "ENUM":
            case "SET":
            case "TEXT":
            case "LONGTEXT":
            case "NCHAR":
            case "BLOB":
            case "NCLOB":
            case "IMAGE":
                return GenJavaTypeEnum.String.getValue();
            case "INTEGER":
            case "BIGINT":
            case "NUMBER":
            case "ID":
                return GenJavaTypeEnum.Long.getValue();
            case "TINYINT":
            case "SMALLINT":
            case "MEDIUMINT":
                return GenJavaTypeEnum.Integer.getValue();
            case "BIT":
            case "BOOLEAN":
                return GenJavaTypeEnum.Boolean.getValue();
            case "FLOAT":
                return GenJavaTypeEnum.Float.getValue();
            case "DOUBLE":
            case "MONEY":
            case "SMALLMONEY":
                return GenJavaTypeEnum.Double.getValue();
            case "DECIMAL":
            case "NUMERIC":
            case "REAL":
                return GenJavaTypeEnum.BigDecimal.getValue();
            case "DATE":
            case "DATETIME":
            case "YEAR":
            case "TIME":
            case "TIMESTAMP":
                return GenJavaTypeEnum.Date.getValue();
            default:
                log.info(">>> 字段的数据库类型：{}无法匹配，使用默认值String", dataType);
                return GenJavaTypeEnum.String.getValue();
        }
    }
}
