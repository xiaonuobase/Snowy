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
package com.cn.xiaonuo.generate.modular.param;

import com.cn.xiaonuo.core.pojo.base.param.BaseParam;
import com.cn.xiaonuo.core.validation.flag.FlagValue;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 代码生成参数类
 *
 * @author yubaoshan
 * @date 2020年12月16日20:41:21
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CodeGenerateParam extends BaseParam {

    /**
     * 主键
     */
    @NotNull(message = "id不能为空，请检查id参数", groups = {edit.class, delete.class, detail.class})
    private Long id;

    /**
     * 作者姓名
     */
    @NotBlank(message = "作者姓名不能为空，请检查authorName参数", groups = {BaseParam.add.class, edit.class})
    private String authorName;

    /**
     * 类名
     */
    @NotBlank(message = "类名不能为空，请检查className参数", groups = {BaseParam.add.class, edit.class})
    private String className;

    /**
     * 是否移除表前缀
     */
    @NotBlank(message = "是否移除表前缀不能为空，请检查tablePrefix参数", groups = {BaseParam.add.class, edit.class})
    @FlagValue(message = "是否移除表前缀格式错误，正确格式应该Y或者N，请检查tablePrefix参数", groups = {add.class, edit.class})
    private String tablePrefix;

    /**
     * 生成方式
     */
    @NotBlank(message = "生成方式不能为空，请检查generateType参数", groups = {BaseParam.add.class, edit.class})
    private String generateType;

    /**
     * 数据库表名
     */
    @NotBlank(message = "数据库表名不能为空，请检查tableName参数", groups = {BaseParam.add.class, edit.class})
    private String tableName;

    /**
     * 代码包名
     */
    private String packageName;

    /**
     * 业务名（业务代码包名称）
     */
    @NotBlank(message = "业务名不能为空，请检查busName参数", groups = {BaseParam.add.class, edit.class})
    private String busName;

    /**
     * 功能名（数据库表名称）
     */
    @NotBlank(message = "功能名不能为空，请检查tableComment参数", groups = {BaseParam.add.class, edit.class})
    private String tableComment;

}
