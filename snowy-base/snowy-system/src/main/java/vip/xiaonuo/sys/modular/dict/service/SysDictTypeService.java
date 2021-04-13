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
package vip.xiaonuo.sys.modular.dict.service;

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.core.pojo.page.PageResult;
import vip.xiaonuo.sys.modular.dict.entity.SysDictType;
import vip.xiaonuo.sys.modular.dict.param.SysDictTypeParam;
import vip.xiaonuo.sys.modular.dict.result.SysDictTreeNode;

import java.util.List;

/**
 * 系统字典类型service接口
 *
 * @author xuyuxiang，xuyuxiang
 * @date 2020/3/13 16:10
 */
public interface SysDictTypeService extends IService<SysDictType> {

    /**
     * 查询系统字典类型
     *
     * @param sysDictTypeParam 查询参数
     * @return 查询分页结果
     * @author xuyuxiang，xuyuxiang
     * @date 2020/3/31 20:34
     */
    PageResult<SysDictType> page(SysDictTypeParam sysDictTypeParam);

    /**
     * 获取字典类型列表
     *
     * @param sysDictTypeParam 查询参数
     * @return 系统字典类型列表
     * @author xuyuxiang，xuyuxiang
     * @date 2020/3/31 21:07
     */
    List<SysDictType> list(SysDictTypeParam sysDictTypeParam);

    /**
     * 系统字典类型下拉
     *
     * @param sysDictTypeParam 下拉参数
     * @return 增强版hashMap，格式：[{"code:":"1", "value":"正常"}]
     * @author xuyuxiang，xuyuxiang
     * @date 2020/3/31 21:23
     */
    List<Dict> dropDown(SysDictTypeParam sysDictTypeParam);

    /**
     * 添加系统字典类型
     *
     * @param sysDictTypeParam 添加参数
     * @author xuyuxiang，xuyuxiang
     * @date 2020/3/31 20:35
     */
    void add(SysDictTypeParam sysDictTypeParam);

    /**
     * 删除系统字典类型
     *
     * @param sysDictTypeParam 删除参数
     * @author xuyuxiang，xuyuxiang
     * @date 2020/3/31 20:35
     */
    void delete(SysDictTypeParam sysDictTypeParam);

    /**
     * 编辑系统字典类型
     *
     * @param sysDictTypeParam 编辑参数
     * @author xuyuxiang，xuyuxiang
     * @date 2020/3/31 20:35
     */
    void edit(SysDictTypeParam sysDictTypeParam);

    /**
     * 查看系统字典类型
     *
     * @param sysDictTypeParam 查看参数
     * @return 系统字典类型
     * @author xuyuxiang，xuyuxiang
     * @date 2020/3/31 20:35
     */
    SysDictType detail(SysDictTypeParam sysDictTypeParam);

    /**
     * 修改状态（字典 0正常 1停用 2删除）
     *
     * @param sysDictTypeParam 修改参数
     * @author yubaoshan
     * @date 2020/4/30 22:30
     */
    void changeStatus(SysDictTypeParam sysDictTypeParam);

    /**
     * 系统字典类型与字典值构造的树
     *
     * @return 树
     * @author xuyuxiang
     * @date 2020/4/30 22:30
     */
    List<SysDictTreeNode> tree();
}
