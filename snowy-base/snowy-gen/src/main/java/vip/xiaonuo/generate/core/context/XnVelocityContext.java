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
package vip.xiaonuo.generate.core.context;

import org.apache.velocity.VelocityContext;
import vip.xiaonuo.core.enums.YesOrNotEnum;
import vip.xiaonuo.generate.core.param.XnCodeGenParam;
import vip.xiaonuo.generate.modular.entity.SysCodeGenerateConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        velocityContext.put("tableField", xnCodeGenParam.getConfigList());

        // 前端查询所有
        List<SysCodeGenerateConfig> codeGenerateConfigList = new ArrayList<>();
        xnCodeGenParam.getConfigList().forEach(item -> {
            if (item.getQueryWhether().equals(YesOrNotEnum.Y.getCode())) {
                codeGenerateConfigList.add(item);
            }
        });
        velocityContext.put("queryWhetherList", codeGenerateConfigList);

        velocityContext.put("appCode", xnCodeGenParam.getAppCode());

        velocityContext.put("menuPids", xnCodeGenParam.getMenuPids() + "[" + xnCodeGenParam.getMenuPid() + "],");

        // sql中id的创建
        List<Long> idList = new ArrayList<>();
        for (int a = 0; a <= 7; a++) {
            idList.add(Math.abs(UUID.randomUUID().getLeastSignificantBits()));
        }
        velocityContext.put("sqlMenuId", idList);

        return velocityContext;
    }
}
