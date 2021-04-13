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
package vip.xiaonuo.generate.core.param;

import lombok.Data;

/**
 * 数据库表字段实体
 *
 * @author yubaoshan
 * @date 2020年12月17日00:08:40
 */
@Data
public class TableField {

    /**
     * 字段名
     */
    public String columnName;

    /**
     * 数据库中类型
     */
    public String dataType;

    /**
     * 字段描述
     */
    public String columnComment;

    /**
     * 主外键（用来做判断的）
     */
    public String columnKey;

    /**
     * 字段名，用来 get set方法使用的
     */
    public String columnKeyName;

    /**
     * Java类型(String,Integer,Date等)
     */
    private String javaType;

    /**
     * 是否是主键
     */
    private Boolean primaryKeyFlag = false;
}
