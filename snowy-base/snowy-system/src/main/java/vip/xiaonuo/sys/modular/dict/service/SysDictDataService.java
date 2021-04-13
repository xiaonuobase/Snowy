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
import vip.xiaonuo.sys.modular.dict.entity.SysDictData;
import vip.xiaonuo.sys.modular.dict.param.SysDictDataParam;

import java.util.List;

/**
 * 系统字典值service接口
 *
 * @author xuyuxiang
 * @date 2020/3/13 16:10
 */
public interface SysDictDataService extends IService<SysDictData> {

    /**
     * 查询系统字典值
     *
     * @param sysDictDataParam 查询参数
     * @return 查询分页结果
     * @author xuyuxiang
     * @date 2020/3/31 20:53
     */
    PageResult<SysDictData> page(SysDictDataParam sysDictDataParam);

    /**
     * 系统字典值列表
     *
     * @param sysDictDataParam 查询参数
     * @return 系统字典值列表
     * @author xuyuxiang
     * @date 2020/3/31 21:07
     */
    List<SysDictData> list(SysDictDataParam sysDictDataParam);

    /**
     * 添加系统字典值
     *
     * @param sysDictDataParam 添加参数
     * @author xuyuxiang
     * @date 2020/3/31 20:53
     */
    void add(SysDictDataParam sysDictDataParam);

    /**
     * 删除系统字典值
     *
     * @param sysDictDataParam 删除参数
     * @author xuyuxiang
     * @date 2020/3/31 20:54
     */
    void delete(SysDictDataParam sysDictDataParam);

    /**
     * 编辑系统字典值
     *
     * @param sysDictDataParam 编辑参数
     * @author xuyuxiang
     * @date 2020/3/31 20:54
     */
    void edit(SysDictDataParam sysDictDataParam);

    /**
     * 查看系统字典值
     *
     * @param sysDictDataParam 查看参数
     * @return 系统字典值
     * @author xuyuxiang
     * @date 2020/3/31 20:54
     */
    SysDictData detail(SysDictDataParam sysDictDataParam);

    /**
     * 根据typeId下拉
     *
     * @param dictTypeId 字典类型id
     * @return 增强版hashMap，格式：[{"code:":"1", "value":"正常"}]
     * @author xuyuxiang yubaoshan
     * @date 2020/3/31 21:27
     */
    List<Dict> getDictDataListByDictTypeId(Long dictTypeId);

    /**
     * 根据typeId删除
     *
     * @param dictTypeId 字典类型id
     * @author xuyuxiang
     * @date 2020/4/1 10:27
     */
    void deleteByTypeId(Long dictTypeId);

    /**
     * 修改状态
     *
     * @param sysDictDataParam 修改参数
     * @author yubaoshan
     * @date 2020/5/1 9:44
     */
    void changeStatus(SysDictDataParam sysDictDataParam);

    /**
     * 根据字典类型获取字典的code值
     *
     * @param dictTypeCodes 字典类型编码集合
     * @return 字典编码值列表
     * @author xuyuxiang
     * @date 2020/8/9 14:18
     */
    List<String> getDictCodesByDictTypeCode(String... dictTypeCodes);
}
