package com.cn.xiaonuo.generate.core.context;

import com.cn.xiaonuo.generate.core.param.XnCodeGenParam;
import org.apache.velocity.VelocityContext;

/**
 * 设置上下文缓存
 *
 * @author yubaoshan
 * @date 2020年12月17日02:04:56
 */
public class XnVelocityContext {

    /**
     * 创建上下文用到的参数
     *
     * @author yubaoshan
     * @date 2020年12月17日02:04:56
     */
    public VelocityContext createVelContext (XnCodeGenParam xnCodeGenParam) {
        VelocityContext velocityContext = new VelocityContext();
        // 取得类名
        String DomainName = xnCodeGenParam.getClassName();
        String domainName = DomainName.substring(0,1).toLowerCase()+DomainName.substring(1);
        // 类名称
        velocityContext.put("ClassName",DomainName);
        // 类名（首字母小写）
        velocityContext.put("className",domainName);

        // 功能名
        velocityContext.put("functionName",xnCodeGenParam.getFunctionName());

        // 包名称
        velocityContext.put("packageName",xnCodeGenParam.getPackageName());
        // 模块名称
        velocityContext.put("modularName",xnCodeGenParam.getModularNane());
        // 业务名
        velocityContext.put("busName",xnCodeGenParam.getBusName());

        // 作者姓名
        velocityContext.put("authorName", xnCodeGenParam.getAuthorName());
        // 代码生成时间
        velocityContext.put("createDateString", xnCodeGenParam.getCreateTimeString());

        // 数据库表名
        velocityContext.put("tableName", xnCodeGenParam.getTableName());
        // 数据库字段
        velocityContext.put("tableField", xnCodeGenParam.getTableField());

        return velocityContext;
    }
}
