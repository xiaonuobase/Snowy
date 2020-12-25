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
package com.cn.xiaonuo.generate.modular.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cn.xiaonuo.core.pojo.page.PageResult;
import com.cn.xiaonuo.generate.modular.entity.CodeGenerate;
import com.cn.xiaonuo.generate.modular.param.CodeGenerateParam;
import com.cn.xiaonuo.generate.modular.result.InformationResult;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 代码生成基础配置service接口
 *
 * @author yubaoshan
 * @date 2020年12月16日21:03:15
 */
public interface CodeGenerateService extends IService<CodeGenerate> {

    /**
     * 查询代码生成基础配置
     *
     * @author yubaoshan
     * @date 2020年12月16日21:03:15
     */
    PageResult<CodeGenerate> page(CodeGenerateParam codeGenerateParam);

    /**
     * 添加查询代码生成基础配置
     *
     * @author yubaoshan
     * @date 2020年12月16日21:03:15
     */
    void add(CodeGenerateParam codeGenerateParam);

    /**
     * 删除查询代码生成基础配置
     *
     * @author yubaoshan
     * @date 2020年12月16日21:03:15
     */
    void delete(List<CodeGenerateParam> codeGenerateParamList);

    /**
     * 编辑查询代码生成基础配置
     *
     * @author yubaoshan
     * @date 2020年12月16日21:03:15
     */
    void edit(CodeGenerateParam codeGenerateParam);

    /**
     * 查看查询代码生成基础配置
     *
     * @author yubaoshan
     * @date 2020年12月16日21:03:15
     */
    CodeGenerate detail(CodeGenerateParam codeGenerateParam);

    /**
     * 查询当前数据库用户下的所有表
     *
     * @author yubaoshan
     * @date 2020年12月16日21:03:15
     */
    List<InformationResult> InformationTableList ();

    /**
     * 本地生成代码
     *
     * @author yubaoshan
     * @date 2020年12月16日21:03:15
     */
    void runLocal(CodeGenerateParam codeGenerateParam);

    /**
     * 下载zip方式
     *
     * @author yubaoshan
     * @date 2020年12月16日21:03:15
     */
    void runDown(CodeGenerateParam codeGenerateParam, HttpServletResponse response);
}
