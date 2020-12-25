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
package com.cn.xiaonuo.generate.modular.mapper;

import com.cn.xiaonuo.generate.modular.entity.CodeGenerate;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cn.xiaonuo.generate.modular.result.InforMationColumnsResult;
import com.cn.xiaonuo.generate.modular.result.InformationResult;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 代码生成基础配置
 *
 * @author yubaoshan
 * @date 2020年12月16日21:07:28
 */
public interface CodeGenerateMapper extends BaseMapper<CodeGenerate> {

    /**
     * 查询指定库中所有表
     *
     * @author yubaoshan
     * @date 2020年12月17日20:06:05
     */
    List<InformationResult> selectInformationTable(@Param("dbName") String dbName);

    /**
     * 查询指定表中所有字段
     *
     * @author yubaoshan
     * @date 2020年12月17日20:06:05
     */
    List<InforMationColumnsResult> selectInformationColumns(@Param("dbName") String dbName, @Param("tableName") String tableName);
}
