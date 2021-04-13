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
package vip.xiaonuo.core.validation.unique;

import cn.hutool.core.util.ObjectUtil;
import vip.xiaonuo.core.context.group.RequestGroupContext;
import vip.xiaonuo.core.context.group.RequestParamIdContext;
import vip.xiaonuo.core.context.system.SystemContextHolder;
import vip.xiaonuo.core.pojo.base.param.BaseParam;
import vip.xiaonuo.core.pojo.base.validate.UniqueValidateParam;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 验证表的的某个字段值是否在是唯一值
 *
 * @author yubaoshan
 * @date 2020/4/14 23:49
 */
public class TableUniqueValueValidator implements ConstraintValidator<TableUniqueValue, String> {

    private String tableName;

    private String columnName;

    private boolean excludeLogicDeleteItems;

    private String logicDeleteFieldName;

    private String logicDeleteValue;

    @Override
    public void initialize(TableUniqueValue constraintAnnotation) {
        this.tableName = constraintAnnotation.tableName();
        this.columnName = constraintAnnotation.columnName();
        this.excludeLogicDeleteItems = constraintAnnotation.excludeLogicDeleteItems();
        this.logicDeleteFieldName = constraintAnnotation.logicDeleteFieldName();
        this.logicDeleteValue = constraintAnnotation.logicDeleteValue();
    }

    @Override
    public boolean isValid(String fieldValue, ConstraintValidatorContext context) {

        if (ObjectUtil.isNull(fieldValue)) {
            return true;
        }

        Class<?> validateGroupClass = RequestGroupContext.get();

        // 如果属于add group，则校验库中所有行
        if (BaseParam.add.class.equals(validateGroupClass)) {
            return SystemContextHolder.me().tableUniValueFlag(createAddParam(fieldValue));
        }

        // 如果属于edit group，校验时需要排除当前修改的这条记录
        if (BaseParam.edit.class.equals(validateGroupClass)) {
            return SystemContextHolder.me().tableUniValueFlag(createEditParam(fieldValue));
        }

        // 默认校验所有的行
        return SystemContextHolder.me().tableUniValueFlag(createAddParam(fieldValue));
    }

    /**
     * 创建校验新增的参数
     *
     * @author xuyuxiang
     * @date 2020/8/17 21:55
     */
    private UniqueValidateParam createAddParam(String fieldValue) {
        return UniqueValidateParam.builder()
                .tableName(tableName)
                .columnName(columnName)
                .value(fieldValue)
                .excludeCurrentRecord(Boolean.FALSE)
                .excludeLogicDeleteItems(excludeLogicDeleteItems)
                .logicDeleteFieldName(logicDeleteFieldName)
                .logicDeleteValue(logicDeleteValue).build();
    }

    /**
     * 创建修改的参数校验
     *
     * @author xuyuxiang
     * @date 2020/8/17 21:56
     */
    private UniqueValidateParam createEditParam(String fieldValue) {
        return UniqueValidateParam.builder()
                .tableName(tableName)
                .columnName(columnName)
                .value(fieldValue)
                .excludeCurrentRecord(Boolean.TRUE)
                .id(RequestParamIdContext.get())
                .excludeLogicDeleteItems(excludeLogicDeleteItems)
                .logicDeleteFieldName(logicDeleteFieldName)
                .logicDeleteValue(logicDeleteValue).build();
    }

}
