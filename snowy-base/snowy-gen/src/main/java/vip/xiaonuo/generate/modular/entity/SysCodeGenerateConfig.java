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
package vip.xiaonuo.generate.modular.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import vip.xiaonuo.core.pojo.base.entity.BaseEntity;

/**
 * 代码生成详细配置
 *
 * @author yubaoshan
 * @date 2021-02-06 20:19:49
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_code_generate_config")
public class SysCodeGenerateConfig extends BaseEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 代码生成主表ID
     */
    private Long codeGenId;

    /**
     * 数据库字段名
     */
    private String columnName;

    /**
     * java类字段名
     */
    private String javaName;

    /**
     * 字段描述
     */
    private String columnComment;

    /**
     * java类型
     */
    private String javaType;

    /**
     * 作用类型（字典）
     */
    private String effectType;

    /**
     * 字典code
     */
    private String dictTypeCode;

    /**
     * 列表是否缩进（字典）
     */
    private String whetherRetract;

    /**
     * 是否必填（字典）
     */
    private String whetherRequired;

    /**
     * 是否是查询条件
     */
    private String queryWhether;

    /**
     * 查询方式
     */
    private String queryType;

    /**
     * 列表显示
     */
    private String whetherTable;

    /**
     * 增改
     */
    private String whetherAddUpdate;

    /**
     * 主外键
     */
    public String columnKey;

    /**
     * 首字母大写名称（用于代码生成get set方法）
     */
    public String columnKeyName;

    /**
     * 数据库中类型（物理类型）
     */
    public String dataType;

    /**
     * 是否是通用字段
     */
    public String whetherCommon;

}
